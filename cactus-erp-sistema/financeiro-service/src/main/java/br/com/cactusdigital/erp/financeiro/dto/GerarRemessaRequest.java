package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record GerarRemessaRequest(
        @NotNull Long contaBancariaId,
        @NotNull Long empresaId,
        @NotEmpty @Valid List<ItemRemessaRequest> itens) {
}
