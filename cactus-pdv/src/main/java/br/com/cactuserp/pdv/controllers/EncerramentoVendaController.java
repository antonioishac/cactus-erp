package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.config.StageManager;
import br.com.cactuserp.pdv.config.util.FormaterCurrency;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.TipoOperacaoDescontoAcrescimo;
import br.com.cactuserp.pdv.repository.PdvVendaCabecalhoRepository;
import br.com.cactuserp.pdv.service.EmissaoFiscalService;
import br.com.cactuserp.pdv.service.FidelidadeService;
import br.com.cactuserp.pdv.service.ImpressaoService;
import br.com.cactuserp.pdv.service.IntegracaoContaPdvService;
import br.com.cactuserp.pdv.service.TipoPagamentoService;
import br.com.cactuserp.pdv.service.dto.ResultadoEmissaoFiscal;
import br.com.cactuserp.pdv.service.dto.TipoPagamentoDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import br.com.cactuserp.pdv.entity.TipoPagamento;
import br.com.cactuserp.pdv.repository.TipoPagamentoRepository;
import br.com.cactuserp.pdv.service.VendaService;
import br.com.cactuserp.pdv.service.VendaService.PagamentoLancado;
import javafx.scene.control.Alert;
import java.util.stream.Collectors;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Controller da tela redesenhada de Finalizar Venda.
 *
 * <h3>Operação 100% por teclado</h3>
 * <ul>
 *   <li><b>↑ ↓ ← →</b> — navega entre os botões de forma de pagamento</li>
 *   <li><b>ENTER</b> sobre um botão de pagamento — seleciona a forma e move foco pro Valor</li>
 *   <li><b>Letra (D, P, C...)</b> — busca rápida (foca no botão cuja descrição começa com a letra)</li>
 *   <li><b>TAB</b> — navega entre painel de pagamentos e campo de Valor</li>
 *   <li><b>ENTER no Valor</b> ou <b>F10</b> — adiciona pagamento</li>
 *   <li><b>F4 / F5 / F6 / F12</b> — atalhos globais (acréscimo, cancelar, desconto, confirmar)</li>
 * </ul>
 */
@Component
public class EncerramentoVendaController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(EncerramentoVendaController.class);

    private static final NumberFormat FMT_VALOR_BR =
            NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR"));
    static {
        FMT_VALOR_BR.setMinimumFractionDigits(2);
        FMT_VALOR_BR.setMaximumFractionDigits(2);
    }

    /**
     * Quantas colunas o FlowPane mostra (usado pra calcular ↑↓).
     */
    private static final int COLUNAS_GRID = 5;

    private final StageManager stageManager;
    private final TipoPagamentoService tipoPagamentoService;
    private final SessaoPdv sessaoPdv;
    private final PdvVendaCabecalhoRepository cabecalhoRepository;
    private final VendaService vendaService;
    private final TipoPagamentoRepository pdvTipoPagamentoRepository;
    private final IntegracaoContaPdvService integracaoContaService;
    private final EmissaoFiscalService emissaoFiscalService;
    private final FidelidadeService fidelidadeService;
    private final ImpressaoService impressaoService;

    @FXML
    private AnchorPane root;
    @FXML
    private Label lblHeaderTitulo;
    @FXML
    private Label lblHeaderSubtitulo;

    @FXML
    private FlowPane painelBotoesPagamento;
    @FXML
    private TextField tfValor;
    @FXML
    private Button btnAdicionarPagamento;

    @FXML
    private Label lblTotalReceber;

    @FXML
    private Label lblSubtotal;
    @FXML
    private Label lblDesconto;
    @FXML
    private Label lblAcrescimo;
    @FXML
    private HBox linhaDesconto;
    @FXML
    private HBox linhaAcrescimo;

    @FXML
    private VBox cardPagamentosAdicionados;
    @FXML
    private VBox listaPagamentos;
    @FXML
    private Label lblPagamentosVazio;

    @FXML
    private Label lblTotalRecebido;
    @FXML
    private Label lblSaldoRestante;
    @FXML
    private ProgressBar progressoPagamento;

    @FXML
    private VBox cardTroco;
    @FXML
    private Label lblTroco;

    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnDesconto;
    @FXML
    private Button btnAcrescimo;
    @FXML
    private Button btnConfirmar;

    // Estado em memória
    private final List<PagamentoAdicionado> pagamentosAdicionados = new ArrayList<>();
    private TipoPagamentoDTO formaSelecionada;
    private final List<Button> botoesDePagamento = new ArrayList<>();

    /**
     * Total a receber (já considera desconto/acréscimo aplicado).
     */
    private BigDecimal valorTotal;

    /**
     * Estado da busca por letra: guarda a última letra pressionada e o
     * índice do último botão encontrado, pra permitir ciclar entre
     * múltiplas formas que começam com a mesma letra.
     */
    private String ultimaLetraBuscada = null;
    private int ultimoIndiceEncontrado = -1;

    public EncerramentoVendaController(TipoPagamentoService tipoPagamentoService,
                                       StageManager stageManager,
                                       SessaoPdv sessaoPdv,
                                       PdvVendaCabecalhoRepository cabecalhoRepository,
                                       VendaService vendaService,
                                       TipoPagamentoRepository pdvTipoPagamentoRepository,
                                       IntegracaoContaPdvService integracaoContaService,
                                       EmissaoFiscalService emissaoFiscalService,
                                       FidelidadeService fidelidadeService,
                                       ImpressaoService impressaoService) {
        this.tipoPagamentoService = tipoPagamentoService;
        this.stageManager = stageManager;
        this.sessaoPdv = sessaoPdv;
        this.cabecalhoRepository = cabecalhoRepository;
        this.vendaService = vendaService;
        this.pdvTipoPagamentoRepository = pdvTipoPagamentoRepository;
        this.integracaoContaService = integracaoContaService;
        this.emissaoFiscalService = emissaoFiscalService;
        this.fidelidadeService = fidelidadeService;
        this.impressaoService = impressaoService;
    }

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            Scene scene = root.getScene();
            if (scene != null) {
                setupKeyEvents(scene);
            }
            tfValor.requestFocus();
        });

        // Formatador do campo de valor (aceita só dígito + vírgula/ponto)
        configurarFormatacaoAutomaticaDoValor();

        tfValor.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                adicionarPagamento();
                event.consume();
            }
        });

        criarBotoesDePagamento();
        atualizarTela();
    }

    public void totalVendaInitialize() {
        recarregarDescontoAcrescimoDoBanco();
        atualizarTela();
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    /**
     * Reseta o estado interno do controller pra uma nova venda.
     *
     * <p>O controller é @Component Spring (singleton), então
     * {@code pagamentosAdicionados}, {@code formaSelecionada} e
     * outros estados internos persistem entre vendas. Sem esse reset,
     * a venda seguinte mostraria os pagamentos da venda anterior.
     *
     * <p>Chamado pelo {@code StageManager.showEncerramentoVenda(...)}
     * ANTES de carregar a nova venda.
     */
    public void prepararParaNovaVenda() {
        pagamentosAdicionados.clear();
        formaSelecionada = null;
        ultimaLetraBuscada = null;
        ultimoIndiceEncontrado = -1;

        // Esconde cards condicionais
        if (cardTroco != null) {
            cardTroco.setVisible(false);
            cardTroco.setManaged(false);
        }
        if (linhaDesconto != null) {
            linhaDesconto.setVisible(false);
            linhaDesconto.setManaged(false);
        }
        if (linhaAcrescimo != null) {
            linhaAcrescimo.setVisible(false);
            linhaAcrescimo.setManaged(false);
        }

        // Limpa input
        if (tfValor != null) {
            tfValor.clear();
        }

        // Reseta progresso e labels
        if (progressoPagamento != null) {
            progressoPagamento.setProgress(0);
        }
        if (lblTroco != null) {
            lblTroco.setText("R$ 0,00");
        }

        // Re-seleciona a primeira forma de pagamento (igual ao initialize)
        // Isso garante que ao abrir a 2ª, 3ª... venda, o operador já vem
        // com a forma padrão (Dinheiro) selecionada como na 1ª venda.
        if (!botoesDePagamento.isEmpty()) {
            Button primeiro = botoesDePagamento.get(0);
            TipoPagamentoDTO formaPadrao = (TipoPagamentoDTO) primeiro.getUserData();
            if (formaPadrao != null) {
                selecionarForma(formaPadrao);
            }
        }
    }

    /**
     * Configura o campo {@code tfValor} para formatação automática estilo
     * calculadora/PDV — operador digita só dígitos e o sistema formata
     * da direita pra esquerda.
     *
     * <p>Exemplos do que aparece no campo conforme o operador digita:
     * <pre>
     *   Digita "5"       → mostra "0,05"
     *   Digita "50"      → mostra "0,50"
     *   Digita "500"     → mostra "5,00"
     *   Digita "1234567" → mostra "12.345,67"
     *   Backspace        → remove último dígito e reformatta
     * </pre>
     *
     * <p>Funciona via TextFormatter: filtra a entrada (só dígitos), extrai
     * o "raw" como inteiro, e formata pra exibição BR (1.234,56).
     */
    private void configurarFormatacaoAutomaticaDoValor() {
        tfValor.setTextFormatter(new TextFormatter<>(change -> {
            // Ignora seleção que não muda conteúdo (foco, scroll, etc)
            if (!change.isContentChange()) {
                return change;
            }

            // Pega só os dígitos do texto novo proposto (ignora vírgulas e pontos)
            String proposto = change.getControlNewText();
            String somenteDigitos = proposto.replaceAll("[^0-9]", "");

            // Limita o tamanho (centavos = 2 dígitos, real até 9 dígitos → 99.999.999,99)
            if (somenteDigitos.length() > 11) {
                return null;
            }

            // Vazio → campo fica vazio
            if (somenteDigitos.isEmpty()) {
                change.setText("");
                change.setRange(0, change.getControlText().length());
                change.setCaretPosition(0);
                change.setAnchor(0);
                return change;
            }

            // Converte raw em valor BigDecimal (dividido por 100)
            java.math.BigDecimal valor = new java.math.BigDecimal(somenteDigitos)
                    .movePointLeft(2);

            // Formata pra BR: 12345,67 vira "12.345,67"
            String formatado = FMT_VALOR_BR.format(valor);

            // Substitui TODO o conteúdo do campo pelo formatado
            change.setText(formatado);
            change.setRange(0, change.getControlText().length());
            change.setCaretPosition(formatado.length());
            change.setAnchor(formatado.length());

            return change;
        }));
    }

    // ====================================================================
    //  ATALHOS GLOBAIS DA TELA
    // ====================================================================
    private void setupKeyEvents(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case F2:
                    focarNoPainelDePagamentos();
                    event.consume();
                    break;
                case F4:
                    aplicarDescontoAcrescimo(TipoOperacaoDescontoAcrescimo.ACRESCIMO);
                    event.consume();
                    break;
                case F5:
                    cancelarEncerramento();
                    event.consume();
                    break;
                case F6:
                    aplicarDescontoAcrescimo(TipoOperacaoDescontoAcrescimo.DESCONTO);
                    event.consume();
                    break;
                case F10:
                    adicionarPagamento();
                    event.consume();
                    break;
                case F12:
                    finalizarVenda();
                    event.consume();
                    break;
                default:
                    break;
            }
        });
    }

    // ====================================================================
    //  HANDLERS DE BOTÕES
    // ====================================================================

    @FXML
    private void handleCancelar() {
        cancelarEncerramento();
    }

    @FXML
    private void handleConfirmar() {
        finalizarVenda();
    }

    @FXML
    private void handleAdicionarPagamento() {
        adicionarPagamento();
    }

    @FXML
    private void handleDesconto() {
        aplicarDescontoAcrescimo(TipoOperacaoDescontoAcrescimo.DESCONTO);
    }

    @FXML
    private void handleAcrescimo() {
        aplicarDescontoAcrescimo(TipoOperacaoDescontoAcrescimo.ACRESCIMO);
    }

    // ====================================================================
    //  BOTÕES DE FORMA DE PAGAMENTO (criados dinamicamente)
    // ====================================================================

    private void criarBotoesDePagamento() {
        List<TipoPagamentoDTO> formas = tipoPagamentoService.listAllPayments();
        botoesDePagamento.clear();
        painelBotoesPagamento.getChildren().clear();

        for (TipoPagamentoDTO forma : formas) {
            Button btn = construirBotaoDePagamento(forma);
            painelBotoesPagamento.getChildren().add(btn);
            botoesDePagamento.add(btn);
        }

        // Seleciona a primeira forma por padrão (geralmente Dinheiro)
        if (!formas.isEmpty()) {
            selecionarForma(formas.get(0));
        }
    }

    private Button construirBotaoDePagamento(TipoPagamentoDTO forma) {
        VBox conteudo = new VBox(4);
        conteudo.setMouseTransparent(true);
        conteudo.setStyle("-fx-alignment: center;");

        String iconLiteral = forma.getIcone();
        if (iconLiteral == null || iconLiteral.isBlank()) {
            iconLiteral = "mdi2c-cash";
        }

        FontIcon icone = new FontIcon(iconLiteral);
        icone.setIconSize(28);
        icone.getStyleClass().add("encv-btn-pagamento-icone");

        Label texto = new Label(forma.getDescricao());
        texto.getStyleClass().add("encv-btn-pagamento-texto");

        conteudo.getChildren().addAll(icone, texto);

        Button btn = new Button();
        btn.setGraphic(conteudo);
        btn.getStyleClass().add("encv-btn-pagamento");
        btn.setPrefWidth(140);
        btn.setMaxWidth(140);
        btn.setFocusTraversable(true);

        btn.setOnAction(e -> {
            selecionarForma(forma);
            tfValor.requestFocus();
        });
        btn.setUserData(forma);

        // Captura teclas QUANDO o botão tem foco
        btn.setOnKeyPressed(this::handleTeclaNoBotaoPagamento);

        return btn;
    }

    /**
     * Trata teclas pressionadas quando um botão de pagamento está focado:
     * - Setas: navega entre botões
     * - ENTER: confirma a seleção e foca no campo de Valor
     * - Letras a-z: busca rápida (cicla entre botões que começam com a letra)
     */
    private void handleTeclaNoBotaoPagamento(KeyEvent event) {
        int indiceAtual = botoesDePagamento.indexOf(event.getSource());
        if (indiceAtual < 0) return;

        KeyCode code = event.getCode();

        switch (code) {
            case RIGHT:
                navegarPara(indiceAtual + 1);
                event.consume();
                return;
            case LEFT:
                navegarPara(indiceAtual - 1);
                event.consume();
                return;
            case DOWN:
                navegarPara(indiceAtual + COLUNAS_GRID);
                event.consume();
                return;
            case UP:
                navegarPara(indiceAtual - COLUNAS_GRID);
                event.consume();
                return;
            case ENTER:
            case SPACE:
                TipoPagamentoDTO forma = (TipoPagamentoDTO) ((Button) event.getSource()).getUserData();
                selecionarForma(forma);
                tfValor.requestFocus();
                event.consume();
                return;
            default:
                break;
        }

        // Busca por letra (ignora teclas com modificadores tipo Ctrl/Alt)
        if (event.isControlDown() || event.isAltDown() || event.isMetaDown()) return;

        String txt = event.getText();
        if (txt != null && txt.length() == 1 && Character.isLetter(txt.charAt(0))) {
            buscarPorLetra(txt, indiceAtual);
            event.consume();
        }
    }

    /**
     * Move o foco pro botão atualmente selecionado no painel de
     * formas de pagamento. Se nenhum estiver selecionado, foca no
     * primeiro botão disponível.
     */
    private void focarNoPainelDePagamentos() {
        if (botoesDePagamento.isEmpty()) return;

        // Tenta focar no botão atualmente selecionado
        for (Button btn : botoesDePagamento) {
            TipoPagamentoDTO dto = (TipoPagamentoDTO) btn.getUserData();
            if (formaSelecionada != null
                    && dto.getId() != null
                    && dto.getId().equals(formaSelecionada.getId())) {
                btn.requestFocus();
                return;
            }
        }

        // Fallback: foca no primeiro
        botoesDePagamento.get(0).requestFocus();
    }

    /**
     * Navega o foco para o botão no índice indicado, respeitando os limites.
     * Se o índice for inválido, mantém o foco no botão atual.
     */
    private void navegarPara(int indice) {
        if (indice < 0 || indice >= botoesDePagamento.size()) return;
        botoesDePagamento.get(indice).requestFocus();
    }

    /**
     * Busca o próximo botão cuja descrição começa com a letra digitada,
     * a partir do índice atual. Cicla caso chegue ao fim.
     */
    private void buscarPorLetra(String letra, int indiceAtual) {
        String letraNormalizada = normalizarTexto(letra).toLowerCase();

        // Se mesma letra anterior, busca a partir do PRÓXIMO botão (ciclar)
        int inicio;
        if (letraNormalizada.equals(ultimaLetraBuscada)) {
            inicio = (ultimoIndiceEncontrado + 1) % botoesDePagamento.size();
        } else {
            inicio = (indiceAtual + 1) % botoesDePagamento.size();
        }

        for (int i = 0; i < botoesDePagamento.size(); i++) {
            int idx = (inicio + i) % botoesDePagamento.size();
            Button btn = botoesDePagamento.get(idx);
            TipoPagamentoDTO dto = (TipoPagamentoDTO) btn.getUserData();
            String desc = normalizarTexto(dto.getDescricao()).toLowerCase();
            if (desc.startsWith(letraNormalizada)) {
                btn.requestFocus();
                ultimaLetraBuscada = letraNormalizada;
                ultimoIndiceEncontrado = idx;
                return;
            }
        }
    }

    /**
     * Remove acentos (ex: "Crédito" → "Credito") pra busca por letra funcionar.
     */
    private String normalizarTexto(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    private void selecionarForma(TipoPagamentoDTO forma) {
        this.formaSelecionada = forma;

        for (Button btn : botoesDePagamento) {
            TipoPagamentoDTO dto = (TipoPagamentoDTO) btn.getUserData();
            VBox conteudo = (VBox) btn.getGraphic();
            FontIcon icone = (FontIcon) conteudo.getChildren().get(0);
            Label texto = (Label) conteudo.getChildren().get(1);

            boolean selecionado = dto.getId() != null
                    && forma.getId() != null
                    && dto.getId().equals(forma.getId());

            btn.getStyleClass().removeIf(c -> c.equals("encv-btn-pagamento-selecionado"));
            if (selecionado) {
                btn.getStyleClass().add("encv-btn-pagamento-selecionado");
            }

            icone.getStyleClass().removeIf(c ->
                    c.equals("encv-btn-pagamento-icone") || c.equals("encv-btn-pagamento-icone-selecionado"));
            icone.getStyleClass().add(selecionado
                    ? "encv-btn-pagamento-icone-selecionado"
                    : "encv-btn-pagamento-icone");

            texto.getStyleClass().removeIf(c ->
                    c.equals("encv-btn-pagamento-texto") || c.equals("encv-btn-pagamento-texto-selecionado"));
            texto.getStyleClass().add(selecionado
                    ? "encv-btn-pagamento-texto-selecionado"
                    : "encv-btn-pagamento-texto");
        }

        // Pré-preenche o campo de valor com o saldo restante.
        // Setamos os DÍGITOS BRUTOS (sem vírgula): o TextFormatter
        // formata automaticamente em "X,XX" ou "X.XXX,XX".
        BigDecimal saldoRestante = calcularSaldoRestante();
        if (saldoRestante.compareTo(BigDecimal.ZERO) > 0) {
            String digitos = saldoRestante
                    .movePointRight(2)
                    .setScale(0, java.math.RoundingMode.HALF_UP)
                    .toPlainString();
            tfValor.setText(digitos);
            tfValor.selectAll();
        } else {
            tfValor.clear();
        }
    }

    // ====================================================================
    //  ADICIONAR / REMOVER PAGAMENTO
    // ====================================================================
    private void adicionarPagamento() {
        if (formaSelecionada == null) {
            LOGGER.warn("Nenhuma forma de pagamento selecionada");
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Selecione uma forma de pagamento antes de adicionar o valor.\n\n"
                            + "Pressione F2 para focar no painel de pagamentos, "
                            + "use as setas para navegar e ENTER para selecionar.");
            return;
        }
        if (tfValor.getText() == null || tfValor.getText().isBlank()) {
            LOGGER.warn("Valor de pagamento vazio");
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Informe o valor do pagamento.");
            tfValor.requestFocus();
            return;
        }

        BigDecimal valor;
        try {
            // Texto chega formatado em BR ("1.234,56") — remove ponto de
            // milhar e troca vírgula por ponto pra parsear corretamente.
            String limpo = tfValor.getText()
                    .replace(".", "")
                    .replace(",", ".");
            valor = new BigDecimal(limpo);
        } catch (NumberFormatException e) {
            LOGGER.warn("Valor inválido: {}", tfValor.getText());
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Valor inválido: " + tfValor.getText());
            tfValor.requestFocus();
            tfValor.selectAll();
            return;
        }

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "O valor do pagamento deve ser maior que zero.");
            tfValor.requestFocus();
            tfValor.selectAll();
            return;
        }

        pagamentosAdicionados.add(new PagamentoAdicionado(formaSelecionada, valor));

        atualizarTela();

        tfValor.clear();
        tfValor.requestFocus();
    }

    private void removerPagamento(PagamentoAdicionado p) {
        pagamentosAdicionados.remove(p);
        atualizarTela();
    }

    // ====================================================================
    //  RENDERIZAÇÃO
    // ====================================================================

    private void atualizarTela() {
        BigDecimal total = valorTotal != null ? valorTotal : BigDecimal.ZERO;
        BigDecimal recebido = calcularTotalRecebido();
        BigDecimal saldo = total.subtract(recebido);

        BigDecimal troco = saldo.compareTo(BigDecimal.ZERO) < 0 ? saldo.abs() : BigDecimal.ZERO;
        BigDecimal saldoRestante = saldo.compareTo(BigDecimal.ZERO) > 0 ? saldo : BigDecimal.ZERO;

        lblTotalReceber.setText("R$ " + FormaterCurrency.fieldFormated(total));
        lblTotalRecebido.setText("R$ " + FormaterCurrency.fieldFormated(recebido));
        lblSaldoRestante.setText("R$ " + FormaterCurrency.fieldFormated(saldoRestante));

        if (total.compareTo(BigDecimal.ZERO) > 0) {
            double pct = recebido.divide(total, 4, RoundingMode.HALF_UP).doubleValue();
            progressoPagamento.setProgress(Math.min(1.0, pct));
        } else {
            progressoPagamento.setProgress(0);
        }

        if (troco.compareTo(BigDecimal.ZERO) > 0) {
            lblTroco.setText("R$ " + FormaterCurrency.fieldFormated(troco));
            cardTroco.setVisible(true);
            cardTroco.setManaged(true);
        } else {
            cardTroco.setVisible(false);
            cardTroco.setManaged(false);
        }

        renderizarListaDePagamentos();

        boolean podeConfirmar = saldo.compareTo(BigDecimal.ZERO) <= 0 && !pagamentosAdicionados.isEmpty();
        btnConfirmar.setDisable(!podeConfirmar);
    }

    private void renderizarListaDePagamentos() {
        listaPagamentos.getChildren().clear();

        if (pagamentosAdicionados.isEmpty()) {
            lblPagamentosVazio.setVisible(true);
            lblPagamentosVazio.setManaged(true);
            return;
        }

        lblPagamentosVazio.setVisible(false);
        lblPagamentosVazio.setManaged(false);

        for (PagamentoAdicionado p : pagamentosAdicionados) {
            listaPagamentos.getChildren().add(criarLinhaPagamento(p));
        }
    }

    private HBox criarLinhaPagamento(PagamentoAdicionado p) {
        HBox row = new HBox(12);
        row.setStyle("-fx-padding: 10 12; -fx-background-color: #f8f9fa; -fx-background-radius: 6;"
                + "-fx-alignment: center-left;");

        // Ícone da forma de pagamento
        FontIcon icone = new FontIcon(
                p.forma.getIcone() != null ? p.forma.getIcone() : "mdi2c-cash");
        icone.setIconSize(18);
        icone.setStyle("-fx-icon-color: #555;");

        Label desc = new Label(p.forma.getDescricao());
        desc.setStyle("-fx-text-fill: #333; -fx-font-size: 15px; -fx-font-weight: 600;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label valor = new Label("R$ " + FormaterCurrency.fieldFormated(p.valor));
        valor.setStyle("-fx-text-fill: #538649; -fx-font-size: 17px; -fx-font-weight: bold;");

        Button btnRemover = new Button("×");
        btnRemover.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #c9724f; " +
                        "-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 0 8; -fx-cursor: hand;");
        btnRemover.setOnAction(e -> removerPagamento(p));
        btnRemover.setFocusTraversable(false);

        row.getChildren().addAll(icone, desc, spacer, valor, btnRemover);
        return row;
    }

    private BigDecimal calcularTotalRecebido() {
        return pagamentosAdicionados.stream()
                .map(p -> p.valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularSaldoRestante() {
        BigDecimal total = valorTotal != null ? valorTotal : BigDecimal.ZERO;
        BigDecimal saldo = total.subtract(calcularTotalRecebido());
        return saldo.compareTo(BigDecimal.ZERO) > 0 ? saldo : BigDecimal.ZERO;
    }

    // ====================================================================
    //  DESCONTO / ACRÉSCIMO na venda total
    // ====================================================================

    private void aplicarDescontoAcrescimo(TipoOperacaoDescontoAcrescimo tipo) {
        PdvVendaCabecalho cupom = sessaoPdv.getCupomAtivoOuNulo();
        if (cupom == null) {
            LOGGER.warn("Cupom não disponível na sessão ao aplicar {}", tipo);
            return;
        }

        PdvVendaCabecalho atualizado = cabecalhoRepository.findById(cupom.getId()).orElse(null);
        if (atualizado == null) return;

        BigDecimal valorBase = atualizado.getValorTotalDocumento() != null
                ? atualizado.getValorTotalDocumento()
                : BigDecimal.ZERO;

        if (valorBase.compareTo(BigDecimal.ZERO) <= 0) {
            LOGGER.warn("Subtotal líquido inválido para aplicar {}: {}", tipo, valorBase);
            return;
        }

        stageManager.showDescontoAcrescimoVenda(cupom, valorBase, tipo, () -> {
            recarregarDescontoAcrescimoDoBanco();
            atualizarTela();
        });
    }

    private void recarregarDescontoAcrescimoDoBanco() {
        PdvVendaCabecalho cupom = sessaoPdv.getCupomAtivoOuNulo();
        if (cupom == null) return;

        PdvVendaCabecalho atualizado = cabecalhoRepository.findById(cupom.getId()).orElse(null);
        if (atualizado == null) return;

        BigDecimal desconto = atualizado.getValorDesconto() != null
                ? atualizado.getValorDesconto() : BigDecimal.ZERO;
        BigDecimal acrescimo = atualizado.getValorAcrescimo() != null
                ? atualizado.getValorAcrescimo() : BigDecimal.ZERO;
        BigDecimal bruto = atualizado.getValorTotalProdutos() != null
                ? atualizado.getValorTotalProdutos() : BigDecimal.ZERO;
        BigDecimal total = atualizado.getValorTotalDocumento() != null
                ? atualizado.getValorTotalDocumento() : valorTotal;

        this.valorTotal = total;

        lblSubtotal.setText("R$ " + FormaterCurrency.fieldFormated(bruto));
        lblDesconto.setText("R$ " + FormaterCurrency.fieldFormated(desconto));
        lblAcrescimo.setText("R$ " + FormaterCurrency.fieldFormated(acrescimo));

        boolean temDesconto = desconto.compareTo(BigDecimal.ZERO) > 0;
        boolean temAcrescimo = acrescimo.compareTo(BigDecimal.ZERO) > 0;

        linhaDesconto.setVisible(temDesconto);
        linhaDesconto.setManaged(temDesconto);
        linhaAcrescimo.setVisible(temAcrescimo);
        linhaAcrescimo.setManaged(temAcrescimo);
    }

    // ====================================================================
    //  FINALIZAR / CANCELAR
    // ====================================================================

    private void cancelarEncerramento() {
        try {
            stageManager.showEncerramentoParaVenda();
        } catch (Exception ex) {
            LOGGER.error("Erro ao cancelar encerramento", ex);
        }
    }

    private void finalizarVenda() {
        if (pagamentosAdicionados.isEmpty()) {
            LOGGER.warn("Tentativa de finalizar sem formas de pagamento");
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Adicione ao menos uma forma de pagamento antes de confirmar.");
            return;
        }

        BigDecimal recebido = calcularTotalRecebido();
        if (recebido.compareTo(valorTotal) < 0) {
            BigDecimal restante = valorTotal.subtract(recebido);
            LOGGER.warn("Saldo restante: R$ {}", restante);
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Saldo restante de R$ " + FormaterCurrency.fieldFormated(restante)
                            + ".\nAdicione mais pagamentos antes de confirmar.");
            return;
        }

        PdvVendaCabecalho cupom = sessaoPdv.getCupomAtivoOuNulo();
        if (cupom == null) {
            LOGGER.error("Cupom ativo nulo ao finalizar venda");
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Não foi possível identificar o cupom da venda.\nCancele e tente novamente.");
            return;
        }

        // Mapeia os PagamentoAdicionado (DTO da UI) → PagamentoLancado (DTO do service)
        List<PagamentoLancado> pagamentosParaService;
        try {
            pagamentosParaService = pagamentosAdicionados.stream()
                    .map(this::mapearParaPagamentoLancado)
                    .collect(Collectors.toList());
        } catch (IllegalStateException ex) {
            LOGGER.error("Falha ao mapear pagamentos", ex);
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Erro ao preparar pagamentos: " + ex.getMessage());
            return;
        }

        // Persiste tudo em transação atômica
        try {
            PdvVendaCabecalho cupomFinalizado =
                    vendaService.finalizarVenda(cupom, pagamentosParaService);

            LOGGER.info("Venda finalizada com sucesso. Cupom #{}, Total R$ {}, Recebido R$ {}, Troco R$ {}",
                    cupomFinalizado.getId(),
                    cupomFinalizado.getValorTotalDocumento(),
                    cupomFinalizado.getValorRecebido(),
                    cupomFinalizado.getValorTroco());

            // Fase 1.2 — se o cupom veio de uma conta importada (mesa/comanda),
            // vincula a conta como F (Fechada) e marca o idPdvVendaCabecalho.
            // Idempotente: se cupom não tem conta, não faz nada.
            try {
                integracaoContaService.vincularAposFinalizar(cupomFinalizado);
            } catch (Exception ex) {
                LOGGER.warn("Falha ao vincular conta aberta ao cupom #{} finalizado — "
                                + "a venda foi concluída, mas a mesa/comanda pode ter ficado "
                                + "presa em status E; operador deve verificar manualmente",
                        cupomFinalizado.getId(), ex);
            }

            // Emissão fiscal (NFC-e/SAT/MFE via ACBr Monitor) — nunca bloqueia
            // o fechamento da venda, que já está paga e persistida. Se falhar,
            // fica PENDENTE_REPROCESSAMENTO (ver EmissaoFiscalService).
            try {
                ResultadoEmissaoFiscal resultadoFiscal =
                        emissaoFiscalService.emitir(sessaoPdv.getCaixaAtual(), cupomFinalizado);
                if (!resultadoFiscal.foiEmitido()) {
                    LOGGER.warn("Cupom #{} finalizado sem emissão fiscal (status={}): {}",
                            cupomFinalizado.getId(), resultadoFiscal.getStatus(), resultadoFiscal.getMensagem());
                }
            } catch (Exception ex) {
                // EmissaoFiscalService já trata falhas internamente e não deveria
                // lançar — este catch é só uma rede de segurança extra.
                LOGGER.error("Erro inesperado na emissão fiscal do cupom #{}", cupomFinalizado.getId(), ex);
            }

            // Fidelidade — aviso não-bloqueante se o cliente da venda atingiu a meta.
            // Usa buscarComCliente (JOIN FETCH) porque cupomFinalizado já está fora da
            // transação do VendaService — acessar getCliente() direto lançaria
            // LazyInitializationException se o FK não for nulo.
            try {
                PdvVendaCabecalho comCliente = cabecalhoRepository
                        .buscarComCliente(cupomFinalizado.getId()).orElse(null);
                if (comCliente != null && comCliente.getCliente() != null) {
                    FidelidadeService.Resultado fidelidade = fidelidadeService.avaliar(comCliente.getCliente());
                    String aviso = fidelidade.mensagemOuNull(comCliente.getCliente().getNome());
                    if (aviso != null) {
                        mostrarAlerta(Alert.AlertType.INFORMATION, aviso);
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("Falha ao avaliar fidelidade do cupom #{}", cupomFinalizado.getId(), ex);
            }

            // Impressão térmica do cupom + abertura de gaveta (fase 7).
            // Não bloqueia o fechamento da venda se falhar (impressora
            // desligada/desconfigurada) — ImpressaoService trata isso internamente.
            try {
                impressaoService.imprimirCupom(sessaoPdv.getCaixaAtual(), cupomFinalizado);
            } catch (Exception ex) {
                LOGGER.error("Falha ao imprimir cupom #{}", cupomFinalizado.getId(), ex);
            }

            stageManager.resetarPdv();
            stageManager.showPdv4Scene();

        } catch (Exception e) {
            LOGGER.error("Erro ao finalizar venda", e);
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Não foi possível finalizar a venda.\n\nMotivo: " + e.getMessage()
                            + "\n\nA venda NÃO foi concluída.");
        }
    }

    /**
     * Converte um {@code PagamentoAdicionado} (DTO interno da UI) em
     * {@code PagamentoLancado} (DTO do service). Carrega a entidade
     * {@link TipoPagamento} pelo ID que veio no TipoPagamentoDTO.
     */
    private PagamentoLancado mapearParaPagamentoLancado(PagamentoAdicionado p) {
        if (p.forma == null || p.forma.getId() == null) {
            throw new IllegalStateException("Pagamento sem tipo selecionado.");
        }
        TipoPagamento tipo = pdvTipoPagamentoRepository.findById(p.forma.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Tipo de pagamento #" + p.forma.getId() + " não encontrado."));
        return new PagamentoLancado(tipo, p.valor);
    }

    /**
     * Exibe um Alert simples. Centralizado pra evitar repetição.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String mensagem) {
        Alert alert = new Alert(tipo, mensagem);
        alert.setHeaderText(tipo == Alert.AlertType.ERROR ? "Erro" : "Atenção");
        alert.showAndWait();
    }

    // ====================================================================
    //  DTO interno
    // ====================================================================

    private static class PagamentoAdicionado {
        final TipoPagamentoDTO forma;
        final BigDecimal valor;

        PagamentoAdicionado(TipoPagamentoDTO forma, BigDecimal valor) {
            this.forma = forma;
            this.valor = valor;
        }
    }
}