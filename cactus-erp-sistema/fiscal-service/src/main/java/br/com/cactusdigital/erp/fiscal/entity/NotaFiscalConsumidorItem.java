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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Item da NFC-e — copiado do snapshot já congelado em {@code ItemPedido}
 * (loja-virtual-service), não recalculado. Só {@code csosn} (Simples Nacional) — ver
 * Javadoc de {@link NotaFiscalConsumidor} pro porquê de não haver CST de regime normal.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "nota_fiscal_consumidor_item")
public class NotaFiscalConsumidorItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nota_fiscal_consumidor_id", nullable = false)
    private NotaFiscalConsumidor notaFiscalConsumidor;

    @Column(name = "numero_item", nullable = false)
    private Integer numeroItem;

    @Column(name = "variante_produto_loja_id", nullable = false)
    private Long varianteProdutoLojaId;

    @Column(name = "descricao", nullable = false, length = 150)
    private String descricao;

    @Column(name = "ncm", length = 8)
    private String ncm;

    @Column(name = "csosn", length = 3)
    private String csosn;

    @Column(name = "quantidade", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorUnitario;

    @Column(name = "valor_total", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTotal;
}
