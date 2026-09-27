package br.com.cactusdigital.erp.ged.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import java.math.BigDecimal;

public record TipoDocumentoResponse(Long id, String nome, BigDecimal tamanhoMaximoMb, Situacao situacao) {
}
