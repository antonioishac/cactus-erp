package br.com.cactuserp.pdv.config;

import br.com.cactuserp.pdv.controllers.*;
import br.com.cactuserp.pdv.controllers.SelecionarItemController.Variante;
import br.com.cactuserp.pdv.entity.*;
import br.com.cactuserp.pdv.service.FechamentoMovimentoService;
import br.com.cactuserp.pdv.service.dto.PesquisarProdutoDTO;
import br.com.cactuserp.pdv.service.dto.ProdutoVenda;
import br.com.cactuserp.pdv.service.dto.RelatorioZ;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

import br.com.cactuserp.pdv.controllers.AberturaComandaController;
import br.com.cactuserp.pdv.controllers.ComandasAbertasController;
import br.com.cactuserp.pdv.controllers.LancamentoComandaController;

import br.com.cactuserp.pdv.controllers.BuscarContaController;
import br.com.cactuserp.pdv.controllers.ImportarContaSeletorController;
import br.com.cactuserp.pdv.entity.TipoContaAberta;

@Component
public class StageManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(StageManager.class);

    public static ApplicationContext springContext;
    private final SessaoPdv sessaoPdv;
    private Parent cenaAnteriorConsultaProduto;
    private Scene cenaQueAbriuConsulta;
    private Parent cenaAnteriorConsultaCupom;
    private Scene cenaQueAbriuConsultaCupom;
    private Parent cenaAnteriorSincronizacao;
    private Scene cenaQueAbriuSincronizacao;
    private Parent cenaAnteriorConferencia;
    private Scene cenaQueAbriuConferencia;
    private Stage primaryStage;

    private Parent cenaAnteriorSalao;
    private Scene cenaQueAbriuSalao;
    private Parent cenaAnteriorLancamento;
    private Scene cenaQueAbriuLancamento;

    private Parent cenaAnteriorComandas;
    private Scene cenaQueAbriuComandas;
    private Parent cenaAnteriorLancamentoComanda;
    private Scene cenaQueAbriuLancamentoComanda;
    private Runnable callbackAposLancamentoComanda;

    private Parent cenaAnteriorBuscarConta;
    private Scene cenaQueAbriuBuscarConta;

    // Cache da tela do PDV — preserva itens da venda em andamento
    private Parent telaPdvCache;
    private Pdv4Controller pdv4ControllerCache;
    private boolean jaMostrouPdv = false;

    /** Callback executado ao fechar o lançamento (recarrega salão). */
    private Runnable callbackAposLancamento;

    public StageManager(ApplicationContext springContext, SessaoPdv sessaoPdv) {
        this.springContext = springContext;
        this.sessaoPdv = sessaoPdv;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showLoginScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            // CRÍTICO: desmaximizar ANTES de configurar a Login.
            // Se o Stage estiver maximizado vindo do PDV, setSize+setResizable(false)
            // numa janela maximizada cria o bug do "canto superior".
            primaryStage.setMaximized(false);
            primaryStage.setResizable(true); // primeiro libera resize pra poder mudar tamanho

            Scene scene = new Scene(root, 650, 500);
            scene.getStylesheets().add(getClass().getResource("/fxml/styles/main.css").toExternalForm());

            primaryStage.setTitle("PDV System - Login");
            primaryStage.setScene(scene);
            primaryStage.setWidth(650);
            primaryStage.setHeight(500);
            primaryStage.centerOnScreen();
            primaryStage.setResizable(false); // agora trava
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mostra a tela principal do PDV.
     *
     * <p>Comportamento por cenário:
     * <ul>
     *   <li><b>Primeira chamada</b> (vindo do login): cria Scene nova,
     *       libera resize e maximiza a janela.</li>
     *   <li><b>Chamadas subsequentes</b> (voltando de encerramento,
     *       finalizar venda, menu, etc): reusa a Scene atual trocando
     *       só o root, preservando o estado maximizado da janela.</li>
     * </ul>
     */
    public void showPdv4Scene() {
        try {
            if (telaPdvCache == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Pdv4.fxml"));
                loader.setControllerFactory(springContext::getBean);
                telaPdvCache = loader.load();
                pdv4ControllerCache = loader.getController();
            }

            primaryStage.setTitle("PDV System - Frente de Caixa");

            String cssPdv = getClass().getResource("/fxml/styles/CactusPdv.css").toExternalForm();

            if (jaMostrouPdv && primaryStage.getScene() != null) {
                // Já estamos numa Scene do PDV — só troca o root.
                Scene cenaAtual = primaryStage.getScene();
                cenaAtual.setRoot(telaPdvCache);

                if (!cenaAtual.getStylesheets().contains(cssPdv)) {
                    cenaAtual.getStylesheets().add(cssPdv);
                }

                Platform.runLater(() -> {
                    if (pdv4ControllerCache != null) {
                        pdv4ControllerCache.reativarAtalhos();
                    }
                });
                return;
            }

            // PRIMEIRA execução do PDV (vindo de Login ou de logout+login).
            // Ordem CRÍTICA no Windows:
            //   1. desmaximizar (caso esteja maximizado de antes)
            //   2. liberar resize
            //   3. setScene
            //   4. setMaximized(true)
            primaryStage.setMaximized(false);
            primaryStage.setResizable(true);

            Scene scene = new Scene(telaPdvCache, 1280, 800);
            scene.getStylesheets().add(cssPdv);

            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);

            jaMostrouPdv = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Variante simples pra quem só quer o código de volta num campo de
     * texto (comportamento original — {@code LancamentoComandaController}/
     * {@code LancamentoMesaController}): preenche {@code tfCodigo} com o
     * código resolvido (GTIN ou balança) do produto escolhido.
     */
    public void showPesquisarProdutos(TextField tfCodigo) {
        showPesquisarProdutos(produto -> {
            tfCodigo.setText(produto.getCodigo());
            tfCodigo.requestFocus();
            tfCodigo.selectEnd();
        });
    }

    /**
     * Base — devolve o {@link PesquisarProdutoDTO} completo (não só um
     * código de texto), pra quem precisar decidir de forma diferente por
     * produto — ex: {@code Pdv4Controller}, que pesa ao vivo na hora se o
     * produto escolhido for vendido por peso, em vez de só preencher um
     * campo (esse produto pode nem ter GTIN/código de balança cadastrado —
     * exigir um deles pra selecionar foi um bug real, corrigido 2026-08-23).
     */
    public void showPesquisarProdutos(Consumer<PesquisarProdutoDTO> onProdutoSelecionado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PesquisarProdutos.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            PesquisaProdutoController controller = loader.getController();
            controller.setOnProdutoSelecionado(onProdutoSelecionado);

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Buscar Produto");
            popup.setScene(new Scene(root, 950.0, 600));
            popup.setResizable(false);

            controller.setStage(popup);
            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showPesquisarClientes(Consumer<Cliente> onClienteSelecionado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PesquisaCliente.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            PesquisaClienteController controller = loader.getController();
            controller.setOnClienteSelecionado(onClienteSelecionado);

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Pesquisar Clientes");

            Scene scene = new Scene(root, 950.0, 600);
            scene.getStylesheets().add(
                    getClass().getResource("/fxml/styles/CactusPdv.css").toExternalForm()
            );
            popup.setResizable(false);
            popup.setScene(scene);

            controller.setStage(popup);
            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showMenuPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuPrincipal.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            MenuPrincipalController controller = loader.getController();

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Menu Principal");
            popup.setResizable(false);

            Scene scene = new Scene(root, 900.0, 750);
            popup.setScene(scene);

            controller.setStage(popup);
            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showIniciarMovimento(PdvOperador operadorPreSelecionado,
                                     PdvCaixa caixaPreSelecionado,
                                     Consumer<PdvMovimento> onMovimentoAberto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/IniciarMovimento.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            IniciarMovimentoController controller = loader.getController();
            controller.setOnMovimentoAberto(onMovimentoAberto);
            controller.preSelecionar(operadorPreSelecionado, caixaPreSelecionado);

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Iniciar Movimento");
            popup.setResizable(false);

            Scene scene = new Scene(root, 950.0, 500);
            scene.getStylesheets().add(
                    getClass().getResource("/fxml/styles/CactusPdv.css").toExternalForm()
            );
            popup.setScene(scene);

            controller.setStage(popup);
            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showEncerramentoVenda(BigDecimal valor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EncerramentoVenda.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            EncerramentoVendaController controller = loader.getController();

            // Ordem importa:
            //   1. setValorTotal — atualiza o total da NOVA venda
            //   2. prepararParaNovaVenda — limpa pagamentos antigos e
            //      re-seleciona forma padrão (que vai pré-preencher
            //      o campo de valor com o saldo restante usando o
            //      total CORRETO da nova venda)
            //   3. totalVendaInitialize — recarrega desconto/acréscimo
            controller.setValorTotal(valor);
            controller.prepararParaNovaVenda();

            Platform.runLater(controller::totalVendaInitialize);

            primaryStage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cancela o encerramento (F5) e volta à tela do PDV preservando os itens.
     * Reusa o cache E reativa o handler de teclas do PDV
     * (que foi sobrescrito pelo EncerramentoVendaController).
     */
    public void showEncerramentoParaVenda() {
        if (telaPdvCache == null) {
            showPdv4Scene();
            return;
        }

        Scene scene = primaryStage.getScene();
        scene.setRoot(telaPdvCache);

        String cssPdv = getClass().getResource("/fxml/styles/CactusPdv.css").toExternalForm();
        if (!scene.getStylesheets().contains(cssPdv)) {
            scene.getStylesheets().add(cssPdv);
        }

        Platform.runLater(() -> {
            if (pdv4ControllerCache != null) {
                pdv4ControllerCache.reativarAtalhos();
            }
        });
    }

    /**
     * Prepara a tela do PDV para uma nova venda, após finalizar a anterior.
     *
     * <p>Em vez de descartar o cache (custoso — recarrega FXML), reusa o
     * controller existente chamando {@code prepararParaNovaVenda()}, que
     * zera o estado interno: itensVenda, card último item, labels do
     * resumo, cliente atual, etc.
     *
     * <p>Também limpa o cupom ativo da sessão.
     */
    public void resetarPdv() {
        sessaoPdv.limparCupom();
        if (pdv4ControllerCache != null) {
            pdv4ControllerCache.prepararParaNovaVenda();
        }
        // NÃO resetamos jaMostrouPdv aqui — o operador continua na
        // mesma sessão, próxima venda deve reusar Scene atual.
    }

    /**
     * Sinaliza que o operador saiu da tela do PDV e voltou para o login.
     * O próximo showPdv4Scene tratará como "primeira execução" e
     * maximizará a janela novamente.
     *
     * <p>Também notifica o controller (se ainda em cache) para que o
     * flag de "recuperação já verificada" seja resetado — garante que
     * o próximo login re-verifique se há cupom pendente.
     *
     * <p>Use sempre que voltar para a tela de login (logout, fechar
     * movimento, etc).
     */
    public void resetarStateDoPdvParaLogin() {
        // Notifica o controller ANTES de zerar a referência
        if (pdv4ControllerCache != null) {
            pdv4ControllerCache.notificarLogout();
        }
        telaPdvCache = null;
        pdv4ControllerCache = null;
        sessaoPdv.limparCupom();
        jaMostrouPdv = false;
    }

    /**
     * Notifica o Pdv4Controller (se ainda em cache) que o estado do
     * movimento mudou. Usado após operações de Fechar/Abrir movimento
     * vindas do MenuPrincipal.
     *
     * <p>Se a tela do PDV não estiver em cache (ex: primeira execução
     * ou logo após resetarPdv), o método não faz nada — o próximo
     * showPdv4Scene fará o trabalho via initialize().
     */
    public void notificarPdvSobreMudancaDeMovimento() {
        if (pdv4ControllerCache != null) {
            pdv4ControllerCache.notificarMudancaDeMovimento();
        }
    }

    public void showCancelarItem(
            ObservableList<ProdutoVenda> itens,
            Consumer<ProdutoVenda> onCancelado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CancelarItem.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            CancelarItemController controller = loader.getController();

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Cancelar Item");

            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(
                    getClass().getResource("/fxml/styles/CactusPdv.css").toExternalForm()
            );
            popup.setResizable(false);
            popup.setScene(scene);

            controller.setStage(popup);
            controller.setOnItemCancelado(onCancelado);
            controller.carregarItens(itens);

            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre a modal de recuperação de cupom em aberto.
     *
     * <p>A modal é <b>travada</b>: o operador é obrigado a escolher entre
     * Retomar ou Cancelar. Não há X nem ESC.
     *
     * @param cupom     cupom em status A encontrado no movimento ativo
     * @param onDecisao callback que recebe a escolha do operador
     */
    public void showRecuperarCupom(
            PdvVendaCabecalho cupom,
            Consumer<RecuperarCupomController.Resultado> onDecisao) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RecuperarCupom.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            RecuperarCupomController controller = loader.getController();

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Recuperação de Cupom");
            popup.setResizable(false);

            // Remove o botão de fechar da janela.
            // Combina com o setOnCloseRequest(ev.consume()) feito no controller.
            popup.initStyle(javafx.stage.StageStyle.UTILITY);

            Scene scene = new Scene(root, 560.0, 460);
            popup.setScene(scene);

            controller.setStage(popup);
            controller.setOnDecisao(onDecisao);
            controller.carregar(cupom);

            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre a modal de Sangria ou Suprimento (mesma tela, parametrizada).
     *
     * @param tipo SUPRIMENTO ou SANGRIA
     */
    public void showOperacaoCaixa(TipoOperacaoCaixa tipo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OperacaoCaixa.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            OperacaoCaixaController controller = loader.getController();

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle(tipo.getRotulo());
            popup.setResizable(false);

            Scene scene = new Scene(root, 520.0, 500);
            popup.setScene(scene);

            controller.setStage(popup);
            controller.configurar(tipo);

            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre a modal de aplicar desconto ou acréscimo num item específico
     * do cupom ativo.
     *
     * @param item       item de venda alvo
     * @param tipo       DESCONTO ou ACRESCIMO
     * @param onAplicado callback executado após aplicação bem-sucedida
     *                   (recebe o detalhe atualizado)
     */
    public void showDescontoAcrescimo(PdvVendaDetalhe item,
                                      TipoOperacaoDescontoAcrescimo tipo,
                                      Consumer<PdvVendaDetalhe> onAplicado) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/DescontoAcrescimo.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            DescontoAcrescimoController controller = loader.getController();

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle(tipo.getRotulo());
            popup.setResizable(false);

            Scene scene = new Scene(root, 440.0, 560);
            popup.setScene(scene);

            controller.setStage(popup);
            controller.setOnItemAplicado(onAplicado);
            controller.configurarItem(item, tipo);

            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre a modal de aplicar desconto/acréscimo na VENDA TOTAL.
     * A redistribuição proporcional entre os itens é feita pelo service.
     *
     * @param cupom      cupom alvo
     * @param valorBase  subtotal líquido pós-F6 (já considera desc. de item)
     * @param tipo       DESCONTO ou ACRESCIMO
     * @param onAplicada callback executado após aplicação bem-sucedida
     */
    public void showDescontoAcrescimoVenda(PdvVendaCabecalho cupom,
                                           BigDecimal valorBase,
                                           TipoOperacaoDescontoAcrescimo tipo,
                                           Runnable onAplicada) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/DescontoAcrescimo.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            DescontoAcrescimoController controller = loader.getController();

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle(tipo.getRotulo() + " na Venda");
            popup.setResizable(false);

            Scene scene = new Scene(root, 440.0, 560);
            popup.setScene(scene);

            controller.setStage(popup);
            controller.setOnVendaAplicada(onAplicada);
            controller.configurarVendaTotal(cupom, valorBase, tipo);

            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre a tela genérica de seleção de item da venda.
     *
     * @param itens          lista de itens (cancelados são filtrados automaticamente)
     * @param titulo         título grande (ex: "Selecionar item")
     * @param subtitulo      contexto (ex: "Aplicar desconto no item selecionado")
     * @param textoConfirmar texto do botão (ex: "[ENTER] Selecionar")
     * @param variante       cor do header e botão (DESCONTO/ACRESCIMO/CANCELAR/NEUTRO)
     * @param onSelecionado  callback executado quando o operador confirma
     */
    public void showSelecionarItem(List<ProdutoVenda> itens,
                                   String titulo,
                                   String subtitulo,
                                   String textoConfirmar,
                                   Variante variante,
                                   Consumer<ProdutoVenda> onSelecionado) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/SelecionarItem.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            SelecionarItemController controller = loader.getController();

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle(titulo);
            popup.setResizable(false);

            Scene scene = new Scene(root, 440.0, 500);
            popup.setScene(scene);

            controller.setStage(popup);
            controller.setOnSelecionado(onSelecionado);
            controller.configurar(titulo, subtitulo, textoConfirmar, variante, itens);

            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Abre a modal do Relatório Z para fechamento do movimento ativo.
     *
     * <p>Se houver cupom em aberto, mostra Alert bloqueando e retorna sem
     * abrir a tela (regra fiscal PAF-NFC-e — não pode fechar caixa com
     * cupom órfão).
     *
     * @param onConcluido callback executado ao fechar o movimento com sucesso
     *                    (use para encerrar sessão + voltar ao login)
     */
    public void showFecharMovimento(Runnable onConcluido) {
        PdvMovimento movimento = sessaoPdv.getMovimentoAtivoOuNulo();
        if (movimento == null) {
            Alert err = new Alert(Alert.AlertType.ERROR,
                    "Não há movimento ativo na sessão.");
            err.setHeaderText("Atenção");
            err.showAndWait();
            return;
        }

        // Bloqueia se houver cupom em aberto
        FechamentoMovimentoService fechamentoService =
                springContext.getBean(FechamentoMovimentoService.class);
        if (fechamentoService.temCupomAberto(movimento)) {
            Alert bloq = new Alert(Alert.AlertType.WARNING,
                    "Existe um cupom em aberto neste movimento.\n\n"
                            + "Retome ou cancele o cupom antes de fechar o caixa.");
            bloq.setHeaderText("Caixa não pode ser fechado");
            bloq.showAndWait();
            return;
        }

        // Consolida o snapshot
        RelatorioZ z = fechamentoService.consolidarRelatorioZ(movimento);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RelatorioZ.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            RelatorioZController controller = loader.getController();

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Fechamento de Caixa — Relatório Z");
            popup.setResizable(false);

            Scene scene = new Scene(root, 650.0, 780);
            popup.setScene(scene);

            controller.setStage(popup);
            controller.setOnFechamentoConcluido(onConcluido);
            controller.carregar(z);

            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mostra a tela de bloqueio (full-screen overlay sobre o PDV).
     *
     * <p>A tela bloqueia toda a UI até alguém digitar a senha correta.
     * Quando desbloqueada, simplesmente volta pra cena anterior do PDV
     * — mantendo todo o estado em memória (cupom, itens, cliente, etc).
     *
     * @param operadorQueBloqueou operador da sessão atual (vai aparecer
     *                            como "Bloqueado por: Fulano")
     */
    public void showTelaBloqueio(PdvOperador operadorQueBloqueou) {
        try {
            // Guarda a cena atual pra restaurar depois
            Scene cenaAnterior = primaryStage.getScene();
            Parent rootAnterior = cenaAnterior != null ? cenaAnterior.getRoot() : null;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TelaBloqueio.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent rootBloqueio = loader.load();

            TelaBloqueioController controller = loader.getController();
            controller.configurar(operadorQueBloqueou, () -> {
                // Callback de desbloqueio: restaura a tela anterior
                if (cenaAnterior != null && rootAnterior != null) {
                    cenaAnterior.setRoot(rootAnterior);
                    primaryStage.setScene(cenaAnterior);

                    // Devolve foco ao PDV
                    if (pdv4ControllerCache != null) {
                        Platform.runLater(pdv4ControllerCache::reativarAtalhos);
                    }
                }
            });

            // Aplica o CSS do PDV (que tem os estilos de bloqueio)
            String cssPdv = getClass().getResource("/fxml/styles/pdv-frente-caixa.css")
                    .toExternalForm();

            // Troca o root da cena pelo overlay de bloqueio
            // (mantém a mesma Scene pra preservar tamanho/maximize)
            if (cenaAnterior != null) {
                cenaAnterior.setRoot(rootBloqueio);
                if (!cenaAnterior.getStylesheets().contains(cssPdv)) {
                    cenaAnterior.getStylesheets().add(cssPdv);
                }
            } else {
                // Fallback (não deveria acontecer)
                Scene cena = new Scene(rootBloqueio);
                cena.getStylesheets().add(cssPdv);
                primaryStage.setScene(cena);
            }

        } catch (IOException e) {
            LOGGER.error("Falha ao abrir tela de bloqueio", e);
            new Alert(Alert.AlertType.ERROR,
                    "Falha ao abrir tela de bloqueio: " + e.getMessage())
                    .showAndWait();
        }
    }

    /**
     * Abre a tela cheia de Consulta de Produto. Faz overlay sobre a
     * cena atual (PDV) e restaura ao fechar (ESC ou comando explícito).
     */
    public void showConsultaProduto() {
        try {
            // Guarda a cena atual pra restaurar depois
            Scene cenaAtual = primaryStage.getScene();
            cenaQueAbriuConsulta = cenaAtual;
            cenaAnteriorConsultaProduto = cenaAtual != null ? cenaAtual.getRoot() : null;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConsultaProduto.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent rootConsulta = loader.load();

            ConsultaProdutoController controller = loader.getController();
            // Reset crítico — limpa busca/seleção da consulta anterior
            controller.prepararParaNovaConsulta();

            String cssConsulta = getClass().getResource("/fxml/styles/consulta-produto.css")
                    .toExternalForm();

            if (cenaAtual != null) {
                cenaAtual.setRoot(rootConsulta);
                if (!cenaAtual.getStylesheets().contains(cssConsulta)) {
                    cenaAtual.getStylesheets().add(cssConsulta);
                }
            } else {
                Scene cena = new Scene(rootConsulta);
                cena.getStylesheets().add(cssConsulta);
                primaryStage.setScene(cena);
            }
        } catch (IOException e) {
            LOGGER.error("Falha ao abrir Consulta de Produto", e);
            new Alert(Alert.AlertType.ERROR,
                    "Falha ao abrir Consulta de Produto: " + e.getMessage())
                    .showAndWait();
        }
    }

    /**
     * Fecha a tela de Consulta de Produto e volta pra cena anterior.
     * Chamado pelo controller quando o operador aperta ESC.
     */
    public void fecharConsultaProduto() {
        if (cenaQueAbriuConsulta != null && cenaAnteriorConsultaProduto != null) {
            cenaQueAbriuConsulta.setRoot(cenaAnteriorConsultaProduto);

            // Devolve foco pro PDV
            if (pdv4ControllerCache != null) {
                Platform.runLater(pdv4ControllerCache::reativarAtalhos);
            }

            cenaQueAbriuConsulta = null;
            cenaAnteriorConsultaProduto = null;
        }
    }

    /**
     * Abre a tela cheia de Consulta de Cupom. Overlay sobre a cena
     * atual (PDV) e restaura ao fechar (ESC ou comando explícito).
     */
    public void showConsultaCupom() {
        try {
            Scene cenaAtual = primaryStage.getScene();
            cenaQueAbriuConsultaCupom = cenaAtual;
            cenaAnteriorConsultaCupom = cenaAtual != null ? cenaAtual.getRoot() : null;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConsultaCupom.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent rootConsulta = loader.load();

            ConsultaCupomController controller = loader.getController();
            controller.prepararParaNovaConsulta();

            String cssConsulta = getClass().getResource("/fxml/styles/consulta-produto.css")
                    .toExternalForm();

            if (cenaAtual != null) {
                cenaAtual.setRoot(rootConsulta);
                if (!cenaAtual.getStylesheets().contains(cssConsulta)) {
                    cenaAtual.getStylesheets().add(cssConsulta);
                }
            } else {
                Scene cena = new Scene(rootConsulta);
                cena.getStylesheets().add(cssConsulta);
                primaryStage.setScene(cena);
            }
        } catch (IOException e) {
            LOGGER.error("Falha ao abrir Consulta de Cupom", e);
            new Alert(Alert.AlertType.ERROR,
                    "Falha ao abrir Consulta de Cupom: " + e.getMessage())
                    .showAndWait();
        }
    }

    /**
     * Abre a tela de Sincronização com o ERP (F12 → Sincronização) como overlay da
     * cena atual — mesmo padrão da Consulta de Cupom: restaura a cena ao fechar.
     */
    public void showSincronizacaoErp() {
        try {
            Scene cenaAtual = primaryStage.getScene();
            cenaQueAbriuSincronizacao = cenaAtual;
            cenaAnteriorSincronizacao = cenaAtual != null ? cenaAtual.getRoot() : null;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SincronizacaoErp.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent rootSincronizacao = loader.load();
            SincronizacaoErpController controller = loader.getController();

            String css = getClass().getResource("/fxml/styles/consulta-produto.css").toExternalForm();
            if (cenaAtual != null) {
                cenaAtual.setRoot(rootSincronizacao);
                if (!cenaAtual.getStylesheets().contains(css)) {
                    cenaAtual.getStylesheets().add(css);
                }
            } else {
                Scene cena = new Scene(rootSincronizacao);
                cena.getStylesheets().add(css);
                primaryStage.setScene(cena);
            }
            controller.prepararParaAbertura();
        } catch (IOException e) {
            LOGGER.error("Falha ao abrir Sincronização com o ERP", e);
            new Alert(Alert.AlertType.ERROR, "Falha ao abrir Sincronização com o ERP: " + e.getMessage()).showAndWait();
        }
    }

    /** Fecha a tela de Sincronização e volta pra cena anterior. */
    public void fecharSincronizacaoErp() {
        if (cenaQueAbriuSincronizacao != null && cenaAnteriorSincronizacao != null) {
            cenaQueAbriuSincronizacao.setRoot(cenaAnteriorSincronizacao);
            if (pdv4ControllerCache != null) {
                Platform.runLater(pdv4ControllerCache::reativarAtalhos);
            }
            cenaQueAbriuSincronizacao = null;
            cenaAnteriorSincronizacao = null;
        }
    }

    /**
     * Fecha a tela de Consulta de Cupom e volta pra cena anterior.
     */
    public void fecharConsultaCupom() {
        if (cenaQueAbriuConsultaCupom != null && cenaAnteriorConsultaCupom != null) {
            cenaQueAbriuConsultaCupom.setRoot(cenaAnteriorConsultaCupom);

            if (pdv4ControllerCache != null) {
                Platform.runLater(pdv4ControllerCache::reativarAtalhos);
            }

            cenaQueAbriuConsultaCupom = null;
            cenaAnteriorConsultaCupom = null;
        }
    }

    /**
     * Abre a tela cheia de Conferência de Caixa. Overlay sobre a cena
     * atual (PDV) e restaura ao fechar.
     */
    public void showConferenciaCaixa(PdvOperador gerenteAutorizador) {
        try {
            Scene cenaAtual = primaryStage.getScene();
            cenaQueAbriuConferencia = cenaAtual;
            cenaAnteriorConferencia = cenaAtual != null ? cenaAtual.getRoot() : null;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConferenciaCaixa.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent rootConf = loader.load();

            ConferenciaCaixaController controller = loader.getController();
            controller.configurar(gerenteAutorizador);

            String cssCp = getClass().getResource("/fxml/styles/consulta-produto.css")
                    .toExternalForm();

            if (cenaAtual != null) {
                cenaAtual.setRoot(rootConf);
                if (!cenaAtual.getStylesheets().contains(cssCp)) {
                    cenaAtual.getStylesheets().add(cssCp);
                }
            } else {
                Scene cena = new Scene(rootConf);
                cena.getStylesheets().add(cssCp);
                primaryStage.setScene(cena);
            }
        } catch (IOException e) {
            LOGGER.error("Falha ao abrir Conferência de Caixa", e);
            new Alert(Alert.AlertType.ERROR,
                    "Falha ao abrir Conferência: " + e.getMessage()).showAndWait();
        }
    }

    /**
     * Fecha a tela de Conferência e volta pra cena anterior.
     */
    public void fecharConferenciaCaixa() {
        if (cenaQueAbriuConferencia != null && cenaAnteriorConferencia != null) {
            cenaQueAbriuConferencia.setRoot(cenaAnteriorConferencia);

            if (pdv4ControllerCache != null) {
                Platform.runLater(pdv4ControllerCache::reativarAtalhos);
            }

            cenaQueAbriuConferencia = null;
            cenaAnteriorConferencia = null;
        }
    }

    /**
     * Abre a tela cheia do Salão de Mesas (overlay sobre o PDV).
     */
    public void showSalaoMesas() {
        try {
            Scene cenaAtual = primaryStage.getScene();
            cenaQueAbriuSalao = cenaAtual;
            cenaAnteriorSalao = cenaAtual != null ? cenaAtual.getRoot() : null;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SalaoMesas.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent rootSalao = loader.load();

            SalaoMesasController controller = loader.getController();
            controller.carregar();

            String cssSalao = getClass().getResource("/fxml/styles/salao-mesas.css")
                    .toExternalForm();

            if (cenaAtual != null) {
                cenaAtual.setRoot(rootSalao);
                if (!cenaAtual.getStylesheets().contains(cssSalao)) {
                    cenaAtual.getStylesheets().add(cssSalao);
                }
            } else {
                Scene cena = new Scene(rootSalao);
                cena.getStylesheets().add(cssSalao);
                primaryStage.setScene(cena);
            }
        } catch (IOException e) {
            LOGGER.error("Falha ao abrir Salão de Mesas", e);
            new Alert(Alert.AlertType.ERROR,
                    "Falha ao abrir Salão de Mesas: " + e.getMessage()).showAndWait();
        }
    }

    /**
     * Fecha o Salão de Mesas e volta pra cena anterior (PDV principal).
     */
    public void fecharSalaoMesas() {
        if (cenaQueAbriuSalao != null && cenaAnteriorSalao != null) {
            cenaQueAbriuSalao.setRoot(cenaAnteriorSalao);

            if (pdv4ControllerCache != null) {
                Platform.runLater(pdv4ControllerCache::reativarAtalhos);
            }

            cenaQueAbriuSalao = null;
            cenaAnteriorSalao = null;
        }
    }

    /**
     * Abre o dialog modal de abertura de mesa.
     *
     * @param mesa     mesa selecionada
     * @param onAberta callback (recebe a conta recém-criada)
     */
    public void showAberturaMesa(Mesa mesa, Runnable onAberta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AberturaMesa.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            AberturaMesaController controller = loader.getController();

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Abrir Mesa " + mesa.getNumero());
            popup.setResizable(false);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/fxml/styles/salao-mesas.css").toExternalForm());
            popup.setScene(scene);

            controller.setStage(popup);
            controller.configurar(mesa, conta -> {
                // Após abrir, vai direto pro lançamento e recarrega o salão
                popup.close();
                if (onAberta != null) onAberta.run();
                showLancamentoMesa(conta.getId(), onAberta);
            });

            popup.showAndWait();
        } catch (IOException e) {
            LOGGER.error("Falha ao abrir AberturaMesa", e);
            new Alert(Alert.AlertType.ERROR,
                    "Falha ao abrir abertura de mesa: " + e.getMessage()).showAndWait();
        }
    }

    /**
     * Abre a tela cheia de Lançamento de Itens da Mesa (overlay sobre o salão).
     *
     * @param idConta       conta a ser operada
     * @param onAposVoltar  callback executado ao voltar (recarregar salão)
     */
    public void showLancamentoMesa(Long idConta, Runnable onAposVoltar) {
        try {
            Scene cenaAtual = primaryStage.getScene();
            cenaQueAbriuLancamento = cenaAtual;
            cenaAnteriorLancamento = cenaAtual != null ? cenaAtual.getRoot() : null;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LancamentoMesa.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent rootLanc = loader.load();

            LancamentoMesaController controller = loader.getController();
            controller.carregar(idConta);

            String cssSalao = getClass().getResource("/fxml/styles/salao-mesas.css")
                    .toExternalForm();

            if (cenaAtual != null) {
                cenaAtual.setRoot(rootLanc);
                if (!cenaAtual.getStylesheets().contains(cssSalao)) {
                    cenaAtual.getStylesheets().add(cssSalao);
                }
            } else {
                Scene cena = new Scene(rootLanc);
                cena.getStylesheets().add(cssSalao);
                primaryStage.setScene(cena);
            }

            // Guarda o callback pra usar no fechar
            this.callbackAposLancamento = onAposVoltar;

        } catch (IOException e) {
            LOGGER.error("Falha ao abrir Lançamento de Mesa", e);
            new Alert(Alert.AlertType.ERROR,
                    "Falha ao abrir lançamento: " + e.getMessage()).showAndWait();
        }
    }

    /**
     * Fecha a tela de Lançamento e volta pra cena anterior (salão).
     */
    public void fecharLancamentoMesa() {
        if (cenaQueAbriuLancamento != null && cenaAnteriorLancamento != null) {
            cenaQueAbriuLancamento.setRoot(cenaAnteriorLancamento);
            cenaQueAbriuLancamento = null;
            cenaAnteriorLancamento = null;

            // Reexecuta callback (geralmente recarrega o salão)
            if (callbackAposLancamento != null) {
                Runnable cb = callbackAposLancamento;
                callbackAposLancamento = null;
                Platform.runLater(cb);
            }
        }
    }

    /**
     * Abre a tela de Comandas Abertas (overlay sobre o PDV).
     */
    public void showComandasAbertas() {
        try {
            Scene cenaAtual = primaryStage.getScene();
            cenaQueAbriuComandas = cenaAtual;
            cenaAnteriorComandas = cenaAtual != null ? cenaAtual.getRoot() : null;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ComandasAbertas.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent rootComandas = loader.load();

            ComandasAbertasController controller = loader.getController();
            controller.carregar();

            String cssSalao = getClass().getResource("/fxml/styles/salao-mesas.css")
                    .toExternalForm();

            if (cenaAtual != null) {
                cenaAtual.setRoot(rootComandas);
                if (!cenaAtual.getStylesheets().contains(cssSalao)) {
                    cenaAtual.getStylesheets().add(cssSalao);
                }
            } else {
                Scene cena = new Scene(rootComandas);
                cena.getStylesheets().add(cssSalao);
                primaryStage.setScene(cena);
            }
        } catch (IOException e) {
            LOGGER.error("Falha ao abrir Comandas Abertas", e);
            new Alert(Alert.AlertType.ERROR,
                    "Falha ao abrir Comandas: " + e.getMessage()).showAndWait();
        }
    }

    public void fecharComandasAbertas() {
        if (cenaQueAbriuComandas != null && cenaAnteriorComandas != null) {
            cenaQueAbriuComandas.setRoot(cenaAnteriorComandas);

            if (pdv4ControllerCache != null) {
                Platform.runLater(pdv4ControllerCache::reativarAtalhos);
            }

            cenaQueAbriuComandas = null;
            cenaAnteriorComandas = null;
        }
    }

    /**
     * Abre o dialog modal de abertura de comanda.
     *
     * @param codigo    código digitado pelo operador
     * @param onAberta  callback (recarrega tela de comandas)
     */
    public void showAberturaComanda(String codigo, Runnable onAberta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AberturaComanda.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            AberturaComandaController controller = loader.getController();

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Abrir Comanda " + codigo);
            popup.setResizable(false);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/fxml/styles/salao-mesas.css").toExternalForm());
            popup.setScene(scene);

            controller.setStage(popup);
            controller.configurar(codigo, conta -> {
                popup.close();
                if (onAberta != null) onAberta.run();
                showLancamentoComanda(conta.getId(), onAberta);
            });

            popup.showAndWait();
        } catch (IOException e) {
            LOGGER.error("Falha ao abrir AberturaComanda", e);
            new Alert(Alert.AlertType.ERROR,
                    "Falha ao abrir comanda: " + e.getMessage()).showAndWait();
        }
    }

    /**
     * Abre tela cheia de Lançamento de Itens da Comanda.
     */
    public void showLancamentoComanda(Long idConta, Runnable onAposVoltar) {
        try {
            Scene cenaAtual = primaryStage.getScene();
            cenaQueAbriuLancamentoComanda = cenaAtual;
            cenaAnteriorLancamentoComanda = cenaAtual != null ? cenaAtual.getRoot() : null;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LancamentoComanda.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent rootLanc = loader.load();

            LancamentoComandaController controller = loader.getController();
            controller.carregar(idConta);

            String cssSalao = getClass().getResource("/fxml/styles/salao-mesas.css")
                    .toExternalForm();

            if (cenaAtual != null) {
                cenaAtual.setRoot(rootLanc);
                if (!cenaAtual.getStylesheets().contains(cssSalao)) {
                    cenaAtual.getStylesheets().add(cssSalao);
                }
            } else {
                Scene cena = new Scene(rootLanc);
                cena.getStylesheets().add(cssSalao);
                primaryStage.setScene(cena);
            }

            this.callbackAposLancamentoComanda = onAposVoltar;

        } catch (IOException e) {
            LOGGER.error("Falha ao abrir Lançamento de Comanda", e);
            new Alert(Alert.AlertType.ERROR,
                    "Falha ao abrir lançamento: " + e.getMessage()).showAndWait();
        }
    }

    public void fecharLancamentoComanda() {
        if (cenaQueAbriuLancamentoComanda != null && cenaAnteriorLancamentoComanda != null) {
            cenaQueAbriuLancamentoComanda.setRoot(cenaAnteriorLancamentoComanda);
            cenaQueAbriuLancamentoComanda = null;
            cenaAnteriorLancamentoComanda = null;

            if (callbackAposLancamentoComanda != null) {
                Runnable cb = callbackAposLancamentoComanda;
                callbackAposLancamentoComanda = null;
                Platform.runLater(cb);
            }
        }
    }

    /**
     * Abre o modal seletor pra escolher Mesa ou Comanda.
     * Após escolher, abre a tela de busca correspondente.
     */
    public void showImportarContaSeletor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ImportarContaSeletor.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            ImportarContaSeletorController controller = loader.getController();

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Importar conta");
            popup.setResizable(false);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/fxml/styles/salao-mesas.css").toExternalForm());
            popup.setScene(scene);

            controller.setStage(popup);
            popup.showAndWait();

        } catch (IOException e) {
            LOGGER.error("Falha ao abrir ImportarContaSeletor", e);
            new Alert(Alert.AlertType.ERROR,
                    "Falha ao abrir seletor: " + e.getMessage()).showAndWait();
        }
    }

    /**
     * Abre a tela cheia de busca + prévia (overlay sobre o PDV).
     * Parametrizada por tipo: Mesa (M) ou Comanda (C).
     */
    public void showBuscarConta(TipoContaAberta tipo) {
        try {
            Scene cenaAtual = primaryStage.getScene();
            cenaQueAbriuBuscarConta = cenaAtual;
            cenaAnteriorBuscarConta = cenaAtual != null ? cenaAtual.getRoot() : null;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BuscarConta.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent rootBusca = loader.load();

            BuscarContaController controller = loader.getController();
            controller.configurar(tipo);

            String cssSalao = getClass().getResource("/fxml/styles/salao-mesas.css")
                    .toExternalForm();

            if (cenaAtual != null) {
                cenaAtual.setRoot(rootBusca);
                if (!cenaAtual.getStylesheets().contains(cssSalao)) {
                    cenaAtual.getStylesheets().add(cssSalao);
                }
            } else {
                Scene cena = new Scene(rootBusca);
                cena.getStylesheets().add(cssSalao);
                primaryStage.setScene(cena);
            }

        } catch (IOException e) {
            LOGGER.error("Falha ao abrir BuscarConta", e);
            new Alert(Alert.AlertType.ERROR,
                    "Falha ao abrir busca: " + e.getMessage()).showAndWait();
        }
    }

    /**
     * Abre a modal de devolução/troca pós-fechamento de um item de venda.
     *
     * @param item        item de venda já finalizada a devolver
     * @param onConcluido callback executado após devolução registrada
     *                    (ex: recarregar o detalhe do cupom na tela chamadora)
     */
    public void showDevolucao(PdvVendaDetalhe item, Runnable onConcluido) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Devolucao.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            DevolucaoController controller = loader.getController();

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Devolução de Item");
            popup.setResizable(false);

            Scene scene = new Scene(root, 460.0, 620);
            popup.setScene(scene);

            controller.setStage(popup);
            controller.configurar(item, onConcluido);

            popup.showAndWait();
        } catch (IOException e) {
            LOGGER.error("Falha ao abrir Devolução", e);
            new Alert(Alert.AlertType.ERROR,
                    "Falha ao abrir devolução: " + e.getMessage()).showAndWait();
        }
    }

    /**
     * Abre a tela de configuração de periféricos (balança/impressora/gaveta/TEF)
     * do caixa atual (fase 7). Restrita a Gerente — quem decide isso é o
     * {@code MenuPrincipalItem}/{@code MenuPrincipalController}.
     */
    public void showConfiguracaoPeriferico() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConfiguracaoPeriferico.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            ConfiguracaoPerifericoController controller = loader.getController();

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Configuração de Periféricos");
            popup.setResizable(true);

            Scene scene = new Scene(root, 820.0, 720);
            popup.setScene(scene);

            controller.setStage(popup);
            popup.showAndWait();
        } catch (IOException e) {
            LOGGER.error("Falha ao abrir Configuração de Periféricos", e);
            new Alert(Alert.AlertType.ERROR,
                    "Falha ao abrir configuração: " + e.getMessage()).showAndWait();
        }
    }

    /**
     * Fecha a tela de busca e volta pro PDV principal.
     * Se houve importação, o cupom já tem os itens — o PDV precisa
     * recarregar a UI a partir do banco.
     */
    public void fecharBuscarConta() {
        if (cenaQueAbriuBuscarConta != null && cenaAnteriorBuscarConta != null) {
            cenaQueAbriuBuscarConta.setRoot(cenaAnteriorBuscarConta);

            // Reativa atalhos do PDV E repopula a UI com os itens do cupom atual
            // (caso tenha sido importado da conta)
            if (pdv4ControllerCache != null) {
                Platform.runLater(() -> {
                    pdv4ControllerCache.reativarAtalhos();
                    // Recarrega itens do cupom (se houver) — a importação
                    // alterou o cupom no banco
                    pdv4ControllerCache.recarregarCupomAposImportacao();
                });
            }

            cenaQueAbriuBuscarConta = null;
            cenaAnteriorBuscarConta = null;
        }
    }
}