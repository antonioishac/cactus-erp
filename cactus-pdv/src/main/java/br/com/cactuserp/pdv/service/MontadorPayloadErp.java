package br.com.cactuserp.pdv.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Monta, a partir das tabelas do PDV, o payload de cada tipo de registro no
 * contrato do {@code sincronizacao-service} (FASE6_SINCRONIZACAO_PDV.md, seções
 * 12, 13 e 15). Lê sempre o estado ATUAL da linha de origem na hora do envio.
 *
 * <p>Ids de cadastro vão traduzidos para o id do ERP ({@code ID_ERP}, vindo do
 * pull). Cadastro obrigatório sem vínculo lança {@link EnvioBloqueadoException}:
 * o registro fica bloqueado e é tentado de novo na próxima rodada (o pull pode
 * trazer o vínculo depois). Terminal ({@code pdvCaixaId}), operador e gerente
 * vão com o id LOCAL do PDV — são identidades do próprio terminal no ERP.
 */
@Service
public class MontadorPayloadErp {

    private final JdbcTemplate jdbc;
    private final Long depositoId;

    public MontadorPayloadErp(JdbcTemplate jdbc, @Value("${cactus.pdv.sync.deposito-id:}") Long depositoId) {
        this.jdbc = jdbc;
        this.depositoId = depositoId;
    }

    /** Registro que não pode ser enviado agora (falta vínculo/configuração); fica para a próxima rodada. */
    public static class EnvioBloqueadoException extends RuntimeException {
        public EnvioBloqueadoException(String mensagem) { super(mensagem); }
    }

    // ================================================================ caixa

    public Map<String, Object> aberturaCaixa(long movimentoId) {
        Map<String, Object> m = linha("SELECT m.ID_PDV_CAIXA, c.NOME, m.VALOR_FUNDO_INICIAL, m.DATA_ABERTURA FROM PDV_MOVIMENTO m "
                + "LEFT JOIN PDV_CAIXA c ON c.ID = m.ID_PDV_CAIXA WHERE m.ID = ?", movimentoId);
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("pdvCaixaId", m.get("ID_PDV_CAIXA"));
        p.put("pdvCaixaNome", m.get("NOME"));
        p.put("valorFundoInicial", zeroSeNulo(m.get("VALOR_FUNDO_INICIAL")));
        p.put("dataAbertura", iso(m.get("DATA_ABERTURA")));
        return p;
    }

    public Map<String, Object> sangria(long id) {
        return movimentoDinheiro("SELECT s.VALOR, s.DATA_SANGRIA AS DATA, s.OBSERVACAO, m.ID_PDV_CAIXA, c.NOME FROM PDV_SANGRIA s "
                + "JOIN PDV_MOVIMENTO m ON m.ID = s.ID_PDV_MOVIMENTO LEFT JOIN PDV_CAIXA c ON c.ID = m.ID_PDV_CAIXA WHERE s.ID = ?", id);
    }

    public Map<String, Object> suprimento(long id) {
        return movimentoDinheiro("SELECT s.VALOR, s.DATA_SUPRIMENTO AS DATA, s.OBSERVACAO, m.ID_PDV_CAIXA, c.NOME FROM PDV_SUPRIMENTO s "
                + "JOIN PDV_MOVIMENTO m ON m.ID = s.ID_PDV_MOVIMENTO LEFT JOIN PDV_CAIXA c ON c.ID = m.ID_PDV_CAIXA WHERE s.ID = ?", id);
    }

    private Map<String, Object> movimentoDinheiro(String sql, long id) {
        Map<String, Object> m = linha(sql, id);
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("pdvCaixaId", m.get("ID_PDV_CAIXA"));
        p.put("pdvCaixaNome", m.get("NOME"));
        p.put("valor", m.get("VALOR"));
        p.put("dataMovimento", iso(m.get("DATA")));
        p.put("observacao", m.get("OBSERVACAO"));
        return p;
    }

    /** Só a divergência da forma DINHEIRO (tPag 01), nunca o total de todas as formas — ver seção 13 do FASE6. */
    public Map<String, Object> conferenciaCaixa(long id) {
        Map<String, Object> m = linha("SELECT cc.DATA_CONFERENCIA, cc.OBSERVACAO, m.ID_PDV_CAIXA, "
                + "(SELECT COALESCE(SUM(d.VALOR_DIVERGENCIA), 0) FROM PDV_CONFERENCIA_CAIXA_DETALHE d "
                + "  JOIN PDV_TIPO_PAGAMENTO t ON t.ID = d.ID_PDV_TIPO_PAGAMENTO "
                + "  WHERE d.ID_PDV_CONFERENCIA_CAIXA = cc.ID AND t.CODIGO_PAGAMENTO_NFCE = '01') AS DIVERGENCIA_DINHEIRO "
                + "FROM PDV_CONFERENCIA_CAIXA cc JOIN PDV_MOVIMENTO m ON m.ID = cc.ID_PDV_MOVIMENTO WHERE cc.ID = ?", id);
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("pdvCaixaId", m.get("ID_PDV_CAIXA"));
        p.put("valorDivergenciaDinheiro", m.get("DIVERGENCIA_DINHEIRO"));
        p.put("dataConferencia", iso(m.get("DATA_CONFERENCIA")));
        p.put("observacao", m.get("OBSERVACAO"));
        return p;
    }

    /**
     * Pagamento (P) cai no caixa do terminal onde o dinheiro entrou: o
     * lançamento não guarda o turno, então é o turno do mesmo operador que
     * estava aberto na data (o mais recente). Sem turno encontrado, bloqueia.
     */
    public Map<String, Object> fiadoLancamento(long id) {
        Map<String, Object> f = linha("SELECT f.ID_CLIENTE, cl.ID_ERP AS CLIENTE_ERP, f.TIPO_LANCAMENTO, f.VALOR, f.DATA_LANCAMENTO, "
                + "f.OBSERVACAO, f.ID_OPERADOR FROM CLIENTE_FIADO_LANCAMENTO f JOIN CLIENTE cl ON cl.ID = f.ID_CLIENTE WHERE f.ID = ?", id);
        if (f.get("CLIENTE_ERP") == null) {
            throw new EnvioBloqueadoException("Cliente #" + f.get("ID_CLIENTE") + " do PDV ainda não está vinculado ao ERP (ID_ERP).");
        }
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("clienteId", f.get("CLIENTE_ERP"));
        p.put("tipoLancamento", f.get("TIPO_LANCAMENTO"));
        p.put("valor", f.get("VALOR"));
        p.put("dataLancamento", iso(f.get("DATA_LANCAMENTO")));
        p.put("observacao", f.get("OBSERVACAO"));
        if ("P".equals(f.get("TIPO_LANCAMENTO"))) {
            List<Long> caixas = jdbc.queryForList("SELECT ID_PDV_CAIXA FROM PDV_MOVIMENTO WHERE ID_PDV_OPERADOR = ? "
                            + "AND DATA_ABERTURA <= ? AND (DATA_FECHAMENTO IS NULL OR DATA_FECHAMENTO >= ?) ORDER BY ID DESC LIMIT 1",
                    Long.class, f.get("ID_OPERADOR"), f.get("DATA_LANCAMENTO"), f.get("DATA_LANCAMENTO"));
            if (caixas.isEmpty()) {
                throw new EnvioBloqueadoException("Pagamento de fiado #" + id + " sem turno de caixa do operador na data — "
                        + "não dá para saber em qual caixa o dinheiro entrou.");
            }
            p.put("pdvCaixaId", caixas.get(0));
        }
        return p;
    }

    // ================================================================ venda

    /**
     * Venda {@code F}/{@code P} vai como fechada ({@code P} é problema só na
     * transmissão fiscal; a venda aconteceu). Venda {@code C} que chega aqui foi
     * fechada e cancelada antes de ser enviada: vai direto como cancelada.
     */
    public Map<String, Object> venda(long id) {
        Map<String, Object> v = linha("SELECT v.*, m.ID_PDV_CAIXA, m.ID_PDV_OPERADOR, c.NOME AS CAIXA_NOME, cl.ID_ERP AS CLIENTE_ERP "
                + "FROM PDV_VENDA_CABECALHO v JOIN PDV_MOVIMENTO m ON m.ID = v.ID_PDV_MOVIMENTO "
                + "LEFT JOIN PDV_CAIXA c ON c.ID = m.ID_PDV_CAIXA LEFT JOIN CLIENTE cl ON cl.ID = v.ID_CLIENTE WHERE v.ID = ?", id);
        boolean cancelada = "C".equals(v.get("STATUS_VENDA"));
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("pdvCaixaId", v.get("ID_PDV_CAIXA"));
        p.put("pdvCaixaNome", v.get("CAIXA_NOME"));
        p.put("pdvOperadorId", v.get("ID_PDV_OPERADOR"));
        p.put("clienteId", v.get("CLIENTE_ERP"));
        p.put("nomeCliente", v.get("NOME_CLIENTE"));
        p.put("cpfCnpjCliente", v.get("CPF_CNPJ_CLIENTE"));
        p.put("dataHoraVenda", dataHora(v.get("DATA_VENDA"), v.get("HORA_VENDA")));
        p.put("situacao", cancelada ? "C" : "F");
        p.put("origemVenda", v.get("ORIGEM_VENDA"));
        p.put("valorTotalProdutos", v.get("VALOR_TOTAL_PRODUTOS"));
        p.put("valorDesconto", zeroSeNulo(v.get("VALOR_DESCONTO")));
        p.put("valorAcrescimo", zeroSeNulo(v.get("VALOR_ACRESCIMO")));
        p.put("valorFinal", zeroSeNulo(v.get("VALOR_FINAL")));
        p.put("valorRecebido", zeroSeNulo(v.get("VALOR_RECEBIDO")));
        p.put("valorTroco", zeroSeNulo(v.get("VALOR_TROCO")));
        p.put("valorBaseIcms", v.get("VALOR_BASE_ICMS"));
        p.put("valorIcms", v.get("VALOR_ICMS"));
        p.put("valorPis", v.get("VALOR_PIS"));
        p.put("valorCofins", v.get("VALOR_COFINS"));
        p.put("valorIssqn", v.get("VALOR_ISSQN"));
        p.put("tipoOperacaoFiscal", v.get("TIPO_OPERACAO"));
        p.put("statusFiscal", v.get("STATUS_FISCAL"));
        p.put("chaveAcessoFiscal", v.get("CHAVE_ACESSO_FISCAL"));
        p.put("protocoloAutorizacao", v.get("PROTOCOLO_AUTORIZACAO"));
        p.put("coo", v.get("COO"));
        p.put("ccf", v.get("CCF"));

        List<Map<String, Object>> itens = new ArrayList<>();
        int sequencia = 0;
        for (Map<String, Object> i : jdbc.queryForList("SELECT d.*, pr.ID_ERP AS PRODUTO_ERP FROM PDV_VENDA_DETALHE d "
                + "JOIN PRODUTO pr ON pr.ID = d.ID_PRODUTO WHERE d.ID_PDV_VENDA_CABECALHO = ? ORDER BY d.ITEM, d.ID", id)) {
            sequencia++;
            if (i.get("PRODUTO_ERP") == null) {
                throw new EnvioBloqueadoException("Produto #" + i.get("ID_PRODUTO") + " do PDV ainda não está vinculado ao ERP (ID_ERP).");
            }
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("numeroItem", i.get("ITEM") != null ? i.get("ITEM") : sequencia);
            item.put("produtoId", i.get("PRODUTO_ERP"));
            item.put("gtin", i.get("GTIN"));
            item.put("quantidade", i.get("QUANTIDADE"));
            item.put("valorUnitario", i.get("VALOR_UNITARIO"));
            item.put("valorDesconto", zeroSeNulo(i.get("VALOR_DESCONTO")));
            item.put("valorAcrescimo", zeroSeNulo(i.get("VALOR_ACRESCIMO")));
            item.put("valorTotal", i.get("VALOR_TOTAL_ITEM") != null ? i.get("VALOR_TOTAL_ITEM") : i.get("VALOR_TOTAL"));
            item.put("cfop", i.get("CFOP"));
            item.put("cst", i.get("CST"));
            item.put("aliquotaIcms", i.get("TAXA_ICMS"));
            item.put("valorIcms", i.get("VALOR_ICMS"));
            item.put("valorPis", i.get("VALOR_PIS"));
            item.put("valorCofins", i.get("VALOR_COFINS"));
            item.put("cancelado", "S".equals(i.get("CANCELADO")));
            itens.add(item);
        }
        p.put("itens", itens);

        List<Map<String, Object>> pagamentos = new ArrayList<>();
        for (Map<String, Object> pg : jdbc.queryForList("SELECT t.*, tp.CODIGO_PAGAMENTO_NFCE, tp.DESCRICAO, tp.ID_ERP AS FORMA_ERP "
                + "FROM PDV_TOTAL_TIPO_PAGAMENTO t LEFT JOIN PDV_TIPO_PAGAMENTO tp ON tp.ID = t.ID_PDV_TIPO_PAGAMENTO "
                + "WHERE t.ID_PDV_VENDA_CABECALHO = ? ORDER BY t.ID", id)) {
            Map<String, Object> pagamento = new LinkedHashMap<>();
            pagamento.put("formaPagamentoId", pg.get("FORMA_ERP"));
            pagamento.put("codigoPagamentoNfce", pg.get("CODIGO_PAGAMENTO_NFCE"));
            pagamento.put("descricao", pg.get("DESCRICAO"));
            pagamento.put("valor", pg.get("VALOR"));
            pagamento.put("nsu", pg.get("NSU"));
            pagamento.put("rede", pg.get("REDE"));
            pagamento.put("cartaoDebitoCredito", pg.get("CARTAO_DC"));
            pagamento.put("estornado", "S".equals(pg.get("ESTORNO")));
            pagamentos.add(pagamento);
        }
        p.put("pagamentos", pagamentos);
        return p;
    }

    public Map<String, Object> devolucao(long id, String vendaIdOrigem) {
        Map<String, Object> d = linha("SELECT dv.*, det.ITEM FROM PDV_DEVOLUCAO dv JOIN PDV_VENDA_DETALHE det ON det.ID = dv.ID_PDV_VENDA_DETALHE "
                + "WHERE dv.ID = ?", id);
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("vendaIdOrigem", vendaIdOrigem);
        p.put("numeroItem", d.get("ITEM"));
        p.put("quantidadeDevolvida", d.get("QUANTIDADE_DEVOLVIDA"));
        p.put("valorDevolvido", d.get("VALOR_DEVOLVIDO"));
        p.put("motivo", d.get("MOTIVO"));
        p.put("pdvOperadorId", d.get("ID_OPERADOR"));
        p.put("pdvGerenteAutorizadorId", d.get("ID_GERENTE_AUTORIZADOR"));
        p.put("dataHoraDevolucao", dataHora(d.get("DATA_DEVOLUCAO"), d.get("HORA_DEVOLUCAO")));
        return p;
    }

    public Map<String, Object> cancelamentoVenda(long vendaId, String vendaIdOrigem) {
        Map<String, Object> v = linha("SELECT DATA_CANCELAMENTO, HORA_CANCELAMENTO, ID_OPERADOR_CANCELAMENTO, ID_GERENTE_CANCELAMENTO, "
                + "JUSTIFICATIVA_CANCELAMENTO, PROTOCOLO_CANCELAMENTO FROM PDV_VENDA_CABECALHO WHERE ID = ?", vendaId);
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("vendaIdOrigem", vendaIdOrigem);
        p.put("pdvOperadorId", v.get("ID_OPERADOR_CANCELAMENTO"));
        p.put("pdvGerenteAutorizadorId", v.get("ID_GERENTE_CANCELAMENTO"));
        p.put("dataHoraCancelamento", dataHora(v.get("DATA_CANCELAMENTO"), v.get("HORA_CANCELAMENTO")));
        p.put("justificativa", v.get("JUSTIFICATIVA_CANCELAMENTO"));
        p.put("protocoloCancelamento", v.get("PROTOCOLO_CANCELAMENTO"));
        return p;
    }

    // ================================================================ estoque

    /**
     * Tipo do PDV (E/S/A/P/D) → tipo do ERP. Ajuste leva a direção pela
     * comparação saldo novo × anterior. Origem: saída de item de venda =
     * {@code VENDA_PDV}, reentrada = {@code DEVOLUCAO_PDV} (inclui o cancelamento
     * de venda), o resto = {@code AJUSTE_MANUAL}.
     */
    public Map<String, Object> estoqueMovimento(long id) {
        if (depositoId == null) {
            throw new EnvioBloqueadoException("Depósito do ERP para o estoque desta loja não configurado (cactus.pdv.sync.deposito-id).");
        }
        Map<String, Object> e = linha("SELECT e.*, pr.ID_ERP AS PRODUTO_ERP FROM ESTOQUE_MOVIMENTO e JOIN PRODUTO pr ON pr.ID = e.ID_PRODUTO "
                + "WHERE e.ID = ?", id);
        if (e.get("PRODUTO_ERP") == null) {
            throw new EnvioBloqueadoException("Produto #" + e.get("ID_PRODUTO") + " do PDV ainda não está vinculado ao ERP (ID_ERP).");
        }
        String tipo = (String) e.get("TIPO_MOVIMENTO");
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("produtoId", e.get("PRODUTO_ERP"));
        p.put("depositoId", depositoId);
        p.put("tipoMovimento", switch (tipo) {
            case "E" -> "ENTRADA";
            case "S" -> "SAIDA";
            case "A" -> "AJUSTE";
            case "P" -> "PERDA";
            case "D" -> "DEVOLUCAO";
            default -> throw new EnvioBloqueadoException("Tipo de movimento de estoque desconhecido: " + tipo);
        });
        if ("A".equals(tipo)) {
            BigDecimal anterior = (BigDecimal) e.get("QUANTIDADE_ANTERIOR");
            BigDecimal nova = (BigDecimal) e.get("QUANTIDADE_NOVA");
            p.put("direcaoAjuste", nova.compareTo(anterior) >= 0 ? "ACRESCIMO" : "DECRESCIMO");
        }
        p.put("quantidade", ((BigDecimal) e.get("QUANTIDADE")).abs());
        p.put("origemTipo", "S".equals(tipo) && e.get("ID_PDV_VENDA_DETALHE") != null ? "VENDA_PDV"
                : "D".equals(tipo) ? "DEVOLUCAO_PDV" : "AJUSTE_MANUAL");
        p.put("observacao", e.get("OBSERVACAO"));
        return p;
    }

    // ================================================================ utilitários

    private Map<String, Object> linha(String sql, long id) {
        List<Map<String, Object>> linhas = jdbc.queryForList(sql, id);
        if (linhas.isEmpty()) {
            throw new EnvioBloqueadoException("Registro de origem #" + id + " não encontrado no PDV.");
        }
        return linhas.get(0);
    }

    private static Object zeroSeNulo(Object valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private static String iso(Object data) {
        if (data == null) return null;
        return data instanceof Date d ? d.toLocalDate().toString() : data instanceof LocalDate ld ? ld.toString() : data.toString();
    }

    /** {@code yyyy-MM-ddTHH:mm:ss} (hora local da loja), o formato que o ERP espera. */
    private static String dataHora(Object data, Object hora) {
        String dia = iso(data);
        if (dia == null) return null;
        String h = hora != null && !hora.toString().isBlank() ? hora.toString() : "00:00:00";
        return dia + "T" + (h.length() == 5 ? h + ":00" : h);
    }
}
