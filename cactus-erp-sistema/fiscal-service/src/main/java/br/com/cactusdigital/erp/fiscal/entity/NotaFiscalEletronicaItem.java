package br.com.cactusdigital.erp.fiscal.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Item de {@link NotaFiscalEletronica} — validado contra {@code fenix.sql.NFE_DETALHE} +
 * {@code NFE_DETALHE_IMPOSTO_ICMS}/{@code _IPI}/{@code _PIS}/{@code _COFINS} (simplificado:
 * uma linha por item já com os 4 tributos achatados, em vez de 4 tabelas satélite —
 * suficiente pro cenário de venda de mercadoria comum desta fase). {@code produtoId} é
 * referência SOLTA. Tributação (CST/CSOSN/alíquotas) copiada do "resolvido padrão" já
 * cacheado no {@code Produto} (cadastro-base, Fase 1b) no momento da geração — não
 * reinvoca o motor tributário aqui.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "nota_fiscal_eletronica_item")
public class NotaFiscalEletronicaItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nota_fiscal_id", nullable = false)
    private NotaFiscalEletronica notaFiscal;

    @Column(name = "numero_item", nullable = false)
    private Integer numeroItem;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(name = "descricao", nullable = false, length = 120)
    private String descricao;

    @Column(name = "gtin", length = 20)
    private String gtin;

    @Column(name = "ncm", length = 8)
    private String ncm;

    @Column(name = "cfop", length = 4)
    private String cfop;

    @Column(name = "unidade", length = 6)
    private String unidade;

    @Column(name = "quantidade", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorUnitario;

    @Column(name = "valor_total", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTotal;

    @Column(name = "valor_desconto", precision = 18, scale = 6)
    private BigDecimal valorDesconto;

    /** 0 = Nacional — simplificação (Produto não guarda origem de mercadoria por item ainda, ver CLAUDE.md). */
    @Builder.Default
    @Column(name = "origem_mercadoria", length = 1)
    private String origemMercadoria = "0";

    @Column(name = "cst_icms", length = 3)
    private String cstIcms;

    @Column(name = "csosn", length = 3)
    private String csosn;

    @Column(name = "aliquota_icms", precision = 18, scale = 6)
    private BigDecimal aliquotaIcms;

    @Column(name = "valor_base_icms", precision = 18, scale = 6)
    private BigDecimal valorBaseIcms;

    @Column(name = "valor_icms", precision = 18, scale = 6)
    private BigDecimal valorIcms;

    @Column(name = "aliquota_ipi", precision = 18, scale = 6)
    private BigDecimal aliquotaIpi;

    @Column(name = "valor_ipi", precision = 18, scale = 6)
    private BigDecimal valorIpi;

    /** CST 01 (tributação normal) fixo — mesma simplificação do cactus-pdv.AcbrComandoFiscalBuilder. */
    @Builder.Default
    @Column(name = "cst_pis", length = 2)
    private String cstPis = "01";

    @Column(name = "aliquota_pis", precision = 18, scale = 6)
    private BigDecimal aliquotaPis;

    @Column(name = "valor_pis", precision = 18, scale = 6)
    private BigDecimal valorPis;

    @Builder.Default
    @Column(name = "cst_cofins", length = 2)
    private String cstCofins = "01";

    @Column(name = "aliquota_cofins", precision = 18, scale = 6)
    private BigDecimal aliquotaCofins;

    @Column(name = "valor_cofins", precision = 18, scale = 6)
    private BigDecimal valorCofins;

    // ====== IBS/CBS (Reforma Tributária) — preenchidos só para emitente CRT 3, ver CalculadoraIbsCbs ======
    @Column(name = "cst_ibs_cbs", length = 3)
    private String cstIbsCbs;

    @Column(name = "classificacao_ibs_cbs", length = 6)
    private String classificacaoIbsCbs;

    @Column(name = "valor_base_ibs_cbs", precision = 15, scale = 2)
    private BigDecimal valorBaseIbsCbs;

    @Column(name = "aliquota_ibs_uf", precision = 7, scale = 4)
    private BigDecimal aliquotaIbsUf;

    @Column(name = "percentual_reducao_ibs", precision = 7, scale = 4)
    private BigDecimal percentualReducaoIbs;

    @Column(name = "aliquota_efetiva_ibs_uf", precision = 7, scale = 4)
    private BigDecimal aliquotaEfetivaIbsUf;

    @Column(name = "valor_ibs_uf", precision = 15, scale = 2)
    private BigDecimal valorIbsUf;

    @Column(name = "aliquota_ibs_municipio", precision = 7, scale = 4)
    private BigDecimal aliquotaIbsMunicipio;

    @Column(name = "aliquota_efetiva_ibs_municipio", precision = 7, scale = 4)
    private BigDecimal aliquotaEfetivaIbsMunicipio;

    @Column(name = "valor_ibs_municipio", precision = 15, scale = 2)
    private BigDecimal valorIbsMunicipio;

    @Column(name = "valor_ibs", precision = 15, scale = 2)
    private BigDecimal valorIbs;

    @Column(name = "aliquota_cbs", precision = 7, scale = 4)
    private BigDecimal aliquotaCbs;

    @Column(name = "percentual_reducao_cbs", precision = 7, scale = 4)
    private BigDecimal percentualReducaoCbs;

    @Column(name = "aliquota_efetiva_cbs", precision = 7, scale = 4)
    private BigDecimal aliquotaEfetivaCbs;

    @Column(name = "valor_cbs", precision = 15, scale = 2)
    private BigDecimal valorCbs;
}
