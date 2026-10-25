package br.com.cactusdigital.erp.vendas.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
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

/** Item de {@link VendaPdv} ({@code PDV_VENDA_DETALHE}, última versão do item). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "venda_pdv_item")
public class VendaPdvItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_pdv_id", nullable = false)
    private VendaPdv venda;

    @Column(name = "numero_item", nullable = false)
    private Integer numeroItem;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(name = "gtin", length = 14)
    private String gtin;

    @Column(name = "quantidade", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorUnitario;

    @Builder.Default
    @Column(name = "valor_desconto", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "valor_acrescimo", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorAcrescimo = BigDecimal.ZERO;

    @Column(name = "valor_total", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTotal;

    @Column(name = "cfop")
    private Integer cfop;

    @Column(name = "cst", length = 3)
    private String cst;

    @Column(name = "aliquota_icms", precision = 18, scale = 6)
    private BigDecimal aliquotaIcms;

    @Column(name = "valor_icms", precision = 18, scale = 6)
    private BigDecimal valorIcms;

    @Column(name = "valor_pis", precision = 18, scale = 6)
    private BigDecimal valorPis;

    @Column(name = "valor_cofins", precision = 18, scale = 6)
    private BigDecimal valorCofins;

    @Builder.Default
    @Column(name = "cancelado", nullable = false)
    private boolean cancelado = false;

    /** Soma das devoluções já registradas deste item — limita a próxima devolução. */
    @Builder.Default
    @Column(name = "quantidade_devolvida", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidadeDevolvida = BigDecimal.ZERO;
}
