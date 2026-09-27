package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.PdvConferenciaCaixa;
import br.com.cactuserp.pdv.entity.PdvConferenciaCaixaDetalhe;
import br.com.cactuserp.pdv.entity.PdvMovimento;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.PdvTotalTipoPagamento;
import br.com.cactuserp.pdv.entity.StatusVenda;
import br.com.cactuserp.pdv.entity.TipoPagamento;
import br.com.cactuserp.pdv.repository.PdvConferenciaCaixaRepository;
import br.com.cactuserp.pdv.repository.PdvTotalTipoPagamentoRepository;
import br.com.cactuserp.pdv.repository.PdvVendaCabecalhoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço da Conferência de Caixa (F12 → Conferência).
 *
 * <p>Calcula o valor ESPERADO em caixa por forma de pagamento (baseado
 * em movimento + vendas finalizadas), e grava o resultado da
 * conferência feita pelo operador.
 *
 * <h3>Cálculo do esperado</h3>
 * <ul>
 *   <li><b>Dinheiro:</b> fundo inicial + suprimentos + vendas em dinheiro - sangrias</li>
 *   <li><b>Outras formas:</b> soma de vendas finalizadas com aquela forma de pagamento</li>
 * </ul>
 */
@Service
public class ConferenciaCaixaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConferenciaCaixaService.class);
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");
    /**
     * CODIGO da forma DINHEIRO em PDV_TIPO_PAGAMENTO. Compartilhado com
     * FechamentoMovimentoService.
     *
     * <p>⚠️ Corrigido em 2026-08-22: era {@code "01"}, mas a coluna é
     * {@code CHAR(3)} e o valor real semeado no banco é {@code "001"} — a
     * comparação nunca batia, então os ajustes de dinheiro (fundo inicial/
     * suprimento/sangria/troco) nunca eram aplicados nem na Conferência de
     * Caixa nem no Relatório Z (que passou a reaproveitar este cálculo).
     */
    public static final String CODIGO_DINHEIRO = "001";

    private final PdvConferenciaCaixaRepository conferenciaRepository;
    private final PdvTotalTipoPagamentoRepository totalPagamentoRepository;
    private final PdvVendaCabecalhoRepository vendaRepository;

    @PersistenceContext
    private EntityManager em;

    public ConferenciaCaixaService(PdvConferenciaCaixaRepository conferenciaRepository,
                                   PdvTotalTipoPagamentoRepository totalPagamentoRepository,
                                   PdvVendaCabecalhoRepository vendaRepository) {
        this.conferenciaRepository = conferenciaRepository;
        this.totalPagamentoRepository = totalPagamentoRepository;
        this.vendaRepository = vendaRepository;
    }

    /**
     * Calcula o esperado em caixa por forma de pagamento, baseado no
     * movimento ativo. Retorna LinkedHashMap (preserva ordem) onde
     * a chave é a forma de pagamento e o valor é o esperado.
     *
     * <p>Dinheiro inclui fundo inicial + suprimentos - sangrias além
     * das vendas em dinheiro.
     */
    @Transactional(readOnly = true)
    public Map<TipoPagamento, BigDecimal> calcularEsperadoPorForma(PdvMovimento movimento) {
        if (movimento == null) {
            throw new IllegalArgumentException("Movimento não pode ser nulo");
        }

        // 1) Pega vendas finalizadas do movimento + agrupa pagamentos por forma
        Map<TipoPagamento, BigDecimal> mapa = agruparPagamentosPorForma(movimento);

        // 2) Ajusta o DINHEIRO: + fundo inicial + suprimentos - sangrias - troco
        //    (troco é dinheiro que SAI do caixa quando cliente paga mais)
        TipoPagamento dinheiro = buscarFormaDinheiro();
        if (dinheiro != null) {
            BigDecimal valorDinheiro = mapa.getOrDefault(dinheiro, BigDecimal.ZERO);

            // + Fundo inicial
            if (movimento.getValorFundoInicial() != null) {
                valorDinheiro = valorDinheiro.add(movimento.getValorFundoInicial());
            }
            // + Suprimentos
            if (movimento.getTotalSuprimento() != null) {
                valorDinheiro = valorDinheiro.add(movimento.getTotalSuprimento());
            }
            // - Sangrias
            if (movimento.getTotalSangria() != null) {
                valorDinheiro = valorDinheiro.subtract(movimento.getTotalSangria());
            }
            // - Trocos pagos
            BigDecimal totalTroco = somarTrocoMovimento(movimento);
            if (totalTroco != null) {
                valorDinheiro = valorDinheiro.subtract(totalTroco);
            }

            mapa.put(dinheiro, valorDinheiro.setScale(2, RoundingMode.HALF_UP));
        }

        // Arredonda todos
        Map<TipoPagamento, BigDecimal> resultado = new LinkedHashMap<>();
        mapa.forEach((forma, valor) ->
                resultado.put(forma, valor.setScale(2, RoundingMode.HALF_UP)));

        return resultado;
    }

    /**
     * Soma bruta dos pagamentos finalizados do movimento, agrupados por
     * forma de pagamento — SEM os ajustes de dinheiro (fundo/suprimento/
     * sangria/troco) que {@link #calcularEsperadoPorForma} aplica.
     *
     * <p>Extraído como método próprio pra ser reaproveitado pelo Relatório Z
     * ({@code FechamentoMovimentoService}), que precisa do valor bruto de
     * vendas em dinheiro como métrica de exibição separada do saldo
     * esperado em caixa.
     */
    @Transactional(readOnly = true)
    public Map<TipoPagamento, BigDecimal> calcularVendasPorForma(PdvMovimento movimento) {
        if (movimento == null) {
            throw new IllegalArgumentException("Movimento não pode ser nulo");
        }
        return agruparPagamentosPorForma(movimento);
    }

    /**
     * Saldo esperado em dinheiro no caixa: fundo inicial + suprimentos +
     * vendas em dinheiro - sangrias - trocos pagos. É o MESMO cálculo usado
     * na Conferência de Caixa — o Relatório Z reaproveita este método pra
     * garantir que os dois números nunca divirjam entre si.
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularSaldoEsperadoDinheiro(PdvMovimento movimento) {
        Map<TipoPagamento, BigDecimal> esperados = calcularEsperadoPorForma(movimento);
        return esperados.entrySet().stream()
                .filter(e -> CODIGO_DINHEIRO.equals(e.getKey().getCodigo()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    private Map<TipoPagamento, BigDecimal> agruparPagamentosPorForma(PdvMovimento movimento) {
        List<PdvVendaCabecalho> vendas = vendaRepository
                .findByPdvMovimentoAndStatusVendaOrderByIdAsc(movimento, StatusVenda.F);

        Map<TipoPagamento, BigDecimal> mapa = new LinkedHashMap<>();
        for (PdvVendaCabecalho v : vendas) {
            List<PdvTotalTipoPagamento> pagamentos =
                    totalPagamentoRepository.findByIdPdvVendaCabecalho(v.getId());
            for (PdvTotalTipoPagamento p : pagamentos) {
                TipoPagamento forma = p.getPdvTipoPagamento();
                if (forma == null) continue;
                BigDecimal valor = p.getValor() != null ? p.getValor() : BigDecimal.ZERO;
                mapa.merge(forma, valor, BigDecimal::add);
            }
        }
        return mapa;
    }

    private BigDecimal somarTrocoMovimento(PdvMovimento movimento) {
        TypedQuery<BigDecimal> q = em.createQuery(
                "SELECT COALESCE(SUM(v.valorTroco), 0) FROM PdvVendaCabecalho v "
                        + "WHERE v.pdvMovimento = :mov "
                        + "  AND v.statusVenda = br.com.cactuserp.pdv.entity.StatusVenda.F",
                BigDecimal.class);
        q.setParameter("mov", movimento);
        BigDecimal r = q.getSingleResult();
        return r != null ? r : BigDecimal.ZERO;
    }

    private TipoPagamento buscarFormaDinheiro() {
        TypedQuery<TipoPagamento> q = em.createQuery(
                "SELECT t FROM TipoPagamento t WHERE t.codigo = :cod",
                TipoPagamento.class);
        q.setParameter("cod", CODIGO_DINHEIRO);
        List<TipoPagamento> res = q.getResultList();
        return res.isEmpty() ? null : res.get(0);
    }

    /**
     * Grava a conferência feita pelo operador.
     *
     * @param movimento     movimento ativo
     * @param operador      operador que realizou a conferência
     * @param gerente       gerente que autorizou
     * @param valoresContados mapa forma → valor contado pelo operador
     * @param observacao     observação opcional
     * @return cabeçalho persistido
     */
    @Transactional
    public PdvConferenciaCaixa salvar(PdvMovimento movimento,
                                      PdvOperador operador,
                                      PdvOperador gerente,
                                      Map<TipoPagamento, BigDecimal> valoresContados,
                                      String observacao) {

        Map<TipoPagamento, BigDecimal> esperados = calcularEsperadoPorForma(movimento);

        PdvConferenciaCaixa cab = new PdvConferenciaCaixa();
        cab.setPdvMovimento(movimento);
        cab.setOperador(operador);
        cab.setGerenteAutorizador(gerente);
        cab.setDataConferencia(LocalDate.now());
        cab.setHoraConferencia(LocalTime.now().format(FMT_HORA));
        cab.setObservacao(observacao);

        BigDecimal totalEsperado = BigDecimal.ZERO;
        BigDecimal totalContado = BigDecimal.ZERO;

        // União das formas: tudo que aparece em esperados OU contados
        List<TipoPagamento> formas = new ArrayList<>(esperados.keySet());
        for (TipoPagamento f : valoresContados.keySet()) {
            if (!formas.contains(f)) formas.add(f);
        }

        for (TipoPagamento forma : formas) {
            BigDecimal esp = esperados.getOrDefault(forma, BigDecimal.ZERO);
            BigDecimal con = valoresContados.getOrDefault(forma, BigDecimal.ZERO);
            BigDecimal div = con.subtract(esp).setScale(2, RoundingMode.HALF_UP);

            PdvConferenciaCaixaDetalhe det = new PdvConferenciaCaixaDetalhe();
            det.setTipoPagamento(forma);
            det.setValorEsperado(esp);
            det.setValorContado(con);
            det.setValorDivergencia(div);
            cab.adicionarDetalhe(det);

            totalEsperado = totalEsperado.add(esp);
            totalContado = totalContado.add(con);
        }

        cab.setValorTotalEsperado(totalEsperado.setScale(2, RoundingMode.HALF_UP));
        cab.setValorTotalContado(totalContado.setScale(2, RoundingMode.HALF_UP));
        cab.setValorDivergencia(totalContado.subtract(totalEsperado)
                .setScale(2, RoundingMode.HALF_UP));

        PdvConferenciaCaixa salva = conferenciaRepository.save(cab);
        LOGGER.info("Conferência de caixa #{} salva: esperado={}, contado={}, divergência={}",
                salva.getId(), salva.getValorTotalEsperado(),
                salva.getValorTotalContado(), salva.getValorDivergencia());
        return salva;
    }
}