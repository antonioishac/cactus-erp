package br.com.cactusdigital.erp.fiscal.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.fiscal.client.CadastroClient;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaConfiguracaoFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.dto.InutilizacaoNumeracaoNfeResponse;
import br.com.cactusdigital.erp.fiscal.dto.InutilizarNumeracaoNfeRequest;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.InutilizacaoNumeracaoNfe;
import br.com.cactusdigital.erp.fiscal.entity.SituacaoInutilizacao;
import br.com.cactusdigital.erp.fiscal.exception.FiscalErrorCode;
import br.com.cactusdigital.erp.fiscal.mapper.FiscalMapper;
import br.com.cactusdigital.erp.fiscal.repository.InutilizacaoNumeracaoNfeRepository;
import br.com.cactusdigital.erp.fiscal.repository.NotaFiscalEletronicaRepository;
import br.com.cactusdigital.erp.fiscal.sefaz.EventosNfe;
import br.com.cactusdigital.erp.fiscal.sefaz.FalhaComunicacaoSefazException;
import br.com.cactusdigital.erp.fiscal.sefaz.RetornoEventoSefaz;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Inutilização de faixas de numeração de NF-e junto à SEFAZ — a regularização legal de número reservado que nunca virou
 * documento. Recusa localmente, antes de qualquer rede, o que a SEFAZ rejeitaria ou o que inutilizaria um número ainda
 * útil:
 * <ul>
 *   <li>faixa invertida ou série fora de 0-999;</li>
 *   <li>número que ainda não foi reservado ({@code >= proximoNumeroNfe} da série configurada): quando o contador
 *       chegasse nele, a NF-e seria rejeitada por número inutilizado;</li>
 *   <li>número que já pertence a uma NF-e do ERP (em qualquer situação — mesmo RASCUNHO/REJEITADA ainda podem ser
 *       emitidas);</li>
 *   <li>sobreposição com faixa já homologada.</li>
 * </ul>
 * Homologada, os números pulados pendentes da faixa são regularizados no cadastro-base-service (best-effort: a
 * inutilização já vale na SEFAZ e não pode ser desfeita, então uma falha ali só é logada).
 * <p>
 * <b>UFs que dispensam o pedido</b> ({@link #UFS_SEM_INUTILIZACAO}, Ajuste SINIEF 40/2026 a partir de
 * {@link #INICIO_DISPENSA}): nada é enviado à SEFAZ — o pedido nem existe lá. O salto fica registrado como
 * {@code DISPENSADA}, com as mesmas validações locais (que continuam protegendo a numeração), e os números pulados são
 * regularizados com a referência ao ajuste.
 */
@Service
@RequiredArgsConstructor
public class InutilizacaoNumeracaoNfeService {

    private static final Logger log = LoggerFactory.getLogger(InutilizacaoNumeracaoNfeService.class);
    private static final ZoneId FUSO_BRASILIA = ZoneId.of("America/Sao_Paulo");
    /** Ajuste SINIEF 40/2026 (NF-e): o pedido de inutilização não se aplica a estas UFs. */
    static final Set<String> UFS_SEM_INUTILIZACAO = Set.of("AC", "BA", "SP");
    static final LocalDate INICIO_DISPENSA = LocalDate.of(2026, 11, 1);
    static final String REFERENCIA_DISPENSA = "AJ.SINIEF 40/2026";

    private final InutilizacaoNumeracaoNfeRepository repository;
    private final NotaFiscalEletronicaRepository notaRepository;
    private final CadastroClient cadastroClient;
    private final EventosNfe eventosNfe;
    private final FiscalMapper mapper;

    @Transactional(readOnly = true)
    public Page<InutilizacaoNumeracaoNfeResponse> listar(Long empresaId, Pageable pageable) {
        return repository.findByEmpresaId(empresaId, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public InutilizacaoNumeracaoNfeResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public String buscarXml(Long id) {
        InutilizacaoNumeracaoNfe inutilizacao = buscarEntidade(id);
        if (inutilizacao.getXmlRetorno() == null) {
            throw new BusinessException(FiscalErrorCode.XML_NAO_DISPONIVEL, "Pedido de inutilização #" + id + " sem retorno da SEFAZ");
        }
        return inutilizacao.getXmlRetorno();
    }

    @Transactional
    public InutilizacaoNumeracaoNfeResponse inutilizar(InutilizarNumeracaoNfeRequest request) {
        Long empresaId = request.empresaId();
        if (request.numeroFinal() < request.numeroInicial()) {
            throw new BusinessException(FiscalErrorCode.INUTILIZACAO_FAIXA_INVALIDA, "O número final é menor que o inicial");
        }
        EmpresaConfiguracaoFiscalDTO configuracao = cadastroClient.buscarConfiguracaoFiscal(empresaId);
        String serie = request.serie() != null && !request.serie().isBlank() ? request.serie().trim() : configuracao.serieNfe();
        if (serie == null) {
            throw new BusinessException(FiscalErrorCode.EMPRESA_SEM_CONFIGURACAO_FISCAL, "Informe a série: a empresa não tem série de NF-e configurada");
        }
        if (!serie.matches("\\d{1,3}")) {
            throw new BusinessException(FiscalErrorCode.INUTILIZACAO_FAIXA_INVALIDA, "Série deve ser numérica de 0 a 999");
        }
        if (serie.equals(configuracao.serieNfe()) && configuracao.proximoNumeroNfe() != null
                && request.numeroFinal() >= configuracao.proximoNumeroNfe()) {
            throw new BusinessException(FiscalErrorCode.INUTILIZACAO_FAIXA_INVALIDA,
                    "Só número já reservado pode ser inutilizado: o próximo número da série " + serie + " é "
                            + configuracao.proximoNumeroNfe() + " (inutilizar à frente faria a NF-e ser rejeitada quando o contador chegasse nele)");
        }
        if (notaRepository.existsByEmpresaIdAndSerieAndNumeroBetween(empresaId, serie, request.numeroInicial(), request.numeroFinal())) {
            throw new ConflictException(FiscalErrorCode.INUTILIZACAO_FAIXA_COM_NOTA);
        }
        if (repository.existeHomologadaSobreposta(empresaId, serie, request.numeroInicial(), request.numeroFinal())) {
            throw new ConflictException(FiscalErrorCode.INUTILIZACAO_FAIXA_JA_INUTILIZADA);
        }

        EmpresaFiscalDTO emitente = cadastroClient.buscarEmpresa(empresaId);
        AmbienteFiscal ambiente = "PRODUCAO".equalsIgnoreCase(configuracao.ambienteFiscal()) ? AmbienteFiscal.PRODUCAO : AmbienteFiscal.HOMOLOGACAO;
        short ano = (short) (Year.now(FUSO_BRASILIA).getValue() % 100);

        if (dispensaInutilizacao(emitente.uf(), LocalDate.now(FUSO_BRASILIA))) {
            InutilizacaoNumeracaoNfe dispensada = repository.save(InutilizacaoNumeracaoNfe.builder()
                    .empresaId(empresaId).serie(serie).numeroInicial(request.numeroInicial()).numeroFinal(request.numeroFinal())
                    .ano(ano).ambiente(ambiente).justificativa(request.justificativa())
                    .situacao(SituacaoInutilizacao.DISPENSADA)
                    .protocolo(REFERENCIA_DISPENSA)
                    .mensagemSefaz("Pedido de inutilização dispensado em " + emitente.uf()
                            + " (Ajuste SINIEF 40/2026): o salto de numeração só é registrado, a emissão segue do número seguinte")
                    .build());
            regularizarNumerosPulados(dispensada);
            return mapper.toResponse(dispensada);
        }

        RetornoEventoSefaz retorno;
        try {
            retorno = eventosNfe.inutilizar(ambiente, emitente, configuracao, serie, request.numeroInicial(), request.numeroFinal(),
                    ano, request.justificativa());
        } catch (FalhaComunicacaoSefazException ex) {
            throw new BusinessException(FiscalErrorCode.FALHA_COMUNICACAO_SEFAZ, ex.getMessage());
        }

        InutilizacaoNumeracaoNfe inutilizacao = repository.save(InutilizacaoNumeracaoNfe.builder()
                .empresaId(empresaId)
                .serie(serie)
                .numeroInicial(request.numeroInicial())
                .numeroFinal(request.numeroFinal())
                .ano(ano)
                .ambiente(ambiente)
                .justificativa(request.justificativa())
                .situacao(retorno.homologado() ? SituacaoInutilizacao.HOMOLOGADA : SituacaoInutilizacao.REJEITADA)
                .protocolo(retorno.protocolo())
                .codigoStatusSefaz(retorno.codigoStatus())
                .mensagemSefaz(truncar(retorno.codigoStatus() + " - " + retorno.motivo()))
                .dataHomologacao(retorno.homologado() ? retorno.dataRegistro() : null)
                .xmlRetorno(retorno.xml())
                .build());

        if (retorno.homologado()) {
            regularizarNumerosPulados(inutilizacao);
        }
        return mapper.toResponse(inutilizacao);
    }

    private void regularizarNumerosPulados(InutilizacaoNumeracaoNfe inutilizacao) {
        try {
            cadastroClient.regularizarNumerosNfePulados(inutilizacao.getEmpresaId(), inutilizacao.getSerie(),
                    inutilizacao.getNumeroInicial(), inutilizacao.getNumeroFinal(), inutilizacao.getProtocolo());
        } catch (BusinessException ex) {
            log.warn("Inutilização #{} homologada (protocolo {}), mas os números pulados não foram marcados no cadastro-base-service: {}",
                    inutilizacao.getId(), inutilizacao.getProtocolo(), ex.getMessage());
        }
    }

    /** Pedido de inutilização não existe mais nestas UFs a partir de 01/11/2026 — package-private para teste. */
    static boolean dispensaInutilizacao(String uf, LocalDate hoje) {
        return uf != null && UFS_SEM_INUTILIZACAO.contains(uf.toUpperCase()) && !hoje.isBefore(INICIO_DISPENSA);
    }

    private InutilizacaoNumeracaoNfe buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Inutilização de numeração", id));
    }

    private static String truncar(String texto) {
        return texto.length() <= 500 ? texto : texto.substring(0, 500);
    }
}
