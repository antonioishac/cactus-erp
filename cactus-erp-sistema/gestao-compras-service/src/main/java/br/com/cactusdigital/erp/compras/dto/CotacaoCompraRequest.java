package br.com.cactusdigital.erp.compras.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CotacaoCompraRequest(
        @NotNull Long requisicaoId,
        LocalDate dataCotacao,
        @Size(max = 100) String descricao) {
}
