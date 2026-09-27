package br.com.cactusdigital.erp.loja.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Valor de UM atributo que compõe uma {@link VarianteProdutoLoja} específica — ex: pra
 * a variante "Vestido Floral Midi — M, Azul" existem 2 linhas aqui: (Tamanho, "M") e
 * (Cor, "Azul"). Só referencia {@link AtributoCategoria} com {@code geraVariacao=true}
 * (validado em {@code VarianteProdutoLojaService}); {@code valor} sempre string, mesmo
 * pra {@link TipoAtributo#NUMERO} — simplificação deliberada desta fatia (sem tipagem
 * forte por linha, mesmo critério já usado em outros catálogos livres do monorepo,
 * ex: {@code Os.tipoCobertura}).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "variante_atributo_valor")
public class VarianteAtributoValor extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_produto_loja_id", nullable = false)
    private VarianteProdutoLoja varianteProdutoLoja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atributo_categoria_id", nullable = false)
    private AtributoCategoria atributoCategoria;

    @Column(name = "valor", nullable = false, length = 60)
    private String valor;
}
