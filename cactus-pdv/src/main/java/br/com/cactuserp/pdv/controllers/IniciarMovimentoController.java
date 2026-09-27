package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvMovimento;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.service.PdvMovimentoService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

@Component
@Scope("prototype")
public class IniciarMovimentoController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(IniciarMovimentoController.class);

    private final PdvMovimentoService movimentoService;

    @FXML private TextField tfLoginGerente;
    @FXML private PasswordField pfSenhaGerente;
    @FXML private Button btnValidar;
    @FXML private Label lblGerenteValidado;

    @FXML private ComboBox<PdvOperador> cbOperador;
    @FXML private ComboBox<PdvCaixa> cbCaixa;
    @FXML private TextField tfValorInicial;

    @FXML private Button btnConfirmar;
    @FXML private Button btnCancelar;

    private Stage stage;
    private PdvOperador gerenteValidado;
    private Consumer<PdvMovimento> onMovimentoAberto;

    public IniciarMovimentoController(PdvMovimentoService movimentoService) {
        this.movimentoService = movimentoService;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarComboOperador();
        configurarComboCaixa();
        carregarComboBoxes();

        btnValidar.setOnAction(e -> validarGerente());
        btnConfirmar.setOnAction(e -> confirmar());
        btnCancelar.setOnAction(e -> cancelar());

        // Enter no campo de senha valida automaticamente
        pfSenhaGerente.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) validarGerente();
        });

        // Bloqueia operador/caixa/suprimento até validar gerente
        travarCampos(true);

        Platform.runLater(tfLoginGerente::requestFocus);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.F1)       { confirmar(); e.consume(); }
            else if (e.getCode() == KeyCode.F10) { cancelar();  e.consume(); }
        });
    }

    public void setOnMovimentoAberto(Consumer<PdvMovimento> callback) {
        this.onMovimentoAberto = callback;
    }

    private void configurarComboOperador() {
        cbOperador.setConverter(new StringConverter<>() {
            @Override
            public String toString(PdvOperador op) {
                if (op == null) return "";
                return op.getColaborador().getNome() + " (" + op.getLogin() + ")";
            }
            @Override
            public PdvOperador fromString(String s) { return null; }
        });
    }

    private void configurarComboCaixa() {
        cbCaixa.setConverter(new StringConverter<>() {
            @Override
            public String toString(PdvCaixa c) { return c == null ? "" : c.getNome(); }
            @Override
            public PdvCaixa fromString(String s) { return null; }
        });
    }

    private void carregarComboBoxes() {
        try {
            cbOperador.setItems(FXCollections.observableArrayList(
                    movimentoService.listarOperadoresDisponiveis()));
            cbCaixa.setItems(FXCollections.observableArrayList(
                    movimentoService.listarCaixas()));
        } catch (Exception ex) {
            LOGGER.error("Erro ao carregar combos", ex);
            alerta(Alert.AlertType.ERROR, "Erro ao carregar dados: " + ex.getMessage());
        }
    }

    private void travarCampos(boolean travar) {
        cbOperador.setDisable(travar);
        cbCaixa.setDisable(travar);
        tfValorInicial.setDisable(travar);
        btnConfirmar.setDisable(travar);
    }

    private void validarGerente() {
        try {
            gerenteValidado = movimentoService.autenticarGerenteSupervisor(
                    tfLoginGerente.getText(), pfSenhaGerente.getText());
            lblGerenteValidado.setText(
                    "✔ Autorizado: " + gerenteValidado.getColaborador().getNome() +
                            " (" + gerenteValidado.getColaborador().getNivelAutorizacao() + ")");
            lblGerenteValidado.setStyle("-fx-text-fill: #1f7a1f; -fx-font-weight: bold;");
            travarCampos(false);
            tfLoginGerente.setDisable(true);
            pfSenhaGerente.setDisable(true);
            btnValidar.setDisable(true);
            Platform.runLater(cbOperador::requestFocus);
        } catch (Exception ex) {
            gerenteValidado = null;
            lblGerenteValidado.setText("");
            alerta(Alert.AlertType.WARNING, ex.getMessage());
            tfLoginGerente.requestFocus();
            tfLoginGerente.selectAll();
        }
    }

    private void confirmar() {
        if (gerenteValidado == null) {
            alerta(Alert.AlertType.WARNING, "Valide a autorização do gerente/supervisor primeiro.");
            return;
        }

        try {
            BigDecimal suprimento = parseSuprimento(tfValorInicial.getText());

            PdvMovimento mov = movimentoService.abrirMovimento(
                    cbOperador.getValue(),
                    cbCaixa.getValue(),
                    gerenteValidado,
                    suprimento,
                    null // id do dispositivo - preencher se você tiver
            );

            LOGGER.info("Movimento aberto: ID={}, operador={}, caixa={}",
                    mov.getId(),
                    mov.getOperador().getColaborador().getNome(),
                    mov.getCaixa().getNome());

            if (onMovimentoAberto != null) {
                onMovimentoAberto.accept(mov);
            }
            fecharJanela();

        } catch (NumberFormatException ex) {
            alerta(Alert.AlertType.WARNING, "Valor de suprimento inválido. Use formato 0,00.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            alerta(Alert.AlertType.WARNING, ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error("Erro ao abrir movimento", ex);
            alerta(Alert.AlertType.ERROR, "Erro ao abrir movimento: " + ex.getMessage());
        }
    }

    /**
     * Pré-seleciona operador e caixa vindos da tela de login.
     * O operador continua editável (caso o gerente queira liberar outro),
     * mas o caixa fica travado por já estar vinculado à máquina.
     */
    public void preSelecionar(PdvOperador operador, PdvCaixa caixa) {
        if (operador != null) {
            Platform.runLater(() -> cbOperador.setValue(operador));
        }
        if (caixa != null) {
            Platform.runLater(() -> {
                cbCaixa.setValue(caixa);
                cbCaixa.setDisable(true); // caixa travado — já está vinculado
            });
        }
    }

    private BigDecimal parseSuprimento(String texto) {
        if (texto == null || texto.isBlank()) return BigDecimal.ZERO;
        // Aceita "1.234,56" ou "1234.56"
        String normalizado = texto.trim().replace(".", "").replace(",", ".");
        return new BigDecimal(normalizado);
    }

    private void cancelar() {
        fecharJanela();
    }

    private void fecharJanela() {
        if (stage != null) stage.close();
    }

    private void alerta(Alert.AlertType tipo, String msg) {
        Alert a = new Alert(tipo, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }
}