package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.Produto;
import br.com.cactuserp.pdv.entity.ProdutoFaixaPreco;
import br.com.cactuserp.pdv.repository.ProdutoFaixaPrecoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Serviço de cálculo de preço por faixa de quantidade (modelo atacarejo).
 *
 * <h3>Regra de negócio</h3>
 * Dado um produto com:
 * <ul>
 *   <li>Preço de varejo em {@code PRODUTO.VALOR_VENDA}</li>
 *   <li>Faixas adicionais em {@code PRODUTO_FAIXA_PRECO} (qtd mínima + preço)</li>
 * </ul>
 *
 * <p>Pra uma quantidade {@code N}, o preço aplicado é:
 * <ol>
 *   <li>Pega o preço base do produto ({@code VALOR_VENDA})</li>
 *   <li>Itera pelas faixas vigentes ordenadas por qtd mínima crescente</li>
 *   <li>Para cada faixa cuja {@code QUANTIDADE_MINIMA <= N}, substitui o preço</li>
 *   <li>Retorna o último preço encontrado (ou o varejo se nenhuma faixa coube)</li>
 * </ol>
 *
 * <p>Exemplo: produto com varejo R$ 24,90 e faixas [5+ R$ 23,90, 10+ R$ 21,90]
 * <ul>
 *   <li>qtd 3 → R$ 24,90 (varejo)</li>
 *   <li>qtd 5 → R$ 23,90 (faixa 5+)</li>
 *   <li>qtd 7 → R$ 23,90 (faixa 5+)</li>
 *   <li>qtd 10 → R$ 21,90 (faixa 10+)</li>
 *   <li>qtd 100 → R$ 21,90 (faixa 10+)</li>
 * </ul>
 *
 * <h3>Recálculo</h3>
 * Quando o operador altera a quantidade de um item já bipado, o PDV
 * chama {@link #calcular(Produto, BigDecimal)} de novo e re-aplica o
 * preço retornado. Isso garante consistência fiscal: cancelar 3 unid
 * de 12 (caindo pra 9) DEVE voltar pro preço de varejo.
 */
@Service
public class FaixaPrecoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FaixaPrecoService.class);

    private final ProdutoFaixaPrecoRepository repository;

    public FaixaPrecoService(ProdutoFaixaPrecoRepository repository) {
        this.repository = repository;
    }

    /**
     * Calcula o preço unitário aplicável a uma quantidade do produto.
     *
     * @param produto    o produto bipado
     * @param quantidade quantidade comprada
     * @return resultado com preço unitário + faixa aplicada (se houver)
     */
    public ResultadoFaixa calcular(Produto produto, BigDecimal quantidade) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo");
        }
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            // Quantidade inválida: retorna preço de varejo sem faixa
            return new ResultadoFaixa(produto.getValorVenda(), null);
        }

        BigDecimal precoBase = produto.getValorVenda();
        if (precoBase == null) precoBase = BigDecimal.ZERO;

        List<ProdutoFaixaPreco> faixas =
                repository.findVigentes(produto.getId(), LocalDate.now());

        if (faixas == null || faixas.isEmpty()) {
            return new ResultadoFaixa(precoBase, null);
        }

        // As faixas já vêm ordenadas por quantidadeMinima ASC.
        // Iteramos pegando sempre a MAIOR cuja qtd mínima cabe.
        BigDecimal precoAplicado = precoBase;
        ProdutoFaixaPreco faixaAplicada = null;

        for (ProdutoFaixaPreco faixa : faixas) {
            if (quantidade.compareTo(faixa.getQuantidadeMinima()) >= 0) {
                precoAplicado = faixa.getValorUnitario();
                faixaAplicada = faixa;
            } else {
                // Como está ordenado crescente, qualquer faixa seguinte
                // também não vai caber. Encerra cedo.
                break;
            }
        }

        return new ResultadoFaixa(precoAplicado, faixaAplicada);
    }

    /**
     * Verifica se um produto tem alguma faixa vigente cadastrada.
     * Útil pra evitar consulta de faixas em produtos sem atacado.
     */
    public boolean produtoTemFaixas(Long idProduto) {
        if (idProduto == null) return false;
        return !repository.findVigentes(idProduto, LocalDate.now()).isEmpty();
    }

    // ====================================================================
    //  Result type
    // ====================================================================

    /**
     * Resultado do cálculo de faixa: preço unitário a aplicar e qual
     * faixa foi escolhida (null = preço de varejo, nenhuma faixa).
     */
    public static class ResultadoFaixa {
        private final BigDecimal precoUnitario;
        private final ProdutoFaixaPreco faixaAplicada;

        public ResultadoFaixa(BigDecimal precoUnitario, ProdutoFaixaPreco faixaAplicada) {
            this.precoUnitario = precoUnitario;
            this.faixaAplicada = faixaAplicada;
        }

        public BigDecimal getPrecoUnitario() { return precoUnitario; }
        public ProdutoFaixaPreco getFaixaAplicada() { return faixaAplicada; }

        /** True se foi aplicada uma faixa de atacado (não é preço de varejo). */
        public boolean temFaixaAplicada() { return faixaAplicada != null; }

        /** Rótulo da faixa pra mostrar no PDV ("ATACADO", "CX FECHADA"). null se for varejo. */
        public String getRotulo() {
            return faixaAplicada != null ? faixaAplicada.getRotulo() : null;
        }

        /** Quantidade mínima da faixa aplicada. null se for varejo. */
        public BigDecimal getQuantidadeMinima() {
            return faixaAplicada != null ? faixaAplicada.getQuantidadeMinima() : null;
        }
    }

    /**
     * Lista as faixas vigentes de um produto na data atual.
     * <p>Útil pra telas de consulta que precisam exibir as faixas.
     * Retorna lista vazia se o produto não tem faixas ou se nenhuma está vigente.
     */
    public java.util.List<br.com.cactuserp.pdv.entity.ProdutoFaixaPreco>
    listarFaixasVigentes(Long idProduto) {
        if (idProduto == null) return java.util.Collections.emptyList();
        return repository.findVigentes(idProduto, java.time.LocalDate.now());
    }
}