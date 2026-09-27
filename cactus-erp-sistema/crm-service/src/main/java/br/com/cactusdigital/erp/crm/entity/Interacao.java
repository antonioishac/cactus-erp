package br.com.cactusdigital.erp.crm.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Interação — histórico append-only do que JÁ aconteceu (ligação feita, e-mail
 * enviado, reunião realizada, nota livre), vinculado a um {@link Lead} e/ou
 * {@link Oportunidade} (mesma validação "pelo menos um dos dois" de {@link Atividade}).
 * Sem {@code PUT}/{@code DELETE} de propósito — mesmo critério de
 * {@code EstoqueMovimento}/{@code DocumentoVersao}: histórico não se edita, só se
 * acumula.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "interacao")
public class Interacao extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oportunidade_id")
    private Oportunidade oportunidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoInteracao tipo;

    @Column(name = "data_interacao", nullable = false)
    private Instant dataInteracao;

    /** Quem registrou a interação — referência SOLTA pro {@code Usuario} do seguranca-service. */
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "descricao", nullable = false, length = 1000)
    private String descricao;
}
