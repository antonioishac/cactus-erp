package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.util.FormaterCurrency;
import br.com.cactuserp.pdv.service.ProdutoService;
import br.com.cactuserp.pdv.service.dto.PesquisarProdutoDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * Controller da {@code PesquisarProdutos.fxml} — popup de pesquisa de
 * produto por nome (F11 em {@code Pdv4Controller}/{@code LancamentoMesaController}/
 * {@code LancamentoComandaController}, via {@code StageManager.showPesquisarProdutos}).
 *
 * <p>Layout padronizado (2026-08-23) com o mesmo visual de
 * {@code ConsultaProduto.fxml} (header azul, barra de busca com ícone,
 * tabela escura) — reaproveita as classes {@code cp-*}/{@code cc2-*} de
 * {@code consulta-produto.css} em vez de manter um CSS próprio divergente.
 *
 * <p><b>100% operável por teclado</b> (PDV não pode depender de mouse):
 * F11 abre, digitar já filtra, ENTER seleciona o primeiro resultado direto
 * (sem precisar de seta pra baixo), ESC/F10 fecha. Duplo-clique também
 * funciona, mas é sempre alternativa — nunca o único caminho.
 *
 * <p>Precisa cobrir <b>todas as possibilidades de produto</b> — com GTIN,
 * sem GTIN mas com código de balança, vendido por peso ou não, e o caso
 * raro de produto sem nenhum código cadastrado — ver
 * {@link ProdutoService#paraPesquisa}, ponto único que decide qual código
 * volta pro PDV. Uma linha só fica bloqueada pra seleção quando não tem
 * NENHUM jeito de ser adicionada (sem código E não vendida por peso); quem
 * decide o que fazer com um produto vendido por peso (pesar ao vivo, com
 * ou sem código) é {@code Pdv4Controller}, que recebe o
 * {@link PesquisarProdutoDTO} completo, não só um texto de código.
 */
@Component
public class PesquisaProdutoController implements Initializable {

    private final ProdutoService produtoService;

    @FXML private TextField tfDescricao;
    @FXML private Label lblContador;
    @FXML private TableView<PesquisarProdutoDTO> tblProdutos;
    @FXML private TableColumn<PesquisarProdutoDTO, String> codigoColumn;
    @FXML private TableColumn<PesquisarProdutoDTO, String> descricaoColumn;
    @FXML private TableColumn<PesquisarProdutoDTO, String> unidadeColumn;
    @FXML private TableColumn<PesquisarProdutoDTO, String> precoColumn;

    private Stage stage;
    private Consumer<PesquisarProdutoDTO> onProdutoSelecionado;

    public PesquisaProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        codigoColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodigo()));
        descricaoColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescricao()));
        unidadeColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getUnidade() != null ? data.getValue().getUnidade() : "—"));
        precoColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getPrecoVenda() != null
                        ? FormaterCurrency.fieldFormated(data.getValue().getPrecoVenda()) : "—"));

        // Linha só fica bloqueada quando não tem NENHUM jeito de ser
        // adicionada: sem código (GTIN/balança) E não é vendida por peso
        // (pesagem ao vivo não depende de código nenhum).
        tblProdutos.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(PesquisarProdutoDTO item, boolean empty) {
                super.updateItem(item, empty);
                boolean bloqueada = !empty && item.semCodigoResolvivel() && !item.isVendidoPorPeso();
                setDisable(bloqueada);
                setOpacity(bloqueada ? 0.45 : 1.0);
            }
        });

        tfDescricao.textProperty().addListener((obs, oldValue, newValue) -> pesquisar(newValue));

        tfDescricao.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Digitou + ENTER seleciona o primeiro resultado direto —
                // não obriga passar pela tabela com as setas.
                if (!tblProdutos.getItems().isEmpty()) {
                    tblProdutos.getSelectionModel().selectFirst();
                    selecionarAtual();
                }
                event.consume();
            } else if (event.getCode() == KeyCode.DOWN) {
                if (!tblProdutos.getItems().isEmpty()) {
                    tblProdutos.requestFocus();
                    tblProdutos.getSelectionModel().selectFirst();
                    tblProdutos.getFocusModel().focus(0);
                }
                event.consume();
            } else if (event.getCode() == KeyCode.F10 || event.getCode() == KeyCode.ESCAPE) {
                fechar();
                event.consume();
            }
        });

        tblProdutos.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                selecionarAtual();
                event.consume();
            } else if (event.getCode() == KeyCode.F10 || event.getCode() == KeyCode.ESCAPE) {
                fechar();
                event.consume();
            }
        });

        tblProdutos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) selecionarAtual();
        });

        pesquisar(null);
        Platform.runLater(tfDescricao::requestFocus);
    }

    private void pesquisar(String filtro) {
        var resultado = produtoService.paraPesquisa(filtro);
        tblProdutos.setItems(FXCollections.observableArrayList(resultado));
        lblContador.setText(resultado.size() + (resultado.size() == 1 ? " produto" : " produtos"));
    }

    private void selecionarAtual() {
        PesquisarProdutoDTO selecionado = tblProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) return;

        if (selecionado.semCodigoResolvivel() && !selecionado.isVendidoPorPeso()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING,
                    "Este produto não tem GTIN nem código de balança cadastrado — "
                            + "não é possível adicioná-lo à venda por aqui.");
            alerta.setHeaderText("Sem código disponível");
            alerta.showAndWait();
            return;
        }

        if (onProdutoSelecionado != null) {
            onProdutoSelecionado.accept(selecionado);
        }
        fechar();
    }

    private void fechar() {
        if (stage != null) {
            stage.close();
        } else if (tblProdutos.getScene() != null) {
            ((Stage) tblProdutos.getScene().getWindow()).close();
        }
    }

    @FXML
    private void handleFechar() {
        fechar();
    }

    public void setOnProdutoSelecionado(Consumer<PesquisarProdutoDTO> callback) {
        this.onProdutoSelecionado = callback;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
