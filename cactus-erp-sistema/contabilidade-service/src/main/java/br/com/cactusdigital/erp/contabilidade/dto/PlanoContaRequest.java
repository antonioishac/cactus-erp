package br.com.cactusdigital.erp.contabilidade.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import br.com.cactusdigital.erp.contabilidade.entity.NaturezaConta;
import br.com.cactusdigital.erp.contabilidade.entity.TipoContaContabil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PlanoContaRequest(
        @NotNull Long empresaId,
        @NotBlank @Size(max = 30) String codigo,
        @NotBlank @Size(max = 150) String nome,
        Long contaPaiId,
        @NotNull TipoContaContabil tipo,
        @NotNull NaturezaConta natureza,
        boolean contaAnalitica,
        @Size(max = 30) String codigoReferencialRfb,
        Situacao situacao) {
}
