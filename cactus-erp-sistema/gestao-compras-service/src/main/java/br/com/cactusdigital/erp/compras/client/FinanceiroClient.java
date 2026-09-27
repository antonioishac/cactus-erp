package br.com.cactusdigital.erp.compras.client;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.compras.exception.ComprasErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Chama {@code POST /api/v1/contas-pagar/gerar-de-compra} no {@code financeiro-service}
 * quando um {@code PedidoCompra} vira {@code RECEBIDO_TOTAL} — fecha o gap registrado
 * desde a Fase 7/financeiro-service ("{@code ContaPagar} só {@code MANUAL}"). Idempotente
 * do lado do {@code financeiro-service} (rejeita 409 se o pedido já gerou contas) — aqui
 * só repassa a chamada, não trata o 409 como sucesso silencioso.
 * <p>
 * <b>Compensação real via saga (ver {@code RecebimentoCompraService#registrar})</b> — ver
 * Javadoc de {@link EstoqueClient} e de {@code vendas-service.FinanceiroClient} (mesmo
 * espírito exato do lado de venda): {@link #estornarContasDeCompra} existe por
 * simetria/futuro-proofing — é o ÚLTIMO passo da saga hoje, então nada vem depois pra
 * falhar e acionar a compensação dele nesta pilot.
 */
@Service
@RequiredArgsConstructor
public class FinanceiroClient {

    private final RestClient financeiroRestClient;

    public void gerarContasPagar(GerarContasPagarClientRequest request) {
        try {
            financeiroRestClient.post()
                    .uri("/api/v1/contas-pagar/gerar-de-compra")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw new BusinessException(ComprasErrorCode.FALHA_INTEGRACAO_FINANCEIRO,
                    "Falha ao gerar contas a pagar no financeiro-service: " + ex.getMessage());
        }
    }

    /** Compensação de {@link #gerarContasPagar} — cancela todas as {@code ContaPagar} geradas pra este pedido de compra (best-effort, chamado só pela saga quando um passo POSTERIOR falhar depois deste ter tido sucesso). */
    public void estornarContasDeCompra(Long pedidoCompraId) {
        try {
            financeiroRestClient.post()
                    .uri("/api/v1/contas-pagar/de-compra/{pedidoCompraId}/estornar", pedidoCompraId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw new BusinessException(ComprasErrorCode.FALHA_INTEGRACAO_FINANCEIRO,
                    "Falha ao estornar contas a pagar (compensação) no financeiro-service: " + ex.getMessage());
        }
    }
}
