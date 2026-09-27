package br.com.cactusdigital.erp.loja.dto;

import br.com.cactusdigital.erp.loja.entity.TipoAtributo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/** {@code opcoes} só é relevante (e nasce vazio se omitido) quando {@code tipo=LISTA}. */
public record AtributoCategoriaRequest(
        @NotNull Long categoriaLojaId,
        @NotBlank @Size(max = 60) String nome,
        @NotNull TipoAtributo tipo,
        boolean obrigatorio,
        boolean geraVariacao,
        Integer ordem,
        @Valid List<AtributoCategoriaOpcaoRequest> opcoes) {
}
