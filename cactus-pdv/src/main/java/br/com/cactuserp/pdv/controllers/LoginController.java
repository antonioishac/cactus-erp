package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.config.StageManager;
import br.com.cactuserp.pdv.entity.DispositivoCaixa;
import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.service.DispositivoService;
import br.com.cactuserp.pdv.service.LicencaService;
import br.com.cactuserp.pdv.service.LoginService;
import br.com.cactuserp.pdv.service.PdvMovimentoService;
import br.com.cactuserp.pdv.service.ResultadoLogin;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class LoginController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    private final LoginService loginService;
    private final DispositivoService dispositivoService;
    private final PdvMovimentoService movimentoService;
    private final SessaoPdv sessaoPdv;
    private final StageManager stageManager;
    private final LicencaService licencaService;

    @FXML private Label lblDispositivo;
    @FXML private TextField tfLogin;
    @FXML private PasswordField pfSenha;
    @FXML private ComboBox<PdvCaixa> cbCaixa;
    @FXML private Label lblMensagem;
    @FXML private Button btnEntrar;
    @FXML private Button btnSair;

    public LoginController(LoginService loginService,
                           DispositivoService dispositivoService,
                           PdvMovimentoService movimentoService,
                           SessaoPdv sessaoPdv,
                           StageManager stageManager,
                           LicencaService licencaService) {
        this.loginService = loginService;
        this.dispositivoService = dispositivoService;
        this.movimentoService = movimentoService;
        this.sessaoPdv = sessaoPdv;
        this.stageManager = stageManager;
        this.licencaService = licencaService;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarComboCaixa();
        carregarCaixas();
        identificarDispositivo();

        btnEntrar.setOnAction(e -> entrar());
        btnSair.setOnAction(e -> Platform.exit());

        pfSenha.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) entrar();
        });

        Platform.runLater(tfLogin::requestFocus);
    }

    private void configurarComboCaixa() {
        cbCaixa.setConverter(new StringConverter<>() {
            @Override public String toString(PdvCaixa c) { return c == null ? "" : c.getNome(); }
            @Override public PdvCaixa fromString(String s) { return null; }
        });
    }

    private void carregarCaixas() {
        try {
            cbCaixa.setItems(FXCollections.observableArrayList(
                    movimentoService.listarCaixas()));
        } catch (Exception ex) {
            LOGGER.error("Erro ao carregar caixas", ex);
            mostrarErro("Erro ao carregar lista de caixas: " + ex.getMessage());
        }
    }

    /**
     * Tenta identificar o caixa vinculado a esta máquina.
     * Se encontrar, pré-seleciona no combo e mostra na tela.
     */
    private void identificarDispositivo() {
        Optional<DispositivoCaixa> dispositivo = dispositivoService.identificarDispositivoAtual();

        if (dispositivo.isPresent()) {
            PdvCaixa caixa = dispositivo.get().getCaixa();
            cbCaixa.setValue(caixa);
            lblDispositivo.setText("Caixa identificado: " + caixa.getNome());
        } else {
            lblDispositivo.setText("Primeira vez nesta máquina — selecione o caixa.");
        }
    }

    private void entrar() {
        limparMensagem();
        btnEntrar.setDisable(true);

        try {
            PdvCaixa caixa = cbCaixa.getValue();
            if (caixa == null) {
                mostrarErro("Selecione o caixa.");
                return;
            }

            // 1. Autentica
            PdvOperador operador = loginService.autenticar(tfLogin.getText(), pfSenha.getText());

            // 2. Vincula dispositivo (idempotente — só faz na primeira vez ou se mudou)
            dispositivoService.vincularDispositivo(caixa);

            // 2.1 Trava de segurança (mensalidade) — avisa se inadimplente,
            // sem bloquear login (bloqueio real acontece só ao abrir novo
            // movimento — ver PdvMovimentoService.abrirMovimento).
            String avisoLicenca = licencaService.mensagemAvisoOuNull(caixa);
            if (avisoLicenca != null) {
                new Alert(Alert.AlertType.WARNING, avisoLicenca).showAndWait();
            }

            // 3. Decide para onde ir
            ResultadoLogin resultado = loginService.processarLogin(operador, caixa);

            switch (resultado.getTipo()) {
                case ABRIR_PDV -> {
                    sessaoPdv.iniciarSessao(resultado.getMovimento());
                    LOGGER.info("Login OK — abrindo PDV (movimento existente)");
                    stageManager.showPdv4Scene();
                }
                case MODO_RESTRITO -> {
                    sessaoPdv.iniciarSessaoApenasOperador(operador);
                    LOGGER.info("Login OK — abrindo PDV em MODO RESTRITO (sem movimento)");
                    stageManager.showPdv4Scene();
                }
                case ABRIR_MOVIMENTO -> {
                    LOGGER.info("Login OK — abrindo tela de Iniciar Movimento");
                    abrirTelaIniciarMovimento(operador, caixa);
                }
                case TROCAR_OPERADOR -> {
                    LOGGER.info("Login OK — caixa em uso por outro operador");
                    tratarTrocaOperador(resultado, operador, caixa);
                }
                case RETOMAR_MOVIMENTO -> {
                    LOGGER.info("Login OK — retomando movimento temporário");
                    tratarRetomada(resultado, operador);
                }
            }

        } catch (IllegalArgumentException | IllegalStateException ex) {
            mostrarErro(ex.getMessage());
            pfSenha.clear();
            tfLogin.requestFocus();
            tfLogin.selectAll();
        } catch (Exception ex) {
            LOGGER.error("Erro no login", ex);
            mostrarErro("Erro inesperado: " + ex.getMessage());
        } finally {
            btnEntrar.setDisable(false);
        }
    }

    private void abrirTelaIniciarMovimento(PdvOperador operador, PdvCaixa caixa) {
        stageManager.showIniciarMovimento(operador, caixa, movimento -> {
            sessaoPdv.iniciarSessao(movimento);
            stageManager.showPdv4Scene();
        });
    }

    private void tratarTrocaOperador(ResultadoLogin resultado, PdvOperador novoOperador, PdvCaixa caixa) {
        var movimento = resultado.getMovimento();
        String operadorAnterior = movimento.getOperador().getColaborador().getNome();

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Este caixa está aberto com o operador: " + operadorAnterior + ".\n\n" +
                        "Deseja assumir o caixa? Será necessária autorização de gerente/supervisor.",
                ButtonType.YES, ButtonType.NO);
        confirmacao.setHeaderText("Caixa em uso");

        Optional<ButtonType> resposta = confirmacao.showAndWait();
        if (resposta.isEmpty() || resposta.get() != ButtonType.YES) {
            mostrarInfo("Operação cancelada.");
            return;
        }

        Optional<PdvOperador> gerente = pedirAutorizacaoGerente();
        if (gerente.isEmpty()) return;

        try {
            var atualizado = movimentoService.trocarOperador(movimento, novoOperador, gerente.get());
            sessaoPdv.iniciarSessao(atualizado);
            LOGGER.info("Operador {} assumiu o caixa {}", novoOperador.getColaborador().getNome(), caixa.getNome());
            stageManager.showPdv4Scene();
        } catch (Exception ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void tratarRetomada(ResultadoLogin resultado, PdvOperador operadorAtual) {
        var movimento = resultado.getMovimento();
        boolean mesmoOperador = movimento.getOperador().getId().equals(operadorAtual.getId());

        if (mesmoOperador) {
            // Mesmo operador desbloqueando — não exige gerente
            try {
                var atualizado = movimentoService.retomarMovimento(movimento, operadorAtual);
                sessaoPdv.iniciarSessao(atualizado);
                stageManager.showPdv4Scene();
            } catch (Exception ex) {
                mostrarErro(ex.getMessage());
            }
            return;
        }

        // Outro operador retomando — exige autorização
        Alert info = new Alert(Alert.AlertType.CONFIRMATION,
                "Este caixa está bloqueado temporariamente por: " +
                        movimento.getOperador().getColaborador().getNome() + ".\n\n" +
                        "Deseja retomar? Será necessária autorização de gerente/supervisor.",
                ButtonType.YES, ButtonType.NO);
        info.setHeaderText("Caixa bloqueado");
        Optional<ButtonType> resp = info.showAndWait();
        if (resp.isEmpty() || resp.get() != ButtonType.YES) return;

        Optional<PdvOperador> gerente = pedirAutorizacaoGerente();
        if (gerente.isEmpty()) return;

        try {
            var atualizado = movimentoService.retomarMovimento(movimento, operadorAtual);
            sessaoPdv.iniciarSessao(atualizado);
            stageManager.showPdv4Scene();
        } catch (Exception ex) {
            mostrarErro(ex.getMessage());
        }
    }

    /**
     * Dialog simples para coletar login/senha de um gerente ou supervisor.
     * Retorna vazio se cancelar ou se a validação falhar.
     */
    private Optional<PdvOperador> pedirAutorizacaoGerente() {
        Dialog<PdvOperador> dialog = new Dialog<>();
        dialog.setTitle("Autorização");
        dialog.setHeaderText("Login do Gerente ou Supervisor");

        TextField tfLog = new TextField();
        tfLog.setPromptText("Login");
        PasswordField pfSen = new PasswordField();
        pfSen.setPromptText("Senha");

        VBoxHelper vbox = new VBoxHelper(tfLog, pfSen);
        dialog.getDialogPane().setContent(vbox.build());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Platform.runLater(tfLog::requestFocus);

        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null;
            try {
                return loginService.autenticarGerenteSupervisor(tfLog.getText(), pfSen.getText());
            } catch (Exception ex) {
                new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
                return null;
            }
        });

        return dialog.showAndWait();
    }

    private void mostrarErro(String msg) {
        lblMensagem.setText(msg);
        lblMensagem.setStyle("-fx-text-fill: #c0392b;");
    }

    private void mostrarInfo(String msg) {
        lblMensagem.setText(msg);
        lblMensagem.setStyle("-fx-text-fill: #2c3e50;");
    }

    private void limparMensagem() {
        lblMensagem.setText("");
    }

    /** Helper interno só para montar o VBox do dialog de autorização sem inflar a classe. */
    private static class VBoxHelper {
        private final TextField a;
        private final PasswordField b;
        VBoxHelper(TextField a, PasswordField b) { this.a = a; this.b = b; }
        javafx.scene.layout.VBox build() {
            javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(8);
            box.setPadding(new javafx.geometry.Insets(10));
            box.getChildren().addAll(new Label("Login:"), a, new Label("Senha:"), b);
            return box;
        }
    }
}