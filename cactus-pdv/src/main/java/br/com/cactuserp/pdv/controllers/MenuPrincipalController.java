package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.config.StageManager;
import br.com.cactuserp.pdv.config.util.AutorizacaoGerencialDialog;
import br.com.cactuserp.pdv.entity.Colaborador;
import br.com.cactuserp.pdv.entity.NivelAutorizacao;
import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvMovimento;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.menu.MenuPrincipalItem;
import br.com.cactuserp.pdv.menu.MenuPrincipalItem.Secao;
import br.com.cactuserp.pdv.service.LoginService;
import br.com.cactuserp.pdv.service.PdvMovimentoService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Controller da tela MenuPrincipal.fxml (acionada pelo F12 do PDV).
 */
@Component
public class MenuPrincipalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuPrincipalController.class);

    @Autowired private SessaoPdv sessaoPdv;
    @Autowired private LoginService loginService;
    @Autowired private StageManager stageManager;
    @Autowired private PdvMovimentoService pdvMovimentoService;

    @FXML private Label lblBadgeStatus;
    @FXML private Label lblHeaderInfo;
    @FXML private GridPane gridCaixa;
    @FXML private GridPane gridCupom;
    @FXML private GridPane gridSistema;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        atualizarCabecalho();
        montarSecao(gridCaixa, MenuPrincipalItem.daSecao(Secao.CAIXA));
        montarSecao(gridCupom, MenuPrincipalItem.daSecao(Secao.CUPOM));
        montarSecao(gridSistema, MenuPrincipalItem.daSecao(Secao.SISTEMA));

        Platform.runLater(this::registrarAtalhosTeclado);
    }

    private void atualizarCabecalho() {
        String classeBadge;
        String textoBadge;

        if (sessaoPdv.movimentoEstaAberto()) {
            classeBadge = "badge-status-aberto";
            textoBadge = "Movimento aberto";
        } else if (sessaoPdv.movimentoEstaBloqueadoTemporariamente()) {
            classeBadge = "badge-status-temporario";
            textoBadge = "Movimento pausado";
        } else {
            classeBadge = "badge-status-fechado";
            textoBadge = "Sem movimento ativo";
        }

        lblBadgeStatus.setText(textoBadge);
        lblBadgeStatus.getStyleClass().removeIf(c ->
                c.equals("badge-status-aberto")
                        || c.equals("badge-status-temporario")
                        || c.equals("badge-status-fechado"));
        lblBadgeStatus.getStyleClass().add(classeBadge);

        lblHeaderInfo.setText(montarInfoOperador());
    }

    private String montarInfoOperador() {
        PdvMovimento mov = sessaoPdv.getMovimentoAtivoOuNulo();

        PdvOperador operador = sessaoPdv.getOperadorAtual();
        if (operador == null) {
            return "Sem operador · sem caixa";
        }

        String nomeOperador = sessaoPdv.getNomeOperadorAtual();
        String nivelExtenso = extensoDoNivel(obterNivel(operador));
        String nomeCaixa = sessaoPdv.getNomeCaixaAtual();
        Long idMov = sessaoPdv.getIdMovimentoAtivo();

        StringBuilder sb = new StringBuilder();
        sb.append(nomeOperador);
        if (nivelExtenso != null) {
            sb.append(" · ").append(nivelExtenso);
        }
        if (nomeCaixa != null && !nomeCaixa.isEmpty()) {
            sb.append(" · ").append(nomeCaixa);
        }
        if (idMov != null) {
            sb.append(" · Mov. #").append(idMov);
        } else {
            sb.append(" · sem movimento");
        }
        return sb.toString();
    }

    private NivelAutorizacao obterNivel(PdvOperador operador) {
        if (operador == null) return null;
        Colaborador colaborador = operador.getColaborador();
        if (colaborador == null) return null;
        return colaborador.getNivelAutorizacao();
    }

    private String extensoDoNivel(NivelAutorizacao nivel) {
        if (nivel == null) return null;
        switch (nivel) {
            case G: return "Gerente";
            case S: return "Supervisor";
            case O: return "Operador";
            case E: return "Entregador";
            default: return null;
        }
    }

    // ====== Montagem da grade de botões ===================================
    private void montarSecao(GridPane grid, List<MenuPrincipalItem> itens) {
        grid.getChildren().clear();

        final int colunas = 4;
        int col = 0;
        int row = 0;

        for (MenuPrincipalItem item : itens) {
            Node node = construirBotao(item);
            grid.add(node, col, row);
            GridPane.setHgrow(node, Priority.ALWAYS);
            GridPane.setFillWidth(node, true);

            col++;
            if (col >= colunas) {
                col = 0;
                row++;
            }
        }

        while (col > 0 && col < colunas) {
            grid.add(construirPlaceholderReservado(), col, row);
            col++;
        }
    }

    private Button construirBotao(MenuPrincipalItem item) {
        FontIcon icone = new FontIcon(item.getIconLiteral());
        icone.setIconSize(28);
        icone.setIconColor(javafx.scene.paint.Color.web(item.getCorIcone()));

        Label titulo = new Label(item.getRotulo());
        titulo.getStyleClass().add("menu-item-titulo");

        Label atalho = new Label(montarTextoAtalho(item));
        atalho.getStyleClass().add("menu-item-atalho");

        VBox conteudo = new VBox(6, icone, titulo, atalho);
        conteudo.setAlignment(Pos.CENTER);
        conteudo.setMouseTransparent(true);

        Button btn = new Button();
        btn.setGraphic(conteudo);
        btn.getStyleClass().add("menu-item-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setMaxHeight(Region.USE_PREF_SIZE);
        btn.setOnAction(e -> executar(item));

        if (!podeUsarAgora(item)) {
            btn.setDisable(true);
        }

        return btn;
    }

    private Region construirPlaceholderReservado() {
        Label lbl = new Label("(reservado)");
        lbl.getStyleClass().add("menu-item-reservado-label");

        VBox box = new VBox(lbl);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("menu-item-reservado");
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private String montarTextoAtalho(MenuPrincipalItem item) {
        if (item.exibeSeloGerencial()) {
            return "[" + item.getAtalho() + "] · Gerente";
        }
        return "[" + item.getAtalho() + "]";
    }

    // ====== Atalhos de teclado ============================================
    private void registrarAtalhosTeclado() {
        if (lblBadgeStatus.getScene() == null) {
            Platform.runLater(this::registrarAtalhosTeclado);
            return;
        }
        lblBadgeStatus.getScene().addEventFilter(KeyEvent.KEY_PRESSED, this::aoPressionarTecla);
    }

    private void aoPressionarTecla(KeyEvent ev) {
        if (ev.getCode() == KeyCode.ESCAPE) {
            fecharMenu();
            ev.consume();
            return;
        }

        String texto = ev.getText();
        if (texto == null || texto.isEmpty()) return;

        // Normaliza pra maiúsculo — atalhos no enum são em caixa alta,
        // mas ev.getText() retorna minúsculo quando CapsLock está
        // desligado. Sem essa conversão, B só funcionava com CapsLock.
        char tecla = Character.toUpperCase(texto.charAt(0));

        MenuPrincipalItem item = MenuPrincipalItem.porAtalho(tecla);
        if (item == null) return;

        if (!podeUsarAgora(item)) {
            LOGGER.warn("Operação {} indisponível agora.", item);
            ev.consume();
            return;
        }

        executar(item);
        ev.consume();
    }

    // ====== Dispatcher das ações ==========================================
    private void executar(MenuPrincipalItem item) {
        LOGGER.info("Menu Principal: executando {}", item);

        // FECHAR_MOVIMENTO pede senha dentro da tela do Z (evita dupla autorização)
        if (item.exigeAutorizacaoGerencial() && item != MenuPrincipalItem.FECHAR_MOVIMENTO) {
            Optional<PdvOperador> autorizador = pedirAutorizacaoGerencial(
                    item.getRotulo() + " — exige autorização de Gerente/Supervisor.");
            if (autorizador.isEmpty()) {
                LOGGER.info("Operação {} abortada — sem autorização.", item);
                return;
            }
            LOGGER.info("Operação {} autorizada por {}", item,
                    autorizador.get().getColaborador().getNome());
        }

        switch (item) {
            case FECHAR_MOVIMENTO:        handleFecharMovimento();        break;
            case ABRIR_MOVIMENTO:         handleAbrirMovimento();         break;
            case FECHAMENTO_TEMPORARIO:   handleFechamentoTemporario();   break;
            case TROCAR_OPERADOR:         handleTrocarOperador();         break;
            case CONFERENCIA_CAIXA:       handleConferenciaCaixa();       break;
            case REIMPRIMIR_CUPOM:        handleReimprimirCupom();        break;
            case CANCELAR_CUPOM_ANTERIOR: handleCancelarCupomAnterior();  break;
            case CONSULTAR_CUPOM:         handleConsultarCupom();         break;
            case CONSULTA_PRODUTO:        handleConsultaProduto();        break;
            case BLOQUEAR:                handleBloquear();               break;
            case SINCRONIZACAO_ERP:       handleSincronizacaoErp();       break;
            case CONFIGURACOES:           handleConfiguracoes();          break;
            case SAIR:                    handleSair();                   break;
            default:
                LOGGER.warn("Item de menu sem handler: {}", item);
        }
    }

    // ====== Handlers ======================================================

    /**
     * Abre o relatório Z. Após sucesso, o callback fecha este menu e
     * MANTÉM o operador logado no PDV em modo restrito.
     */
    private void handleFecharMovimento() {
        stageManager.showFecharMovimento(() -> {
            LOGGER.info("Pós-fechamento: movimento encerrado, operador permanece logado.");
            sessaoPdv.encerrarMovimento();
            fecharMenu();
            stageManager.notificarPdvSobreMudancaDeMovimento();
        });
    }

    /**
     * Abre um novo movimento. Reutiliza o {@code StageManager.showIniciarMovimento}
     * passando o operador atual da sessão (mesmo após fechar movimento ele
     * permanece em memória).
     */
    private void handleAbrirMovimento() {
        if (sessaoPdv.temMovimentoAtivo()) {
            new Alert(Alert.AlertType.WARNING,
                    "Já existe um movimento ativo. Feche-o antes de abrir um novo.")
                    .showAndWait();
            return;
        }

        PdvOperador operador = sessaoPdv.getOperadorAtual();
        if (operador == null) {
            new Alert(Alert.AlertType.ERROR,
                    "Não foi possível identificar o operador da sessão.")
                    .showAndWait();
            return;
        }

        stageManager.showIniciarMovimento(operador, null, movimento -> {
            LOGGER.info("Novo movimento aberto: ID={}", movimento.getId());
            sessaoPdv.iniciarSessao(movimento);
            fecharMenu();
            stageManager.notificarPdvSobreMudancaDeMovimento();
        });
    }

    /**
     * SAIR — logoff total: encerra sessão e volta ao Login.
     *
     * <p>Pede confirmação por senha do operador atual antes de prosseguir.
     * Isso evita logoff acidental e mantém auditabilidade — fica claro
     * quem fez o logout e quando.
     *
     * <p>O movimento de caixa fica <b>aberto</b> no banco; o próximo
     * operador pode retomá-lo via login normal.
     */
    private void handleSair() {
        PdvOperador operadorAtual = sessaoPdv.getOperadorAtual();
        if (operadorAtual == null) {
            // Sem operador na sessão? Logoff direto.
            sessaoPdv.encerrarSessao();
            fecharMenu();
            stageManager.resetarPdv();
            stageManager.showLoginScene();
            return;
        }

        String nomeOperador = operadorAtual.getColaborador().getNome();

        Optional<String> senhaConfirmada = pedirSenhaDoOperadorAtual(nomeOperador);
        if (senhaConfirmada.isEmpty()) {
            LOGGER.info("Logout cancelado pelo operador {}", nomeOperador);
            return;
        }

        // Valida a senha usando o LoginService (mesmo método do login)
        try {
            loginService.autenticar(operadorAtual.getLogin(), senhaConfirmada.get());
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR,
                    "Senha incorreta. Logout cancelado.").showAndWait();
            LOGGER.warn("Tentativa de logout com senha incorreta: operador={}", nomeOperador);
            return;
        }

        LOGGER.info("Logout autorizado: operador={}", nomeOperador);

        // Avisa se ainda tem movimento aberto (não bloqueia, só informa)
        if (sessaoPdv.movimentoEstaAberto()) {
            LOGGER.info("Logout com movimento ID={} ainda aberto — próximo operador retoma.",
                    sessaoPdv.getIdMovimentoAtivo());
        }

        sessaoPdv.encerrarSessao();
        fecharMenu();
        stageManager.resetarStateDoPdvParaLogin();
        stageManager.showLoginScene();
    }

    // Stubs por enquanto
    private void handleCancelarCupomAnterior()  { stub("Cancelar Cupom Anterior"); }

    /**
     * D.7 — TROCAR OPERADOR
     *
     * <p>Operador vai sair e OUTRO operador vai assumir.
     * Tecnicamente igual ao D.6 (status A → T) — diferença é só a
     * mensagem de confirmação e o uso típico.
     *
     * <p>Quando o novo operador fizer login, o
     * {@code LoginController.tratarRetomada()} detecta que é outro
     * operador, pede autorização gerencial, e o
     * {@code PdvMovimentoService.retomarMovimento()} reatribui o
     * movimento ao novo operador.
     */
    private void handleTrocarOperador() {
        LOGGER.info("Menu Principal: executando TROCAR_OPERADOR");

        PdvOperador operadorAtual = sessaoPdv.getOperadorAtual();
        PdvMovimento mov = sessaoPdv.getMovimentoAtivoOuNulo();

        if (operadorAtual == null || mov == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Não há movimento ativo para trocar de operador.")
                    .showAndWait();
            return;
        }

        // 1) Confirmação
        Alert confirma = new Alert(Alert.AlertType.CONFIRMATION,
                "Encerrar o turno de " + operadorAtual.getColaborador().getNome()
                        + " para outro operador assumir?\n\n"
                        + "O movimento ficará preservado (valores, vendas, fundo).\n"
                        + "O novo operador precisará autorização gerencial para retomar.",
                ButtonType.YES, ButtonType.NO);
        confirma.setTitle("Trocar Operador");
        confirma.setHeaderText("Confirmação");
        Optional<ButtonType> r = confirma.showAndWait();
        if (r.isEmpty() || r.get() != ButtonType.YES) {
            LOGGER.info("Troca de operador cancelada");
            return;
        }

        // 2) Pede senha do operador atual (confirmação que é ele mesmo saindo)
        if (!confirmarSenhaOperadorAtual(
                "Trocar Operador",
                "Confirme sua senha para liberar o caixa para outro operador:")) {
            LOGGER.info("Troca de operador cancelada — senha não confirmada");
            return;
        }

        // 3) Fecha o movimento (mesma operação técnica do D.6)
        executarFechamentoTemporario(mov);
    }

    /**
     * D.6 — FECHAMENTO TEMPORÁRIO
     *
     * <p>Operador vai se afastar do PDV mas pretende voltar
     * (almoço, pausa, troca de turno breve). Movimento muda pra status T.
     * Quando o MESMO operador faz login depois, continua de onde parou.
     */
    private void handleFechamentoTemporario() {
        LOGGER.info("Menu Principal: executando FECHAMENTO_TEMPORARIO");

        PdvOperador operadorAtual = sessaoPdv.getOperadorAtual();
        PdvMovimento mov = sessaoPdv.getMovimentoAtivoOuNulo();

        if (operadorAtual == null || mov == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Não há movimento ativo para fechar temporariamente.")
                    .showAndWait();
            return;
        }

        // 1) Confirmação
        Alert confirma = new Alert(Alert.AlertType.CONFIRMATION,
                "Fechar temporariamente o movimento de "
                        + operadorAtual.getColaborador().getNome() + "?\n\n"
                        + "O PDV será bloqueado e os valores ficarão preservados.\n"
                        + "Você poderá retomar fazendo login novamente.",
                ButtonType.YES, ButtonType.NO);
        confirma.setTitle("Fechamento Temporário");
        confirma.setHeaderText("Confirmação");
        Optional<ButtonType> r = confirma.showAndWait();
        if (r.isEmpty() || r.get() != ButtonType.YES) {
            LOGGER.info("Fechamento temporário cancelado pelo operador");
            return;
        }

        // 2) Pede senha do próprio operador (segurança)
        if (!confirmarSenhaOperadorAtual(
                "Fechamento Temporário",
                "Confirme sua senha para fechar temporariamente o caixa:")) {
            LOGGER.info("Fechamento temporário cancelado — senha não confirmada");
            return;
        }

        // 3) Fecha o movimento
        executarFechamentoTemporario(mov);
    }

    /**
     * Executa o fechamento temporário e leva pra tela de login.
     * Usado pelos handlers D.6 (Fechamento Temporário) e D.7 (Trocar Operador).
     *
     * <p>Ordem crítica:
     * <ol>
     *   <li>{@code fecharMenu()} — fecha a modal do menu primeiro</li>
     *   <li>{@code fecharTemporariamente(mov)} — persiste status A→T no banco</li>
     *   <li>{@code resetarStateDoPdvParaLogin()} — limpa cache do PDV
     *       (telaPdvCache + pdv4ControllerCache + jaMostrouPdv).
     *       SEM ISSO, o próximo showPdv4Scene reusa Nodes velhos e
     *       estoura NullPointerException no TAB traversal.</li>
     *   <li>{@code encerrarSessao()} — limpa a sessão atual</li>
     *   <li>{@code showLoginScene()} — vai pra tela de login</li>
     * </ol>
     */
    private void executarFechamentoTemporario(PdvMovimento mov) {
        try {
            pdvMovimentoService.fecharTemporariamente(mov);
            LOGGER.info("Movimento {} fechado temporariamente — voltando ao login",
                    mov.getId());

            fecharMenu();
            stageManager.resetarStateDoPdvParaLogin(); // CRÍTICO — limpa cache
            sessaoPdv.encerrarSessao();
            stageManager.showLoginScene();

        } catch (Exception ex) {
            LOGGER.error("Falha ao fechar movimento temporariamente", ex);
            new Alert(Alert.AlertType.ERROR,
                    "Falha ao fechar movimento: " + ex.getMessage())
                    .showAndWait();
        }
    }

    /**
     * CONFERÊNCIA DE CAIXA — confronta valores físicos com o esperado.
     *
     * <p>Exige autorização gerencial (que o dispatcher já trata, mas a
     * permissão padrão do menu já marca como exigeAutorizacaoGerencial=false,
     * então fazemos a auth aqui explicitamente).
     *
     * <p>Em vez disso, vamos forçar auth via dispatcher: o item da
     * Conferência precisa de exigeAutorizacaoGerencial=true no enum.
     * Como você pediu auth obrigatória, vou fazer aqui no handler
     * sem mexer no enum (menos invasivo).
     */
    private void handleConferenciaCaixa() {
        Optional<PdvOperador> autorizador = pedirAutorizacaoGerencial(
                "Conferência de Caixa — exige autorização de Gerente/Supervisor.");
        if (autorizador.isEmpty()) {
            LOGGER.info("Conferência abortada — sem autorização");
            return;
        }

        LOGGER.info("Conferência autorizada por {}",
                autorizador.get().getColaborador().getNome());
        fecharMenu();
        stageManager.showConferenciaCaixa(autorizador.get());
    }

    /**
     * Reimprimir cupom fiscal.
     *
     * <p><b>BLOQUEADO até NFC-e/SAT ficar pronto (fase E do roadmap).</b>
     * Não é possível "reimprimir" sem ter primeiro a impressão fiscal
     * original funcionando — o cupom precisa do XML autorizado pela
     * SEFAZ, QR Code, chave de acesso, etc.
     *
     * <p>Quando NFC-e/SAT estiver pronto, esta tela vai:
     * <ol>
     *   <li>Carregar XML do cupom já autorizado (banco/disco)</li>
     *   <li>Renderizar DANFE-NFC-e 80mm</li>
     *   <li>Enviar à impressora térmica</li>
     * </ol>
     */
    private void handleReimprimirCupom() {
        LOGGER.info("Reimprimir Cupom acionado — recurso bloqueado até NFC-e estar pronto");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reimpressão de Cupom");
        alert.setHeaderText("Recurso em desenvolvimento");
        alert.setContentText(
                "A reimpressão de cupom depende da emissão fiscal "
                        + "(NFC-e / SAT), que ainda não foi implementada "
                        + "neste PDV.\n\n"
                        + "Para consultar dados de um cupom anterior, "
                        + "use F12 → Consultar Cupom.");
        alert.showAndWait();
    }

    private void handleConsultarCupom() {
        LOGGER.info("Abrindo Consulta de Cupom");
        fecharMenu();
        stageManager.showConsultaCupom();
    }

    private void handleConsultaProduto() {
        LOGGER.info("Abrindo Consulta de Produto");
        fecharMenu();
        stageManager.showConsultaProduto();
    }

    private void handleSincronizacaoErp() {
        LOGGER.info("Abrindo Sincronização com o ERP");
        fecharMenu();
        stageManager.showSincronizacaoErp();
    }

    private void handleConfiguracoes() {
        LOGGER.info("Abrindo Configuração de Periféricos");
        fecharMenu();
        stageManager.showConfiguracaoPeriferico();
    }
    /**
     * BLOQUEAR — bloqueia a UI do PDV até alguém digitar a senha.
     *
     * <p>O movimento de caixa NÃO é alterado (continua aberto fiscalmente).
     * O bloqueio é só de UI/sessão. Mantém todo o estado (cupom em
     * andamento, itens, etc) intacto.
     *
     * <p>Desbloqueio possível por:
     * <ul>
     *   <li>Senha do próprio operador que bloqueou</li>
     *   <li>Login + senha de Gerente/Supervisor (botão F1 na tela)</li>
     * </ul>
     */
    private void handleBloquear() {
        PdvOperador operadorAtual = sessaoPdv.getOperadorAtual();
        if (operadorAtual == null) {
            new Alert(Alert.AlertType.ERROR,
                    "PONTO 2: operadorAtual é NULL").showAndWait();
            return;
        }

        LOGGER.info("PDV bloqueado por {}",
                operadorAtual.getColaborador().getNome());

        fecharMenu();

        try {
            stageManager.showTelaBloqueio(operadorAtual);
        } catch (Throwable t) {
            // Pega QUALQUER coisa (Exception, Error, etc)
            new Alert(Alert.AlertType.ERROR,
                    "EXCEPTION em showTelaBloqueio:\n\n"
                            + t.getClass().getName() + "\n"
                            + t.getMessage()).showAndWait();
            t.printStackTrace();
        }
    }

    private void stub(String nomeAcao) {
        LOGGER.info(">>> Em desenvolvimento: {}", nomeAcao);
    }

    private void fecharMenu() {
        LOGGER.info("Fechando Menu Principal");
        if (stage != null) {
            stage.close();
        }
    }

    // ====== Permissão =====================================================

    /**
     * Verifica se a opção pode ser usada AGORA, considerando nível do
     * operador E o estado do movimento.
     *
     * <p>Itens que exigem movimento aberto ficam desabilitados quando
     * em modo restrito; ABRIR_MOVIMENTO é o inverso (desabilitado se
     * já houver movimento).
     */
    private boolean podeUsarAgora(MenuPrincipalItem item) {
        PdvOperador operador = sessaoPdv.getOperadorAtual();
        NivelAutorizacao nivel = obterNivel(operador);
        if (!item.permiteNivel(nivel)) {
            return false;
        }

        boolean restrito = sessaoPdv.estaEmModoRestrito();

        switch (item) {
            case ABRIR_MOVIMENTO:
                return restrito; // só disponível quando NÃO há movimento ativo
            case FECHAR_MOVIMENTO:
            case FECHAMENTO_TEMPORARIO:
            case TROCAR_OPERADOR:
            case CONFERENCIA_CAIXA:
            case CANCELAR_CUPOM_ANTERIOR:
            case REIMPRIMIR_CUPOM:
                return !restrito; // exigem movimento aberto
            case CONSULTAR_CUPOM:
            case CONSULTA_PRODUTO:
            case BLOQUEAR:
            case SINCRONIZACAO_ERP:
            case CONFIGURACOES:
            case SAIR:
                return true; // sempre disponíveis enquanto houver operador logado
            default:
                return true;
        }
    }

    // ====== Sub-modal de autorização gerencial ============================
    private Optional<PdvOperador> pedirAutorizacaoGerencial(String contextoMensagem) {
        return AutorizacaoGerencialDialog.pedir(loginService, contextoMensagem);
    }

    /**
     * Pede a senha do operador atualmente logado pra confirmar uma ação
     * sensível (logout, bloqueio, etc).
     *
     * <p>Diferente de {@link #pedirAutorizacaoGerencial}, NÃO pede login —
     * já sabemos quem é o operador. Só valida que ele tem a senha correta.
     *
     * @param nomeOperador nome a exibir no diálogo (somente UI)
     * @return Optional com a senha digitada, ou empty se cancelou
     */
    private Optional<String> pedirSenhaDoOperadorAtual(String nomeOperador) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Confirmar logout");
        dialog.setHeaderText("Sair como " + nomeOperador + "?\n"
                + "Digite sua senha para confirmar.");

        ButtonType btnConfirmar = new ButtonType("Sair", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(btnConfirmar, ButtonType.CANCEL);

        PasswordField pfSenha = new PasswordField();
        pfSenha.setPromptText("Senha do operador");
        pfSenha.setPrefWidth(280);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Senha:"), 0, 0);
        grid.add(pfSenha, 1, 0);
        dialog.getDialogPane().setContent(grid);

        Platform.runLater(pfSenha::requestFocus);

        dialog.setResultConverter(button -> {
            if (button != btnConfirmar) return null;
            if (pfSenha.getText() == null || pfSenha.getText().isBlank()) return null;
            return pfSenha.getText();
        });

        return dialog.showAndWait();
    }

    /**
     * Pede e VALIDA a senha do operador atual.
     *
     * <p>Helper genérico reaproveitado por D.6 (Fechamento Temporário)
     * e D.7 (Trocar Operador). Valida a senha contra o {@code LoginService}
     * — se errar 3 vezes, cancela.
     *
     * @param titulo título do dialog (ex: "Fechamento Temporário")
     * @param mensagem texto acima do campo de senha
     * @return true se a senha foi confirmada corretamente, false se
     *         cancelou ou esgotou tentativas
     */
    private boolean confirmarSenhaOperadorAtual(String titulo, String mensagem) {
        PdvOperador operadorAtual = sessaoPdv.getOperadorAtual();
        if (operadorAtual == null) {
            new Alert(Alert.AlertType.ERROR,
                    "Sessão sem operador. Não é possível continuar.")
                    .showAndWait();
            return false;
        }

        for (int tentativa = 1; tentativa <= 3; tentativa++) {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle(titulo);
            dialog.setHeaderText(mensagem
                    + "\n\nOperador: " + operadorAtual.getColaborador().getNome()
                    + (tentativa > 1 ? "\n\n[Tentativa " + tentativa + " de 3]" : ""));

            ButtonType btnOk = new ButtonType("Confirmar", ButtonType.OK.getButtonData());
            dialog.getDialogPane().getButtonTypes().addAll(btnOk, ButtonType.CANCEL);

            PasswordField pfSenha = new PasswordField();
            pfSenha.setPromptText("Senha do operador");
            pfSenha.setPrefWidth(280);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.add(new Label("Senha:"), 0, 0);
            grid.add(pfSenha, 1, 0);
            dialog.getDialogPane().setContent(grid);

            Platform.runLater(pfSenha::requestFocus);

            dialog.setResultConverter(button -> {
                if (button != btnOk) return null;
                if (pfSenha.getText() == null || pfSenha.getText().isBlank()) return null;
                return pfSenha.getText();
            });

            Optional<String> senhaDigitada = dialog.showAndWait();

            if (senhaDigitada.isEmpty()) {
                // Cancelou — não conta como tentativa errada
                return false;
            }

            try {
                // Valida a senha contra o LoginService
                loginService.autenticar(operadorAtual.getLogin(), senhaDigitada.get());
                return true; // ok
            } catch (Exception ex) {
                if (tentativa < 3) {
                    new Alert(Alert.AlertType.WARNING,
                            "Senha incorreta. Tente novamente.").showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR,
                            "Senha incorreta. Operação cancelada.").showAndWait();
                }
            }
        }
        return false;
    }
}