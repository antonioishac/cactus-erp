package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.StageManager;
import br.com.cactuserp.pdv.config.util.DateTimeUtil;
import br.com.cactuserp.pdv.config.util.FormaterCurrency;
import br.com.cactuserp.pdv.entity.Produto;
import br.com.cactuserp.pdv.service.ProdutoService;
import br.com.cactuserp.pdv.service.dto.ProdutoVenda;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class Pdv3Controller implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Pdv3Controller.class);

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    @FXML
    private AnchorPane overlayPane;

    @FXML
    private Label lblRelogio;

    @FXML
    private Label lblDescricao;
    @FXML
    private TextField tfMensagens;
    @FXML
    private TextField tfTotalGeral;
    @FXML
    private TextField tfSubtotal;
    @FXML
    private TextField tfValorTotal;
    @FXML
    private TextField tfValorUnitario;
    @FXML
    private TextField tfQtde;
    @FXML
    private TextField tfCodigo;

    @FXML
    private TableView<ProdutoVenda> tblItems;
    @FXML
    private TableColumn<ProdutoVenda, String> itemColumn;
    @FXML
    private TableColumn<ProdutoVenda, String> codigoColumn;
    @FXML
    private TableColumn<ProdutoVenda, String> descricaoColumn;
    @FXML
    private TableColumn<ProdutoVenda, String> qtdeColumn;
    @FXML
    private TableColumn<ProdutoVenda, String> valorUnitarioColumn;
    @FXML
    private TableColumn<ProdutoVenda, String> valorTotalColumn;

    @FXML
    private VBox rootRodape;

    private final ProdutoService produtoService;
    private final ObservableList<ProdutoVenda> itensVenda = FXCollections.observableArrayList();
    private final StageManager stageManager;

    public Pdv3Controller(StageManager stageManager, ProdutoService produtoService) {
        this.stageManager = stageManager;
        this.produtoService = produtoService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        tblItems.setRowFactory(tv -> new TableRow<ProdutoVenda>() {
            @Override
            protected void updateItem(ProdutoVenda item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (item.isCancelado()) {
                    // Deixa a linha com fundo vermelho e texto branco
                    setStyle("-fx-background-color: #ff4c4c; -fx-text-fill: white;");
                } else {
                    setStyle("");
                }
            }
        });


        Platform.runLater(() -> {

            Scene scene = rootRodape.getScene();
            if (scene != null) {
                setupKeyEvents(scene);
            }
            tfCodigo.clear();
            tfCodigo.requestFocus();
        });

        tfQtde.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.TAB) {
                tfCodigo.requestFocus();
                tfCodigo.selectAll();
            }
        });

        tfCodigo.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.TAB) {
                tfCodigo.requestFocus();
                tfCodigo.selectAll();
            }
        });


        setupTable();
        DateTimeUtil.dateTimeNow(lblRelogio);
        clearPDV();
    }

    private void setupKeyEvents(Scene scene) {
        scene.setOnKeyPressed(event -> {

            switch (event.getCode()) {
                case F1:
                    System.out.println("Identificar cliente...");
                    event.consume();
                    break;
                case F2:
                    //stageManager.showMenuPrincipal();
                    event.consume();
                    break;
                case F3:
                    changeAmount();
                    event.consume();
                    break;
                case F6:
                    stageManager.showPesquisarProdutos(tfCodigo);
                    event.consume();
                    break;
                case F7:
                    encerrarVenda();
                    event.consume();
                    break;
                case F8:
                    cancelarItem();
                    event.consume();
                    break;
            }
        });
    }

    private void encerrarVenda() {
        stageManager.showEncerramentoVenda(FormaterCurrency.fieldUnFormated(tfTotalGeral.getText()));
        //stageManager.showEncerramentoVenda(FormaterCurrency.fieldUnFormated("0"));
    }

    private void cancelarItem() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cancelar Item");
        dialog.setHeaderText("Digite o número do item que deseja cancelar:");
        dialog.setContentText("Número do item:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(numero -> {
            try {
                int index = Integer.parseInt(numero) - 1; // Subtrai 1 se o número digitado começa em 1
                if (index >= 0 && index < tblItems.getItems().size()) {
                    ProdutoVenda item = tblItems.getItems().get(index);
                    item.setCancelado(true);
                    tblItems.refresh(); // Atualiza a visualização
                }
            } catch (NumberFormatException e) {
                mostrarAlertaErro("Número inválido. Digite um número inteiro.");
            }
        });

        tfSubtotal.setText(FormaterCurrency.fieldFormated(calculateSubTotal(itensVenda)));
        tfTotalGeral.setText(FormaterCurrency.fieldFormated(calculateSubTotal(itensVenda)));
    }

    private void mostrarAlertaErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void setupTable() {
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("item"));
        codigoColumn.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        descricaoColumn.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        qtdeColumn.setCellValueFactory(new PropertyValueFactory<>("qtde"));

        valorUnitarioColumn.setCellValueFactory(param ->
                new SimpleStringProperty(currencyFormat.format(param.getValue().getValorUnitario())));

        valorTotalColumn.setCellValueFactory(param ->
                new SimpleStringProperty(currencyFormat.format(param.getValue().getValorTotal())));

    }

    @FXML
    private void focusProduct(ActionEvent event) {
        tfCodigo.requestFocus();
        tfCodigo.selectAll();
    }

    private void clearPDV() {
        tblItems.setItems(itensVenda);
        lblDescricao.setText("");
        tfCodigo.clear();
        tfCodigo.requestFocus();

        tfQtde.setTextFormatter(new TextFormatter<>(FormaterCurrency.filterDecimal()));
        tfQtde.setText("1");


        tfValorUnitario.clear();
        tfValorTotal.clear();
        tfSubtotal.clear();
        tfTotalGeral.clear();

        tfValorUnitario.setEditable(false);
        tfValorUnitario.setFocusTraversable(false);

        tfValorTotal.setEditable(false);
        tfValorTotal.setFocusTraversable(false);

        tfSubtotal.setEditable(false);
        tfSubtotal.setFocusTraversable(false);

        tfTotalGeral.setEditable(false);
        tfTotalGeral.setFocusTraversable(false);

        lblDescricao.setText("CAIXA LIVE");
    }

    @FXML
    public void addProduct(ActionEvent event) {
        if (tfCodigo.getText().trim().isBlank()) {
            tfCodigo.requestFocus();
            tfCodigo.selectAll();
            return;
        }

        var product = produtoService.buscarPorCodigo(tfCodigo.getText());

        if (product.isEmpty()) {
            product = produtoService.buscarPorCodigoBalanca(Integer.valueOf(tfCodigo.getText()));
        }

        product.ifPresentOrElse(this::adicionarProdutoVenda, this::produtoNaoEncontrado);
    }

    private void adicionarProdutoVenda(Produto prod) {
        var qtde = new BigDecimal(tfQtde.getText().replace(",", "."));

        ProdutoVenda produtoVenda = new ProdutoVenda(
                String.valueOf(itensVenda.size() + 1),
                prod.getGtin() == null ? String.valueOf(prod.getCodigoBalanca()) : prod.getGtin(),
                prod.getDescricaoPdv(),
                qtde,
                prod.getValorVenda(),
                calculateTotalValue(String.valueOf(qtde), prod.getValorVenda()),
                false
        );
        itensVenda.add(produtoVenda);

        // Preenche a descricao no topo
        lblDescricao.setText(prod.getNome());

        // Preenche o valor unitário
        tfValorUnitario.setText(FormaterCurrency.fieldFormated(prod.getValorVenda()));

        // Preenche o valor total do item pela quantidade
        var valorTotalProduto = calculateTotalValue(tfQtde.getText(), FormaterCurrency.fieldUnFormated(tfValorUnitario.getText()));
        tfValorTotal.setText(FormaterCurrency.fieldFormated(valorTotalProduto));


        // Preenche o campo subTotal
        var subtotal = calculateSubTotal(itensVenda);
        tfSubtotal.setText(FormaterCurrency.fieldFormated(subtotal));

        // Preenche o campo Total Geral, depois precisa ver como vai ficar com o desconto e acrescimo
        var totalGeral = calculateSubTotal(itensVenda);
        tfTotalGeral.setText(FormaterCurrency.fieldFormated(totalGeral));

        tfCodigo.clear();
        tfQtde.setText("1");
        tfCodigo.requestFocus();

        tblItems.scrollTo(itensVenda.size() - 1);
        tblItems.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }


    private BigDecimal calculateTotalValue(String amount, BigDecimal unitValue) {
        return unitValue.multiply(new BigDecimal(amount.replace(",", "."))).setScale(2, RoundingMode.DOWN);
    }

    private BigDecimal calculateSubTotal(ObservableList<ProdutoVenda> items) {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (ProdutoVenda item : items) {
            valorTotal = valorTotal.add(item.isCancelado() ? BigDecimal.ZERO : item.getValorTotal());
        }

        return valorTotal.setScale(2, RoundingMode.DOWN);
    }

    private void produtoNaoEncontrado() {
        LOGGER.error("Produto não encontrado");
        tfCodigo.clear();
        tfQtde.setText("1");
        tfCodigo.requestFocus();
        lblDescricao.setText("");
    }


    private void changeAmount() {
        Platform.runLater(() -> {
            tfQtde.requestFocus();
            tfQtde.selectAll();
        });
    }
}
