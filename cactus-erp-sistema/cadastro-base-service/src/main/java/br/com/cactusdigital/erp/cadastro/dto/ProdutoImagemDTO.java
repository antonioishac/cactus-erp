package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Upload direto ainda não existe (depende do futuro GED) — por ora só imagem já hospedada, referenciada por URL. */
public record ProdutoImagemDTO(
        @NotBlank @Size(max = 500) String url,
        @Size(max = 64) String hashArquivo,
        boolean principal,
        Integer ordem,
        Situacao situacao) {
}
