package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.service.dto.ItemFilaEnvio;
import br.com.cactuserp.pdv.service.dto.SituacaoPullEntidade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Painel de sincronização com o ERP (tela {@code SincronizacaoErp.fxml}): situação do
 * pull por entidade, fila de envio e as ações do gerente — reenviar o que ficou
 * bloqueado/rejeitado e sincronizar na hora, sem esperar o agendamento.
 *
 * <p>Reenviar só volta o registro para pendente: o payload é remontado no envio a
 * partir do estado ATUAL da linha de origem, então um cadastro corrigido no meio
 * tempo (ex: produto que ganhou vínculo com o ERP) já sai certo. O ERP deduplica pelo
 * {@code idOrigem}, então reenviar algo que ele já tinha aceitado é inofensivo.
 */
@Service
public class SincronizacaoPainelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SincronizacaoPainelService.class);
    /** Status de envio que pedem ação de alguém: bloqueado (falta vínculo/config) e rejeitado (erro de formato). */
    public static final Set<String> STATUS_COM_PROBLEMA = Set.of("B", "R");
    private static final int LIMITE_LISTAGEM = 500;

    private final JdbcTemplate jdbc;
    private final SincronizacaoPullService pull;
    private final SincronizacaoEnvioService envio;

    public SincronizacaoPainelService(JdbcTemplate jdbc, SincronizacaoPullService pull, SincronizacaoEnvioService envio) {
        this.jdbc = jdbc;
        this.pull = pull;
        this.envio = envio;
    }

    /** Quantidade na fila por status (P/E/R/B/D), na ordem de exibição. */
    public Map<String, Integer> resumoFila() {
        Map<String, Integer> resumo = new LinkedHashMap<>();
        for (String status : List.of("P", "B", "R", "E", "D")) resumo.put(status, 0);
        jdbc.query("SELECT STATUS, COUNT(*) AS QTD FROM SINCRONIZACAO_ENVIO GROUP BY STATUS",
                rs -> { resumo.put(rs.getString("STATUS"), rs.getInt("QTD")); });
        return resumo;
    }

    public List<SituacaoPullEntidade> situacaoPull() {
        return jdbc.query("SELECT ENTIDADE, CURSOR_ATUAL, DATA_ULTIMA_EXECUCAO, ULTIMO_ERRO FROM SINCRONIZACAO_CURSOR ORDER BY ENTIDADE",
                (rs, i) -> new SituacaoPullEntidade(rs.getString("ENTIDADE"), rs.getString("CURSOR_ATUAL"),
                        dataHora(rs.getTimestamp("DATA_ULTIMA_EXECUCAO")), rs.getString("ULTIMO_ERRO")));
    }

    /** Fila filtrada por status (vazio = todos), mais recentes primeiro, limitada a {@value #LIMITE_LISTAGEM}. */
    public List<ItemFilaEnvio> listarFila(Set<String> status) {
        String filtro = status.isEmpty() ? "" : " WHERE STATUS IN (" + status.stream().map(s -> "?").collect(Collectors.joining(",")) + ")";
        return jdbc.query("SELECT ID, TIPO_REGISTRO, ID_ORIGEM, STATUS, TENTATIVAS, ULTIMO_ERRO, DATA_CRIACAO, DATA_ENVIO "
                        + "FROM SINCRONIZACAO_ENVIO" + filtro + " ORDER BY ID DESC LIMIT " + LIMITE_LISTAGEM,
                (rs, i) -> new ItemFilaEnvio(rs.getLong("ID"), rs.getString("TIPO_REGISTRO"), rs.getString("ID_ORIGEM"),
                        rs.getString("STATUS"), rs.getInt("TENTATIVAS"), rs.getString("ULTIMO_ERRO"),
                        dataHoraCurta(rs.getTimestamp("DATA_CRIACAO")), dataHoraCurta(rs.getTimestamp("DATA_ENVIO"))),
                status.toArray());
    }

    /** Volta um registro bloqueado/rejeitado para pendente. {@code false} se ele não estava em B/R. */
    public boolean reenviar(long idFila) {
        int alterados = jdbc.update("UPDATE SINCRONIZACAO_ENVIO SET STATUS = 'P', TENTATIVAS = 0, ULTIMO_ERRO = NULL "
                + "WHERE ID = ? AND STATUS IN ('B','R')", idFila);
        if (alterados > 0) LOGGER.info("Fila de envio: registro #{} reaberto para reenvio.", idFila);
        return alterados > 0;
    }

    /** Volta TODOS os bloqueados/rejeitados para pendente. Devolve quantos. */
    public int reenviarTodosComProblema() {
        int alterados = jdbc.update("UPDATE SINCRONIZACAO_ENVIO SET STATUS = 'P', TENTATIVAS = 0, ULTIMO_ERRO = NULL "
                + "WHERE STATUS IN ('B','R')");
        LOGGER.info("Fila de envio: {} registro(s) reabertos para reenvio.", alterados);
        return alterados;
    }

    /** Motivo pelo qual a sincronização não pode rodar (desligada/sem configuração), ou vazio se pode. */
    public Optional<String> motivoIndisponivel() {
        return envio.motivoIndisponivel();
    }

    /**
     * Pull + envio agora, fora do agendamento. Bloqueante (rede): a tela chama numa
     * thread de fundo. Os dois serviços são {@code synchronized}, então não colide com
     * uma rodada agendada em andamento — espera ela terminar.
     */
    public void sincronizarAgora() {
        motivoIndisponivel().ifPresent(motivo -> { throw new IllegalStateException(motivo); });
        pull.executar();
        envio.executar();
    }

    private static final java.time.format.DateTimeFormatter COMPLETA = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    /** Na fila (muitas colunas) a data vai curta para caber sem cortar. */
    private static final java.time.format.DateTimeFormatter CURTA = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");

    private static String dataHora(Timestamp t) {
        return t == null ? null : t.toLocalDateTime().format(COMPLETA);
    }

    private static String dataHoraCurta(Timestamp t) {
        return t == null ? null : t.toLocalDateTime().format(CURTA);
    }
}
