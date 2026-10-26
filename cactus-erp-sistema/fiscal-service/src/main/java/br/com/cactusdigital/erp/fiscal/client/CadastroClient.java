package br.com.cactusdigital.erp.fiscal.client;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.fiscal.client.dto.AliquotaIbsCbsDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.ClassificacaoIbsCbsDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.ClienteFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaConfiguracaoFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.NumeroFiscalPuladoClientRequest;
import br.com.cactusdigital.erp.fiscal.client.dto.ProdutoFiscalDTO;
import br.com.cactusdigital.erp.fiscal.exception.FiscalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Lê Cliente/Produto/Empresa/Transportadora no {@code cadastro-base-service} pra compor a NF-e. */
@Service
@RequiredArgsConstructor
public class CadastroClient {

    private final RestClient cadastroBaseRestClient;

    public ClienteFiscalDTO buscarCliente(Long clienteId) {
        return get("/api/v1/clientes/{id}", clienteId, ClienteFiscalDTO.class, "cliente");
    }

    public ClienteFiscalDTO buscarTransportadora(Long transportadoraId) {
        return get("/api/v1/transportadoras/{id}", transportadoraId, ClienteFiscalDTO.class, "transportadora");
    }

    public ProdutoFiscalDTO buscarProduto(Long produtoId) {
        return get("/api/v1/produtos/{id}", produtoId, ProdutoFiscalDTO.class, "produto");
    }

    public ClassificacaoIbsCbsDTO buscarClassificacaoIbsCbs(String codigo) {
        try {
            return cadastroBaseRestClient.get()
                    .uri("/api/v1/tributacao/ibs-cbs/classificacoes/{codigo}", codigo)
                    .retrieve()
                    .body(ClassificacaoIbsCbsDTO.class);
        } catch (RestClientException ex) {
            throw new BusinessException(FiscalErrorCode.FALHA_INTEGRACAO_CADASTRO,
                    "Falha ao consultar a classificação IBS/CBS " + codigo + " no cadastro-base-service: " + ex.getMessage());
        }
    }

    /** Alíquotas de IBS/CBS do ano de {@code data}. Ano sem alíquota em lei: o cadastro responde 422 e a mensagem sobe. */
    public AliquotaIbsCbsDTO buscarAliquotasIbsCbs(java.time.LocalDate data) {
        try {
            return cadastroBaseRestClient.get()
                    .uri(uri -> uri.path("/api/v1/tributacao/ibs-cbs/aliquotas").queryParam("data", data).build())
                    .retrieve()
                    .body(AliquotaIbsCbsDTO.class);
        } catch (RestClientException ex) {
            throw new BusinessException(FiscalErrorCode.FALHA_INTEGRACAO_CADASTRO,
                    "Falha ao consultar as alíquotas de IBS/CBS de " + data + " no cadastro-base-service: " + ex.getMessage());
        }
    }

    public EmpresaFiscalDTO buscarEmpresa(Long empresaId) {
        return get("/api/v1/empresas/{id}", empresaId, EmpresaFiscalDTO.class, "empresa");
    }

    public EmpresaConfiguracaoFiscalDTO buscarConfiguracaoFiscal(Long empresaId) {
        return get("/api/v1/empresas/{id}/configuracao-fiscal", empresaId, EmpresaConfiguracaoFiscalDTO.class, "configuração fiscal da empresa");
    }

    /** Reserva (e incrementa) o próximo número de NF-e da série configurada — ver {@code EmpresaConfiguracaoFiscalService#reservarProximoNumeroNfe} no cadastro-base-service. */
    public EmpresaConfiguracaoFiscalDTO reservarProximoNumeroNfe(Long empresaId) {
        try {
            return cadastroBaseRestClient.post()
                    .uri("/api/v1/empresas/{id}/configuracao-fiscal/reservar-numero-nfe", empresaId)
                    .retrieve()
                    .body(EmpresaConfiguracaoFiscalDTO.class);
        } catch (RestClientException ex) {
            throw new BusinessException(FiscalErrorCode.FALHA_INTEGRACAO_CADASTRO,
                    "Falha ao reservar número de NF-e da empresa #" + empresaId + " no cadastro-base-service: " + ex.getMessage());
        }
    }

    /** Reserva (e incrementa) o próximo número de CT-e da série configurada — mesmo padrão de {@link #reservarProximoNumeroNfe} (Fase 11). */
    public EmpresaConfiguracaoFiscalDTO reservarProximoNumeroCte(Long empresaId) {
        try {
            return cadastroBaseRestClient.post()
                    .uri("/api/v1/empresas/{id}/configuracao-fiscal/reservar-numero-cte", empresaId)
                    .retrieve()
                    .body(EmpresaConfiguracaoFiscalDTO.class);
        } catch (RestClientException ex) {
            throw new BusinessException(FiscalErrorCode.FALHA_INTEGRACAO_CADASTRO,
                    "Falha ao reservar número de CT-e da empresa #" + empresaId + " no cadastro-base-service: " + ex.getMessage());
        }
    }

    /** Reserva (e incrementa) o próximo número de NFC-e da série configurada — mesmo padrão de {@link #reservarProximoNumeroNfe} (Fase 13). */
    public EmpresaConfiguracaoFiscalDTO reservarProximoNumeroNfce(Long empresaId) {
        try {
            return cadastroBaseRestClient.post()
                    .uri("/api/v1/empresas/{id}/configuracao-fiscal/reservar-numero-nfce", empresaId)
                    .retrieve()
                    .body(EmpresaConfiguracaoFiscalDTO.class);
        } catch (RestClientException ex) {
            throw new BusinessException(FiscalErrorCode.FALHA_INTEGRACAO_CADASTRO,
                    "Falha ao reservar número de NFC-e da empresa #" + empresaId + " no cadastro-base-service: " + ex.getMessage());
        }
    }

    /** Reserva (e incrementa) o próximo número de MDF-e da série configurada — mesmo padrão de {@link #reservarProximoNumeroNfe} (Fase 16). */
    public EmpresaConfiguracaoFiscalDTO reservarProximoNumeroMdfe(Long empresaId) {
        try {
            return cadastroBaseRestClient.post()
                    .uri("/api/v1/empresas/{id}/configuracao-fiscal/reservar-numero-mdfe", empresaId)
                    .retrieve()
                    .body(EmpresaConfiguracaoFiscalDTO.class);
        } catch (RestClientException ex) {
            throw new BusinessException(FiscalErrorCode.FALHA_INTEGRACAO_CADASTRO,
                    "Falha ao reservar número de MDF-e da empresa #" + empresaId + " no cadastro-base-service: " + ex.getMessage());
        }
    }

    /**
     * Compensação de {@link #reservarProximoNumeroNfe} via saga (ver
     * {@code common.saga.SagaOrchestrator} em {@code NotaFiscalEletronicaService#gerarDeVenda})
     * — NÃO devolve/decrementa o contador (risco de colisão com reserva concorrente), só
     * registra o número como pulado no {@code cadastro-base-service}
     * ({@code NumeroFiscalPulado}), tornando o gap consultável em vez de só existir como
     * log perdido. Best-effort: se esta própria chamada falhar, o {@link SagaOrchestrator}
     * já registra isso como {@code FALHOU_AO_COMPENSAR} na auditoria local.
     */
    public void registrarNumeroNfePulado(Long empresaId, String serie, Long numero, String motivo) {
        try {
            cadastroBaseRestClient.post()
                    .uri("/api/v1/empresas/{id}/configuracao-fiscal/numero-fiscal-pulado", empresaId)
                    .body(new NumeroFiscalPuladoClientRequest("NFE", serie, numero, motivo))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw new BusinessException(FiscalErrorCode.FALHA_INTEGRACAO_CADASTRO,
                    "Falha ao registrar número de NF-e pulado da empresa #" + empresaId + " no cadastro-base-service: " + ex.getMessage());
        }
    }

    private <T> T get(String uri, Long id, Class<T> tipo, String descricao) {
        try {
            return cadastroBaseRestClient.get()
                    .uri(uri, id)
                    .retrieve()
                    .body(tipo);
        } catch (RestClientException ex) {
            throw new BusinessException(FiscalErrorCode.FALHA_INTEGRACAO_CADASTRO,
                    "Falha ao consultar " + descricao + " #" + id + " no cadastro-base-service: " + ex.getMessage());
        }
    }
}
