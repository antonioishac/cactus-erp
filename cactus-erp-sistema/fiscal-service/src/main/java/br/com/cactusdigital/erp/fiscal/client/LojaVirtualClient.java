package br.com.cactusdigital.erp.fiscal.client;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.fiscal.client.dto.LojaClienteDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.PedidoLojaClienteDTO;
import br.com.cactusdigital.erp.fiscal.exception.FiscalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Lê Pedido/Loja no {@code loja-virtual-service} pra compor a NFC-e (Fase 13, sub-fase 3). */
@Service
@RequiredArgsConstructor
public class LojaVirtualClient {

    private final RestClient lojaVirtualRestClient;

    public PedidoLojaClienteDTO buscarPedido(Long pedidoId) {
        try {
            return lojaVirtualRestClient.get()
                    .uri("/api/v1/pedidos/{id}", pedidoId)
                    .retrieve()
                    .body(PedidoLojaClienteDTO.class);
        } catch (RestClientException ex) {
            throw new BusinessException(FiscalErrorCode.FALHA_INTEGRACAO_LOJA_VIRTUAL,
                    "Falha ao consultar o pedido #" + pedidoId + " no loja-virtual-service: " + ex.getMessage());
        }
    }

    public LojaClienteDTO buscarLoja(Long lojaId) {
        try {
            return lojaVirtualRestClient.get()
                    .uri("/api/v1/lojas/{id}", lojaId)
                    .retrieve()
                    .body(LojaClienteDTO.class);
        } catch (RestClientException ex) {
            throw new BusinessException(FiscalErrorCode.FALHA_INTEGRACAO_LOJA_VIRTUAL,
                    "Falha ao consultar a loja #" + lojaId + " no loja-virtual-service: " + ex.getMessage());
        }
    }
}
