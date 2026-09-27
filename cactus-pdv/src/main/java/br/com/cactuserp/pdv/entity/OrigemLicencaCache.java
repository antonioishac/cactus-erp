package br.com.cactuserp.pdv.entity;

/** Origem de um registro em {@link LicencaCache}. */
public enum OrigemLicencaCache {
    /** Retaguarda respondeu com sucesso — situação confirmada online. */
    R,
    /** Janela de tolerância offline expirou — sistema forçou bloqueio por segurança. */
    F,
    /**
     * Instalação sem retaguarda configurada (nunca houve contato remoto
     * bem-sucedido) — situação lida direto de
     * {@code PDV_CONFIGURACAO.PLANO_SITUACAO}. Só gravado quando a
     * situação muda em relação ao último registro conhecido (qualquer
     * origem), pra não inundar {@link LicencaCache} a cada consulta.
     */
    L
}
