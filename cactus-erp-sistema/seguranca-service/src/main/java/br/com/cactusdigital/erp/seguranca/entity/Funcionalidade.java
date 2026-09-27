package br.com.cactusdigital.erp.seguranca.entity;

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
 * Ação granular dentro de um {@link Modulo}, código no padrão {@code MODULO_RECURSO_ACAO}
 * (ex: {@code CADASTRO_PRODUTO_CRIAR}) — é este código que vira {@code authority} no
 * JWT (ver {@code br.com.cactusdigital.erp.common.security.JwtAuthoritiesConverter})
 * e o que os controllers checam via {@code @PreAuthorize("hasAuthority('...')")}.
 * <p>
 * Só leitura via API pelo mesmo motivo de {@link Modulo}: cada serviço registra as
 * próprias funcionalidades via migration ao ganhar um novo endpoint protegido.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "funcionalidade")
public class Funcionalidade extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modulo_id", nullable = false)
    private Modulo modulo;

    @Column(name = "codigo", nullable = false, length = 60, unique = true)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
