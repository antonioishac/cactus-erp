package br.com.cactusdigital.erp.sincronizacao.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record TabelaPrecoSincronizacaoResponse(
        Long id,
        String nome,
        String descricao,
        String tipoAjuste,
        String situacao,
        List<Map<String, Object>> produtos,
        Instant dataAtualizacao) {
}
