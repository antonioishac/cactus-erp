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

/**
 * {@code PRODUTO_TIPO} do cactus-pdv — lookup configurável pela empresa (ex: "Produto
 * normal", "Kit", "Insumo"), **distinto** de {@link ProdutoGrupo}/{@link ProdutoSubgrupo}
 * (categoria comercial) e de {@link TipoItemSped} (classificação fiscal fechada da
 * Receita) — ver MAPA DE SINCRONIZAÇÃO no CLAUDE.md.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "produto_tipo")
public class ProdutoTipo extends BaseEntity {

    @Column(name = "codigo", nullable = false, length = 2, unique = true)
    private String codigo;

    @Column(name = "descricao", nullable = false, length = 100)
    private String descricao;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
