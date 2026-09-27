package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.config.util.AutorizacaoGerencialDialog;
import br.com.cactuserp.pdv.config.util.FormaterCurrency;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;
import br.com.cactuserp.pdv.service.DevolucaoService;
import br.com.cactuserp.pdv.service.LoginService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Controller da modal {@code Devolucao.fxml}.
 *
 * <p>Devolução/troca pós-fechamento de um item de venda já finalizada
 * (ver {@link DevolucaoService}). Sempre exige autorização gerencial — sem
 * alçada por valor, ao contrário de desconto/sangria.
 *
 * <p>Aberta a partir da tela de Consulta de Cupom (F12 → Consultar Cupom),
 * escolhendo um item de uma venda com status FINALIZADO.
 */
@Component
public class DevolucaoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevolucaoController.class);

    @FXML private Label lblProduto;
    @FXML private Label lblResumoVenda;
    @FXML private Label lblDisponivel;
    @FXML private TextField tfQuantidade;
    @FXML private TextArea taMotivo;
    @FXML private Label lblInfoOperador;
    @FXML private Button btnConfirmar;

    private final DevolucaoService devolucaoService;
    private final LoginService loginService;
    private final SessaoPdv sessaoPdv;

    private Stage stage;
    private PdvVendaDetalhe item;
    private BigDecimal quantidadeDisponivel;
    private Runnable onConcluido;

    public DevolucaoController(DevolucaoService devolucaoService,
                               LoginService loginService,
                               SessaoPdv sessaoPdv) {
        this.devolucaoService = devolucaoService;
        this.loginService = loginService;
        this.sessaoPdv = sessaoPdv;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Configura a modal para o item selecionado. Deve ser chamado pelo
     * {@code StageManager} logo após o load do FXML.
     */
    public void configurar(PdvVendaDetalhe item, Runnable onConcluido) {
        this.item = item;
        this.onConcluido = onConcluido;
        this.quantidadeDisponivel = devolucaoService.quantidadeDisponivelParaDevolucao(item);

        String descricao = item.getProduto() != null && item.getProduto().getDescricaoPdv() != null
                ? item.getProduto().getDescricaoPdv() : "Item";
        lblProduto.setText(descricao);

        lblResumoVenda.setText("Cupom #" + item.getPdvVendaCabecalho().getId()
                + " · Vendido: " + formatarQtd(item.getQuantidade())
                + " × " + FormaterCurrency.fieldFormated(item.getValorUnitario()));

        lblDisponivel.setText("Disponível para devolução: " + formatarQtd(quantidadeDisponivel));

        tfQuantidade.setText(formatarQtd(quantidadeDisponivel));
        lblInfoOperador.setText("Operador: " + sessaoPdv.getNomeOperadorAtual());

        Platform.runLater(() -> {
            tfQuantidade.requestFocus();
            tfQuantidade.selectAll();
        });
    }

    @FXML
    private void handleCancelar() {
        fechar();
    }

    @FXML
    private void handleConfirmar() {
        BigDecimal quantidade;
        try {
            quantidade = parseQuantidade();
        } catch (NumberFormatException e) {
            mostrarErro("Quantidade inválida.");
            tfQuantidade.requestFocus();
            return;
        }

        if (quantidade.compareTo(quantidadeDisponivel) > 0) {
            mostrarErro("Quantidade maior que a disponível para devolução ("
                    + formatarQtd(quantidadeDisponivel) + ").");
            tfQuantidade.requestFocus();
            return;
        }

        String motivo = taMotivo.getText() == null ? "" : taMotivo.getText().trim();
        if (motivo.length() < 5) {
            mostrarErro("Motivo obrigatório (mínimo 5 caracteres).");
            taMotivo.requestFocus();
            return;
        }

        Optional<PdvOperador> gerente = pedirAutorizacaoGerencial(
                "Devolução exige autorização de Gerente/Supervisor.");
        if (gerente.isEmpty()) {
            LOGGER.info("Devolução abortada — sem autorização.");
            return;
        }

        try {
            devolucaoService.registrarDevolucao(item, quantidade, motivo,
                    sessaoPdv.getOperadorAtual(), gerente.get());
        } catch (Exception ex) {
            LOGGER.error("Falha ao registrar devolução", ex);
            mostrarErro("Não foi possível registrar a devolução.\n\nMotivo: " + ex.getMessage());
            return;
        }

        Alert ok = new Alert(Alert.AlertType.INFORMATION,
                "Devolução de " + formatarQtd(quantidade) + " unid. registrada com sucesso.\n\n"
                        + "O estoque já foi atualizado. O reembolso ao cliente "
                        + "(dinheiro/crédito/estorno) é feito manualmente pela loja.");
        ok.setHeaderText("Devolução concluída");
        ok.showAndWait();

        fechar();
        if (onConcluido != null) onConcluido.run();
    }

    private BigDecimal parseQuantidade() {
        String texto = tfQuantidade.getText() == null ? "" : tfQuantidade.getText().trim().replace(",", ".");
        if (texto.isEmpty()) throw new NumberFormatException("vazio");
        BigDecimal valor = new BigDecimal(texto);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException("não positivo");
        return valor;
    }

    private Optional<PdvOperador> pedirAutorizacaoGerencial(String contextoMensagem) {
        return AutorizacaoGerencialDialog.pedir(loginService, contextoMensagem);
    }

    private String formatarQtd(BigDecimal qtd) {
        if (qtd == null) return "0";
        BigDecimal limpo = qtd.stripTrailingZeros();
        return limpo.scale() <= 0
                ? limpo.toBigInteger().toString()
                : limpo.toPlainString().replace(".", ",");
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
