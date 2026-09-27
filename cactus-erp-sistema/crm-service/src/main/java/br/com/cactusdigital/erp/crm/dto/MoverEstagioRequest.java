package br.com.cactusdigital.erp.crm.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** {@code motivoPerda} só é exigido (validado em {@code OportunidadeService#moverEstagio}) quando o estágio destino tem {@code tipoEncerramento=PERDIDO}. */
public record MoverEstagioRequest(
        @NotNull Long estagioFunilId,
        @Size(max = 250) String motivoPerda) {
}
