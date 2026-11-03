package br.com.cactuserp.pdv.service.fiscal;

import br.com.cactuserp.pdv.entity.LojaConfiguracao;
import br.com.cactuserp.pdv.entity.PdvTotalTipoPagamento;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;
import br.com.cactuserp.pdv.entity.Produto;
import br.com.cactuserp.pdv.entity.TipoPagamento;
import com.fincatto.documentofiscal.DFAmbiente;
import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.nfe.NFTipoEmissao;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNota;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * NFC-e (modelo 65) gerada pelo PDV, assinada e conferida contra o <b>XSD oficial</b> ({@link AssinadorNfce} valida o
 * lote), incluindo o QR Code v3 — normal e de contingência offline. Certificado autoassinado gerado pelo {@code keytool}
 * do JDK: o XSD exige a assinatura, a cadeia ICP-Brasil é a SEFAZ que valida.
 */
class NfceDocumentoBuilderTest {

    private static CertificadosNfceProvider.CertificadoA1 certificado;

    private final NfceDocumentoBuilder builder = new NfceDocumentoBuilder();
    private final AssinadorNfce assinador = new AssinadorNfce();

    @BeforeAll
    static void gerarCertificado(@TempDir Path pasta) throws Exception {
        Path arquivo = pasta.resolve("loja.p12");
        String keytool = Path.of(System.getProperty("java.home"), "bin", "keytool").toString();
        Process processo = new ProcessBuilder(keytool, "-genkeypair", "-alias", "loja", "-keyalg", "RSA", "-keysize", "2048",
                "-dname", "CN=LOJA TESTE:11222333000181", "-validity", "2", "-storetype", "PKCS12",
                "-keystore", arquivo.toString(), "-storepass", "senha123", "-keypass", "senha123").redirectErrorStream(true).start();
        assertThat(processo.waitFor()).as(new String(processo.getInputStream().readAllBytes())).isZero();
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream in = Files.newInputStream(arquivo)) {
            keyStore.load(in, "senha123".toCharArray());
        }
        certificado = new CertificadosNfceProvider.CertificadoA1(keyStore, "senha123", "loja", (X509Certificate) keyStore.getCertificate("loja"));
    }

    @Test
    void regimeNormalOnlineComDescontoAcrescimoTrocoECartaoPassaNoXsd() {
        PdvVendaCabecalho cupom = cupom(41L, "12345678909");
        List<PdvVendaDetalhe> itens = List.of(
                item(1L, "2", "10.00", "1.00", "0", "00", "18.00", "1.65", "7.60"),   // bruto 20, líquido 19
                item(2L, "1.250", "8.00", "0", "0.50", "60", null, null, null));      // bruto 10, líquido 10.50
        // vNF = 30 - 1 + 0.50 = 29.50; pagos 20 cartão + 10 dinheiro = 30 → troco 0.50
        List<PdvTotalTipoPagamento> pagamentos = List.of(pagamento("03", "Cartão de crédito", "20.00"), pagamento("01", "Dinheiro", "10.00"));

        AssinadorNfce.DocumentoNfceAssinado doc = assinar(dados(cupom, loja("3"), itens, pagamentos, NFTipoEmissao.EMISSAO_NORMAL));

        assertThat(doc.chaveAcesso()).hasSize(44).startsWith("35").contains("65" + "005" + "000000123");
        assertThat(doc.qrCode()).endsWith("?p=" + doc.chaveAcesso() + "|3|2").contains("homologacao.nfce.fazenda.sp.gov.br");
        assertThat(doc.xmlLote())
                .contains("<mod>65</mod>", "<tpEmis>1</tpEmis>", "<tpImp>4</tpImp>", "<indPres>1</indPres>", "<indFinal>1</indFinal>")
                .contains("<xProd>" + NfceDocumentoBuilder.DESCRICAO_HOMOLOGACAO + "</xProd>")
                .contains("<xNome>" + NfceDocumentoBuilder.NOME_HOMOLOGACAO + "</xNome>", "<CPF>12345678909</CPF>")
                .contains("<CFOP>5102</CFOP>", "<CFOP>5405</CFOP>", "<ICMS60><orig>0</orig><CST>60</CST></ICMS60>")
                .contains("<vDesc>1.00</vDesc>", "<vOutro>0.50</vOutro>", "<vNF>29.50</vNF>")
                .contains("<card><tpIntegra>2</tpIntegra></card>", "<vTroco>0.50</vTroco>")
                .contains("<infNFeSupl><qrCode>", "<urlChave>")
                .doesNotContain("<dhCont>");
    }

    @Test
    void contingenciaOfflineLevaDhContXJustEQrCodeAssinadoEPassaNoXsd() {
        PdvVendaCabecalho cupom = cupom(42L, null);
        List<PdvVendaDetalhe> itens = List.of(item(1L, "3", "9.99", "0", "0", "40", null, null, null));
        LocalDateTime entrada = LocalDateTime.of(2026, 11, 1, 10, 0, 0);
        NfceDocumentoBuilder.DadosNfce dados = new NfceDocumentoBuilder.DadosNfce(cupom, loja("3"), itens,
                List.of(pagamento("01", "Dinheiro", "50.00")), Map.of(), 5, 124L, LocalDateTime.of(2026, 11, 1, 10, 5, 0), false,
                NFTipoEmissao.CONTIGENCIA_OFFLINE, entrada, "Contingencia offline: SEFAZ sem resposta (timeout)", null);

        AssinadorNfce.DocumentoNfceAssinado doc = assinar(dados);

        String[] parametros = doc.qrCode().substring(doc.qrCode().indexOf("?p=") + 3).split("\\|", -1);
        assertThat(parametros).hasSize(8);
        assertThat(parametros[1]).isEqualTo("3");
        assertThat(parametros[3]).isEqualTo("01");          // dia da emissão
        assertThat(parametros[4]).isEqualTo("29.97");       // vNF
        assertThat(parametros[5]).isEmpty();                 // consumidor não identificado
        assertThat(parametros[7]).isNotBlank();              // assinatura com o certificado
        assertThat(doc.chaveAcesso().charAt(34)).as("tpEmis na chave").isEqualTo('9');
        assertThat(doc.xmlLote()).contains("<tpEmis>9</tpEmis>", "<dhCont>2026-11-01T10:00:00-03:00</dhCont>",
                "<xJust>Contingencia offline: SEFAZ sem resposta (timeout)</xJust>", "<vTroco>20.03</vTroco>").doesNotContain("<dest>");
    }

    @Test
    void simplesNacionalComCsosnPassaNoXsd() {
        PdvVendaDetalhe item = item(1L, "1", "5.00", "0", "0", null, null, null, null);
        item.getProduto().setCsosn("102");

        AssinadorNfce.DocumentoNfceAssinado doc = assinar(dados(cupom(43L, null), loja("1"), List.of(item),
                List.of(pagamento("17", "PIX", "5.00")), NFTipoEmissao.EMISSAO_NORMAL));

        assertThat(doc.xmlLote()).contains("<CRT>1</CRT>", "<ICMSSN102><orig>0</orig><CSOSN>102</CSOSN></ICMSSN102>",
                "<PISOutr><CST>49</CST>", "<tPag>17</tPag>").doesNotContain("<vTroco>");
    }

    @Test
    void cstDeIcmsSemSuporteERecusadoComOMotivo() {
        PdvVendaDetalhe item = item(1L, "1", "5.00", "0", "0", "10", null, null, null);

        assertThatThrownBy(() -> builder.construir(dados(cupom(44L, null), loja("3"), List.of(item),
                List.of(pagamento("01", "Dinheiro", "5.00")), NFTipoEmissao.EMISSAO_NORMAL)))
                .isInstanceOf(NfceConfiguracaoException.class).hasMessageContaining("CST de ICMS 10");
    }

    @Test
    void pagamentoMenorQueOTotalERecusado() {
        PdvVendaDetalhe item = item(1L, "1", "5.00", "0", "0", "40", null, null, null);

        assertThatThrownBy(() -> builder.construir(dados(cupom(45L, null), loja("3"), List.of(item),
                List.of(pagamento("01", "Dinheiro", "4.99")), NFTipoEmissao.EMISSAO_NORMAL)))
                .isInstanceOf(NfceConfiguracaoException.class).hasMessageContaining("menores que o total");
    }

    @Test
    void lojaSemDadosDoEmitenteERecusadaListandoOQueFalta() {
        LojaConfiguracao loja = loja("3");
        loja.setInscricaoEstadual(null);
        loja.setCodigoIbgeCidade(null);

        assertThatThrownBy(() -> builder.construir(dados(cupom(46L, null), loja, List.of(item(1L, "1", "5.00", "0", "0", "40", null, null, null)),
                List.of(pagamento("01", "Dinheiro", "5.00")), NFTipoEmissao.EMISSAO_NORMAL)))
                .hasMessageContaining("inscrição estadual").hasMessageContaining("código IBGE da cidade");
    }

    @Test
    void codigoNumericoEDeterministicoEDifereEntreNormalEOffline() {
        String normal = NfceDocumentoBuilder.codigoNumerico(41L, 123L, NFTipoEmissao.EMISSAO_NORMAL);
        assertThat(normal).hasSize(8).isEqualTo(NfceDocumentoBuilder.codigoNumerico(41L, 123L, NFTipoEmissao.EMISSAO_NORMAL));
        assertThat(NfceDocumentoBuilder.codigoNumerico(41L, 123L, NFTipoEmissao.CONTIGENCIA_OFFLINE)).isNotEqualTo(normal);
    }

    // ================================================================ montagem

    private AssinadorNfce.DocumentoNfceAssinado assinar(NfceDocumentoBuilder.DadosNfce dados) {
        NFNota nota = builder.construir(dados);
        return assinador.assinar(nota, new PdvNfceConfig(DFUnidadeFederativa.SP, DFAmbiente.HOMOLOGACAO, dados.tipoEmissao(),
                certificado, null, 8_000), dados.cupom().getId());
    }

    private static NfceDocumentoBuilder.DadosNfce dados(PdvVendaCabecalho cupom, LojaConfiguracao loja, List<PdvVendaDetalhe> itens,
                                                        List<PdvTotalTipoPagamento> pagamentos, NFTipoEmissao tipo) {
        return new NfceDocumentoBuilder.DadosNfce(cupom, loja, itens, pagamentos, Map.of(10L, "KG"), 5, 123L,
                LocalDateTime.of(2026, 11, 1, 10, 5, 0), false, tipo, null, null,
                new FiscalPdvProperties.ResponsavelTecnico("11222333000181", "Suporte Cactus", "fiscal@cactus.com.br", "11999990000"));
    }

    private static PdvVendaCabecalho cupom(Long id, String cpf) {
        PdvVendaCabecalho cupom = new PdvVendaCabecalho();
        cupom.setId(id);
        cupom.setCpfCnpjCliente(cpf);
        cupom.setNomeCliente(cpf != null ? "Consumidor" : null);
        return cupom;
    }

    private static LojaConfiguracao loja(String crt) {
        LojaConfiguracao loja = new LojaConfiguracao();
        loja.setCnpj("11222333000181");
        loja.setInscricaoEstadual("111222333444");
        loja.setRazaoSocial("Cactus Mercado Ltda");
        loja.setNomeFantasia("Cactus Mercado");
        loja.setCrt(crt);
        loja.setLogradouro("Rua das Flores");
        loja.setNumero("100");
        loja.setBairro("Centro");
        loja.setCep("01001000");
        loja.setCidade("Sao Paulo");
        loja.setCodigoIbgeCidade(3550308L);
        loja.setUf("SP");
        loja.setTelefone("(11) 3333-4444");
        return loja;
    }

    private static PdvVendaDetalhe item(Long produtoId, String quantidade, String unitario, String desconto, String acrescimo, String cst,
                                        String taxaIcms, String taxaPis, String taxaCofins) {
        Produto produto = new Produto();
        produto.setId(produtoId);
        produto.setDescricao("Produto " + produtoId);
        produto.setCodigoNcm("22021000");
        produto.setProdutoUnidade(10L);
        BigDecimal bruto = new BigDecimal(quantidade).multiply(new BigDecimal(unitario));
        BigDecimal liquido = bruto.subtract(new BigDecimal(desconto)).add(new BigDecimal(acrescimo));
        PdvVendaDetalhe detalhe = new PdvVendaDetalhe();
        detalhe.setProduto(produto);
        detalhe.setGtin("7891234567895");
        detalhe.setQuantidade(new BigDecimal(quantidade));
        detalhe.setValorUnitario(new BigDecimal(unitario));
        detalhe.setValorDesconto(new BigDecimal(desconto));
        detalhe.setValorAcrescimo(new BigDecimal(acrescimo));
        detalhe.setValorTotal(liquido);
        detalhe.setCst(cst);
        if (taxaIcms != null) {
            detalhe.setValorBaseIcms(liquido);
            detalhe.setTaxaIcms(new BigDecimal(taxaIcms));
            detalhe.setValorIcms(liquido.multiply(new BigDecimal(taxaIcms)).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP));
        }
        if (taxaPis != null) {
            detalhe.setTaxaPis(new BigDecimal(taxaPis));
            detalhe.setValorPis(liquido.multiply(new BigDecimal(taxaPis)).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP));
            detalhe.setTaxaCofins(new BigDecimal(taxaCofins));
            detalhe.setValorCofins(liquido.multiply(new BigDecimal(taxaCofins)).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP));
        }
        return detalhe;
    }

    private static PdvTotalTipoPagamento pagamento(String tPag, String descricao, String valor) {
        TipoPagamento tipo = new TipoPagamento();
        tipo.setCodigoPagamentoNfce(tPag);
        tipo.setDescricao(descricao);
        PdvTotalTipoPagamento pagamento = new PdvTotalTipoPagamento();
        pagamento.setPdvTipoPagamento(tipo);
        pagamento.setValor(new BigDecimal(valor));
        pagamento.setEstorno("N");
        return pagamento;
    }

    @SuppressWarnings("unused")
    private static List<PdvVendaDetalhe> lista(PdvVendaDetalhe... itens) {
        return new ArrayList<>(List.of(itens));
    }
}
