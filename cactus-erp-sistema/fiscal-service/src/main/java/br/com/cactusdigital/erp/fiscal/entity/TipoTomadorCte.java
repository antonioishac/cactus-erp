package br.com.cactusdigital.erp.fiscal.entity;

/**
 * Tomador do serviço de transporte — quem paga o frete (validado contra
 * {@code fenix.sql.CTE_CABECALHO.TOMADOR}: {@code 0-Remetente;1-Expedidor;2-Recebedor;
 * 3-Destinatário;4-Outros}). É uma decisão de negócio da venda (não derivável só do
 * {@code TipoFrete} CIF/FOB), por isso é informado explicitamente ao gerar o CT-e.
 */
public enum TipoTomadorCte {
    REMETENTE("0"),
    EXPEDIDOR("1"),
    RECEBEDOR("2"),
    DESTINATARIO("3"),
    OUTROS("4");

    private final String codigo;

    TipoTomadorCte(String codigo) {
        this.codigo = codigo;
    }

    public String codigo() {
        return codigo;
    }
}
