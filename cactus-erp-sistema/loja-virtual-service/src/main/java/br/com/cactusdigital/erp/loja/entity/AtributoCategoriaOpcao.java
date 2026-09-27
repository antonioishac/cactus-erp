package br.com.cactusdigital.erp.loja.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
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

/** Opção válida de um {@link AtributoCategoria} do tipo {@link TipoAtributo#LISTA} — ex: atributo "Tamanho" com opções "P"/"M"/"G"/"GG". */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "atributo_categoria_opcao")
public class AtributoCategoriaOpcao extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atributo_categoria_id", nullable = false)
    private AtributoCategoria atributoCategoria;

    @Column(name = "valor", nullable = false, length = 60)
    private String valor;

    @Builder.Default
    @Column(name = "ordem", nullable = false)
    private Integer ordem = 0;
}
