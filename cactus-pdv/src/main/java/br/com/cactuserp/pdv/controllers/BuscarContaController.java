package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.StageManager;
import br.com.cactuserp.pdv.entity.ContaAberta;
import br.com.cactuserp.pdv.entity.ContaAbertaItem;
import br.com.cactuserp.pdv.entity.Mesa;
import br.com.cactuserp.pdv.entity.SituacaoItemConta;
import br.com.cactuserp.pdv.entity.TipoContaAberta;
import br.com.cactuserp.pdv.service.ContaAbertaService;
import br.com.cactuserp.pdv.service.IntegracaoContaPdvService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller da tela BuscarConta.fxml.
 *
 * <p>Tela cheia parametrizada por {@link TipoContaAberta}:
 * <ul>
 *   <li><b>Mesa (M)</b> — busca por número (somente dígitos), header azul</li>
 *   <li><b>Comanda (C)</b> — busca por código (alfanumérico), header laranja</li>
 * </ul>
 *
 * <p>Fluxo: digita identificador → ENTER → mostra prévia → F8 importa.
 *
 * <h3>Atalhos</h3>
 * <ul>
 *   <li>ENTER no campo  → busca a conta</li>
 *   <li>F8              → importa pro cupom (se houver conta carregada)</li>
 *   <li>ESC             → cancela e volta pro PDV</li>
 * </ul>
 */
@Component
public class BuscarContaController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(BuscarContaController.class);
    private static final NumberFormat MOEDA =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    private static final Color COR_MESA = Color.web("#2969c9");
    private static final Color COR_COMANDA = Color.web("#c9901e");

    private final StageManager stageManager;
    private final ContaAbertaService contaService;
    private final IntegracaoContaPdvService integracaoService;

    @FXML private AnchorPane root;
    @FXML private FontIcon iconeHeader;
    @FXML private Label lblTitulo;
    @FXML private Label lblSubtitulo;
    @FXML private Label lblLabelBusca;
    @FXML private TextField tfBusca;
    @FXML private Button btnBuscar;
    @FXML private Label lblMensagem;

    @FXML private HBox boxInfoConta;
    @FXML private FontIcon iconeConta;
    @FXML private Label lblInfoTitulo;
    @FXML private Label lblInfoSub;

    @FXML private Label lblTituloItens;
    @FXML private TableView<ContaAbertaItem> tabelaItens;
    @FXML private TableColumn<ContaAbertaItem, String> colSeq;
    @FXML private TableColumn<ContaAbertaItem, String> colDesc;
    @FXML private TableColumn<ContaAbertaItem, String> colObs;
    @FXML private TableColumn<ContaAbertaItem, String> colQtd;
    @FXML private TableColumn<ContaAbertaItem, String> colVlrU;
    @FXML private TableColumn<ContaAbertaItem, String> colTotal;
    @FXML private Label lblVazio;

    @FXML private Label lblTotal;
    @FXML private Button btnCancelar;
    @FXML private Button btnImportar;

    private final ObservableList<ContaAbertaItem> itens = FXCollections.observableArrayList();
    private TipoContaAberta tipo;
    private ContaAberta contaCarregada;

    private EventHandler<KeyEvent> filterTeclas;

    public BuscarContaController(StageManager stageManager,
                                 ContaAbertaService contaService,
                                 IntegracaoContaPdvService integracaoService) {
        this.stageManager = stageManager;
        this.contaService = contaService;
        this.integracaoService = integracaoService;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabela();
        tfBusca.setOnAction(e -> aoBuscar());
        tabelaItens.setItems(itens);
    }

    /**
     * Configura a tela. Chamado pelo StageManager antes de mostrar.
     */
    public void configurar(TipoContaAberta tipo) {
        this.tipo = tipo;
        this.contaCarregada = null;
        itens.clear();
        tfBusca.clear();
        lblMensagem.setText("");
        boxInfoConta.setVisible(false);
        boxInfoConta.setManaged(false);
        lblTituloItens.setVisible(false);
        lblTituloItens.setManaged(false);
        lblVazio.setVisible(true);
        lblVazio.setManaged(true);
        lblTotal.setText("R$ 0,00");
        btnImportar.setDisable(true);

        // Adapta UI por tipo
        if (tipo == TipoContaAberta.M) {
            iconeHeader.setIconLiteral("mdi2t-tray-arrow-down");
            iconeHeader.setIconColor(Color.WHITE);
            lblTitulo.setText("Importar Mesa pro cupom");
            lblSubtitulo.setText("Digite o número da mesa e veja os itens antes de importar");
            lblLabelBusca.setText("NÚMERO DA MESA");
            tfBusca.setPromptText("Ex: 5");
            iconeConta.setIconLiteral("mdi2s-silverware-fork-knife");
            iconeConta.setIconColor(COR_MESA);

            // Filtro só dígitos
            tfBusca.textProperty().addListener((o, antigo, novo) -> {
                if (novo != null && !novo.matches("\\d*")) {
                    tfBusca.setText(antigo);
                }
            });
        } else if (tipo == TipoContaAberta.C) {
            iconeHeader.setIconLiteral("mdi2t-tray-arrow-down");
            iconeHeader.setIconColor(Color.WHITE);
            lblTitulo.setText("Importar Comanda pro cupom");
            lblSubtitulo.setText("Digite o código da comanda e veja os itens antes de importar");
            lblLabelBusca.setText("CÓDIGO DA COMANDA");
            tfBusca.setPromptText("Ex: A12, B-07, 145");
            iconeConta.setIconLiteral("mdi2r-receipt");
            iconeConta.setIconColor(COR_COMANDA);
        }

        Platform.runLater(tfBusca::requestFocus);
        registrarAtalhosGlobais();
    }

    // ====================================================================
    //  Atalhos globais — filter no root
    // ====================================================================

    private void registrarAtalhosGlobais() {
        if (filterTeclas != null) {
            root.removeEventFilter(KeyEvent.KEY_PRESSED, filterTeclas);
        }

        filterTeclas = ev -> {
            if (ev.getCode() == KeyCode.ESCAPE) {
                desregistrarAtalhosGlobais();
                aoCancelar();
                ev.consume();
            } else if (ev.getCode() == KeyCode.F8) {
                if (!btnImportar.isDisabled()) {
                    aoImportar();
                }
                ev.consume();
            }
        };

        root.addEventFilter(KeyEvent.KEY_PRESSED, filterTeclas);
    }

    private void desregistrarAtalhosGlobais() {
        if (filterTeclas != null) {
            root.removeEventFilter(KeyEvent.KEY_PRESSED, filterTeclas);
            filterTeclas = null;
        }
    }

    // ====================================================================
    //  Tabela
    // ====================================================================

    private void configurarTabela() {
        colSeq.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getItem() != null
                        ? String.valueOf(c.getValue().getItem()) : ""));

        colDesc.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDescricao() != null
                        ? c.getValue().getDescricao() : ""));

        colObs.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getObservacao() != null
                        ? c.getValue().getObservacao() : "—"));

        colQtd.setCellValueFactory(c -> new SimpleStringProperty(
                formatarQtd(c.getValue().getQuantidade())));

        colVlrU.setCellValueFactory(c -> new SimpleStringProperty(
                formatarMoeda(c.getValue().getValorUnitario())));

        colTotal.setCellValueFactory(c -> new SimpleStringProperty(
                formatarMoeda(c.getValue().getValorTotal())));

        tabelaItens.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    // ====================================================================
    //  Ações
    // ====================================================================

    @FXML
    private void aoBuscar() {
        String txt = tfBusca.getText() != null ? tfBusca.getText().trim() : "";
        if (txt.isEmpty()) {
            mostrarMensagem(tipo == TipoContaAberta.M
                    ? "Digite o número da mesa"
                    : "Digite o código da comanda", true);
            return;
        }

        Optional<ContaAberta> opt;
        if (tipo == TipoContaAberta.M) {
            int numero;
            try {
                numero = Integer.parseInt(txt);
            } catch (NumberFormatException e) {
                mostrarMensagem("Número inválido", true);
                return;
            }
            // Busca a mesa pelo número e depois a conta ativa
            opt = contaService.listarMesasAtivas().stream()
                    .filter(m -> m.getNumero() != null && m.getNumero() == numero)
                    .findFirst()
                    .flatMap(contaService::buscarContaAtivaPorMesa);

            if (opt.isEmpty()) {
                mostrarConta(null);
                mostrarMensagem("Mesa " + numero + " não tem conta aberta", true);
                return;
            }
        } else {
            opt = contaService.buscarContaAtivaPorComanda(txt);
            if (opt.isEmpty()) {
                mostrarConta(null);
                mostrarMensagem("Comanda '" + txt + "' não encontrada", true);
                return;
            }
        }

        ContaAberta conta = opt.get();

        // Recarrega com itens (JOIN FETCH)
        Optional<ContaAberta> comItens = contaService.buscarComItens(conta.getId());
        if (comItens.isEmpty()) {
            mostrarMensagem("Conta não encontrada", true);
            return;
        }

        mostrarConta(comItens.get());
    }

    private void mostrarConta(ContaAberta conta) {
        this.contaCarregada = conta;

        if (conta == null) {
            boxInfoConta.setVisible(false);
            boxInfoConta.setManaged(false);
            lblTituloItens.setVisible(false);
            lblTituloItens.setManaged(false);
            itens.clear();
            lblVazio.setVisible(true);
            lblVazio.setManaged(true);
            lblTotal.setText("R$ 0,00");
            btnImportar.setDisable(true);
            return;
        }

        // Info da conta (header secundário)
        StringBuilder titulo = new StringBuilder();
        StringBuilder sub = new StringBuilder();

        if (tipo == TipoContaAberta.M) {
            Mesa mesa = conta.getMesa();
            if (mesa != null) {
                titulo.append("Mesa ").append(mesa.getNumero());
                if (mesa.getSetor() != null) {
                    titulo.append(" · ").append(mesa.getSetor());
                }
                if (mesa.getCapacidade() != null) {
                    titulo.append(" · ").append(mesa.getCapacidade()).append(" lugares");
                }
            }
        } else {
            titulo.append("Comanda ").append(conta.getCodigoComanda());
        }

        if (conta.getNomeClienteAvulso() != null
                && !conta.getNomeClienteAvulso().isBlank()) {
            sub.append("Cliente: ").append(conta.getNomeClienteAvulso());
        }
        if (conta.getHoraAbertura() != null) {
            if (sub.length() > 0) sub.append(" · ");
            sub.append("Aberta às ").append(conta.getHoraAbertura().substring(
                    0, Math.min(5, conta.getHoraAbertura().length())));
        }

        lblInfoTitulo.setText(titulo.toString());
        lblInfoSub.setText(sub.toString());
        boxInfoConta.setVisible(true);
        boxInfoConta.setManaged(true);

        // Filtra ATIVOS
        List<ContaAbertaItem> ativos = new ArrayList<>();
        for (ContaAbertaItem it : conta.getItens()) {
            if (it.getSituacao() == SituacaoItemConta.A) {
                ativos.add(it);
            }
        }

        if (ativos.isEmpty()) {
            itens.clear();
            lblTituloItens.setVisible(false);
            lblTituloItens.setManaged(false);
            lblVazio.setText("Conta sem itens ativos.");
            lblVazio.setVisible(true);
            lblVazio.setManaged(true);
            lblTotal.setText("R$ 0,00");
            btnImportar.setDisable(true);
            mostrarMensagem("Conta encontrada, mas não tem itens ativos", true);
            return;
        }

        lblTituloItens.setText("ITENS QUE SERÃO IMPORTADOS (" + ativos.size() + ")");
        lblTituloItens.setVisible(true);
        lblTituloItens.setManaged(true);
        lblVazio.setVisible(false);
        lblVazio.setManaged(false);

        itens.setAll(ativos);

        BigDecimal total = conta.getValorTotal() != null
                ? conta.getValorTotal() : BigDecimal.ZERO;
        lblTotal.setText(formatarMoeda(total));
        btnImportar.setDisable(false);
        mostrarMensagem("F8 pra importar pro cupom", false);
    }

    @FXML
    private void aoImportar() {
        if (contaCarregada == null) return;

        try {
            integracaoService.importarParaCupom(contaCarregada.getId());
            LOGGER.info("Conta #{} importada com sucesso", contaCarregada.getId());

            desregistrarAtalhosGlobais();
            // Volta pro PDV. Será necessário repopularUiAPartirDoBanco
            // no Pdv4Controller — o StageManager.fecharBuscarConta cuida.
            stageManager.fecharBuscarConta();

        } catch (Exception ex) {
            LOGGER.error("Falha ao importar conta", ex);
            new Alert(Alert.AlertType.ERROR,
                    "Falha ao importar conta:\n\n" + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void aoCancelar() {
        desregistrarAtalhosGlobais();
        stageManager.fecharBuscarConta();
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