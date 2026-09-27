package br.com.cactusdigital.erp.financeiro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Catálogo GLOBAL de bancos brasileiros (código FEBRABAN de 3 dígitos — dado público,
 * oficial, seedado por migration — mesmo critério de {@code Ncm}/{@code Cfop} no
 * cadastro-base-service). Espelha {@code BANCO} do {@code fenix.sql}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "banco")
public class Banco extends BaseEntity {

    /** Código FEBRABAN, 3 dígitos (ex: "001" Banco do Brasil, "341" Itaú). */
    @Column(name = "codigo", nullable = false, unique = true, length = 3)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
}
