package br.com.cactuserp.pdv.entity;

/**
 * Situação da assinatura/mensalidade do estabelecimento, mapeada de
 * {@code PDV_CONFIGURACAO.PLANO_SITUACAO}.
 *
 * <p>Usada pela trava de segurança do PDV — ver {@code LICENCIAMENTO.md} na
 * raiz do projeto para o desenho completo (estados, fluxo de verificação,
 * tolerância offline).
 */
public enum PlanoSituacao {
    /** Uso normal, sem restrições. */
    A,
    /** Inadimplente: uso liberado com aviso, não bloqueia a operação. */
    I,
    /** Bloqueado: impede abrir novo movimento de caixa. */
    B
}
