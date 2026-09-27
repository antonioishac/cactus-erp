package br.com.cactusdigital.erp.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Superclasse de toda entidade JPA do Cactus ERP: PK {@code BIGINT} auto-increment
 * (não {@code INT UNSIGNED} como no PDV legado — mais folga para o volume esperado)
 * e auditoria automática (quem/quando criou e alterou), via Spring Data JPA Auditing.
 * <p>
 * {@code createdBy}/{@code updatedBy} são resolvidos pelo {@code AuditorAware} de cada
 * serviço (tipicamente o {@code login} extraído do JWT autenticado).
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(of = "id")
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private Instant dataCriacao;

    @LastModifiedDate
    @Column(name = "data_atualizacao", nullable = false)
    private Instant dataAtualizacao;

    @CreatedBy
    @Column(name = "criado_por", updatable = false, length = 100)
    private String criadoPor;

    @LastModifiedBy
    @Column(name = "atualizado_por", length = 100)
    private String atualizadoPor;
}
