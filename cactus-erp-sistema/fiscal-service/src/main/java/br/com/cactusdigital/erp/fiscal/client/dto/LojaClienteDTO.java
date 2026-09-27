package br.com.cactusdigital.erp.fiscal.client.dto;

/** Espelha o subconjunto do {@code LojaResponse} (loja-virtual-service) necessário pra resolver a Empresa emitente da NFC-e. */
public record LojaClienteDTO(
        Long id,
        String nome,
        Long empresaId,
        String subdominio) {
}
