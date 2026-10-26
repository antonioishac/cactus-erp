package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.PdvApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ponta a ponta: PDV real → sincronizacao-service → RabbitMQ → vendas/financeiro/
 * estoque reais. O lado do PDV roda em transação REVERTIDA; o lado do ERP grava de
 * verdade e precisa ser limpo depois (registros com {@code id_origem} começando por
 * {@code IT-ENVIO:}). Fora do {@code mvn test} padrão (sufixo IT). Mesmo ambiente de
 * {@link SincronizacaoPullServiceIT} (client de loja no ambiente).
 */
@SpringBootTest(classes = PdvApplication.class, properties = {
        "cactus.pdv.sync.produto.subgrupo-padrao-id=1",
        "cactus.pdv.sync.produto.tipo-padrao-id=1",
        "cactus.pdv.sync.produto.grupo-tributario-padrao-id=1",
        "cactus.pdv.sync.dispositivo-id=IT-ENVIO",
        "cactus.pdv.sync.deposito-id=1"})
@Transactional
class SincronizacaoEnvioServiceIT {

    private static final String HOJE = LocalDate.now().toString();
    private static final String HORA = LocalTime.now().minusMinutes(10).format(DateTimeFormatter.ofPattern("HH:mm:ss"));

    @DynamicPropertySource
    static void propriedades(DynamicPropertyRegistry r) {
        r.add("cactus.pdv.sync.envio.a-partir-de", () -> HOJE);
    }

    @Autowired private SincronizacaoPullService pull;
    @Autowired private SincronizacaoEnvioService envio;
    @Autowired private JdbcTemplate jdbc;
    /** Leitura do ERP fora da transação do teste: dentro dela o InnoDB (REPEATABLE READ) congela a foto e o ERP "nunca muda". */
    private JdbcTemplate erp;

    @org.springframework.beans.factory.annotation.Value("${spring.datasource.url}") private String url;
    @org.springframework.beans.factory.annotation.Value("${spring.datasource.username}") private String usuario;
    @org.springframework.beans.factory.annotation.Value("${spring.datasource.password}") private String senha;

    @Test
    void operacaoDoDiaChegaAoErpComDinheiroVendaCancelamentoEBloqueioPorVinculo() throws Exception {
        erp = new JdbcTemplate(new org.springframework.jdbc.datasource.DriverManagerDataSource(url, usuario, senha));
        pull.executar();
        long produto = jdbc.queryForObject("SELECT ID FROM PRODUTO WHERE ID_ERP = 1", Long.class);
        long produtoSemVinculo = jdbc.queryForObject("SELECT MIN(ID) FROM PRODUTO WHERE ID_ERP IS NULL", Long.class);
        long cliente = jdbc.queryForObject("SELECT MIN(ID) FROM CLIENTE WHERE ID_ERP IS NOT NULL", Long.class);
        long caixa = 1L;

        long movimento = inserir("INSERT INTO PDV_MOVIMENTO (ID_PDV_OPERADOR, ID_PDV_CAIXA, DATA_ABERTURA, HORA_ABERTURA, "
                + "VALOR_FUNDO_INICIAL, STATUS_MOVIMENTO) VALUES (4, ?, CURDATE(), ?, 100, 'A')", caixa, HORA);
        inserir("INSERT INTO PDV_SUPRIMENTO (ID_PDV_MOVIMENTO, ID_OPERADOR, ID_GERENTE_AUTORIZADOR, DATA_SUPRIMENTO, HORA_SUPRIMENTO, VALOR) "
                + "VALUES (?, 4, 1, CURDATE(), ?, 20)", movimento, HORA);
        inserir("INSERT INTO PDV_SANGRIA (ID_PDV_MOVIMENTO, ID_OPERADOR, ID_GERENTE_AUTORIZADOR, DATA_SANGRIA, HORA_SANGRIA, VALOR) "
                + "VALUES (?, 4, 1, CURDATE(), ?, 10)", movimento, HORA);

        long v1 = venda(movimento, "F", produto, 2, 10, 30, 10);          // dinheiro líquido 20, depois devolve 1
        long v2 = venda(movimento, "F", produto, 1, 10, 10, 0);           // dinheiro 10, cancelada DEPOIS de enviada
        long v3 = venda(movimento, "F", produto, 1, 10, 10, 0);           // cancelada ANTES de enviar
        cancelar(v3);
        long v4 = venda(movimento, "F", produtoSemVinculo, 1, 10, 10, 0);  // produto sem ID_ERP: bloqueia
        long detalheV1 = jdbc.queryForObject("SELECT ID FROM PDV_VENDA_DETALHE WHERE ID_PDV_VENDA_CABECALHO = ?", Long.class, v1);
        estoque(produto, "S", 2, detalheV1);
        inserir("INSERT INTO CLIENTE_FIADO_LANCAMENTO (ID_CLIENTE, TIPO_LANCAMENTO, VALOR, SALDO_ANTERIOR, SALDO_NOVO, ID_OPERADOR, "
                + "DATA_LANCAMENTO, HORA_LANCAMENTO) VALUES (?, 'C', 15, 0, 15, 4, CURDATE(), ?)", cliente, HORA);
        long conferencia = inserir("INSERT INTO PDV_CONFERENCIA_CAIXA (ID_PDV_MOVIMENTO, ID_OPERADOR, ID_GERENTE_AUTORIZADOR, "
                + "DATA_CONFERENCIA, HORA_CONFERENCIA, VALOR_TOTAL_ESPERADO, VALOR_TOTAL_CONTADO, VALOR_DIVERGENCIA) "
                + "VALUES (?, 4, 1, CURDATE(), ?, 135, 130, -5)", movimento, HORA);
        inserir("INSERT INTO PDV_CONFERENCIA_CAIXA_DETALHE (ID_PDV_CONFERENCIA_CAIXA, ID_PDV_TIPO_PAGAMENTO, VALOR_ESPERADO, "
                + "VALOR_CONTADO, VALOR_DIVERGENCIA) VALUES (?, 1, 135, 130, -5)", conferencia);

        envio.executar(); // rodada 1: tudo, menos devolução/cancelamento (dependem da venda aceita)

        inserir("INSERT INTO PDV_DEVOLUCAO (ID_PDV_VENDA_CABECALHO, ID_PDV_VENDA_DETALHE, QUANTIDADE_DEVOLVIDA, VALOR_DEVOLVIDO, "
                + "ID_OPERADOR, ID_GERENTE_AUTORIZADOR, DATA_DEVOLUCAO, HORA_DEVOLUCAO) VALUES (?, ?, 1, 10, 4, 1, CURDATE(), ?)", v1, detalheV1, HORA);
        cancelar(v2);
        envio.executar(); // rodada 2: devolução e cancelamento da v2
        envio.executar(); // rodada 3: repetir não muda nada

        // ----- fila local
        assertThat(status("VENDA_PDV", v4)).isEqualTo("B");
        assertThat(erro("VENDA_PDV", v4)).contains("não está vinculado ao ERP");
        assertThat(jdbc.queryForObject("SELECT SITUACAO_ENVIADA FROM SINCRONIZACAO_ENVIO WHERE TIPO_REGISTRO='VENDA_PDV' AND ID_LOCAL=?",
                String.class, v3)).isEqualTo("C");
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM SINCRONIZACAO_ENVIO WHERE TIPO_REGISTRO='CANCELAMENTO_VENDA_PDV'", Integer.class))
                .as("só a v2 gera cancelamento; a v3 saiu cancelada").isEqualTo(1);
        assertThat(jdbc.queryForList("SELECT CONCAT(TIPO_REGISTRO,':',STATUS,':',COALESCE(ULTIMO_ERRO,'')) FROM SINCRONIZACAO_ENVIO "
                + "WHERE STATUS NOT IN ('E') AND NOT (TIPO_REGISTRO='VENDA_PDV' AND ID_LOCAL=" + v4 + ")", String.class)).isEmpty();

        // ----- ERP (assíncrono: espera o processamento das filas)
        aguardarErp();
        assertThat(erp.queryForList("SELECT CONCAT(tipo_registro,' ',status,' ',COALESCE(motivo_erro,'')) FROM cactus_sincronizacao.registro_sincronizacao "
                + "WHERE id_origem LIKE 'IT-ENVIO:%' AND status <> 'PROCESSADO'", String.class)).as("registros com erro no ERP").isEmpty();
        assertThat(erp.queryForObject("SELECT saldo_atual FROM cactus_financeiro.conta_bancaria WHERE empresa_id=4 AND pdv_dispositivo_id='IT-ENVIO' AND pdv_caixa_id=1",
                BigDecimal.class)).as("100 + 20 sup + 20 v1 + 10 v2 - 10 cancel v2 - 10 sangria - 5 falta").isEqualByComparingTo("125");
        Map<String, Object> vendas = erp.queryForMap("SELECT SUM(situacao='FECHADA') fechadas, SUM(situacao='CANCELADA') canceladas "
                + "FROM cactus_vendas.venda_pdv WHERE id_origem LIKE 'IT-ENVIO:%'");
        assertThat(((Number) vendas.get("fechadas")).intValue()).isEqualTo(1);
        assertThat(((Number) vendas.get("canceladas")).intValue()).isEqualTo(2);
        assertThat(erp.queryForObject("SELECT i.quantidade_devolvida FROM cactus_vendas.venda_pdv_item i JOIN cactus_vendas.venda_pdv v "
                + "ON v.id = i.venda_pdv_id WHERE v.id_origem = ?", BigDecimal.class, "IT-ENVIO:V:" + v1)).isEqualByComparingTo("1");
        assertThat(erp.queryForObject("SELECT COUNT(*) FROM cactus_financeiro.conta_receber WHERE origem_tipo='FIADO_PDV' AND empresa_id=4 "
                + "AND valor_original=15", Integer.class)).isEqualTo(1);
        assertThat(erp.queryForObject("SELECT COUNT(*) FROM cactus_estoque.estoque_movimento WHERE origem_tipo='VENDA_PDV' AND produto_id=1 "
                + "AND data_movimento >= NOW() - INTERVAL 10 MINUTE", Integer.class)).isEqualTo(1);
    }

    private long venda(long movimento, String status, long produto, int quantidade, int unitario, int recebido, int troco) {
        int total = quantidade * unitario;
        long id = inserir("INSERT INTO PDV_VENDA_CABECALHO (ID_PDV_MOVIMENTO, DATA_VENDA, HORA_VENDA, VALOR_VENDA, VALOR_TOTAL_PRODUTOS, "
                        + "VALOR_TOTAL_DOCUMENTO, VALOR_FINAL, VALOR_RECEBIDO, VALOR_TROCO, VALOR_DESCONTO, VALOR_ACRESCIMO, STATUS_VENDA, "
                        + "CUPOM_CANCELADO, ORIGEM_VENDA, TIPO_OPERACAO, STATUS_FISCAL) VALUES (?, CURDATE(), ?, ?, ?, ?, ?, ?, ?, 0, 0, ?, 'N', 'B', 'REC', 'N')",
                movimento, HORA, total, total, total, total, recebido, troco, status);
        inserir("INSERT INTO PDV_VENDA_DETALHE (ID_PRODUTO, ID_PDV_VENDA_CABECALHO, ITEM, VERSAO, QUANTIDADE, VALOR_UNITARIO, VALOR_TOTAL, "
                + "VALOR_TOTAL_ITEM, CANCELADO) VALUES (?, ?, 1, 1, ?, ?, ?, ?, 'N')", produto, id, quantidade, unitario, total, total);
        inserir("INSERT INTO PDV_TOTAL_TIPO_PAGAMENTO (ID_PDV_VENDA_CABECALHO, ID_PDV_TIPO_PAGAMENTO, DATA_VENDA, HORA_VENDA, VALOR, ESTORNO) "
                + "VALUES (?, 1, CURDATE(), ?, ?, 'N')", id, HORA, recebido);
        return id;
    }

    private void cancelar(long venda) {
        jdbc.update("UPDATE PDV_VENDA_CABECALHO SET STATUS_VENDA='C', CUPOM_CANCELADO='S', DATA_CANCELAMENTO=CURDATE(), HORA_CANCELAMENTO=?, "
                + "ID_OPERADOR_CANCELAMENTO=4, ID_GERENTE_CANCELAMENTO=1, JUSTIFICATIVA_CANCELAMENTO='Cliente desistiu da compra no caixa' WHERE ID=?",
                HORA, venda);
    }

    private void estoque(long produto, String tipo, int quantidade, Long detalhe) {
        inserir("INSERT INTO ESTOQUE_MOVIMENTO (ID_PRODUTO, TIPO_MOVIMENTO, QUANTIDADE, QUANTIDADE_ANTERIOR, QUANTIDADE_NOVA, "
                + "ID_PDV_VENDA_DETALHE, ID_OPERADOR, DATA_MOVIMENTO, HORA_MOVIMENTO) VALUES (?, ?, ?, 100, 98, ?, 4, CURDATE(), ?)",
                produto, tipo, quantidade, detalhe, HORA);
    }

    private void aguardarErp() throws InterruptedException {
        for (int i = 0; i < 60; i++) {
            int pendentes = erp.queryForObject("SELECT COUNT(*) FROM cactus_sincronizacao.registro_sincronizacao "
                    + "WHERE id_origem LIKE 'IT-ENVIO:%' AND status IN ('RECEBIDO','PUBLICADO')", Integer.class);
            if (pendentes == 0) return;
            Thread.sleep(1000);
        }
    }

    private String status(String tipo, long idLocal) {
        return jdbc.queryForObject("SELECT STATUS FROM SINCRONIZACAO_ENVIO WHERE TIPO_REGISTRO=? AND ID_LOCAL=?", String.class, tipo, idLocal);
    }

    private String erro(String tipo, long idLocal) {
        return jdbc.queryForObject("SELECT ULTIMO_ERRO FROM SINCRONIZACAO_ENVIO WHERE TIPO_REGISTRO=? AND ID_LOCAL=?", String.class, tipo, idLocal);
    }

    private long inserir(String sql, Object... valores) {
        org.springframework.jdbc.support.KeyHolder chave = new org.springframework.jdbc.support.GeneratedKeyHolder();
        jdbc.update(c -> {
            java.sql.PreparedStatement ps = c.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < valores.length; i++) ps.setObject(i + 1, valores[i]);
            return ps;
        }, chave);
        return chave.getKey().longValue();
    }
}
