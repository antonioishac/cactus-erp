package br.com.cactusdigital.erp.estoque.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * {@code quantidadeSistema} é o snapshot na abertura do inventário — em contagem
 * {@code CEGA} o app cliente decide não exibir esse campo ao contador (a API não
 * esconde, só documenta a intenção).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "inventario_fisico_item")
public class InventarioFisicoItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventario_id", nullable = false)
    private InventarioFisico inventario;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @Column(name = "quantidade_sistema", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidadeSistema;

    @Column(name = "quantidade_contada", precision = 18, scale = 6)
    private BigDecimal quantidadeContada;

    @Column(name = "contador_id")
    private Long contadorId;

    @Column(name = "data_contagem")
    private Instant dataContagem;

    /** {@code null} enquanto não contado. */
    public BigDecimal getQuantidadeDivergencia() {
        return quantidadeContada == null ? null : quantidadeContada.subtract(quantidadeSistema);
    }
}
