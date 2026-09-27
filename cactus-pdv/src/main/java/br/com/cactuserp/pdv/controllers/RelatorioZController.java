package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.config.util.AutorizacaoGerencialDialog;
import br.com.cactuserp.pdv.config.util.FormaterCurrency;
import br.com.cactuserp.pdv.entity.PdvMovimento;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.service.FechamentoMovimentoService;
import br.com.cactuserp.pdv.service.LoginService;
import br.com.cactuserp.pdv.service.dto.RelatorioZ;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Controller da tela {@code RelatorioZ.fxml} (item 9 do roadmap).
 *
 * <p>Mostra os totalizadores do movimento e exige conferência do valor
 * físico antes de fechar o caixa. Persiste o fechamento via
 * {@link FechamentoMovimentoService}.
 *
 * <p>Fluxo:
 * <ol>
 *   <li>Carrega o snapshot do {@link RelatorioZ} e popula os labels</li>
 *   <li>Operador digita o valor físico → divergência atualiza em tempo real</li>
 *   <li>Operador clica em "Fechar Movimento"</li>
 *   <li>Pede senha de gerente/supervisor</li>
 *   <li>Persiste o fechamento</li>
 *   <li>Notifica o chamador via callback ({@link #onFechamentoConcluido})</li>
 * </ol>
 */
@Component
public class RelatorioZController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelatorioZController.class);

    @FXML private Label lblInfoMovimento;
    @FXML private Label lblVendasBruto;
    @FXML private Label lblCancelamentos;
    @FXML private Label lblTotalLiquido;
    @FXML private Label lblFundoInicial;
    @FXML private Label lblSuprimentos;
    @FXML private Label lblSangrias;
    @FXML private Label lblSaldoEsperado;
    @FXML private TextField tfValorFisico;
    @FXML private Label lblDivergencia;

    private final FechamentoMovimentoService fechamentoService;
    private final LoginService loginService;
    private final SessaoPdv sessaoPdv;

    private Stage stage;
    private RelatorioZ relatorioZ;
    private Runnable onFechamentoConcluido;

    public RelatorioZController(FechamentoMovimentoService fechamentoService,
                                LoginService loginService,
                                SessaoPdv sessaoPdv) {
        this.fechamentoService = fechamentoService;
        this.loginService = loginService;
        this.sessaoPdv = sessaoPdv;
    }

    // ====== Inicialização via StageManager ===============================

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOnFechamentoConcluido(Runnable callback) {
        this.onFechamentoConcluido = callback;
    }

    /** Carrega o snapshot do relatório e popula os labels. */
    public void carregar(RelatorioZ z) {
        this.relatorioZ = z;

        lblInfoMovimento.setText(montarInfoMovimento(z));

        lblVendasBruto.setText("R$ " + FormaterCurrency.fieldFormated(z.getTotalVendasBruto()));
        lblCancelamentos.setText("R$ " + FormaterCurrency.fieldFormated(z.getTotalCancelado()));
        lblTotalLiquido.setText("R$ " + FormaterCurrency.fieldFormated(z.getTotalVendasLiquido()));

        lblFundoInicial.setText("R$ " + FormaterCurrency.fieldFormated(z.getValorFundoInicial()));
        lblSuprimentos.setText("R$ " + FormaterCurrency.fieldFormated(z.getTotalSuprimento()));
        lblSangrias.setText("R$ " + FormaterCurrency.fieldFormated(z.getTotalSangria()));
        lblSaldoEsperado.setText("R$ " + FormaterCurrency.fieldFormated(z.getSaldoEsperadoEmCaixa()));

        // Listener no campo de valor físico — atualiza divergência ao vivo
        tfValorFisico.textProperty().addListener((obs, old, novo) -> atualizarDivergencia());

        Platform.runLater(tfValorFisico::requestFocus);
    }

    private String montarInfoMovimento(RelatorioZ z) {
        return "Movimento #" + z.getIdMovimento()
                + " · " + z.getNomeOperador()
                + " · " + z.getNomeCaixa()
                + " · Aberto " + z.getDataAbertura() + " " + z.getHoraAbertura();
    }

    // ====== Atualização visual da divergência ============================

    private void atualizarDivergencia() {
        BigDecimal valorFisico = parseValorSilencioso();

        lblDivergencia.getStyleClass().removeIf(c ->
                c.equals("relz-divergencia-valor-aguardando")
                        || c.equals("relz-divergencia-valor-zero")
                        || c.equals("relz-divergencia-valor-divergente"));

        if (valorFisico == null) {
            lblDivergencia.setText("aguardando valor…");
            lblDivergencia.getStyleClass().add("relz-divergencia-valor-aguardando");
            return;
        }

        BigDecimal div = relatorioZ.calcularDivergencia(valorFisico);
        if (div.compareTo(BigDecimal.ZERO) == 0) {
            lblDivergencia.setText("R$ 0,00 (conferido)");
            lblDivergencia.getStyleClass().add("relz-divergencia-valor-zero");
        } else {
            String sinal = div.compareTo(BigDecimal.ZERO) > 0 ? "+ " : "- ";
            String tipo  = div.compareTo(BigDecimal.ZERO) > 0 ? "sobra" : "falta";
            lblDivergencia.setText(sinal + "R$ "
                    + FormaterCurrency.fieldFormated(div.abs())
                    + " (" + tipo + ")");
            lblDivergencia.getStyleClass().add("relz-divergencia-valor-divergente");
        }
    }

    // ====== Handlers =====================================================

    @FXML
    private void handleCancelar() {
        LOGGER.debug("Fechamento cancelado pelo operador.");
        fechar();
    }

    @FXML
    private void handleConfirmar() {
        BigDecimal valorFisico;
        try {
            valorFisico = parseValor();
        } catch (NumberFormatException e) {
            mostrarErro("Valor físico inválido. Digite um número (ex: 1065,00).");
            tfValorFisico.requestFocus();
            return;
        }

        Optional<PdvOperador> autorizador = pedirAutorizacaoGerencial(
                "Fechar movimento exige autorização de Gerente ou Supervisor.");
        if (autorizador.isEmpty()) {
            LOGGER.info("Fechamento abortado — sem autorização.");
            return;
        }

        PdvMovimento movimento = sessaoPdv.getMovimentoAtivoOuNulo();
        if (movimento == null) {
            mostrarErro("Não há movimento ativo na sessão.");
            return;
        }

        try {
            fechamentoService.fecharMovimento(movimento, valorFisico, autorizador.get());
        } catch (Exception ex) {
            LOGGER.error("Falha ao fechar movimento", ex);
            mostrarErro("Não foi possível fechar o movimento.\n\nMotivo: " + ex.getMessage());
            return;
        }

        LOGGER.info("Movimento #{} fechado por {}",
                movimento.getId(),
                autorizador.get().getColaborador().getNome());

        Alert ok = new Alert(Alert.AlertType.INFORMATION,
                "Movimento #" + movimento.getId() + " fechado com sucesso.");
        ok.setHeaderText("Caixa fechado");
        ok.showAndWait();

        // Notifica o chamador (que vai encerrar sessão e ir pro login)
        if (onFechamentoConcluido != null) {
            onFechamentoConcluido.run();
        }
        fechar();
    }

    // ====== Helpers ======================================================

    private BigDecimal parseValor() {
        String texto = tfValorFisico.getText() == null
                ? "" : tfValorFisico.getText().trim().replace(",", ".");
        if (texto.isEmpty()) throw new NumberFormatException("vazio");
        BigDecimal valor = new BigDecimal(texto);
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new NumberFormatException("negativo");
        }
        return valor;
    }

    private BigDecimal parseValorSilencioso() {
        try { return parseValor(); } catch (NumberFormatException e) { return null; }
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