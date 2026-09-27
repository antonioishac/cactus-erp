package br.com.cactusdigital.erp.crm.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/** {@code leadId} é opcional — uma oportunidade pode nascer direto de um {@code clienteId} já existente, sem passar por Lead. */
public record OportunidadeRequest(
        Long leadId,
        Long clienteId,
        Long vendedorId,
        @NotBlank @Size(max = 120) String nome,
        BigDecimal valorEstimado,
        @DecimalMin("0") @DecimalMax("100") BigDecimal probabilidadePercentual,
        LocalDate dataPrevisaoFechamento,
        @NotNull Long estagioFunilId,
        @Size(max = 500) String observacao) {
}
