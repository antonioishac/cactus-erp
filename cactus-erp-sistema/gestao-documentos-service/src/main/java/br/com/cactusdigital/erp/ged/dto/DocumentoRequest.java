package br.com.cactusdigital.erp.ged.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** {@code permiteExclusao}/{@code permiteAlteracao} nulos assumem {@code true}
 * (permissivo por padrão); {@code assinado} nulo assume {@code false}. */
public record DocumentoRequest(
        Long pastaId,
        @NotNull Long tipoDocumentoId,
        @NotBlank @Size(max = 150) String nome,
        @Size(max = 250) String descricao,
        @Size(max = 250) String palavrasChave,
        @Size(max = 50) String entidadeTipo,
        Long entidadeId,
        Boolean permiteExclusao,
        Boolean permiteAlteracao,
        Boolean assinado,
        LocalDate dataFimVigencia) {
}
