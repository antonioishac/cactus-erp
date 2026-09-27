package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** {@code issConfiguracao} só é preenchido quando a operação é de serviço (não mercadoria). */
public record OperacaoFiscalRequest(
        @NotBlank @Size(max = 100) String nome,
        @Size(max = 100) String descricaoNaNf,
        Long cfopId,
        String observacao,
        @NotNull Situacao situacao,
        @Valid IssConfiguracaoDTO issConfiguracao) {
}
