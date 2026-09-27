package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.config.StageManager;
import br.com.cactuserp.pdv.entity.ContaAberta;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.entity.StatusContaAberta;
import br.com.cactuserp.pdv.entity.TipoContaAberta;
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
 * Controller da tela ComandasAbertas.fxml.
 *
 * <p>Grid de cards das comandas abertas (status A) ou em atendimento (E).
 * Cores: laranja = aberta, azul = em atendimento.
 *
 * <p>Acionada por Ctrl+B. Fluxo:
 * <ul>
 *   <li>Operador digita código + ENTER → se existe continua, se não abre nova</li>
 *   <li>Setas pra navegar cards (último caso, com mouse)</li>
 *   <li>ENTER no card → vai pra LancamentoComanda</li>
 * </ul>
 *
 * <p><b>Importante:</b> NÃO acessa {@code comanda.getItens()} aqui — a query
 * de listagem não traz itens (são LAZY), então faria N+1 queries ou
 * LazyInitException fora de transação.
 */
@Component
public class ComandasAbertasController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComandasAbertasController.class);
    private static final NumberFormat MOEDA =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    private static final Color COR_ABERTA = Color.web("#c9901e");
    private static final Color COR_ATENDIMENTO = Color.web("#2969c9");

    private final StageManager stageManager;
    private final SessaoPdv sessaoPdv;
    private final ContaAbertaService contaService;

    @FXML private AnchorPane root;
    @FXML private Label lblOperador;
    @FXML private TextField tfCodigo;
    @FXML private VBox containerComandas;

    private final List<StackPane> cardsOrdem = new ArrayList<>();
    private final Map<StackPane, ContaAberta> comandaPorCard = new LinkedHashMap<>();

    private EventHandler<KeyEvent> filterTeclas;

    public ComandasAbertasController(StageManager stageManager,
                                     SessaoPdv sessaoPdv,
                                     ContaAbertaService contaService) {
        this.stageManager = stageManager;
        this.sessaoPdv = sessaoPdv;
        this.contaService = contaService;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ENTER no código → abre ou continua
        tfCodigo.setOnAction(e -> abrirOuContinuarPorCodigo());

        // Seta ↓ no campo de código → manda foco pro primeiro card
        tfCodigo.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.DOWN && !cardsOrdem.isEmpty()) {
                cardsOrdem.get(0).requestFocus();
                ev.consume();
            }
        });
    }

    public void carregar() {
        cardsOrdem.clear();
        comandaPorCard.clear();
        containerComandas.getChildren().clear();
        tfCodigo.clear();

        PdvOperador op = sessaoPdv.getOperadorAtual();
        if (op != null && op.getColaborador() != null) {
            lblOperador.setText("Operador: " + op.getColaborador().getNome());
        }

        // Carrega comandas ativas (status A ou E)
        List<ContaAberta> comandas = contaService.listarContasAtivasPorTipo(TipoContaAberta.C);

        // Título da seção + contador
        Label titulo = new Label("COMANDAS ABERTAS (" + comandas.size() + ")");
        titulo.getStyleClass().add("sm-setor-titulo");
        containerComandas.getChildren().add(titulo);

        if (comandas.isEmpty()) {
            Label vazio = new Label(
                    "Nenhuma comanda aberta. Digite o código acima para abrir uma nova.");
            vazio.getStyleClass().add("sm-vazio");
            containerComandas.getChildren().add(vazio);
        } else {
            FlowPane grid = new FlowPane();
            grid.setHgap(12);
            grid.setVgap(12);
            grid.setPadding(new Insets(8, 0, 0, 0));

            for (ContaAberta c : comandas) {
                StackPane card = montarCardComanda(c);
                grid.getChildren().add(card);
                cardsOrdem.add(card);
                comandaPorCard.put(card, c);
            }
            containerComandas.getChildren().add(grid);
        }

        // Foco inicial no campo código
        Platform.runLater(tfCodigo::requestFocus);

        registrarAtalhosGlobais();
    }

    // ====================================================================
    //  Atalhos globais — filter no root
    // ====================================================================

    private void registrarAtalhosGlobais() {
        if (filterTeclas != null) {
            root.removeEventFilter(KeyEvent.KEY_PRESSED, filterTeclas);
        }

        filterTeclas = ev -> {
            if (ev.getCode() == KeyCode.ESCAPE) {
                desregistrarAtalhosGlobais();
                stageManager.fecharComandasAbertas();
                ev.consume();
            } else if (ev.getCode() == KeyCode.F5) {
                recarregar();
                ev.consume();
            }
        };

        root.addEventFilter(KeyEvent.KEY_PRESSED, filterTeclas);
    }

    private void desregistrarAtalhosGlobais() {
        if (filterTeclas != null) {
            root.removeEventFilter(KeyEvent.KEY_PRESSED, filterTeclas);
            filterTeclas = null;
        }
    }

    // ====================================================================
    //  Cards
    // ====================================================================

    private StackPane montarCardComanda(ContaAberta comanda) {
        boolean atendimento = comanda.getStatus() == StatusContaAberta.E;
        Color corStatus = atendimento ? COR_ATENDIMENTO : COR_ABERTA;
        String classeStatus = atendimento ? "sm-card-atendimento" : "sm-card-ocupada";

        // Código (centro grande)
        Label lblCodigo = new Label(comanda.getCodigoComanda() != null
                ? comanda.getCodigoComanda() : "?");
        lblCodigo.getStyleClass().add("sm-card-codigo");

        // Cliente (ou "sem cliente")
        String nomeCliente = comanda.getNomeClienteAvulso();
        Label lblCliente;
        if (nomeCliente != null && !nomeCliente.isBlank()) {
            lblCliente = new Label(nomeCliente);
            lblCliente.getStyleClass().add("sm-card-cliente");
        } else {
            lblCliente = new Label("sem cliente");
            lblCliente.getStyleClass().addAll("sm-card-cliente", "sm-card-cliente-vazio");
        }
        lblCliente.setMaxWidth(110);
        lblCliente.setWrapText(false);

        // Valor
        BigDecimal v = comanda.getValorTotal() != null
                ? comanda.getValorTotal() : BigDecimal.ZERO;
        Label lblValor = new Label(MOEDA.format(v));
        lblValor.getStyleClass().add("sm-card-valor");

        // Hora de abertura (sem contagem de itens — evita LazyInit)
        String hora = comanda.getHoraAbertura() != null
                ? comanda.getHoraAbertura().substring(0,
                Math.min(5, comanda.getHoraAbertura().length()))
                : "—";
        Label lblInfo = new Label("Aberta às " + hora);
        lblInfo.getStyleClass().add("sm-card-info-rodape");

        VBox conteudo = new VBox(lblCodigo, lblCliente, lblValor, lblInfo);
        conteudo.setAlignment(Pos.CENTER);
        conteudo.setSpacing(3);

        // Ícone recibo no canto superior direito
        FontIcon icone = new FontIcon("mdi2r-receipt");
        icone.setIconSize(16);
        icone.setIconColor(corStatus);
        icone.setOpacity(0.6);
        StackPane.setAlignment(icone, Pos.TOP_RIGHT);
        StackPane.setMargin(icone, new Insets(8, 10, 0, 0));

        StackPane card = new StackPane(conteudo, icone);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(140, 140);
        card.setMaxSize(140, 140);
        card.setMinSize(140, 140);
        card.setStyle("-fx-cursor: hand;");
        card.setFocusTraversable(true);
        card.getStyleClass().addAll("sm-card", classeStatus);

        card.setOnMouseClicked(ev -> abrirLancamento(comanda));
        card.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ENTER || ev.getCode() == KeyCode.SPACE) {
                abrirLancamento(comanda);
                ev.consume();
            } else {
                navegarComSetas(ev.getCode(), card);
            }
        });
        card.focusedProperty().addListener((o, antigo, focado) -> {
            if (focado) {
                if (!card.getStyleClass().contains("sm-card-focado")) {
                    card.getStyleClass().add("sm-card-focado");
                }
            } else {
                card.getStyleClass().remove("sm-card-focado");
            }
        });

        return card;
    }

    private void navegarComSetas(KeyCode codigo, StackPane cardAtual) {
        int idx = cardsOrdem.indexOf(cardAtual);
        if (idx < 0) return;

        // Seta ↑ no PRIMEIRO card → volta pro campo código
        if (codigo == KeyCode.UP && idx == 0) {
            tfCodigo.requestFocus();
            tfCodigo.positionCaret(tfCodigo.getText() != null
                    ? tfCodigo.getText().length() : 0);
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

    // ====================================================================
    //  Ações
    // ====================================================================

    /**
     * ENTER no campo código: decide se abre nova ou continua existente.
     */
    private void abrirOuContinuarPorCodigo() {
        String codigo = tfCodigo.getText() != null ? tfCodigo.getText().trim() : "";
        if (codigo.isEmpty()) return;

        // Já existe comanda aberta com esse código?
        Optional<ContaAberta> existente = contaService.buscarContaAtivaPorComanda(codigo);
        if (existente.isPresent()) {
            ContaAberta c = existente.get();
            if (c.getStatus() == StatusContaAberta.E) {
                new Alert(Alert.AlertType.WARNING,
                        "Comanda '" + codigo + "' está em atendimento no caixa.\n"
                                + "Aguarde o pagamento ser concluído.").showAndWait();
                return;
            }
            // Continua
            tfCodigo.clear();
            abrirLancamento(c);
            return;
        }

        // Não existe — abre dialog de abertura (cliente opcional)
        tfCodigo.clear();
        stageManager.showAberturaComanda(codigo, this::recarregar);
    }

    private void abrirLancamento(ContaAberta comanda) {
        stageManager.showLancamentoComanda(comanda.getId(), this::recarregar);
    }

    public void recarregar() {
        carregar();
    }
}