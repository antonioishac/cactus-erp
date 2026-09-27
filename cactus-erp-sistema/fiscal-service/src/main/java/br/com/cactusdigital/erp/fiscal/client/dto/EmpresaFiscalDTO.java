package br.com.cactusdigital.erp.fiscal.client.dto;

/** Espelha o subconjunto do {@code EmpresaResponse} (cadastro-base-service) necessário pro Emitente da NF-e. */
public record EmpresaFiscalDTO(
        Long id,
        String razaoSocial,
        String nomeFantasia,
        String cnpj,
        String inscricaoEstadual,
        String crt,
        String telefone,
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
