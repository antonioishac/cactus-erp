package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.service.MontadorPayloadErp.EnvioBloqueadoException;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Envio PDV → ERP ({@code POST /api/v1/sync/lotes}) por uma fila local
 * ({@code SINCRONIZACAO_ENVIO}, padrão outbox).
 *
 * <h3>Coleta</h3>
 * Um coletor varre as tabelas de operação a partir de
 * {@code cactus.pdv.sync.envio.a-partir-de} e enfileira cada linha ainda não
 * enfileirada (chave única tipo + id local). O código que registra venda,
 * sangria etc. não muda: o envio é um leitor das tabelas, e o que aconteceu
 * com o PDV desligado da rede é enviado depois.
 * <ul>
 *   <li>Venda: só a que foi finalizada um dia ({@code F}/{@code P}, ou {@code C}
 *       com data de cancelamento). Cupom aberto e abortado nunca existiu como
 *       venda e não vai.</li>
 *   <li>Cancelamento de venda: só quando a venda já foi ENVIADA como fechada.
 *       Se foi cancelada antes de sair, a própria venda vai como cancelada.</li>
 *   <li>Devolução: só depois da venda enfileirada; se a venda saiu
 *       cancelada, a devolução é dispensada (o ERP não aceita devolução de
 *       venda cancelada e o estoque já volta pelo movimento).</li>
 * </ul>
 *
 * <h3>Envio</h3>
 * Lotes de até {@code cactus.pdv.sync.envio.tamanho-lote} por tipo, na ordem
 * de dependência (abertura antes de venda, venda antes de devolução...). O
 * {@code idOrigem} leva o dispositivo ({@code CAIXA01:V:43}) para dois PDVs da
 * mesma loja não colidirem no ERP. Resultado por item: {@code ACEITO} →
 * enviado; {@code REJEITADO} → fica rejeitado com o motivo (erro de formato,
 * precisa de correção). Falha de rede → continua pendente e repete. Reenviar
 * é seguro: o ERP deduplica pela chave loja+dispositivo+tipo+idOrigem.
 *
 * <p>Status: {@code P} pendente | {@code E} enviado | {@code R} rejeitado |
 * {@code B} bloqueado (falta vínculo {@code ID_ERP}/configuração; tentado de
 * novo a cada rodada) | {@code D} dispensado.
 */
@Service
public class SincronizacaoEnvioService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SincronizacaoEnvioService.class);

    /** Tipo de registro do ERP → prefixo do idOrigem, na ordem de envio. */
    static final Map<String, String> PREFIXOS = new LinkedHashMap<>();

    static {
        PREFIXOS.put("ABERTURA_CAIXA", "AB");
        PREFIXOS.put("SUPRIMENTO", "SU");
        PREFIXOS.put("VENDA_PDV", "V");
        PREFIXOS.put("DEVOLUCAO_PDV", "DV");
        PREFIXOS.put("CANCELAMENTO_VENDA_PDV", "CV");
        PREFIXOS.put("SANGRIA", "SA");
        PREFIXOS.put("FIADO_LANCAMENTO", "FI");
        PREFIXOS.put("ESTOQUE_MOVIMENTO", "EM");
        PREFIXOS.put("CONFERENCIA_CAIXA", "CC");
    }

    private final ErpSincronizacaoClient client;
    private final MontadorPayloadErp montador;
    private final JdbcTemplate jdbc;
    private final boolean habilitado;
    private final Long lojaId;
    private final String dispositivoId;
    private final LocalDate aPartirDe;
    private final int tamanhoLote;

    public SincronizacaoEnvioService(ErpSincronizacaoClient client, MontadorPayloadErp montador, JdbcTemplate jdbc,
                                     DispositivoInfoService dispositivoInfo,
                                     @Value("${cactus.pdv.sync.habilitado:false}") boolean habilitado,
                                     @Value("${cactus.pdv.sync.loja-id:}") Long lojaId,
                                     @Value("${cactus.pdv.sync.dispositivo-id:}") String dispositivoId,
                                     @Value("${cactus.pdv.sync.envio.a-partir-de:}") String aPartirDe,
                                     @Value("${cactus.pdv.sync.envio.tamanho-lote:100}") int tamanhoLote) {
        this.client = client;
        this.montador = montador;
        this.jdbc = jdbc;
        this.habilitado = habilitado;
        this.lojaId = lojaId;
        String dispositivo = dispositivoId == null || dispositivoId.isBlank() ? dispositivoInfo.getHostname() : dispositivoId;
        this.dispositivoId = dispositivo.length() <= 30 ? dispositivo : dispositivo.substring(0, 30);
        this.aPartirDe = aPartirDe == null || aPartirDe.isBlank() ? null : LocalDate.parse(aPartirDe);
        this.tamanhoLote = tamanhoLote;
    }

    @Scheduled(initialDelayString = "${cactus.pdv.sync.atraso-inicial-ms:60000}",
            fixedDelayString = "${cactus.pdv.sync.intervalo-envio-ms:60000}")
    public void executarAgendado() {
        if (!habilitado) return;
        motivoIndisponivel().ifPresentOrElse(
                motivo -> LOGGER.warn("Envio ao ERP habilitado mas não pode rodar: {}", motivo),
                this::executar);
    }

    /** Por que a sincronização não pode rodar agora, ou vazio se pode. */
    public java.util.Optional<String> motivoIndisponivel() {
        if (!habilitado) return java.util.Optional.of("Sincronização com o ERP desligada (cactus.pdv.sync.habilitado=false).");
        if (!client.configurado()) return java.util.Optional.of("URL do ERP ou client da loja não configurados (cactus.pdv.sync.*).");
        if (lojaId == null) return java.util.Optional.of("Loja não configurada (cactus.pdv.sync.loja-id).");
        if (aPartirDe == null) return java.util.Optional.of("Data inicial do envio não configurada (cactus.pdv.sync.envio.a-partir-de).");
        return java.util.Optional.empty();
    }

    /**
     * Uma rodada: coleta e envia. {@code synchronized}: a tela de sincronização pode
     * disparar na mão enquanto o agendamento roda — a segunda espera a primeira, em vez
     * de as duas pegarem os mesmos pendentes.
     */
    public synchronized void executar() {
        coletar();
        enviar();
    }

    // ================================================================ coleta

    void coletar() {
        Date desde = Date.valueOf(aPartirDe);
        enfileirar("ABERTURA_CAIXA", "SELECT ID FROM PDV_MOVIMENTO WHERE DATA_ABERTURA >= ?", desde);
        enfileirar("SUPRIMENTO", "SELECT ID FROM PDV_SUPRIMENTO WHERE DATA_SUPRIMENTO >= ?", desde);
        enfileirar("SANGRIA", "SELECT ID FROM PDV_SANGRIA WHERE DATA_SANGRIA >= ?", desde);
        enfileirar("CONFERENCIA_CAIXA", "SELECT ID FROM PDV_CONFERENCIA_CAIXA WHERE DATA_CONFERENCIA >= ?", desde);
        enfileirar("FIADO_LANCAMENTO", "SELECT ID FROM CLIENTE_FIADO_LANCAMENTO WHERE DATA_LANCAMENTO >= ?", desde);
        enfileirar("ESTOQUE_MOVIMENTO", "SELECT ID FROM ESTOQUE_MOVIMENTO WHERE DATA_MOVIMENTO >= ?", desde);
        enfileirar("VENDA_PDV", "SELECT ID FROM PDV_VENDA_CABECALHO WHERE DATA_VENDA >= ? "
                + "AND (STATUS_VENDA IN ('F','P') OR (STATUS_VENDA = 'C' AND DATA_CANCELAMENTO IS NOT NULL))", desde);
        enfileirar("DEVOLUCAO_PDV", "SELECT d.ID FROM PDV_DEVOLUCAO d WHERE d.DATA_DEVOLUCAO >= ? AND EXISTS (SELECT 1 FROM "
                + "SINCRONIZACAO_ENVIO e WHERE e.TIPO_REGISTRO = 'VENDA_PDV' AND e.ID_LOCAL = d.ID_PDV_VENDA_CABECALHO)", desde);
        // ID_LOCAL do cancelamento = id da venda (uma venda só se cancela uma vez)
        enfileirar("CANCELAMENTO_VENDA_PDV", "SELECT v.ID FROM PDV_VENDA_CABECALHO v JOIN SINCRONIZACAO_ENVIO e "
                + "ON e.TIPO_REGISTRO = 'VENDA_PDV' AND e.ID_LOCAL = v.ID AND e.STATUS = 'E' AND e.SITUACAO_ENVIADA = 'F' "
                + "WHERE v.STATUS_VENDA = 'C' AND v.DATA_CANCELAMENTO IS NOT NULL AND v.DATA_VENDA >= ?", desde);
    }

    private void enfileirar(String tipo, String selectIds, Date desde) {
        String prefixo = dispositivoId + ":" + PREFIXOS.get(tipo) + ":";
        int novos = jdbc.update("INSERT INTO SINCRONIZACAO_ENVIO (TIPO_REGISTRO, ID_LOCAL, ID_ORIGEM, STATUS, TENTATIVAS, DATA_CRIACAO) "
                        + "SELECT ?, o.ID, CONCAT(?, o.ID), 'P', 0, ? FROM (" + selectIds + ") o "
                        + "WHERE NOT EXISTS (SELECT 1 FROM SINCRONIZACAO_ENVIO x WHERE x.TIPO_REGISTRO = ? AND x.ID_LOCAL = o.ID)",
                tipo, prefixo, Timestamp.valueOf(LocalDateTime.now()), desde, tipo);
        if (novos > 0) LOGGER.info("Envio ao ERP: {} registro(s) {} enfileirado(s).", novos, tipo);
    }

    // ================================================================ envio

    void enviar() {
        for (String tipo : PREFIXOS.keySet()) {
            List<Map<String, Object>> fila = jdbc.queryForList("SELECT ID, ID_LOCAL, ID_ORIGEM FROM SINCRONIZACAO_ENVIO "
                    + "WHERE TIPO_REGISTRO = ? AND STATUS IN ('P','B') ORDER BY ID LIMIT ?", tipo, tamanhoLote);
            if (!fila.isEmpty()) enviarLote(tipo, fila);
        }
    }

    private void enviarLote(String tipo, List<Map<String, Object>> fila) {
        List<Map<String, Object>> registros = new ArrayList<>();
        Map<String, Long> filaPorOrigem = new LinkedHashMap<>();
        Map<String, String> situacaoPorOrigem = new LinkedHashMap<>();
        for (Map<String, Object> linha : fila) {
            long idFila = ((Number) linha.get("ID")).longValue();
            long idLocal = ((Number) linha.get("ID_LOCAL")).longValue();
            String idOrigem = (String) linha.get("ID_ORIGEM");
            try {
                Map<String, Object> payload = montar(tipo, idLocal);
                if (payload == null) {
                    marcar(idFila, "D", "Dispensado: a venda foi enviada como cancelada.", null);
                    continue;
                }
                registros.add(Map.of("idOrigem", idOrigem, "payload", payload));
                filaPorOrigem.put(idOrigem, idFila);
                if ("VENDA_PDV".equals(tipo)) situacaoPorOrigem.put(idOrigem, (String) payload.get("situacao"));
            } catch (EnvioBloqueadoException ex) {
                marcar(idFila, "B", ex.getMessage(), null);
            }
        }
        if (registros.isEmpty()) return;

        Map<String, Object> lote = new LinkedHashMap<>();
        lote.put("lojaId", lojaId);
        lote.put("dispositivoId", dispositivoId);
        lote.put("tipoRegistro", tipo);
        lote.put("registros", registros);
        JsonNode resposta;
        try {
            resposta = client.post("/api/v1/sync/lotes", lote);
        } catch (RuntimeException ex) {
            LOGGER.warn("Envio de {} ao ERP falhou, fica pendente para a próxima rodada: {}", tipo, ex.getMessage());
            filaPorOrigem.values().forEach(id -> jdbc.update("UPDATE SINCRONIZACAO_ENVIO SET STATUS = 'P', TENTATIVAS = TENTATIVAS + 1, "
                    + "ULTIMO_ERRO = ? WHERE ID = ?", truncar(ex.getMessage()), id));
            return;
        }
        int aceitos = 0;
        for (JsonNode r : resposta.path("resultados")) {
            Long idFila = filaPorOrigem.get(r.path("idOrigem").asText());
            if (idFila == null) continue;
            if ("ACEITO".equals(r.path("situacao").asText())) {
                marcar(idFila, "E", null, situacaoPorOrigem.get(r.path("idOrigem").asText()));
                aceitos++;
            } else {
                marcar(idFila, "R", r.path("motivo").asText("Rejeitado pelo ERP"), null);
            }
        }
        LOGGER.info("Envio ao ERP: lote de {} — {} aceito(s) de {}.", tipo, aceitos, registros.size());
    }

    /** {@code null} = dispensado (devolução de venda que saiu cancelada). */
    private Map<String, Object> montar(String tipo, long idLocal) {
        return switch (tipo) {
            case "ABERTURA_CAIXA" -> montador.aberturaCaixa(idLocal);
            case "SUPRIMENTO" -> montador.suprimento(idLocal);
            case "SANGRIA" -> montador.sangria(idLocal);
            case "CONFERENCIA_CAIXA" -> montador.conferenciaCaixa(idLocal);
            case "FIADO_LANCAMENTO" -> montador.fiadoLancamento(idLocal);
            case "ESTOQUE_MOVIMENTO" -> montador.estoqueMovimento(idLocal);
            case "VENDA_PDV" -> montador.venda(idLocal);
            case "DEVOLUCAO_PDV" -> montarDevolucao(idLocal);
            case "CANCELAMENTO_VENDA_PDV" -> montador.cancelamentoVenda(idLocal, idOrigemVenda(idLocal));
            default -> throw new IllegalArgumentException("Tipo sem montador: " + tipo);
        };
    }

    private Map<String, Object> montarDevolucao(long devolucaoId) {
        Long vendaId = jdbc.queryForObject("SELECT ID_PDV_VENDA_CABECALHO FROM PDV_DEVOLUCAO WHERE ID = ?", Long.class, devolucaoId);
        List<Map<String, Object>> venda = jdbc.queryForList("SELECT STATUS, SITUACAO_ENVIADA FROM SINCRONIZACAO_ENVIO "
                + "WHERE TIPO_REGISTRO = 'VENDA_PDV' AND ID_LOCAL = ?", vendaId);
        if (venda.isEmpty() || !"E".equals(venda.get(0).get("STATUS"))) {
            throw new EnvioBloqueadoException("Aguardando a venda #" + vendaId + " ser aceita pelo ERP.");
        }
        if ("C".equals(venda.get(0).get("SITUACAO_ENVIADA"))) return null;
        return montador.devolucao(devolucaoId, idOrigemVenda(vendaId));
    }

    private String idOrigemVenda(long vendaId) {
        return dispositivoId + ":" + PREFIXOS.get("VENDA_PDV") + ":" + vendaId;
    }

    private void marcar(long idFila, String status, String erro, String situacaoEnviada) {
        jdbc.update("UPDATE SINCRONIZACAO_ENVIO SET STATUS = ?, ULTIMO_ERRO = ?, SITUACAO_ENVIADA = COALESCE(?, SITUACAO_ENVIADA), "
                        + "TENTATIVAS = TENTATIVAS + 1, DATA_ENVIO = CASE WHEN ? = 'E' THEN ? ELSE DATA_ENVIO END WHERE ID = ?",
                status, truncar(erro), situacaoEnviada, status, Timestamp.valueOf(LocalDateTime.now()), idFila);
    }

    private static String truncar(String texto) {
        return texto == null ? null : texto.length() <= 500 ? texto : texto.substring(0, 500);
    }
}
