package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Cargo do colaborador — validado contra {@code fenix.sql.CARGO}: nome/descrição não
 * bastam pra um RH de verdade, precisa de salário-base e os códigos CBO (Classificação
 * Brasileira de Ocupações) oficiais, usados em eSocial/RAIS.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "cargo")
public class Cargo extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "descricao", length = 250)
    private String descricao;

    @Column(name = "salario", precision = 18, scale = 6)
    private BigDecimal salario;

    @Column(name = "cbo_1994", length = 10)
    private String cbo1994;

    @Column(name = "cbo_2002", length = 10)
    private String cbo2002;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
