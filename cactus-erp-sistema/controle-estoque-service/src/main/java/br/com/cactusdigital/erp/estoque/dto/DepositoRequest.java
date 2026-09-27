package br.com.cactusdigital.erp.estoque.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import br.com.cactusdigital.erp.estoque.entity.TipoDeposito;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DepositoRequest(
        @NotNull Long empresaId,
        @NotBlank @Size(max = 20) String codigo,
        @NotBlank @Size(max = 100) String nome,
        @NotNull TipoDeposito tipo,
        @NotNull Situacao situacao) {
}
