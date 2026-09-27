package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;
import br.com.cactuserp.pdv.entity.Produto;
import br.com.cactuserp.pdv.service.dto.ResultadoTributario;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Aplica as alíquotas de ICMS/PIS/COFINS/ISSQN já cadastradas no produto
 * sobre o valor de um item de venda.
 *
 * <h3>Escopo — o que este service FAZ e o que NÃO faz</h3>
 * <p><b>Faz</b>: aritmética — {@code valor = base × alíquota / 100},
 * arredondada conforme {@code PRODUTO.IAT} (A=Arredondamento HALF_UP,
 * T=Truncamento DOWN). É a mesma responsabilidade que qualquer PDV de
 * mercado (Linx, TOTVS Varejo, Bling, Consinco) tem nessa camada: aplicar
 * alíquotas já definidas, não decidir quais são.
 *
 * <p><b>NÃO faz</b> — fica de fora deliberadamente, por exigir dados/
 * regras que este sistema não tem e que são responsabilidade de quem
 * classifica fiscalmente o produto (contador/fiscal), não do PDV:
 * <ul>
 *   <li><b>Classificação fiscal</b> (qual CST/CSOSN se aplica, se é
 *       Simples Nacional ou regime normal) — assume que
 *       {@code PRODUTO.TAXA_ICMS} etc já reflete a alíquota efetiva
 *       correta pro produto, cadastrada por quem sabe a regra.</li>
 *   <li><b>ICMS-ST</b> (substituição tributária) — precisa de MVA/pauta
 *       fiscal por NCM/UF, tabela que não existe neste schema.</li>
 *   <li><b>Redução de base de cálculo, isenções parciais, DIFAL</b>
 *       (diferencial de alíquota interestadual) — regras adicionais que
 *       mudam a base antes de aplicar a alíquota; não implementadas.</li>
 *   <li><b>VALOR_IMPOSTO_FEDERAL/ESTADUAL/MUNICIPAL</b> (carga tributária
 *       aproximada da Lei da Transparência, Lei 12.741/2012) — depende de
 *       tabela IBPT por NCM, uma fonte de dados externa que não existe
 *       neste projeto. Deliberadamente deixado de fora — melhor não
 *       preencher do que preencher com um valor inventado.</li>
 * </ul>
 *
 * <p>Se a loja precisar de ICMS-ST/DIFAL/regras avançadas, este service é
 * o ponto de extensão natural — mas requer as tabelas de apoio fiscais
 * correspondentes, que precisam ser desenhadas com um especialista antes.
 */
@Service
public class CalculoTributarioService {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private static final int ESCALA_VALOR = 2;

    /** Calcula os tributos incidentes sobre {@code valorBase} (normalmente o VALOR_TOTAL do item). */
    public ResultadoTributario calcular(Produto produto, BigDecimal valorBase) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto é obrigatório para cálculo tributário.");
        }
        BigDecimal base = valorBase != null ? valorBase : BigDecimal.ZERO;
        RoundingMode arredondamento = resolverArredondamento(produto.getIat());

        BigDecimal taxaIcms = taxaOuZero(produto.getTaxaIcms());
        BigDecimal taxaPis = taxaOuZero(produto.getTaxaPis());
        BigDecimal taxaCofins = taxaOuZero(produto.getTaxaCofins());
        BigDecimal taxaIssqn = taxaOuZero(produto.getTaxaIssqn());

        return new ResultadoTributario(
                base,
                taxaIcms, aplicarAliquota(base, taxaIcms, arredondamento),
                taxaPis, aplicarAliquota(base, taxaPis, arredondamento),
                taxaCofins, aplicarAliquota(base, taxaCofins, arredondamento),
                taxaIssqn, aplicarAliquota(base, taxaIssqn, arredondamento)
        );
    }

    /**
     * Calcula e já grava os campos de tributo no item — fonte única usada
     * tanto na inclusão/agrupamento quanto na aplicação de desconto/
     * acréscimo (ver {@code VendaService} e {@code DescontoAcrescimoService}),
     * evitando duplicar a lista de 9 campos em cada caller.
     *
     * @param valorBase normalmente o {@code VALOR_TOTAL} vigente do item
     *                  (já refletindo faixa de preço e desconto/acréscimo,
     *                  se houver, no momento da chamada)
     */
    public void aplicarEm(PdvVendaDetalhe detalhe, Produto produto, BigDecimal valorBase) {
        ResultadoTributario tributos = calcular(produto, valorBase);
        detalhe.setValorBaseIcms(tributos.getValorBaseIcms());
        detalhe.setTaxaIcms(tributos.getTaxaIcms());
        detalhe.setValorIcms(tributos.getValorIcms());
        detalhe.setTaxaPis(tributos.getTaxaPis());
        detalhe.setValorPis(tributos.getValorPis());
        detalhe.setTaxaCofins(tributos.getTaxaCofins());
        detalhe.setValorCofins(tributos.getValorCofins());
        detalhe.setTaxaIssqn(tributos.getTaxaIssqn());
        detalhe.setValorIssqn(tributos.getValorIssqn());
        detalhe.setCst(produto.getCst());
    }

    private BigDecimal aplicarAliquota(BigDecimal base, BigDecimal taxaPercentual, RoundingMode arredondamento) {
        if (base.signum() <= 0 || taxaPercentual.signum() <= 0) {
            return BigDecimal.ZERO.setScale(ESCALA_VALOR, RoundingMode.UNNECESSARY);
        }
        return base.multiply(taxaPercentual).divide(CEM, ESCALA_VALOR, arredondamento);
    }

    private RoundingMode resolverArredondamento(String iat) {
        return "T".equalsIgnoreCase(iat) ? RoundingMode.DOWN : RoundingMode.HALF_UP;
    }

    private BigDecimal taxaOuZero(BigDecimal taxa) {
        return taxa != null ? taxa : BigDecimal.ZERO;
    }
}
