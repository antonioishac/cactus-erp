package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.config.util.AutorizacaoGerencialDialog;
import br.com.cactuserp.pdv.config.util.FormaterCurrency;
import br.com.cactuserp.pdv.entity.PdvConfiguracao;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;
import br.com.cactuserp.pdv.entity.TipoEntradaValor;
import br.com.cactuserp.pdv.entity.TipoOperacaoDescontoAcrescimo;
import br.com.cactuserp.pdv.service.DescontoAcrescimoService;
import br.com.cactuserp.pdv.service.DescontoAcrescimoService.ResultadoCalculo;
import br.com.cactuserp.pdv.service.LoginService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Controller da modal {@code DescontoAcrescimo.fxml}.
 *
 * <p>Suporta dois <b>escopos</b> de aplicação:
 * <ul>
 *   <li>{@link Escopo#ITEM} — aplica em um item específico do cupom</li>
 *   <li>{@link Escopo#VENDA_TOTAL} — aplica no total do cupom, redistribuindo
 *       proporcionalmente entre os itens (PAF-NFC-e)</li>
 * </ul>
 *
 * <p>A configuração é feita pelo {@code StageManager} antes de exibir.
 */
@Component
public class DescontoAcrescimoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DescontoAcrescimoController.class);

    public enum Escopo { ITEM, VENDA_TOTAL }

    @FXML private VBox header;
    @FXML private Label lblTitulo;
    @FXML private Label lblInfoItem;
    @FXML private Button btnValor;
    @FXML private Button btnPercentual;
    @FXML private Label lblCampoValor;
    @FXML private TextField tfValor;
    @FXML private Label lblAvisoLimite;
    @FXML private Label lblValorOriginal;
    @FXML private Label lblOperacaoLabel;
    @FXML private Label lblOperacaoValor;
    @FXML private Label lblTotalFinal;
    @FXML private Button btnCancelar;
    @FXML private Button btnAplicar;

    private final DescontoAcrescimoService descontoAcrescimoService;
    private final LoginService loginService;
    private final SessaoPdv sessaoPdv;

    private Stage stage;
    private Escopo escopo;

    // Estado quando escopo = ITEM
    private PdvVendaDetalhe item;
    private Consumer<PdvVendaDetalhe> onItemAplicado;

    // Estado quando escopo = VENDA_TOTAL
    private PdvVendaCabecalho cupom;
    private BigDecimal valorBaseVenda;
    private Runnable onVendaAplicada;

    // Comum
    private TipoOperacaoDescontoAcrescimo tipo;
    private TipoEntradaValor tipoEntrada = TipoEntradaValor.VALOR_RS;
    private BigDecimal valorBase;
    private PdvConfiguracao config;

    public DescontoAcrescimoController(DescontoAcrescimoService descontoAcrescimoService,
                                       LoginService loginService,
                                       SessaoPdv sessaoPdv) {
        this.descontoAcrescimoService = descontoAcrescimoService;
        this.loginService = loginService;
        this.sessaoPdv = sessaoPdv;
    }

    // ====== Inicialização ===============================================

    public void setStage(Stage stage) { this.stage = stage; }

    public void setOnItemAplicado(Consumer<PdvVendaDetalhe> callback) {
        this.onItemAplicado = callback;
    }

    public void setOnVendaAplicada(Runnable callback) {
        this.onVendaAplicada = callback;
    }

    /**
     * Configura para aplicar em um ITEM específico.
     */
    public void configurarItem(PdvVendaDetalhe item, TipoOperacaoDescontoAcrescimo tipo) {
        this.escopo = Escopo.ITEM;
        this.item = item;
        this.tipo = tipo;
        this.valorBase = item.getValorUnitario().multiply(item.getQuantidade());
        this.config = descontoAcrescimoService.obterConfiguracao(sessaoPdv.getCaixaAtual());

        atualizarHeader();
        atualizarLabelOperacao();
        atualizarLabelCampoValor();
        atualizarAvisoLimite();
        atualizarPreview();

        tfValor.textProperty().addListener((obs, old, novo) -> atualizarPreview());

        Platform.runLater(tfValor::requestFocus);
    }

    /**
     * Configura para aplicar na VENDA TOTAL.
     *
     * @param cupom         cupom alvo
     * @param valorBaseVenda subtotal líquido (já considera descontos de item via F6)
     * @param tipo          DESCONTO ou ACRESCIMO
     */
    public void configurarVendaTotal(PdvVendaCabecalho cupom,
                                     BigDecimal valorBaseVenda,
                                     TipoOperacaoDescontoAcrescimo tipo) {
        this.escopo = Escopo.VENDA_TOTAL;
        this.cupom = cupom;
        this.valorBaseVenda = valorBaseVenda;
        this.tipo = tipo;
        this.valorBase = valorBaseVenda;
        this.config = descontoAcrescimoService.obterConfiguracao(sessaoPdv.getCaixaAtual());

        atualizarHeader();
        atualizarLabelOperacao();
        atualizarLabelCampoValor();
        atualizarAvisoLimiteVenda();
        atualizarPreview();

        tfValor.textProperty().addListener((obs, old, novo) -> atualizarPreview());

        Platform.runLater(tfValor::requestFocus);
    }

    // ====== Aparência por tipo (cor do header, textos) ==================

    private void atualizarHeader() {
        header.getStyleClass().removeIf(c ->
                c.equals("dadc-header-desconto") || c.equals("dadc-header-acrescimo"));

        if (tipo == TipoOperacaoDescontoAcrescimo.DESCONTO) {
            header.getStyleClass().add("dadc-header-desconto");
            lblTitulo.setText(escopo == Escopo.ITEM
                    ? "Aplicar Desconto"
                    : "Aplicar Desconto na Venda");
        } else {
            header.getStyleClass().add("dadc-header-acrescimo");
            lblTitulo.setText(escopo == Escopo.ITEM
                    ? "Aplicar Acréscimo"
                    : "Aplicar Acréscimo na Venda");
        }

        if (escopo == Escopo.ITEM) {
            String descricao = item.getProduto() != null
                    ? item.getProduto().getDescricaoPdv()
                    : "Produto";
            lblInfoItem.setText("Item: " + descricao
                    + " · Subtotal R$ " + FormaterCurrency.fieldFormated(valorBase));
        } else {
            lblInfoItem.setText("Aplicar em toda a venda · Subtotal líquido R$ "
                    + FormaterCurrency.fieldFormated(valorBaseVenda));
        }
    }

    private void atualizarLabelOperacao() {
        if (tipo == TipoOperacaoDescontoAcrescimo.DESCONTO) {
            lblOperacaoLabel.setText("(-) Desconto:");
            lblOperacaoLabel.getStyleClass().removeIf(c -> c.equals("dadc-preview-operacao-acrescimo"));
            if (!lblOperacaoLabel.getStyleClass().contains("dadc-preview-operacao-desconto")) {
                lblOperacaoLabel.getStyleClass().add("dadc-preview-operacao-desconto");
            }
            lblOperacaoValor.getStyleClass().removeIf(c -> c.equals("dadc-preview-operacao-acrescimo"));
            if (!lblOperacaoValor.getStyleClass().contains("dadc-preview-operacao-desconto")) {
                lblOperacaoValor.getStyleClass().add("dadc-preview-operacao-desconto");
            }
        } else {
            lblOperacaoLabel.setText("(+) Acréscimo:");
            lblOperacaoLabel.getStyleClass().removeIf(c -> c.equals("dadc-preview-operacao-desconto"));
            if (!lblOperacaoLabel.getStyleClass().contains("dadc-preview-operacao-acrescimo")) {
                lblOperacaoLabel.getStyleClass().add("dadc-preview-operacao-acrescimo");
            }
            lblOperacaoValor.getStyleClass().removeIf(c -> c.equals("dadc-preview-operacao-desconto"));
            if (!lblOperacaoValor.getStyleClass().contains("dadc-preview-operacao-acrescimo")) {
                lblOperacaoValor.getStyleClass().add("dadc-preview-operacao-acrescimo");
            }
        }
    }

    private void atualizarLabelCampoValor() {
        String tipoTexto = tipo == TipoOperacaoDescontoAcrescimo.DESCONTO
                ? "DESCONTO" : "ACRÉSCIMO";
        if (tipoEntrada == TipoEntradaValor.VALOR_RS) {
            lblCampoValor.setText("VALOR DO " + tipoTexto + " (R$)");
            tfValor.setPromptText("0,00");
        } else {
            lblCampoValor.setText("PERCENTUAL DE " + tipoTexto + " (%)");
            tfValor.setPromptText("0,00 %");
        }
    }

    private void atualizarAvisoLimite() {
        // Escopo ITEM — limites de item
        BigDecimal limitePct = (tipo == TipoOperacaoDescontoAcrescimo.DESCONTO)
                ? config.limiteDescontoItemPercentualOuPadrao()
                : config.limiteAcrescimoItemPercentualOuPadrao();

        BigDecimal limiteValor = valorBase
                .multiply(limitePct)
                .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);

        lblAvisoLimite.setText(String.format(
                "Limite sem autorização: %s%% (R$ %s). Acima exige Gerente.",
                limitePct.stripTrailingZeros().toPlainString(),
                FormaterCurrency.fieldFormated(limiteValor)
        ));
    }

    private void atualizarAvisoLimiteVenda() {
        // Escopo VENDA_TOTAL — limites de venda (%, R$)
        if (tipo == TipoOperacaoDescontoAcrescimo.DESCONTO) {
            BigDecimal limitePct = config.limiteDescontoVendaPercentualOuPadrao();
            BigDecimal limiteRS  = config.limiteDescontoVendaValorOuPadrao();
            lblAvisoLimite.setText(String.format(
                    "Limite sem autorização: %s%% ou R$ %s. Acima exige Gerente.",
                    limitePct.stripTrailingZeros().toPlainString(),
                    FormaterCurrency.fieldFormated(limiteRS)
            ));
        } else {
            BigDecimal limitePct = config.limiteAcrescimoVendaPercentualOuPadrao();
            lblAvisoLimite.setText(String.format(
                    "Limite sem autorização: %s%%. Acima exige Gerente.",
                    limitePct.stripTrailingZeros().toPlainString()
            ));
        }
    }

    // ====== Toggle R$ / % ===============================================

    @FXML
    private void handleSelecionarValor() {
        tipoEntrada = TipoEntradaValor.VALOR_RS;
        atualizarToggle();
        atualizarLabelCampoValor();
        tfValor.clear();
        atualizarPreview();
        Platform.runLater(tfValor::requestFocus);
    }

    @FXML
    private void handleSelecionarPercentual() {
        tipoEntrada = TipoEntradaValor.PERCENTUAL;
        atualizarToggle();
        atualizarLabelCampoValor();
        tfValor.clear();
        atualizarPreview();
        Platform.runLater(tfValor::requestFocus);
    }

    private void atualizarToggle() {
        if (tipoEntrada == TipoEntradaValor.VALOR_RS) {
            if (!btnValor.getStyleClass().contains("dadc-toggle-btn-ativo")) {
                btnValor.getStyleClass().add("dadc-toggle-btn-ativo");
            }
            btnPercentual.getStyleClass().removeIf(c -> c.equals("dadc-toggle-btn-ativo"));
        } else {
            if (!btnPercentual.getStyleClass().contains("dadc-toggle-btn-ativo")) {
                btnPercentual.getStyleClass().add("dadc-toggle-btn-ativo");
            }
            btnValor.getStyleClass().removeIf(c -> c.equals("dadc-toggle-btn-ativo"));
        }
    }

    // ====== Preview ao vivo ============================================

    private void atualizarPreview() {
        lblValorOriginal.setText("R$ " + FormaterCurrency.fieldFormated(valorBase));

        BigDecimal entrada = parseValorSilencioso();
        if (entrada == null) {
            lblOperacaoValor.setText("— —");
            lblTotalFinal.setText("R$ " + FormaterCurrency.fieldFormated(valorBase));
            return;
        }

        try {
            ResultadoCalculo calc = descontoAcrescimoService.calcular(valorBase, entrada, tipoEntrada);

            String prefixo = tipo == TipoOperacaoDescontoAcrescimo.DESCONTO ? "-" : "+";
            lblOperacaoValor.setText(String.format("%s R$ %s (%s%%)",
                    prefixo,
                    FormaterCurrency.fieldFormated(calc.getValorRS()),
                    calc.getPercentual().stripTrailingZeros().toPlainString()
            ));

            BigDecimal totalFinal;
            if (tipo == TipoOperacaoDescontoAcrescimo.DESCONTO) {
                totalFinal = valorBase.subtract(calc.getValorRS());
            } else {
                totalFinal = valorBase.add(calc.getValorRS());
            }
            lblTotalFinal.setText("R$ " + FormaterCurrency.fieldFormated(totalFinal));

        } catch (Exception ex) {
            lblOperacaoValor.setText("— —");
            lblTotalFinal.setText("R$ " + FormaterCurrency.fieldFormated(valorBase));
        }
    }

    // ====== Aplicar =====================================================

    @FXML
    private void handleAplicar() {
        BigDecimal entrada;
        try {
            entrada = parseValor();
        } catch (NumberFormatException ex) {
            mostrarErro("Valor inválido. Digite um número positivo (ex: 5,00).");
            tfValor.requestFocus();
            return;
        }

        ResultadoCalculo calc;
        try {
            calc = descontoAcrescimoService.calcular(valorBase, entrada, tipoEntrada);
        } catch (Exception ex) {
            mostrarErro(ex.getMessage());
            return;
        }

        if (tipo == TipoOperacaoDescontoAcrescimo.DESCONTO
                && calc.getValorRS().compareTo(valorBase) >= 0) {
            mostrarErro(escopo == Escopo.ITEM
                    ? "Desconto não pode ser maior ou igual ao valor do item."
                    : "Desconto não pode ser maior ou igual ao subtotal da venda.");
            return;
        }

        // Decide se precisa autorização gerencial conforme o escopo
        boolean exige;
        boolean proibido;
        if (escopo == Escopo.ITEM) {
            exige = descontoAcrescimoService.exigeAutorizacaoGerencialEmItem(tipo, calc.getPercentual(), config);
            proibido = descontoAcrescimoService.proibidoMesmoComGerente(tipo, calc.getPercentual(), config);
        } else {
            exige = descontoAcrescimoService.exigeAutorizacaoGerencialEmVenda(tipo, calc.getValorRS(), calc.getPercentual(), config);
            proibido = descontoAcrescimoService.proibidoMesmoComGerenteVenda(tipo, calc.getValorRS(), calc.getPercentual(), config);
        }

        if (proibido) {
            mostrarErro("Esta operação ultrapassa o limite e a política da loja não permite "
                    + "autorização gerencial. Reveja o valor.");
            return;
        }

        PdvOperador autorizador = null;
        if (exige) {
            Optional<PdvOperador> aut = pedirAutorizacaoGerencial(
                    String.format("%s de %s%% excede o limite. Exige autorização gerencial.",
                            tipo.getRotulo(),
                            calc.getPercentual().stripTrailingZeros().toPlainString())
            );
            if (aut.isEmpty()) {
                LOGGER.info("{} ({}) abortado — sem autorização.", tipo, escopo);
                return;
            }
            autorizador = aut.get();
        }

        // Aplica conforme escopo
        try {
            if (escopo == Escopo.ITEM) {
                PdvVendaDetalhe atualizado = descontoAcrescimoService.aplicarEmItem(
                        item, tipo, entrada, tipoEntrada, autorizador);
                LOGGER.info("{} aplicado no item #{}: R$ {} ({}%)",
                        tipo, item.getId(), calc.getValorRS(), calc.getPercentual());
                if (onItemAplicado != null) onItemAplicado.accept(atualizado);

            } else {
                descontoAcrescimoService.aplicarEmVendaTotal(
                        cupom, tipo, entrada, tipoEntrada, autorizador);
                LOGGER.info("{} aplicado na venda #{}: R$ {} ({}%)",
                        tipo, cupom.getId(), calc.getValorRS(), calc.getPercentual());
                if (onVendaAplicada != null) onVendaAplicada.run();
            }
            fechar();

        } catch (Exception ex) {
            LOGGER.error("Falha ao aplicar {} ({})", tipo, escopo, ex);
            mostrarErro("Não foi possível aplicar.\n\nMotivo: " + ex.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        fechar();
    }

    // ====== Helpers =====================================================

    private BigDecimal parseValor() {
        String texto = tfValor.getText() == null
                ? "" : tfValor.getText().trim().replace(",", ".");
        if (texto.isEmpty()) throw new NumberFormatException("vazio");
        BigDecimal valor = new BigDecimal(texto);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NumberFormatException("não positivo");
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