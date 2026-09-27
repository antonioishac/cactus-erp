package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.config.StageManager;
import br.com.cactuserp.pdv.entity.PdvConferenciaCaixa;
import br.com.cactuserp.pdv.entity.PdvMovimento;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.entity.TipoPagamento;
import br.com.cactuserp.pdv.service.ConferenciaCaixaService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
 * Controller da tela ConferenciaCaixa.fxml — versão refeita com
 * layout claro, navegação 100% teclado, e linha de dinheiro destacada
 * por ser a única que exige contagem física.
 */
@Component
public class ConferenciaCaixaController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConferenciaCaixaController.class);
    private static final NumberFormat MOEDA =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private static final NumberFormat FMT_VALOR_BR =
            NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR"));
    static {
        FMT_VALOR_BR.setMinimumFractionDigits(2);
        FMT_VALOR_BR.setMaximumFractionDigits(2);
    }

    /**
     * Código da forma de pagamento "Dinheiro" (definido em PDV_TIPO_PAGAMENTO).
     * Reaproveita a constante de {@link ConferenciaCaixaService} — evita ter
     * dois valores hardcoded que podem divergir (já aconteceu: este aqui
     * estava "01" enquanto o banco usa "001").
     */
    private static final String CODIGO_DINHEIRO = ConferenciaCaixaService.CODIGO_DINHEIRO;

    private final StageManager stageManager;
    private final SessaoPdv sessaoPdv;
    private final ConferenciaCaixaService conferenciaCaixaService;

    @FXML private AnchorPane root;
    @FXML private Label lblOperador;
    @FXML private Label lblInfoMovimento;
    @FXML private Label lblInfoGerente;
    @FXML private VBox containerFormas;
    @FXML private Label lblTotalEsperado;
    @FXML private Label lblTotalContado;
    @FXML private Label lblTotalDivergencia;
    @FXML private TextArea taObservacao;

    private final Map<TipoPagamento, TextField> camposContado = new LinkedHashMap<>();
    private final Map<TipoPagamento, BigDecimal> esperadoPorForma = new LinkedHashMap<>();
    private final Map<TipoPagamento, Label> labelsDivergencia = new LinkedHashMap<>();

    private PdvOperador gerenteAutorizador;

    public ConferenciaCaixaController(StageManager stageManager,
                                      SessaoPdv sessaoPdv,
                                      ConferenciaCaixaService conferenciaCaixaService) {
        this.stageManager = stageManager;
        this.sessaoPdv = sessaoPdv;
        this.conferenciaCaixaService = conferenciaCaixaService;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            Scene scene = root.getScene();
            if (scene != null) {
                // Atalhos GLOBAIS
                scene.setOnKeyPressed(ev -> {
                    if (ev.getCode() == KeyCode.ESCAPE) {
                        aoClicarCancelar();
                        ev.consume();
                    } else if (ev.getCode() == KeyCode.F2) {
                        aoClicarConfirmar();
                        ev.consume();
                    } else if (ev.getCode() == KeyCode.F3) {
                        aoClicarRecontar();
                        ev.consume();
                    }
                });
            }
        });
    }

    /**
     * Configura a tela com o gerente autorizador e carrega valores.
     * Chamado pelo StageManager.
     */
    public void configurar(PdvOperador gerente) {
        this.gerenteAutorizador = gerente;

        // Reset (singleton Spring)
        camposContado.clear();
        esperadoPorForma.clear();
        labelsDivergencia.clear();
        containerFormas.getChildren().clear();
        if (taObservacao != null) taObservacao.clear();

        PdvOperador operador = sessaoPdv.getOperadorAtual();
        if (operador != null && operador.getColaborador() != null) {
            lblOperador.setText("Operador: " + operador.getColaborador().getNome());
        } else {
            lblOperador.setText("");
        }

        PdvMovimento mov = sessaoPdv.getMovimentoAtivoOuNulo();
        if (mov != null) {
            lblInfoMovimento.setText("Movimento #" + mov.getId()
                    + (operador != null && operador.getColaborador() != null
                    ? " · Operador: " + operador.getColaborador().getNome() : ""));
        } else {
            lblInfoMovimento.setText("Sem movimento ativo");
        }

        if (gerente != null && gerente.getColaborador() != null) {
            lblInfoGerente.setText("Autorizado por: " + gerente.getColaborador().getNome());
        } else {
            lblInfoGerente.setText("");
        }

        if (mov != null) {
            Map<TipoPagamento, BigDecimal> esperados =
                    conferenciaCaixaService.calcularEsperadoPorForma(mov);
            esperadoPorForma.putAll(esperados);
        }

        if (esperadoPorForma.isEmpty()) {
            Label vazio = new Label("Nenhuma movimentação registrada neste movimento.");
            vazio.getStyleClass().add("cc2-vazio-label");
            HBox box = new HBox(vazio);
            box.setStyle("-fx-padding: 30 18 30 18; -fx-alignment: center;");
            containerFormas.getChildren().add(box);
        } else {
            for (Map.Entry<TipoPagamento, BigDecimal> e : esperadoPorForma.entrySet()) {
                adicionarLinhaForma(e.getKey(), e.getValue());
            }
        }

        atualizarTotais();

        // Foco no primeiro campo (que é o DINHEIRO se existir, senão o primeiro)
        Platform.runLater(() -> {
            if (!camposContado.isEmpty()) {
                TextField primeiro = camposContado.values().iterator().next();
                primeiro.requestFocus();
                primeiro.selectAll();
            } else {
                taObservacao.requestFocus();
            }
        });
    }

    /**
     * Cria UMA linha visual (HBox) por forma de pagamento.
     * A linha do DINHEIRO recebe destaque (classe extra cc2-linha-dinheiro).
     */
    private void adicionarLinhaForma(TipoPagamento forma, BigDecimal esperado) {
        boolean ehDinheiro = CODIGO_DINHEIRO.equals(forma.getCodigo());

        // --- Coluna 1: ícone + nome + dica ---
        FontIcon icone = new FontIcon(ehDinheiro ? "mdi2c-cash" : iconeDeForma(forma));
        icone.setIconSize(22);
        icone.setIconColor(javafx.scene.paint.Color.web(
                ehDinheiro ? "#c9901e" : "#4a5fb8"));

        Label lblNome = new Label(forma.getDescricao() != null ? forma.getDescricao() : "—");
        lblNome.getStyleClass().add("cc2-linha-nome");

        Label lblDica = new Label(ehDinheiro
                ? "Conte fisicamente na gaveta"
                : "Confira contra comprovantes/extrato");
        lblDica.getStyleClass().add("cc2-linha-dica");

        VBox boxTexto = new VBox(lblNome, lblDica);
        boxTexto.setSpacing(2);

        HBox col1 = new HBox(12, icone, boxTexto);
        col1.setAlignment(Pos.CENTER_LEFT);
        col1.setPrefWidth(340);
        HBox.setHgrow(col1, Priority.ALWAYS);

        // --- Coluna 2: esperado ---
        Label lblEsperado = new Label(formatarMoeda(esperado));
        lblEsperado.getStyleClass().add("cc2-linha-esperado");
        lblEsperado.setPrefWidth(140);
        lblEsperado.setMaxWidth(140);

        // --- Coluna 3: input contado ---
        TextField tfContado = new TextField();
        tfContado.getStyleClass().add(ehDinheiro
                ? "cc2-input-contado-dinheiro"
                : "cc2-input-contado");
        tfContado.setPromptText("0,00");
        tfContado.setPrefWidth(160);
        tfContado.setMaxWidth(160);
        configurarFormatacaoValor(tfContado);

        // --- Coluna 4: divergência ---
        Label lblDiv = new Label("R$ 0,00");
        lblDiv.getStyleClass().add("cc2-linha-divergencia");
        lblDiv.setPrefWidth(140);
        lblDiv.setMaxWidth(140);

        // Listener pra atualizar divergência + total ao digitar
        tfContado.textProperty().addListener((o, antigo, novo) -> {
            atualizarDivergenciaLinha(forma);
            atualizarTotais();
        });

        // ENTER pula pro próximo campo (em vez de TAB)
        tfContado.setOnAction(ev -> moverFocoAdiante(tfContado));

        HBox linha = new HBox(col1, lblEsperado, tfContado, lblDiv);
        linha.setAlignment(Pos.CENTER_LEFT);
        linha.setStyle("-fx-padding: 16 18 16 18;");
        linha.getStyleClass().add(ehDinheiro ? "cc2-linha-dinheiro" : "cc2-linha");

        camposContado.put(forma, tfContado);
        labelsDivergencia.put(forma, lblDiv);
        containerFormas.getChildren().add(linha);
    }

    private String iconeDeForma(TipoPagamento forma) {
        // Tenta usar o icone cadastrado, fallback genérico
        if (forma.getIcone() != null && !forma.getIcone().isBlank()) {
            return forma.getIcone();
        }
        return "mdi2c-credit-card";
    }

    private void configurarFormatacaoValor(TextField tf) {
        tf.setTextFormatter(new TextFormatter<>(change -> {
            if (!change.isContentChange()) return change;

            String proposto = change.getControlNewText();
            String digitos = proposto.replaceAll("[^0-9]", "");
            if (digitos.length() > 11) return null;

            if (digitos.isEmpty()) {
                change.setText("");
                change.setRange(0, change.getControlText().length());
                change.setCaretPosition(0);
                change.setAnchor(0);
                return change;
            }

            BigDecimal valor = new BigDecimal(digitos).movePointLeft(2);
            String formatado = FMT_VALOR_BR.format(valor);
            change.setText(formatado);
            change.setRange(0, change.getControlText().length());
            change.setCaretPosition(formatado.length());
            change.setAnchor(formatado.length());
            return change;
        }));
    }

    private void moverFocoAdiante(TextField atual) {
        List<TextField> campos = new ArrayList<>(camposContado.values());
        int idx = campos.indexOf(atual);
        if (idx >= 0 && idx < campos.size() - 1) {
            TextField proximo = campos.get(idx + 1);
            proximo.requestFocus();
            proximo.selectAll();
        } else {
            taObservacao.requestFocus();
        }
    }

    private void atualizarDivergenciaLinha(TipoPagamento forma) {
        TextField tf = camposContado.get(forma);
        Label lbl = labelsDivergencia.get(forma);
        if (tf == null || lbl == null) return;

        BigDecimal esp = esperadoPorForma.getOrDefault(forma, BigDecimal.ZERO);
        BigDecimal con = lerValor(tf);
        BigDecimal div = con.subtract(esp).setScale(2, RoundingMode.HALF_UP);

        lbl.setText(formatarMoeda(div));
        lbl.getStyleClass().removeAll("cc2-div-ok", "cc2-div-positiva", "cc2-div-negativa");
        if (div.compareTo(BigDecimal.ZERO) == 0) {
            lbl.getStyleClass().add("cc2-div-ok");
        } else if (div.compareTo(BigDecimal.ZERO) > 0) {
            lbl.getStyleClass().add("cc2-div-positiva");
        } else {
            lbl.getStyleClass().add("cc2-div-negativa");
        }
    }

    private void atualizarTotais() {
        BigDecimal totalEsp = BigDecimal.ZERO;
        BigDecimal totalCon = BigDecimal.ZERO;

        for (Map.Entry<TipoPagamento, BigDecimal> e : esperadoPorForma.entrySet()) {
            totalEsp = totalEsp.add(e.getValue());
            TextField tf = camposContado.get(e.getKey());
            if (tf != null) totalCon = totalCon.add(lerValor(tf));
        }

        BigDecimal div = totalCon.subtract(totalEsp).setScale(2, RoundingMode.HALF_UP);
        lblTotalEsperado.setText(formatarMoeda(totalEsp));
        lblTotalContado.setText(formatarMoeda(totalCon));
        lblTotalDivergencia.setText(formatarMoeda(div));

        lblTotalDivergencia.getStyleClass().removeAll("cc2-div-ok", "cc2-div-positiva", "cc2-div-negativa");
        if (div.compareTo(BigDecimal.ZERO) == 0) {
            lblTotalDivergencia.getStyleClass().add("cc2-div-ok");
        } else if (div.compareTo(BigDecimal.ZERO) > 0) {
            lblTotalDivergencia.getStyleClass().add("cc2-div-positiva");
        } else {
            lblTotalDivergencia.getStyleClass().add("cc2-div-negativa");
        }
    }

    private BigDecimal lerValor(TextField tf) {
        if (tf == null || tf.getText() == null || tf.getText().isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            String limpo = tf.getText().replace(".", "").replace(",", ".");
            return new BigDecimal(limpo);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    // ====================================================================
    //  Ações
    // ====================================================================

    @FXML
    private void aoClicarRecontar() {
        for (TextField tf : camposContado.values()) {
            tf.clear();
        }
        if (taObservacao != null) taObservacao.clear();
        atualizarTotais();

        if (!camposContado.isEmpty()) {
            TextField primeiro = camposContado.values().iterator().next();
            primeiro.requestFocus();
        }
    }

    @FXML
    private void aoClicarCancelar() {
        stageManager.fecharConferenciaCaixa();
    }

    @FXML
    private void aoClicarConfirmar() {
        PdvMovimento mov = sessaoPdv.getMovimentoAtivoOuNulo();
        PdvOperador operador = sessaoPdv.getOperadorAtual();

        if (mov == null || operador == null) {
            new Alert(Alert.AlertType.ERROR,
                    "Sessão sem movimento ou operador. Não é possível gravar.")
                    .showAndWait();
            return;
        }
        if (gerenteAutorizador == null) {
            new Alert(Alert.AlertType.ERROR,
                    "Autorização gerencial ausente. Não é possível gravar.")
                    .showAndWait();
            return;
        }

        BigDecimal totalDiv = lerTotalDivergencia();
        if (totalDiv.compareTo(BigDecimal.ZERO) != 0) {
            Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
            conf.setTitle("Divergência detectada");
            conf.setHeaderText("Há divergência de " + formatarMoeda(totalDiv));
            conf.setContentText("Confirmar a gravação mesmo com divergência?");
            Optional<ButtonType> r = conf.showAndWait();
            if (r.isEmpty() || r.get() != ButtonType.OK) return;
        }

        Map<TipoPagamento, BigDecimal> contados = new LinkedHashMap<>();
        for (Map.Entry<TipoPagamento, TextField> e : camposContado.entrySet()) {
            contados.put(e.getKey(), lerValor(e.getValue()));
        }

        String obs = taObservacao != null ? taObservacao.getText() : null;

        try {
            PdvConferenciaCaixa salva = conferenciaCaixaService.salvar(
                    mov, operador, gerenteAutorizador, contados, obs);

            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Conferência registrada");
            ok.setHeaderText("Conferência #" + salva.getId() + " gravada com sucesso");
            ok.setContentText("Esperado: " + formatarMoeda(salva.getValorTotalEsperado())
                    + "\nContado:  " + formatarMoeda(salva.getValorTotalContado())
                    + "\nDivergência: " + formatarMoeda(salva.getValorDivergencia()));
            ok.showAndWait();

            stageManager.fecharConferenciaCaixa();
        } catch (Exception ex) {
            LOGGER.error("Falha ao salvar conferência", ex);
            new Alert(Alert.AlertType.ERROR,
                    "Falha ao gravar conferência: " + ex.getMessage())
                    .showAndWait();
        }
    }

    private BigDecimal lerTotalDivergencia() {
        BigDecimal totalEsp = BigDecimal.ZERO;
        BigDecimal totalCon = BigDecimal.ZERO;
        for (Map.Entry<TipoPagamento, BigDecimal> e : esperadoPorForma.entrySet()) {
            totalEsp = totalEsp.add(e.getValue());
            TextField tf = camposContado.get(e.getKey());
            if (tf != null) totalCon = totalCon.add(lerValor(tf));
        }
        return totalCon.subtract(totalEsp).setScale(2, RoundingMode.HALF_UP);
    }

    private String formatarMoeda(BigDecimal v) {
        if (v == null) return "R$ 0,00";
        return MOEDA.format(v);
    }
}