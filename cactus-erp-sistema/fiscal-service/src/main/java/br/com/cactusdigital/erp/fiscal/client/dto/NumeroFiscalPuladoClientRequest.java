package br.com.cactusdigital.erp.fiscal.client.dto;

/** Espelha {@code cadastro-base-service.NumeroFiscalPuladoRequest} — cópia deliberada, não dependência Java (limite de serviço). {@code tipoDocumento} só vale {@code "NFE"} nesta fatia. */
public record NumeroFiscalPuladoClientRequest(String tipoDocumento, String serie, Long numero, String motivo) {
}
