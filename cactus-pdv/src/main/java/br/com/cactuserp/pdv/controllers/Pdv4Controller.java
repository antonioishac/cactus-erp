package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.config.StageManager;
import br.com.cactuserp.pdv.config.util.AutorizacaoGerencialDialog;
import br.com.cactuserp.pdv.config.util.Formatadores;
import br.com.cactuserp.pdv.config.util.FormaterCurrency;
import br.com.cactuserp.pdv.controllers.SelecionarItemController.Variante;
import br.com.cactuserp.pdv.entity.*;
import br.com.cactuserp.pdv.service.BalancaService;
import br.com.cactuserp.pdv.service.LoginService;
import br.com.cactuserp.pdv.service.ProdutoImagemService;
import br.com.cactuserp.pdv.service.ProdutoService;
import br.com.cactuserp.pdv.service.VendaService;
import br.com.cactuserp.pdv.service.VendaService.ResultadoAdicaoItem;
import br.com.cactuserp.pdv.service.dto.ProdutoVenda;
import br.com.cactuserp.pdv.service.IntegracaoContaPdvService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class Pdv4Controller implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Pdv4Controller.class);
    private static final NumberFormat CURRENCY_FORMAT =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    // ====================================================================
    //  Dependências
    // ====================================================================
    private final StageManager stageManager;
    private final ProdutoService produtoService;
    private final ProdutoImagemService produtoImagemService;
    private final SessaoPdv sessaoPdv;
    private final VendaService vendaService;
    private final LoginService loginService;
    private final IntegracaoContaPdvService integracaoContaService;
    private final BalancaService balancaService;

    // ====================================================================
    //  Estado em memória
    // ====================================================================
    private final ObservableList<ProdutoVenda> itensVenda = FXCollections.observableArrayList();
    private Cliente clienteAtual;
    private boolean recuperacaoJaVerificada = false;
    private Timeline timelineRelogio;

    // ====================================================================
    //  @FXML — raiz e header
    // ====================================================================
    @FXML private AnchorPane root;
    @FXML private Label lblHeaderVenda;
    @FXML private Label lblHeaderOperador;
    @FXML private Label lblHeaderPdv;
    @FXML private Label lblHeaderHora;
    @FXML private HBox bannerModoRestrito;

    // ====================================================================
    //  @FXML — Card do último item bipado
    // ====================================================================
    @FXML private HBox cardUltimoItem;
    @FXML private ImageView imageViewProduto;
    @FXML private Label lblImagemEmoji;
    @FXML private Label lblCardBadgeAtacado;
    @FXML private Label lblCardMeta;
    @FXML private Label lblCardTitulo;
    @FXML private Label lblCardEconomia;
    @FXML private Label lblCardQtdXPreco;
    @FXML private Label lblCardTotal;

    // ====================================================================
    //  @FXML — Tabela e área vazia
    // ====================================================================
    @FXML private VBox areaVazia;
    @FXML private TableView<ProdutoVenda> tabelaItens;
    @FXML private TableColumn<ProdutoVenda, String> seqColumn;
    @FXML private TableColumn<ProdutoVenda, String> produtoColumn;
    @FXML private TableColumn<ProdutoVenda, String> qtdColumn;
    @FXML private TableColumn<ProdutoVenda, String> vlrUnidadeColumn;
    @FXML private TableColumn<ProdutoVenda, String> vlrTotalColumn;

    // ====================================================================
    //  @FXML — Inputs
    // ====================================================================
    @FXML private TextField tfCampoBusca;
    @FXML private TextField tfQuantidade;

    // ====================================================================
    //  @FXML — Painel direito
    // ====================================================================
    @FXML private Label lblTotal;
    @FXML private Label lblTotalMeta;
    @FXML private VBox cardEconomia;
    @FXML private Label lblEconomia;
    @FXML private Label lblSubtotal;
    @FXML private Label lblDesconto;
    @FXML private Label lblAcrescimo;
    @FXML private Label lblQtdItems;
    @FXML private Label lblQtdProdutos;
    @FXML private Label lblQtdCancelados;
    @FXML private HBox linhaDesconto;
    @FXML private HBox linhaAcrescimo;
    @FXML private HBox linhaCancelados;
    @FXML private Label lblNomeCliente;
    @FXML private Label lblCpfCnpjCliente;

    // ====================================================================
    //  Construtor — DI Spring
    // ====================================================================
    public Pdv4Controller(StageManager stageManager,
                          ProdutoService produtoService,
                          ProdutoImagemService produtoImagemService,
                          SessaoPdv sessaoPdv,
                          VendaService vendaService,
                          LoginService loginService,
                          IntegracaoContaPdvService integracaoContaService,
                          BalancaService balancaService) {
        this.stageManager = stageManager;
        this.produtoService = produtoService;
        this.produtoImagemService = produtoImagemService;
        this.sessaoPdv = sessaoPdv;
        this.vendaService = vendaService;
        this.loginService = loginService;
        this.integracaoContaService = integracaoContaService;
        this.balancaService = balancaService;
    }

    // ====================================================================
    //  initialize
    // ====================================================================
    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            Scene scene = root.getScene();
            if (scene != null) {
                setupKeyEvents(scene);
            }
            tfCampoBusca.clear();
            tfCampoBusca.requestFocus();

            tfQuantidade.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ENTER) {
                    tfCampoBusca.requestFocus();
                    tfCampoBusca.selectAll();
                }
            });

            atualizarModoDeOperacao();
            iniciarRelogio();
            atualizarHeader();

            if (!recuperacaoJaVerificada) {
                recuperacaoJaVerificada = true;
                verificarCupomPendente();
            }
        });

        // Row factory pra destacar linhas: cancelada (rosa+riscado) e atacado (verde)
        tabelaItens.setRowFactory(tv -> new TableRow<ProdutoVenda>() {
            @Override
            protected void updateItem(ProdutoVenda item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("pdv-tabela-linha-cancelada", "pdv-tabela-linha-atacado");
                if (empty || item == null) return;

                if (item.isCancelado()) {
                    getStyleClass().add("pdv-tabela-linha-cancelada");
                } else if (item.estaEmFaixaAtacado()) {
                    getStyleClass().add("pdv-tabela-linha-atacado");
                }
            }
        });

        iniciaValoresVendas();
        setupTable();
        configurarCellFactoryProdutoComBadge();
    }

    // ====================================================================
    //  HEADER e RELÓGIO
    // ====================================================================
    private void iniciarRelogio() {
        if (timelineRelogio != null) timelineRelogio.stop();
        timelineRelogio = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (lblHeaderHora != null) {
                lblHeaderHora.setText(LocalTime.now().format(FMT_HORA));
            }
        }));
        timelineRelogio.setCycleCount(Animation.INDEFINITE);
        timelineRelogio.play();
        // Primeira atualização imediata
        lblHeaderHora.setText(LocalTime.now().format(FMT_HORA));
    }

    private void atualizarHeader() {
        // Número da venda
        PdvVendaCabecalho cupom = sessaoPdv.getCupomAtivoOuNulo();
        if (cupom != null && cupom.getId() != null) {
            lblHeaderVenda.setText("#" + cupom.getId() + " · Em andamento");
        } else {
            lblHeaderVenda.setText("Caixa Livre");
        }

        // Operador e PDV
        if (sessaoPdv.temMovimentoAtivo()) {
            lblHeaderOperador.setText(sessaoPdv.getNomeOperadorAtual());
            lblHeaderPdv.setText(sessaoPdv.getNomeCaixaAtual());
        } else if (sessaoPdv.temOperadorLogado()) {
            lblHeaderOperador.setText(sessaoPdv.getNomeOperadorAtual());
            lblHeaderPdv.setText("Sem movimento");
        } else {
            lblHeaderOperador.setText("—");
            lblHeaderPdv.setText("—");
        }
    }

    // ====================================================================
    //  MODO DE OPERAÇÃO (NORMAL vs RESTRITO)
    // ====================================================================
    private void atualizarModoDeOperacao() {
        boolean restrito = sessaoPdv.estaEmModoRestrito();

        if (bannerModoRestrito != null) {
            bannerModoRestrito.setVisible(restrito);
            bannerModoRestrito.setManaged(restrito);
        }

        tfCampoBusca.setDisable(restrito);
        tfQuantidade.setDisable(restrito);

        if (restrito) {
            tfCampoBusca.setPromptText("Sem movimento aberto — abra movimento via F12");
        } else {
            tfCampoBusca.setPromptText("Bipe ou digite o código...");
        }
    }

    public void notificarMudancaDeMovimento() {
        atualizarModoDeOperacao();
        atualizarHeader();
        tfCampoBusca.requestFocus();
    }

    // ====================================================================
    //  RECUPERAÇÃO DE CUPOM PENDENTE
    // ====================================================================
    private void verificarCupomPendente() {
        if (!sessaoPdv.movimentoEstaAberto()) return;

        Optional<PdvVendaCabecalho> pendente =
                vendaService.buscarCupomAberto(sessaoPdv.getMovimentoAtivo());
        if (pendente.isEmpty()) return;

        PdvVendaCabecalho cupom = pendente.get();
        LOGGER.info("Cupom pendente detectado: #{}", cupom.getId());

        stageManager.showRecuperarCupom(cupom, resultado -> {
            switch (resultado) {
                case RETOMAR:
                    sessaoPdv.setCupomAtivo(cupom);
                    repopularUiAPartirDoBanco(cupom);
                    atualizarHeader();
                    LOGGER.info("Cupom #{} retomado", cupom.getId());
                    break;
                case CANCELADO:
                    sessaoPdv.limparCupom();
                    LOGGER.info("Cupom #{} cancelado pelo operador na recuperação", cupom.getId());
                    break;
            }
        });
    }

    private void repopularUiAPartirDoBanco(PdvVendaCabecalho cupom) {
        List<PdvVendaDetalhe> detalhes = vendaService.listarDetalhes(cupom);
        itensVenda.clear();

        PdvVendaDetalhe ultimoDetalhe = null;
        Produto ultimoProduto = null;
        String ultimoRotuloFaixa = null;
        BigDecimal ultimoPrecoVarejo = null;

        for (PdvVendaDetalhe d : detalhes) {
            String descricao = d.getProduto() != null
                    ? d.getProduto().getDescricaoPdv()
                    : "Produto removido";

            ProdutoVenda pv = new ProdutoVenda(
                    String.valueOf(d.getItem()),
                    d.getGtin(),
                    descricao,
                    d.getQuantidade(),
                    d.getValorUnitario(),
                    d.getValorTotal(),
                    d.getCancelado() == SimNao.S
            );
            pv.setValorDesconto(d.getValorDesconto() != null
                    ? d.getValorDesconto() : BigDecimal.ZERO);
            pv.setValorAcrescimo(d.getValorAcrescimo() != null
                    ? d.getValorAcrescimo() : BigDecimal.ZERO);

            // Recupera info de faixa pra item vindo do banco
            if (d.getProduto() != null && d.getProduto().getValorVenda() != null
                    && d.getValorUnitario() != null
                    && d.getValorUnitario().compareTo(d.getProduto().getValorVenda()) < 0) {
                pv.setPrecoVarejoOriginal(d.getProduto().getValorVenda());
                pv.setRotuloFaixa("ATACADO");
            }

            itensVenda.add(pv);

            // Guarda o último item não-cancelado pra mostrar no card superior
            if (d.getCancelado() != SimNao.S) {
                ultimoDetalhe = d;
                ultimoProduto = d.getProduto();
                ultimoRotuloFaixa = pv.getRotuloFaixa();
                ultimoPrecoVarejo = pv.getPrecoVarejoOriginal();
            }
        }

        recalcularTotaisDoResumo();
        atualizarContadores();
        atualizarVisibilidadeTabela();

        // Continuidade visual: mostra o ÚLTIMO item recuperado no card
        // superior. Tenta com Produto carregado; se falhar, ainda assim
        // exibe o card com dados do detalhe (defensivo).
        if (ultimoDetalhe != null) {
            try {
                if (ultimoProduto != null) {
                    atualizarCardUltimoItem(ultimoProduto, ultimoDetalhe,
                            ultimoRotuloFaixa, ultimoPrecoVarejo);
                } else {
                    LOGGER.warn("Produto do último item está nulo (LazyInit?). "
                            + "Card superior não será renderizado.");
                }
            } catch (Exception ex) {
                LOGGER.error("Falha ao renderizar card do último item recuperado", ex);
            }
        }

        tabelaItens.scrollTo(itensVenda.size() - 1);
        tfCampoBusca.requestFocus();
    }

    /**
     * Recarrega a UI do PDV a partir do cupom no banco — chamado após
     * importação de mesa/comanda (Fase 1.2).
     *
     * <p>Diferença vs {@link #repopularUiAPartirDoBanco}: este método é
     * público e usado pelo {@code StageManager.fecharBuscarConta()}
     * pra refletir os itens importados na UI sem precisar refazer o
     * fluxo de bipagem.
     */
    public void recarregarCupomAposImportacao() {
        PdvVendaCabecalho cupom = sessaoPdv.getCupomAtivoOuNulo();
        if (cupom == null) {
            // Importação pode ter criado o cupom no banco mas ainda não
            // refletiu na sessão — tenta buscar do movimento
            if (sessaoPdv.movimentoEstaAberto()) {
                Optional<PdvVendaCabecalho> aberto =
                        vendaService.buscarCupomAberto(sessaoPdv.getMovimentoAtivo());
                if (aberto.isPresent()) {
                    sessaoPdv.setCupomAtivo(aberto.get());
                    cupom = aberto.get();
                }
            }
        }
        if (cupom == null) {
            LOGGER.warn("recarregarCupomAposImportacao: sessão sem cupom ativo");
            return;
        }
        LOGGER.info("Recarregando UI a partir do cupom #{} (pós-importação)",
                cupom.getId());
        repopularUiAPartirDoBanco(cupom);
        atualizarHeader();
    }

    // ====================================================================
    //  Tabela
    // ====================================================================
    private void setupTable() {
        seqColumn.setCellValueFactory(new PropertyValueFactory<>("item"));
        produtoColumn.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        // QTD: formata BigDecimal sem zeros desnecessários (5.000000 → 5)
        qtdColumn.setCellValueFactory(param ->
                new SimpleStringProperty(formatarQuantidade(param.getValue().getQtde())));

        // VLR UNIT: cell factory custom (mostra de/por quando atacado)
        configurarCellFactoryVlrUnitarioComDePor();

        // TOTAL: cell factory custom (mostra economia abaixo quando atacado)
        configurarCellFactoryTotalComEconomia();

        tabelaItens.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        tabelaItens.setItems(itensVenda);
    }

    /**
     * Configura a coluna VLR UNIT para mostrar:
     * <ul>
     *   <li>Item em atacado: 2 linhas — "de R$ X,XX" riscado (amarelo)
     *       em cima, "por R$ Y,YY" (verde) embaixo</li>
     *   <li>Item de varejo: 1 linha simples com o valor unitário</li>
     * </ul>
     */
    private void configurarCellFactoryVlrUnitarioComDePor() {
        vlrUnidadeColumn.setCellFactory(col -> new TableCell<ProdutoVenda, String>() {
            @Override
            protected void updateItem(String unused, boolean empty) {
                super.updateItem(unused, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                ProdutoVenda pv = getTableRow() != null ? getTableRow().getItem() : null;
                if (pv == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                BigDecimal valorUnit = pv.getValorUnitario();
                BigDecimal precoVarejo = pv.getPrecoVarejoOriginal();

                // Critério pra mostrar "de/por": item em faixa atacado E
                // preço varejo é maior que o preço aplicado
                boolean mostraDePor = pv.estaEmFaixaAtacado()
                        && precoVarejo != null
                        && valorUnit != null
                        && precoVarejo.compareTo(valorUnit) > 0;

                if (mostraDePor) {
                    Label de = new Label("de " + CURRENCY_FORMAT.format(precoVarejo));
                    de.getStyleClass().add("pdv-celula-de-preco");

                    Label por = new Label("por " + CURRENCY_FORMAT.format(valorUnit));
                    por.getStyleClass().add("pdv-celula-por-preco");

                    VBox box = new VBox(2);
                    box.setAlignment(Pos.CENTER_RIGHT);
                    box.getChildren().addAll(de, por);

                    setText(null);
                    setGraphic(box);
                } else {
                    Label valor = new Label(CURRENCY_FORMAT.format(valorUnit));
                    valor.getStyleClass().add("pdv-celula-valor-unitario");
                    setText(null);
                    setGraphic(valor);
                }
            }
        });
    }

    /**
     * Configura a coluna TOTAL para mostrar:
     * <ul>
     *   <li>Item em atacado: valor total em verde + "economia R$ X,XX"
     *       em texto pequeno abaixo</li>
     *   <li>Item de varejo: só o valor total simples</li>
     * </ul>
     */
    private void configurarCellFactoryTotalComEconomia() {
        vlrTotalColumn.setCellFactory(col -> new TableCell<ProdutoVenda, String>() {
            @Override
            protected void updateItem(String unused, boolean empty) {
                super.updateItem(unused, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                ProdutoVenda pv = getTableRow() != null ? getTableRow().getItem() : null;
                if (pv == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                BigDecimal valorTotal = pv.getValorTotal();
                BigDecimal economia = pv.calcularEconomia();

                Label total = new Label(CURRENCY_FORMAT.format(valorTotal));

                if (pv.estaEmFaixaAtacado()
                        && economia != null
                        && economia.compareTo(BigDecimal.ZERO) > 0) {
                    // Atacado: total verde + economia abaixo
                    total.getStyleClass().add("pdv-celula-total-atacado");

                    Label lblEconomia = new Label("economia " + CURRENCY_FORMAT.format(economia));
                    lblEconomia.getStyleClass().add("pdv-celula-economia");

                    VBox box = new VBox(2);
                    box.setAlignment(Pos.CENTER_RIGHT);
                    box.getChildren().addAll(total, lblEconomia);

                    setText(null);
                    setGraphic(box);
                } else {
                    // Varejo: só o total
                    total.getStyleClass().add("pdv-celula-total-varejo");
                    setText(null);
                    setGraphic(total);
                }
            }
        });
    }

    /**
     * Formata uma quantidade BigDecimal removendo zeros desnecessários
     * pra exibição limpa na grid:
     *   5.000000 → "5"
     *   1.500000 → "1,5"
     *   2.345000 → "2,345"
     */
    private String formatarQuantidade(BigDecimal qtd) {
        if (qtd == null) return "0";
        BigDecimal limpo = qtd.stripTrailingZeros();
        if (limpo.scale() <= 0) {
            return limpo.toBigInteger().toString();
        }
        return limpo.toPlainString().replace(".", ",");
    }

    /**
     * Alterna entre mostrar a área vazia (logo + dica) ou a tabela,
     * conforme houver ou não itens no cupom.
     */
    private void atualizarVisibilidadeTabela() {
        boolean temItens = !itensVenda.isEmpty();
        if (tabelaItens != null) {
            tabelaItens.setVisible(temItens);
            tabelaItens.setManaged(temItens);
        }
        if (areaVazia != null) {
            areaVazia.setVisible(!temItens);
            areaVazia.setManaged(!temItens);
        }
    }

    // ====================================================================
    //  Atalhos de teclado
    // ====================================================================
    private void setupKeyEvents(Scene scene) {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();

            boolean restrito = sessaoPdv.estaEmModoRestrito();
            boolean ehSempreLiberado = (code == KeyCode.F5 || code == KeyCode.F11 || code == KeyCode.F12);

            if (restrito && !ehSempreLiberado) {
                event.consume();
                return;
            }

            if (event.getCode() == KeyCode.M && event.isControlDown()) {
                LOGGER.info("Ctrl+M → abrindo Salão de Mesas");
                stageManager.showSalaoMesas();
                event.consume();
                return;
            }

            // Ctrl+B → Comandas
            if (event.getCode() == KeyCode.B && event.isControlDown()) {
                LOGGER.info("Ctrl+B → abrindo Comandas");
                stageManager.showComandasAbertas();
                event.consume();
                return;
            }

            // Ctrl+F12 → Importar conta de Mesa/Comanda (Fase 1.2)
            if (event.getCode() == KeyCode.F12 && event.isControlDown()) {
                LOGGER.info("Ctrl+F12 → abrindo seletor Importar conta");
                stageManager.showImportarContaSeletor();
                event.consume();
                return;
            }

            switch (code) {
                case F1:
                    cancelarItem();
                    event.consume();
                    break;
                case F2:
                    cancelarVendaInteira();
                    event.consume();
                    break;
                case F3:
                    changeAmount();
                    event.consume();
                    break;
                case F4:
                    aplicarDescontoAcrescimoEmItem(TipoOperacaoDescontoAcrescimo.ACRESCIMO);
                    event.consume();
                    break;
                case F5:
                    abrirPesquisaCliente();
                    event.consume();
                    break;
                case F6:
                    aplicarDescontoAcrescimoEmItem(TipoOperacaoDescontoAcrescimo.DESCONTO);
                    event.consume();
                    break;
                case F7:
                    encerrarVenda();
                    event.consume();
                    break;
                case F8:
                    stageManager.showOperacaoCaixa(TipoOperacaoCaixa.SUPRIMENTO);
                    event.consume();
                    break;
                case F9:
                    stageManager.showOperacaoCaixa(TipoOperacaoCaixa.SANGRIA);
                    event.consume();
                    break;
                case F11:
                    abrirPesquisaProduto();
                    event.consume();
                    break;
                case F12:
                    stageManager.showMenuPrincipal();
                    event.consume();
                    break;
                case CONTROL:
                default:
                    break;
            }
        });
    }

    private void abrirPesquisaCliente() {
        stageManager.showPesquisarClientes(cliente -> {
            this.clienteAtual = cliente;
            atualizarLabelsCliente();

            // Se já existe cupom em aberto, persiste o cliente nele imediatamente.
            // Se ainda não existe (cliente selecionado antes do 1º item), fica em
            // clienteAtual e é aplicado em adicionarProdutoVenda() na criação do cupom.
            PdvVendaCabecalho cupomAtivo = sessaoPdv.getCupomAtivoOuNulo();
            if (cupomAtivo != null) {
                try {
                    vendaService.atualizarCliente(cupomAtivo, cliente);
                } catch (Exception ex) {
                    LOGGER.error("Falha ao associar cliente ao cupom {}", cupomAtivo.getId(), ex);
                    mostrarErro("Não foi possível associar o cliente à venda.\n\nMotivo: " + ex.getMessage());
                }
            }

            tfCampoBusca.requestFocus();
        });
    }

    private void atualizarLabelsCliente() {
        if (clienteAtual == null) {
            lblNomeCliente.setText("Consumidor");
            lblCpfCnpjCliente.setText("CPF/CNPJ não informado");
        } else {
            lblNomeCliente.setText(clienteAtual.getNome());
            lblCpfCnpjCliente.setText(Formatadores.cpfCnpj(clienteAtual.getCpfCnpj()));
        }
    }

    public Cliente getClienteAtual() {
        return clienteAtual;
    }

    public void reativarAtalhos() {
        Scene scene = root.getScene();
        if (scene != null) {
            setupKeyEvents(scene);
            tfCampoBusca.requestFocus();
        }
        atualizarHeader();
        atualizarModoDeOperacao();
    }

    private void encerrarVenda() {
        if (itensVenda.isEmpty()) {
            LOGGER.warn("Tentativa de encerrar venda sem itens");
            return;
        }
        if (!sessaoPdv.movimentoEstaAberto()) {
            LOGGER.error("Tentativa de encerrar venda sem movimento aberto");
            new Alert(Alert.AlertType.ERROR,
                    "Nenhum movimento de caixa aberto. Não é possível encerrar a venda.")
                    .showAndWait();
            return;
        }

        BigDecimal totalLiquido = calcularTotalLiquido();
        stageManager.showEncerramentoVenda(totalLiquido);
    }

    @FXML
    public void addProduct(ActionEvent event) {
        if (tfCampoBusca.getText().trim().isBlank()) {
            tfCampoBusca.requestFocus();
            tfCampoBusca.selectAll();
            return;
        }
        if (!sessaoPdv.movimentoEstaAberto()) {
            mostrarErro("Nenhum movimento de caixa aberto. Inicie o movimento antes de vender.");
            return;
        }

        Optional<Produto> product = produtoService.buscarPorCodigo(tfCampoBusca.getText());

        if (product.isEmpty()) {
            try {
                product = produtoService.buscarPorCodigoBalanca(
                        Integer.valueOf(tfCampoBusca.getText()));
            } catch (NumberFormatException e) {
                // não numérico
            }
        }

        if (product.isPresent()) {
            adicionarProdutoResolvido(product.get());
            return;
        }

        // Fallback: código de barras de balança com peso/valor embutido
        // (13 dígitos, prefixo reservado — ver BalancaService). Já vem com
        // a quantidade calculada a partir da etiqueta — não passa pela
        // pesagem ao vivo (o peso já está impresso, não precisa reler).
        Optional<BalancaService.ResultadoLeituraBalanca> leitura =
                balancaService.tentarDecodificar(sessaoPdv.getCaixaAtual(), tfCampoBusca.getText());
        if (leitura.isPresent()) {
            adicionarProdutoVenda(leitura.get().getProduto(), leitura.get().getQuantidade());
            return;
        }

        produtoNaoEncontrado();
    }

    /**
     * Ponto único de decisão pra QUALQUER produto já resolvido — por código
     * digitado/bipado ({@link #addProduct}) ou pela busca F11
     * ({@link #abrirPesquisaProduto}): se é vendido por peso, pesa AO VIVO
     * na balança automaticamente (cenário 2 do javadoc de
     * {@code CLAUDE.md}/{@code BalancaService} — produto sem etiqueta
     * nenhuma, ex: bananas soltas) em vez de usar a quantidade digitada ou
     * o padrão "1"; senão, segue o fluxo normal de quantidade manual.
     *
     * <p><b>100% por teclado, sem botão nenhum</b> — bipar/digitar um
     * código de balança de produto pesado e apertar ENTER já pesa
     * sozinho; achar o produto por nome (F11) e apertar ENTER também. Um
     * PDV não pode depender de mouse (feedback do usuário, 2026-08-23) —
     * a versão anterior desta feature era um botão "Pesar" separado, que
     * exigia clique e não se integrava ao fluxo de teclado já existente.
     */
    private void adicionarProdutoResolvido(Produto produto) {
        if (balancaService.vendidoPorPeso(produto)) {
            pesarEAdicionar(produto);
        } else {
            adicionarProdutoVenda(produto);
        }
    }

    /**
     * Abre a busca de produto por nome (F11) — cobre tanto o fluxo normal
     * (preenche {@code tfCampoBusca} com o código resolvido, como sempre
     * funcionou) quanto produto vendido por peso sem GTIN/código de
     * balança nenhum (pesa direto, ver {@link #adicionarProdutoResolvido}).
     */
    private void abrirPesquisaProduto() {
        stageManager.showPesquisarProdutos(dto -> {
            if (dto.isVendidoPorPeso()) {
                produtoService.buscarPorId(dto.getId())
                        .ifPresentOrElse(this::pesarEAdicionar,
                                () -> mostrarErro("Produto não encontrado."));
            } else {
                tfCampoBusca.setText(dto.getCodigo());
                tfCampoBusca.requestFocus();
                tfCampoBusca.selectEnd();
            }
        });
    }

    /**
     * Lê a balança e adiciona o item pesado — assume que {@code produto} já
     * foi confirmado como vendido por peso. Se não der pra ler (balança não
     * configurada, desligada, cabo solto, timeout — motivos legítimos que
     * não podem travar a venda), cai pro peso manual com autorização
     * gerencial ({@link #pesoManualComAutorizacao}) em vez de só barrar o
     * operador.
     */
    private void pesarEAdicionar(Produto produto) {
        Optional<BigDecimal> peso = balancaService.lerPesoAoVivo(sessaoPdv.getCaixaAtual());
        if (peso.isPresent()) {
            adicionarProdutoVenda(produto, peso.get());
            tfCampoBusca.clear();
            tfCampoBusca.requestFocus();
            return;
        }
        pesoManualComAutorizacao(produto);
    }

    /**
     * Fallback quando a balança não responde: peso digitado manualmente,
     * SEMPRE atrás de autorização de gerente/supervisor (mesmo padrão de
     * alçada usado em sangria/desconto/devolução — sem essa trava,
     * qualquer operador poderia digitar um peso menor que o real pra
     * fraudar o preço de um produto pesado). Cobre tanto o caso real
     * (balança quebrada/sem cabo no meio do expediente — a loja não pode
     * parar de vender) quanto ambiente sem balança física nenhuma ainda
     * (2026-08-23, pedido do usuário pra conseguir testar o fluxo
     * completo sem hardware).
     */
    private void pesoManualComAutorizacao(Produto produto) {
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                "Não foi possível ler o peso da balança de '" + produto.getNome() + "'.\n\n"
                        + "Deseja informar o peso manualmente? Isso exige autorização de gerente/supervisor.",
                ButtonType.YES, ButtonType.NO);
        confirmar.setHeaderText("Balança indisponível");
        Optional<ButtonType> resposta = confirmar.showAndWait();
        if (resposta.isEmpty() || resposta.get() != ButtonType.YES) {
            return;
        }

        Optional<PdvOperador> gerente = pedirAutorizacaoGerencial(
                "Peso manual de '" + produto.getNome() + "' (balança indisponível) "
                        + "exige autorização de Gerente ou Supervisor.");
        if (gerente.isEmpty()) return;

        TextInputDialog dialogPeso = new TextInputDialog();
        dialogPeso.setTitle("Peso manual");
        dialogPeso.setHeaderText("Peso de '" + produto.getNome() + "' em KG — autorizado por "
                + gerente.get().getColaborador().getNome());
        dialogPeso.setContentText("Peso (kg):");
        Optional<String> texto = dialogPeso.showAndWait();
        if (texto.isEmpty() || texto.get().isBlank()) return;

        BigDecimal peso;
        try {
            peso = new BigDecimal(texto.get().replace(",", ".").trim());
        } catch (NumberFormatException ex) {
            mostrarErro("Peso inválido: " + texto.get());
            return;
        }
        if (peso.compareTo(BigDecimal.ZERO) <= 0) {
            mostrarErro("Peso deve ser maior que zero.");
            return;
        }

        LOGGER.warn("Peso manual (balança indisponível): produto={}, peso={}kg, autorizado por={}",
                produto.getNome(), peso, gerente.get().getColaborador().getNome());

        adicionarProdutoVenda(produto, peso);
        tfCampoBusca.clear();
        tfCampoBusca.requestFocus();
    }

    private void adicionarProdutoVenda(Produto prod) {
        String qtdTexto = tfQuantidade.getText();
        if (qtdTexto == null || qtdTexto.isBlank()) {
            qtdTexto = "1";
            tfQuantidade.setText("1");
        }
        final BigDecimal qtde;
        try {
            qtde = new BigDecimal(qtdTexto.replace(",", "."));
        } catch (NumberFormatException e) {
            mostrarErro("Quantidade inválida: " + qtdTexto);
            return;
        }
        adicionarProdutoVenda(prod, qtde);
    }

    /**
     * Variante com quantidade explícita — usada pela leitura de código de
     * balança (peso/valor embutido no código de barras, ver
     * {@link BalancaService}), que não passa pelo campo {@code tfQuantidade}.
     */
    private void adicionarProdutoVenda(Produto prod, BigDecimal qtde) {
        final ResultadoAdicaoItem resultado;
        try {
            PdvVendaCabecalho cupom = sessaoPdv.getCupomAtivoOuNulo();
            if (cupom == null) {
                cupom = vendaService.abrirCupomSeNaoExistir(sessaoPdv.getMovimentoAtivo());
                sessaoPdv.setCupomAtivo(cupom);
                atualizarHeader();

                // Cliente já pode ter sido selecionado (F-cliente) antes do 1º item —
                // aplica agora que o cupom acabou de ser criado.
                if (clienteAtual != null) {
                    cupom = vendaService.atualizarCliente(cupom, clienteAtual);
                    sessaoPdv.setCupomAtivo(cupom);
                }
            }
            resultado = vendaService.adicionarItem(cupom, prod, qtde);
        } catch (Exception ex) {
            LOGGER.error("Falha ao persistir item no banco", ex);
            mostrarErro("Não foi possível registrar o item.\n\nMotivo: " + ex.getMessage()
                    + "\n\nO item NÃO foi adicionado à venda.");
            return;
        }

        PdvVendaDetalhe detalhe = resultado.getDetalhe();
        String rotuloFaixa = (resultado.getFaixa() != null && resultado.getFaixa().temFaixaAplicada())
                ? resultado.getFaixa().getRotulo()
                : null;
        BigDecimal precoVarejoOriginal = prod.getValorVenda();

        if (resultado.isAgrupado()) {
            atualizarItemAgrupadoNaUi(detalhe, rotuloFaixa, precoVarejoOriginal);
        } else {
            ProdutoVenda produtoVenda = new ProdutoVenda(
                    String.valueOf(detalhe.getItem()),
                    detalhe.getGtin() == null
                            ? String.valueOf(prod.getCodigoBalanca())
                            : detalhe.getGtin(),
                    prod.getDescricaoPdv(),
                    detalhe.getQuantidade(),
                    detalhe.getValorUnitario(),
                    detalhe.getValorTotal(),
                    false
            );
            produtoVenda.setRotuloFaixa(rotuloFaixa);
            produtoVenda.setPrecoVarejoOriginal(precoVarejoOriginal);
            itensVenda.add(produtoVenda);
            tabelaItens.scrollTo(itensVenda.size() - 1);
        }

        atualizarCardUltimoItem(prod, detalhe, rotuloFaixa, precoVarejoOriginal);

        tfCampoBusca.clear();
        tfQuantidade.setText("1");
        tfCampoBusca.requestFocus();

        recalcularTotaisDoResumo();
        atualizarContadores();
        atualizarVisibilidadeTabela();
        tabelaItens.refresh();
    }

    /**
     * Atualiza o card do último item bipado. Versão simples no PDV.3 —
     * o PDV.4 vai adicionar o selo ATACADO, cálculo de economia, etc.
     */
    private void atualizarCardUltimoItem(Produto prod,
                                         PdvVendaDetalhe detalhe,
                                         String rotuloFaixa,
                                         BigDecimal precoVarejoOriginal) {
        if (cardUltimoItem == null) return;

        cardUltimoItem.setVisible(true);
        cardUltimoItem.setManaged(true);

        // Imagem ou emoji fallback
        Image img = produtoImagemService.buscarImagemProduto(prod.getId());
        if (img != null) {
            imageViewProduto.setImage(img);
            imageViewProduto.setVisible(true);
            imageViewProduto.setManaged(true);
            lblImagemEmoji.setVisible(false);
            lblImagemEmoji.setManaged(false);
        } else {
            imageViewProduto.setImage(null);
            imageViewProduto.setVisible(false);
            imageViewProduto.setManaged(false);
            lblImagemEmoji.setVisible(true);
            lblImagemEmoji.setManaged(true);
        }

        // Meta (código + gtin)
        StringBuilder meta = new StringBuilder();
        if (prod.getCodigoInterno() != null) meta.append(prod.getCodigoInterno());
        if (prod.getGtin() != null) {
            if (meta.length() > 0) meta.append(" · ");
            meta.append(prod.getGtin());
        }
        lblCardMeta.setText(meta.toString());

        // Título
        lblCardTitulo.setText(prod.getNome() != null ? prod.getNome() : prod.getDescricaoPdv());

        // Qtd × preço
        lblCardQtdXPreco.setText(
                detalhe.getQuantidade().stripTrailingZeros().toPlainString()
                        + " × " + formatarMoeda(detalhe.getValorUnitario()));

        // Total
        lblCardTotal.setText(formatarMoeda(detalhe.getValorTotal()));

        // === BADGE ATACADO + ECONOMIA + ESTILOS DINÂMICOS ===
        boolean emAtacado = rotuloFaixa != null && !rotuloFaixa.isBlank();

        // Garante estado limpo (remove todas as classes dinâmicas antes)
        cardUltimoItem.getStyleClass().removeAll("pdv-card-ultimo-item-atacado", "pdv-card-ultimo-item-varejo");
        lblCardTotal.getStyleClass().removeAll("pdv-card-total-atacado", "pdv-card-total-varejo");
        lblCardEconomia.getStyleClass().removeAll("pdv-card-economia-chip", "pdv-card-economia");

        if (emAtacado) {
            // Badge ATACADO grande no topo
            lblCardBadgeAtacado.setText(rotuloFaixa);
            lblCardBadgeAtacado.setVisible(true);
            lblCardBadgeAtacado.setManaged(true);

            // Calcula economia
            BigDecimal economia = BigDecimal.ZERO;
            if (precoVarejoOriginal != null && detalhe.getValorUnitario() != null) {
                BigDecimal diferenca = precoVarejoOriginal.subtract(detalhe.getValorUnitario());
                if (diferenca.compareTo(BigDecimal.ZERO) > 0) {
                    economia = diferenca.multiply(detalhe.getQuantidade())
                            .setScale(2, RoundingMode.HALF_UP);
                }
            }

            if (economia.compareTo(BigDecimal.ZERO) > 0) {
                lblCardEconomia.setText("✓ Economia de " + formatarMoeda(economia)
                        + "  (varejo " + formatarMoeda(precoVarejoOriginal) + ")");
                lblCardEconomia.getStyleClass().add("pdv-card-economia-chip"); // CHIP branco
                lblCardEconomia.setVisible(true);
                lblCardEconomia.setManaged(true);
            } else {
                lblCardEconomia.setVisible(false);
                lblCardEconomia.setManaged(false);
            }

            // Estilos: card verde + total verde
            cardUltimoItem.getStyleClass().add("pdv-card-ultimo-item-atacado");
            lblCardTotal.getStyleClass().add("pdv-card-total-atacado");
        } else {
            lblCardBadgeAtacado.setVisible(false);
            lblCardBadgeAtacado.setManaged(false);
            lblCardEconomia.setVisible(false);
            lblCardEconomia.setManaged(false);

            // Estilos: card cinza + total preto
            cardUltimoItem.getStyleClass().add("pdv-card-ultimo-item-varejo");
            lblCardTotal.getStyleClass().add("pdv-card-total-varejo");
        }
    }

    private void atualizarItemAgrupadoNaUi(PdvVendaDetalhe detalheAtualizado,
                                           String rotuloFaixa,
                                           BigDecimal precoVarejoOriginal) {
        String numeroItem = String.valueOf(detalheAtualizado.getItem());
        for (ProdutoVenda pv : itensVenda) {
            if (numeroItem.equals(pv.getItem())) {
                pv.setQtde(detalheAtualizado.getQuantidade());
                pv.setValorUnitario(detalheAtualizado.getValorUnitario());
                pv.setValorTotal(detalheAtualizado.getValorTotal());
                pv.setRotuloFaixa(rotuloFaixa);
                pv.setPrecoVarejoOriginal(precoVarejoOriginal);
                tabelaItens.refresh();
                int idx = itensVenda.indexOf(pv);
                if (idx >= 0) tabelaItens.scrollTo(idx);
                return;
            }
        }
        LOGGER.warn("Backend informou agrupamento mas item #{} não está na ObservableList",
                numeroItem);
    }

    private BigDecimal calcularTotalLiquido() {
        BigDecimal total = BigDecimal.ZERO;
        for (ProdutoVenda item : itensVenda) {
            if (item.isCancelado()) continue;
            total = total.add(item.getValorTotal());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private void produtoNaoEncontrado() {
        LOGGER.error("Produto não encontrado");
        tfCampoBusca.clear();
        tfQuantidade.setText("1");
        tfCampoBusca.requestFocus();
    }

    private void changeAmount() {
        Platform.runLater(() -> {
            tfQuantidade.requestFocus();
            tfQuantidade.selectAll();
        });
    }

    private void iniciaValoresVendas() {
        if (lblQtdProdutos != null) lblQtdProdutos.setText("0");
        if (lblQtdItems != null) lblQtdItems.setText("0");
        if (lblQtdCancelados != null) lblQtdCancelados.setText("0");

        clienteAtual = null;
        atualizarLabelsCliente();
        recalcularTotaisDoResumo();
        atualizarVisibilidadeTabela();

        // Esconde card de último item bipado
        if (cardUltimoItem != null) {
            cardUltimoItem.setVisible(false);
            cardUltimoItem.setManaged(false);
        }
    }

    // ====================================================================
    //  Resumo da venda
    // ====================================================================
    private void recalcularTotaisDoResumo() {
        BigDecimal subtotalBruto = BigDecimal.ZERO;
        BigDecimal somaDesconto  = BigDecimal.ZERO;
        BigDecimal somaAcrescimo = BigDecimal.ZERO;
        BigDecimal somaEconomia  = BigDecimal.ZERO;

        for (ProdutoVenda item : itensVenda) {
            if (item.isCancelado()) continue;
            BigDecimal bruto = item.getValorUnitario().multiply(item.getQtde());
            subtotalBruto = subtotalBruto.add(bruto);
            somaDesconto  = somaDesconto.add(item.getValorDesconto());
            somaAcrescimo = somaAcrescimo.add(item.getValorAcrescimo());
            somaEconomia  = somaEconomia.add(item.calcularEconomia());
        }

        subtotalBruto = subtotalBruto.setScale(2, RoundingMode.HALF_UP);
        somaDesconto  = somaDesconto.setScale(2, RoundingMode.HALF_UP);
        somaAcrescimo = somaAcrescimo.setScale(2, RoundingMode.HALF_UP);
        somaEconomia  = somaEconomia.setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalLiquido = subtotalBruto.subtract(somaDesconto).add(somaAcrescimo);

        lblSubtotal.setText(formatarMoeda(subtotalBruto));
        lblDesconto.setText(formatarMoeda(somaDesconto));
        lblAcrescimo.setText(formatarMoeda(somaAcrescimo));
        lblTotal.setText(formatarMoeda(totalLiquido));

        long qtdItens = calcularQtdItens();
        BigDecimal qtdProd = calcularQtdProdutos();
        lblTotalMeta.setText(qtdItens + " " + (qtdItens == 1 ? "item" : "itens")
                + " · " + formatarQtdProdutos(qtdProd)
                + " " + (qtdProd.compareTo(BigDecimal.ONE) == 0 ? "unidade" : "unidades"));

        boolean temDesconto  = somaDesconto.compareTo(BigDecimal.ZERO) > 0;
        boolean temAcrescimo = somaAcrescimo.compareTo(BigDecimal.ZERO) > 0;
        if (linhaDesconto != null) {
            linhaDesconto.setVisible(temDesconto);
            linhaDesconto.setManaged(temDesconto);
        }
        if (linhaAcrescimo != null) {
            linhaAcrescimo.setVisible(temAcrescimo);
            linhaAcrescimo.setManaged(temAcrescimo);
        }

        // === CARD ECONOMIA (verde, esconde quando zero) ===
        boolean temEconomia = somaEconomia.compareTo(BigDecimal.ZERO) > 0;
        if (cardEconomia != null) {
            cardEconomia.setVisible(temEconomia);
            cardEconomia.setManaged(temEconomia);
        }
        if (lblEconomia != null) {
            lblEconomia.setText(formatarMoeda(somaEconomia));
        }
    }

    // ====================================================================
    //  CANCELAR ITEM (F1)
    // ====================================================================
    private void cancelarItem() {
        if (itensVenda.isEmpty()) return;

        long ativos = itensVenda.stream().filter(i -> !i.isCancelado()).count();
        if (ativos == 0) return;

        stageManager.showCancelarItem(itensVenda, this::processarCancelamentoDeItem);
    }

    private void processarCancelamentoDeItem(ProdutoVenda itemUi) {
        Optional<PdvOperador> autorizador = pedirAutorizacaoGerencial(
                "Cancelar item exige autorização de Gerente ou Supervisor.");
        if (autorizador.isEmpty()) {
            reverterCancelamentoNaUi(itemUi);
            return;
        }

        PdvVendaCabecalho cupom = sessaoPdv.getCupomAtivoOuNulo();
        if (cupom == null) {
            reverterCancelamentoNaUi(itemUi);
            mostrarErro("Não foi possível localizar o cupom ativo.");
            return;
        }

        int numeroDoItem;
        try {
            numeroDoItem = Integer.parseInt(itemUi.getItem());
        } catch (NumberFormatException e) {
            reverterCancelamentoNaUi(itemUi);
            return;
        }

        PdvVendaDetalhe detalhe = vendaService.listarDetalhes(cupom).stream()
                .filter(d -> d.getItem() != null && d.getItem() == numeroDoItem)
                .findFirst().orElse(null);

        if (detalhe == null) {
            reverterCancelamentoNaUi(itemUi);
            return;
        }

        try {
            vendaService.cancelarItem(detalhe);
        } catch (Exception ex) {
            LOGGER.error("Falha ao cancelar item", ex);
            reverterCancelamentoNaUi(itemUi);
            return;
        }

        LOGGER.info("Item #{} cancelado por {}",
                numeroDoItem, autorizador.get().getColaborador().getNome());
        tabelaItens.refresh();
        atualizarTotaisAposCancelamento();
        tfCampoBusca.requestFocus();
    }

    private void reverterCancelamentoNaUi(ProdutoVenda item) {
        item.setCancelado(false);
        tabelaItens.refresh();
    }

    private void atualizarTotaisAposCancelamento() {
        recalcularTotaisDoResumo();
        atualizarContadores();
    }

    // ====================================================================
    //  CANCELAR VENDA INTEIRA (F2)
    // ====================================================================
    private void cancelarVendaInteira() {
        if (itensVenda.isEmpty() || sessaoPdv.getCupomAtivoOuNulo() == null) return;

        long ativos = itensVenda.stream().filter(i -> !i.isCancelado()).count();
        if (ativos == 0) return;

        BigDecimal totalAtual = calcularTotalLiquido();
        Alert confirma = new Alert(Alert.AlertType.CONFIRMATION);
        confirma.setTitle("Cancelar Venda");
        confirma.setHeaderText("Cancelar TODA a venda em andamento?");
        confirma.setContentText(String.format(
                "Itens ativos: %d%nTotal: %s%n%nEsta ação não pode ser desfeita.",
                ativos, formatarMoeda(totalAtual)));

        Optional<ButtonType> resposta = confirma.showAndWait();
        if (resposta.isEmpty() || resposta.get() != ButtonType.OK) return;

        Optional<PdvOperador> autorizador = pedirAutorizacaoGerencial(
                "Cancelar a venda inteira exige autorização de Gerente ou Supervisor.");
        if (autorizador.isEmpty()) return;

        PdvVendaCabecalho cupom = sessaoPdv.getCupomAtivoOuNulo();
        try {
            vendaService.cancelarCupom(cupom);
        } catch (Exception ex) {
            LOGGER.error("Falha ao cancelar venda", ex);
            return;
        }

        // Fase 1.2 — se cupom veio de mesa/comanda importada, libera de volta
        try {
            integracaoContaService.liberarSeImportada(cupom);
        } catch (Exception ex) {
            LOGGER.warn("Falha ao liberar conta importada (cupom #{}) — "
                    + "operador deverá liberar manualmente", cupom.getId(), ex);
        }

        LOGGER.info("Venda #{} cancelada por {}",
                cupom.getId(), autorizador.get().getColaborador().getNome());

        resetarParaProximaVenda();
    }

    private void resetarParaProximaVenda() {
        itensVenda.clear();
        clienteAtual = null;
        sessaoPdv.limparCupom();

        iniciaValoresVendas();
        atualizarContadores();
        atualizarHeader();

        if (imageViewProduto != null) imageViewProduto.setImage(null);

        tfCampoBusca.clear();
        tfQuantidade.setText("1");
        tfCampoBusca.requestFocus();
    }

    /**
     * Reseta a tela do PDV para uma nova venda, após a anterior ter sido
     * finalizada com sucesso.
     *
     * <p>Limpa:
     * <ul>
     *   <li>Lista de itens da venda (itensVenda)</li>
     *   <li>Card do último item bipado (esconde)</li>
     *   <li>Cliente atual (volta pra Consumidor)</li>
     *   <li>Totalizadores do resumo (Subtotal, Total, Itens, Unidades)</li>
     *   <li>Card de Economia (esconde)</li>
     *   <li>Imagem do produto bipado</li>
     *   <li>Inputs (código vazio, quantidade=1)</li>
     * </ul>
     *
     * <p>Atualiza o header com o novo estado ("Caixa Livre") e devolve
     * o foco ao campo de busca.
     *
     * <p>Chamado pelo {@code StageManager.resetarPdv()} após
     * {@code VendaService.finalizarVenda()} ser bem-sucedido.
     */
    public void prepararParaNovaVenda() {
        // Limpa estado interno
        itensVenda.clear();
        clienteAtual = null;

        // Reseta os labels usando o método existente
        iniciaValoresVendas();
        atualizarContadores();
        atualizarVisibilidadeTabela();
        atualizarHeader();

        // Garante que a tabela renderize do zero (sem linhas fantasma)
        if (tabelaItens != null) {
            tabelaItens.refresh();
        }

        // Imagem do produto
        if (imageViewProduto != null) {
            imageViewProduto.setImage(null);
        }

        // Card "último item bipado" — esconde explicitamente
        if (cardUltimoItem != null) {
            cardUltimoItem.setVisible(false);
            cardUltimoItem.setManaged(false);
        }

        // Card Economia — esconde
        if (cardEconomia != null) {
            cardEconomia.setVisible(false);
            cardEconomia.setManaged(false);
        }

        // Inputs limpos
        if (tfCampoBusca != null) {
            tfCampoBusca.clear();
            tfCampoBusca.requestFocus();
        }
        if (tfQuantidade != null) {
            tfQuantidade.setText("1");
        }

        LOGGER.info("PDV pronto para nova venda");
    }

    /**
     * Reseta o flag de verificação de cupom pendente.
     *
     * <p>O controller é @Component Spring (singleton): mesma instância
     * persiste entre sessões. Sem este reset, o flag
     * {@code recuperacaoJaVerificada} continua {@code true} após o
     * logout, e o próximo login pula a verificação — fazendo o card
     * do último item bipado não aparecer.
     *
     * <p>Chamado pelo {@code StageManager.resetarStateDoPdvParaLogin()}.
     */
    public void notificarLogout() {
        recuperacaoJaVerificada = false;
        LOGGER.info("Pdv4Controller notificado de logout — recuperação será re-verificada no próximo login.");
    }

    /**
     * Configura a coluna PRODUTO da tabela pra renderizar o nome do
     * produto + um badge inline "ATACADO" (cor laranja) quando o item
     * está em faixa de atacado.
     *
     * <p>A cor laranja foi escolhida pra contrastar com o fundo verde
     * claro que a linha inteira ganha (.pdv-tabela-linha-atacado).
     */
    private void configurarCellFactoryProdutoComBadge() {
        produtoColumn.setCellFactory(col -> new TableCell<ProdutoVenda, String>() {
            @Override
            protected void updateItem(String descricao, boolean empty) {
                super.updateItem(descricao, empty);

                if (empty || descricao == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                ProdutoVenda pv = getTableRow() != null ? getTableRow().getItem() : null;

                if (pv != null && pv.estaEmFaixaAtacado()) {
                    // Renderiza: nome + badge
                    Label nome = new Label(descricao);
                    nome.setStyle("-fx-text-fill: #1f5a2b; -fx-font-weight: bold; -fx-font-size: 15px;");

                    Label badge = new Label(pv.getRotuloFaixa());
                    badge.getStyleClass().add("pdv-badge-atacado-mini");

                    HBox box = new HBox(8);
                    box.setAlignment(Pos.CENTER_LEFT);
                    box.getChildren().addAll(nome, badge);

                    setText(null);
                    setGraphic(box);
                } else {
                    setText(descricao);
                    setGraphic(null);
                }
            }
        });
    }

    // ====================================================================
    //  Autorização gerencial
    // ====================================================================
    private Optional<PdvOperador> pedirAutorizacaoGerencial(String contextoMensagem) {
        return AutorizacaoGerencialDialog.pedir(loginService, contextoMensagem);
    }

    // ====================================================================
    //  Contadores
    // ====================================================================
    private long calcularQtdItens() {
        return itensVenda.stream().filter(p -> !p.isCancelado()).count();
    }

    private BigDecimal calcularQtdProdutos() {
        return itensVenda.stream()
                .filter(p -> !p.isCancelado())
                .map(ProdutoVenda::getQtde)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private long calcularQtdCancelados() {
        return itensVenda.stream().filter(ProdutoVenda::isCancelado).count();
    }

    private void atualizarContadores() {
        long cancelados = calcularQtdCancelados();
        lblQtdItems.setText(String.valueOf(calcularQtdItens()));
        lblQtdProdutos.setText(formatarQtdProdutos(calcularQtdProdutos()));
        lblQtdCancelados.setText(String.valueOf(cancelados));

        // Esconde linha "Cancelados" do resumo quando não há
        if (linhaCancelados != null) {
            linhaCancelados.setVisible(cancelados > 0);
            linhaCancelados.setManaged(cancelados > 0);
        }
    }

    private String formatarQtdProdutos(BigDecimal qtd) {
        return formatarQuantidade(qtd);
    }

    private String formatarMoeda(BigDecimal v) {
        return "R$ " + FormaterCurrency.fieldFormated(v == null ? BigDecimal.ZERO : v);
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR, mensagem);
        alert.setHeaderText("Atenção");
        alert.showAndWait();
        tfCampoBusca.requestFocus();
    }

    // ====================================================================
    //  DESCONTO / ACRÉSCIMO em item (F4 / F6)
    // ====================================================================
    private void aplicarDescontoAcrescimoEmItem(TipoOperacaoDescontoAcrescimo tipo) {
        if (itensVenda.isEmpty()) return;

        long ativos = itensVenda.stream().filter(i -> !i.isCancelado()).count();
        if (ativos == 0) return;

        boolean ehDesconto = (tipo == TipoOperacaoDescontoAcrescimo.DESCONTO);
        Variante variante = ehDesconto ? Variante.DESCONTO : Variante.ACRESCIMO;
        String subtitulo = ehDesconto
                ? "Aplicar desconto no item selecionado"
                : "Aplicar acréscimo no item selecionado";

        stageManager.showSelecionarItem(
                itensVenda,
                "Selecionar item",
                subtitulo,
                "[ENTER] Selecionar",
                variante,
                itemUi -> processarItemSelecionadoParaDescontoAcrescimo(itemUi, tipo)
        );
    }

    private void processarItemSelecionadoParaDescontoAcrescimo(ProdutoVenda itemUi,
                                                               TipoOperacaoDescontoAcrescimo tipo) {
        PdvVendaCabecalho cupom = sessaoPdv.getCupomAtivoOuNulo();
        if (cupom == null) {
            mostrarErro("Não foi possível localizar o cupom ativo.");
            return;
        }

        int numeroDoItem;
        try {
            numeroDoItem = Integer.parseInt(itemUi.getItem());
        } catch (NumberFormatException e) {
            return;
        }

        PdvVendaDetalhe detalhe = vendaService.listarDetalhes(cupom).stream()
                .filter(d -> d.getItem() != null && d.getItem() == numeroDoItem)
                .findFirst().orElse(null);

        if (detalhe == null) return;

        stageManager.showDescontoAcrescimo(detalhe, tipo, atualizado -> {
            atualizarItemNaUi(itemUi, atualizado);
            tfCampoBusca.requestFocus();
        });
    }

    private void atualizarItemNaUi(ProdutoVenda itemUi, PdvVendaDetalhe atualizado) {
        itemUi.setValorTotal(atualizado.getValorTotal());
        itemUi.setValorDesconto(atualizado.getValorDesconto() != null
                ? atualizado.getValorDesconto() : BigDecimal.ZERO);
        itemUi.setValorAcrescimo(atualizado.getValorAcrescimo() != null
                ? atualizado.getValorAcrescimo() : BigDecimal.ZERO);
        tabelaItens.refresh();
        recalcularTotaisDoResumo();
    }
}