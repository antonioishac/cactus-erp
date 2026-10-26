package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.StageManager;
import br.com.cactuserp.pdv.service.SincronizacaoPainelService;
import br.com.cactuserp.pdv.service.dto.ItemFilaEnvio;
import br.com.cactuserp.pdv.service.dto.SituacaoPullEntidade;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Controller da tela {@code SincronizacaoErp.fxml} (F12 → Sincronização, gerente/
 * supervisor). Só apresentação: a regra fica em {@link SincronizacaoPainelService}.
 *
 * <p>100% por teclado, como o resto do PDV: F5 sincroniza agora, F6 reenvia o registro
 * selecionado, F7 reenvia todos os bloqueados/rejeitados, ESC volta. A sincronização
 * fala com a rede, então roda num {@link Task} em thread de fundo — a tela continua
 * respondendo e o botão fica desabilitado até terminar.
 */
@Component
public class SincronizacaoErpController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SincronizacaoErpController.class);

    /** Filtro da fila: rótulo → status (vazio = todos). Padrão: o que pede ação. */
    private enum Filtro {
        COM_PROBLEMA("Bloqueados e rejeitados", SincronizacaoPainelService.STATUS_COM_PROBLEMA),
        PENDENTES("Pendentes", Set.of("P")),
        ENVIADOS("Enviados", Set.of("E")),
        TODOS("Todos", Set.of());

        final String rotulo;
        final Set<String> status;

        Filtro(String rotulo, Set<String> status) {
            this.rotulo = rotulo;
            this.status = status;
        }

        @Override
        public String toString() { return rotulo; }
    }

    @FXML private AnchorPane root;
    @FXML private Label lblDisponibilidade;
    @FXML private Label lblProgresso;
    @FXML private Button btnSincronizar;
    @FXML private TableView<SituacaoPullEntidade> tabelaPull;
    @FXML private TableColumn<SituacaoPullEntidade, String> colPullEntidade;
    @FXML private TableColumn<SituacaoPullEntidade, String> colPullExecucao;
    @FXML private TableColumn<SituacaoPullEntidade, String> colPullSituacao;
    @FXML private Label lblPullErro;
    @FXML private Label lblResumoFila;
    @FXML private ComboBox<Filtro> cbFiltro;
    @FXML private TableView<ItemFilaEnvio> tabelaFila;
    @FXML private TableColumn<ItemFilaEnvio, String> colTipo;
    @FXML private TableColumn<ItemFilaEnvio, String> colOrigem;
    @FXML private TableColumn<ItemFilaEnvio, String> colStatus;
    @FXML private TableColumn<ItemFilaEnvio, String> colTentativas;
    @FXML private TableColumn<ItemFilaEnvio, String> colCriacao;
    @FXML private TableColumn<ItemFilaEnvio, String> colEnvio;
    @FXML private TextArea taMotivo;
    @FXML private Button btnReenviar;
    @FXML private Button btnReenviarTodos;

    private final StageManager stageManager;
    private final SincronizacaoPainelService painel;

    public SincronizacaoErpController(StageManager stageManager, SincronizacaoPainelService painel) {
        this.stageManager = stageManager;
        this.painel = painel;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colPullEntidade.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().entidadeDescricao()));
        colPullExecucao.setCellValueFactory(c -> new SimpleStringProperty(valor(c.getValue().ultimaExecucao())));
        colPullSituacao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().comErro() ? "ERRO" : "OK"));
        tabelaPull.getSelectionModel().selectedItemProperty().addListener((obs, antigo, item) ->
                lblPullErro.setText(item != null && item.comErro() ? item.ultimoErro() : ""));

        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().tipoDescricao()));
        colOrigem.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().idOrigem()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().statusDescricao()));
        colTentativas.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().tentativas())));
        colCriacao.setCellValueFactory(c -> new SimpleStringProperty(valor(c.getValue().dataCriacao())));
        colEnvio.setCellValueFactory(c -> new SimpleStringProperty(valor(c.getValue().dataEnvio())));
        tabelaFila.getSelectionModel().selectedItemProperty().addListener((obs, antigo, item) -> {
            taMotivo.setText(item != null && item.ultimoErro() != null ? item.ultimoErro() : "");
            btnReenviar.setDisable(item == null || !SincronizacaoPainelService.STATUS_COM_PROBLEMA.contains(item.status()));
        });

        // Colunas dividem a largura da tabela (sem sobra à direita nem texto cortado nas proporções da tela)
        tabelaPull.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabelaFila.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        cbFiltro.setItems(FXCollections.observableArrayList(Filtro.values()));
        cbFiltro.getSelectionModel().select(Filtro.COM_PROBLEMA);
        cbFiltro.valueProperty().addListener((obs, antigo, novo) -> carregarFila());

        Platform.runLater(this::configurarAtalhos);
    }

    /** Chamado pelo {@link StageManager} a cada abertura: dados sempre atualizados. */
    public void prepararParaAbertura() {
        Optional<String> indisponivel = painel.motivoIndisponivel();
        lblDisponibilidade.setText(indisponivel.orElse("Sincronização ativa — roda sozinha a cada poucos minutos."));
        btnSincronizar.setDisable(indisponivel.isPresent());
        lblProgresso.setText("");
        recarregar();
        Platform.runLater(() -> tabelaFila.requestFocus());
    }

    private void configurarAtalhos() {
        Scene cena = root.getScene();
        if (cena == null) return;
        cena.setOnKeyPressed(ev -> {
            KeyCode tecla = ev.getCode();
            if (tecla == KeyCode.ESCAPE) {
                stageManager.fecharSincronizacaoErp();
            } else if (tecla == KeyCode.F5) {
                handleSincronizar();
            } else if (tecla == KeyCode.F6) {
                handleReenviarSelecionado();
            } else if (tecla == KeyCode.F7) {
                handleReenviarTodos();
            } else {
                return;
            }
            ev.consume();
        });
    }

    @FXML
    private void handleSincronizar() {
        if (btnSincronizar.isDisable()) return;
        btnSincronizar.setDisable(true);
        lblProgresso.setText("Sincronizando com o ERP...");
        Task<Void> tarefa = new Task<>() {
            @Override
            protected Void call() {
                painel.sincronizarAgora();
                return null;
            }
        };
        tarefa.setOnSucceeded(e -> concluirSincronizacao("Sincronização concluída."));
        tarefa.setOnFailed(e -> {
            Throwable erro = tarefa.getException();
            LOGGER.error("Sincronização manual com o ERP falhou", erro);
            concluirSincronizacao("Falhou: " + (erro != null ? erro.getMessage() : "erro desconhecido"));
        });
        Thread thread = new Thread(tarefa, "sincronizacao-manual-erp");
        thread.setDaemon(true);
        thread.start();
    }

    private void concluirSincronizacao(String mensagem) {
        lblProgresso.setText(mensagem);
        btnSincronizar.setDisable(painel.motivoIndisponivel().isPresent());
        recarregar();
    }

    @FXML
    private void handleReenviarSelecionado() {
        ItemFilaEnvio item = tabelaFila.getSelectionModel().getSelectedItem();
        if (item == null) return;
        if (!painel.reenviar(item.id())) {
            alerta(Alert.AlertType.INFORMATION, "Só registros bloqueados ou rejeitados voltam para a fila.");
            return;
        }
        lblProgresso.setText("Registro " + item.idOrigem() + " volta a ser enviado na próxima rodada (ou F5).");
        recarregar();
    }

    @FXML
    private void handleReenviarTodos() {
        int quantidade = painel.reenviarTodosComProblema();
        lblProgresso.setText(quantidade == 0 ? "Nenhum registro com problema."
                : quantidade + " registro(s) voltam a ser enviados na próxima rodada (ou F5).");
        recarregar();
    }

    private void recarregar() {
        tabelaPull.setItems(FXCollections.observableArrayList(painel.situacaoPull()));
        Map<String, Integer> resumo = painel.resumoFila();
        lblResumoFila.setText(resumo.get("P") + " pendente(s) · " + resumo.get("B") + " bloqueado(s) · "
                + resumo.get("R") + " rejeitado(s) · " + resumo.get("E") + " enviado(s)");
        btnReenviarTodos.setDisable(resumo.get("B") + resumo.get("R") == 0);
        carregarFila();
    }

    private void carregarFila() {
        Filtro filtro = cbFiltro.getValue() != null ? cbFiltro.getValue() : Filtro.COM_PROBLEMA;
        tabelaFila.setItems(FXCollections.observableArrayList(painel.listarFila(filtro.status)));
        taMotivo.clear();
        btnReenviar.setDisable(true);
    }

    private void alerta(Alert.AlertType tipo, String mensagem) {
        Alert alert = new Alert(tipo, mensagem);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private static String valor(String texto) {
        return texto != null ? texto : "—";
    }
}
