package br.com.cactusdigital.erp.fiscal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.fiscal.client.CadastroClient;
import br.com.cactusdigital.erp.fiscal.dto.CancelarNotaFiscalRequest;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronica;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronicaXml;
import br.com.cactusdigital.erp.fiscal.entity.SituacaoNotaFiscal;
import br.com.cactusdigital.erp.fiscal.exception.FiscalErrorCode;
import br.com.cactusdigital.erp.fiscal.mapper.FiscalMapper;
import br.com.cactusdigital.erp.fiscal.repository.NotaFiscalEletronicaRepository;
import br.com.cactusdigital.erp.fiscal.repository.NotaFiscalEletronicaXmlRepository;
import br.com.cactusdigital.erp.fiscal.sefaz.EventosNfe;
import br.com.cactusdigital.erp.fiscal.sefaz.FalhaComunicacaoSefazException;
import br.com.cactusdigital.erp.fiscal.sefaz.RetornoEventoSefaz;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/** {@link NotaFiscalEletronicaService#cancelar}: só nota autorizada; rejeição e falha de rede nunca alteram a nota. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CancelamentoNotaFiscalEletronicaTest {

    private static final CancelarNotaFiscalRequest PEDIDO = new CancelarNotaFiscalRequest("Erro na digitacao da quantidade do item");

    @Mock private NotaFiscalEletronicaRepository repository;
    @Mock private NotaFiscalEletronicaXmlRepository xmlRepository;
    @Mock private CadastroClient cadastroClient;
    @Mock private EventosNfe eventosNfe;
    @Mock private FiscalMapper mapper;
    @InjectMocks private NotaFiscalEletronicaService service;

    private NotaFiscalEletronica nota;
    private NotaFiscalEletronicaXml xml;

    @BeforeEach
    void notaAutorizada() {
        nota = NotaFiscalEletronica.builder().empresaId(1L).serie("1").numero(10L).ambiente(AmbienteFiscal.HOMOLOGACAO)
                .chaveAcesso("35261011222333000181550010000000101234567890").protocoloAutorizacao("135260000000010")
                .situacao(SituacaoNotaFiscal.AUTORIZADA).build();
        nota.setId(7L);
        xml = new NotaFiscalEletronicaXml(7L, "<nfeProc/>");
        when(repository.findById(7L)).thenReturn(Optional.of(nota));
        when(xmlRepository.findById(7L)).thenReturn(Optional.of(xml));
    }

    @Test
    void notaNaoAutorizadaNaoECancelada() {
        nota.setSituacao(SituacaoNotaFiscal.REJEITADA);

        assertThatThrownBy(() -> service.cancelar(7L, PEDIDO)).isInstanceOf(ConflictException.class);
        verifyNoInteractions(eventosNfe);
    }

    @Test
    void rejeicaoDaSefazMantemANotaAutorizada() {
        when(eventosNfe.cancelar(any(), any(), any(), anyString()))
                .thenReturn(new RetornoEventoSefaz(false, "501", "Prazo de cancelamento superior ao previsto na Legislacao", null, null, null));

        assertThatThrownBy(() -> service.cancelar(7L, PEDIDO))
                .isInstanceOfSatisfying(BusinessException.class, ex -> {
                    assertThat(ex.getErrorCode()).isEqualTo(FiscalErrorCode.SEFAZ_REJEITOU_DOCUMENTO);
                    assertThat(ex.getMessage()).contains("501");
                });
        assertThat(nota.getSituacao()).isEqualTo(SituacaoNotaFiscal.AUTORIZADA);
        assertThat(xml.getXmlCancelamento()).isNull();
    }

    @Test
    void falhaDeComunicacaoVira502SemAlterarANota() {
        when(eventosNfe.cancelar(any(), any(), any(), anyString())).thenThrow(new FalhaComunicacaoSefazException(null, new IOException("timeout")));

        assertThatThrownBy(() -> service.cancelar(7L, PEDIDO))
                .isInstanceOfSatisfying(BusinessException.class, ex -> assertThat(ex.getErrorCode()).isEqualTo(FiscalErrorCode.FALHA_COMUNICACAO_SEFAZ));
        assertThat(nota.getSituacao()).isEqualTo(SituacaoNotaFiscal.AUTORIZADA);
    }

    @Test
    void homologadoCancelaEGuardaOEvento() {
        Instant registro = Instant.parse("2026-11-01T13:00:00Z");
        when(eventosNfe.cancelar(any(), any(), any(), anyString()))
                .thenReturn(new RetornoEventoSefaz(true, "135", "Evento registrado e vinculado a NF-e", "135260000000099", registro, "<procEventoNFe/>"));

        service.cancelar(7L, PEDIDO);

        assertThat(nota.getSituacao()).isEqualTo(SituacaoNotaFiscal.CANCELADA);
        assertThat(nota.getProtocoloCancelamento()).isEqualTo("135260000000099");
        assertThat(nota.getDataCancelamento()).isEqualTo(registro);
        assertThat(nota.getJustificativaCancelamento()).isEqualTo(PEDIDO.justificativa());
        assertThat(nota.getProtocoloAutorizacao()).as("protocolo de autorização é preservado").isEqualTo("135260000000010");
        assertThat(xml.getXmlProcessado()).isEqualTo("<nfeProc/>");
        assertThat(xml.getXmlCancelamento()).isEqualTo("<procEventoNFe/>");
    }
}
