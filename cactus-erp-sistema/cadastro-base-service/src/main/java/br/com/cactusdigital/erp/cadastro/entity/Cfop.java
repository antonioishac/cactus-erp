package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Código Fiscal de Operações e Prestações — catálogo oficial (~600 códigos, seed com os mais comuns). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "cfop")
public class Cfop extends BaseEntity {

    @Column(name = "codigo", nullable = false, unique = true)
    private Integer codigo;

    @Column(name = "descricao", nullable = false, length = 1000)
    private String descricao;

    @Column(name = "aplicacao", length = 1000)
    private String aplicacao;
}
