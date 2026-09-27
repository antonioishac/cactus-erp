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
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Saldo agregado de um produto num depósito — soma de todos os lotes, se o produto for
 * lote-controlado (ver {@link SaldoEstoqueLote}). <b>Nunca é alterado diretamente</b>:
 * todo `UPDATE` aqui acontece dentro da mesma transação que insere um
 * {@link EstoqueMovimento} (ver {@code EstoqueMovimentoService}) — o ledger é a fonte da
 * verdade, este registro é só a materialização rápida de leitura.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "saldo_estoque",
        uniqueConstraints = @UniqueConstraint(name = "uk_saldo_estoque_produto_deposito", columnNames = {"produto_id", "deposito_id"}))
public class SaldoEstoque extends BaseEntity {

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposito_id", nullable = false)
    private Deposito deposito;

    @Builder.Default
    @Column(name = "quantidade", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidade = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "quantidade_reservada", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidadeReservada = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "custo_medio_ponderado", nullable = false, precision = 18, scale = 6)
    private BigDecimal custoMedioPonderado = BigDecimal.ZERO;

    @Column(name = "data_ultima_movimentacao")
    private Instant dataUltimaMovimentacao;

    /** Quantidade livre pra nova venda/reserva — total menos o que já está comprometido. */
    public BigDecimal getQuantidadeDisponivel() {
        return quantidade.subtract(quantidadeReservada);
    }
}
