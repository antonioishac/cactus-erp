package br.com.cactusdigital.erp.vendas.entity;

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
 * Perfil de comissão (ex: "Vendedor Balcão", "Representante Externo") — validado contra
 * {@code fenix.sql.COMISSAO_PERFIL} (só {@code codigo}/{@code nome} lá). {@code situacao}
 * é enriquecimento (🆕) — mesmo padrão de soft-delete usado em todo o resto do sistema,
 * fenix não tinha.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "comissao_perfil")
public class ComissaoPerfil extends BaseEntity {

    @Column(name = "codigo", length = 3)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
