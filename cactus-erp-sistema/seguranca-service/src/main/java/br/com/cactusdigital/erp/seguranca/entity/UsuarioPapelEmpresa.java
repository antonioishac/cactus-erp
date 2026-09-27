package br.com.cactusdigital.erp.seguranca.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Atribuição de um {@link Papel} a um {@link Usuario} numa empresa específica — é isso
 * que dá o escopo "por filial" ao perfilamento: um mesmo usuário pode ter papéis (logo,
 * permissões) diferentes em cada empresa.
 * <p>
 * {@code empresaId} é uma referência solta (sem FK de banco, sem {@code @ManyToOne}) —
 * {@code Empresa} mora no {@code cadastro-base-service}, em outro schema. Por isso
 * também não existe {@code empresaNome} em nenhum DTO/claim deste serviço: sem cópia
 * local de Empresa, resolver o nome exigiria chamar o cadastro-base a cada emissão de
 * token, transformando o login numa dependência síncrona forte — o token carrega só o
 * {@code empresaId}, quem precisa do nome resolve a partir dele no serviço que já tem
 * esse dado.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "usuario_papel_empresa",
        uniqueConstraints = @UniqueConstraint(name = "uk_usuario_papel_empresa", columnNames = {"usuario_id", "papel_id", "empresa_id"}))
public class UsuarioPapelEmpresa extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "papel_id", nullable = false)
    private Papel papel;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;
}
