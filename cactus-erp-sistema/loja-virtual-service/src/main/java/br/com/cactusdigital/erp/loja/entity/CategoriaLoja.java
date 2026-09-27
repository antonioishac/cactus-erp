package br.com.cactusdigital.erp.loja.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Categoria de produto — árvore (auto-referência, mesmo padrão de {@code PastaDocumento}
 * do gestao-documentos-service), POR LOJA (nichos diferentes têm árvores de categoria
 * completamente diferentes — "Feminino &gt; Vestidos" não existe na loja de materiais de
 * construção). É o elo entre categoria e o schema de atributos dinâmicos que ela carrega
 * ({@link AtributoCategoria}) — ver Javadoc de {@code ProdutoLoja} pro desenho completo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "categoria_loja")
public class CategoriaLoja extends BaseEntity {

    @Column(name = "loja_id", nullable = false)
    private Long lojaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_pai_id")
    private CategoriaLoja categoriaPai;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Builder.Default
    @Column(name = "ordem", nullable = false)
    private Integer ordem = 0;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
