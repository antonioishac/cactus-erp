package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.StageManager;
import br.com.cactuserp.pdv.entity.PdvTotalTipoPagamento;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;
import br.com.cactuserp.pdv.entity.SimNao;
import br.com.cactuserp.pdv.entity.StatusVenda;
import br.com.cactuserp.pdv.service.ConsultaCupomService;
import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.config.util.AutorizacaoGerencialDialog;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.service.CancelamentoVendaService;
import br.com.cactuserp.pdv.service.DevolucaoService;
import br.com.cactuserp.pdv.service.LoginService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller da tela ConsultaCupom.fxml.
 *
 * <p>Tela cheia read-only de consulta de vendas finalizadas/canceladas
 * com filtros (data início/fim, número de cupom/COO, status).
 * Acionada via F12 → CONSULTAR_CUPOM.
 */
@Component
public class ConsultaCupomController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaCupomController.class);
    private static final NumberFormat MOEDA =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private static final DateTimeFormatter FMT_DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM HH:mm");

    /** Opção do combo de status. */
    public enum FiltroStatus {
        FINALIZADOS_E_CANCELADOS("Finalizados e Cancelados"),
        SO_FINALIZADOS("Só Finalizados"),
        SO_CANCELADOS("Só Cancelados");

        private final String rotulo;
        FiltroStatus(String r) { this.rotulo = r; }
        @Override public String toString() { return rotulo; }
    }

    private final StageManager stageManager;
    private final ConsultaCupomService consultaCupomService;
    private final DevolucaoService devolucaoService;
    private final CancelamentoVendaService cancelamentoVendaService;
    private final LoginService loginService;
    private final SessaoPdv sessaoPdv;

    @FXML private AnchorPane root;
    @FXML private DatePicker dpDataInicio;
    @FXML private DatePicker dpDataFim;
    @FXML private TextField tfNumeroCupom;
    @FXML private ComboBox<FiltroStatus> cbStatus;
    @FXML private Button btnBuscar;
    @FXML private Label lblContador;
    @FXML private TextField tfCpfCnpj;

    // Lista de cupons
    @FXML private TableView<PdvVendaCabecalho> tabelaCupons;
    @FXML private TableColumn<PdvVendaCabecalho, String> colCoo;
    @FXML private TableColumn<PdvVendaCabecalho, String> colData;
    @FXML private TableColumn<PdvVendaCabecalho, String> colTotal;
    @FXML private TableColumn<PdvVendaCabecalho, String> colStatus;

    // Detalhe
    @FXML private VBox estadoVazio;
    @FXML private VBox painelInfo;
    @FXML private Label lblNumeroCupom;
    @FXML private Label lblBadgeStatus;
    @FXML private Label lblDataHora;
    @FXML private Label lblOperadorCliente;
    @FXML private Label lblInfoCancelamento;
    @FXML private Button btnCancelarVenda;

    // Itens
    @FXML private TableView<PdvVendaDetalhe> tabelaItens;
    @FXML private TableColumn<PdvVendaDetalhe, String> colItemSeq;
    @FXML private TableColumn<PdvVendaDetalhe, String> colItemDesc;
    @FXML private TableColumn<PdvVendaDetalhe, String> colItemQtd;
    @FXML private TableColumn<PdvVendaDetalhe, String> colItemTotal;
    @FXML private TableColumn<PdvVendaDetalhe, Void> colItemAcao;

    // Pagamentos
    @FXML private VBox blocoPagamentos;
    @FXML private VBox containerPagamentos;

    // Totais
    @FXML private Label lblSubtotalDetalhe;
    @FXML private Label lblDescontoDetalhe;
    @FXML private Label lblAcrescimoDetalhe;
    @FXML private Label lblCanceladoDetalhe;
    @FXML private Label lblTotalFinalDetalhe;
    @FXML private Label lblRecebidoDetalhe;
    @FXML private Label lblTrocoDetalhe;

    private final ObservableList<PdvVendaCabecalho> cupons = FXCollections.observableArrayList();
    private final ObservableList<PdvVendaDetalhe> itensDoCupom = FXCollections.observableArrayList();

    private PdvVendaCabecalho cupomExibido;

    public ConsultaCupomController(StageManager stageManager,
                                   ConsultaCupomService consultaCupomService,
                                   DevolucaoService devolucaoService,
                                   CancelamentoVendaService cancelamentoVendaService,
                                   LoginService loginService,
                                   SessaoPdv sessaoPdv) {
        this.stageManager = stageManager;
        this.consultaCupomService = consultaCupomService;
        this.devolucaoService = devolucaoService;
        this.cancelamentoVendaService = cancelamentoVendaService;
        this.loginService = loginService;
        this.sessaoPdv = sessaoPdv;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarFiltros();
        configurarTabelaCupons();
        configurarTabelaItens();
        configurarAtalhosTeclado();

        Platform.runLater(() -> {
            tfNumeroCupom.requestFocus();
            executarBusca(); // já busca com os filtros default
        });
    }

    // ====================================================================
    //  Configuração inicial
    // ====================================================================

    private void configurarFiltros() {
        cbStatus.getItems().addAll(FiltroStatus.values());
        cbStatus.setValue(FiltroStatus.FINALIZADOS_E_CANCELADOS);

        // Default: últimos 7 dias
        LocalDate hoje = LocalDate.now();
        dpDataFim.setValue(hoje);
        dpDataInicio.setValue(hoje.minusDays(7));

        // ENTER em qualquer campo de filtro → busca
        tfNumeroCupom.setOnAction(e -> executarBusca());
    }

    private void configurarTabelaCupons() {
        colCoo.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCoo() != null
                        ? String.valueOf(c.getValue().getCoo())
                        : "#" + c.getValue().getId()));

        colData.setCellValueFactory(c -> {
            PdvVendaCabecalho v = c.getValue();
            if (v.getDataVenda() == null) return new SimpleStringProperty("—");
            String hh = v.getHoraVenda() != null
                    ? v.getHoraVenda().substring(0, Math.min(5, v.getHoraVenda().length()))
                    : "";
            return new SimpleStringProperty(
                    v.getDataVenda().format(DateTimeFormatter.ofPattern("dd/MM"))
                            + " " + hh);
        });

        colTotal.setCellValueFactory(c ->
                new SimpleStringProperty(formatarMoeda(c.getValue().getValorTotalDocumento())));

        colStatus.setCellValueFactory(c -> {
            StatusVenda s = c.getValue().getStatusVenda();
            return new SimpleStringProperty(textoStatus(s));
        });

        // Row factory pra colorir status cancelado
        tabelaCupons.setRowFactory(tv -> new TableRow<PdvVendaCabecalho>() {
            @Override
            protected void updateItem(PdvVendaCabecalho item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("cc-linha-cancelada");
                if (!empty && item != null && item.getStatusVenda() == StatusVenda.C) {
                    getStyleClass().add("cc-linha-cancelada");
                }
            }
        });

        tabelaCupons.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabelaCupons.setItems(cupons);

        // Seleção atualiza detalhe
        tabelaCupons.getSelectionModel().selectedItemProperty().addListener((o, antigo, novo) -> {
            if (novo != null) renderizarDetalhe(novo);
        });
    }

    private void configurarTabelaItens() {
        colItemSeq.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getItem() != null
                        ? String.valueOf(c.getValue().getItem()) : ""));

        colItemDesc.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getProduto() != null
                        ? c.getValue().getProduto().getDescricaoPdv()
                        : "Produto removido"));

        colItemQtd.setCellValueFactory(c ->
                new SimpleStringProperty(formatarQuantidade(c.getValue().getQuantidade())));

        colItemTotal.setCellValueFactory(c ->
                new SimpleStringProperty(formatarMoeda(c.getValue().getValorTotal())));

        // Risca itens cancelados
        tabelaItens.setRowFactory(tv -> new TableRow<PdvVendaDetalhe>() {
            @Override
            protected void updateItem(PdvVendaDetalhe item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("cc-item-cancelado");
                if (!empty && item != null && item.getCancelado() == SimNao.S) {
                    getStyleClass().add("cc-item-cancelado");
                }
            }
        });

        tabelaItens.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabelaItens.setItems(itensDoCupom);

        configurarColunaDevolucao();
    }

    /**
     * Botão "Devolver" por linha — só habilitado quando o cupom está
     * FINALIZADO, o item não está cancelado, e ainda há quantidade
     * disponível para devolução (ver {@link DevolucaoService}).
     */
    private void configurarColunaDevolucao() {
        colItemAcao.setCellFactory(coluna -> new TableCell<PdvVendaDetalhe, Void>() {
            private final Button btnDevolver = new Button("Devolver");

            {
                btnDevolver.getStyleClass().add("cc-btn-devolver");
                btnDevolver.setOnAction(e -> {
                    PdvVendaDetalhe item = getTableRow().getItem();
                    if (item == null) return;
                    stageManager.showDevolucao(item, () -> {
                        if (cupomExibido != null) renderizarDetalhe(cupomExibido);
                    });
                });
            }

            @Override
            protected void updateItem(Void valor, boolean empty) {
                super.updateItem(valor, empty);
                PdvVendaDetalhe item = empty ? null : getTableRow().getItem();
                if (item == null) {
                    setGraphic(null);
                    return;
                }
                boolean cupomFinalizado = cupomExibido != null
                        && cupomExibido.getStatusVenda() == StatusVenda.F;
                boolean itemAtivo = item.getCancelado() != SimNao.S;
                boolean temSaldo = itemAtivo && cupomFinalizado
                        && devolucaoService.quantidadeDisponivelParaDevolucao(item)
                                .compareTo(BigDecimal.ZERO) > 0;
                btnDevolver.setDisable(!temSaldo);
                setGraphic(btnDevolver);
            }
        });
    }

    private void configurarAtalhosTeclado() {
        Platform.runLater(() -> {
            Scene scene = root.getScene();
            if (scene == null) return;

            // Atalho GLOBAL: F5 = buscar de qualquer lugar
            scene.setOnKeyPressed(ev -> {
                if (ev.getCode() == KeyCode.ESCAPE) {
                    stageManager.fecharConsultaCupom();
                    ev.consume();
                    return;
                }
                if (ev.getCode() == KeyCode.F5) {
                    executarBusca();
                    if (!cupons.isEmpty()) tabelaCupons.requestFocus();
                    ev.consume();
                    return;
                }
                // F2 = foco rápido no campo Nº COO (atalho de PDV pra pesquisa)
                if (ev.getCode() == KeyCode.F2) {
                    tfNumeroCupom.requestFocus();
                    tfNumeroCupom.selectAll();
                    ev.consume();
                    return;
                }
                // F8 = cancelar a venda finalizada exibida (PDV não depende de mouse)
                if (ev.getCode() == KeyCode.F8) {
                    handleCancelarVenda();
                    ev.consume();
                    return;
                }
                // F3 = foco no campo CPF/CNPJ
                if (ev.getCode() == KeyCode.F3) {
                    tfCpfCnpj.requestFocus();
                    tfCpfCnpj.selectAll();
                    ev.consume();
                }
            });
        });

        // ENTER em qualquer campo de filtro → busca
        tfNumeroCupom.setOnAction(e -> executarBusca());
        tfCpfCnpj.setOnAction(e -> executarBusca());

        // ↓ nos campos de texto → vai pra tabela
        tfNumeroCupom.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.DOWN && !cupons.isEmpty()) {
                tabelaCupons.requestFocus();
                tabelaCupons.getSelectionModel().selectFirst();
                ev.consume();
            }
        });
        tfCpfCnpj.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.DOWN && !cupons.isEmpty()) {
                tabelaCupons.requestFocus();
                tabelaCupons.getSelectionModel().selectFirst();
                ev.consume();
            }
        });

        // ENTER no combo de status → busca
        cbStatus.setOnAction(e -> {
            // ouvido também a cada mudança — útil quando muda pelo teclado
        });
        cbStatus.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                executarBusca();
                ev.consume();
            }
        });

        // ↑ na tabela no primeiro item → volta pro Nº COO
        // Backspace na tabela → volta pro CPF e remove último caractere
        tabelaCupons.setOnKeyPressed(ev -> {
            int idx = tabelaCupons.getSelectionModel().getSelectedIndex();

            if (ev.getCode() == KeyCode.UP && idx == 0) {
                tfNumeroCupom.requestFocus();
                tfNumeroCupom.positionCaret(tfNumeroCupom.getText() != null
                        ? tfNumeroCupom.getText().length() : 0);
                ev.consume();
                return;
            }

            // Backspace na tabela → volta pro Nº COO e apaga último caractere
            if (ev.getCode() == KeyCode.BACK_SPACE) {
                tfNumeroCupom.requestFocus();
                String txt = tfNumeroCupom.getText();
                if (txt != null && !txt.isEmpty()) {
                    tfNumeroCupom.setText(txt.substring(0, txt.length() - 1));
                    tfNumeroCupom.positionCaret(tfNumeroCupom.getText().length());
                }
                ev.consume();
            }
        });
    }

    // ====================================================================
    //  Busca
    // ====================================================================

    @FXML
    private void aoClicarBuscar() {
        executarBusca();
    }

    private void executarBusca() {
        cupons.clear();
        esconderDetalhe();

        LocalDate inicio = dpDataInicio.getValue();
        LocalDate fim = dpDataFim.getValue();
        if (inicio == null || fim == null) {
            lblContador.setText("Informe data início e fim");
            return;
        }
        if (inicio.isAfter(fim)) {
            lblContador.setText("Data início > data fim");
            return;
        }

        List<StatusVenda> statuses;
        FiltroStatus filtro = cbStatus.getValue();
        if (filtro == null) filtro = FiltroStatus.FINALIZADOS_E_CANCELADOS;
        switch (filtro) {
            case SO_FINALIZADOS: statuses = Arrays.asList(StatusVenda.F); break;
            case SO_CANCELADOS:  statuses = Arrays.asList(StatusVenda.C); break;
            default:             statuses = Arrays.asList(StatusVenda.F, StatusVenda.C);
        }

        Integer numero = null;
        String numTxt = tfNumeroCupom.getText() != null ? tfNumeroCupom.getText().trim() : "";
        if (!numTxt.isEmpty()) {
            try {
                numero = Integer.valueOf(numTxt);
            } catch (NumberFormatException e) {
                lblContador.setText("Número inválido");
                return;
            }
        }

        String cpf = tfCpfCnpj != null ? tfCpfCnpj.getText() : null;

        try {
            List<PdvVendaCabecalho> resultado =
                    consultaCupomService.buscar(inicio, fim, statuses, numero, cpf);
            cupons.addAll(resultado);
            atualizarContador();
            if (!cupons.isEmpty()) {
                tabelaCupons.getSelectionModel().selectFirst();
            }
        } catch (Exception ex) {
            LOGGER.error("Falha ao buscar cupons", ex);
            lblContador.setText("Erro na busca");
        }
    }

    private void atualizarContador() {
        int n = cupons.size();
        if (n == 0) lblContador.setText("Nenhum cupom");
        else if (n == 1) lblContador.setText("1 cupom");
        else lblContador.setText(n + " cupons");
    }

    // ====================================================================
    //  Detalhe
    // ====================================================================

    private void esconderDetalhe() {
        estadoVazio.setVisible(true);
        estadoVazio.setManaged(true);
        painelInfo.setVisible(false);
        painelInfo.setManaged(false);
    }

    private void renderizarDetalhe(PdvVendaCabecalho cupom) {
        this.cupomExibido = cupom;
        estadoVazio.setVisible(false);
        estadoVazio.setManaged(false);
        painelInfo.setVisible(true);
        painelInfo.setManaged(true);

        // Carrega dados completos (itens + pagamentos)
        ConsultaCupomService.DetalheCompleto det =
                consultaCupomService.carregarDetalhe(cupom);

        // Cabeçalho
        String numeroLbl = cupom.getCoo() != null
                ? "CUPOM Nº " + cupom.getCoo()
                : "CUPOM #" + cupom.getId();
        lblNumeroCupom.setText(numeroLbl);

        // Badge status
        StatusVenda s = cupom.getStatusVenda();
        lblBadgeStatus.setText(textoStatus(s));
        lblBadgeStatus.getStyleClass().removeAll("cp-badge-ativo", "cp-badge-inativo", "cc-badge-cancelado");
        if (s == StatusVenda.C) {
            lblBadgeStatus.getStyleClass().add("cc-badge-cancelado");
        } else {
            lblBadgeStatus.getStyleClass().add("cp-badge-ativo");
        }

        StringBuilder dh = new StringBuilder();
        if (cupom.getDataVenda() != null) {
            dh.append(cupom.getDataVenda().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        if (cupom.getHoraVenda() != null) {
            if (dh.length() > 0) dh.append("  ·  ");
            dh.append(cupom.getHoraVenda());
        }
        lblDataHora.setText(dh.toString());

        StringBuilder oc = new StringBuilder();
        if (cupom.getColaborador() != null) {
            oc.append("Operador: ").append(cupom.getColaborador().getNome());
        }
        if (cupom.getNomeCliente() != null && !cupom.getNomeCliente().isBlank()) {
            if (oc.length() > 0) oc.append("  ·  ");
            oc.append("Cliente: ").append(cupom.getNomeCliente());
        } else if (cupom.getCliente() != null) {
            if (oc.length() > 0) oc.append("  ·  ");
            oc.append("Cliente: ").append(cupom.getCliente().getNome());
        }
        lblOperadorCliente.setText(oc.toString());

        renderizarCancelamento(cupom);

        // Itens
        itensDoCupom.setAll(det.getItens());

        // Pagamentos
        renderizarPagamentos(det.getPagamentos());

        // Totais
        lblSubtotalDetalhe.setText(formatarMoeda(cupom.getValorTotalProdutos()));
        lblDescontoDetalhe.setText(formatarMoeda(cupom.getValorDesconto()));
        lblAcrescimoDetalhe.setText(formatarMoeda(cupom.getValorAcrescimo()));
        lblCanceladoDetalhe.setText(formatarMoeda(cupom.getValorCancelado()));
        lblTotalFinalDetalhe.setText(formatarMoeda(cupom.getValorTotalDocumento()));
        lblRecebidoDetalhe.setText(formatarMoeda(cupom.getValorRecebido()));
        lblTrocoDetalhe.setText(formatarMoeda(cupom.getValorTroco()));
    }

    /** Botão habilitado só quando o cancelamento é possível; o motivo do bloqueio vai no tooltip. */
    private void renderizarCancelamento(PdvVendaCabecalho cupom) {
        String bloqueio = cancelamentoVendaService.motivoBloqueio(cupom);
        boolean finalizada = cupom.getStatusVenda() == StatusVenda.F;
        btnCancelarVenda.setVisible(finalizada);
        btnCancelarVenda.setManaged(finalizada);
        btnCancelarVenda.setDisable(bloqueio != null);
        btnCancelarVenda.setTooltip(bloqueio != null ? new javafx.scene.control.Tooltip(bloqueio) : null);

        boolean temCancelamento = cupom.getStatusVenda() == StatusVenda.C && cupom.getDataCancelamento() != null;
        lblInfoCancelamento.setVisible(temCancelamento);
        lblInfoCancelamento.setManaged(temCancelamento);
        if (temCancelamento) {
            lblInfoCancelamento.setText("Cancelada em " + cupom.getDataCancelamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + " " + cupom.getHoraCancelamento() + "  ·  " + cupom.getJustificativaCancelamento()
                    + (cupom.getProtocoloCancelamento() != null ? "  ·  Protocolo SEFAZ " + cupom.getProtocoloCancelamento() : ""));
        }
    }

    @FXML
    private void handleCancelarVenda() {
        PdvVendaCabecalho cupom = cupomExibido;
        if (cupom == null || cupom.getStatusVenda() != StatusVenda.F) return;
        String bloqueio = cancelamentoVendaService.motivoBloqueio(cupom);
        if (bloqueio != null) {
            mostrarAlerta(Alert.AlertType.WARNING, bloqueio);
            return;
        }

        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Cancelar venda");
        dialog.setHeaderText("Cancelar a venda inteira (" + formatarMoeda(cupom.getValorFinal()) + ")?\n"
                + "Os itens voltam ao estoque. A NFC-e autorizada é cancelada na SEFAZ.");
        dialog.setContentText("Justificativa (15 a 255 caracteres):");
        java.util.Optional<String> justificativa = dialog.showAndWait();
        if (justificativa.isEmpty()) return;
        String texto = justificativa.get().trim();
        if (texto.length() < 15 || texto.length() > 255) {
            mostrarAlerta(Alert.AlertType.ERROR, "A justificativa precisa ter entre 15 e 255 caracteres.");
            return;
        }

        java.util.Optional<PdvOperador> gerente = AutorizacaoGerencialDialog.pedir(loginService,
                "Cancelamento de venda exige autorização de Gerente/Supervisor.");
        if (gerente.isEmpty()) return;

        try {
            PdvVendaCabecalho cancelada = cancelamentoVendaService.cancelar(cupom, texto, sessaoPdv.getOperadorAtual(), gerente.get());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Venda cancelada."
                    + (cancelada.getProtocoloCancelamento() != null ? "\nProtocolo SEFAZ: " + cancelada.getProtocoloCancelamento() : "")
                    + "\nO reembolso em dinheiro, se houver, sai da gaveta como sangria.");
            executarBusca();
            renderizarDetalhe(cancelada);
        } catch (Exception ex) {
            LOGGER.error("Falha ao cancelar a venda #{}", cupom.getId(), ex);
            mostrarAlerta(Alert.AlertType.ERROR, "Não foi possível cancelar a venda.\n\nMotivo: " + ex.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensagem) {
        Alert alert = new Alert(tipo, mensagem);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void renderizarPagamentos(List<PdvTotalTipoPagamento> pagamentos) {
        containerPagamentos.getChildren().clear();

        if (pagamentos == null || pagamentos.isEmpty()) {
            Label vazio = new Label("Nenhum pagamento registrado");
            vazio.getStyleClass().add("cp-bloco-label");
            containerPagamentos.getChildren().add(vazio);
            return;
        }

        for (PdvTotalTipoPagamento p : pagamentos) {
            HBox linha = new HBox(10);
            linha.getStyleClass().add("cc-pagamento-linha");

            String desc = p.getPdvTipoPagamento() != null
                    ? p.getPdvTipoPagamento().getDescricao() : "—";
            Label lblDesc = new Label(desc);
            lblDesc.getStyleClass().add("cp-bloco-valor");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label lblValor = new Label(formatarMoeda(p.getValor()));
            lblValor.getStyleClass().add("cp-bloco-valor");

            linha.getChildren().addAll(lblDesc, spacer, lblValor);
            containerPagamentos.getChildren().add(linha);
        }
    }

    // ====================================================================
    //  Reset entre aberturas (singleton Spring)
    // ====================================================================

    public void prepararParaNovaConsulta() {
        cupomExibido = null;
        cupons.clear();
        itensDoCupom.clear();
        if (lblContador != null) lblContador.setText("");
        if (tfNumeroCupom != null) tfNumeroCupom.clear();
        if (tfCpfCnpj != null) tfCpfCnpj.clear();

        LocalDate hoje = LocalDate.now();
        if (dpDataFim != null) dpDataFim.setValue(hoje);
        if (dpDataInicio != null) dpDataInicio.setValue(hoje.minusDays(7));
        if (cbStatus != null) cbStatus.setValue(FiltroStatus.FINALIZADOS_E_CANCELADOS);

        esconderDetalhe();
    }

    // ====================================================================
    //  Helpers
    // ====================================================================

    private String textoStatus(StatusVenda s) {
        if (s == null) return "—";
        switch (s) {
            case A: return "ABERTO";
            case F: return "FINALIZADO";
            case C: return "CANCELADO";
            default: return s.toString();
        }
    }

    private String formatarMoeda(BigDecimal v) {
        if (v == null) return "R$ 0,00";
        return MOEDA.format(v);
    }

    private String formatarQuantidade(BigDecimal qtd) {
        if (qtd == null) return "0";
        BigDecimal limpo = qtd.stripTrailingZeros();
        if (limpo.scale() <= 0) {
            return limpo.toBigInteger().toString();
        }
        return limpo.toPlainString().replace(".", ",");
    }
}