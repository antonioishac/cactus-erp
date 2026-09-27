package br.com.cactuserp.pdv.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testa só a extração de {@code ChDFe}/{@code NProt} de uma resposta do
 * ACBr Monitor — lógica pura de regex ({@link EmissaoFiscalService#extrairChaveAcesso}/
 * {@link EmissaoFiscalService#extrairProtocoloAutorizacao}), sem subir
 * contexto Spring (o service tem dependências de repository/socket que não
 * fazem sentido testar aqui).
 *
 * <p><b>Ressalva importante (ver ROADMAP.md, fase 6):</b> os textos de
 * resposta usados abaixo são sintéticos, construídos a partir do formato
 * documentado pelo ACBr ({@code ChDFe=}/{@code NProt=}) — NÃO são uma
 * captura de uma resposta real de SUCESSO da SEFAZ, porque isso exige um
 * certificado ICP-Brasil pago (decisão financeira do usuário, ainda não
 * feita). Este teste garante que o código interpreta corretamente esse
 * formato assumido; não prova que é o formato real devolvido em produção.
 * Até 2026-08-23 só o caminho de ERRO foi validado contra uma instância
 * ACBr Monitor de verdade — o caminho de sucesso segue sem confirmação
 * empírica.</p>
 */
class EmissaoFiscalServiceTest {

    private static final String CHAVE_ACESSO_EXEMPLO = "35230812345678000199650010000001231234567890";
    private static final String PROTOCOLO_EXEMPLO = "135230000123456";

    @Test
    @DisplayName("extrai ChDFe de uma linha simples")
    void extraiChaveDeAcessoSimples() {
        String resposta = "OK\r\nChDFe=" + CHAVE_ACESSO_EXEMPLO + "\r\n";
        assertThat(EmissaoFiscalService.extrairChaveAcesso(resposta)).isEqualTo(CHAVE_ACESSO_EXEMPLO);
    }

    @Test
    @DisplayName("extrai NProt de uma linha simples")
    void extraiProtocoloSimples() {
        String resposta = "OK\r\nNProt=" + PROTOCOLO_EXEMPLO + "\r\n";
        assertThat(EmissaoFiscalService.extrairProtocoloAutorizacao(resposta)).isEqualTo(PROTOCOLO_EXEMPLO);
    }

    @Test
    @DisplayName("extrai ambos de uma resposta multi-linha completa")
    void extraiChaveEProtocoloJuntos() {
        String resposta = String.join("\r\n",
                "OK: NFe enviada com sucesso",
                "Arquivo=C:\\ACBrMonitor\\NFe\\" + CHAVE_ACESSO_EXEMPLO + "-nfe.xml",
                "ChDFe=" + CHAVE_ACESSO_EXEMPLO,
                "NProt=" + PROTOCOLO_EXEMPLO,
                "");
        assertThat(EmissaoFiscalService.extrairChaveAcesso(resposta)).isEqualTo(CHAVE_ACESSO_EXEMPLO);
        assertThat(EmissaoFiscalService.extrairProtocoloAutorizacao(resposta)).isEqualTo(PROTOCOLO_EXEMPLO);
    }

    @Test
    @DisplayName("é case-insensitive e tolera espaços ao redor do '='")
    void extraiComEspacosEcaseDiferente() {
        String resposta = "  chdfe  =  " + CHAVE_ACESSO_EXEMPLO + "  \r\nnprot= " + PROTOCOLO_EXEMPLO + " ";
        assertThat(EmissaoFiscalService.extrairChaveAcesso(resposta)).isEqualTo(CHAVE_ACESSO_EXEMPLO);
        assertThat(EmissaoFiscalService.extrairProtocoloAutorizacao(resposta)).isEqualTo(PROTOCOLO_EXEMPLO);
    }

    @Test
    @DisplayName("retorna null quando a resposta é de erro (sem ChDFe/NProt)")
    void retornaNuloEmRespostaDeErro() {
        String resposta = "ERRO: Rejeicao: 999 - Certificado invalido";
        assertThat(EmissaoFiscalService.extrairChaveAcesso(resposta)).isNull();
        assertThat(EmissaoFiscalService.extrairProtocoloAutorizacao(resposta)).isNull();
    }

    @Test
    @DisplayName("retorna null quando a resposta é null")
    void retornaNuloQuandoRespostaNula() {
        assertThat(EmissaoFiscalService.extrairChaveAcesso(null)).isNull();
        assertThat(EmissaoFiscalService.extrairProtocoloAutorizacao(null)).isNull();
    }

    @Test
    @DisplayName("trunca a chave de acesso em 44 caracteres se vier maior que o esperado")
    void truncaChaveDeAcessoMuitoLonga() {
        String chaveComLixo = CHAVE_ACESSO_EXEMPLO + "EXTRA";
        String resposta = "ChDFe=" + chaveComLixo;
        assertThat(EmissaoFiscalService.extrairChaveAcesso(resposta))
                .hasSize(44)
                .isEqualTo(chaveComLixo.substring(0, 44));
    }
}
