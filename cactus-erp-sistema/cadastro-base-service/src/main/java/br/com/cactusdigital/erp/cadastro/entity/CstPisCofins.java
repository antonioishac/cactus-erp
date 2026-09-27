package br.com.cactusdigital.erp.cadastro.entity;

/**
 * Situação Tributária do PIS/COFINS — a tabela oficial (Tabela 4.3.3 do layout da NFe)
 * é IDÊNTICA pras duas contribuições, por isso um enum só serve pras duas
 * ({@code TributPis}/{@code TributCofins} nas configurações). Só os códigos de SAÍDA
 * (venda) — a faixa 50-75 (créditos de entrada) fica pro módulo de Compras.
 */
public enum CstPisCofins {
    CST_01("01", "Operação Tributável com Alíquota Básica"),
    CST_02("02", "Operação Tributável com Alíquota Diferenciada"),
    CST_03("03", "Operação Tributável com Alíquota por Unidade de Medida de Produto"),
    CST_04("04", "Operação Tributável Monofásica - Revenda a Alíquota Zero"),
    CST_05("05", "Operação Tributável por Substituição Tributária"),
    CST_06("06", "Operação Tributável a Alíquota Zero"),
    CST_07("07", "Operação Isenta da Contribuição"),
    CST_08("08", "Operação sem Incidência da Contribuição"),
    CST_09("09", "Operação com Suspensão da Contribuição"),
    CST_49("49", "Outras Operações de Saída");

    private final String codigo;
    private final String descricao;

    CstPisCofins(String codigo, String descricao) {
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
