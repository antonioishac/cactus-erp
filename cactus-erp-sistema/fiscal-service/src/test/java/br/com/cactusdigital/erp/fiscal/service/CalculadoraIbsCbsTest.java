package br.com.cactusdigital.erp.fiscal.service;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.cactusdigital.erp.fiscal.client.dto.AliquotaIbsCbsDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.ClassificacaoIbsCbsDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.ClienteFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronica;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronicaItem;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Fórmulas do IBS/CBS (NT 2025.002-RTC v1.50) com valores calculados à mão. Alíquotas de 2026 da NT:
 * IBS-UF 0,1% (UB18-10), IBS-Mun 0% (UB37-10), CBS 0,9% (UB56-10).
 */
class CalculadoraIbsCbsTest {

    private static final AliquotaIbsCbsDTO ALIQUOTAS_2026 =
            new AliquotaIbsCbsDTO(2026, new BigDecimal("0.1000"), new BigDecimal("0.0000"), new BigDecimal("0.9000"));
    private static final ClassificacaoIbsCbsDTO INTEGRAL =
            new ClassificacaoIbsCbsDTO("000001", "000", true, false, BigDecimal.ZERO, BigDecimal.ZERO, null);
    private static final ClassificacaoIbsCbsDTO REDUCAO_60 =
            new ClassificacaoIbsCbsDTO("011001", "011", true, true, new BigDecimal("60.0000"), new BigDecimal("60.0000"), null);
    private static final ClassificacaoIbsCbsDTO IMUNIDADE =
            new ClassificacaoIbsCbsDTO("410001", "410", false, false, BigDecimal.ZERO, BigDecimal.ZERO, null);

    private final CalculadoraIbsCbs calculadora = new CalculadoraIbsCbs();

    @Test
    void tributacaoIntegralUsaBaseSemIcmsPisCofins() {
        // UB16-10: vBC = vProd − vDesc − vPIS − vCOFINS − vICMS = 100 − 0 − 1,65 − 7,60 − 18 = 72,75
        NotaFiscalEletronicaItem item = item(1, "100.00", "0", "18.00", "1.65", "7.60");
        calculadora.calcularItem(item, INTEGRAL, ALIQUOTAS_2026);

        assertThat(item.getCstIbsCbs()).isEqualTo("000");
        assertThat(item.getClassificacaoIbsCbs()).isEqualTo("000001");
        assertThat(item.getValorBaseIbsCbs()).isEqualByComparingTo("72.75");
        assertThat(item.getValorIbsUf()).isEqualByComparingTo("0.07");     // 72,75 × 0,1% = 0,07275
        assertThat(item.getValorIbsMunicipio()).isEqualByComparingTo("0.00");
        assertThat(item.getValorIbs()).isEqualByComparingTo("0.07");       // UB54a-10: vIBSUF + vIBSMun
        assertThat(item.getValorCbs()).isEqualByComparingTo("0.65");       // 72,75 × 0,9% = 0,65475
        assertThat(item.getPercentualReducaoIbs()).isNull();
        assertThat(item.getAliquotaEfetivaCbs()).isEqualByComparingTo("0.9000");
    }

    @Test
    void baseUsaOsValoresArredondadosComoVaoNoXml() {
        // O INI manda 6 casas e o ACBr arredonda para 2: a base tem de sair dos valores de 2 casas.
        NotaFiscalEletronicaItem item = item(1, "33.333333", "0.004999", "5.999999", null, null);
        calculadora.calcularItem(item, INTEGRAL, ALIQUOTAS_2026);
        assertThat(item.getValorBaseIbsCbs()).isEqualByComparingTo("27.33"); // 33,33 − 0,00 − 6,00
    }

    @Test
    void reducaoDeAliquotaUsaAliquotaEfetiva() {
        // pAliqEfet = alíquota × (1 − 60/100): IBS-UF 0,04%, CBS 0,36%; base 1000
        NotaFiscalEletronicaItem item = item(1, "1000.00", "0", null, null, null);
        calculadora.calcularItem(item, REDUCAO_60, ALIQUOTAS_2026);

        assertThat(item.getPercentualReducaoIbs()).isEqualByComparingTo("60");
        assertThat(item.getAliquotaEfetivaIbsUf()).isEqualByComparingTo("0.0400");
        assertThat(item.getValorIbsUf()).isEqualByComparingTo("0.40");
        assertThat(item.getAliquotaEfetivaCbs()).isEqualByComparingTo("0.3600");
        assertThat(item.getValorCbs()).isEqualByComparingTo("3.60");
        assertThat(item.getAliquotaIbsUf()).as("alíquota nominal continua a da lei").isEqualByComparingTo("0.1000");
    }

    @Test
    void imunidadeLevaSoCstEClassificacao() {
        NotaFiscalEletronicaItem item = item(1, "50.00", "0", null, null, null);
        calculadora.calcularItem(item, IMUNIDADE, ALIQUOTAS_2026);
        assertThat(item.getCstIbsCbs()).isEqualTo("410");
        assertThat(item.getValorBaseIbsCbs()).isNull();
        assertThat(item.getValorCbs()).isNull();
    }

    @Test
    void totaisSomamOsItensENotaSemIbsCbsFicaSemTotais() {
        NotaFiscalEletronica nota = nota();
        NotaFiscalEletronicaItem a = item(1, "100.00", "0", "18.00", "1.65", "7.60");
        NotaFiscalEletronicaItem b = item(2, "1000.00", "0", null, null, null);
        NotaFiscalEletronicaItem c = item(3, "50.00", "0", null, null, null);
        calculadora.calcularItem(a, INTEGRAL, ALIQUOTAS_2026);
        calculadora.calcularItem(b, REDUCAO_60, ALIQUOTAS_2026);
        calculadora.calcularItem(c, IMUNIDADE, ALIQUOTAS_2026);
        nota.getItens().addAll(List.of(a, b, c));
        calculadora.totalizar(nota);

        assertThat(nota.getValorBaseIbsCbs()).isEqualByComparingTo("1072.75");
        assertThat(nota.getValorIbsUf()).isEqualByComparingTo("0.47");
        assertThat(nota.getValorIbs()).isEqualByComparingTo("0.47");
        assertThat(nota.getValorCbs()).isEqualByComparingTo("4.25");

        NotaFiscalEletronica semIbsCbs = nota();
        semIbsCbs.getItens().add(item(1, "10.00", "0", null, null, null));
        calculadora.totalizar(semIbsCbs);
        assertThat(semIbsCbs.getValorBaseIbsCbs()).isNull();
    }

    @Test
    void iniLevaOsGruposNoFormatoDoModeloOficialDoAcbr() {
        NotaFiscalEletronica nota = nota();
        NotaFiscalEletronicaItem a = item(1, "100.00", "0", "18.00", "1.65", "7.60");
        NotaFiscalEletronicaItem b = item(2, "1000.00", "0", null, null, null);
        NotaFiscalEletronicaItem c = item(3, "50.00", "0", null, null, null);
        calculadora.calcularItem(a, INTEGRAL, ALIQUOTAS_2026);
        calculadora.calcularItem(b, REDUCAO_60, ALIQUOTAS_2026);
        calculadora.calcularItem(c, IMUNIDADE, ALIQUOTAS_2026);
        nota.getItens().addAll(List.of(a, b, c));
        calculadora.totalizar(nota);

        String ini = new NfeIniBuilder().construirIniNFe(nota, emitente(), destinatario(), null);

        assertThat(ini).contains("[IBSCBS001]\r\nCST=000\r\ncClassTrib=000001\r\n"
                + "[gIBSCBS001]\r\nvBC=72.75\r\nvIBS=0.07\r\n"
                + "[gIBSUF001]\r\npIBSUF=0.1000\r\nvIBSUF=0.07\r\n"
                + "[gIBSMun001]\r\npIBSMun=0.0000\r\nvIBSMun=0.00\r\n"
                + "[gCBS001]\r\npCBS=0.9000\r\nvCBS=0.65\r\n");
        assertThat(ini).contains("[gCBS002]\r\npCBS=0.9000\r\nvCBS=3.60\r\npRedAliq=60.0000\r\npAliqEfet=0.3600\r\n");
        assertThat(ini).contains("[IBSCBS003]\r\nCST=410\r\ncClassTrib=410001\r\n").doesNotContain("[gIBSCBS003]");
        assertThat(ini).contains("[IBSCBSTot]\r\nvBCIBSCBS=1072.75\r\n[gIBS]\r\nvIBS=0.47\r\n")
                .contains("[gCBSTot]\r\nvDif=0.00\r\nvDevTrib=0.00\r\nvCBS=4.25\r\n");
    }

    private static NotaFiscalEletronicaItem item(int numero, String valorTotal, String desconto, String icms, String pis, String cofins) {
        return NotaFiscalEletronicaItem.builder()
                .numeroItem(numero).produtoId((long) numero).descricao("Produto " + numero).ncm("22021000")
                .quantidade(BigDecimal.ONE).valorUnitario(new BigDecimal(valorTotal)).valorTotal(new BigDecimal(valorTotal))
                .valorDesconto(new BigDecimal(desconto)).cstIcms("00")
                .valorIcms(icms != null ? new BigDecimal(icms) : null)
                .valorPis(pis != null ? new BigDecimal(pis) : null)
                .valorCofins(cofins != null ? new BigDecimal(cofins) : null)
                .build();
    }

    private static NotaFiscalEletronica nota() {
        NotaFiscalEletronica nota = NotaFiscalEletronica.builder().empresaId(1L).serie("1").numero(10L)
                .ambiente(AmbienteFiscal.HOMOLOGACAO).valorTotal(new BigDecimal("1150.00")).build();
        nota.setId(42L);
        nota.setDataEmissao(Instant.parse("2026-10-25T15:00:00Z"));
        return nota;
    }

    private static EmpresaFiscalDTO emitente() {
        return new EmpresaFiscalDTO(1L, "Cactus", "Cactus", "12345678000195", "123", "3",
                null, "Rua", "1", null, "Centro", "01000000", "Sao Paulo", "SP", 3550308, 35);
    }

    private static ClienteFiscalDTO destinatario() {
        return new ClienteFiscalDTO(2L, "Cliente", "JURIDICA", "98765432000110", null, null, List.of(), null);
    }
}
