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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Atividade agendada (tarefa futura — ligar, reunir, visitar) vinculada a um
 * {@link Lead} e/ou {@link Oportunidade}. Pelo menos um dos dois precisa estar
 * preenchido (validado em {@code AtividadeService#criar}, mesmo critério de
 * {@code OsProdutoServico} exigir {@code produtoId} condicionalmente ao {@code tipo}).
 * Diferente de {@link Interacao} (histórico do que JÁ aconteceu, append-only), Atividade
 * tem estado ({@code concluida}) — é uma tarefa, não um registro de evento passado.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "atividade")
public class Atividade extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oportunidade_id")
    private Oportunidade oportunidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoAtividade tipo;

    /** Vendedor/usuário responsável por executar a atividade — referência SOLTA. */
    @Column(name = "responsavel_id")
    private Long responsavelId;

    @Column(name = "data_hora", nullable = false)
    private Instant dataHora;

    @Builder.Default
    @Column(name = "concluida", nullable = false)
    private boolean concluida = false;

    @Column(name = "data_conclusao")
    private Instant dataConclusao;

    @Column(name = "descricao", length = 500)
    private String descricao;
}
