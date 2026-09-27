package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.entity.Produto;
import br.com.cactuserp.pdv.service.ProdutoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Component
public class PdvViewController {

    @FXML private TextField codigoField;
    @FXML private TableView<Produto> itensTable;
    @FXML private TableColumn<Produto, String> codigoColumn;
    @FXML private TableColumn<Produto, String> nomeColumn;
    @FXML private TableColumn<Produto, String> precoColumn;
    @FXML private TableColumn<Produto, Integer> quantidadeColumn;
    @FXML private TableColumn<Produto, String> subtotalColumn;
    @FXML private Label totalLabel;
    @FXML private Button adicionarButton;
    @FXML private Button finalizarButton;
    @FXML private Button cancelarButton;

    private final ProdutoService produtoService;
    private final ObservableList<Produto> itensVenda = FXCollections.observableArrayList();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public PdvViewController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @FXML
    private void initialize() {
        setupTable();
        setupButtons();

        codigoField.setOnAction(e -> adicionarProduto());
        itensTable.setItems(itensVenda);
        atualizarTotal();
    }

    private void setupTable() {
        codigoColumn.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));

        precoColumn.setCellValueFactory(param ->
                new SimpleStringProperty(currencyFormat.format(param.getValue().getValorVenda())));

        quantidadeColumn.setCellValueFactory(new PropertyValueFactory<>("estoque"));

        subtotalColumn.setCellValueFactory(param -> {
            BigDecimal subtotal = param.getValue().getValorVenda().multiply(param.getValue().getQuantidadeEstoque());

            return new SimpleStringProperty(currencyFormat.format(subtotal));
        });
    }

    private void setupButtons() {
        adicionarButton.setOnAction(e -> adicionarProduto());
        finalizarButton.setOnAction(e -> finalizarVenda());
        cancelarButton.setOnAction(e -> cancelarVenda());
    }

    private void adicionarProduto() {
        String codigo = codigoField.getText().trim();
        if (codigo.isEmpty()) return;

        produtoService.buscarPorCodigo(codigo).ifPresentOrElse(
                produto -> {
                    // Verifica se o produto já está na lista
                    boolean encontrado = false;
                    for (Produto item : itensVenda) {
                        if (item.getGtin().equals(codigo)) {
                            item.setQuantidadeEstoque(item.getQuantidadeEstoque().add(BigDecimal.ONE));
                            encontrado = true;
                            break;
                        }
                    }

                    if (!encontrado) {
                        Produto itemVenda = new Produto();
                        itemVenda.setGtin(produto.getGtin());
                        itemVenda.setDescricao(produto.getDescricao());
                        itemVenda.setValorVenda(produto.getValorVenda());
                        itemVenda.setQuantidadeEstoque(BigDecimal.ONE); // Quantidade
                        itensVenda.add(itemVenda);
                    }

                    itensTable.refresh();
                    atualizarTotal();
                    codigoField.clear();
                },
                () -> showAlert("Produto não encontrado!", Alert.AlertType.WARNING)
        );
    }

    private void atualizarTotal() {
        BigDecimal total = itensVenda.stream()
                .map(item -> item.getValorVenda().multiply(item.getQuantidadeEstoque()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalLabel.setText("TOTAL: " + currencyFormat.format(total));
    }

    private void finalizarVenda() {
        if (itensVenda.isEmpty()) {
            showAlert("Nenhum item na venda!", Alert.AlertType.WARNING);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Finalizar Venda");
        alert.setHeaderText("Confirmar venda?");
        alert.setContentText("Total: " + totalLabel.getText());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                itensVenda.clear();
                atualizarTotal();
                showAlert("Venda finalizada com sucesso!", Alert.AlertType.INFORMATION);
            }
        });
    }

    private void cancelarVenda() {
        if (!itensVenda.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cancelar Venda");
            alert.setHeaderText("Deseja cancelar a venda atual?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    itensVenda.clear();
                    atualizarTotal();
                }
            });
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}