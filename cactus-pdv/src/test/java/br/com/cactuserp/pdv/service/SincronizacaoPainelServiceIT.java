package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.PdvApplication;
import br.com.cactuserp.pdv.controllers.SincronizacaoErpController;
import br.com.cactuserp.pdv.service.dto.ItemFilaEnvio;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Painel de sincronização contra o MySQL local do PDV, em transação revertida. Fora do
 * {@code mvn test} padrão (sufixo IT). Sincronização desligada (padrão), então
 * {@code motivoIndisponivel} explica o porquê.
 *
 * <p>O último teste sobe o toolkit JavaFX e carrega o FXML de verdade com o controller
 * do Spring — pega {@code fx:id} com tipo errado, {@code onAction} inexistente e ícone
 * inválido, que só quebrariam ao abrir a tela.
 */
@SpringBootTest(classes = PdvApplication.class)
@Transactional
class SincronizacaoPainelServiceIT {

    @Autowired private SincronizacaoPainelService painel;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private ApplicationContext contexto;

    @BeforeEach
    void fila() {
        jdbc.update("DELETE FROM SINCRONIZACAO_ENVIO");
        inserir("VENDA_PDV", 1, "B", "Produto #9 do PDV ainda não está vinculado ao ERP (ID_ERP).");
        inserir("SANGRIA", 2, "R", "Campo(s) obrigatório(s) ausente(s) no payload: valor");
        inserir("VENDA_PDV", 3, "E", null);
        inserir("ESTOQUE_MOVIMENTO", 4, "P", null);
    }

    @Test
    void resumoEFiltroDaFila() {
        assertThat(painel.resumoFila()).containsEntry("B", 1).containsEntry("R", 1).containsEntry("E", 1)
                .containsEntry("P", 1).containsEntry("D", 0);
        List<ItemFilaEnvio> problemas = painel.listarFila(SincronizacaoPainelService.STATUS_COM_PROBLEMA);
        assertThat(problemas).extracting(ItemFilaEnvio::statusDescricao).containsExactlyInAnyOrder("Bloqueado", "Rejeitado");
        assertThat(problemas).allSatisfy(i -> assertThat(i.ultimoErro()).isNotBlank());
        assertThat(painel.listarFila(Set.of())).hasSize(4);
    }

    @Test
    void reenviarSoReabreBloqueadoERejeitado() {
        long enviado = id("VENDA_PDV", 3);
        long bloqueado = id("VENDA_PDV", 1);
        assertThat(painel.reenviar(enviado)).as("enviado não volta para a fila").isFalse();
        assertThat(painel.reenviar(bloqueado)).isTrue();
        assertThat(jdbc.queryForObject("SELECT CONCAT(STATUS, '|', TENTATIVAS, '|', COALESCE(ULTIMO_ERRO, '-')) FROM SINCRONIZACAO_ENVIO "
                + "WHERE ID = ?", String.class, bloqueado)).isEqualTo("P|0|-");

        assertThat(painel.reenviarTodosComProblema()).isEqualTo(1); // sobrou só o rejeitado
        assertThat(painel.resumoFila()).containsEntry("B", 0).containsEntry("R", 0).containsEntry("P", 3).containsEntry("E", 1);
    }

    @Test
    void desligadaExplicaPorQueNaoSincroniza() {
        assertThat(painel.motivoIndisponivel()).hasValueSatisfying(m -> assertThat(m).contains("desligada"));
    }

    @Test
    void telaCarregaComOControllerDoSpring() throws Exception {
        CompletableFuture<Void> toolkit = new CompletableFuture<>();
        try {
            Platform.startup(() -> toolkit.complete(null));
        } catch (IllegalStateException jaIniciado) {
            toolkit.complete(null);
        }
        toolkit.get(10, TimeUnit.SECONDS);

        CompletableFuture<String> resultado = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SincronizacaoErp.fxml"));
                loader.setControllerFactory(contexto::getBean);
                Parent raiz = loader.load();
                new Scene(raiz);
                SincronizacaoErpController controller = loader.getController();
                controller.prepararParaAbertura();
                TableView<?> fila = (TableView<?>) raiz.lookup("#tabelaFila");
                Label disponibilidade = (Label) raiz.lookup("#lblDisponibilidade");
                resultado.complete(fila.getItems().size() + "|" + disponibilidade.getText());
            } catch (Exception ex) {
                resultado.completeExceptionally(ex);
            }
        });
        // A thread do JavaFX usa outra conexão e não enxerga a fila desta transação (revertida): aqui só se valida
        // que a tela carregou e preencheu sem erro. O conteúdo da fila já foi validado nos testes acima.
        String carregado = resultado.get(20, TimeUnit.SECONDS);
        assertThat(carregado).contains("desligada");
    }

    private void inserir(String tipo, long idLocal, String status, String erro) {
        jdbc.update("INSERT INTO SINCRONIZACAO_ENVIO (TIPO_REGISTRO, ID_LOCAL, ID_ORIGEM, STATUS, TENTATIVAS, ULTIMO_ERRO, DATA_CRIACAO) "
                + "VALUES (?, ?, ?, ?, 1, ?, NOW())", tipo, idLocal, "IT:" + tipo + ":" + idLocal, status, erro);
    }

    private long id(String tipo, long idLocal) {
        return jdbc.queryForObject("SELECT ID FROM SINCRONIZACAO_ENVIO WHERE TIPO_REGISTRO = ? AND ID_LOCAL = ?", Long.class, tipo, idLocal);
    }
}
