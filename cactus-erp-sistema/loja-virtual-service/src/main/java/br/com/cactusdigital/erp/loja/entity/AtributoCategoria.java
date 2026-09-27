package br.com.cactusdigital.erp.loja.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Schema de atributo dinâmico de uma {@link CategoriaLoja} — é isto que permite o MESMO
 * par {@code ProdutoLoja}/{@code VarianteProdutoLoja} atender qualquer nicho sem tabela
 * nova: a categoria "Feminino &gt; Vestidos" carrega os atributos Tamanho/Cor/Tecido, a
 * categoria "Bebidas &gt; Vinhos" carrega Volume/Safra/Uva — nenhum dos dois nichos
 * precisa de modelagem própria, só de atributos diferentes.
 * <p>
 * {@code geraVariacao=true} marca os atributos que DIFERENCIAM SKUs vendáveis (ex:
 * Tamanho/Cor — "Vestido M Azul" e "Vestido G Azul" são variantes/estoques distintos);
 * {@code geraVariacao=false} marca atributos só informativos, presos ao
 * {@code ProdutoLoja} (ex: composição do tecido — não gera SKU novo). Essa distinção é
 * o que diferencia este desenho de EAV genérico solto: aqui a intenção de cada atributo
 * é explícita, não inferida.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "atributo_categoria")
public class AtributoCategoria extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_loja_id", nullable = false)
    private CategoriaLoja categoriaLoja;

    @Column(name = "nome", nullable = false, length = 60)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoAtributo tipo;

    @Builder.Default
    @Column(name = "obrigatorio", nullable = false)
    private boolean obrigatorio = false;

    @Builder.Default
    @Column(name = "gera_variacao", nullable = false)
    private boolean geraVariacao = false;

    @Builder.Default
    @Column(name = "ordem", nullable = false)
    private Integer ordem = 0;

    @Builder.Default
    @OneToMany(mappedBy = "atributoCategoria", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AtributoCategoriaOpcao> opcoes = new ArrayList<>();
}
