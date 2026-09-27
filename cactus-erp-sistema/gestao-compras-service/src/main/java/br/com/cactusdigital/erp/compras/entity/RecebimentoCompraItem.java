package br.com.cactusdigital.erp.compras.entity;

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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "recebimento_compra_item")
public class RecebimentoCompraItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recebimento_id", nullable = false)
    private RecebimentoCompra recebimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_compra_item_id", nullable = false)
    private PedidoCompraItem pedidoCompraItem;

    @Column(name = "quantidade_recebida", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidadeRecebida;

    @Column(name = "deposito_id", nullable = false)
    private Long depositoId;

    @Column(name = "lote_id")
    private Long loteId;
}
