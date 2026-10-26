package br.com.cactuserp.pdv.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Pull ERP → PDV: traz os cadastros do {@code sincronizacao-service} e grava
 * localmente por {@link ImportadorCadastroErp}, casando pelo {@code ID_ERP}.
 *
 * <h3>Cursor (FASE6_SINCRONIZACAO_PDV.md, seção 5.1)</h3>
 * O cursor de cada entidade fica em {@code SINCRONIZACAO_CURSOR} e é o
 * {@code proximoCursor} carimbado pelo SERVIDOR, nunca o relógio do PC da
 * loja. Cada página é gravada numa transação própria e o cursor só avança
 * depois de TODAS as páginas da rodada estarem gravadas: se cair no meio, a
 * próxima rodada repete do cursor antigo (repetir é seguro, o upsert é
 * idempotente). Primeira execução (cursor vazio) = carga completa.
 *
 * <p>Ordem: formas de pagamento → operadores → clientes → produtos →
 * configuração da loja. Uma entidade com erro não impede as outras; o erro
 * fica registrado em {@code SINCRONIZACAO_CURSOR.ULTIMO_ERRO}.
 *
 * <p>Fora desta fatia: tabelas de preço (o PDV não tem tabela para elas),
 * imagens de produto e o envio PDV → ERP (fatia 2).
 *
 * <p>Desligado por padrão ({@code cactus.pdv.sync.habilitado=false}) e
 * também quando o client da loja não está configurado — o PDV continua
 * operando 100% local, como sempre.
 */
@Service
public class SincronizacaoPullService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SincronizacaoPullService.class);
    private static final int LIMITE_PAGINAS_POR_RODADA = 500;

    private final ErpSincronizacaoClient client;
    private final ImportadorCadastroErp importador;
    private final JdbcTemplate jdbc;
    private final TransactionTemplate transacao;
    private final boolean habilitado;
    private final Long lojaId;

    public SincronizacaoPullService(ErpSincronizacaoClient client, ImportadorCadastroErp importador, JdbcTemplate jdbc,
                                    TransactionTemplate transacao,
                                    @Value("${cactus.pdv.sync.habilitado:false}") boolean habilitado,
                                    @Value("${cactus.pdv.sync.loja-id:}") Long lojaId) {
        this.client = client;
        this.importador = importador;
        this.jdbc = jdbc;
        this.transacao = transacao;
        this.habilitado = habilitado;
        this.lojaId = lojaId;
    }

    @Scheduled(initialDelayString = "${cactus.pdv.sync.atraso-inicial-ms:60000}",
            fixedDelayString = "${cactus.pdv.sync.intervalo-pull-ms:300000}")
    public void executarAgendado() {
        if (!habilitado) return;
        if (!client.configurado() || lojaId == null) {
            LOGGER.warn("Sincronização habilitada mas sem URL/client/loja configurados (cactus.pdv.sync.*) — pull ignorado.");
            return;
        }
        executar();
    }

    /** Uma rodada completa de pull. Público para disparo manual e teste. {@code synchronized} pelo mesmo motivo do envio. */
    public synchronized void executar() {
        sincronizarPaginado("FORMA_PAGAMENTO", "/api/v1/sync/formas-pagamento", false, importador::importarFormaPagamento);
        sincronizarLista("OPERADOR", "/api/v1/sync/operadores", importador::importarOperador);
        sincronizarPaginado("CLIENTE", "/api/v1/sync/clientes", false, importador::importarCliente);
        sincronizarPaginado("PRODUTO", "/api/v1/sync/produtos", true, importador::importarProduto);
        sincronizarUnico("CONFIGURACAO_LOJA", "/api/v1/sync/configuracao-loja", importador::importarConfiguracaoLoja);
    }

    private void sincronizarPaginado(String entidade, String caminho, boolean exigeLoja, Consumer<JsonNode> importar) {
        String cursor = cursorAtual(entidade);
        String proximoCursor = cursor;
        int registros = 0;
        try {
            for (int pagina = 0; pagina < LIMITE_PAGINAS_POR_RODADA; pagina++) {
                Map<String, Object> parametros = new LinkedHashMap<>();
                if (exigeLoja) parametros.put("lojaId", lojaId);
                parametros.put("desde", cursor);
                parametros.put("pagina", pagina);
                JsonNode resposta = client.get(caminho, parametros);
                JsonNode conteudo = resposta.path("conteudo");
                transacao.executeWithoutResult(s -> conteudo.forEach(importar));
                registros += conteudo.size();
                if (resposta.hasNonNull("proximoCursor")) proximoCursor = resposta.get("proximoCursor").asText();
                if (resposta.path("ultimaPagina").asBoolean(true)) break;
            }
            gravarCursor(entidade, proximoCursor, null);
            if (registros > 0) LOGGER.info("Pull {}: {} registro(s) importado(s).", entidade, registros);
        } catch (RuntimeException ex) {
            LOGGER.error("Pull {} falhou — cursor mantido, a próxima rodada repete: {}", entidade, ex.getMessage(), ex);
            gravarCursor(entidade, cursor, ex.getMessage());
        }
    }

    /** Operadores: lista completa a cada rodada (o ERP não pagina nem tem cursor aqui). */
    private void sincronizarLista(String entidade, String caminho, Consumer<JsonNode> importar) {
        try {
            JsonNode lista = client.get(caminho, Map.of("lojaId", lojaId));
            transacao.executeWithoutResult(s -> lista.forEach(importar));
            gravarCursor(entidade, null, null);
        } catch (RuntimeException ex) {
            LOGGER.error("Pull {} falhou: {}", entidade, ex.getMessage(), ex);
            gravarCursor(entidade, null, ex.getMessage());
        }
    }

    private void sincronizarUnico(String entidade, String caminho, Consumer<JsonNode> importar) {
        try {
            JsonNode objeto = client.get(caminho, Map.of("lojaId", lojaId));
            transacao.executeWithoutResult(s -> importar.accept(objeto));
            gravarCursor(entidade, null, null);
        } catch (RuntimeException ex) {
            LOGGER.error("Pull {} falhou: {}", entidade, ex.getMessage(), ex);
            gravarCursor(entidade, null, ex.getMessage());
        }
    }

    private String cursorAtual(String entidade) {
        List<String> cursores = jdbc.queryForList("SELECT CURSOR_ATUAL FROM SINCRONIZACAO_CURSOR WHERE ENTIDADE = ?", String.class, entidade);
        return cursores.isEmpty() ? null : cursores.get(0);
    }

    private void gravarCursor(String entidade, String cursor, String erro) {
        String erroTruncado = erro == null ? null : erro.length() <= 500 ? erro : erro.substring(0, 500);
        jdbc.update("INSERT INTO SINCRONIZACAO_CURSOR (ENTIDADE, CURSOR_ATUAL, DATA_ULTIMA_EXECUCAO, ULTIMO_ERRO) VALUES (?,?,?,?) "
                        + "ON DUPLICATE KEY UPDATE CURSOR_ATUAL = VALUES(CURSOR_ATUAL), DATA_ULTIMA_EXECUCAO = VALUES(DATA_ULTIMA_EXECUCAO), "
                        + "ULTIMO_ERRO = VALUES(ULTIMO_ERRO)",
                entidade, cursor, Timestamp.valueOf(LocalDateTime.now()), erroTruncado);
    }
}
