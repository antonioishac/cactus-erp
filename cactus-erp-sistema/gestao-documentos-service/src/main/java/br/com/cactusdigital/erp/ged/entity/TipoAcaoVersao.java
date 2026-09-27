package br.com.cactusdigital.erp.ged.entity;

/** Validado contra {@code fenix.sql.GED_VERSAO_DOCUMENTO.ACAO} (Incluído/Alterado/Excluído).
 * {@code EXCLUSAO} não tem arquivo associado — é só o registro do evento. */
public enum TipoAcaoVersao {
    INCLUSAO,
    ALTERACAO,
    EXCLUSAO
}
