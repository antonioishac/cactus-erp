package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.config.StageManager;
import br.com.cactuserp.pdv.entity.ContaAberta;
import br.com.cactuserp.pdv.entity.Mesa;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.entity.StatusContaAberta;
import br.com.cactuserp.pdv.service.ContaAbertaService;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller do Salão de Mesas.
 *
 * <p>FIX v123: usa addEventFilter no ROOT (não na Scene) — assim o
 * filter "viaja" com o nó quando a Scene troca de root, evitando
 * filters fantasmas acumulados.
 */
@Component
public class SalaoMesasController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalaoMesasController.class);
    private static final NumberFormat MOEDA =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    private static final Color COR_LIVRE = Color.web("#2d7a3e");
    private static final Color COR_OCUPADA = Color.web("#c9901e");
    private static final Color COR_ATENDIMENTO = Color.web("#2969c9");

    private final StageManager stageManager;
    private final SessaoPdv sessaoPdv;
    private final ContaAbertaService contaService;

    @FXML private AnchorPane root;
    @FXML private Label lblOperador;
    @FXML private TextField tfBusca;
    @FXML private VBox containerSetores;

    private final List<StackPane> cardsOrdem = new ArrayList<>();
    private final Map<StackPane, Mesa> mesaPorCard = new LinkedHashMap<>();
    private int indiceFocado = -1;

    // ===== Atalhos globais (filter no ROOT, não na scene) =====
    private EventHandler<KeyEvent> filterTeclas;

    public SalaoMesasController(StageManager stageManager,
                                SessaoPdv sessaoPdv,
                                ContaAbertaService contaService) {
        this.stageManager = stageManager;
        this.sessaoPdv = sessaoPdv;
        this.contaService = contaService;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tfBusca.setOnAction(e -> buscarPorNumero());

        tfBusca.textProperty().addListener((o, antigo, novo) -> {
            if (novo != null && !novo.matches("\\d*")) {
                tfBusca.setText(antigo);
            }
        });

        tfBusca.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.DOWN && !cardsOrdem.isEmpty()) {
                cardsOrdem.get(0).requestFocus();
                ev.consume();
            }
        });
    }

    public void carregar() {
        LOGGER.info("=== SALAO.carregar() chamado");
        cardsOrdem.clear();
        mesaPorCard.clear();
        containerSetores.getChildren().clear();
        indiceFocado = -1;
        tfBusca.clear();

        PdvOperador op = sessaoPdv.getOperadorAtual();
        if (op != null && op.getColaborador() != null) {
            lblOperador.setText("Operador: " + op.getColaborador().getNome());
        }

        List<Mesa> mesas = contaService.listarMesasAtivas();
        if (mesas.isEmpty()) {
            Label vazio = new Label("Nenhuma mesa cadastrada. "
                    + "Cadastre mesas no sistema back-office.");
            vazio.getStyleClass().add("sm-vazio");
            containerSetores.getChildren().add(vazio);
            return;
        }

        Map<Long, ContaAberta> contaPorMesaId = new LinkedHashMap<>();
        for (ContaAberta c : contaService.listarContasAtivasPorTipo(
                br.com.cactuserp.pdv.entity.TipoContaAberta.M)) {
            if (c.getMesa() != null) {
                contaPorMesaId.put(c.getMesa().getId(), c);
            }
        }

        Map<String, List<Mesa>> porSetor = new LinkedHashMap<>();
        for (Mesa m : mesas) {
            String setor = m.getSetor() != null ? m.getSetor() : "—";
            porSetor.computeIfAbsent(setor, k -> new ArrayList<>()).add(m);
        }

        for (Map.Entry<String, List<Mesa>> e : porSetor.entrySet()) {
            VBox bloco = montarBlocoSetor(e.getKey(), e.getValue(), contaPorMesaId);
            containerSetores.getChildren().add(bloco);
        }

        Platform.runLater(tfBusca::requestFocus);

        // Registra atalhos (no ROOT, não na Scene)
        registrarAtalhosGlobais();
    }

    // ====================================================================
    //  Atalhos globais — filter no ROOT
    // ====================================================================

    private void registrarAtalhosGlobais() {
        LOGGER.info("=== SALAO.registrarAtalhosGlobais() root={} filterTeclas={}",
                System.identityHashCode(root),
                filterTeclas != null ? "PRESENT" : "null");

        if (filterTeclas != null) {
            LOGGER.info("SALAO: removendo filter anterior do root");
            root.removeEventFilter(KeyEvent.KEY_PRESSED, filterTeclas);
        }

        filterTeclas = ev -> {
            LOGGER.info("[SALAO FILTER] tecla={} target={}",
                    ev.getCode(),
                    ev.getTarget() != null ? ev.getTarget().getClass().getSimpleName() : "?");
            if (ev.getCode() == KeyCode.ESCAPE) {
                LOGGER.info("[SALAO FILTER] ESC detectado, fechando salão");
                desregistrarAtalhosGlobais();
                stageManager.fecharSalaoMesas();
                ev.consume();
            } else if (ev.getCode() == KeyCode.F5) {
                recarregar();
                ev.consume();
            }
        };

        root.addEventFilter(KeyEvent.KEY_PRESSED, filterTeclas);
        LOGGER.info("SALAO: filter REGISTRADO no root {}", System.identityHashCode(root));
    }

    private void desregistrarAtalhosGlobais() {
        if (filterTeclas != null) {
            LOGGER.info("SALAO: desregistrando filter do root");
            root.removeEventFilter(KeyEvent.KEY_PRESSED, filterTeclas);
            filterTeclas = null;
        } else {
            LOGGER.info("SALAO: desregistrar SKIP (nada pra remover)");
        }
    }

    // ====================================================================
    //  Construção dos cards
    // ====================================================================

    private VBox montarBlocoSetor(String setor, List<Mesa> mesas,
                                  Map<Long, ContaAberta> contaPorMesaId) {
        Label titulo = new Label(setor.toUpperCase());
        titulo.getStyleClass().add("sm-setor-titulo");

        FlowPane grid = new FlowPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(8, 0, 0, 0));

        for (Mesa m : mesas) {
            ContaAberta conta = contaPorMesaId.get(m.getId());
            StackPane card = montarCardMesa(m, conta);
            grid.getChildren().add(card);
            cardsOrdem.add(card);
            mesaPorCard.put(card, m);
        }

        VBox bloco = new VBox(titulo, grid);
        bloco.setSpacing(6);
        bloco.getStyleClass().add("sm-setor-bloco");
        return bloco;
    }

    private StackPane montarCardMesa(Mesa mesa, ContaAberta conta) {
        boolean ocupada = conta != null && conta.getStatus() == StatusContaAberta.A;
        boolean atendimento = conta != null && conta.getStatus() == StatusContaAberta.E;

        String classeStatus;
        Color corStatus;
        if (atendimento) {
            classeStatus = "sm-card-atendimento";
            corStatus = COR_ATENDIMENTO;
        } else if (ocupada) {
            classeStatus = "sm-card-ocupada";
            corStatus = COR_OCUPADA;
        } else {
            classeStatus = "sm-card-livre";
            corStatus = COR_LIVRE;
        }

        Label lblNum = new Label(String.valueOf(mesa.getNumero()));
        lblNum.getStyleClass().add("sm-card-numero");

        String cap = mesa.getCapacidade() != null
                ? mesa.getCapacidade() + " lug." : "—";
        Label lblCap = new Label(cap);
        lblCap.getStyleClass().add("sm-card-info");

        Label lblValor;
        if (ocupada || atendimento) {
            BigDecimal v = conta.getValorTotal() != null
                    ? conta.getValorTotal() : BigDecimal.ZERO;
            lblValor = new Label(MOEDA.format(v));
        } else {
            lblValor = new Label("Livre");
        }
        lblValor.getStyleClass().add("sm-card-valor");

        VBox conteudo = new VBox(lblNum, lblCap, lblValor);
        conteudo.setAlignment(Pos.CENTER);
        conteudo.setSpacing(4);

        FontIcon icone = new FontIcon("mdi2s-silverware-fork-knife");
        icone.setIconSize(16);
        icone.setIconColor(corStatus);
        icone.setOpacity(0.6);
        StackPane.setAlignment(icone, Pos.TOP_RIGHT);
        StackPane.setMargin(icone, new Insets(8, 10, 0, 0));

        StackPane card = new StackPane(conteudo, icone);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(130, 130);
        card.setMaxSize(130, 130);
        card.setMinSize(130, 130);
        card.setStyle("-fx-cursor: hand;");
        card.setFocusTraversable(true);
        card.getStyleClass().addAll("sm-card", classeStatus);

        card.setOnMouseClicked(ev -> abrirOuRetomar(mesa));
        card.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ENTER || ev.getCode() == KeyCode.SPACE) {
                abrirOuRetomar(mesa);
                ev.consume();
            } else {
                navegarComSetas(ev.getCode(), card);
            }
        });
        card.focusedProperty().addListener((o, antigo, focado) -> {
            if (focado) {
                indiceFocado = cardsOrdem.indexOf(card);
                if (!card.getStyleClass().contains("sm-card-focado")) {
                    card.getStyleClass().add("sm-card-focado");
                }
            } else {
                card.getStyleClass().remove("sm-card-focado");
            }
        });

        return card;
    }

    // ====================================================================
    //  Navegação
    // ====================================================================

    private void navegarComSetas(KeyCode codigo, StackPane cardAtual) {
        int idx = cardsOrdem.indexOf(cardAtual);
        if (idx < 0) return;

        if (codigo == KeyCode.UP && idx == 0) {
            tfBusca.requestFocus();
            tfBusca.positionCaret(tfBusca.getText() != null
                    ? tfBusca.getText().length() : 0);
            return;
        }

        int proximo = -1;
        if (codigo == KeyCode.RIGHT) proximo = idx + 1;
        else if (codigo == KeyCode.LEFT) proximo = idx - 1;
        else if (codigo == KeyCode.UP) proximo = idx - 5;
        else if (codigo == KeyCode.DOWN) proximo = idx + 5;
        else return;

        if (proximo >= 0 && proximo < cardsOrdem.size()) {
            cardsOrdem.get(proximo).requestFocus();
        }
    }

    private void buscarPorNumero() {
        String txt = tfBusca.getText() != null ? tfBusca.getText().trim() : "";
        if (txt.isEmpty()) return;

        int numero;
        try {
            numero = Integer.parseInt(txt);
        } catch (NumberFormatException e) {
            return;
        }

        for (Map.Entry<StackPane, Mesa> e : mesaPorCard.entrySet()) {
            if (e.getValue().getNumero() != null
                    && e.getValue().getNumero() == numero) {
                tfBusca.clear();
                abrirOuRetomar(e.getValue());
                return;
            }
        }

        new Alert(Alert.AlertType.WARNING,
                "Mesa " + numero + " não encontrada.").showAndWait();
    }

    private void abrirOuRetomar(Mesa mesa) {
        Optional<ContaAberta> contaOpt = contaService.buscarContaAtivaPorMesa(mesa);

        if (contaOpt.isEmpty()) {
            stageManager.showAberturaMesa(mesa, this::recarregar);
            return;
        }

        ContaAberta conta = contaOpt.get();
        if (conta.getStatus() == StatusContaAberta.E) {
            new Alert(Alert.AlertType.WARNING,
                    "Mesa " + mesa.getNumero()
                            + " está em atendimento no caixa.\n"
                            + "Aguarde o pagamento ser concluído.")
                    .showAndWait();
            return;
        }

        stageManager.showLancamentoMesa(conta.getId(), this::recarregar);
    }

    public void recarregar() {
        LOGGER.info("=== SALAO.recarregar() chamado");
        carregar();
    }
}