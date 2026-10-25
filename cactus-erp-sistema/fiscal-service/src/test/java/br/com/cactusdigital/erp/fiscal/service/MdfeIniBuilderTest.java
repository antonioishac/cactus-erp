package br.com.cactusdigital.erp.fiscal.service;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.CteCabecalho;
import br.com.cactusdigital.erp.fiscal.entity.MdfeCabecalho;
import br.com.cactusdigital.erp.fiscal.entity.MdfeDocumentoVinculado;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/** Conferência estrutural do INI — o layout em si nunca foi validado contra um ACBr real (ver Javadoc de {@link MdfeIniBuilder}). */
class MdfeIniBuilderTest {

    private final MdfeIniBuilder builder = new MdfeIniBuilder();

    @Test
    void agrupaCtePorMunicipioDeDescargaETotaliza() {
        MdfeCabecalho mdfe = MdfeCabecalho.builder()
                .empresaId(1L).serie("1").numero(7L).ambiente(AmbienteFiscal.HOMOLOGACAO).uf("SP")
                .veiculoPlaca("ABC1D23").veiculoUf("SP").veiculoTaraKg(new BigDecimal("3500"))
                .condutorNome("Joao").condutorCpf("12345678901")
                .municipioCarregamentoCodigoIbge(3550308).municipioCarregamentoNome("Sao Paulo")
                .dataInicioViagem(LocalDate.of(2026, 10, 24))
                .valorTotalCarga(new BigDecimal("300.00")).pesoBrutoTotal(new BigDecimal("45.5"))
                .build();
        mdfe.setId(42L);
        mdfe.getDocumentos().add(documento(mdfe, "CHAVE_A", 3550308, "Sao Paulo"));
        mdfe.getDocumentos().add(documento(mdfe, "CHAVE_B", 3509502, "Campinas"));
        mdfe.getDocumentos().add(documento(mdfe, "CHAVE_C", 3550308, "Sao Paulo"));

        EmpresaFiscalDTO emitente = new EmpresaFiscalDTO(1L, "Cactus", "Cactus", "12345678000195", "123", "1",
                null, "Rua", "1", null, "Centro", "01000000", "Sao Paulo", "SP", 3550308, 35);

        String comando = builder.construirComando(mdfe, emitente, 1);

        assertThat(comando).startsWith("MDFE.CriarEnviarMDFe(").endsWith(",1,0)");
        assertThat(comando)
                .contains("[ide]\r\ncUF=35\r\ntpAmb=2\r\n")
                .contains("nMDF=7\r\n").contains("cMDF=00000042\r\n")
                .contains("UFIni=SP\r\nUFFim=SP\r\n")
                .contains("dhIniViagem=24/10/2026 00:00:00\r\n")
                .contains("[CARR001]\r\ncMunCarrega=3550308\r\n")
                .contains("[veicTracao]\r\nplaca=ABC1D23\r\n")
                .contains("[moto001]\r\nxNome=Joao\r\nCPF=12345678901\r\n")
                // São Paulo agrupa A e C; Campinas vira o segundo município
                .contains("[DESC001]\r\ncMunDescarga=3550308\r\nxMunDescarga=Sao Paulo\r\n[infCTe001001]\r\nchCTe=CHAVE_A\r\n[infCTe001002]\r\nchCTe=CHAVE_C\r\n")
                .contains("[DESC002]\r\ncMunDescarga=3509502\r\nxMunDescarga=Campinas\r\n[infCTe002001]\r\nchCTe=CHAVE_B\r\n")
                .contains("[tot]\r\nqCTe=3\r\nvCarga=300.00\r\ncUnid=01\r\nqCarga=45.5\r\n");
    }

    @Test
    void comandoDeEncerramentoSegueAssinaturaDoAcbr() {
        MdfeCabecalho mdfe = MdfeCabecalho.builder()
                .chaveAcesso("35261012345678000195580010000000011000000010").protocoloAutorizacao("135260000000001")
                .dataEncerramento(LocalDate.of(2026, 10, 25)).municipioEncerramentoCodigoIbge(3509502).build();

        assertThat(builder.construirComandoEncerramento(mdfe, "12345678000195")).isEqualTo(
                "MDFE.EncerrarMDFe(\"35261012345678000195580010000000011000000010\",\"25/10/2026\",\"3509502\",\"12345678000195\",\"135260000000001\")");
    }

    @Test
    void justificativaDoCancelamentoVaiEntreAspasSemAspasInternas() {
        MdfeCabecalho mdfe = MdfeCabecalho.builder()
                .chaveAcesso("CHAVE").justificativaCancelamento("Viagem desfeita, carga \"retida\" no cliente").build();

        assertThat(builder.construirComandoCancelamento(mdfe, "12345678000195")).isEqualTo(
                "MDFE.CancelarMDFe(\"CHAVE\",\"Viagem desfeita, carga retida no cliente\",\"12345678000195\")");
    }

    private MdfeDocumentoVinculado documento(MdfeCabecalho mdfe, String chave, int ibge, String municipio) {
        return MdfeDocumentoVinculado.builder()
                .mdfeCabecalho(mdfe)
                .cteCabecalho(CteCabecalho.builder().chaveAcesso(chave).build())
                .municipioDescargaCodigoIbge(ibge).municipioDescargaNome(municipio)
                .build();
    }
}
