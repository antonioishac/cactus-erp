package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "produto_marca")
public class ProdutoMarca extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 100, unique = true)
    private String nome;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
