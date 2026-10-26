package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.PdvApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pull real contra o ERP local (sincronizacao-service + cadastro-base + seguranca
 * no ar), gravando no MySQL local do PDV numa transação REVERTIDA ao fim — nada
 * fica no banco. Fora do {@code mvn test} padrão (sufixo IT). Precisa de um client
 * de loja provisionado no ERP: {@code CACTUS_SYNC_LOJA_ID},
 * {@code CACTUS_SYNC_CLIENT_ID}, {@code CACTUS_SYNC_CLIENT_SECRET} e
 * {@code DB_PASSWORD} no ambiente.
 *
 * <p>Compara com o banco do ERP direto (mesmo MySQL), lendo os schemas
 * {@code cactus_cadastro}/{@code cactus_seguranca}.
 */
@SpringBootTest(classes = PdvApplication.class, properties = {
        "cactus.pdv.sync.produto.subgrupo-padrao-id=1",
        "cactus.pdv.sync.produto.tipo-padrao-id=1",
        "cactus.pdv.sync.produto.grupo-tributario-padrao-id=1"})
@Transactional
class SincronizacaoPullServiceIT {

    @Autowired private SincronizacaoPullService service;
    @Autowired private JdbcTemplate jdbc;

    @Test
    void cargaCompletaVinculaTudoPeloIdDoErpERepetirNaoDuplica() {
        service.executar();

        assertThat(erros()).as("erros por entidade").isEmpty();
        int produtosErp = conta("SELECT COUNT(*) FROM cactus_cadastro.produto");
        int formasErp = conta("SELECT COUNT(*) FROM cactus_cadastro.forma_pagamento WHERE situacao='A'");
        int clientesErp = conta("SELECT COUNT(*) FROM cactus_cadastro.cliente c JOIN cactus_cadastro.pessoa p ON p.id=c.pessoa_id WHERE p.situacao='A'");
        assertThat(conta("SELECT COUNT(*) FROM PRODUTO WHERE ID_ERP IS NOT NULL")).isEqualTo(produtosErp);
        assertThat(conta("SELECT COUNT(*) FROM PDV_TIPO_PAGAMENTO WHERE ID_ERP IS NOT NULL")).isGreaterThanOrEqualTo(formasErp);
        assertThat(conta("SELECT COUNT(*) FROM CLIENTE WHERE ID_ERP IS NOT NULL")).isGreaterThanOrEqualTo(clientesErp);

        // Campo a campo de um produto com faixa de preço no ERP
        Map<String, Object> erp = jdbc.queryForMap("SELECT p.id, p.nome, p.valor_venda, p.gtin, "
                + "(SELECT COUNT(*) FROM cactus_cadastro.produto_faixa_preco f WHERE f.produto_id=p.id) faixas "
                + "FROM cactus_cadastro.produto p ORDER BY faixas DESC, p.id LIMIT 1");
        Map<String, Object> pdv = jdbc.queryForMap("SELECT ID, NOME, VALOR_VENDA, GTIN FROM PRODUTO WHERE ID_ERP = ?", erp.get("id"));
        assertThat(pdv.get("NOME")).isEqualTo(erp.get("nome"));
        assertThat(pdv.get("GTIN")).isEqualTo(erp.get("gtin"));
        assertThat(((java.math.BigDecimal) pdv.get("VALOR_VENDA"))).isEqualByComparingTo((java.math.BigDecimal) erp.get("valor_venda"));
        assertThat(conta("SELECT COUNT(*) FROM PRODUTO_FAIXA_PRECO WHERE ID_PRODUTO = " + pdv.get("ID")))
                .isEqualTo(((Number) erp.get("faixas")).intValue());

        // Operador do ERP (usuário com papel GERENTE na loja) vira PDV_OPERADOR + COLABORADOR nível G, senha BCrypt do ERP
        Map<String, Object> usuario = jdbc.queryForMap("SELECT id, senha_hash FROM cactus_seguranca.usuario WHERE login='t_sync_admin'");
        Map<String, Object> operador = jdbc.queryForMap("SELECT o.LOGIN, o.SENHA, c.NIVEL_AUTORIZACAO, c.ID_ERP FROM PDV_OPERADOR o "
                + "JOIN COLABORADOR c ON c.ID = o.ID_COLABORADOR WHERE o.ID_ERP = ?", usuario.get("id"));
        assertThat(operador.get("LOGIN")).isEqualTo("t_sync_admin");
        assertThat(operador.get("SENHA")).isEqualTo(usuario.get("senha_hash"));
        assertThat(operador.get("NIVEL_AUTORIZACAO")).isEqualTo("G");
        assertThat(((Number) operador.get("ID_ERP")).longValue()).isEqualTo(2L);

        List<Map<String, Object>> cursores = jdbc.queryForList("SELECT ENTIDADE, CURSOR_ATUAL FROM SINCRONIZACAO_CURSOR");
        assertThat(cursores).extracting(c -> c.get("ENTIDADE"))
                .contains("PRODUTO", "CLIENTE", "FORMA_PAGAMENTO", "OPERADOR", "CONFIGURACAO_LOJA");

        // Segunda rodada: com cursor, nada novo; nenhuma linha duplicada
        int produtos = conta("SELECT COUNT(*) FROM PRODUTO");
        int clientes = conta("SELECT COUNT(*) FROM CLIENTE");
        int operadores = conta("SELECT COUNT(*) FROM PDV_OPERADOR");
        int faixas = conta("SELECT COUNT(*) FROM PRODUTO_FAIXA_PRECO");
        service.executar();
        assertThat(erros()).isEmpty();
        assertThat(conta("SELECT COUNT(*) FROM PRODUTO")).isEqualTo(produtos);
        assertThat(conta("SELECT COUNT(*) FROM CLIENTE")).isEqualTo(clientes);
        assertThat(conta("SELECT COUNT(*) FROM PDV_OPERADOR")).isEqualTo(operadores);
        assertThat(conta("SELECT COUNT(*) FROM PRODUTO_FAIXA_PRECO")).isEqualTo(faixas);
    }

    private List<String> erros() {
        return jdbc.queryForList("SELECT CONCAT(ENTIDADE, ': ', ULTIMO_ERRO) FROM SINCRONIZACAO_CURSOR WHERE ULTIMO_ERRO IS NOT NULL", String.class);
    }

    private int conta(String sql) {
        return jdbc.queryForObject(sql, Integer.class);
    }
}
