package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.config.StageManager;
import br.com.cactuserp.pdv.entity.ContaAberta;
import br.com.cactuserp.pdv.entity.ContaAbertaItem;
import br.com.cactuserp.pdv.entity.Mesa;
import br.com.cactuserp.pdv.entity.Produto;
import br.com.cactuserp.pdv.entity.SituacaoItemConta;
import br.com.cactuserp.pdv.service.ContaAbertaService;
import br.com.cactuserp.pdv.service.ProdutoService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller do Lançamento de Mesa.
 *
 * <p>FIX v123: usa addEventFilter no ROOT (não na Scene) — assim o
 * filter "viaja" com o nó quando a Scene troca de root.
 */
@Component
public class LancamentoMesaController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LancamentoMesaController.class);
    private static final NumberFormat MOEDA =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    private final StageManager stageManager;
    private final SessaoPdv sessaoPdv;
    private final ContaAbertaService contaService;
    private final ProdutoService produtoService;

    @FXML private AnchorPane root;
    @FXML private Label lblTitulo;
    @FXML private Label lblSubtitulo;
    @FXML private Label lblOperador;
    @FXML private TextField tfCodigo;
    @FXML private TextField tfQuantidade;
    @FXML private TextField tfObservacao;
    @FXML private Label lblMensagem;

    @FXML private TableView<ContaAbertaItem> tabelaItens;
    @FXML private TableColumn<ContaAbertaItem, String> colSeq;
    @FXML private TableColumn<ContaAbertaItem, String> colDesc;
    @FXML private TableColumn<ContaAbertaItem, String> colObs;
    @FXML private TableColumn<ContaAbertaItem, String> colQtd;
    @FXML private TableColumn<ContaAbertaItem, String> colVlrU;
    @FXML private TableColumn<ContaAbertaItem, String> colTotal;
    @FXML private TableColumn<ContaAbertaItem, String> colHora;
    @FXML private TableColumn<ContaAbertaItem, String> colOp;

    @FXML private Label lblTotal;

    private final ObservableList<ContaAbertaItem> itens = FXCollections.observableArrayList();
    private Long idConta;
    private ContaAberta conta;

    // ===== Atalhos globais (filter no ROOT) =====
    private EventHandler<KeyEvent> filterTeclas;

    public LancamentoMesaController(StageManager stageManager,
                                    SessaoPdv sessaoPdv,
                                    ContaAbertaService contaService,
                                    ProdutoService produtoService) {
        this.stageManager = stageManager;
        this.sessaoPdv = sessaoPdv;
        this.contaService = contaService;
        this.produtoService = produtoService;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabela();

        tfCodigo.setOnAction(e -> lancarItem());

        tfQuantidade.setOnAction(e -> {
            tfCodigo.requestFocus();
            tfCodigo.selectAll();
        });

        tfObservacao.setOnAction(e -> {
            tfCodigo.requestFocus();
            tfCodigo.selectAll();
        });

        tfCodigo.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.DOWN && !itens.isEmpty()) {
                tabelaItens.requestFocus();
                tabelaItens.getSelectionModel().selectFirst();
                ev.consume();
            }
        });

        tabelaItens.setOnKeyPressed(ev -> {
            int idx = tabelaItens.getSelectionModel().getSelectedIndex();
            if (ev.getCode() == KeyCode.UP && idx == 0) {
                tfCodigo.requestFocus();
                tfCodigo.positionCaret(tfCodigo.getText() != null
                        ? tfCodigo.getText().length() : 0);
                ev.consume();
            }
        });

        tabelaItens.setItems(itens);
    }

    public void carregar(Long idConta) {
        LOGGER.info("=== LANCAMENTO.carregar(id={}) chamado", idConta);
        this.idConta = idConta;
        recarregarConta();

        Platform.runLater(() -> {
            tfCodigo.requestFocus();
            tfCodigo.selectAll();
        });

        registrarAtalhosGlobais();
    }

    // ====================================================================
    //  Atalhos globais — filter no ROOT
    // ====================================================================

    private void registrarAtalhosGlobais() {
        LOGGER.info("=== LANCAMENTO.registrarAtalhosGlobais() root={} filterTeclas={}",
                System.identityHashCode(root),
                filterTeclas != null ? "PRESENT" : "null");

        if (filterTeclas != null) {
            LOGGER.info("LANCAMENTO: removendo filter anterior do root");
            root.removeEventFilter(KeyEvent.KEY_PRESSED, filterTeclas);
        }

        filterTeclas = ev -> {
            LOGGER.info("[LANC FILTER] tecla={} target={}",
                    ev.getCode(),
                    ev.getTarget() != null ? ev.getTarget().getClass().getSimpleName() : "?");
            if (ev.getCode() == KeyCode.ESCAPE) {
                LOGGER.info("[LANC FILTER] ESC detectado, voltando pro salão");
                desregistrarAtalhosGlobais();
                aoVoltar();
                ev.consume();
            } else if (ev.getCode() == KeyCode.F1) {
                aoCancelarItem();
                ev.consume();
            } else if (ev.getCode() == KeyCode.F3) {
                tfQuantidade.requestFocus();
                tfQuantidade.selectAll();
                ev.consume();
            } else if (ev.getCode() == KeyCode.F11) {
                abrirPesquisaProduto();
                ev.consume();
            }
        };

        root.addEventFilter(KeyEvent.KEY_PRESSED, filterTeclas);
        LOGGER.info("LANCAMENTO: filter REGISTRADO no root {}", System.identityHashCode(root));
    }

    private void desregistrarAtalhosGlobais() {
        if (filterTeclas != null) {
            LOGGER.info("LANCAMENTO: desregistrando filter do root");
            root.removeEventFilter(KeyEvent.KEY_PRESSED, filterTeclas);
            filterTeclas = null;
        } else {
            LOGGER.info("LANCAMENTO: desregistrar SKIP (nada pra remover)");
        }
    }

    // ====================================================================
    //  Carregamento de dados
    // ====================================================================

    private void recarregarConta() {
        Optional<ContaAberta> opt = contaService.buscarComItens(idConta);
        if (opt.isEmpty()) {
            new Alert(Alert.AlertType.ERROR,
                    "Conta não encontrada.").showAndWait();
            aoVoltar();
            return;
        }
        conta = opt.get();

        Mesa mesa = conta.getMesa();
        if (mesa != null) {
            lblTitulo.setText("MESA " + mesa.getNumero());
            StringBuilder sub = new StringBuilder();
            if (mesa.getSetor() != null) sub.append(mesa.getSetor());
            if (mesa.getCapacidade() != null) {
                if (sub.length() > 0) sub.append(" · ");
                sub.append(mesa.getCapacidade()).append(" lugares");
            }
            if (conta.getNomeClienteAvulso() != null
                    && !conta.getNomeClienteAvulso().isBlank()) {
                if (sub.length() > 0) sub.append(" · ");
                sub.append("Cliente: ").append(conta.getNomeClienteAvulso());
            }
            lblSubtitulo.setText(sub.toString());
        } else {
            lblTitulo.setText("CONTA #" + conta.getId());
            lblSubtitulo.setText("");
        }

        if (sessaoPdv.getOperadorAtual() != null
                && sessaoPdv.getOperadorAtual().getColaborador() != null) {
            lblOperador.setText("Operador: "
                    + sessaoPdv.getOperadorAtual().getColaborador().getNome());
        }

        itens.setAll(conta.getItens());
        lblTotal.setText(formatarMoeda(conta.getValorTotal()));
    }

    private void configurarTabela() {
        colSeq.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getItem() != null
                        ? String.valueOf(c.getValue().getItem()) : ""));

        colDesc.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDescricao() != null
                        ? c.getValue().getDescricao() : ""));

        colObs.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getObservacao() != null
                        ? c.getValue().getObservacao() : ""));

        colQtd.setCellValueFactory(c -> new SimpleStringProperty(
                formatarQtd(c.getValue().getQuantidade())));

        colVlrU.setCellValueFactory(c -> new SimpleStringProperty(
                formatarMoeda(c.getValue().getValorUnitario())));

        colTotal.setCellValueFactory(c -> new SimpleStringProperty(
                formatarMoeda(c.getValue().getValorTotal())));

        colHora.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getHoraLancamento() != null
                        ? c.getValue().getHoraLancamento().substring(
                        0, Math.min(5, c.getValue().getHoraLancamento().length()))
                        : ""));

        colOp.setCellValueFactory(c -> {
            String nome = "";
            if (c.getValue().getOperadorLancamento() != null
                    && c.getValue().getOperadorLancamento().getColaborador() != null) {
                nome = c.getValue().getOperadorLancamento().getColaborador().getNome();
            }
            return new SimpleStringProperty(nome);
        });

        tabelaItens.setRowFactory(tv -> new TableRow<ContaAbertaItem>() {
            @Override
            protected void updateItem(ContaAbertaItem item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("sm-item-cancelado");
                if (!empty && item != null && item.getSituacao() == SituacaoItemConta.C) {
                    getStyleClass().add("sm-item-cancelado");
                }
            }
        });

        tabelaItens.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    // ====================================================================
    //  Ações
    // ====================================================================

    private void lancarItem() {
        String codigo = tfCodigo.getText() != null ? tfCodigo.getText().trim() : "";
        if (codigo.isEmpty()) return;

        BigDecimal qtd = lerQuantidade();
        if (qtd == null) return;

        Optional<Produto> prodOpt = produtoService.buscarPorCodigo(codigo);

        if (prodOpt.isEmpty() && codigo.matches("\\d+")) {
            try {
                prodOpt = produtoService.buscarPorCodigoBalanca(Integer.valueOf(codigo));
            } catch (NumberFormatException ignored) {}
        }

        if (prodOpt.isEmpty()) {
            mostrarMensagem("Produto não encontrado: " + codigo, true);
            tfCodigo.selectAll();
            return;
        }

        try {
            String obs = tfObservacao.getText() != null
                    ? tfObservacao.getText().trim() : null;
            if (obs != null && obs.isEmpty()) obs = null;

            contaService.lancarItem(conta, prodOpt.get(), qtd, null, obs,
                    sessaoPdv.getOperadorAtual());

            tfCodigo.clear();
            tfQuantidade.setText("1");
            tfObservacao.clear();

            mostrarMensagem("Item lançado: " + prodOpt.get().getDescricaoPdv(), false);
            recarregarConta();
            tfCodigo.requestFocus();
        } catch (Exception ex) {
            LOGGER.error("Falha ao lançar item", ex);
            mostrarMensagem("Erro: " + ex.getMessage(), true);
        }
    }

    private BigDecimal lerQuantidade() {
        String q = tfQuantidade.getText() != null ? tfQuantidade.getText().trim() : "";
        if (q.isEmpty()) q = "1";
        try {
            BigDecimal v = new BigDecimal(q.replace(",", "."));
            if (v.signum() <= 0) {
                mostrarMensagem("Quantidade deve ser positiva", true);
                return null;
            }
            return v;
        } catch (NumberFormatException e) {
            mostrarMensagem("Quantidade inválida", true);
            return null;
        }
    }

    @FXML
    private void aoCancelarItem() {
        ContaAbertaItem sel = tabelaItens.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarMensagem("Selecione um item da tabela", true);
            return;
        }
        if (sel.getSituacao() == SituacaoItemConta.C) {
            mostrarMensagem("Item já está cancelado", true);
            return;
        }

        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Cancelar item");
        dlg.setHeaderText("Cancelar: " + sel.getDescricao());
        dlg.setContentText("Motivo (opcional):");
        Optional<String> motivoOpt = dlg.showAndWait();
        if (motivoOpt.isEmpty()) return;

        try {
            contaService.cancelarItem(sel, sessaoPdv.getOperadorAtual(),
                    motivoOpt.get());
            mostrarMensagem("Item cancelado", false);
            recarregarConta();
            tfCodigo.requestFocus();
        } catch (Exception ex) {
            LOGGER.error("Falha ao cancelar item", ex);
            mostrarMensagem("Erro: " + ex.getMessage(), true);
        }
    }

    @FXML
    private void aoVoltar() {
        LOGGER.info("=== LANCAMENTO.aoVoltar() chamado");
        desregistrarAtalhosGlobais();
        stageManager.fecharLancamentoMesa();
    }

    private void abrirPesquisaProduto() {
        stageManager.showPesquisarProdutos(tfCodigo);
    }

    // ====================================================================
    //  Helpers
    // ====================================================================

    private void mostrarMensagem(String msg, boolean erro) {
        lblMensagem.setText(msg);
        lblMensagem.getStyleClass().removeAll("sm-mensagem-ok", "sm-mensagem-erro");
        lblMensagem.getStyleClass().add(erro ? "sm-mensagem-erro" : "sm-mensagem-ok");
    }

    private String formatarMoeda(BigDecimal v) {
        if (v == null) return "R$ 0,00";
        return MOEDA.format(v);
    }

    private String formatarQtd(BigDecimal q) {
        if (q == null) return "0";
        BigDecimal limpo = q.stripTrailingZeros();
        if (limpo.scale() <= 0) return limpo.toBigInteger().toString();
        return limpo.toPlainString().replace(".", ",");
    }
}