package br.com.cactusdigital.erp.fiscal.client;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.fiscal.client.dto.OsAberturaClienteDTO;
import br.com.cactusdigital.erp.fiscal.exception.FiscalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Lê a OsAbertura no {@code os-service} pra compor a NFS-e (Fase 9) — único destino de leitura pra esse serviço. */
@Service
@RequiredArgsConstructor
public class OsClient {

    private final RestClient osRestClient;

    public OsAberturaClienteDTO buscarOsAbertura(Long osAberturaId) {
        try {
            return osRestClient.get()
                    .uri("/api/v1/os-aberturas/{id}", osAberturaId)
                    .retrieve()
                    .body(OsAberturaClienteDTO.class);
        } catch (RestClientException ex) {
            throw new BusinessException(FiscalErrorCode.FALHA_INTEGRACAO_OS,
                    "Falha ao consultar a OS #" + osAberturaId + " no os-service: " + ex.getMessage());
        }
    }
}
