package br.com.cactusdigital.erp.fiscal.entity;

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

/** Ledger append-only — cada linha é UM evento (passo executado, compensado, ou falhou ao compensar); nunca é atualizada/apagada depois de criada. Mesmo desenho de {@code vendas-service}/{@code gestao-compras-service.SagaExecucaoPasso}. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "saga_execucao_passo")
public class SagaExecucaoPasso extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saga_execucao_id", nullable = false)
    private SagaExecucao sagaExecucao;

    @Column(name = "nome", nullable = false, length = 80)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 20)
    private SituacaoPassoSaga situacao;

    @Column(name = "motivo_erro", length = 500)
    private String motivoErro;

    @Column(name = "data_evento", nullable = false)
    private Instant dataEvento;
}
