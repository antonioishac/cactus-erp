package br.com.cactusdigital.erp.cadastro.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * {@code entrega}/{@code cobranca}/{@code correspondencia}: um endereço pode servir mais
 * de um papel ao mesmo tempo — validado contra {@code fenix.sql.PESSOA_ENDERECO}, que
 * tem os 4 flags (o {@code principal} sozinho, que era tudo que eu tinha antes, não bastava).
 */
public record EnderecoDTO(
        @Size(max = 150) String logradouro,
        @Size(max = 10) String numero,
        @Size(max = 100) String complemento,
        @Size(max = 100) String bairro,
        @Pattern(regexp = "\\d{8}", message = "CEP deve ter 8 dígitos") String cep,
        @Size(max = 100) String municipio,
        @Size(min = 2, max = 2) String uf,
        Integer codigoIbgeMunicipio,
        Integer codigoIbgeUf,
        boolean principal,
        boolean entrega,
        boolean cobranca,
        boolean correspondencia) {
}
