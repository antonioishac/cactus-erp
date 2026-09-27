package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.TipoDocumentoFiscal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Chamado pelo {@code fiscal-service} — ver Javadoc de {@code NumeroFiscalPulado}. */
public record NumeroFiscalPuladoRequest(
        @NotNull TipoDocumentoFiscal tipoDocumento,
        @Size(max = 3) String serie,
        @NotNull Long numero,
        @Size(max = 500) String motivo) {
}
