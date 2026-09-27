package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.entity.ContaAberta;
import br.com.cactuserp.pdv.entity.Mesa;
import br.com.cactuserp.pdv.service.ContaAbertaService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * Dialog modal pra abrir uma mesa, com campo opcional de nome do cliente.
 */
@Component
public class AberturaMesaController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AberturaMesaController.class);

    private final SessaoPdv sessaoPdv;
    private final ContaAbertaService contaService;

    @FXML private VBox root;
    @FXML private Label lblTitulo;
    @FXML private Label lblMesaInfo;
    @FXML private TextField tfNomeCliente;

    private Stage stage;
    private Mesa mesa;
    private Consumer<ContaAberta> onAberta;

    public AberturaMesaController(SessaoPdv sessaoPdv, ContaAbertaService contaService) {
        this.sessaoPdv = sessaoPdv;
        this.contaService = contaService;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            Scene s = root.getScene();
            if (s != null) {
                s.setOnKeyPressed(ev -> {
                    if (ev.getCode() == KeyCode.ESCAPE) {
                        aoCancelar();
                        ev.consume();
                    } else if (ev.getCode() == KeyCode.F2) {
                        aoAbrir();
                        ev.consume();
                    }
                });
            }
            tfNomeCliente.requestFocus();
        });
    }

    public void setStage(Stage stage) { this.stage = stage; }

    public void configurar(Mesa mesa, Consumer<ContaAberta> onAberta) {
        this.mesa = mesa;
        this.onAberta = onAberta;

        lblTitulo.setText("Abrir Mesa " + mesa.getNumero());
        String info = "Mesa " + mesa.getNumero();
        if (mesa.getSetor() != null) info += " · " + mesa.getSetor();
        if (mesa.getCapacidade() != null) info += " · " + mesa.getCapacidade() + " lugares";
        lblMesaInfo.setText(info);
    }

    @FXML
    private void aoCancelar() {
        if (stage != null) stage.close();
    }

    @FXML
    private void aoAbrir() {
        try {
            ContaAberta conta = contaService.abrirContaMesa(
                    mesa.getNumero(), sessaoPdv.getOperadorAtual());

            String nome = tfNomeCliente.getText() != null
                    ? tfNomeCliente.getText().trim() : "";
            if (!nome.isEmpty()) {
                conta.setNomeClienteAvulso(nome);
                // Como não exigi salvar de novo, deixa o service do lançamento gravar.
                // Pra simplicidade, gravo aqui também.
                contaService.atualizarObservacaoOuCliente(conta);
            }

            LOGGER.info("Mesa {} aberta com sucesso (conta #{})",
                    mesa.getNumero(), conta.getId());
            if (onAberta != null) onAberta.accept(conta);
            if (stage != null) stage.close();
        } catch (Exception ex) {
            LOGGER.error("Falha ao abrir mesa {}", mesa.getNumero(), ex);
            new Alert(Alert.AlertType.ERROR,
                    "Falha ao abrir mesa: " + ex.getMessage()).showAndWait();
        }
    }
}
