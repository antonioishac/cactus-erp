package br.com.cactusdigital.erp.loja.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Produto — o "modelo" vendido pela loja (ex: "Vestido Floral Midi", "Vinho Tinto
 * Reserva 2021"), POR LOJA (catálogo isolado por tenant). Não é vendável diretamente —
 * quem carrega preço/estoque/GTIN é {@link VarianteProdutoLoja} (o SKU de fato). Ver
 * CLAUDE.md, ANÁLISE: LOJA VIRTUAL B2C, seção "Parametrização de produto" pro desenho
 * completo Produto+Variante+Atributo.
 * <p>
 * <b>Simplificação registrada desta fatia</b>: atributos "informativos" (
 * {@code geraVariacao=false} em {@link AtributoCategoria}, ex: composição do tecido)
 * ainda não têm uma tabela de valor própria neste nível (só {@link VarianteProdutoLoja}
 * tem {@link VarianteAtributoValor}) — registrado como próximo passo, não bloqueia o
 * catálogo funcionar (a maioria dos atributos relevantes pra busca/filtro é a que gera
 * variação mesmo).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "produto_loja")
public class ProdutoLoja extends BaseEntity {

    @Column(name = "loja_id", nullable = false)
    private Long lojaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_loja_id", nullable = false)
    private CategoriaLoja categoriaLoja;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "descricao", length = 2000)
    private String descricao;

    @Column(name = "marca", length = 60)
    private String marca;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;

    @Builder.Default
    @OneToMany(mappedBy = "produtoLoja", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VarianteProdutoLoja> variantes = new ArrayList<>();
}
