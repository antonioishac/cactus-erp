package br.com.cactusdigital.erp.fiscal.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.fiscal.AcbrMonitorClient;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.fiscal.client.CadastroClient;
import br.com.cactusdigital.erp.fiscal.client.OsClient;
import br.com.cactusdigital.erp.fiscal.client.dto.ClienteFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaConfiguracaoFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.OsAberturaClienteDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.OsItemClienteDTO;
import br.com.cactusdigital.erp.fiscal.dto.GerarNotaServicoRequest;
import br.com.cactusdigital.erp.fiscal.dto.NotaFiscalServicoResponse;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.ListaServicoMunicipal;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalServico;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalServicoItem;
import br.com.cactusdigital.erp.fiscal.entity.SituacaoNotaFiscal;
import br.com.cactusdigital.erp.fiscal.exception.FiscalErrorCode;
import br.com.cactusdigital.erp.fiscal.mapper.FiscalMapper;
import br.com.cactusdigital.erp.fiscal.repository.NotaFiscalServicoRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Compõe e emite uma NFS-e a partir de uma {@code OsAbertura} (os-service, Fase 10) com
 * {@code orcamentoAprovado=true}. Só os itens {@code tipo=SERVICO} da OS entram na nota
 * — ver Javadoc de {@link br.com.cactusdigital.erp.fiscal.entity.NotaFiscalServico}.
 * <p>
 * <b>Emissão real (ver {@link #emitir}) tem confiança bem menor que
 * {@code NotaFiscalEletronicaService}</b> — ver Javadoc de {@link NfseIniBuilder} pro
 * porquê (a sequência de 2 comandos ACBr agora está confirmada por documentação oficial;
 * o que resta incerto é só o detalhamento de chaves do INI). Nunca chamado ao vivo nesta
 * sessão, mesma decisão de segurança da Fase 8 (ambiente com certificado de produção
 * configurado no ACBr Monitor local).
 */
@Service
@RequiredArgsConstructor
public class NotaFiscalServicoService {

    private final NotaFiscalServicoRepository repository;
    private final OsClient osClient;
    private final CadastroClient cadastroClient;
    private final ListaServicoMunicipalService listaServicoMunicipalService;
    private final AcbrMonitorClient acbrMonitorClient;
    private final NfseIniBuilder iniBuilder;
    private final FiscalMapper mapper;

    @Value("${cactus.acbr.host}")
    private String acbrHost;

    @Value("${cactus.acbr.porta}")
    private int acbrPorta;

    @Transactional(readOnly = true)
    public Page<NotaFiscalServicoResponse> listar(Pageable pageable) {
        Page<NotaFiscalServico> pagina = repository.findAll(pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, NotaFiscalServico::getId, repository::buscarComItens, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public NotaFiscalServicoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public NotaFiscalServicoResponse gerarDeOs(Long osAberturaId, GerarNotaServicoRequest request) {
        if (repository.existsByOsAberturaId(osAberturaId)) {
            throw new ConflictException(FiscalErrorCode.OS_JA_TEM_NOTA_SERVICO);
        }

        OsAberturaClienteDTO os = osClient.buscarOsAbertura(osAberturaId);
        if (!os.orcamentoAprovado()) {
            throw new BusinessException(FiscalErrorCode.OS_ORCAMENTO_NAO_APROVADO);
        }

        List<OsItemClienteDTO> itensServico = os.itens().stream()
                .filter(item -> "SERVICO".equals(item.tipo()))
                .toList();
        if (itensServico.isEmpty()) {
            throw new BusinessException(FiscalErrorCode.OS_SEM_ITEM_SERVICO);
        }

        // Validação de existência do cliente por efeito colateral (mesmo padrão de
        // NotaFiscalEletronicaService#gerarDeVenda).
        cadastroClient.buscarCliente(os.clienteId());
        EmpresaConfiguracaoFiscalDTO configFiscal = cadastroClient.buscarConfiguracaoFiscal(os.empresaId());
        ListaServicoMunicipal listaServico = listaServicoMunicipalService.buscarEntidade(request.listaServicoId());

        NotaFiscalServico nota = NotaFiscalServico.builder()
                .osAberturaId(osAberturaId)
                .empresaId(os.empresaId())
                .clienteId(os.clienteId())
                .numeroRps(request.numeroRps())
                .serieRps(request.serieRps())
                .competencia(request.competencia())
                .ambiente("PRODUCAO".equalsIgnoreCase(configFiscal.ambienteFiscal()) ? AmbienteFiscal.PRODUCAO : AmbienteFiscal.HOMOLOGACAO)
                .situacao(SituacaoNotaFiscal.RASCUNHO)
                .build();

        BigDecimal totalServicos = BigDecimal.ZERO;
        BigDecimal totalIss = BigDecimal.ZERO;
        for (OsItemClienteDTO itemOs : itensServico) {
            BigDecimal aliquota = listaServico.getAliquotaIssPadrao();
            BigDecimal valorIss = aliquota != null
                    ? itemOs.valorTotal().multiply(aliquota).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                    : null;
            nota.getItens().add(NotaFiscalServicoItem.builder()
                    .notaFiscalServico(nota)
                    .listaServico(listaServico)
                    .discriminacao(itemOs.complemento())
                    .codigoTributacaoMunicipio(listaServico.getCodigoTributacaoMunicipio())
                    .valorServicos(itemOs.valorTotal())
                    .aliquotaIss(aliquota)
                    .valorIss(valorIss)
                    .build());
            totalServicos = totalServicos.add(itemOs.valorTotal());
            totalIss = totalIss.add(valorIss != null ? valorIss : BigDecimal.ZERO);
        }
        nota.setValorServicos(totalServicos);
        nota.setValorIss(totalIss);
        nota.setValorLiquido(totalServicos.subtract(totalIss));

        return mapper.toResponse(repository.save(nota));
    }

    /** Número de lote fixo — este serviço só forma lotes de 1 RPS por vez, nunca agrupa múltiplas notas. */
    private static final String NUMERO_LOTE = "1";

    /**
     * Monta e envia os 2 comandos ACBr do fluxo de NFS-e (ver Javadoc da classe/de
     * {@link NfseIniBuilder}): {@code NFSe.AdicionarRPS} adiciona o RPS ao lote,
     * {@code NFSe.EnviarLoteRPS} transmite o lote (modo síncrono — devolve o resultado da
     * autorização na mesma chamada, sem precisar de consulta de lote à parte). Se o
     * primeiro comando já falhar, o segundo nem é tentado (não faz sentido enviar um
     * lote que não recebeu RPS nenhum).
     */
    @Transactional
    public NotaFiscalServicoResponse emitir(Long id) {
        NotaFiscalServico nota = buscarEntidade(id);
        if (nota.getSituacao() != SituacaoNotaFiscal.RASCUNHO && nota.getSituacao() != SituacaoNotaFiscal.ERRO) {
            throw new ConflictException(FiscalErrorCode.NOTA_FISCAL_SITUACAO_INVALIDA);
        }

        EmpresaFiscalDTO prestador = cadastroClient.buscarEmpresa(nota.getEmpresaId());
        ClienteFiscalDTO tomador = cadastroClient.buscarCliente(nota.getClienteId());

        nota.setDataEmissao(Instant.now());
        String comandoAdicionar = iniBuilder.construirComandoAdicionarRps(nota, prestador, tomador, NUMERO_LOTE);

        try {
            String respostaAdicionar = acbrMonitorClient.enviarComando(acbrHost, acbrPorta, comandoAdicionar);
            if (acbrMonitorClient.respostaComErro(respostaAdicionar)) {
                nota.setSituacao(SituacaoNotaFiscal.REJEITADA);
                nota.setMensagemSefaz(truncar(respostaAdicionar, 500));
                return mapper.toResponse(nota);
            }

            String comandoEnviar = iniBuilder.construirComandoEnviarLote(NUMERO_LOTE);
            String respostaEnviar = acbrMonitorClient.enviarComando(acbrHost, acbrPorta, comandoEnviar);
            if (acbrMonitorClient.respostaComErro(respostaEnviar)) {
                nota.setSituacao(SituacaoNotaFiscal.REJEITADA);
                nota.setMensagemSefaz(truncar(respostaEnviar, 500));
                return mapper.toResponse(nota);
            }

            nota.setSituacao(SituacaoNotaFiscal.AUTORIZADA);
            nota.setMensagemSefaz(truncar(respostaEnviar, 500));
            return mapper.toResponse(nota);
        } catch (IOException ex) {
            nota.setSituacao(SituacaoNotaFiscal.ERRO);
            nota.setMensagemSefaz(truncar("Falha de comunicação com o ACBr Monitor: " + ex.getMessage(), 500));
            return mapper.toResponse(nota);
        }
    }

    private String truncar(String texto, int max) {
        if (texto == null) {
            return null;
        }
        return texto.length() <= max ? texto : texto.substring(0, max);
    }

    NotaFiscalServico buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Nota fiscal de serviço", id));
    }
}
