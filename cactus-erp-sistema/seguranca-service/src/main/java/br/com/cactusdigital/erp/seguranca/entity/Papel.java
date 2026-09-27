package br.com.cactusdigital.erp.seguranca.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Papel (ex: ADMIN, COMPRADOR) — agrupador de {@link Funcionalidade}s. Um {@link Usuario}
 * recebe papéis por empresa via {@link UsuarioPapelEmpresa}, não diretamente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "papel")
public class Papel extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 60, unique = true)
    private String nome;

    @Column(name = "descricao", length = 250)
    private String descricao;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "papel_funcionalidade",
            joinColumns = @JoinColumn(name = "papel_id"),
            inverseJoinColumns = @JoinColumn(name = "funcionalidade_id"))
    private Set<Funcionalidade> funcionalidades = new HashSet<>();
}
