package br.com.cactusdigital.erp.estoque.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * BOM simples (não é manufatura completa): {@code produtoKitId} é vendido como um
 * conjunto e baixa {@code quantidade} de cada {@code produtoComponenteId} na venda (ver
 * endpoint de baixa em cascata — {@code EstoqueMovimentoService#baixarKit}).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "produto_kit_componente", uniqueConstraints = @UniqueConstraint(name = "uk_kit_componente", columnNames = {"produto_kit_id", "produto_componente_id"}))
public class ProdutoKitComponente extends BaseEntity {

    @Column(name = "produto_kit_id", nullable = false)
    private Long produtoKitId;

    @Column(name = "produto_componente_id", nullable = false)
    private Long produtoComponenteId;

    @Column(name = "quantidade", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidade;
}
