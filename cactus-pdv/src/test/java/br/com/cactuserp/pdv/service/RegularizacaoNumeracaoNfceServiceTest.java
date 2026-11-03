package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.LojaConfiguracao;
import br.com.cactuserp.pdv.entity.PdvNfceNumeroPendente;
import br.com.cactuserp.pdv.entity.PdvNfceXml;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.SituacaoNumeroPendente;
import br.com.cactuserp.pdv.entity.StatusFiscal;
import br.com.cactuserp.pdv.repository.LojaConfiguracaoRepository;
import br.com.cactuserp.pdv.repository.PdvNfceNumeroPendenteRepository;
import br.com.cactuserp.pdv.repository.PdvNfceXmlRepository;
import br.com.cactuserp.pdv.repository.PdvVendaCabecalhoRepository;
import br.com.cactuserp.pdv.service.fiscal.ContingenciaNfceService;
import br.com.cactuserp.pdv.service.fiscal.NfceConfigFactory;
import br.com.cactuserp.pdv.service.fiscal.TransmissorNfce;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Regularização dos números de NFC-e que não ficaram com o cupom (substituição 110112, inutilização, dispensa por UF). */
class RegularizacaoNumeracaoNfceServiceTest {

    private static final String CHAVE_TENTATIVA = "35261111222333000181650050000001231000000011";
    private static final String CHAVE_SUBSTITUTA = "35261111222333000181650050000001249000000022";

    private final PdvNfceNumeroPendenteRepository pendenteRepository = mock(PdvNfceNumeroPendenteRepository.class);
    private final PdvVendaCabecalhoRepository cabecalhoRepository = mock(PdvVendaCabecalhoRepository.class);
    private final PdvNfceXmlRepository xmlRepository = mock(PdvNfceXmlRepository.class);
    private final LojaConfiguracaoRepository lojaRepository = mock(LojaConfiguracaoRepository.class);
    private final TransmissorNfce transmissor = mock(TransmissorNfce.class);
    private final RegularizacaoNumeracaoNfceService service = new RegularizacaoNumeracaoNfceService(pendenteRepository, cabecalhoRepository,
            xmlRepository, lojaRepository, transmissor, mock(NfceConfigFactory.class), new ContingenciaNfceService());

    private final LojaConfiguracao loja = new LojaConfiguracao();
    private PdvNfceNumeroPendente pendente;
    private final PdvVendaCabecalho substituta = new PdvVendaCabecalho();

    @BeforeEach
    void montar() {
        loja.setCnpj("11.222.333/0001-81");
        loja.setUf("MG");
        when(lojaRepository.buscarConfiguracao()).thenReturn(Optional.of(loja));
        pendente = new PdvNfceNumeroPendente(1L, 41L, "2", 5, 123L, CHAVE_TENTATIVA, "timeout");
        when(pendenteRepository.findById(7L)).thenReturn(Optional.of(pendente));
        substituta.setId(41L);
        substituta.setChaveAcessoFiscal(CHAVE_SUBSTITUTA);
        substituta.setStatusFiscal(StatusFiscal.AUTORIZADA);
        when(cabecalhoRepository.findById(41L)).thenReturn(Optional.of(substituta));
        PdvNfceXml xml = new PdvNfceXml(41L, "<enviNFe/>", "qr", "url");
        xml.setXmlProcessado("<nfeProc><NFe/><protNFe versao=\"4.00\"><infProt><tpAmb>2</tpAmb><verAplic>SP_NFCE_PL_009_V4</verAplic></infProt></protNFe></nfeProc>");
        when(xmlRepository.findById(41L)).thenReturn(Optional.of(xml));
    }

    @Test
    void tentativaAutorizadaECanceladaPorSubstituicaoReferenciandoASubstituta() {
        consultaRetorna(TransmissorNfce.SituacaoConsulta.AUTORIZADA, "100", "135260000000555");
        when(transmissor.cancelarPorSubstituicao(eq(CHAVE_TENTATIVA), eq("135260000000555"), anyString(), eq("SP_NFCE_PL_009_V4"),
                eq(CHAVE_SUBSTITUTA), any())).thenReturn(new TransmissorNfce.RetornoEvento(true, "135", "Evento registrado", "135260000000999", "<ret/>"));

        service.regularizar(7L);

        assertThat(pendente.getSituacao()).isEqualTo(SituacaoNumeroPendente.S);
        assertThat(pendente.getProtocolo()).isEqualTo("135260000000999");
    }

    @Test
    void substitutaAindaNaoAutorizadaEsperaAProximaRodada() {
        consultaRetorna(TransmissorNfce.SituacaoConsulta.AUTORIZADA, "100", "135260000000555");
        substituta.setStatusFiscal(StatusFiscal.CONTINGENCIA_OFFLINE);

        service.regularizar(7L);

        assertThat(pendente.getSituacao()).isEqualTo(SituacaoNumeroPendente.V);
        verify(transmissor, never()).cancelarPorSubstituicao(any(), any(), any(), any(), any(), any());
    }

    @Test
    void tentativaQueNaoChegouASefazEInutilizadaForaDasUfsDispensadas() {
        consultaRetorna(TransmissorNfce.SituacaoConsulta.NAO_ENCONTRADA, "217", null);
        when(transmissor.inutilizar(anyInt(), eq("11222333000181"), eq(5), eq(123L), anyString(), any()))
                .thenReturn(new TransmissorNfce.RetornoEvento(true, "102", "Inutilizacao de numero homologado", "135260000000321", "<ret/>"));

        service.regularizar(7L);

        assertThat(pendente.getSituacao()).isEqualTo(SituacaoNumeroPendente.I);
        assertThat(pendente.getProtocolo()).isEqualTo("135260000000321");
    }

    @Test
    void emSaoPauloAInutilizacaoEDispensada() {
        loja.setUf("SP");
        pendente = new PdvNfceNumeroPendente(1L, null, "2", 5, 123L, null, "abandonado");
        when(pendenteRepository.findById(7L)).thenReturn(Optional.of(pendente));

        service.regularizar(7L);

        assertThat(pendente.getSituacao()).isEqualTo(SituacaoNumeroPendente.D);
        verify(transmissor, never()).inutilizar(anyInt(), anyString(), anyInt(), anyLong(), anyString(), any());
    }

    @Test
    void recusasSeguidasViramAcaoManualNaQuintaTentativa() {
        consultaRetorna(TransmissorNfce.SituacaoConsulta.NAO_ENCONTRADA, "217", null);
        when(transmissor.inutilizar(anyInt(), anyString(), anyInt(), anyLong(), anyString(), any()))
                .thenReturn(new TransmissorNfce.RetornoEvento(false, "241", "Um numero da faixa ja foi utilizado", null, null));

        for (int i = 0; i < RegularizacaoNumeracaoNfceService.MAXIMO_TENTATIVAS; i++) {
            service.regularizar(7L);
        }

        assertThat(pendente.getSituacao()).isEqualTo(SituacaoNumeroPendente.E);
        assertThat(pendente.getMensagem()).contains("241");
    }

    @Test
    void dispensaSoValeParaAcBaSpAPartirDe01DeNovembroDe2026() {
        LocalDate vigencia = LocalDate.of(2026, 11, 1);
        assertThat(RegularizacaoNumeracaoNfceService.dispensaInutilizacao("SP", vigencia)).isTrue();
        assertThat(RegularizacaoNumeracaoNfceService.dispensaInutilizacao("ba", vigencia)).isTrue();
        assertThat(RegularizacaoNumeracaoNfceService.dispensaInutilizacao("SP", vigencia.minusDays(1))).isFalse();
        assertThat(RegularizacaoNumeracaoNfceService.dispensaInutilizacao("RJ", vigencia)).isFalse();
    }

    @Test
    void versaoDoAplicativoVemDoProtocoloDaSubstituta() {
        assertThat(RegularizacaoNumeracaoNfceService.versaoAplicativo("<nfeProc><NFe><infNFe><verAplic>X</verAplic></infNFe></NFe>"
                + "<protNFe><infProt><verAplic>SP_NFCE_9</verAplic></infProt></protNFe></nfeProc>")).isEqualTo("SP_NFCE_9");
    }

    private void consultaRetorna(TransmissorNfce.SituacaoConsulta situacao, String cStat, String protocolo) {
        when(transmissor.consultar(eq(CHAVE_TENTATIVA), any(), any()))
                .thenReturn(new TransmissorNfce.RetornoConsulta(situacao, cStat, "motivo", protocolo, "SP_NFCE_1", null));
    }
}
