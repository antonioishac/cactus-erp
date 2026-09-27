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

/** Cache local denormalizado do fornecedor (cadastro-base-service). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "fornecedor_dim")
public class FornecedorDim extends BaseEntity {

    @Column(name = "fornecedor_id", nullable = false, unique = true)
    private Long fornecedorId;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;
}
