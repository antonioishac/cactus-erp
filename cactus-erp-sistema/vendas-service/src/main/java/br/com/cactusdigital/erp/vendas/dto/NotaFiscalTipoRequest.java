package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotaFiscalTipoRequest(
        @NotNull Long notaFiscalModeloId,
        @NotBlank @Size(max = 50) String nome,
        @Size(max = 250) String descricao,
        @Size(max = 3) String serie,
        @Size(max = 3) String serieScan,
        @NotNull Situacao situacao) {
}
