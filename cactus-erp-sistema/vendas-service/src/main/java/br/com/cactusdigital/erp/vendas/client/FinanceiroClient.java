package br.com.cactusdigital.erp.vendas.client;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.vendas.exception.VendasErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Chama {@code POST /contas-receber/gerar-de-venda} no {@code financeiro-service} ao
 * confirmar uma venda — fecha o gap registrado desde a Fase 7 (condição de
 * pagamento/parcelas nunca geravam lançamento financeiro de verdade). Idempotente do
 * lado do {@code financeiro-service} (rejeita 409 se a venda já gerou contas) — aqui só
 * repassa a chamada, não trata o 409 como sucesso silencioso (se a confirmação da venda
 * for re-executada por engano, o erro deve aparecer, não ser escondido).
 * <p>
 * <b>Compensação real via saga (ver {@code VendaService#confirmar})</b>: se este passo
 * falhar, a saga orquestrada compensa os passos ANTERIORES (débito de estoque por item) —
 * este passo em si não precisa de compensação própria porque, se
 * {@link #gerarContasReceber} lançou exceção, nenhuma {@code ContaReceber} foi criada do
 * lado do {@code financeiro-service} (a chamada falhou antes de confirmar sucesso, e o
 * `gerarDeVenda` de lá é atômico numa transação própria — tudo ou nada). Se o passo
 * SEGUINTE de uma saga futura precisar desfazer este aqui depois de bem-sucedido,
 * {@link #estornarContasDeVenda} está pronto pra isso.
 */
@Service
@RequiredArgsConstructor
public class FinanceiroClient {

    private final RestClient financeiroRestClient;

    /**
     * Lança o dinheiro líquido de uma {@code VendaPdv} no caixa do terminal ({@code POST /pdv/vendas/dinheiro},
     * idempotente do lado do financeiro). Chamado de DENTRO do consumidor da fila, por isso a falha de
     * comunicação vira {@link IllegalStateException} e não {@link BusinessException}: o retry da fila só
     * não retenta erro de negócio, e uma queda momentânea do financeiro precisa ser retentada, não ir direto
     * pra DLQ. Um 4xx de volta (requisição rejeitada) é determinístico e vira {@link BusinessException}.
     */
    public void registrarDinheiroVendaPdv(VendaPdvDinheiroClientRequest request) {
        try {
            financeiroRestClient.post()
                    .uri("/api/v1/pdv/vendas/dinheiro")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (org.springframework.web.client.HttpClientErrorException ex) {
            throw new BusinessException(VendasErrorCode.FALHA_INTEGRACAO_FINANCEIRO,
                    "Financeiro rejeitou o dinheiro da venda do PDV (registro #" + request.registroId() + "): " + ex.getMessage());
        } catch (RestClientException ex) {
            throw new IllegalStateException("Falha de comunicação ao lançar o dinheiro da venda do PDV no financeiro-service: " + ex.getMessage(), ex);
        }
    }

    /** Desfaz {@link #registrarDinheiroVendaPdv} quando a venda é cancelada depois de fechada. Mesma política de erro. */
    public void estornarDinheiroVendaPdv(VendaPdvDinheiroClientRequest request) {
        try {
            financeiroRestClient.post()
                    .uri("/api/v1/pdv/vendas/dinheiro/estorno")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (org.springframework.web.client.HttpClientErrorException ex) {
            throw new BusinessException(VendasErrorCode.FALHA_INTEGRACAO_FINANCEIRO,
                    "Financeiro rejeitou o estorno do dinheiro da venda do PDV (registro #" + request.registroId() + "): " + ex.getMessage());
        } catch (RestClientException ex) {
            throw new IllegalStateException("Falha de comunicação ao estornar o dinheiro da venda do PDV no financeiro-service: " + ex.getMessage(), ex);
        }
    }

    public void gerarContasReceber(GerarContasReceberClientRequest request) {
        try {
            financeiroRestClient.post()
                    .uri("/api/v1/contas-receber/gerar-de-venda")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw new BusinessException(VendasErrorCode.FALHA_INTEGRACAO_FINANCEIRO,
                    "Falha ao gerar contas a receber no financeiro-service: " + ex.getMessage());
        }
    }

    /** Compensação de {@link #gerarContasReceber} — cancela todas as {@code ContaReceber} geradas pra esta venda (best-effort, chamado só pela saga quando um passo POSTERIOR falhar depois deste ter tido sucesso). */
    public void estornarContasDeVenda(Long vendaId) {
        try {
            financeiroRestClient.post()
                    .uri("/api/v1/contas-receber/de-venda/{vendaId}/estornar", vendaId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw new BusinessException(VendasErrorCode.FALHA_INTEGRACAO_FINANCEIRO,
                    "Falha ao estornar contas a receber (compensação) no financeiro-service: " + ex.getMessage());
        }
    }
}
