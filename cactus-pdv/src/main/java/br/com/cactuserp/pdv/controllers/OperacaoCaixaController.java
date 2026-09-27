package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.config.util.AutorizacaoGerencialDialog;
import br.com.cactuserp.pdv.config.util.FormaterCurrency;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.entity.TipoOperacaoCaixa;
import br.com.cactuserp.pdv.service.LoginService;
import br.com.cactuserp.pdv.service.OperacaoCaixaService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Controller da modal {@code OperacaoCaixa.fxml}.
 *
 * <p>Tela única que serve tanto para Sangria quanto para Suprimento,
 * parametrizada por {@link TipoOperacaoCaixa} via {@link #configurar(TipoOperacaoCaixa)}.
 *
 * <p>Fluxo de confirmação:
 * <ol>
 *   <li>Operador digita valor e observação</li>
 *   <li>Service decide se exige autorização gerencial</li>
 *   <li>Se sim, pede login+senha de gerente/supervisor</li>
 *   <li>Persiste e fecha modal</li>
 * </ol>
 */
@Component
public class OperacaoCaixaController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperacaoCaixaController.class);

    @FXML private VBox boxHeader;
    @FXML private Label lblTitulo;
    @FXML private Label lblDescricao;
    @FXML private TextField tfValor;
    @FXML private TextArea taObservacao;
    @FXML private Label lblInfoOperador;
    @FXML private HBox boxAviso;
    @FXML private Label lblAviso;
    @FXML private Button btnConfirmar;

    private final OperacaoCaixaService operacaoCaixaService;
    private final LoginService loginService;
    private final SessaoPdv sessaoPdv;

    private Stage stage;
    private TipoOperacaoCaixa tipo;

    public OperacaoCaixaController(OperacaoCaixaService operacaoCaixaService,
                                   LoginService loginService,
                                   SessaoPdv sessaoPdv) {
        this.operacaoCaixaService = operacaoCaixaService;
        this.loginService = loginService;
        this.sessaoPdv = sessaoPdv;
    }

    // ====== Inicialização via StageManager ===============================

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Configura a modal para um tipo específico (SUPRIMENTO ou SANGRIA).
     * Deve ser chamado pelo StageManager logo após o load do FXML.
     */
    public void configurar(TipoOperacaoCaixa tipo) {
        this.tipo = tipo;

        lblTitulo.setText(tipo.getRotulo());
        lblDescricao.setText(tipo.getDescricao());
        lblInfoOperador.setText(montarInfoOperador());

        // Cor do cabeçalho + descrição + botão
        if (tipo == TipoOperacaoCaixa.SUPRIMENTO) {
            boxHeader.getStyleClass().add("opcx-header-suprimento");
            lblDescricao.getStyleClass().add("opcx-header-desc-suprimento");
            btnConfirmar.getStyleClass().add("opcx-btn-confirmar-suprimento");
        } else {
            boxHeader.getStyleClass().add("opcx-header-sangria");
            lblDescricao.getStyleClass().add("opcx-header-desc-sangria");
            btnConfirmar.getStyleClass().add("opcx-btn-confirmar-sangria");
        }

        // Listener no valor — atualiza aviso de autorização em tempo real
        tfValor.textProperty().addListener((obs, old, novo) -> atualizarAvisoAutorizacao());

        Platform.runLater(tfValor::requestFocus);
    }

    private String montarInfoOperador() {
        String nome = sessaoPdv.getNomeOperadorAtual();
        String caixa = sessaoPdv.getNomeCaixaAtual();
        Long mov = sessaoPdv.getIdMovimentoAtivo();
        StringBuilder sb = new StringBuilder("Operador: ").append(nome);
        if (caixa != null && !caixa.isEmpty()) sb.append(" · Caixa ").append(caixa);
        if (mov != null) sb.append(" · Mov. #").append(mov);
        return sb.toString();
    }

    private void atualizarAvisoAutorizacao() {
        BigDecimal valor = parseValorSilencioso();
        boolean exige = operacaoCaixaService.exigeAutorizacaoGerencial(tipo, valor);
        boxAviso.setVisible(exige);
        boxAviso.setManaged(exige);
        if (exige) {
            lblAviso.setText("Valor acima de R$ "
                    + FormaterCurrency.fieldFormated(OperacaoCaixaService.LIMITE_SANGRIA_SEM_AUTORIZACAO)
                    + " — exigirá autorização de Gerente/Supervisor.");
        }
    }

    // ====== Handlers =====================================================

    @FXML
    private void handleCancelar() {
        LOGGER.debug("Operação cancelada pelo operador.");
        fechar();
    }

    @FXML
    private void handleConfirmar() {
        BigDecimal valor;
        try {
            valor = parseValor();
        } catch (NumberFormatException e) {
            mostrarErro("Valor inválido. Digite um número (ex: 100,00).");
            tfValor.requestFocus();
            return;
        }

        String observacao = taObservacao.getText() == null ? "" : taObservacao.getText().trim();
        if (observacao.length() < 5) {
            mostrarErro("Observação obrigatória (mínimo 5 caracteres).");
            taObservacao.requestFocus();
            return;
        }

        // Pede autorização gerencial se for o caso
        PdvOperador gerenteAutorizador = null;
        if (operacaoCaixaService.exigeAutorizacaoGerencial(tipo, valor)) {
            Optional<PdvOperador> autorizador = pedirAutorizacaoGerencial(
                    tipo.getRotulo() + " acima do limite — exige autorização gerencial.");
            if (autorizador.isEmpty()) {
                LOGGER.info("Operação abortada — autorização não fornecida.");
                return;
            }
            gerenteAutorizador = autorizador.get();
        }

        // Persiste
        try {
            if (tipo == TipoOperacaoCaixa.SUPRIMENTO) {
                operacaoCaixaService.registrarSuprimento(valor, observacao, gerenteAutorizador);
            } else {
                operacaoCaixaService.registrarSangria(valor, observacao, gerenteAutorizador);
            }
        } catch (Exception ex) {
            LOGGER.error("Falha ao registrar " + tipo, ex);
            mostrarErro("Não foi possível registrar.\n\nMotivo: " + ex.getMessage());
            return;
        }

        // Sucesso
        Alert ok = new Alert(Alert.AlertType.INFORMATION,
                tipo.getRotulo() + " de R$ " + FormaterCurrency.fieldFormated(valor)
                        + " registrado com sucesso.");
        ok.setHeaderText("Operação concluída");
        ok.showAndWait();
        fechar();
    }

    // ====== Helpers ======================================================

    private BigDecimal parseValor() {
        String texto = tfValor.getText() == null ? "" : tfValor.getText().trim().replace(",", ".");
        if (texto.isEmpty()) {
            throw new NumberFormatException("vazio");
        }
        BigDecimal valor = new BigDecimal(texto);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NumberFormatException("não positivo");
        }
        return valor;
    }

    private BigDecimal parseValorSilencioso() {
        try {
            return parseValor();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Optional<PdvOperador> pedirAutorizacaoGerencial(String contextoMensagem) {
        return AutorizacaoGerencialDialog.pedir(loginService, contextoMensagem);
    }

    private void mostrarErro(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.setHeaderText("Atenção");
        alert.showAndWait();
    }

    private void fechar() {
        if (stage != null) stage.close();
    }
}