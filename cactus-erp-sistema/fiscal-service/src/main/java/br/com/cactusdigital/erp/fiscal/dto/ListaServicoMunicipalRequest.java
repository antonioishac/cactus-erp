package br.com.cactusdigital.erp.fiscal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ListaServicoMunicipalRequest(
        @NotBlank @Size(max = 20) String codigo,
        @NotBlank @Size(max = 250) String descricao,
        @Size(max = 20) String codigoTributacaoMunicipio,
        BigDecimal aliquotaIssPadrao) {
}
