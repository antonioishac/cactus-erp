package br.com.cactusdigital.erp.fiscal.sefaz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.fiscal.client.dto.AliquotaIbsCbsDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.ClassificacaoIbsCbsDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.ClienteFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EnderecoClienteDTO;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronica;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronicaItem;
import br.com.cactusdigital.erp.fiscal.service.CalculadoraIbsCbs;
import com.fincatto.documentofiscal.DFAmbiente;
import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.nfe400.classes.lote.envio.NFLoteEnvio;
import com.fincatto.documentofiscal.nfe400.classes.lote.envio.NFLoteIndicadorProcessamento;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNota;
import com.fincatto.documentofiscal.nfe400.utils.NFGeraChave;
import com.fincatto.documentofiscal.utils.DFAssinaturaDigital;
import com.fincatto.documentofiscal.validadores.DFXMLValidador;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Leiaute da NF-e 4.00 gerado por {@link NfeDocumentoBuilder}, conferido contra o <b>XSD oficial</b> (empacotado na
 * biblioteca) depois de assinado — o mesmo passo que {@link SefazEmissorNfe} executa antes de qualquer transmissão. A
 * assinatura usa um certificado autoassinado gerado pelo {@code keytool} do JDK: o XSD exige a assinatura, mas não
 * valida a cadeia ICP-Brasil (isso é a SEFAZ que faz).
 */
class NfeDocumentoBuilderTest {

    private static final AliquotaIbsCbsDTO ALIQUOTAS_2026 =
            new AliquotaIbsCbsDTO(2026, new BigDecimal("0.1000"), new BigDecimal("0.0000"), new BigDecimal("0.9000"));
    private static final ClassificacaoIbsCbsDTO INTEGRAL =
            new ClassificacaoIbsCbsDTO("000001", "000", true, false, BigDecimal.ZERO, BigDecimal.ZERO, null);
    private static final ClassificacaoIbsCbsDTO REDUCAO_60 =
            new ClassificacaoIbsCbsDTO("011001", "011", true, true, new BigDecimal("60.0000"), new BigDecimal("60.0000"), null);
    private static final ClassificacaoIbsCbsDTO IMUNIDADE =
            new ClassificacaoIbsCbsDTO("410001", "410", false, false, BigDecimal.ZERO, BigDecimal.ZERO, null);

    private static CactusNFeConfig config;

    private final NfeDocumentoBuilder builder = new NfeDocumentoBuilder(
            new FiscalProperties(false, null, null, new FiscalProperties.ResponsavelTecnico("11222333000181", "Suporte", "fiscal@cactus.com.br", "11999990000")));
    private final CalculadoraIbsCbs calculadora = new CalculadoraIbsCbs();

    @BeforeAll
    static void gerarCertificadoDeTeste(@TempDir Path pasta) throws Exception {
        Path arquivo = pasta.resolve("teste.p12");
        String keytool = Path.of(System.getProperty("java.home"), "bin", "keytool").toString();
        Process processo = new ProcessBuilder(keytool, "-genkeypair", "-alias", "teste", "-keyalg", "RSA", "-keysize", "2048",
                "-dname", "CN=EMPRESA TESTE:11222333000181", "-validity", "2", "-storetype", "PKCS12",
                "-keystore", arquivo.toString(), "-storepass", "senha123", "-keypass", "senha123")
                .redirectErrorStream(true).start();
        assertThat(processo.waitFor()).as(new String(processo.getInputStream().readAllBytes())).isZero();

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream in = Files.newInputStream(arquivo)) {
            keyStore.load(in, "senha123".toCharArray());
        }
        var certificado = new CertificadoDigitalProvider.CertificadoA1(keyStore, "senha123", "teste", (X509Certificate) keyStore.getCertificate("teste"));
        config = new CactusNFeConfig(DFUnidadeFederativa.SP, DFAmbiente.HOMOLOGACAO, certificado,
                new CadeiaCertificadosProvider.CadeiaCertificados(null, ""));
    }

    @Test
    void regimeNormalComIbsCbsPassaNoXsdDepoisDeAssinado() throws Exception {
        NotaFiscalEletronica nota = nota();
        NotaFiscalEletronicaItem integral = item(1, "2", "50.00", "0", "00");
        integral.setAliquotaIcms(new BigDecimal("18.00"));
        integral.setValorBaseIcms(new BigDecimal("100.00"));
        integral.setValorIcms(new BigDecimal("18.000000"));
        integral.setAliquotaPis(new BigDecimal("1.65"));
        integral.setValorPis(new BigDecimal("1.650000"));
        integral.setAliquotaCofins(new BigDecimal("7.60"));
        integral.setValorCofins(new BigDecimal("7.600000"));
        NotaFiscalEletronicaItem reduzido = item(2, "1", "1000.00", "10.00", "40");
        NotaFiscalEletronicaItem imune = item(3, "1", "50.00", "0", "41");
        calculadora.calcularItem(integral, INTEGRAL, ALIQUOTAS_2026);
        calculadora.calcularItem(reduzido, REDUCAO_60, ALIQUOTAS_2026);
        calculadora.calcularItem(imune, IMUNIDADE, ALIQUOTAS_2026);
        nota.getItens().addAll(List.of(integral, reduzido, imune));
        calculadora.totalizar(nota);

        String xml = assinarEValidar(builder.construir(nota, emitente("3"), destinatario("SP", "123456789012"), null));

        assertThat(xml)
                .contains("<xNome>" + NfeDocumentoBuilder.NOME_DESTINATARIO_HOMOLOGACAO + "</xNome>")
                .contains("<tpAmb>2</tpAmb>", "<idDest>1</idDest>", "<indFinal>0</indFinal>", "<indIEDest>1</indIEDest>")
                .contains("<IBSCBS><CST>000</CST><cClassTrib>000001</cClassTrib><gIBSCBS><vBC>72.75</vBC>")
                .contains("<gCBS><pCBS>0.90</pCBS><gRed><pRedAliq>60.00</pRedAliq><pAliqEfet>0.36</pAliqEfet></gRed>")
                .contains("<IBSCBS><CST>410</CST><cClassTrib>410001</cClassTrib></IBSCBS>")
                .contains("<infRespTec><CNPJ>11222333000181</CNPJ>")
                // vProd = 100 + 1000 + 50; vDesc = 10; vNF = 1140
                .contains("<vProd>1150.00</vProd>", "<vDesc>10.00</vDesc>", "<vNF>1140.00</vNF>", "<vPag>1140.00</vPag>")
                .contains("<modFrete>9</modFrete>");
    }

    @Test
    void simplesNacionalSemIbsCbsPassaNoXsd() throws Exception {
        NotaFiscalEletronica nota = nota();
        NotaFiscalEletronicaItem item = item(1, "3", "9.99", "0", null);
        item.setCsosn("102");
        item.setCstPis("49");
        item.setCstCofins("49");
        nota.getItens().add(item);

        String xml = assinarEValidar(builder.construir(nota, emitente("1"), destinatario("SP", null), null));

        assertThat(xml).contains("<ICMSSN102><orig>0</orig><CSOSN>102</CSOSN></ICMSSN102>", "<CRT>1</CRT>",
                "<indFinal>1</indFinal>", "<indIEDest>9</indIEDest>", "<vNF>29.97</vNF>").doesNotContain("<IBSCBS>");
    }

    @Test
    void operacaoInterestadualERecusada() {
        NotaFiscalEletronica nota = nota();
        nota.getItens().add(item(1, "1", "10.00", "0", "40"));

        assertThatThrownBy(() -> builder.construir(nota, emitente("3"), destinatario("RJ", null), null))
                .isInstanceOf(BusinessException.class).hasMessageContaining("RJ");
    }

    @Test
    void cstDeIcmsComSubstituicaoTributariaERecusado() {
        NotaFiscalEletronica nota = nota();
        nota.getItens().add(item(1, "1", "10.00", "0", "10"));

        assertThatThrownBy(() -> builder.construir(nota, emitente("3"), destinatario("SP", null), null))
                .isInstanceOf(BusinessException.class).hasMessageContaining("CST de ICMS 10");
    }

    @Test
    void codigoNumericoEDeterministicoENuncaIgualAoNumero() {
        NotaFiscalEletronica nota = nota();
        assertThat(NfeDocumentoBuilder.codigoNumerico(nota)).hasSize(8).isEqualTo(NfeDocumentoBuilder.codigoNumerico(nota));

        nota.setNumero(Long.parseLong(NfeDocumentoBuilder.codigoNumerico(nota)));
        assertThat(NfeDocumentoBuilder.codigoNumerico(nota)).isNotEqualTo(String.format("%08d", nota.getNumero()));
    }

    /** Mesmos passos de {@code WSLoteEnvio#getLoteAssinado} (chave + DV, assinatura) e da validação de {@link SefazEmissorNfe}. */
    private static String assinarEValidar(NFNota nfNota) throws Exception {
        NFGeraChave geraChave = new NFGeraChave(nfNota);
        nfNota.getInfo().getIdentificacao().setDigitoVerificador(geraChave.getDV());
        nfNota.getInfo().setIdentificador(geraChave.getChaveAcesso());
        NFLoteEnvio lote = new NFLoteEnvio();
        lote.setIdLote("1");
        lote.setVersao("4.00");
        lote.setIndicadorProcessamento(NFLoteIndicadorProcessamento.PROCESSAMENTO_SINCRONO);
        lote.setNotas(List.of(nfNota));
        String assinado = new DFAssinaturaDigital(config).assinarDocumento(lote.toString());
        assertThat(DFXMLValidador.validaLote400(assinado)).isTrue();
        return assinado;
    }

    private static NotaFiscalEletronicaItem item(int numero, String quantidade, String unitario, String desconto, String cstIcms) {
        BigDecimal bruto = new BigDecimal(quantidade).multiply(new BigDecimal(unitario));
        return NotaFiscalEletronicaItem.builder()
                .numeroItem(numero).produtoId((long) numero).descricao("Produto " + numero).ncm("22021000").gtin("7891234567895")
                .unidade("UN").quantidade(new BigDecimal(quantidade)).valorUnitario(new BigDecimal(unitario))
                .valorDesconto(new BigDecimal(desconto)).valorTotal(bruto.subtract(new BigDecimal(desconto)))
                .cstIcms(cstIcms).cstPis("01").cstCofins("01")
                .build();
    }

    private static NotaFiscalEletronica nota() {
        NotaFiscalEletronica nota = NotaFiscalEletronica.builder().empresaId(1L).serie("1").numero(10L)
                .ambiente(AmbienteFiscal.HOMOLOGACAO).build();
        nota.setId(42L);
        nota.setDataEmissao(Instant.parse("2026-10-25T15:00:00Z"));
        return nota;
    }

    private static EmpresaFiscalDTO emitente(String crt) {
        return new EmpresaFiscalDTO(1L, "Cactus Digital Ltda", "Cactus", "11222333000181", "111222333444", crt,
                "1133334444", "Rua das Flores", "100", null, "Centro", "01001000", "Sao Paulo", "SP", 3550308, 35);
    }

    private static ClienteFiscalDTO destinatario(String uf, String inscricaoEstadual) {
        EnderecoClienteDTO endereco = new EnderecoClienteDTO("Av. Paulista", "1000", "Sala 1", "Bela Vista", "01310100",
                "Sao Paulo", uf, "SP".equals(uf) ? 3550308 : 3304557, "SP".equals(uf) ? 35 : 33);
        return new ClienteFiscalDTO(2L, "Cliente Real Ltda", "JURIDICA", "11444777000161", inscricaoEstadual,
                "compras@cliente.com.br", List.of(endereco), null);
    }
}
