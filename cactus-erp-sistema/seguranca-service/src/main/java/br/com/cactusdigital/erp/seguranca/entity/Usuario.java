package br.com.cactusdigital.erp.seguranca.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Usuário do sistema. {@code ativo} e {@code bloqueado} são flags separadas (em vez de
 * reaproveitar o enum {@link br.com.cactusdigital.erp.common.domain.Situacao}):
 * são conceitos diferentes — {@code ativo=false} é uma desativação deliberada
 * (equivalente a "excluído" sem perder histórico/auditoria, ver
 * {@code UsuarioService#inativar}), {@code bloqueado=true} é um travamento temporário
 * (ex: excesso de tentativas de login), reversível sem reativar o usuário.
 * <p>
 * {@code colaboradorId} é uma referência solta (sem FK de banco), não um
 * {@code @ManyToOne} — {@code Colaborador} mora no {@code cadastro-base-service}, em
 * outro schema. Validado contra {@code fenix.sql.USUARIO} (lá é 1:1 obrigatório com
 * Colaborador — todo usuário é um funcionário); mantido OPCIONAL aqui, deliberadamente:
 * contas de sistema/suporte/administração pura não precisam ter um registro de RH por
 * trás. Não há validação de existência do id no momento — ver decisão registrada no
 * roadmap sobre referências cross-serviço (fica pra quando houver necessidade real de
 * chamar o cadastro-base na hora de provisionar um usuário).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "usuario")
public class Usuario extends BaseEntity {

    @Column(name = "colaborador_id")
    private Long colaboradorId;

    @Column(name = "login", nullable = false, length = 60, unique = true)
    private String login;

    @Column(name = "senha_hash", nullable = false, length = 100)
    private String senhaHash;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "email", length = 250)
    private String email;

    @Builder.Default
    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Builder.Default
    @Column(name = "bloqueado", nullable = false)
    private boolean bloqueado = false;

    @Column(name = "data_ultimo_login")
    private Instant dataUltimoLogin;
}
