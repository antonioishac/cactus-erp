package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.StageManager;
import br.com.cactuserp.pdv.entity.TipoContaAberta;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Dialog modal de seleção entre Mesa e Comanda pra importar conta.
 *
 * <h3>Atalhos</h3>
 * <ul>
 *   <li>F2 → escolhe Mesa</li>
 *   <li>F3 → escolhe Comanda</li>
 *   <li>ESC → cancela</li>
 * </ul>
 */
@Component
public class ImportarContaSeletorController implements Initializable {

    private final StageManager stageManager;

    @FXML private VBox root;
    @FXML private Button btnMesa;
    @FXML private Button btnComanda;

    private Stage stage;

    public ImportarContaSeletorController(StageManager stageManager) {
        this.stageManager = stageManager;
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
                        aoEscolherMesa();
                        ev.consume();
                    } else if (ev.getCode() == KeyCode.F3) {
                        aoEscolherComanda();
                        ev.consume();
                    }
                });
            }
            // Foco inicial na Mesa (opção mais comum)
            btnMesa.requestFocus();
        });
    }

    public void setStage(Stage stage) { this.stage = stage; }

    @FXML
    private void aoEscolherMesa() {
        if (stage != null) stage.close();
        stageManager.showBuscarConta(TipoContaAberta.M);
    }

    @FXML
    private void aoEscolherComanda() {
        if (stage != null) stage.close();
        stageManager.showBuscarConta(TipoContaAberta.C);
    }

    private void aoCancelar() {
        if (stage != null) stage.close();
    }
}