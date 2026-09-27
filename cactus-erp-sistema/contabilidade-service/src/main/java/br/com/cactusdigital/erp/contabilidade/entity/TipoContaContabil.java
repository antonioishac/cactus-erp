package br.com.cactusdigital.erp.contabilidade.entity;

/**
 * Grupo patrimonial/resultado de uma conta do {@link PlanoConta} — mesma classificação
 * usada por qualquer plano de contas de mercado (referencial da RFB incluso, ver Bloco I
 * da ECD, registro {@code I050}, campo {@code COD_NAT}). {@code RESULTADO} cobre contas
 * de apuração (ex: "Apuração do Resultado do Exercício"), distintas de
 * {@code RECEITA}/{@code DESPESA} em si.
 */
public enum TipoContaContabil {
    ATIVO,
    PASSIVO,
    PATRIMONIO_LIQUIDO,
    RECEITA,
    DESPESA,
    RESULTADO
}
