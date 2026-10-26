package br.com.cactusdigital.erp.fiscal.service;

import br.com.cactusdigital.erp.fiscal.client.dto.AliquotaIbsCbsDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.ClassificacaoIbsCbsDTO;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronica;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronicaItem;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

/**
 * Cálculo do IBS/CBS do item e dos totais da NF-e, conforme a NT 2025.002-RTC v1.50:
 * <ul>
 *   <li><b>Base (UB16-10)</b>: {@code vProd − vDesc − vPIS − vCOFINS − vICMS} (os demais termos da regra —
 *       frete, seguro, outras, II, ICMS-ST/FCP/UFDest, monofásico, ISSQN, IS — não existem na NF-e de venda
 *       que o ERP emite hoje, então valem zero).</li>
 *   <li><b>Valores (UB35-10, UB54-10, UB67-10)</b>: {@code vBC × alíquota / 100}; com redução de alíquota,
 *       usa a alíquota efetiva {@code pAliqEfet = alíquota × (1 − pRedAliq/100)}, 4 casas (observação da
 *       NT). Sem diferimento nem devolução de tributo nesta fase ({@code vDif}/{@code vDevTrib} = 0).</li>
 *   <li><b>vIBS (UB54a-10)</b>: {@code vIBSUF + vIBSMun} (sem crédito presumido nesta fase).</li>
 * </ul>
 * <b>Arredondamento</b>: o INI manda os valores do item com 6 casas e o ACBr arredonda para 2 no XML; a
 * SEFAZ valida a base com os valores DO XML (tolerância de 0,01). Por isso cada termo da base é arredondado
 * a 2 casas antes de somar — exatamente o que vai aparecer na nota.
 *
 * <p>Classificação sem grupo de tributação (isenção/imunidade, {@code exigeGrupoTributacao=false}): o item
 * leva só CST e cClassTrib, sem valores. Componente puro (sem I/O): testado sem Spring.
 */
@Component
public class CalculadoraIbsCbs {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    public void calcularItem(NotaFiscalEletronicaItem item, ClassificacaoIbsCbsDTO classificacao, AliquotaIbsCbsDTO aliquotas) {
        item.setCstIbsCbs(classificacao.cst());
        item.setClassificacaoIbsCbs(classificacao.codigo());
        if (!classificacao.exigeGrupoTributacao()) {
            limparValores(item);
            return;
        }
        BigDecimal base = dois(item.getValorTotal()).subtract(dois(item.getValorDesconto())).subtract(dois(item.getValorPis()))
                .subtract(dois(item.getValorCofins())).subtract(dois(item.getValorIcms())).max(BigDecimal.ZERO);
        BigDecimal reducaoIbs = classificacao.reducaoAliquota() ? nulo(classificacao.percentualReducaoIbs()) : null;
        BigDecimal reducaoCbs = classificacao.reducaoAliquota() ? nulo(classificacao.percentualReducaoCbs()) : null;

        BigDecimal efetivaUf = efetiva(aliquotas.aliquotaIbsUf(), reducaoIbs);
        BigDecimal efetivaMun = efetiva(aliquotas.aliquotaIbsMunicipio(), reducaoIbs);
        BigDecimal efetivaCbs = efetiva(aliquotas.aliquotaCbs(), reducaoCbs);
        BigDecimal valorUf = valor(base, efetivaUf);
        BigDecimal valorMun = valor(base, efetivaMun);

        item.setValorBaseIbsCbs(base);
        item.setAliquotaIbsUf(quatro(aliquotas.aliquotaIbsUf()));
        item.setPercentualReducaoIbs(reducaoIbs != null ? quatro(reducaoIbs) : null);
        item.setAliquotaEfetivaIbsUf(efetivaUf);
        item.setValorIbsUf(valorUf);
        item.setAliquotaIbsMunicipio(quatro(aliquotas.aliquotaIbsMunicipio()));
        item.setAliquotaEfetivaIbsMunicipio(efetivaMun);
        item.setValorIbsMunicipio(valorMun);
        item.setValorIbs(valorUf.add(valorMun));
        item.setAliquotaCbs(quatro(aliquotas.aliquotaCbs()));
        item.setPercentualReducaoCbs(reducaoCbs != null ? quatro(reducaoCbs) : null);
        item.setAliquotaEfetivaCbs(efetivaCbs);
        item.setValorCbs(valor(base, efetivaCbs));
    }

    /** Totais W03 = soma dos itens (regras W34/W35...). Nota sem nenhum item classificado fica com totais nulos. */
    public void totalizar(NotaFiscalEletronica nota) {
        boolean temIbsCbs = nota.getItens().stream().anyMatch(i -> i.getCstIbsCbs() != null);
        nota.setValorBaseIbsCbs(temIbsCbs ? somar(nota, NotaFiscalEletronicaItem::getValorBaseIbsCbs) : null);
        nota.setValorIbsUf(temIbsCbs ? somar(nota, NotaFiscalEletronicaItem::getValorIbsUf) : null);
        nota.setValorIbsMunicipio(temIbsCbs ? somar(nota, NotaFiscalEletronicaItem::getValorIbsMunicipio) : null);
        nota.setValorIbs(temIbsCbs ? somar(nota, NotaFiscalEletronicaItem::getValorIbs) : null);
        nota.setValorCbs(temIbsCbs ? somar(nota, NotaFiscalEletronicaItem::getValorCbs) : null);
    }

    private static BigDecimal efetiva(BigDecimal aliquota, BigDecimal reducao) {
        if (reducao == null) {
            return quatro(aliquota);
        }
        return aliquota.multiply(BigDecimal.ONE.subtract(reducao.divide(CEM, 10, RoundingMode.HALF_UP))).setScale(4, RoundingMode.HALF_UP);
    }

    private static BigDecimal valor(BigDecimal base, BigDecimal aliquota) {
        return base.multiply(aliquota).divide(CEM, 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal somar(NotaFiscalEletronica nota, java.util.function.Function<NotaFiscalEletronicaItem, BigDecimal> campo) {
        return nota.getItens().stream().map(campo).filter(java.util.Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2);
    }

    private static void limparValores(NotaFiscalEletronicaItem item) {
        item.setValorBaseIbsCbs(null);
        item.setAliquotaIbsUf(null);
        item.setPercentualReducaoIbs(null);
        item.setAliquotaEfetivaIbsUf(null);
        item.setValorIbsUf(null);
        item.setAliquotaIbsMunicipio(null);
        item.setAliquotaEfetivaIbsMunicipio(null);
        item.setValorIbsMunicipio(null);
        item.setValorIbs(null);
        item.setAliquotaCbs(null);
        item.setPercentualReducaoCbs(null);
        item.setAliquotaEfetivaCbs(null);
        item.setValorCbs(null);
    }

    private static BigDecimal dois(BigDecimal v) {
        return v == null ? BigDecimal.ZERO.setScale(2) : v.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal quatro(BigDecimal v) {
        return v.setScale(4, RoundingMode.HALF_UP);
    }

    private static BigDecimal nulo(BigDecimal v) {
        return v == null || v.signum() == 0 ? null : v;
    }
}
