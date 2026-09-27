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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * {@code unidadeCompraId}/{@code fatorConversaoEstoque} fecham o gap "unidade de compra
 * × estoque com fator de conversão" registrado como pendente na Fase 2 — ex: compra em
 * caixa de 12 (unidade de compra), gera ENTRADA de {@code quantidade × fatorConversao}
 * unidades no controle-estoque-service (que só entende a unidade de estoque do produto).
 * {@code quantidadeRecebida} é um acumulador — soma de todos os recebimentos parciais.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "pedido_compra_item")
public class PedidoCompraItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoCompra pedido;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(name = "unidade_compra_id")
    private Long unidadeCompraId;

    @Builder.Default
    @Column(name = "fator_conversao_estoque", nullable = false, precision = 18, scale = 6)
    private BigDecimal fatorConversaoEstoque = BigDecimal.ONE;

    @Column(name = "quantidade", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidade;

    @Builder.Default
    @Column(name = "quantidade_recebida", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidadeRecebida = BigDecimal.ZERO;

    @Column(name = "valor_unitario", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorUnitario;

    @Column(name = "taxa_desconto", precision = 18, scale = 6)
    private BigDecimal taxaDesconto;

    @Column(name = "valor_desconto", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorDesconto;

    @Column(name = "valor_total", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTotal;

    /** Quanto ainda falta receber, na unidade de compra. */
    public BigDecimal getQuantidadePendente() {
        return quantidade.subtract(quantidadeRecebida);
    }
}
