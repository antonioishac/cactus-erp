package br.com.cactuserp.pdv.controllers;

import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.entity.ModeloBalanca;
import br.com.cactuserp.pdv.entity.PdvConfiguracao;
import br.com.cactuserp.pdv.entity.PdvConfiguracaoBalanca;
import br.com.cactuserp.pdv.service.ConfiguracaoPerifericoService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller da tela {@code ConfiguracaoPeriferico.fxml} — cadastro de
 * balança, impressora térmica/gaveta e TEF do caixa atual (fase 7).
 *
 * <p>Acessível pelo Menu Principal (item {@code CONFIGURACOES}), restrito
 * a Gerente. Edita {@link PdvConfiguracao} (impressora/gaveta/TEF/marketing)
 * e {@link PdvConfiguracaoBalanca} numa transação só via
 * {@link ConfiguracaoPerifericoService}. A seção de marketing (fase 8)
 * grava {@code CAMINHO_IMAGENS_MARKETING}/{@code MARKETING_ATIVO},
 * consumidos por {@code MarketingService} na tela de bloqueio.
 */
@Component
public class ConfiguracaoPerifericoController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfiguracaoPerifericoController.class);

    /** Par valor-rótulo pra combos de campos codificados (ver PDV.md). */
    private record Opcao(Integer valor, String rotulo) {
        @Override public String toString() { return rotulo; }
    }

    private static final List<Opcao> OPCOES_STOP_BITS = List.of(
            new Opcao(0, "1"), new Opcao(1, "1,5"), new Opcao(2, "2"));
    private static final List<Opcao> OPCOES_PARITY = List.of(
            new Opcao(0, "Nenhuma"), new Opcao(1, "Ímpar"), new Opcao(2, "Par"),
            new Opcao(3, "Mark"), new Opcao(4, "Space"));
    private static final List<Opcao> OPCOES_HANDSHAKE = List.of(
            new Opcao(0, "Nenhum"), new Opcao(1, "XON/XOFF"),
            new Opcao(2, "RTS/CTS"), new Opcao(3, "DTR/DSR"));
    private static final List<Integer> BAUD_RATES_COMUNS =
            Arrays.asList(1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200);

    private final SessaoPdv sessaoPdv;
    private final ConfiguracaoPerifericoService service;

    @FXML private ComboBox<ModeloBalanca> cbModeloBalanca;
    @FXML private TextField tfPortaBalanca;
    @FXML private ComboBox<Integer> cbBaudRate;
    @FXML private TextField tfDataBits;
    @FXML private ComboBox<Opcao> cbStopBits;
    @FXML private ComboBox<Opcao> cbParity;
    @FXML private ComboBox<Opcao> cbHandShake;
    @FXML private TextField tfTimeout;
    @FXML private TextField tfTipoConfiguracao;

    @FXML private ComboBox<String> cbNomeImpressora;
    @FXML private ComboBox<Opcao> cbGavetaUtilizacao;
    @FXML private CheckBox chkGavetaSinalInvertido;

    @FXML private TextField tfTefHiperlinkUrl;

    @FXML private CheckBox chkMarketingAtivo;
    @FXML private TextField tfCaminhoImagensMarketing;

    private Stage stage;
    private PdvConfiguracao configuracao;
    private PdvConfiguracaoBalanca configuracaoBalanca;

    public ConfiguracaoPerifericoController(SessaoPdv sessaoPdv, ConfiguracaoPerifericoService service) {
        this.sessaoPdv = sessaoPdv;
        this.service = service;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbModeloBalanca.setItems(FXCollections.observableArrayList(ModeloBalanca.values()));
        cbBaudRate.setItems(FXCollections.observableArrayList(BAUD_RATES_COMUNS));
        cbStopBits.setItems(FXCollections.observableArrayList(OPCOES_STOP_BITS));
        cbParity.setItems(FXCollections.observableArrayList(OPCOES_PARITY));
        cbHandShake.setItems(FXCollections.observableArrayList(OPCOES_HANDSHAKE));

        List<Opcao> opcoesGaveta = List.of(
                new Opcao(0, "Sem gaveta"),
                new Opcao(1, "Com gaveta (ENTER/ESC)"),
                new Opcao(2, "Com gaveta (Exclusivo)"));
        cbGavetaUtilizacao.setItems(FXCollections.observableArrayList(opcoesGaveta));

        carregarImpressorasDoSistema();
        Platform.runLater(this::carregarDados);
    }

    /** Lista as impressoras cadastradas no SO — evita erro de digitação no nome. */
    private void carregarImpressorasDoSistema() {
        try {
            List<String> nomes = Arrays.stream(PrintServiceLookup.lookupPrintServices(null, null))
                    .map(PrintService::getName)
                    .toList();
            cbNomeImpressora.setItems(FXCollections.observableArrayList(nomes));
            cbNomeImpressora.setEditable(true); // permite digitar um nome fora da lista (ex: impressora de rede)
        } catch (Exception ex) {
            LOGGER.warn("Falha ao listar impressoras do sistema operacional", ex);
        }
    }

    private void carregarDados() {
        if (!sessaoPdv.temMovimentoAtivo() && sessaoPdv.getCaixaAtual() == null) {
            mostrarErro("Nenhum caixa identificado na sessão. Não é possível configurar periféricos.");
            fechar();
            return;
        }

        configuracao = service.buscarOuNovaConfiguracao(sessaoPdv.getCaixaAtual());
        configuracaoBalanca = service.buscarOuNovaConfiguracaoBalanca(configuracao);

        // Balança
        cbModeloBalanca.setValue(configuracaoBalanca.getModelo() != null
                ? configuracaoBalanca.getModelo() : ModeloBalanca.NENHUM);
        tfPortaBalanca.setText(configuracaoBalanca.getPorta());
        cbBaudRate.setValue(configuracaoBalanca.getBaudRate());
        tfDataBits.setText(str(configuracaoBalanca.getDataBits()));
        cbStopBits.setValue(porValor(OPCOES_STOP_BITS, configuracaoBalanca.getStopBits()));
        cbParity.setValue(porValor(OPCOES_PARITY, configuracaoBalanca.getParity()));
        cbHandShake.setValue(porValor(OPCOES_HANDSHAKE, configuracaoBalanca.getHandShake()));
        tfTimeout.setText(str(configuracaoBalanca.getTimeout()));
        tfTipoConfiguracao.setText(configuracaoBalanca.getTipoConfiguracao());

        // Impressora / Gaveta
        cbNomeImpressora.setValue(configuracao.getNomeImpressoraCupom());
        cbGavetaUtilizacao.setValue(porValor(cbGavetaUtilizacao.getItems(), configuracao.getGavetaUtilizacao()));
        chkGavetaSinalInvertido.setSelected("S".equalsIgnoreCase(configuracao.getGavetaSinalInvertido()));

        // TEF
        tfTefHiperlinkUrl.setText(configuracao.getTefHiperlinkUrl());

        // Marketing
        chkMarketingAtivo.setSelected(configuracao.marketingEstaAtivo());
        tfCaminhoImagensMarketing.setText(configuracao.getCaminhoImagensMarketing());
    }

    @FXML
    private void handleSalvar() {
        try {
            configuracaoBalanca.setModelo(cbModeloBalanca.getValue());
            configuracaoBalanca.setPorta(vazioParaNull(tfPortaBalanca.getText()));
            configuracaoBalanca.setBaudRate(cbBaudRate.getValue());
            configuracaoBalanca.setDataBits(parseInteiro(tfDataBits.getText()));
            configuracaoBalanca.setStopBits(valorDe(cbStopBits.getValue()));
            configuracaoBalanca.setParity(valorDe(cbParity.getValue()));
            configuracaoBalanca.setHandShake(valorDe(cbHandShake.getValue()));
            configuracaoBalanca.setTimeout(parseInteiro(tfTimeout.getText()));
            configuracaoBalanca.setTipoConfiguracao(vazioParaNull(tfTipoConfiguracao.getText()));

            configuracao.setNomeImpressoraCupom(vazioParaNull(cbNomeImpressora.getValue()));
            configuracao.setGavetaUtilizacao(valorDe(cbGavetaUtilizacao.getValue()));
            configuracao.setGavetaSinalInvertido(chkGavetaSinalInvertido.isSelected() ? "S" : "N");

            configuracao.setTefHiperlinkUrl(vazioParaNull(tfTefHiperlinkUrl.getText()));

            configuracao.setMarketingAtivo(chkMarketingAtivo.isSelected() ? "S" : "N");
            configuracao.setCaminhoImagensMarketing(vazioParaNull(tfCaminhoImagensMarketing.getText()));

            service.salvar(configuracao, configuracaoBalanca);
        } catch (NumberFormatException ex) {
            mostrarErro("Verifique os campos numéricos (data bits / timeout) — valor inválido.");
            return;
        } catch (Exception ex) {
            LOGGER.error("Falha ao salvar configuração de periféricos", ex);
            mostrarErro("Não foi possível salvar.\n\nMotivo: " + ex.getMessage());
            return;
        }

        Alert ok = new Alert(Alert.AlertType.INFORMATION, "Configuração salva com sucesso.");
        ok.setHeaderText("Concluído");
        ok.showAndWait();
        fechar();
    }

    @FXML
    private void handleFechar() {
        fechar();
    }

    // ====== Helpers =======================================================

    private Opcao porValor(List<Opcao> opcoes, Integer valor) {
        if (valor == null) return null;
        return opcoes.stream().filter(o -> o.valor().equals(valor)).findFirst().orElse(null);
    }

    private Integer valorDe(Opcao opcao) {
        return opcao != null ? opcao.valor() : null;
    }

    private Integer parseInteiro(String texto) {
        if (texto == null || texto.isBlank()) return null;
        return Integer.valueOf(texto.trim());
    }

    private String str(Integer v) {
        return v != null ? String.valueOf(v) : "";
    }

    private String vazioParaNull(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }

    private void mostrarErro(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.setHeaderText("Atenção");
        alert.showAndWait();
    }

    private void fechar() {
        if (stage != null) stage.close();
    }
}
