package br.com.cactusdigital.erp.estoque.service;

import br.com.cactusdigital.erp.estoque.dto.CurvaAbcItemResponse;
import br.com.cactusdigital.erp.estoque.entity.EstoqueMovimento;
import br.com.cactusdigital.erp.estoque.entity.TipoMovimentoEstoque;
import br.com.cactusdigital.erp.estoque.repository.EstoqueMovimentoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classificação de relevância/giro (padrão A 80% / B 15% / C 5% acumulado, por valor
 * movimentado) — calculada sob demanda a partir do ledger de {@code SAIDA}, não é uma
 * tabela persistida na v1. Cada {@link EstoqueMovimento} já carrega
 * {@code custoUnitario} efetivo (o informado na ENTRADA, ou o custo médio ponderado
 * vigente nos demais tipos — ver {@code EstoqueMovimentoService#registrar}), então dá
 * pra valorizar toda saída sem precisar de outra fonte.
 */
@Service
@RequiredArgsConstructor
public class CurvaAbcService {

    private static final BigDecimal LIMITE_A = BigDecimal.valueOf(80);
    private static final BigDecimal LIMITE_B = BigDecimal.valueOf(95);

    private final EstoqueMovimentoRepository repository;

    @Transactional(readOnly = true)
    public List<CurvaAbcItemResponse> calcular(Long depositoId, int dias) {
        Instant fim = Instant.now();
        Instant inicio = fim.minus(dias, ChronoUnit.DAYS);

        List<EstoqueMovimento> saidas = repository.findByTipoMovimentoAndDataMovimentoBetween(TipoMovimentoEstoque.SAIDA, inicio, fim);

        Map<Long, BigDecimal> valorPorProduto = new LinkedHashMap<>();
        for (EstoqueMovimento movimento : saidas) {
            if (depositoId != null && !movimento.getDeposito().getId().equals(depositoId)) {
                continue;
            }
            BigDecimal custo = movimento.getCustoUnitario() != null ? movimento.getCustoUnitario() : BigDecimal.ZERO;
            BigDecimal valor = movimento.getQuantidade().multiply(custo);
            valorPorProduto.merge(movimento.getProdutoId(), valor, BigDecimal::add);
        }

        BigDecimal total = valorPorProduto.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        List<Map.Entry<Long, BigDecimal>> ordenado = valorPorProduto.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .toList();

        List<CurvaAbcItemResponse> resultado = new ArrayList<>();
        BigDecimal acumulado = BigDecimal.ZERO;
        for (Map.Entry<Long, BigDecimal> entrada : ordenado) {
            BigDecimal percentual = total.signum() == 0
                    ? BigDecimal.ZERO
                    : entrada.getValue().multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP);
            acumulado = acumulado.add(percentual);
            String classificacao = acumulado.compareTo(LIMITE_A) <= 0 ? "A" : acumulado.compareTo(LIMITE_B) <= 0 ? "B" : "C";
            resultado.add(new CurvaAbcItemResponse(entrada.getKey(), entrada.getValue(), percentual, acumulado, classificacao));
        }
        return resultado;
    }
}
