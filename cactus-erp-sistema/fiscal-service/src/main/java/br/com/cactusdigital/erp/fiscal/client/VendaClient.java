package br.com.cactusdigital.erp.fiscal.client;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.fiscal.client.dto.VendaClienteDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.VendaFreteClienteDTO;
import br.com.cactusdigital.erp.fiscal.exception.FiscalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Lê a Venda (e seu frete, pra Fase 11/CT-e) no {@code vendas-service} — única chamada de leitura cross-serviço pra esse destino. */
@Service
@RequiredArgsConstructor
public class VendaClient {

    private final RestClient vendasRestClient;

    public VendaClienteDTO buscarVenda(Long vendaId) {
        try {
            return vendasRestClient.get()
                    .uri("/api/v1/vendas/{id}", vendaId)
                    .retrieve()
                    .body(VendaClienteDTO.class);
        } catch (RestClientException ex) {
            throw new BusinessException(FiscalErrorCode.FALHA_INTEGRACAO_VENDAS,
                    "Falha ao consultar a venda #" + vendaId + " no vendas-service: " + ex.getMessage());
        }
    }

    /** {@code null} quando a venda não tem frete definido (404) — distinto de falha de comunicação de verdade. */
    public VendaFreteClienteDTO buscarFrete(Long vendaId) {
        try {
            return vendasRestClient.get()
                    .uri("/api/v1/vendas/{id}/frete", vendaId)
                    .retrieve()
                    .body(VendaFreteClienteDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            return null;
        } catch (RestClientException ex) {
            throw new BusinessException(FiscalErrorCode.FALHA_INTEGRACAO_VENDAS,
                    "Falha ao consultar o frete da venda #" + vendaId + " no vendas-service: " + ex.getMessage());
        }
    }
}
