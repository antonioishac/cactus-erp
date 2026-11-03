package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.LojaConfiguracao;
import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvConfiguracao;
import br.com.cactuserp.pdv.entity.PdvNfceNumeroPendente;
import br.com.cactuserp.pdv.entity.PdvNfceXml;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.SimNao;
import br.com.cactuserp.pdv.entity.StatusFiscal;
import br.com.cactuserp.pdv.repository.LojaConfiguracaoRepository;
import br.com.cactuserp.pdv.repository.PdvConfiguracaoRepository;
import br.com.cactuserp.pdv.repository.PdvNfceNumeroPendenteRepository;
import br.com.cactuserp.pdv.repository.PdvNfceXmlRepository;
import br.com.cactuserp.pdv.repository.PdvTotalTipoPagamentoRepository;
import br.com.cactuserp.pdv.repository.PdvVendaCabecalhoRepository;
import br.com.cactuserp.pdv.repository.PdvVendaDetalheRepository;
import br.com.cactuserp.pdv.repository.ProdutoUnidadeRepository;
import br.com.cactuserp.pdv.service.dto.ResultadoEmissaoFiscal;
import br.com.cactuserp.pdv.service.fiscal.AssinadorNfce;
import br.com.cactuserp.pdv.service.fiscal.ContingenciaNfceService;
import br.com.cactuserp.pdv.service.fiscal.FalhaComunicacaoNfceException;
import br.com.cactuserp.pdv.service.fiscal.FiscalPdvProperties;
import br.com.cactuserp.pdv.service.fiscal.NfceConfigFactory;
import br.com.cactuserp.pdv.service.fiscal.NfceConfiguracaoException;
import br.com.cactuserp.pdv.service.fiscal.NfceDocumentoBuilder;
import br.com.cactuserp.pdv.service.fiscal.NumeracaoNfceService;
import br.com.cactuserp.pdv.service.fiscal.TransmissorNfce;
import com.fincatto.documentofiscal.nfe.NFTipoEmissao;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Orquestração da NFC-e no PDV com a SEFAZ simulada pela porta {@link TransmissorNfce}: autorizada, rejeitada,
 * duplicidade, sem conexão (mesmo número offline), timeout (outro número offline + número pendente), contingência ativa
 * e erro de cadastro. Montagem e assinatura são testadas à parte ({@code NfceDocumentoBuilderTest}).
 */
class EmissaoFiscalServiceTest {

    private static final String CHAVE_NORMAL = "35261111222333000181650050000001231000000011";
    private static final String CHAVE_OFFLINE = "35261111222333000181650050000001249000000022";

    private final PdvConfiguracaoRepository configuracaoRepository = mock(PdvConfiguracaoRepository.class);
    private final LojaConfiguracaoRepository lojaRepository = mock(LojaConfiguracaoRepository.class);
    private final PdvVendaDetalheRepository detalheRepository = mock(PdvVendaDetalheRepository.class);
    private final PdvTotalTipoPagamentoRepository pagamentoRepository = mock(PdvTotalTipoPagamentoRepository.class);
    private final PdvVendaCabecalhoRepository cabecalhoRepository = mock(PdvVendaCabecalhoRepository.class);
    private final ProdutoUnidadeRepository unidadeRepository = mock(ProdutoUnidadeRepository.class);
    private final PdvNfceXmlRepository xmlRepository = mock(PdvNfceXmlRepository.class);
    private final PdvNfceNumeroPendenteRepository pendenteRepository = mock(PdvNfceNumeroPendenteRepository.class);
    private final NumeracaoNfceService numeracao = mock(NumeracaoNfceService.class);
    private final NfceDocumentoBuilder builder = mock(NfceDocumentoBuilder.class);
    private final AssinadorNfce assinador = mock(AssinadorNfce.class);
    private final NfceConfigFactory configFactory = mock(NfceConfigFactory.class);
    private final TransmissorNfce transmissor = mock(TransmissorNfce.class);
    private final ContingenciaNfceService contingencia = new ContingenciaNfceService();

    private EmissaoFiscalService service;
    private PdvCaixa caixa;
    private PdvVendaCabecalho cupom;
    private PdvConfiguracao terminal;
    private final AtomicLong proximoNumero = new AtomicLong(123);
    private final List<NFTipoEmissao> tiposAssinados = new ArrayList<>();
    private PdvNfceXml xmlGuardado;

    @BeforeEach
    void montar() {
        service = new EmissaoFiscalService(configuracaoRepository, lojaRepository, detalheRepository, pagamentoRepository,
                cabecalhoRepository, unidadeRepository, xmlRepository, pendenteRepository, numeracao, contingencia, builder, assinador,
                configFactory, transmissor, new FiscalPdvProperties(null, null, false, null, null), new TransacaoFalsa());
        caixa = new PdvCaixa();
        caixa.setId(1L);
        cupom = new PdvVendaCabecalho();
        cupom.setId(41L);
        terminal = new PdvConfiguracao();
        terminal.setNfceHabilitada(SimNao.S);

        when(cabecalhoRepository.findById(41L)).thenReturn(Optional.of(cupom));
        when(cabecalhoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(configuracaoRepository.findFirstByCaixaId(1L)).thenReturn(Optional.of(terminal));
        when(lojaRepository.buscarConfiguracao()).thenReturn(Optional.of(new LojaConfiguracao()));
        when(detalheRepository.findByPdvVendaCabecalhoAndCanceladoOrderByItemAsc(any(), any())).thenReturn(List.of());
        when(pagamentoRepository.findByIdPdvVendaCabecalho(anyLong())).thenReturn(List.of());
        when(unidadeRepository.findAllById(any())).thenReturn(List.of());
        when(xmlRepository.findById(41L)).thenAnswer(i -> Optional.ofNullable(xmlGuardado));
        when(xmlRepository.save(any())).thenAnswer(i -> xmlGuardado = i.getArgument(0));

        // Reserva: grava série/número no cupom como a transação real faz.
        when(numeracao.reservarParaCupom(eq(1L), eq(41L), anyString(), any())).thenAnswer(i -> {
            long numero = proximoNumero.getAndIncrement();
            cupom.setNfceSerie(5);
            cupom.setNfceNumero(numero);
            cupom.setNfceTipoEmissao(i.getArgument(2));
            cupom.setNfceDataHoraEmissao(i.getArgument(3));
            cupom.setStatusFiscal(StatusFiscal.PENDENTE);
            return new NumeracaoNfceService.NumeroNfce(5, numero);
        });
        when(builder.construir(any())).thenAnswer(i -> {
            NfceDocumentoBuilder.DadosNfce dados = i.getArgument(0);
            tiposAssinados.add(dados.tipoEmissao());
            return new NFNota();
        });
        when(assinador.assinar(any(), any(), anyLong())).thenAnswer(i -> {
            boolean offline = tiposAssinados.get(tiposAssinados.size() - 1) == NFTipoEmissao.CONTIGENCIA_OFFLINE;
            return new AssinadorNfce.DocumentoNfceAssinado("<enviNFe/>", offline ? CHAVE_OFFLINE : CHAVE_NORMAL, "https://qr?p=x", "url",
                    new NFNota());
        });
    }

    @Test
    void terminalSemNfceHabilitadaFicaComRecibo() {
        terminal.setNfceHabilitada(SimNao.N);

        ResultadoEmissaoFiscal resultado = service.emitir(caixa, cupom);

        assertThat(resultado.getStatus()).isEqualTo(ResultadoEmissaoFiscal.Status.NAO_CONFIGURADO);
        assertThat(cupom.getStatusFiscal()).isEqualTo(StatusFiscal.NAO_HABILITADA);
        verify(numeracao, never()).reservarParaCupom(anyLong(), anyLong(), anyString(), any());
    }

    @Test
    void autorizadaGuardaProtocoloENfeProc() {
        when(transmissor.autorizar(any(), any(), any())).thenReturn(autorizacao(TransmissorNfce.Resultado.AUTORIZADA, "100"));

        ResultadoEmissaoFiscal resultado = service.emitir(caixa, cupom);

        assertThat(resultado.getStatus()).isEqualTo(ResultadoEmissaoFiscal.Status.AUTORIZADA);
        assertThat(cupom.getStatusFiscal()).isEqualTo(StatusFiscal.AUTORIZADA);
        assertThat(cupom.getTipoOperacao()).isEqualTo("NFC");
        assertThat(cupom.getNfceNumero()).isEqualTo(123L);
        assertThat(cupom.getChaveAcessoFiscal()).isEqualTo(CHAVE_NORMAL);
        assertThat(cupom.getProtocoloAutorizacao()).isEqualTo("135260000000001");
        assertThat(xmlGuardado.getXmlProcessado()).isEqualTo("<nfeProc/>");
        assertThat(contingencia.ativa()).isFalse();
    }

    @Test
    void rejeitadaFicaParaCorrecaoSemContingencia() {
        when(transmissor.autorizar(any(), any(), any()))
                .thenReturn(new TransmissorNfce.RetornoAutorizacao(TransmissorNfce.Resultado.REJEITADA, "778", "NCM inexistente", null, null, null));

        ResultadoEmissaoFiscal resultado = service.emitir(caixa, cupom);

        assertThat(resultado.getStatus()).isEqualTo(ResultadoEmissaoFiscal.Status.REJEITADA);
        assertThat(cupom.getStatusFiscal()).isEqualTo(StatusFiscal.REJEITADA);
        assertThat(cupom.getMensagemFiscal()).isEqualTo("778 - NCM inexistente");
        assertThat(contingencia.ativa()).isFalse();
    }

    @Test
    void duplicidadeEReconciliadaPelaConsultaDaChave() {
        when(transmissor.autorizar(any(), any(), any()))
                .thenReturn(new TransmissorNfce.RetornoAutorizacao(TransmissorNfce.Resultado.DUPLICIDADE, "204", "Duplicidade de NF-e", null, null, null));
        when(transmissor.consultar(eq(CHAVE_NORMAL), any(), any())).thenReturn(new TransmissorNfce.RetornoConsulta(
                TransmissorNfce.SituacaoConsulta.AUTORIZADA, "100", "Autorizado o uso", "135260000000777", "SP_NFCE_1", "<nfeProc/>"));

        service.emitir(caixa, cupom);

        assertThat(cupom.getStatusFiscal()).isEqualTo(StatusFiscal.AUTORIZADA);
        assertThat(cupom.getProtocoloAutorizacao()).isEqualTo("135260000000777");
    }

    @Test
    void semConexaoEmiteOfflineComOMesmoNumero() {
        when(transmissor.autorizar(any(), any(), any())).thenThrow(new FalhaComunicacaoNfceException(new ConnectException("Connection refused")));

        ResultadoEmissaoFiscal resultado = service.emitir(caixa, cupom);

        assertThat(resultado.getStatus()).isEqualTo(ResultadoEmissaoFiscal.Status.CONTINGENCIA);
        assertThat(resultado.foiEmitido()).isTrue();
        assertThat(cupom.getStatusFiscal()).isEqualTo(StatusFiscal.CONTINGENCIA_OFFLINE);
        assertThat(cupom.getNfceTipoEmissao()).isEqualTo("9");
        assertThat(cupom.getNfceNumero()).as("nada chegou à SEFAZ: mesmo número").isEqualTo(123L);
        assertThat(cupom.getChaveAcessoFiscal()).isEqualTo(CHAVE_OFFLINE);
        assertThat(tiposAssinados).containsExactly(NFTipoEmissao.EMISSAO_NORMAL, NFTipoEmissao.CONTIGENCIA_OFFLINE);
        assertThat(contingencia.ativa()).isTrue();
        verify(pendenteRepository, never()).save(any());
    }

    @Test
    void timeoutEmiteOfflineComOutroNumeroERegistraATentativa() {
        when(transmissor.autorizar(any(), any(), any())).thenThrow(new FalhaComunicacaoNfceException(new SocketTimeoutException("Read timed out")));

        service.emitir(caixa, cupom);

        assertThat(cupom.getStatusFiscal()).isEqualTo(StatusFiscal.CONTINGENCIA_OFFLINE);
        assertThat(cupom.getNfceNumero()).as("a tentativa pode ter sido autorizada: outro número").isEqualTo(124L);
        ArgumentCaptor<PdvNfceNumeroPendente> pendente = ArgumentCaptor.forClass(PdvNfceNumeroPendente.class);
        verify(pendenteRepository).save(pendente.capture());
        assertThat(pendente.getValue().getNumero()).isEqualTo(123L);
        assertThat(pendente.getValue().getChaveAcesso()).isEqualTo(CHAVE_NORMAL);
        assertThat(pendente.getValue().getIdPdvVendaCabecalho()).isEqualTo(41L);
    }

    @Test
    void comContingenciaAtivaNemTentaOnline() {
        contingencia.entrar("SEFAZ fora do ar");

        ResultadoEmissaoFiscal resultado = service.emitir(caixa, cupom);

        assertThat(resultado.getStatus()).isEqualTo(ResultadoEmissaoFiscal.Status.CONTINGENCIA);
        assertThat(tiposAssinados).containsExactly(NFTipoEmissao.CONTIGENCIA_OFFLINE);
        verify(transmissor, never()).autorizar(any(), any(), any());
    }

    @Test
    void erroDeCadastroFicaPendenteComAMensagem() {
        org.mockito.Mockito.doThrow(new NfceConfiguracaoException("Produto #7 sem NCM válido (8 dígitos).")).when(builder).construir(any());

        ResultadoEmissaoFiscal resultado = service.emitir(caixa, cupom);

        assertThat(resultado.getStatus()).isEqualTo(ResultadoEmissaoFiscal.Status.PENDENTE_REPROCESSAMENTO);
        assertThat(cupom.getStatusFiscal()).isEqualTo(StatusFiscal.PENDENTE);
        assertThat(cupom.getMensagemFiscal()).contains("sem NCM");
        assertThat(cupom.getNfceNumero()).as("o número reservado continua com o cupom").isEqualTo(123L);
    }

    @Test
    void cupomJaAutorizadoNaoEEmitidoDeNovo() {
        cupom.setStatusFiscal(StatusFiscal.AUTORIZADA);

        service.emitir(caixa, cupom);

        verify(numeracao, never()).reservarParaCupom(anyLong(), anyLong(), anyString(), any());
        verify(transmissor, never()).autorizar(any(), any(), any());
    }

    @Test
    void falhaDeComunicacaoDistingueNaoConectouDePodeTerChegado() {
        assertThat(new FalhaComunicacaoNfceException(new ConnectException("refused")).isPodeTerSidoRecebida()).isFalse();
        assertThat(new FalhaComunicacaoNfceException(new IOException("x", new java.net.UnknownHostException("sefaz"))).isPodeTerSidoRecebida()).isFalse();
        assertThat(new FalhaComunicacaoNfceException(new SocketTimeoutException("Read timed out")).isPodeTerSidoRecebida()).isTrue();
    }

    private static TransmissorNfce.RetornoAutorizacao autorizacao(TransmissorNfce.Resultado resultado, String cStat) {
        return new TransmissorNfce.RetornoAutorizacao(resultado, cStat, "Autorizado o uso da NF-e", "135260000000001", "SP_NFCE_1", "<nfeProc/>");
    }

    @SuppressWarnings("unused")
    private static LocalDateTime agora() {
        return LocalDateTime.now();
    }

    /** Executa o callback direto: o teste não tem banco. */
    private static final class TransacaoFalsa implements org.springframework.transaction.PlatformTransactionManager {
        @Override
        public org.springframework.transaction.TransactionStatus getTransaction(org.springframework.transaction.TransactionDefinition definicao) {
            return new org.springframework.transaction.support.SimpleTransactionStatus();
        }

        @Override
        public void commit(org.springframework.transaction.TransactionStatus status) {
        }

        @Override
        public void rollback(org.springframework.transaction.TransactionStatus status) {
        }
    }

    @SuppressWarnings("unused")
    private static <T> T qualquer() {
        return isNull();
    }
}
