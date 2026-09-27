package br.com.cactusdigital.erp.contabilidade.service;

import br.com.cactusdigital.erp.contabilidade.dto.BalanceteItemResponse;
import br.com.cactusdigital.erp.contabilidade.dto.RazaoItemResponse;
import br.com.cactusdigital.erp.contabilidade.entity.NaturezaConta;
import br.com.cactusdigital.erp.contabilidade.entity.PartidaContabil;
import br.com.cactusdigital.erp.contabilidade.entity.PlanoConta;
import br.com.cactusdigital.erp.contabilidade.repository.PartidaContabilRepository;
import br.com.cactusdigital.erp.contabilidade.repository.PlanoContaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Balancete e Razão — relatórios calculados sob demanda a partir de {@link PartidaContabil},
 * sem snapshot persistido (mesmo critério já usado em {@code CurvaAbcService}/
 * {@code FluxoCaixaService} — persistir fica pra quando o volume justificar). Só entram
 * contas com movimento no período, mesmo critério do registro {@code I155} da ECD
 * ("só apresentado para contas analíticas que tiveram movimento").
 */
@Service
@RequiredArgsConstructor
public class RelatorioContabilService {

    private final PartidaContabilRepository partidaContabilRepository;
    private final PlanoContaRepository planoContaRepository;
    private final PlanoContaService planoContaService;

    @Transactional(readOnly = true)
    public List<BalanceteItemResponse> balancete(Long empresaId, LocalDate dataInicio, LocalDate dataFim) {
        Map<Long, PlanoConta> contasPorId = planoContaRepository.findByEmpresaIdOrderByCodigoAsc(empresaId).stream()
                .collect(Collectors.toMap(PlanoConta::getId, Function.identity()));

        return partidaContabilRepository.somarDebitoCredito(empresaId, dataInicio, dataFim).stream()
                .map(linha -> montarItemBalancete(contasPorId, linha))
                .sorted(Comparator.comparing(BalanceteItemResponse::contaCodigo))
                .toList();
    }

    /** Saldo corrido é inerentemente sequencial (cada linha depende da anterior) — loop simples é mais claro aqui que forçar um `reduce` com estado externo. */
    @Transactional(readOnly = true)
    public List<RazaoItemResponse> razao(Long contaId, LocalDate dataInicio, LocalDate dataFim) {
        PlanoConta conta = planoContaService.buscarEntidade(contaId);
        List<PartidaContabil> partidas = partidaContabilRepository.buscarPorContaEPeriodo(contaId, dataInicio, dataFim);

        BigDecimal saldoAnterior = saldoAntesDe(conta, dataInicio);
        BigDecimal saldoAcumulado = saldoAnterior;
        List<RazaoItemResponse> itens = new ArrayList<>(partidas.size());
        for (PartidaContabil partida : partidas) {
            saldoAcumulado = saldoAcumulado.add(efeitoNoSaldo(conta.getNatureza(), partida));
            itens.add(new RazaoItemResponse(
                    partida.getLancamentoContabil().getId(),
                    partida.getLancamentoContabil().getDataLancamento(),
                    partida.getLancamentoContabil().getHistorico(),
                    partida.getTipoPartida(),
                    partida.getValor(),
                    saldoAcumulado));
        }
        return itens;
    }

    private BigDecimal saldoAntesDe(PlanoConta conta, LocalDate dataLimite) {
        return partidaContabilRepository.somarDebitoCreditoAntesDe(conta.getEmpresaId(), dataLimite).stream()
                .filter(linha -> conta.getId().equals(linha[0]))
                .findFirst()
                .map(linha -> calcularSaldo(conta.getNatureza(), (BigDecimal) linha[1], (BigDecimal) linha[2]))
                .orElse(BigDecimal.ZERO);
    }

    private static BalanceteItemResponse montarItemBalancete(Map<Long, PlanoConta> contasPorId, Object[] linha) {
        Long contaId = (Long) linha[0];
        BigDecimal totalDebito = (BigDecimal) linha[1];
        BigDecimal totalCredito = (BigDecimal) linha[2];
        PlanoConta conta = contasPorId.get(contaId);
        BigDecimal saldo = calcularSaldo(conta.getNatureza(), totalDebito, totalCredito);
        return new BalanceteItemResponse(conta.getId(), conta.getCodigo(), conta.getNome(), conta.getTipo(), conta.getNatureza(), totalDebito, totalCredito, saldo);
    }

    /** Convenção contábil universal (partida dobrada), não decisão de projeto — ver Javadoc de {@link NaturezaConta}. */
    private static BigDecimal calcularSaldo(NaturezaConta natureza, BigDecimal totalDebito, BigDecimal totalCredito) {
        return switch (natureza) {
            case DEVEDORA -> totalDebito.subtract(totalCredito);
            case CREDORA -> totalCredito.subtract(totalDebito);
        };
    }

    private static BigDecimal efeitoNoSaldo(NaturezaConta natureza, PartidaContabil partida) {
        boolean aumentaSaldo = switch (partida.getTipoPartida()) {
            case DEBITO -> natureza == NaturezaConta.DEVEDORA;
            case CREDITO -> natureza == NaturezaConta.CREDORA;
        };
        return aumentaSaldo ? partida.getValor() : partida.getValor().negate();
    }
}
