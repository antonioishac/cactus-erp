package br.com.cactusdigital.erp.estoque.dto;

import br.com.cactusdigital.erp.estoque.entity.SituacaoInventario;
import br.com.cactusdigital.erp.estoque.entity.TipoInventario;
import java.time.Instant;
import java.util.List;

public record InventarioFisicoResponse(
        Long id,
        Long empresaId,
        Long depositoId,
        String depositoNome,
        TipoInventario tipo,
        SituacaoInventario situacao,
        Long responsavelId,
        Instant dataAbertura,
        Instant dataFechamento,
        List<InventarioFisicoItemResponse> itens) {
}
