package br.com.cactusdigital.erp.sincronizacao.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/** Payload achatado de cliente pro PDV — campo a campo contra a tabela `CLIENTE` no CLAUDE.md
 * (inclui fidelidade e {@code limiteCredito} = {@code FIADO_VALOR_TETO} do PDV). */
public record ClienteSincronizacaoResponse(
        Long id,
        String nome,
        String tipoPessoa,
        String documento,
        String email,
        String url,
        BigDecimal limiteCredito,
        BigDecimal taxaDesconto,
        String fidelidadeAviso,
        Integer fidelidadeQuantidade,
        BigDecimal fidelidadeValor,
        String situacao,
        List<Map<String, Object>> enderecos,
        List<Map<String, Object>> contatos,
        List<Map<String, Object>> telefones,
        Instant dataAtualizacao) {
}
