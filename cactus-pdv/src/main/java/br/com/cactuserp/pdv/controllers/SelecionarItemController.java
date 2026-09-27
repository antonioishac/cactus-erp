package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.service.dto.ProdutoVenda;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Tela genérica de seleção de item da venda em andamento.
 *
 * <p>Usada por qualquer fluxo que precise pedir ao operador para escolher
 * um item específico do cupom — desconto, acréscimo, cancelamento, etc.
 *
 * <p>Configurável por contexto:
 * <ul>
 *   <li>Título e subtítulo dinâmicos</li>
 *   <li>Cor do header (variantes via CSS: desconto/acrescimo/cancelar/neutro)</li>
 *   <li>Texto do botão de confirmar</li>
 *   <li>Callback que recebe o item escolhido</li>
 * </ul>
 *
 * <p>Atalhos: ESC cancela, ENTER confirma, ↑↓ navega.
 *
 * <p>Itens já cancelados são automaticamente filtrados — não aparecem na lista.
 */
@Component
public class SelecionarItemController {

    /** Variantes de cor disponíveis para o header e botão de confirmar. */
    public enum Variante {
        DESCONTO("si-header-desconto",  "si-btn-confirmar-desconto"),
        ACRESCIMO("si-header-acrescimo", "si-btn-confirmar-acrescimo"),
        CANCELAR("si-header-cancelar",  "si-btn-confirmar-cancelar"),
        NEUTRO("si-header-neutro",      "si-btn-confirmar-neutro");

        final String classeHeader;
        final String classeBotao;

        Variante(String classeHeader, String classeBotao) {
            this.classeHeader = classeHeader;
            this.classeBotao = classeBotao;
        }
    }

    private static final NumberFormat CURRENCY_FORMAT =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    @FXML private VBox header;
    @FXML private Label lblTitulo;
    @FXML private Label lblSubtitulo;
    @FXML private ListView<ProdutoVenda> listView;
    @FXML private Button btnCancelar;
    @FXML private Button btnConfirmar;

    private Stage stage;
    private Consumer<ProdutoVenda> onSelecionado;

    // =============== Inicialização via StageManager =====================

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOnSelecionado(Consumer<ProdutoVenda> callback) {
        this.onSelecionado = callback;
    }

    /**
     * Configura o conteúdo da tela.
     *
     * @param titulo         título grande (ex: "Selecionar item")
     * @param subtitulo      contexto (ex: "Aplicar desconto no item selecionado")
     * @param textoConfirmar texto do botão direito (ex: "[ENTER] Selecionar")
     * @param variante       cor do header e botão
     * @param itens          itens da venda (apenas não-cancelados são exibidos)
     */
    public void configurar(String titulo,
                           String subtitulo,
                           String textoConfirmar,
                           Variante variante,
                           List<ProdutoVenda> itens) {

        lblTitulo.setText(titulo);
        lblSubtitulo.setText(subtitulo);
        btnConfirmar.setText(textoConfirmar);

        // Aplica variante (cor) ao header e botão
        // Limpa variantes anteriores
        header.getStyleClass().removeIf(c -> c.startsWith("si-header-"));
        btnConfirmar.getStyleClass().removeIf(c -> c.startsWith("si-btn-confirmar-")
                && !c.equals("si-btn-confirmar"));

        header.getStyleClass().add(variante.classeHeader);
        btnConfirmar.getStyleClass().add(variante.classeBotao);

        // Filtra itens cancelados
        ObservableList<ProdutoVenda> ativos = FXCollections.observableArrayList();
        for (ProdutoVenda p : itens) {
            if (!p.isCancelado()) ativos.add(p);
        }
        listView.setItems(ativos);

        // Cell factory — renderização customizada
        listView.setCellFactory(lv -> new ItemCell());

        // Seleção inicial — primeiro item
        if (!ativos.isEmpty()) {
            listView.getSelectionModel().select(0);
        }

        // Double click = seleciona
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleConfirmar();
            }
        });

        // Captura ENTER na lista
        listView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleConfirmar();
                event.consume();
            }
        });

        Platform.runLater(listView::requestFocus);
    }

    // =============== Handlers ==========================================

    @FXML
    private void handleConfirmar() {
        ProdutoVenda escolhido = listView.getSelectionModel().getSelectedItem();
        if (escolhido == null) return;

        if (onSelecionado != null) {
            onSelecionado.accept(escolhido);
        }
        fechar();
    }

    @FXML
    private void handleCancelar() {
        fechar();
    }

    private void fechar() {
        if (stage != null) stage.close();
    }

    // =============== Cell customizada =================================

    private static class ItemCell extends ListCell<ProdutoVenda> {
        private final VBox container = new VBox(2);
        private final Label lblSeq = new Label();
        private final Label lblDescricao = new Label();

        ItemCell() {
            lblSeq.getStyleClass().add("si-item-seq");
            container.getChildren().addAll(lblSeq, lblDescricao);
            container.getStyleClass().add("si-item");
            container.setMaxWidth(Double.MAX_VALUE);
        }

        @Override
        protected void updateItem(ProdutoVenda p, boolean empty) {
            super.updateItem(p, empty);

            if (empty || p == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            lblSeq.setText("Item " + p.getItem());

            String qtdeFormatada = formatarQtde(p.getQtde());
            String descricao = String.format("%s · %s · %s",
                    p.getDescricao(),
                    qtdeFormatada,
                    CURRENCY_FORMAT.format(p.getValorTotal()));
            lblDescricao.setText(descricao);

            // Marca visualmente o item selecionado
            container.getStyleClass().removeIf(c ->
                    c.equals("si-item") || c.equals("si-item-selecionado"));
            lblDescricao.getStyleClass().removeIf(c ->
                    c.equals("si-item-descricao") || c.equals("si-item-descricao-normal"));

            if (isSelected()) {
                container.getStyleClass().add("si-item-selecionado");
                lblDescricao.getStyleClass().add("si-item-descricao");
            } else {
                container.getStyleClass().add("si-item");
                lblDescricao.getStyleClass().add("si-item-descricao-normal");
            }

            setGraphic(container);
            setText(null);
        }

        private String formatarQtde(BigDecimal qtde) {
            if (qtde == null) return "0";
            if (qtde.stripTrailingZeros().scale() <= 0) {
                return qtde.toBigInteger() + " un";
            }
            return qtde.toPlainString().replace(".", ",") + " kg";
        }
    }
}