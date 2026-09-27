package br.com.cactusdigital.erp.estoque.entity;

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
 * Cabeçalho de contagem física — {@code finalizar()} (no service) gera um
 * {@link EstoqueMovimento} tipo {@code AJUSTE} (origem {@code INVENTARIO}) pra cada
 * {@link InventarioFisicoItem} com divergência ≠ 0.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "inventario_fisico")
public class InventarioFisico extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposito_id", nullable = false)
    private Deposito deposito;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 15)
    private TipoInventario tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 15)
    private SituacaoInventario situacao;

    @Column(name = "responsavel_id", nullable = false)
    private Long responsavelId;

    @Column(name = "data_abertura", nullable = false)
    private Instant dataAbertura;

    @Column(name = "data_fechamento")
    private Instant dataFechamento;

    @Builder.Default
    @OneToMany(mappedBy = "inventario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InventarioFisicoItem> itens = new ArrayList<>();
}
