package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.repository.PdvVendaCabecalhoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reprocessa periodicamente vendas cuja emissão fiscal ficou
 * {@code STATUS_FISCAL = 'P'} (pendente) — ver {@link EmissaoFiscalService}.
 *
 * <p>Cenário típico: ACBr Monitor ou SEFAZ ficaram fora do ar no momento do
 * fechamento da venda. A venda já foi concluída (cliente já saiu com o
 * produto); esse job tenta emitir o documento fiscal de novo em background,
 * sem intervenção do operador.
 *
 * <p>Roda enquanto o processo do PDV estiver de pé (é um app desktop
 * JavaFX+Spring Boot num único processo — não há servidor separado). Se o
 * PDV for fechado com cupons ainda pendentes, o reprocessamento retoma
 * automaticamente na próxima vez que o aplicativo for aberto.
 */
@Service
public class ReprocessamentoFiscalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReprocessamentoFiscalService.class);
    private static final String STATUS_FISCAL_PENDENTE = "P";

    private final PdvVendaCabecalhoRepository cabecalhoRepository;
    private final EmissaoFiscalService emissaoFiscalService;

    public ReprocessamentoFiscalService(PdvVendaCabecalhoRepository cabecalhoRepository,
                                        EmissaoFiscalService emissaoFiscalService) {
        this.cabecalhoRepository = cabecalhoRepository;
        this.emissaoFiscalService = emissaoFiscalService;
    }

    /**
     * Intervalo configurável via {@code cactus.pdv.fiscal.reprocessamento.intervalo-ms}
     * (padrão 5 minutos). Conta a partir do FIM da execução anterior — não
     * empilha execuções se o reprocessamento demorar mais que o intervalo.
     */
    @Scheduled(fixedDelayString = "${cactus.pdv.fiscal.reprocessamento.intervalo-ms:300000}")
    public void reprocessarPendentes() {
        List<PdvVendaCabecalho> pendentes = cabecalhoRepository.findByStatusFiscal(STATUS_FISCAL_PENDENTE);
        if (pendentes.isEmpty()) {
            return;
        }

        LOGGER.info("Reprocessamento fiscal: {} cupom(ns) pendente(s) encontrados.", pendentes.size());
        for (PdvVendaCabecalho cupom : pendentes) {
            reprocessarUm(cupom.getId());
        }
    }

    /**
     * Reprocessa um único cupom em transação própria — isola falhas entre
     * cupons (um erro num não deve impedir a tentativa nos demais).
     */
    @Transactional
    public void reprocessarUm(Long idCupom) {
        try {
            PdvVendaCabecalho cupom = cabecalhoRepository.findById(idCupom).orElse(null);
            if (cupom == null) return;

            PdvCaixa caixa = cupom.getPdvMovimento() != null ? cupom.getPdvMovimento().getCaixa() : null;
            if (caixa == null) {
                LOGGER.warn("Cupom #{} pendente sem movimento/caixa associado — não é possível reprocessar.", idCupom);
                return;
            }

            emissaoFiscalService.emitir(caixa, cupom);
        } catch (Exception ex) {
            LOGGER.error("Falha ao reprocessar emissão fiscal do cupom #{}", idCupom, ex);
        }
    }
}
