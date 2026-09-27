package br.com.cactusdigital.erp.contabilidade.dto;

import br.com.cactusdigital.erp.contabilidade.entity.TipoPartida;
import java.math.BigDecimal;

public record PartidaContabilResponse(
        Long id,
        Long contaId,
        String contaCodigo,
        String contaNome,
        Long centroCustoId,
        String centroCustoNome,
        TipoPartida tipoPartida,
        BigDecimal valor,
        String historicoComplementar) {
}
