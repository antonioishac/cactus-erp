package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.util.AutorizacaoGerencialDialog;
import br.com.cactuserp.pdv.config.util.FormaterCurrency;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.SimNao;
import br.com.cactuserp.pdv.service.LoginService;
import br.com.cactuserp.pdv.service.VendaService;
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
 * Controller da modal de recuperação de cupom em aberto.
 *
 * <p>Acionada pelo {@code Pdv4Controller} ao detectar (no {@code initialize}
 * ou após login) um cupom com status A pendente no movimento ativo.
 *
 * <p>A modal é <b>travada</b>: o operador é obrigado a escolher Retomar ou
 * Cancelar. Não há botão fechar, ESC ou X.
 *
 * <h3>Resultado da escolha</h3>
 * Após o operador escolher, o controller chama o callback {@link #onDecisao}
 * com o {@link Resultado} ({@code RETOMAR} ou {@code CANCELADO}). Cabe ao
 * chamador agir (carregar detalhes na UI ou seguir com cupom vazio).
 */
@Component
public class RecuperarCupomController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecuperarCupomController.class);

    public enum Resultado { RETOMAR, CANCELADO }

    @FXML private Label lblNumeroCupom;
    @FXML private Label lblHoraAbertura;
    @FXML private Label lblQtdItens;
    @FXML private Label lblTotalAcumulado;

    private final VendaService vendaService;
    private final LoginService loginService;

    private Stage stage;
    private PdvVendaCabecalho cupom;
    private java.util.function.Consumer<Resultado> onDecisao;

    public RecuperarCupomController(VendaService vendaService,
                                    LoginService loginService) {
        this.vendaService = vendaService;
        this.loginService = loginService;
    }

    // ====== Inicialização via StageManager =================================

    public void setStage(Stage stage) {
        this.stage = stage;
        // Bloqueia o X da janela: se o operador tentar fechar, ignora o evento.
        stage.setOnCloseRequest(ev -> ev.consume());
    }

    public void setOnDecisao(java.util.function.Consumer<Resultado> onDecisao) {
        this.onDecisao = onDecisao;
    }

    /**
     * Carrega o cupom a recuperar e preenche os labels da tela.
     */
    public void carregar(PdvVendaCabecalho cupom) {
        this.cupom = cupom;

        lblNumeroCupom.setText("#" + cupom.getId());
        lblHoraAbertura.setText(cupom.getHoraVenda() != null ? cupom.getHoraVenda() : "—");

        int totalDeItens = vendaService.listarDetalhes(cupom).size();
        long ativos = vendaService.listarDetalhes(cupom).stream()
                .filter(d -> d.getCancelado() == SimNao.N)
                .count();
        lblQtdItens.setText(ativos + (totalDeItens != ativos ? " (de " + totalDeItens + ")" : ""));

        BigDecimal total = cupom.getValorTotalDocumento() != null
                ? cupom.getValorTotalDocumento()
                : BigDecimal.ZERO;
        lblTotalAcumulado.setText("R$ " + FormaterCurrency.fieldFormated(total));
    }

    // ====== Handlers dos botões ===========================================

    @FXML
    private void handleRetomar() {
        LOGGER.info("Operador escolheu RETOMAR cupom #{}", cupom.getId());
        encerrarCom(Resultado.RETOMAR);
    }

    @FXML
    private void handleCancelar() {
        // Cancelar cupom exige autorização de gerente ou supervisor.
        Optional<PdvOperador> autorizador = pedirAutorizacaoGerencial();
        if (autorizador.isEmpty()) {
            LOGGER.debug("Cancelamento abortado: autorização não fornecida.");
            return; // operador não autorizou; modal continua aberta
        }

        try {
            vendaService.cancelarCupom(cupom);
            LOGGER.info("Cupom #{} cancelado por {}",
                    cupom.getId(),
                    autorizador.get().getColaborador().getNome());
            encerrarCom(Resultado.CANCELADO);
        } catch (Exception ex) {
            LOGGER.error("Falha ao cancelar cupom", ex);
            Alert err = new Alert(Alert.AlertType.ERROR,
                    "Não foi possível cancelar o cupom.\n\nMotivo: " + ex.getMessage());
            err.setHeaderText("Atenção");
            err.showAndWait();
            // não fecha — modal permanece para nova tentativa
        }
    }

    // ====== Sub-modal de senha de gerente =================================

    /**
     * Abre uma sub-modal pedindo login + senha de gerente/supervisor.
     * Retorna o operador autorizador, ou {@code Optional.empty()} se cancelou
     * ou a credencial não tem permissão.
     */
    private Optional<PdvOperador> pedirAutorizacaoGerencial() {
        return AutorizacaoGerencialDialog.pedir(loginService,
                "Cancelar o cupom exige autorização de Gerente ou Supervisor.");
    }

    // ====== Encerramento ==================================================

    private void encerrarCom(Resultado resultado) {
        if (onDecisao != null) {
            onDecisao.accept(resultado);
        }
        if (stage != null) {
            stage.close();
        }
    }
}