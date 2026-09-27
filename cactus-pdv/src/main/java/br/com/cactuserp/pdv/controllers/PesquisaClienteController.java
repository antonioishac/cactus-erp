package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.util.Formatadores;
import br.com.cactuserp.pdv.entity.Cliente;
import br.com.cactuserp.pdv.service.ClienteService;
import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

@Component
@Scope("prototype")
public class PesquisaClienteController implements Initializable {

    private final ClienteService clienteService;

    @FXML private RadioButton rbNome;
    @FXML private RadioButton rbCpfCnpj;
    @FXML private ToggleGroup tgTipoBusca;
    @FXML private Label lblFiltro;
    @FXML private TextField tfFiltro;

    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, Number> codigoColumn;
    @FXML private TableColumn<Cliente, String> nomeColumn;
    @FXML private TableColumn<Cliente, String> cpfCnpjColumn;
    @FXML private TableColumn<Cliente, String> tipoPessoaColumn;
    @FXML private TableColumn<Cliente, String> cidadeColumn;
    @FXML private TableColumn<Cliente, String> ufColumn;
    @FXML private TableColumn<Cliente, String> telefoneColumn;

    @FXML private Button btnPesquisar;
    @FXML private Button btnSelecionar;
    @FXML private Button btnCancelar;

    private final ObservableList<Cliente> clientes = FXCollections.observableArrayList();

    private Stage stage;
    private Consumer<Cliente> onClienteSelecionado;

    @Autowired
    public PesquisaClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColunas();
        configurarEventos();
        tblClientes.setItems(clientes);
        Platform.runLater(tfFiltro::requestFocus);
    }

    // ===== Configuração injetada pelo StageManager =====

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.F1)       { pesquisar();  e.consume(); }
            else if (e.getCode() == KeyCode.F2)  { selecionar(); e.consume(); }
            else if (e.getCode() == KeyCode.F10) { cancelar();   e.consume(); }
        });
    }

    public void setOnClienteSelecionado(Consumer<Cliente> callback) {
        this.onClienteSelecionado = callback;
    }

    // ===== Setup da tabela e eventos =====

    private void configurarColunas() {
        codigoColumn.setCellValueFactory(c ->
                new SimpleLongProperty(c.getValue().getId() != null ? c.getValue().getId() : 0L));

        nomeColumn.setCellValueFactory(c -> {
            Cliente cli = c.getValue();
            String exibicao = cli.getNome();
            if (cli.getFantasia() != null && !cli.getFantasia().isBlank()) {
                exibicao = cli.getNome() + " (" + cli.getFantasia() + ")";
            }
            return new SimpleStringProperty(exibicao);
        });

        cpfCnpjColumn.setCellValueFactory(c ->
                new SimpleStringProperty(Formatadores.cpfCnpj(c.getValue().getCpfCnpj())));

        tipoPessoaColumn.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getTipoPessoa() != null
                                ? c.getValue().getTipoPessoa().name()
                                : ""));

        cidadeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCidade()));
        ufColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUf()));

        telefoneColumn.setCellValueFactory(c -> {
            Cliente cli = c.getValue();
            String fone = (cli.getCelular() != null && !cli.getCelular().isBlank())
                    ? cli.getCelular()
                    : cli.getTelefone();
            return new SimpleStringProperty(Formatadores.telefone(fone));
        });
    }

    private void configurarEventos() {
        tgTipoBusca.selectedToggleProperty().addListener((obs, antigo, novo) -> {
            if (novo == rbCpfCnpj) {
                lblFiltro.setText("CPF / CNPJ:");
                tfFiltro.setPromptText("Apenas números");
            } else {
                lblFiltro.setText("Nome / Razão Social:");
                tfFiltro.setPromptText("");
            }
            tfFiltro.clear();
            tfFiltro.requestFocus();
        });

        tfFiltro.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) pesquisar();
        });

        tblClientes.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && tblClientes.getSelectionModel().getSelectedItem() != null) {
                selecionar();
            }
        });

        tblClientes.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) selecionar();
        });

        btnPesquisar.setOnAction(e -> pesquisar());
        btnSelecionar.setOnAction(e -> selecionar());
        btnCancelar.setOnAction(e -> cancelar());
    }

    // ===== Ações =====

    private void pesquisar() {
        String filtro = tfFiltro.getText();
        try {
            List<Cliente> resultado = rbCpfCnpj.isSelected()
                    ? clienteService.buscarPorCpfCnpj(filtro)
                    : clienteService.buscarPorNome(filtro);

            clientes.setAll(resultado);

            if (resultado.isEmpty()) {
                alerta(Alert.AlertType.INFORMATION, "Nenhum cliente encontrado.");
                tfFiltro.requestFocus();
            } else {
                tblClientes.getSelectionModel().selectFirst();
                tblClientes.requestFocus();
            }
        } catch (Exception ex) {
            alerta(Alert.AlertType.ERROR, "Erro ao pesquisar: " + ex.getMessage());
        }
    }

    private void selecionar() {
        Cliente sel = tblClientes.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alerta(Alert.AlertType.WARNING, "Selecione um cliente na lista.");
            return;
        }
        if (onClienteSelecionado != null) {
            onClienteSelecionado.accept(sel);
        }
        fecharJanela();
    }

    private void cancelar() {
        fecharJanela();
    }

    private void fecharJanela() {
        if (stage != null) {
            stage.close();
        }
    }

    private void alerta(Alert.AlertType tipo, String msg) {
        Alert a = new Alert(tipo, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }
}