package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.Cliente;
import br.com.cactuserp.pdv.entity.ClienteFiadoLancamento;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.TipoLancamentoFiado;
import br.com.cactuserp.pdv.repository.ClienteFiadoLancamentoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Ponto único de movimentação do fiado (crédito ao cliente).
 *
 * <p>Mesmo desenho do {@link EstoqueMovimentoService}: o saldo devedor do
 * cliente não é um campo solto — é sempre calculado a partir da soma dos
 * lançamentos ({@link ClienteFiadoLancamento}), auditável e reconstruível.
 *
 * <p>Identifica a forma de pagamento "fiado" pelo {@code CODIGO} "010"
 * (Crediário Loja) em {@code PDV_TIPO_PAGAMENTO} — é o tipo semeado no
 * banco mais próximo semanticamente de fiado/crediário hoje. Se a loja
 * cadastrar um tipo de pagamento diferente especificamente para fiado,
 * ajustar {@link #CODIGO_FIADO}.
 */
@Service
@Transactional(readOnly = true)
public class FiadoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FiadoService.class);
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    /** CODIGO de PDV_TIPO_PAGAMENTO usado como "venda fiado" (Crediário Loja). */
    public static final String CODIGO_FIADO = "010";

    private final ClienteFiadoLancamentoRepository lancamentoRepository;
    private final DispositivoInfoService dispositivoInfo;

    public FiadoService(ClienteFiadoLancamentoRepository lancamentoRepository,
                        DispositivoInfoService dispositivoInfo) {
        this.lancamentoRepository = lancamentoRepository;
        this.dispositivoInfo = dispositivoInfo;
    }

    /** Saldo devedor atual do cliente (soma de compras C menos pagamentos P). */
    public BigDecimal saldoDevedor(Cliente cliente) {
        if (cliente == null) return BigDecimal.ZERO;
        BigDecimal saldo = BigDecimal.ZERO;
        for (ClienteFiadoLancamento l : lancamentoRepository.findByClienteOrderByIdDesc(cliente)) {
            saldo = l.getTipoLancamento() == TipoLancamentoFiado.C
                    ? saldo.add(l.getValor())
                    : saldo.subtract(l.getValor());
        }
        return saldo;
    }

    /**
     * Verifica se uma nova compra fiado de {@code valorCompra} cabe dentro
     * do teto configurado em {@code CLIENTE.FIADO_VALOR_TETO}.
     *
     * @return true se pode comprar (ou se não há teto configurado — sem
     *         limite, fail-open igual ao padrão adotado em LicencaService)
     */
    public boolean dentroDoLimite(Cliente cliente, BigDecimal valorCompra) {
        if (cliente == null) return false;
        BigDecimal teto = cliente.getFiadoValorTeto();
        if (teto == null || teto.signum() <= 0) return true; // sem teto configurado = sem limite
        BigDecimal saldoFuturo = saldoDevedor(cliente).add(valorCompra);
        return saldoFuturo.compareTo(teto) <= 0;
    }

    /**
     * Registra uma compra fiado (aumenta o saldo devedor). Idempotente por
     * venda: se o cupom já gerou lançamento C, não duplica.
     */
    @Transactional
    public ClienteFiadoLancamento registrarCompra(Cliente cliente, BigDecimal valor,
                                                   PdvVendaCabecalho venda, PdvOperador operador) {
        if (venda != null && venda.getId() != null
                && lancamentoRepository.existsByVendaCabecalhoIdAndTipoLancamento(venda.getId(), TipoLancamentoFiado.C)) {
            LOGGER.warn("Lançamento de compra fiado já registrado para o cupom #{} — ignorando.", venda.getId());
            return null;
        }
        return registrar(cliente, TipoLancamentoFiado.C, valor, venda, operador,
                venda != null ? "Venda fiado — cupom #" + venda.getId() : "Compra fiado");
    }

    /** Registra um pagamento/baixa do fiado (reduz o saldo devedor). */
    @Transactional
    public ClienteFiadoLancamento registrarPagamento(Cliente cliente, BigDecimal valor,
                                                      PdvOperador operador, String observacao) {
        return registrar(cliente, TipoLancamentoFiado.P, valor, null, operador, observacao);
    }

    private ClienteFiadoLancamento registrar(Cliente cliente, TipoLancamentoFiado tipo, BigDecimal valor,
                                             PdvVendaCabecalho venda, PdvOperador operador, String observacao) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente é obrigatório para lançamento de fiado.");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero.");
        }

        BigDecimal saldoAnterior = saldoDevedor(cliente);
        BigDecimal saldoNovo = tipo == TipoLancamentoFiado.C
                ? saldoAnterior.add(valor)
                : saldoAnterior.subtract(valor);

        ClienteFiadoLancamento lanc = new ClienteFiadoLancamento();
        lanc.setCliente(cliente);
        lanc.setTipoLancamento(tipo);
        lanc.setValor(valor);
        lanc.setSaldoAnterior(saldoAnterior);
        lanc.setSaldoNovo(saldoNovo);
        lanc.setVendaCabecalho(venda);
        lanc.setOperador(operador);
        lanc.setDataLancamento(LocalDate.now());
        lanc.setHoraLancamento(LocalTime.now().format(FMT_HORA));
        lanc.setObservacao(observacao);
        lanc.setIdDispositivo(dispositivoInfo.getHostname());

        ClienteFiadoLancamento salvo = lancamentoRepository.save(lanc);
        LOGGER.info("Fiado movimentado: cliente={}, tipo={}, valor={}, saldo {} -> {}",
                cliente.getId(), tipo, valor, saldoAnterior, saldoNovo);
        return salvo;
    }
}
