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
 * Perfil tributário do produto (ex: "mercadoria própria sujeita a ICMS-ST", "revenda
 * sem ST") — validado contra {@code fenix.sql.TRIBUT_GRUPO_TRIBUTARIO}. Produtos com
 * tributação idêntica ficam no mesmo grupo, independente da categoria comercial
 * (Grupo/Subgrupo) deles.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "grupo_tributario")
public class GrupoTributario extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    /** Campo "orig" da NFe: 0-9, origem da mercadoria (nacional/importada/etc). */
    @Column(name = "origem_mercadoria", nullable = false, length = 1)
    private String origemMercadoria;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
