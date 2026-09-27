package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.StageManager;
import br.com.cactuserp.pdv.entity.Produto;
import br.com.cactuserp.pdv.entity.ProdutoFaixaPreco;
import br.com.cactuserp.pdv.service.FaixaPrecoService;
import br.com.cactuserp.pdv.service.ProdutoImagemService;
import br.com.cactuserp.pdv.service.ProdutoService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller da tela ConsultaProduto.fxml.
 *
 * <p>Tela de consulta read-only: o operador pesquisa um produto (por
 * código, EAN ou descrição), vê detalhes completos (preço, estoque,
 * faixas de atacado, info fiscal) mas NÃO adiciona à venda.
 *
 * <p>Acionada via F12 → CONSULTA_PRODUTO.
 */
@Component
public class ConsultaProdutoController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaProdutoController.class);
    private static final NumberFormat MOEDA =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private static final NumberFormat PERCENT_FMT;
    static {
        PERCENT_FMT = NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR"));
        PERCENT_FMT.setMinimumFractionDigits(2);
        PERCENT_FMT.setMaximumFractionDigits(2);
    }

    private final StageManager stageManager;
    private final ProdutoService produtoService;
    private final ProdutoImagemService produtoImagemService;
    private final FaixaPrecoService faixaPrecoService;

    @FXML private AnchorPane root;
    @FXML private TextField tfBusca;
    @FXML private Label lblContador;

    // Tabela
    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, String> colCodigo;
    @FXML private TableColumn<Produto, String> colDescricao;
    @FXML private TableColumn<Produto, String> colPreco;

    // Detalhe
    @FXML private VBox estadoVazio;
    @FXML private VBox painelInfo;
    @FXML private ImageView imageViewProduto;
    @FXML private Label lblEmoji;
    @FXML private Label lblNomeProduto;
    @FXML private Label lblBadgeSituacao;
    @FXML private Label lblCodigos;
    @FXML private Label lblDescricaoCompleta;
    @FXML private Label lblPrecoVenda;

    // Estoque
    @FXML private Label lblEstoqueAtual;
    @FXML private Label lblEstoqueMinimo;
    @FXML private Label lblEstoqueStatus;

    // Faixas
    @FXML private VBox blocoFaixas;
    @FXML private VBox containerFaixas;

    // Fiscal
    @FXML private Label lblNcm;
    @FXML private Label lblCest;
    @FXML private Label lblCst;
    @FXML private Label lblAliqIcms;

    private final ObservableList<Produto> resultados = FXCollections.observableArrayList();
    private Produto produtoSelecionado;

    public ConsultaProdutoController(StageManager stageManager,
                                     ProdutoService produtoService,
                                     ProdutoImagemService produtoImagemService,
                                     FaixaPrecoService faixaPrecoService) {
        this.stageManager = stageManager;
        this.produtoService = produtoService;
        this.produtoImagemService = produtoImagemService;
        this.faixaPrecoService = faixaPrecoService;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabela();
        configurarListeners();

        Platform.runLater(() -> {
            Scene scene = root.getScene();
            if (scene != null) {
                scene.setOnKeyPressed(ev -> {
                    if (ev.getCode() == KeyCode.ESCAPE) {
                        stageManager.fecharConsultaProduto();
                        ev.consume();
                        return;
                    }
                    // Atalho "/" → volta foco rapidamente ao campo de busca
                    // (não consome se o foco já está num TextField,
                    // senão atrapalharia digitar "/" no nome do produto)
                    if ("/".equals(ev.getText())
                            && !(scene.getFocusOwner() instanceof TextField)) {
                        tfBusca.requestFocus();
                        tfBusca.selectAll();
                        ev.consume();
                    }
                });
            }
            tfBusca.requestFocus();
            tfBusca.selectAll();
        });
    }

    // ====================================================================
    //  Tabela e busca
    // ====================================================================

    private void configurarTabela() {
        colCodigo.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCodigoInterno() != null
                        ? c.getValue().getCodigoInterno() : "-"));

        colDescricao.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDescricaoPdv() != null
                        ? c.getValue().getDescricaoPdv()
                        : (c.getValue().getNome() != null ? c.getValue().getNome() : "-")));

        colPreco.setCellValueFactory(c ->
                new SimpleStringProperty(formatarMoeda(c.getValue().getValorVenda())));

        tabelaProdutos.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        tabelaProdutos.setItems(resultados);

        // Seleção atualiza detalhe
        tabelaProdutos.getSelectionModel().selectedItemProperty().addListener((o, antigo, novo) -> {
            if (novo != null) {
                produtoSelecionado = novo;
                renderizarDetalhe(novo);
            }
        });
    }

    private void configurarListeners() {
        // ENTER no campo de busca → executa pesquisa
        tfBusca.setOnAction(e -> executarBusca());

        // Navegação ↓ do campo de busca → manda foco pra tabela
        tfBusca.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.DOWN && !resultados.isEmpty()) {
                tabelaProdutos.requestFocus();
                tabelaProdutos.getSelectionModel().selectFirst();
                ev.consume();
            }
        });

        // Navegação na tabela: ↑ no primeiro item ou Backspace volta pro campo
        tabelaProdutos.setOnKeyPressed(ev -> {
            int idxAtual = tabelaProdutos.getSelectionModel().getSelectedIndex();

            // Seta ↑ no primeiro item → volta pro campo de busca
            if (ev.getCode() == KeyCode.UP && idxAtual == 0) {
                tfBusca.requestFocus();
                tfBusca.positionCaret(tfBusca.getText() != null
                        ? tfBusca.getText().length() : 0);
                ev.consume();
                return;
            }

            // Backspace → volta pro campo e remove o último caractere
            if (ev.getCode() == KeyCode.BACK_SPACE) {
                tfBusca.requestFocus();
                String txt = tfBusca.getText();
                if (txt != null && !txt.isEmpty()) {
                    tfBusca.setText(txt.substring(0, txt.length() - 1));
                    tfBusca.positionCaret(tfBusca.getText().length());
                }
                ev.consume();
                return;
            }

            // ENTER na tabela → não faz nada de especial (item já está
            // selecionado, detalhe já aparece). Deixa o comportamento padrão.
        });
    }

    private void executarBusca() {
        String termo = tfBusca.getText() != null ? tfBusca.getText().trim() : "";
        if (termo.isEmpty()) {
            resultados.clear();
            lblContador.setText("");
            esconderDetalhe();
            return;
        }

        resultados.clear();

        // Tenta como código/EAN exato primeiro
        Optional<Produto> exato = produtoService.buscarPorCodigo(termo);
        if (exato.isPresent()) {
            resultados.add(exato.get());
            atualizarContador();
            // 1 resultado só: seleciona automaticamente
            tabelaProdutos.getSelectionModel().selectFirst();
            return;
        }

        // Tenta como código de balança (numérico)
        try {
            Optional<Produto> balanca = produtoService.buscarPorCodigoBalanca(Integer.valueOf(termo));
            if (balanca.isPresent()) {
                resultados.add(balanca.get());
                atualizarContador();
                tabelaProdutos.getSelectionModel().selectFirst();
                return;
            }
        } catch (NumberFormatException ignored) {
            // não é número, segue pra descrição
        }

        // Busca por descrição (LIKE)
        try {
            List<Produto> porDescricao = produtoService.buscarPorDescricao(termo);
            resultados.addAll(porDescricao);
        } catch (Exception ex) {
            LOGGER.error("Falha ao buscar por descrição", ex);
        }

        atualizarContador();
        if (!resultados.isEmpty()) {
            tabelaProdutos.getSelectionModel().selectFirst();
        } else {
            esconderDetalhe();
        }
    }

    private void atualizarContador() {
        int n = resultados.size();
        if (n == 0) lblContador.setText("Nenhum encontrado");
        else if (n == 1) lblContador.setText("1 produto");
        else lblContador.setText(n + " produtos");
    }

    // ====================================================================
    //  Renderização do detalhe
    // ====================================================================

    private void esconderDetalhe() {
        produtoSelecionado = null;
        estadoVazio.setVisible(true);
        estadoVazio.setManaged(true);
        painelInfo.setVisible(false);
        painelInfo.setManaged(false);
    }

    private void renderizarDetalhe(Produto p) {
        estadoVazio.setVisible(false);
        estadoVazio.setManaged(false);
        painelInfo.setVisible(true);
        painelInfo.setManaged(true);

        // Imagem
        Image img = produtoImagemService.buscarImagemProduto(p.getId());
        if (img != null) {
            imageViewProduto.setImage(img);
            imageViewProduto.setVisible(true);
            imageViewProduto.setManaged(true);
            lblEmoji.setVisible(false);
            lblEmoji.setManaged(false);
        } else {
            imageViewProduto.setImage(null);
            imageViewProduto.setVisible(false);
            imageViewProduto.setManaged(false);
            lblEmoji.setVisible(true);
            lblEmoji.setManaged(true);
        }

        // Nome + descrição
        lblNomeProduto.setText(p.getNome() != null ? p.getNome() : p.getDescricaoPdv());
        lblDescricaoCompleta.setText(p.getDescricao() != null ? p.getDescricao() : "");

        // Badge situação
        boolean ativo = "A".equalsIgnoreCase(p.getSituacao());
        lblBadgeSituacao.setText(ativo ? "ATIVO" : "INATIVO");
        lblBadgeSituacao.getStyleClass().removeAll("cp-badge-ativo", "cp-badge-inativo");
        lblBadgeSituacao.getStyleClass().add(ativo ? "cp-badge-ativo" : "cp-badge-inativo");

        // Códigos
        StringBuilder codigos = new StringBuilder();
        if (p.getCodigoInterno() != null) codigos.append("Código: ").append(p.getCodigoInterno());
        if (p.getGtin() != null) {
            if (codigos.length() > 0) codigos.append("  ·  ");
            codigos.append("EAN: ").append(p.getGtin());
        }
        if (p.getCodigoBalanca() != null) {
            if (codigos.length() > 0) codigos.append("  ·  ");
            codigos.append("Balança: ").append(p.getCodigoBalanca());
        }
        lblCodigos.setText(codigos.toString());

        // Preço
        lblPrecoVenda.setText(formatarMoeda(p.getValorVenda()));

        // Estoque
        renderizarEstoque(p);

        // Faixas de atacado
        renderizarFaixas(p);

        // Info fiscal
        lblNcm.setText(p.getCodigoNcm() != null && !p.getCodigoNcm().isBlank()
                ? p.getCodigoNcm() : "—");
        lblCest.setText(p.getCodigoCest() != null && !p.getCodigoCest().isBlank()
                ? p.getCodigoCest() : "—");

        String cstCsosn;
        if (p.getCst() != null && !p.getCst().isBlank()) {
            cstCsosn = "CST " + p.getCst();
        } else if (p.getCsosn() != null && !p.getCsosn().isBlank()) {
            cstCsosn = "CSOSN " + p.getCsosn();
        } else {
            cstCsosn = "—";
        }
        lblCst.setText(cstCsosn);

        if (p.getTaxaIcms() != null) {
            lblAliqIcms.setText(PERCENT_FMT.format(p.getTaxaIcms()) + " %");
        } else {
            lblAliqIcms.setText("—");
        }
    }

    private void renderizarEstoque(Produto p) {
        BigDecimal atual = p.getQuantidadeEstoque();
        BigDecimal minimo = p.getEstoqueMinimo();

        lblEstoqueAtual.setText(formatarQuantidade(atual));
        lblEstoqueMinimo.setText(formatarQuantidade(minimo));

        // Status
        lblEstoqueStatus.getStyleClass().removeAll("cp-status-ok", "cp-status-baixo", "cp-status-zero");
        if (atual == null || atual.compareTo(BigDecimal.ZERO) <= 0) {
            lblEstoqueStatus.setText("⚠ Sem estoque");
            lblEstoqueStatus.getStyleClass().add("cp-status-zero");
        } else if (minimo != null && atual.compareTo(minimo) <= 0) {
            lblEstoqueStatus.setText("⚠ Abaixo do mínimo");
            lblEstoqueStatus.getStyleClass().add("cp-status-baixo");
        } else {
            lblEstoqueStatus.setText("✓ OK");
            lblEstoqueStatus.getStyleClass().add("cp-status-ok");
        }
    }

    private void renderizarFaixas(Produto p) {
        containerFaixas.getChildren().clear();

        List<ProdutoFaixaPreco> faixas = faixaPrecoService.listarFaixasVigentes(p.getId());
        if (faixas == null || faixas.isEmpty()) {
            blocoFaixas.setVisible(false);
            blocoFaixas.setManaged(false);
            return;
        }

        blocoFaixas.setVisible(true);
        blocoFaixas.setManaged(true);

        BigDecimal precoVarejo = p.getValorVenda();

        for (ProdutoFaixaPreco f : faixas) {
            HBox linha = new HBox(10);
            linha.getStyleClass().add("cp-faixa-linha");

            Label rotulo = new Label(f.getRotulo() != null ? f.getRotulo()
                    : "A partir de " + formatarQuantidade(f.getQuantidadeMinima()));
            rotulo.getStyleClass().add("cp-faixa-rotulo");

            Label qtd = new Label("(" + formatarQuantidade(f.getQuantidadeMinima()) + "+)");
            qtd.getStyleClass().add("cp-faixa-qtd");

            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            Label valor = new Label("R$ " + formatarMoedaSemPrefixo(f.getValorUnitario()));
            valor.getStyleClass().add("cp-faixa-valor");

            String economia = "";
            if (precoVarejo != null && f.getValorUnitario() != null
                    && precoVarejo.compareTo(f.getValorUnitario()) > 0) {
                BigDecimal diff = precoVarejo.subtract(f.getValorUnitario());
                economia = "  (economia R$ " + formatarMoedaSemPrefixo(diff) + ")";
            }
            Label lblEconomia = new Label(economia);
            lblEconomia.getStyleClass().add("cp-faixa-economia");

            linha.getChildren().addAll(rotulo, qtd, spacer, valor, lblEconomia);
            containerFaixas.getChildren().add(linha);
        }
    }

    // ====================================================================
    //  Helpers
    // ====================================================================

    private String formatarMoeda(BigDecimal v) {
        if (v == null) return "R$ 0,00";
        return MOEDA.format(v);
    }

    private String formatarMoedaSemPrefixo(BigDecimal v) {
        if (v == null) return "0,00";
        NumberFormat n = NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR"));
        n.setMinimumFractionDigits(2);
        n.setMaximumFractionDigits(2);
        return n.format(v);
    }

    private String formatarQuantidade(BigDecimal qtd) {
        if (qtd == null) return "0";
        BigDecimal limpo = qtd.stripTrailingZeros();
        if (limpo.scale() <= 0) {
            return limpo.toBigInteger().toString();
        }
        return limpo.toPlainString().replace(".", ",");
    }

    /**
     * Reseta o estado interno do controller pra uma nova consulta.
     *
     * <p>O controller é @Component Spring (singleton): mesma instância
     * persiste entre aberturas. Sem este reset, a busca anterior fica
     * visível ao reabrir a tela.
     *
     * <p>Chamado pelo {@code StageManager.showConsultaProduto()} antes
     * de mostrar a tela.
     */
    public void prepararParaNovaConsulta() {
        resultados.clear();
        produtoSelecionado = null;

        if (tfBusca != null) {
            tfBusca.clear();
            tfBusca.requestFocus();
        }
        if (lblContador != null) {
            lblContador.setText("");
        }

        esconderDetalhe();
    }
}