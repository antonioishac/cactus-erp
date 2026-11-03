package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.TipoDocumentoFiscal;
import java.time.Instant;

public record NumeroFiscalPuladoResponse(
        Long id,
        Long empresaId,
        TipoDocumentoFiscal tipoDocumento,
        String serie,
        Long numero,
        String motivo,
        String protocoloInutilizacao,
        Instant dataRegularizacao,
        Instant dataCriacao) {
}
