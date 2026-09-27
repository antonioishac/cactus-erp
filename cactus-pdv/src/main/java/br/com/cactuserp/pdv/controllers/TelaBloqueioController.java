package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.config.util.AutorizacaoGerencialDialog;
import br.com.cactuserp.pdv.entity.NivelAutorizacao;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.service.LoginService;
import br.com.cactuserp.pdv.service.MarketingService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller da TelaBloqueio.fxml.
 *
 * <p>Mostrada quando o operador escolhe F12 -> Bloquear no menu principal.
 * Cobre toda a UI do PDV. Destrava de duas maneiras:
 * <ul>
 *   <li>Operador digita sua propria senha + ENTER</li>
 *   <li>F1 abre dialogo gerencial com login + senha de Gerente/Supervisor</li>
 * </ul>
 *
 * <p>ESC NAO desbloqueia.
 */
@Component
public class TelaBloqueioController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelaBloqueioController.class);
    private static final DateTimeFormatter FMT_RELOGIO = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("'as' HH:mm");
    /** Tempo de exibição de cada imagem do slideshow de marketing. */
    private static final Duration DURACAO_SLIDE_MARKETING = Duration.seconds(8);

    @Autowired private SessaoPdv sessaoPdv;
    @Autowired private LoginService loginService;
    @Autowired private MarketingService marketingService;

    @FXML private Label lblNomeOperador;
    @FXML private Label lblHoraBloqueio;
    @FXML private PasswordField tfSenha;
    @FXML private Label lblMensagemErro;
    @FXML private Label lblRelogio;
    @FXML private ImageView ivMarketing;

    private Runnable callbackDesbloqueio;
    private PdvOperador operadorQueBloqueou;
    private Timeline timelineRelogio;
    private Timeline timelineMarketing;
    private int tentativasFalhas = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tfSenha.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ESCAPE) {
                ev.consume();
                return;
            }
            if (ev.getCode() == KeyCode.ENTER) {
                tentarDesbloquearComoOperador();
                ev.consume();
            }
            if (ev.getCode() == KeyCode.F1) {
                abrirDialogoGerencial();
                ev.consume();
            }
        });

        timelineRelogio = new Timeline(new KeyFrame(Duration.seconds(1), e -> atualizarRelogio()));
        timelineRelogio.setCycleCount(Animation.INDEFINITE);
        timelineRelogio.play();
        atualizarRelogio();

        if (ivMarketing.getParent() instanceof Region parentRegion) {
            ivMarketing.fitWidthProperty().bind(parentRegion.widthProperty());
            ivMarketing.fitHeightProperty().bind(parentRegion.heightProperty());
        }

        Platform.runLater(tfSenha::requestFocus);
    }

    /**
     * Configura a tela de bloqueio com o operador que esta bloqueando.
     * Chamado pelo StageManager antes de mostrar a tela.
     */
    public void configurar(PdvOperador operador, Runnable aoDesbloquear) {
        this.operadorQueBloqueou = operador;
        this.callbackDesbloqueio = aoDesbloquear;
        this.tentativasFalhas = 0;

        if (operador != null && operador.getColaborador() != null) {
            lblNomeOperador.setText(operador.getColaborador().getNome());
        } else {
            lblNomeOperador.setText("-");
        }
        lblHoraBloqueio.setText(LocalDateTime.now().format(FMT_HORA));

        configurarMarketing();
    }

    /**
     * Slideshow de marketing (fase 8, {@link MarketingService}) — só ativa
     * se a loja configurou {@code PDV_CONFIGURACAO.MARKETING_ATIVO='S'} e
     * a pasta de imagens tem conteúdo; senão a tela de bloqueio segue
     * exatamente como antes (fundo em gradiente, sem imagem).
     */
    private void configurarMarketing() {
        List<String> imagens = marketingService.listarImagensAtivas(sessaoPdv.getCaixaAtual());
        if (imagens.isEmpty()) {
            ivMarketing.setVisible(false);
            ivMarketing.setImage(null);
            return;
        }

        ivMarketing.setImage(new Image(imagens.get(0), true));
        ivMarketing.setVisible(true);

        int[] indiceAtual = {0};
        timelineMarketing = new Timeline(new KeyFrame(DURACAO_SLIDE_MARKETING, e -> {
            indiceAtual[0] = (indiceAtual[0] + 1) % imagens.size();
            ivMarketing.setImage(new Image(imagens.get(indiceAtual[0]), true));
        }));
        timelineMarketing.setCycleCount(Animation.INDEFINITE);
        timelineMarketing.play();
    }

    private void atualizarRelogio() {
        if (lblRelogio != null) {
            lblRelogio.setText(LocalTime.now().format(FMT_RELOGIO));
        }
    }

    private void tentarDesbloquearComoOperador() {
        String senha = tfSenha.getText();
        if (senha == null || senha.isBlank()) {
            mostrarErro("Digite sua senha.");
            return;
        }

        if (operadorQueBloqueou == null) {
            mostrarErro("Operador da sessao indefinido. Use F1 para desbloquear como gerente.");
            return;
        }

        try {
            loginService.autenticar(operadorQueBloqueou.getLogin(), senha);
            desbloquear(operadorQueBloqueou, "proprio operador");
        } catch (Exception ex) {
            tentativasFalhas++;
            tfSenha.clear();
            tfSenha.requestFocus();

            if (tentativasFalhas >= 3) {
                mostrarErro("Senha incorreta. Pressione F1 para desbloquear como gerente.");
            } else {
                mostrarErro("Senha incorreta. Tente novamente.");
            }
        }
    }

    /**
     * Abre dialogo modal pedindo login + senha de gerente/supervisor.
     * E a "saida de emergencia" quando o operador esqueceu a senha ou
     * esta ausente.
     */
    private void abrirDialogoGerencial() {
        Optional<PdvOperador> resultado = AutorizacaoGerencialDialog.pedir(loginService,
                "Desbloqueio gerencial", "Desbloquear PDV como Gerente/Supervisor",
                "Desbloquear", Alert.AlertType.ERROR);
        if (resultado.isPresent()) {
            desbloquear(resultado.get(), "autorizacao gerencial");
        } else {
            tfSenha.requestFocus();
        }
    }

    private void desbloquear(PdvOperador autorizador, String contexto) {
        LOGGER.info("PDV desbloqueado por {} ({})",
                autorizador.getColaborador().getNome(), contexto);

        if (timelineRelogio != null) {
            timelineRelogio.stop();
        }
        if (timelineMarketing != null) {
            timelineMarketing.stop();
        }

        if (callbackDesbloqueio != null) {
            callbackDesbloqueio.run();
        }
    }

    private void mostrarErro(String mensagem) {
        lblMensagemErro.setText(mensagem);
        lblMensagemErro.setVisible(true);
        lblMensagemErro.setManaged(true);
    }
}