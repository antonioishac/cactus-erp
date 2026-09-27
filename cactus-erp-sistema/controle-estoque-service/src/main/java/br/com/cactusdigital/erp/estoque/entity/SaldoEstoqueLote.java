package br.com.cactusdigital.erp.estoque.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Detalhe de saldo por lote, quando o produto é lote-controlado — a soma dos lotes de
 * um produto×depósito deve bater com {@link SaldoEstoque#getQuantidade()}. Tabela
 * separada de {@code SaldoEstoque} de propósito (ver Javadoc de lá). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "saldo_estoque_lote",
        uniqueConstraints = @UniqueConstraint(name = "uk_saldo_estoque_lote", columnNames = {"produto_id", "deposito_id", "lote_id"}))
public class SaldoEstoqueLote extends BaseEntity {

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposito_id", nullable = false)
    private Deposito deposito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @Builder.Default
    @Column(name = "quantidade", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidade = BigDecimal.ZERO;
}
