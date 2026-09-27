package br.com.cactusdigital.erp.estoque.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Rastreio unitário — eletrônicos/ativos que precisam identificar cada unidade
 * individualmente (não só a quantidade). {@code deposito} é o local atual; fica
 * {@code null} quando já vendido/baixado. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "numero_serie", uniqueConstraints = @UniqueConstraint(name = "uk_numero_serie_produto", columnNames = {"produto_id", "numero_serie"}))
public class NumeroSerie extends BaseEntity {

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(name = "numero_serie", nullable = false, length = 100)
    private String numeroSerie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposito_id")
    private Deposito deposito;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 20)
    private SituacaoNumeroSerie situacao;
}
