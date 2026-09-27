package br.com.cactusdigital.erp.financeiro.cnab240;

/**
 * Código de ocorrência (retorno) do FEBRABAN — tabela padrão usada por todos os bancos
 * pra reportar o que aconteceu com um título em cobrança. <b>Transparência</b>: só
 * {@link #LIQUIDACAO_NORMAL} dispara baixa automática nesta implementação — decisão
 * deliberadamente conservadora. Outros códigos de "quitação" existem na tabela FEBRABAN
 * (ex: liquidação em cartório, baixado automaticamente por decurso de prazo) mas cada um
 * tem nuance de negócio própria (cartório cobra custas, baixa por decurso não significa
 * pago) que não modelamos ainda — melhor deixar pra revisão manual do que arriscar dar
 * baixa errada num título por causa de um código mal interpretado.
 */
public final class OcorrenciaRetornoCnab240 {

    /** "02" — Entrada confirmada (o banco aceitou o título registrado na remessa). */
    public static final String ENTRADA_CONFIRMADA = "02";

    /** "03" — Entrada rejeitada (erro no registro; o motivo detalhado fica em
     * {@code RetornoBancarioItem.observacao} quando disponível). */
    public static final String ENTRADA_REJEITADA = "03";

    /** "06" — Liquidação normal: o sacado pagou o título. Único código que aciona
     * {@code baixa automática} nesta implementação. */
    public static final String LIQUIDACAO_NORMAL = "06";

    /** "09" — Baixado automaticamente via arquivo (decurso de prazo) — NÃO é pagamento,
     * só registrado pra consulta, sem baixa automática. */
    public static final String BAIXADO_AUTOMATICAMENTE = "09";

    private OcorrenciaRetornoCnab240() {
    }

    public static boolean geraBaixaAutomatica(String codigoMovimento) {
        return LIQUIDACAO_NORMAL.equals(codigoMovimento);
    }
}
