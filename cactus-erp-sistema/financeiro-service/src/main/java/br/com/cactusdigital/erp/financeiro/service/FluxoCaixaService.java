package br.com.cactusdigital.erp.financeiro.service;

import br.com.cactusdigital.erp.financeiro.dto.AgrupamentoFluxoCaixa;
import br.com.cactusdigital.erp.financeiro.dto.FluxoCaixaPeriodoResponse;
import br.com.cactusdigital.erp.financeiro.entity.ContaPagar;
import br.com.cactusdigital.erp.financeiro.entity.ContaReceber;
import br.com.cactusdigital.erp.financeiro.entity.SituacaoTitulo;
import br.com.cactusdigital.erp.financeiro.repository.ContaPagarRepository;
import br.com.cactusdigital.erp.financeiro.repository.ContaReceberRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fluxo de caixa PROJETADO (não realizado) — soma {@code valorSaldo} de
 * {@code ContaReceber}/{@code ContaPagar} ainda em aberto (ABERTA/PARCIAL) com
 * {@code dataVencimento} dentro do intervalo consultado, agrupados por dia ou por mês
 * (ver {@link AgrupamentoFluxoCaixa}). Não olha títulos já {@code PAGA}/{@code CANCELADA}
 * — isso seria fluxo de caixa REALIZADO (histórico), fora de escopo desta fase.
 * <p>
 * Sem snapshot persistido, mesmo critério já usado em
 * {@code controle-estoque-service.CurvaAbcService} — calculado sob demanda a partir dos
 * títulos reais; persistir fica pra quando o volume justificar.
 */
@Service
@RequiredArgsConstructor
public class FluxoCaixaService {

    private static final List<SituacaoTitulo> SITUACOES_EM_ABERTO = List.of(SituacaoTitulo.ABERTA, SituacaoTitulo.PARCIAL);

    private final ContaReceberRepository contaReceberRepository;
    private final ContaPagarRepository contaPagarRepository;

    @Transactional(readOnly = true)
    public List<FluxoCaixaPeriodoResponse> calcular(Long empresaId, LocalDate dataInicio, LocalDate dataFim, AgrupamentoFluxoCaixa agrupamento) {
        List<ContaReceber> aReceber = contaReceberRepository.findByEmpresaIdAndDataVencimentoBetweenAndSituacaoIn(
                empresaId, dataInicio, dataFim, SITUACOES_EM_ABERTO);
        List<ContaPagar> aPagar = contaPagarRepository.findByEmpresaIdAndDataVencimentoBetweenAndSituacaoIn(
                empresaId, dataInicio, dataFim, SITUACOES_EM_ABERTO);

        Map<LocalDate, BigDecimal> entradasPorPeriodo = new TreeMap<>();
        for (ContaReceber conta : aReceber) {
            LocalDate periodo = chavePeriodo(conta.getDataVencimento(), agrupamento);
            entradasPorPeriodo.merge(periodo, conta.getValorSaldo(), BigDecimal::add);
        }

        Map<LocalDate, BigDecimal> saidasPorPeriodo = new TreeMap<>();
        for (ContaPagar conta : aPagar) {
            LocalDate periodo = chavePeriodo(conta.getDataVencimento(), agrupamento);
            saidasPorPeriodo.merge(periodo, conta.getValorSaldo(), BigDecimal::add);
        }

        // Une as duas séries de períodos — um mês/dia pode ter só entrada, só saída, ou
        // ambos; só entram períodos com pelo menos um título (sem padding de período
        // vazio, mesma simplificação de não persistir snapshot).
        BigDecimal[] saldoAcumulado = {BigDecimal.ZERO};
        Set<LocalDate> periodos = new TreeSet<>();
        periodos.addAll(entradasPorPeriodo.keySet());
        periodos.addAll(saidasPorPeriodo.keySet());

        return periodos.stream()
                .map(periodo -> {
                    BigDecimal entradas = entradasPorPeriodo.getOrDefault(periodo, BigDecimal.ZERO);
                    BigDecimal saidas = saidasPorPeriodo.getOrDefault(periodo, BigDecimal.ZERO);
                    BigDecimal saldoPeriodo = entradas.subtract(saidas);
                    saldoAcumulado[0] = saldoAcumulado[0].add(saldoPeriodo);
                    return new FluxoCaixaPeriodoResponse(periodo, entradas, saidas, saldoPeriodo, saldoAcumulado[0]);
                })
                .toList();
    }

    private LocalDate chavePeriodo(LocalDate dataVencimento, AgrupamentoFluxoCaixa agrupamento) {
        return agrupamento == AgrupamentoFluxoCaixa.MES ? dataVencimento.withDayOfMonth(1) : dataVencimento;
    }
}
