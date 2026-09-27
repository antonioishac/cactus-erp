package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.StageManager;
import br.com.cactuserp.pdv.config.util.FormaterCurrency;
import br.com.cactuserp.pdv.service.ProdutoService;
import br.com.cactuserp.pdv.service.dto.ProdutoVenda;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

@Component
public class PdvController implements Initializable {

    private final StageManager stageManager;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private final ProdutoService produtoService;
    private final ObservableList<ProdutoVenda> itensVenda = FXCollections.observableArrayList();

    @FXML
    private Label lblCliente;
    @FXML
    private Label lblCaixa;
    @FXML
    private Label lblOperador;
    @FXML
    private Label lblCpfCliente;

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
    private TextField tfCodigo;
    @FXML
    private TextField tfQtde;
    @FXML
    private TextField tfValorUnitario;
    @FXML
    private TextField tfValorTotal;
    @FXML
    private ImageView imgProdutos;
    @FXML
    private TextField tfSubTotal;
    @FXML
    private TextField tfTotalGeral;
    @FXML
    private TextField tfMensagem;
    @FXML
    private Label lblIdentificarCliente;
    @FXML
    private Label lblMenuPrincipal;
    @FXML
    private Label lblQtde;
    @FXML
    private Label lblMenuFiscal;
    @FXML
    private Label lblDadosNf;
    @FXML
    private Label lblPesqProduto;
    @FXML
    private Label lblEncerrarVenda;
    @FXML
    private Label lblCancelarItem;
    @FXML
    private Label lblCancelarCupom;
    @FXML
    private Label lblDescontoAcrescimo;
    @FXML
    private Label lblIdentificarVendedor;
    @FXML
    private Label lblSairCaixa;
    @FXML
    private Label lblDescricao;
    @FXML
    private VBox rootRodape;
    @FXML
    private AnchorPane overlayPane;

    public PdvController(StageManager stageManager, ProdutoService produtoService) {
        this.stageManager = stageManager;
        this.produtoService = produtoService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {

            initializePDV();

            Scene scene = rootRodape.getScene();
            if (scene != null) {
                setupKeyEvents(scene);
            }
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
    }

    private void initializePDV() {
        tblItems.setItems(itensVenda);
        setupTable();

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            return (text.matches("\\d*(,\\d*)?")) ? change : null;
        };

        tfCodigo.clear();
        tfCodigo.requestFocus();

        tfQtde.setTextFormatter(new TextFormatter<>(filter));
        tfQtde.setText("1");

        lblDescricao.setText("");

        tfValorUnitario.clear();
        tfValorTotal.clear();
        tfSubTotal.clear();
        tfTotalGeral.clear();

        tfValorUnitario.setEditable(false);
        tfValorUnitario.setFocusTraversable(false);

        tfValorTotal.setEditable(false);
        tfValorTotal.setFocusTraversable(false);

        tfSubTotal.setEditable(false);
        tfSubTotal.setFocusTraversable(false);

        tfTotalGeral.setEditable(false);
        tfTotalGeral.setFocusTraversable(false);

        lblDescricao.setText("CAIXA LIVE");
        //tblItems.setPlaceholder(new Label(""));

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

    private void setupKeyEvents(Scene scene) {
        scene.setOnKeyPressed(event -> {

            switch (event.getCode()) {
                case F1:
                    showIdentifierClient();
                    event.consume();
                    break;
                case F3:
                    changeAmount();
                    event.consume();
                    break;
                case F6:
                    pesquisarProduto();
                    event.consume();
                    break;
                case F7:
                    encerrarVenda();
                    event.consume();
                    break;
            }
        });
    }

    private void encerrarVenda() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EncerramentoVenda.fxml"));
//
//            // Aqui usamos o contexto do Spring
//            loader.setControllerFactory(StageManager.springContext::getBean);
//
//            AnchorPane pagamentoPane = loader.load();
//
//            EncerramentoVendaController controller = loader.getController();
//
//            overlayPane.getChildren().setAll(pagamentoPane);
//            overlayPane.setVisible(true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

// stageManager.showEncerramentoVenda(FormaterCurrency.fieldUnFormated(tfTotalGeral.getText()));

//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EncerramentoVenda.fxml"));
//            loader.setControllerFactory(springContext::getBean);
//            Parent root = loader.load();
//
//            EncerramentoVendaController controller = loader.getController();
//
//            Stage popup = new Stage();
//            popup.initModality(Modality.APPLICATION_MODAL);
//            popup.setTitle("Encerramento Venda");
//            popup.setScene(new Scene(root, 950.0, 600));
//
//            controller.setStage(popup);
//            controller.setValorTotal(valor);
//            Platform.runLater(controller::totalVendaInitialize);
//
//            popup.showAndWait();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private void pesquisarProduto() {
        stageManager.showPesquisarProdutos(tfCodigo);
    }

    @FXML
    private void focusProduct(ActionEvent event) {
        tfCodigo.requestFocus();
        tfCodigo.selectAll();
    }

    @FXML
    private void addProduct(ActionEvent event) {
        if (tfCodigo.getText().trim().isBlank()) {
            tfCodigo.requestFocus();
            tfCodigo.selectAll();
            return;
        }

        var product = produtoService.buscarPorCodigo(tfCodigo.getText());

        // Preenche o objeto para inserir na TableView
        ProdutoVenda produtoVenda = new ProdutoVenda(
                String.valueOf(itensVenda.size() + 1),
                product.get().getGtin(),
                product.get().getNome(),
                new BigDecimal(tfQtde.getText().replace(",", ".")),
                product.get().getValorVenda(),
                calculateTotalValue(tfQtde.getText(), product.get().getValorVenda()), false);

        // Adiciona o item na TableView
        itensVenda.add(produtoVenda);

        // Preenche a descricao no topo
        lblDescricao.setText(product.get().getNome());

        // Preenche o valor unitário
        tfValorUnitario.setText(FormaterCurrency.fieldFormated(product.get().getValorVenda()));

        // Preenche o valor total do item pela quantidade
        var valorTotalProduto = calculateTotalValue(tfQtde.getText(), FormaterCurrency.fieldUnFormated(tfValorUnitario.getText()));
        tfValorTotal.setText(FormaterCurrency.fieldFormated(valorTotalProduto));


        // Preenche o campo subTotal
        var subtotal = calculateSubTotal(itensVenda);
        tfSubTotal.setText(FormaterCurrency.fieldFormated(subtotal));

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
        return unitValue.multiply(new BigDecimal(amount)).setScale(2, RoundingMode.DOWN);
    }

    private BigDecimal calculateSubTotal(ObservableList<ProdutoVenda> items) {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (ProdutoVenda item : items) {
            valorTotal = valorTotal.add(item.getValorTotal());
        }

        return valorTotal.setScale(2, RoundingMode.DOWN);
    }

    private void showIdentifierClient() {
        System.out.println("Identificar cliente...");
    }

    private void changeAmount() {
        Platform.runLater(() -> {
            tfQtde.requestFocus();
            tfQtde.selectAll();
        });
    }
}