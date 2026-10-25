package br.com.cactuserp.pdv.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Comando e interpretação da resposta do cancelamento de NFC-e. Respostas
 * sintéticas no formato documentado pelo ACBr ({@code [CANCELAMENTO]} com
 * {@code CStat}/{@code XMotivo}/{@code NProt}) — mesma ressalva de
 * {@link EmissaoFiscalServiceTest}: não é uma captura de resposta real.
 */
class CancelamentoNfceRespostaTest {

    @Test
    @DisplayName("monta NFE.CancelarNFe com os três parâmetros entre aspas e sem aspas internas")
    void montaComando() {
        String comando = new AcbrComandoFiscalBuilder()
                .construirComandoCancelamentoNfce("35260812345678000199650010000001231234567890",
                        "Cliente desistiu, \"erro\" de digitação", "12345678000199");
        assertThat(comando).isEqualTo("NFE.CancelarNFe(\"35260812345678000199650010000001231234567890\","
                + "\"Cliente desistiu, erro de digitação\",\"12345678000199\")");
    }

    @Test
    @DisplayName("CStat 135 aceito, devolve o protocolo do evento")
    void cstat135Aceito() {
        String resposta = "OK: Cancelamento de NF-e homologado\r\n[CANCELAMENTO]\r\nCStat=135\r\n"
                + "XMotivo=Evento registrado e vinculado a NF-e\r\nNProt=135260000999888\r\n";
        assertThat(EmissaoFiscalService.interpretarRespostaCancelamento(resposta)).isEqualTo("135260000999888");
    }

    @Test
    @DisplayName("CStat 155 (homologado fora de prazo) também é aceito")
    void cstat155Aceito() {
        assertThat(EmissaoFiscalService.interpretarRespostaCancelamento("OK\r\nCStat=155\r\nNProt=1\r\n")).isEqualTo("1");
    }

    @Test
    @DisplayName("OK: com CStat de rejeição é tratado como falha, com o motivo da SEFAZ")
    void okComRejeicaoFalha() {
        String resposta = "OK: Rejeicao\r\nCStat=218\r\nXMotivo=Rejeicao: NF-e ja esta cancelada\r\n";
        assertThatThrownBy(() -> EmissaoFiscalService.interpretarRespostaCancelamento(resposta))
                .hasMessageContaining("CStat=218").hasMessageContaining("ja esta cancelada");
    }

    @Test
    @DisplayName("ERRO: do ACBr é falha")
    void erroDoAcbr() {
        assertThatThrownBy(() -> EmissaoFiscalService.interpretarRespostaCancelamento("ERRO: Justificativa invalida"))
                .hasMessageContaining("Justificativa invalida");
    }

    @Test
    @DisplayName("resposta sem CStat é falha (não presume sucesso)")
    void semCstatFalha() {
        assertThatThrownBy(() -> EmissaoFiscalService.interpretarRespostaCancelamento("OK: algo"))
                .hasMessageContaining("CStat=null");
    }
}
