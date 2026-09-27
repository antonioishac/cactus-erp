package br.com.cactusdigital.erp.sincronizacao.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Caminho B da seção 7.2 do FASE6_SINCRONIZACAO_PDV.md — payload reduzido pra balança de
 * rede autônoma (não mediada pelo PDV): só o que um equipamento de PLU precisa.
 * <p>
 * {@code pesoVariavel} é uma heurística, não um campo modelado em {@code Produto}
 * (registrado como gap, não inventado): {@code true} quando a unidade de venda é "KG" —
 * suficiente pro caso de uso de balança (produto vendido no peso), mas não é um sinal
 * fiscal formal. {@code validadeDias} não tem produtor real ainda (nenhuma entidade de
 * validade/lote no cadastro-base-service) — fica sempre nulo, registrado como gap
 * futuro, não escondido.
 */
public record ProdutoBalancaSincronizacaoResponse(
        Integer codigoBalanca,
        String descricaoTruncada,
        BigDecimal precoVenda,
        boolean pesoVariavel,
        Integer validadeDias,
        Instant dataAtualizacao) {
}
