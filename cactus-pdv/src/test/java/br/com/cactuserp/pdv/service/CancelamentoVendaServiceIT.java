package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.PdvApplication;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.StatusVenda;
import br.com.cactuserp.pdv.repository.PdvOperadorRepository;
import br.com.cactuserp.pdv.repository.PdvVendaCabecalhoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integração contra o MySQL LOCAL do PDV ({@code cactus_pdv}), sempre em
 * transação revertida ao fim de cada teste — nada fica no banco. Não roda no
 * {@code mvn test} padrão (sufixo IT, fora do padrão do Surefire); rodar com
 * {@code -Dtest=CancelamentoVendaServiceIT}, banco no ar e {@code DB_PASSWORD}
 * definido. O cenário de NFC-e precisa de um ACBr Monitor FALSO em
 * 127.0.0.1:3535 (responde CStat=135, ou 218 quando a justificativa contém
 * "REJEITAR") — nunca o ACBr real.
 *
 * <p>Usa vendas já existentes no banco de desenvolvimento: #43 (recibo, 10
 * itens) e #41 (3 itens), ambas F com o turno aberto.
 */
@SpringBootTest(classes = PdvApplication.class)
@Transactional
class CancelamentoVendaServiceIT {

    private static final long VENDA_RECIBO = 43L;
    private static final long VENDA_NFCE = 41L;
    private static final String JUSTIFICATIVA = "Cliente desistiu da compra no caixa";

    @Autowired private CancelamentoVendaService service;
    @Autowired private PdvVendaCabecalhoRepository cabecalhoRepository;
    @Autowired private PdvOperadorRepository operadorRepository;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private EntityManager em;

    private PdvOperador operador;
    private PdvOperador gerente;

    @BeforeEach
    void preparar() {
        operador = operadorRepository.findById(2L).orElseThrow();
        gerente = operadorRepository.findById(1L).orElseThrow();
    }

    @Test
    void cancelaReciboDevolvendoEstoqueEGravandoAuditoria() {
        int itensAtivos = jdbc.queryForObject("SELECT COUNT(*) FROM PDV_VENDA_DETALHE WHERE ID_PDV_VENDA_CABECALHO=? AND CANCELADO='N'",
                Integer.class, VENDA_RECIBO);
        int reentradasAntes = reentradas(VENDA_RECIBO);

        PdvVendaCabecalho cancelada = service.cancelar(venda(VENDA_RECIBO), JUSTIFICATIVA, operador, gerente);

        assertThat(cancelada.getStatusVenda()).isEqualTo(StatusVenda.C);
        assertThat(cancelada.getValorCancelado()).isEqualByComparingTo(cancelada.getValorFinal());
        assertThat(cancelada.getJustificativaCancelamento()).isEqualTo(JUSTIFICATIVA);
        assertThat(cancelada.getGerenteCancelamento().getId()).isEqualTo(1L);
        assertThat(cancelada.getProtocoloCancelamento()).isNull();
        em.flush();
        assertThat(reentradas(VENDA_RECIBO) - reentradasAntes).isEqualTo(itensAtivos);
        String colunas = jdbc.queryForObject("SELECT CONCAT(STATUS_VENDA,'|',DATA_CANCELAMENTO,'|',ID_GERENTE_CANCELAMENTO) "
                + "FROM PDV_VENDA_CABECALHO WHERE ID=?", String.class, VENDA_RECIBO);
        assertThat(colunas).isEqualTo("C|" + LocalDate.now() + "|1");

        assertThatThrownBy(() -> service.cancelar(venda(VENDA_RECIBO), JUSTIFICATIVA, operador, gerente))
                .hasMessageContaining("Só vendas finalizadas");
    }

    @Test
    void reentradaDescontaOQueJaFoiDevolvido() {
        // Item 114 (16 un.) com 5 já devolvidas: só 11 voltam no cancelamento.
        jdbc.update("INSERT INTO PDV_DEVOLUCAO (ID_PDV_VENDA_CABECALHO, ID_PDV_VENDA_DETALHE, QUANTIDADE_DEVOLVIDA, VALOR_DEVOLVIDO, "
                + "ID_OPERADOR, ID_GERENTE_AUTORIZADOR, DATA_DEVOLUCAO, HORA_DEVOLUCAO) VALUES (?,114,5,1,2,1,CURDATE(),'10:00:00')", VENDA_NFCE);
        service.cancelar(venda(VENDA_NFCE), JUSTIFICATIVA, operador, gerente);
        em.flush();
        BigDecimal reentrou = jdbc.queryForObject("SELECT QUANTIDADE FROM ESTOQUE_MOVIMENTO WHERE ID_PDV_VENDA_DETALHE=114 "
                + "AND TIPO_MOVIMENTO='D' ORDER BY ID DESC LIMIT 1", BigDecimal.class);
        assertThat(reentrou).isEqualByComparingTo("11");
    }

    @Test
    void exigeGerenteEJustificativaValida() {
        assertThatThrownBy(() -> service.cancelar(venda(VENDA_RECIBO), JUSTIFICATIVA, operador, null))
                .hasMessageContaining("autorização gerencial");
        assertThatThrownBy(() -> service.cancelar(venda(VENDA_RECIBO), "curta", operador, gerente))
                .hasMessageContaining("entre 15 e 255");
    }

    @Test
    void bloqueiaComTurnoFechado() {
        jdbc.update("UPDATE PDV_MOVIMENTO SET STATUS_MOVIMENTO='F' WHERE ID=(SELECT ID_PDV_MOVIMENTO FROM PDV_VENDA_CABECALHO WHERE ID=?)", VENDA_RECIBO);
        em.clear();
        assertThatThrownBy(() -> service.cancelar(venda(VENDA_RECIBO), JUSTIFICATIVA, operador, gerente))
                .hasMessageContaining("turno desta venda já foi fechado");
    }

    @Test
    void emissaoPendenteViraStatusFiscalC() {
        jdbc.update("UPDATE PDV_VENDA_CABECALHO SET STATUS_FISCAL='P' WHERE ID=?", VENDA_RECIBO);
        em.clear();
        assertThat(service.cancelar(venda(VENDA_RECIBO), JUSTIFICATIVA, operador, gerente).getStatusFiscal()).isEqualTo("C");
    }

    @Test
    void nfceAutorizadaCanceladaNaSefazGravaProtocolo() {
        prepararNfce(LocalTime.now().minusMinutes(5));
        PdvVendaCabecalho cancelada = service.cancelar(venda(VENDA_NFCE), JUSTIFICATIVA, operador, gerente);
        assertThat(cancelada.getStatusVenda()).isEqualTo(StatusVenda.C);
        assertThat(cancelada.getProtocoloCancelamento()).isEqualTo("135260000999888");
    }

    @Test
    void sefazRejeitandoNadaMudaNoPdv() {
        prepararNfce(LocalTime.now().minusMinutes(5));
        assertThatThrownBy(() -> service.cancelar(venda(VENDA_NFCE), "REJEITAR este cancelamento de teste", operador, gerente))
                .hasMessageContaining("CStat=218");
        String status = jdbc.queryForObject("SELECT STATUS_VENDA FROM PDV_VENDA_CABECALHO WHERE ID=?", String.class, VENDA_NFCE);
        assertThat(status).isEqualTo("F");
        assertThat(reentradas(VENDA_NFCE)).isZero();
    }

    @Test
    void nfceSemCnpjDoEmitenteEhBloqueada() {
        prepararNfce(LocalTime.now().minusMinutes(5));
        jdbc.update("UPDATE LOJA_CONFIGURACAO SET CNPJ=NULL");
        assertThatThrownBy(() -> service.cancelar(venda(VENDA_NFCE), JUSTIFICATIVA, operador, gerente))
                .hasMessageContaining("CNPJ do emitente");
    }

    @Test
    void nfceForaDoPrazoEhBloqueada() {
        prepararNfce(LocalTime.now().minusMinutes(31));
        assertThatThrownBy(() -> service.cancelar(venda(VENDA_NFCE), JUSTIFICATIVA, operador, gerente))
                .hasMessageContaining("Prazo de 30 minutos");
    }

    private void prepararNfce(LocalTime horaVenda) {
        jdbc.update("UPDATE PDV_VENDA_CABECALHO SET STATUS_FISCAL='E', TIPO_OPERACAO='NFC', "
                        + "CHAVE_ACESSO_FISCAL='35260812345678000199650010000001231234567890', DATA_VENDA=?, HORA_VENDA=? WHERE ID=?",
                LocalDate.now(), horaVenda.format(DateTimeFormatter.ofPattern("HH:mm:ss")), VENDA_NFCE);
        jdbc.update("INSERT INTO PDV_CONFIGURACAO (ID_PDV_CAIXA, ACBR_MONITOR_ENDERECO, ACBR_MONITOR_PORTA, MODULO_FISCAL_PRINCIPAL) "
                + "VALUES (1, '127.0.0.1', 3535, 'NFC-E')");
        jdbc.update("UPDATE LOJA_CONFIGURACAO SET CNPJ='12345678000199'");
        em.clear();
    }

    private int reentradas(long vendaId) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM ESTOQUE_MOVIMENTO e JOIN PDV_VENDA_DETALHE d ON d.ID=e.ID_PDV_VENDA_DETALHE "
                + "WHERE d.ID_PDV_VENDA_CABECALHO=? AND e.TIPO_MOVIMENTO='D'", Integer.class, vendaId);
    }

    private PdvVendaCabecalho venda(long id) {
        return cabecalhoRepository.findById(id).orElseThrow();
    }
}
