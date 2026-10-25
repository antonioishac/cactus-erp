package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.config.util.HashCalculator;
import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;
import br.com.cactuserp.pdv.entity.SimNao;
import br.com.cactuserp.pdv.entity.StatusMovimento;
import br.com.cactuserp.pdv.entity.StatusVenda;
import br.com.cactuserp.pdv.entity.TipoMovimentoEstoque;
import br.com.cactuserp.pdv.repository.PdvVendaCabecalhoRepository;
import br.com.cactuserp.pdv.repository.PdvVendaDetalheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Cancelamento de uma venda JÁ FECHADA ({@code StatusVenda.F}) — diferente de
 * {@link VendaService#cancelarCupom}, que só aborta cupom em aberto.
 *
 * <h3>Regras</h3>
 * <ul>
 *   <li>Sempre exige autorização gerencial e justificativa de 15 a 255
 *       caracteres (a regra do evento de cancelamento da NFC-e, exigida mesmo
 *       sem documento fiscal, para a trilha de auditoria ficar uniforme).</li>
 *   <li>Só com o turno ({@code PDV_MOVIMENTO}) da venda ainda aberto: depois
 *       do fechamento a conferência já foi apurada contando esta venda, e
 *       cancelar mudaria o "esperado" de um caixa já conferido. Fora do turno,
 *       o caminho é a devolução.</li>
 *   <li><b>NFC-e autorizada</b>: cancela primeiro na SEFAZ (via ACBr), dentro
 *       do prazo ({@code cactus.pdv.fiscal.prazo-cancelamento-nfce-minutos},
 *       padrão 30, o prazo da maioria das UFs). Se a SEFAZ não homologar, nada
 *       muda no PDV. SAT/MF-e autorizado: não suportado (comando próprio, não
 *       mapeado), recusa com mensagem.</li>
 *   <li>Sem documento autorizado (recibo, ACBr não configurado ou emissão
 *       pendente): cancela só localmente, e o status fiscal pendente vira
 *       {@code C} para o reprocessamento não emitir depois.</li>
 *   <li>Estoque: reentrada ({@code D}) do que ainda não tinha sido devolvido
 *       de cada item não cancelado — a parte já devolvida já reentrou.</li>
 *   <li>Dinheiro: nada a lançar. A conferência só soma vendas {@code F}, então
 *       a venda cancelada sai do "esperado" da gaveta sozinha; o reembolso ao
 *       cliente em dinheiro sai como sangria, igual à devolução.</li>
 * </ul>
 *
 * <p>Ordem deliberada: a SEFAZ vem antes da gravação local porque é a parte
 * irreversível. Se a gravação local falhar depois da SEFAZ ter homologado, o
 * erro é logado como inconsistência para tratamento manual (a NFC-e está
 * cancelada e o cupom continua F).
 */
@Service
@Transactional(readOnly = true)
public class CancelamentoVendaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CancelamentoVendaService.class);
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String STATUS_FISCAL_EMITIDO = "E";
    private static final String STATUS_FISCAL_PENDENTE = "P";
    /** Venda cancelada antes de o documento fiscal pendente ser emitido. */
    static final String STATUS_FISCAL_CANCELADO_SEM_EMISSAO = "C";
    static final int JUSTIFICATIVA_MINIMA = 15;
    static final int JUSTIFICATIVA_MAXIMA = 255;

    private final PdvVendaCabecalhoRepository cabecalhoRepository;
    private final PdvVendaDetalheRepository detalheRepository;
    private final DevolucaoService devolucaoService;
    private final EstoqueMovimentoService estoqueMovimentoService;
    private final EmissaoFiscalService emissaoFiscalService;

    @Value("${cactus.pdv.fiscal.prazo-cancelamento-nfce-minutos:30}")
    private long prazoCancelamentoNfceMinutos;

    public CancelamentoVendaService(PdvVendaCabecalhoRepository cabecalhoRepository,
                                    PdvVendaDetalheRepository detalheRepository,
                                    DevolucaoService devolucaoService,
                                    EstoqueMovimentoService estoqueMovimentoService,
                                    EmissaoFiscalService emissaoFiscalService) {
        this.cabecalhoRepository = cabecalhoRepository;
        this.detalheRepository = detalheRepository;
        this.devolucaoService = devolucaoService;
        this.estoqueMovimentoService = estoqueMovimentoService;
        this.emissaoFiscalService = emissaoFiscalService;
    }

    /** Pré-checagem para a tela: devolve o motivo de bloqueio, ou {@code null} se pode cancelar. */
    public String motivoBloqueio(PdvVendaCabecalho cupom) {
        if (cupom == null || cupom.getStatusVenda() != StatusVenda.F) {
            return "Só vendas finalizadas podem ser canceladas por aqui.";
        }
        if (cupom.getPdvMovimento() == null || cupom.getPdvMovimento().getStatusMovimento() != StatusMovimento.A) {
            return "O turno desta venda já foi fechado. Use a devolução.";
        }
        if (STATUS_FISCAL_EMITIDO.equals(cupom.getStatusFiscal())) {
            if (!"NFC".equals(cupom.getTipoOperacao())) {
                return "Cancelamento de " + cupom.getTipoOperacao() + " não é suportado. Use a devolução.";
            }
            if (cupom.getChaveAcessoFiscal() == null) {
                return "NFC-e sem chave de acesso registrada — não é possível cancelar na SEFAZ.";
            }
            LocalDateTime emissao = dataHoraVenda(cupom);
            if (emissao != null && Duration.between(emissao, LocalDateTime.now()).toMinutes() >= prazoCancelamentoNfceMinutos) {
                return "Prazo de " + prazoCancelamentoNfceMinutos + " minutos para cancelar a NFC-e expirou. Use a devolução.";
            }
        }
        return null;
    }

    @Transactional
    public PdvVendaCabecalho cancelar(PdvVendaCabecalho cupom, String justificativa,
                                      PdvOperador operador, PdvOperador gerenteAutorizador) {
        if (cupom == null || cupom.getId() == null) {
            throw new IllegalArgumentException("Cupom é obrigatório.");
        }
        if (operador == null) {
            throw new IllegalArgumentException("Operador é obrigatório.");
        }
        if (gerenteAutorizador == null) {
            throw new IllegalArgumentException("Cancelamento de venda sempre exige autorização gerencial.");
        }
        String motivo = justificativa == null ? "" : justificativa.trim();
        if (motivo.length() < JUSTIFICATIVA_MINIMA || motivo.length() > JUSTIFICATIVA_MAXIMA) {
            throw new IllegalArgumentException("Justificativa deve ter entre " + JUSTIFICATIVA_MINIMA + " e "
                    + JUSTIFICATIVA_MAXIMA + " caracteres.");
        }

        PdvVendaCabecalho gerenciado = cabecalhoRepository.findById(cupom.getId())
                .orElseThrow(() -> new IllegalStateException("Cupom #" + cupom.getId() + " não encontrado."));
        String bloqueio = motivoBloqueio(gerenciado);
        if (bloqueio != null) {
            throw new IllegalStateException(bloqueio);
        }

        String protocolo = null;
        if (STATUS_FISCAL_EMITIDO.equals(gerenciado.getStatusFiscal())) {
            PdvCaixa caixa = gerenciado.getPdvMovimento().getCaixa();
            protocolo = emissaoFiscalService.cancelarNfce(caixa, gerenciado, motivo);
            LOGGER.info("NFC-e do cupom #{} cancelada na SEFAZ, protocolo={}", gerenciado.getId(), protocolo);
        } else if (STATUS_FISCAL_PENDENTE.equals(gerenciado.getStatusFiscal())) {
            gerenciado.setStatusFiscal(STATUS_FISCAL_CANCELADO_SEM_EMISSAO);
        }

        try {
            return cancelarLocalmente(gerenciado, motivo, operador, gerenteAutorizador, protocolo);
        } catch (RuntimeException ex) {
            if (protocolo != null) {
                LOGGER.error("INCONSISTÊNCIA: NFC-e do cupom #{} cancelada na SEFAZ (protocolo {}) mas a gravação "
                        + "local falhou — o cupom continua F e precisa de correção manual.", gerenciado.getId(), protocolo, ex);
            }
            throw ex;
        }
    }

    private PdvVendaCabecalho cancelarLocalmente(PdvVendaCabecalho cupom, String justificativa, PdvOperador operador,
                                                 PdvOperador gerente, String protocolo) {
        List<PdvVendaDetalhe> itens = detalheRepository.findByPdvVendaCabecalhoAndCanceladoOrderByItemAsc(cupom, SimNao.N);
        for (PdvVendaDetalhe item : itens) {
            BigDecimal aReentrar = devolucaoService.quantidadeDisponivelParaDevolucao(item);
            if (aReentrar.signum() > 0) {
                estoqueMovimentoService.registrarMovimento(item.getProduto(), TipoMovimentoEstoque.D, aReentrar, item, operador,
                        "Cancelamento da venda — item #" + item.getId() + " do cupom #" + cupom.getId());
            }
        }

        cupom.setStatusVenda(StatusVenda.C);
        cupom.setCupomCancelado(SimNao.S);
        cupom.setValorCancelado(cupom.getValorFinal());
        cupom.setDataCancelamento(LocalDate.now());
        cupom.setHoraCancelamento(LocalTime.now().format(FMT_HORA));
        cupom.setOperadorCancelamento(operador);
        cupom.setGerenteCancelamento(gerente);
        cupom.setJustificativaCancelamento(justificativa);
        cupom.setProtocoloCancelamento(protocolo);
        cupom.setHashRegistro(HashCalculator.hashDoCabecalho(cupom));
        PdvVendaCabecalho salvo = cabecalhoRepository.save(cupom);

        LOGGER.info("Venda finalizada cancelada: cupom={}, valor={}, autorizador={}, protocolo={}",
                salvo.getId(), salvo.getValorFinal(), gerente.getId(), protocolo);
        return salvo;
    }

    private static LocalDateTime dataHoraVenda(PdvVendaCabecalho cupom) {
        if (cupom.getDataVenda() == null) {
            return null;
        }
        LocalTime hora = cupom.getHoraVenda() != null ? LocalTime.parse(cupom.getHoraVenda()) : LocalTime.MIDNIGHT;
        return cupom.getDataVenda().atTime(hora);
    }
}
