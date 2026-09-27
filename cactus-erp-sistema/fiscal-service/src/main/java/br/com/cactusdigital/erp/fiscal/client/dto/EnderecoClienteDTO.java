package br.com.cactusdigital.erp.fiscal.client.dto;

/** Espelha {@code cadastro-base-service.EnderecoDTO} — só os campos usados pela NF-e. */
public record EnderecoClienteDTO(
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cep,
        String municipio,
        String uf,
        Integer codigoIbgeMunicipio,
        Integer codigoIbgeUf) {
}
