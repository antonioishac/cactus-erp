package br.com.cactusdigital.erp.fiscal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.fiscal.client.CadastroClient;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaConfiguracaoFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.dto.InutilizarNumeracaoNfeRequest;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.InutilizacaoNumeracaoNfe;
import br.com.cactusdigital.erp.fiscal.entity.SituacaoInutilizacao;
import br.com.cactusdigital.erp.fiscal.exception.FiscalErrorCode;
import br.com.cactusdigital.erp.fiscal.mapper.FiscalMapper;
import br.com.cactusdigital.erp.fiscal.repository.InutilizacaoNumeracaoNfeRepository;
import br.com.cactusdigital.erp.fiscal.repository.NotaFiscalEletronicaRepository;
import br.com.cactusdigital.erp.fiscal.sefaz.EventosNfe;
import br.com.cactusdigital.erp.fiscal.sefaz.FalhaComunicacaoSefazException;
import br.com.cactusdigital.erp.fiscal.sefaz.RetornoEventoSefaz;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/** Regras locais da inutilização (o que é recusado antes da rede) e o que acontece com cada retorno da SEFAZ. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InutilizacaoNumeracaoNfeServiceTest {

    private static final long EMPRESA = 1L;
    /** Série 1, próximo número livre 101: os números 1..100 já foram reservados. */
    private static final EmpresaConfiguracaoFiscalDTO CONFIGURACAO =
            new EmpresaConfiguracaoFiscalDTO(EMPRESA, "A1", "matriz", "HOMOLOGACAO", "1", 101L, null, null, null, null, null, null);
    private static final String JUSTIFICATIVA = "Numeros pulados por falha na composicao da NF-e";

    @Mock private InutilizacaoNumeracaoNfeRepository repository;
    @Mock private NotaFiscalEletronicaRepository notaRepository;
    @Mock private CadastroClient cadastroClient;
    @Mock private EventosNfe eventosNfe;
    @Mock private FiscalMapper mapper;
    @InjectMocks private InutilizacaoNumeracaoNfeService service;

    @BeforeEach
    void cadastro() {
        when(cadastroClient.buscarConfiguracaoFiscal(EMPRESA)).thenReturn(CONFIGURACAO);
        when(cadastroClient.buscarEmpresa(EMPRESA)).thenReturn(emitenteEm("MG"));
        when(repository.save(any())).thenAnswer(invocacao -> invocacao.getArgument(0));
    }

    @Test
    void faixaInvertidaERecusadaSemTocarASefaz() {
        assertThatThrownBy(() -> service.inutilizar(pedido(10, 5)))
                .isInstanceOfSatisfying(BusinessException.class, ex -> assertThat(ex.getErrorCode()).isEqualTo(FiscalErrorCode.INUTILIZACAO_FAIXA_INVALIDA));
        verifyNoInteractions(eventosNfe);
    }

    @Test
    void numeroAindaNaoReservadoERecusado() {
        assertThatThrownBy(() -> service.inutilizar(pedido(95, 101)))
                .isInstanceOfSatisfying(BusinessException.class, ex -> assertThat(ex.getMessage()).contains("próximo número da série 1 é 101"));
        verifyNoInteractions(eventosNfe);
    }

    @Test
    void faixaComNotaDoErpERecusada() {
        when(notaRepository.existsByEmpresaIdAndSerieAndNumeroBetween(EMPRESA, "1", 10L, 12L)).thenReturn(true);

        assertThatThrownBy(() -> service.inutilizar(pedido(10, 12)))
                .isInstanceOfSatisfying(ConflictException.class, ex -> assertThat(ex.getErrorCode()).isEqualTo(FiscalErrorCode.INUTILIZACAO_FAIXA_COM_NOTA));
        verifyNoInteractions(eventosNfe);
    }

    @Test
    void faixaJaInutilizadaERecusada() {
        when(repository.existeHomologadaSobreposta(EMPRESA, "1", 10L, 12L)).thenReturn(true);

        assertThatThrownBy(() -> service.inutilizar(pedido(10, 12)))
                .isInstanceOfSatisfying(ConflictException.class, ex -> assertThat(ex.getErrorCode()).isEqualTo(FiscalErrorCode.INUTILIZACAO_FAIXA_JA_INUTILIZADA));
        verifyNoInteractions(eventosNfe);
    }

    @Test
    void homologadaRegistraERegularizaOsNumerosPulados() {
        when(eventosNfe.inutilizar(eq(AmbienteFiscal.HOMOLOGACAO), any(), eq(CONFIGURACAO), eq("1"), eq(10L), eq(12L), anyInt(), eq(JUSTIFICATIVA)))
                .thenReturn(new RetornoEventoSefaz(true, "102", "Inutilizacao de numero homologado", "135260000000001", Instant.now(), "<retInutNFe/>"));

        service.inutilizar(pedido(10, 12));

        InutilizacaoNumeracaoNfe salva = salva();
        assertThat(salva.getSituacao()).isEqualTo(SituacaoInutilizacao.HOMOLOGADA);
        assertThat(salva.getProtocolo()).isEqualTo("135260000000001");
        assertThat(salva.getXmlRetorno()).isEqualTo("<retInutNFe/>");
        verify(cadastroClient).regularizarNumerosNfePulados(EMPRESA, "1", 10L, 12L, "135260000000001");
    }

    @Test
    void falhaAoRegularizarNoCadastroNaoDesfazAInutilizacao() {
        when(eventosNfe.inutilizar(any(), any(), any(), anyString(), anyLong(), anyLong(), anyInt(), anyString()))
                .thenReturn(new RetornoEventoSefaz(true, "102", "ok", "135260000000002", Instant.now(), "<retInutNFe/>"));
        doThrow(new BusinessException(FiscalErrorCode.FALHA_INTEGRACAO_CADASTRO))
                .when(cadastroClient).regularizarNumerosNfePulados(anyLong(), anyString(), anyLong(), anyLong(), anyString());

        service.inutilizar(pedido(10, 12));

        assertThat(salva().getSituacao()).isEqualTo(SituacaoInutilizacao.HOMOLOGADA);
    }

    @Test
    void rejeitadaFicaRegistradaSemRegularizar() {
        when(eventosNfe.inutilizar(any(), any(), any(), anyString(), anyLong(), anyLong(), anyInt(), anyString()))
                .thenReturn(new RetornoEventoSefaz(false, "241", "Um numero da faixa ja foi utilizado", null, Instant.now(), "<retInutNFe/>"));

        service.inutilizar(pedido(10, 12));

        InutilizacaoNumeracaoNfe salva = salva();
        assertThat(salva.getSituacao()).isEqualTo(SituacaoInutilizacao.REJEITADA);
        assertThat(salva.getMensagemSefaz()).isEqualTo("241 - Um numero da faixa ja foi utilizado");
        assertThat(salva.getDataHomologacao()).isNull();
        verify(cadastroClient, never()).regularizarNumerosNfePulados(anyLong(), anyString(), anyLong(), anyLong(), anyString());
    }

    @Test
    void falhaDeComunicacaoNaoRegistraNada() {
        when(eventosNfe.inutilizar(any(), any(), any(), anyString(), anyLong(), anyLong(), anyInt(), anyString()))
                .thenThrow(new FalhaComunicacaoSefazException(null, new IOException("timeout")));

        assertThatThrownBy(() -> service.inutilizar(pedido(10, 12)))
                .isInstanceOfSatisfying(BusinessException.class, ex -> assertThat(ex.getErrorCode()).isEqualTo(FiscalErrorCode.FALHA_COMUNICACAO_SEFAZ));
        verify(repository, never()).save(any());
    }

    @Test
    void emSaoPauloOPedidoEDispensadoENadaVaiASefaz() {
        when(cadastroClient.buscarEmpresa(EMPRESA)).thenReturn(emitenteEm("SP"));

        service.inutilizar(pedido(10, 12));

        InutilizacaoNumeracaoNfe salva = salva();
        assertThat(salva.getSituacao()).isEqualTo(SituacaoInutilizacao.DISPENSADA);
        assertThat(salva.getProtocolo()).isEqualTo(InutilizacaoNumeracaoNfeService.REFERENCIA_DISPENSA);
        verifyNoInteractions(eventosNfe);
        verify(cadastroClient).regularizarNumerosNfePulados(EMPRESA, "1", 10L, 12L, InutilizacaoNumeracaoNfeService.REFERENCIA_DISPENSA);
    }

    @Test
    void dispensaNaoDispensaAsValidacoesLocais() {
        when(cadastroClient.buscarEmpresa(EMPRESA)).thenReturn(emitenteEm("SP"));
        when(notaRepository.existsByEmpresaIdAndSerieAndNumeroBetween(EMPRESA, "1", 10L, 12L)).thenReturn(true);

        assertThatThrownBy(() -> service.inutilizar(pedido(10, 12))).isInstanceOf(ConflictException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void dispensaSoValeParaAcBaSpAPartirDe01DeNovembroDe2026() {
        LocalDate vigencia = LocalDate.of(2026, 11, 1);
        assertThat(InutilizacaoNumeracaoNfeService.dispensaInutilizacao("SP", vigencia)).isTrue();
        assertThat(InutilizacaoNumeracaoNfeService.dispensaInutilizacao("ba", vigencia)).isTrue();
        assertThat(InutilizacaoNumeracaoNfeService.dispensaInutilizacao("AC", vigencia.plusYears(1))).isTrue();
        assertThat(InutilizacaoNumeracaoNfeService.dispensaInutilizacao("SP", vigencia.minusDays(1))).isFalse();
        assertThat(InutilizacaoNumeracaoNfeService.dispensaInutilizacao("MG", vigencia)).isFalse();
    }

    private static EmpresaFiscalDTO emitenteEm(String uf) {
        return new EmpresaFiscalDTO(EMPRESA, "Cactus", "Cactus", "11222333000181", "111", "3", null, "Rua", "1", null, "Centro",
                "30110000", "Cidade", uf, 3106200, 31);
    }

    private InutilizacaoNumeracaoNfe salva() {
        ArgumentCaptor<InutilizacaoNumeracaoNfe> captor = ArgumentCaptor.forClass(InutilizacaoNumeracaoNfe.class);
        verify(repository).save(captor.capture());
        return captor.getValue();
    }

    private static InutilizarNumeracaoNfeRequest pedido(long inicial, long numeroFinal) {
        return new InutilizarNumeracaoNfeRequest(EMPRESA, null, inicial, numeroFinal, JUSTIFICATIVA);
    }
}
