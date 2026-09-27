package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.service.dto.ProdutoVenda;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

@Component
public class CancelarItemController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CancelarItemController.class);
    private final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    @FXML private VBox root;
    @FXML private TableView<ProdutoVenda> tabelaItens;
    @FXML private TableColumn<ProdutoVenda, String> colSeq;
    @FXML private TableColumn<ProdutoVenda, String> colProduto;
    @FXML private TableColumn<ProdutoVenda, String> colQtde;
    @FXML private TableColumn<ProdutoVenda, String> colValor;
    @FXML private TableColumn<ProdutoVenda, String> colStatus;
    @FXML private Button btnConfirmar;
    @FXML private Button btnCancelar;

    private Stage stage;
    private Consumer<ProdutoVenda> onItemCancelado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColunas();
        configurarLinhas();
        configurarAtalhos();
    }

    private void configurarColunas() {
        colSeq.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getItem()));
        colProduto.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDescricao()));
        colQtde.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getQtde().toPlainString()));
        colValor.setCellValueFactory(c ->
                new SimpleStringProperty(currencyFormat.format(c.getValue().getValorTotal())));
        colStatus.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().isCancelado() ? "CANCELADO" : "ATIVO"));
    }

    private void configurarLinhas() {
        tabelaItens.setRowFactory(tv -> {
            TableRow<ProdutoVenda> row = new TableRow<>() {
                @Override
                protected void updateItem(ProdutoVenda item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("");
                    } else if (item.isCancelado()) {
                        setStyle("-fx-background-color: #ffe6e6;");
                    } else {
                        setStyle("");
                    }
                }
            };

            // Duplo clique também confirma
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    confirmar(null);
                }
            });
            return row;
        });
    }

    private void configurarAtalhos() {
        // Registra atalhos ESC e ENTER quando a Scene estiver disponível
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    switch (event.getCode()) {
                        case ESCAPE:
                            voltar(null);
                            event.consume();
                            break;
                        case ENTER:
                            confirmar(null);
                            event.consume();
                            break;
                        default:
                            break;
                    }
                });
            }
        });
    }

    /**
     * Carrega a lista de itens da venda na tabela e seleciona o primeiro item ATIVO.
     */
    public void carregarItens(ObservableList<ProdutoVenda> itens) {
        tabelaItens.setItems(itens);

        // Seleciona o primeiro item que ainda não foi cancelado
        Platform.runLater(() -> {
            for (int i = 0; i < itens.size(); i++) {
                if (!itens.get(i).isCancelado()) {
                    tabelaItens.getSelectionModel().select(i);
                    tabelaItens.scrollTo(i);
                    tabelaItens.requestFocus();
                    break;
                }
            }
        });
    }

    @FXML
    public void confirmar(ActionEvent event) {
        ProdutoVenda selecionado = tabelaItens.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            LOGGER.warn("Nenhum item selecionado");
            return;
        }

        if (selecionado.isCancelado()) {
            LOGGER.warn("Item já está cancelado");
            return;
        }

        // Alert de confirmação
        Alert confirma = new Alert(Alert.AlertType.CONFIRMATION);
        confirma.setTitle("Confirmar Cancelamento");
        confirma.setHeaderText("Item " + selecionado.getItem() + " - " + selecionado.getDescricao());
        confirma.setContentText(String.format(
                "Quantidade: %s%nValor: %s%n%nConfirma o cancelamento?",
                selecionado.getQtde().toPlainString(),
                currencyFormat.format(selecionado.getValorTotal())
        ));
        aplicarEstiloPdv(confirma.getDialogPane());

        Optional<ButtonType> resposta = confirma.showAndWait();
        if (resposta.isEmpty() || resposta.get() != ButtonType.OK) {
            return;
        }

        // Marca o item como cancelado
        selecionado.setCancelado(true);

        // Notifica o controller chamador
        if (onItemCancelado != null) {
            onItemCancelado.accept(selecionado);
        }

        fechar();
    }

    @FXML
    public void voltar(ActionEvent event) {
        fechar();
    }

    private void fechar() {
        if (stage != null) {
            stage.close();
        }
    }

    private void aplicarEstiloPdv(DialogPane pane) {
        try {
            pane.getStylesheets().add(
                    getClass().getResource("/fxml/styles/produtos.css").toExternalForm()
            );
        } catch (Exception e) {
            LOGGER.warn("Não foi possível aplicar o estilo ao dialog: {}", e.getMessage());
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOnItemCancelado(Consumer<ProdutoVenda> handler) {
        this.onItemCancelado = handler;
    }
}