package br.com.cactusdigital.erp.bi.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Cache local denormalizado do depósito (controle-estoque-service). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "deposito_dim")
public class DepositoDim extends BaseEntity {

    @Column(name = "deposito_id", nullable = false, unique = true)
    private Long depositoId;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;
}
