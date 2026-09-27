package br.com.cactusdigital.erp.fiscal.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.fiscal.AcbrMonitorClient;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.fiscal.client.CadastroClient;
import br.com.cactusdigital.erp.fiscal.client.VendaClient;
import br.com.cactusdigital.erp.fiscal.client.dto.ClienteFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaConfiguracaoFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.VendaClienteDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.VendaFreteClienteDTO;
import br.com.cactusdigital.erp.fiscal.dto.CteCabecalhoResponse;
import br.com.cactusdigital.erp.fiscal.dto.GerarCteDeVendaRequest;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.CteCabecalho;
import br.com.cactusdigital.erp.fiscal.entity.CteComponente;
import br.com.cactusdigital.erp.fiscal.entity.SituacaoNotaFiscal;
import br.com.cactusdigital.erp.fiscal.entity.TipoTomadorCte;
import br.com.cactusdigital.erp.fiscal.exception.FiscalErrorCode;
import br.com.cactusdigital.erp.fiscal.mapper.FiscalMapper;
import br.com.cactusdigital.erp.fiscal.repository.CteCabecalhoRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Compõe e emite um CT-e (modelo 57, modal Rodoviário) a partir do frete de uma
 * {@code VendaCabecalho} (vendas-service, Fase 7) já {@code CONFIRMADA} e com
 * {@code VendaFrete} definido (transportadora obrigatória). Mesma máquina de estados de
 * {@code NotaFiscalEletronicaService}: {@code RASCUNHO} → (emitir) → {@code ENVIADA} →
 * {@code AUTORIZADA}/{@code REJEITADA} ou {@code ERRO}.
 * <p>
 * <b>Cancelamento fica fora de escopo</b>, mesmo critério de NF-e/NFS-e. Ver Javadoc de
 * {@link br.com.cactusdigital.erp.fiscal.entity.CteCabecalho} pro detalhamento completo
 * do que está fora de escopo nesta fase (modais não-rodoviários, expedidor/recebedor,
 * cobrança/duplicata, carga perigosa, vínculo com NF-e transportada).
 */
@Service
@RequiredArgsConstructor
public class CteCabecalhoService {

    private final CteCabecalhoRepository repository;
    private final VendaClient vendaClient;
    private final CadastroClient cadastroClient;
    private final AcbrMonitorClient acbrMonitorClient;
    private final CteIniBuilder iniBuilder;
    private final FiscalMapper mapper;

    @Value("${cactus.acbr.host}")
    private String acbrHost;

    @Value("${cactus.acbr.porta}")
    private int acbrPorta;

    @Transactional(readOnly = true)
    public Page<CteCabecalhoResponse> listar(Pageable pageable) {
        Page<CteCabecalho> pagina = repository.findAll(pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, CteCabecalho::getId, repository::buscarComComponentes, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CteCabecalhoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    /**
     * Compõe o CT-e (RASCUNHO) a partir da Venda + seu Frete — não chama o ACBr Monitor
     * ainda (isso só acontece em {@link #emitir}).
     */
    @Transactional
    public CteCabecalhoResponse gerarDeVenda(Long vendaId, GerarCteDeVendaRequest request) {
        if (repository.existsByVendaId(vendaId)) {
            throw new ConflictException(FiscalErrorCode.VENDA_JA_TEM_CTE);
        }
        if (request.tomador() != TipoTomadorCte.REMETENTE && request.tomador() != TipoTomadorCte.DESTINATARIO) {
            throw new BusinessException(FiscalErrorCode.TOMADOR_NAO_SUPORTADO);
        }

        VendaClienteDTO venda = vendaClient.buscarVenda(vendaId);
        if (!"CONFIRMADA".equals(venda.situacao())) {
            throw new BusinessException(FiscalErrorCode.VENDA_SITUACAO_INVALIDA_PARA_CTE);
        }

        VendaFreteClienteDTO frete = vendaClient.buscarFrete(vendaId);
        if (frete == null) {
            throw new BusinessException(FiscalErrorCode.VENDA_SEM_FRETE);
        }
        if (frete.transportadoraId() == null) {
            throw new BusinessException(FiscalErrorCode.FRETE_SEM_TRANSPORTADORA);
        }

        // Validação de existência por efeito colateral (mesmo padrão de NotaFiscalEletronicaService).
        cadastroClient.buscarCliente(venda.clienteId());
        cadastroClient.buscarTransportadora(frete.transportadoraId());
        EmpresaConfiguracaoFiscalDTO configFiscal = cadastroClient.reservarProximoNumeroCte(venda.empresaId());

        BigDecimal aliquotaIcms = request.aliquotaIcms();
        BigDecimal baseCalculoIcms = aliquotaIcms != null ? request.valorTotalServico() : null;
        BigDecimal valorIcms = aliquotaIcms != null
                ? request.valorTotalServico().multiply(aliquotaIcms).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                : null;

        CteCabecalho cte = CteCabecalho.builder()
                .vendaId(venda.id())
                .empresaId(venda.empresaId())
                .clienteId(venda.clienteId())
                .transportadoraId(frete.transportadoraId())
                .serie(configFiscal.serieCte())
                .numero(configFiscal.proximoNumeroCte())
                .ambiente("PRODUCAO".equalsIgnoreCase(configFiscal.ambienteFiscal()) ? AmbienteFiscal.PRODUCAO : AmbienteFiscal.HOMOLOGACAO)
                .tomador(request.tomador())
                .produtoPredominante(request.produtoPredominante())
                .observacoesGerais(request.observacoesGerais())
                .indicadorLotacao(request.indicadorLotacao() != null ? request.indicadorLotacao() : "0")
                .dataPrevistaEntrega(request.dataPrevistaEntrega())
                .ciot(request.ciot())
                .placa(frete.placa())
                .ufPlaca(frete.ufPlaca())
                .pesoBruto(frete.pesoBruto())
                .quantidadeVolume(frete.quantidadeVolume())
                .valorTotalServico(request.valorTotalServico())
                .valorReceber(request.valorTotalServico())
                .valorTotalCarga(venda.valorTotal())
                .baseCalculoIcms(baseCalculoIcms)
                .aliquotaIcms(aliquotaIcms)
                .valorIcms(valorIcms)
                .situacao(SituacaoNotaFiscal.RASCUNHO)
                .build();

        if (request.componentes() != null) {
            request.componentes().forEach(c -> cte.getComponentes().add(
                    CteComponente.builder().cteCabecalho(cte).nome(c.nome()).valor(c.valor()).build()));
        }

        return mapper.toResponse(repository.save(cte));
    }

    /**
     * Monta o comando {@code CTE.CriarEnviarCTe} (via {@link CteIniBuilder}) e chama o
     * ACBr Monitor. Nunca invocado ao vivo nesta sessão — ver Javadoc de
     * {@link CteIniBuilder}.
     */
    @Transactional
    public CteCabecalhoResponse emitir(Long id) {
        CteCabecalho cte = buscarEntidade(id);
        if (cte.getSituacao() != SituacaoNotaFiscal.RASCUNHO && cte.getSituacao() != SituacaoNotaFiscal.ERRO) {
            throw new ConflictException(FiscalErrorCode.NOTA_FISCAL_SITUACAO_INVALIDA);
        }

        EmpresaFiscalDTO emitente = cadastroClient.buscarEmpresa(cte.getEmpresaId());
        ClienteFiscalDTO destinatario = cadastroClient.buscarCliente(cte.getClienteId());
        ClienteFiscalDTO transportadora = cadastroClient.buscarTransportadora(cte.getTransportadoraId());

        cte.setDataEmissao(Instant.now());
        String comando = iniBuilder.construirComando(cte, emitente, destinatario, transportadora, 1);

        cte.setSituacao(SituacaoNotaFiscal.ENVIADA);
        try {
            String resposta = acbrMonitorClient.enviarComando(acbrHost, acbrPorta, comando);
            if (acbrMonitorClient.respostaComErro(resposta)) {
                cte.setSituacao(SituacaoNotaFiscal.REJEITADA);
                cte.setMensagemSefaz(truncar(resposta, 500));
                return mapper.toResponse(cte);
            }
            cte.setSituacao(SituacaoNotaFiscal.AUTORIZADA);
            cte.setChaveAcesso(extrair(resposta, "ChCTe", 44));
            cte.setProtocoloAutorizacao(extrair(resposta, "NProt", 30));
            cte.setMensagemSefaz(truncar("Autorizado.", 500));
            return mapper.toResponse(cte);
        } catch (IOException ex) {
            cte.setSituacao(SituacaoNotaFiscal.ERRO);
            cte.setMensagemSefaz(truncar("Falha de comunicação com o ACBr Monitor: " + ex.getMessage(), 500));
            return mapper.toResponse(cte);
        }
    }

    private String truncar(String texto, int max) {
        if (texto == null) {
            return null;
        }
        return texto.length() <= max ? texto : texto.substring(0, max);
    }

    private String extrair(String texto, String chave, int maxLength) {
        if (texto == null) {
            return null;
        }
        var matcher = java.util.regex.Pattern.compile("(?im)^\\s*" + chave + "\\s*=\\s*(.+?)\\s*$").matcher(texto);
        return matcher.find() ? truncar(matcher.group(1), maxLength) : null;
    }

    CteCabecalho buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("CT-e", id));
    }
}
