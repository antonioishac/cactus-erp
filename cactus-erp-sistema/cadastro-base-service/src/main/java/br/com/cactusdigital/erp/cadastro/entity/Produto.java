package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Campos compatíveis com o que o {@code PRODUTO} do cactus-pdv espera (GTIN, código
 * interno, preços, estoque mín/máx) — é este cadastro que vai alimentar o PDV quando a
 * sincronização (Fase 6) for implementada. Fiscal: {@code codigoNcm} solto virou
 * {@link Ncm}/{@link Cest}/{@link GrupoTributario} (FK, resolvido pelo motor tributário —
 * ver ANÁLISE TRIBUTÁRIA no CLAUDE.md), com um "resolvido padrão" cacheado nos campos
 * {@code *Padrao} pra sync rápida com o PDV sem recalcular a cada consulta. Estoque
 * (quantidade) NÃO mora aqui — vive no futuro {@code controle-estoque-service}, por
 * produto × depósito (ver ANÁLISE: ESTOQUE NÍVEL ENTERPRISE).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "produto")
public class Produto extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_unidade_id", nullable = false)
    private ProdutoUnidade produtoUnidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_marca_id")
    private ProdutoMarca produtoMarca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_subgrupo_id", nullable = false)
    private ProdutoSubgrupo produtoSubgrupo;

    /** Lookup configurável pela empresa (ex: "Normal"/"Kit"/"Insumo") — distinto de Grupo/Subgrupo e de {@code tipoItemSped}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_tipo_id")
    private ProdutoTipo produtoTipo;

    @Column(name = "gtin", length = 14)
    private String gtin;

    @Column(name = "codigo_interno", nullable = false, length = 50, unique = true)
    private String codigoInterno;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "descricao", length = 250)
    private String descricao;

    @Column(name = "descricao_pdv", length = 30)
    private String descricaoPdv;

    // --- Identidade fiscal (motor tributário — Fase 1b) --------------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ncm_id", nullable = false)
    private Ncm ncm;

    /** Só preenchido quando o NCM/atividade está sujeito a substituição tributária. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cest_id")
    private Cest cest;

    /** Perfil tributário do produto — combinado com a operação da venda resolve CST/CSOSN/alíquotas. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_tributario_id", nullable = false)
    private GrupoTributario grupoTributario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item_sped", length = 30)
    private TipoItemSped tipoItemSped;

    /** IAT: arredondamento ou truncamento do valor unitário fracionado. */
    @Column(name = "iat", length = 1)
    private IndicadorArredondamentoTruncamento iat;

    /** IPPT: mercadoria própria ou de terceiro (consignação). */
    @Column(name = "ippt", length = 1)
    private IndicadorProprioTerceiro ippt;

    @Column(name = "totalizador_parcial", length = 10)
    private String totalizadorParcial;

    /** PLU de etiqueta de balança (hortifruti/açougue). */
    @Column(name = "codigo_balanca")
    private Integer codigoBalanca;

    /** Indicador PAF-NFC-e específico de produto sujeito a ST — mesmo nome/formato do campo do cactus-pdv. */
    @Column(name = "paf_p_st", length = 1)
    private String pafPSt;

    // --- Preço/custo ---------------------------------------------------------------------

    /** Custo de formação (última apuração de custo médio/FIFO) — distinto de {@code valorCompra}. */
    @Column(name = "valor_custo", precision = 18, scale = 6)
    private BigDecimal valorCusto;

    /** Preço pago na última compra — distinto de {@code valorCusto} (custo de formação, ver ANÁLISE TRIBUTÁRIA). */
    @Column(name = "valor_compra", precision = 18, scale = 6)
    private BigDecimal valorCompra;

    @Column(name = "valor_venda", precision = 18, scale = 6)
    private BigDecimal valorVenda;

    @Column(name = "estoque_minimo", precision = 18, scale = 6)
    private BigDecimal estoqueMinimo;

    @Column(name = "estoque_maximo", precision = 18, scale = 6)
    private BigDecimal estoqueMaximo;

    // --- Tributação resolvida, cacheada (sync rápida com o PDV) --------------------------

    @Enumerated(EnumType.STRING)
    @Column(name = "cst_icms_padrao", length = 10)
    private CstIcms cstIcmsPadrao;

    @Enumerated(EnumType.STRING)
    @Column(name = "csosn_padrao", length = 10)
    private Csosn csosnPadrao;

    @Column(name = "aliquota_icms_padrao", precision = 18, scale = 6)
    private BigDecimal aliquotaIcmsPadrao;

    @Column(name = "aliquota_ipi_padrao", precision = 18, scale = 6)
    private BigDecimal aliquotaIpiPadrao;

    @Column(name = "aliquota_pis_padrao", precision = 18, scale = 6)
    private BigDecimal aliquotaPisPadrao;

    @Column(name = "aliquota_cofins_padrao", precision = 18, scale = 6)
    private BigDecimal aliquotaCofinsPadrao;

    @Column(name = "aliquota_issqn_padrao", precision = 18, scale = 6)
    private BigDecimal aliquotaIssqnPadrao;

    /** Quando o "resolvido padrão" acima foi calculado pela última vez — usado pra detectar cache desatualizado. */
    @Column(name = "data_resolucao_padrao")
    private Instant dataResolucaoPadrao;

    // --- Situação e agregados filhos ------------------------------------------------------

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;

    @Builder.Default
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProdutoFaixaPreco> faixasPreco = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProdutoImagem> imagens = new ArrayList<>();
}
