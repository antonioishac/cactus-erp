package br.com.cactusdigital.erp.loja.entity;

/**
 * Plano de assinatura SaaS da {@link Loja} — mensalidade fixa (decisão do usuário,
 * 2026-09-04: SaaS por assinatura, sem comissão/split sobre venda, ver CLAUDE.md).
 * Valores fixos por enquanto (sem catálogo de planos configurável em banco) — vira
 * catálogo de verdade só quando a cobrança recorrente for construída de fato
 * (fora de escopo desta fatia).
 */
public enum PlanoAssinatura {
    BASICO,
    INTERMEDIARIO,
    PREMIUM
}
