package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.TefTipoGp;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FormaPagamentoRequest(
        @NotBlank @Size(max = 3) String codigo,
        @NotBlank @Size(max = 20) String descricao,
        @Size(max = 50) String icone,
        boolean tef,
        boolean imprimeVinculado,
        boolean permiteTroco,
        TefTipoGp tefTipoGp,
        boolean geraParcelas,
        @Size(max = 2) String codigoPagamentoNfce,
        @NotNull Situacao situacao) {
}
