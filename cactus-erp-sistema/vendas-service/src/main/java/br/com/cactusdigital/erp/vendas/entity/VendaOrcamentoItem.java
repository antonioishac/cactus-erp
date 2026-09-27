package br.com.cactusdigital.erp.vendas.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Item de {@link VendaOrcamento} — validado contra {@code fenix.sql.VENDA_ORCAMENTO_DETALHE}. {@code produtoId} é referência SOLTA. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "venda_orcamento_item")
public class VendaOrcamentoItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orcamento_id", nullable = false)
    private VendaOrcamento orcamento;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(name = "quantidade", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorUnitario;

    @Column(name = "valor_subtotal", precision = 18, scale = 6)
    private BigDecimal valorSubtotal;

    @Column(name = "taxa_desconto", precision = 18, scale = 6)
    private BigDecimal taxaDesconto;

    @Column(name = "valor_desconto", precision = 18, scale = 6)
    private BigDecimal valorDesconto;

    @Column(name = "valor_total", precision = 18, scale = 6)
    private BigDecimal valorTotal;
}
