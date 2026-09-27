package br.com.cactusdigital.erp.cadastro.entity;

/**
 * Campo {@code TIPO_ITEM} do Registro 0200 do SPED Fiscal (EFD ICMS/IPI) — tabela
 * oficial fechada da Receita Federal, por isso modelada como enum (mesmo padrão de
 * {@link CstIcms}), não como lookup configurável pela empresa.
 */
public enum TipoItemSped {
    MERCADORIA_PARA_REVENDA("00", "Mercadoria para Revenda"),
    MATERIA_PRIMA("01", "Matéria-Prima"),
    EMBALAGEM("02", "Embalagem"),
    PRODUTO_EM_PROCESSO("03", "Produto em Processo"),
    PRODUTO_ACABADO("04", "Produto Acabado"),
    SUBPRODUTO("05", "Subproduto"),
    PRODUTO_INTERMEDIARIO("06", "Produto Intermediário"),
    MATERIAL_USO_CONSUMO("07", "Material de Uso e Consumo"),
    ATIVO_IMOBILIZADO("08", "Ativo Imobilizado"),
    SERVICOS("09", "Serviços"),
    OUTROS_INSUMOS("10", "Outros insumos"),
    OUTRAS("99", "Outras");

    private final String codigo;
    private final String descricao;

    TipoItemSped(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }
}
