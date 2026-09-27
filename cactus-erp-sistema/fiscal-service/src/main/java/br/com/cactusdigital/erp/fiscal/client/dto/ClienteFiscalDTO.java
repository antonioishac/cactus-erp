package br.com.cactusdigital.erp.fiscal.client.dto;

import java.util.List;

/**
 * Espelha o subconjunto do {@code ClienteResponse}/{@code FornecedorResponse}/{@code
 * TransportadoraResponse} (cadastro-base-service) necessário pro Destinatário/
 * Transportadora da NF-e/CT-e. {@code rntrc} só existe de fato em
 * {@code TransportadoraResponse} — fica {@code null} quando o DTO é populado a partir de
 * {@code ClienteResponse}/{@code FornecedorResponse} (Jackson ignora o campo ausente,
 * {@code FAIL_ON_UNKNOWN_PROPERTIES=false}), sem problema porque só é lido no contexto de
 * transportadora.
 */
public record ClienteFiscalDTO(
        Long id,
        String nome,
        String tipoPessoa,
        String documento,
        String inscricaoEstadual,
        String email,
        List<EnderecoClienteDTO> enderecos,
        String rntrc) {
}
