package br.com.cactuserp.pdv.config.util;

import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.service.LoginService;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.Optional;

/**
 * Diálogo padrão de autorização gerencial (login + senha de gerente/
 * supervisor) — pedido em toda operação que exige alçada gerencial:
 * sangria/suprimento acima do limite, desconto/acréscimo acima do limite,
 * devolução, fechamento de movimento com divergência, cancelamento de
 * cupom, Relatório Z fora do expediente, desbloqueio da tela de bloqueio.
 *
 * <p>Extraído (2026-08-23, ver {@code ROADMAP.md} fase 8 "autorização por
 * papel centralizada") de 8 controllers que replicavam essencialmente a
 * mesma ~35 linhas de JavaFX {@link Dialog}. A REGRA de negócio — quem
 * conta como gerente/supervisor — continua só em
 * {@link LoginService#autenticarGerenteSupervisor}; esta classe centraliza
 * apenas a UI que coleta login/senha e mostra o diálogo, evitando que cada
 * tela nova precise copiar/colar o mesmo bloco.
 *
 * <p>{@code LoginController} mantém seu próprio diálogo (layout diferente,
 * via {@code VBoxHelper}, tela de primeira impressão do sistema) — não foi
 * migrado pra não alterar a aparência dessa tela sem necessidade.
 */
public final class AutorizacaoGerencialDialog {

    private static final String TITULO_PADRAO = "Autorização necessária";
    private static final String BOTAO_PADRAO = "Autorizar";

    private AutorizacaoGerencialDialog() {}

    /** Diálogo com título/botão/severidade de erro padrão — cobre a maioria dos casos. */
    public static Optional<PdvOperador> pedir(LoginService loginService, String contextoMensagem) {
        return pedir(loginService, TITULO_PADRAO, contextoMensagem, BOTAO_PADRAO, Alert.AlertType.ERROR);
    }

    /** Variante totalmente customizável (título/texto do botão/severidade do alerta de erro). */
    public static Optional<PdvOperador> pedir(LoginService loginService, String tituloDialogo,
                                               String contextoMensagem, String textoBotaoConfirmar,
                                               Alert.AlertType tipoAlertaErro) {
        Dialog<PdvOperador> dialog = new Dialog<>();
        dialog.setTitle(tituloDialogo);
        dialog.setHeaderText(contextoMensagem);

        ButtonType btnAutorizar = new ButtonType(textoBotaoConfirmar, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAutorizar, ButtonType.CANCEL);

        TextField tfLogin = new TextField();
        tfLogin.setPromptText("Login do gerente/supervisor");
        PasswordField pfSenha = new PasswordField();
        pfSenha.setPromptText("Senha");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Login:"), 0, 0);
        grid.add(tfLogin, 1, 0);
        grid.add(new Label("Senha:"), 0, 1);
        grid.add(pfSenha, 1, 1);
        dialog.getDialogPane().setContent(grid);

        Platform.runLater(tfLogin::requestFocus);

        dialog.setResultConverter(button -> {
            if (button != btnAutorizar) return null;
            try {
                return loginService.autenticarGerenteSupervisor(tfLogin.getText(), pfSenha.getText());
            } catch (Exception ex) {
                new Alert(tipoAlertaErro, ex.getMessage()).showAndWait();
                return null;
            }
        });

        return dialog.showAndWait();
    }
}
