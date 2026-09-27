package br.com.cactusdigital.erp.fiscal.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Trilha de auditoria de uma execução de {@code common.saga.SagaOrchestrator} — cabeçalho.
 * Terceiro consumidor real do mecanismo (mesmo desenho exato de {@code vendas-service}/
 * {@code gestao-compras-service.SagaExecucao}) — piloto: {@code NotaFiscalEletronicaService#gerarDeVenda}.
 * <p>
 * {@code referenciaTipo}/{@code referenciaId} soltos, sem FK — hoje só vale
 * {@code "VENDA"} (o {@code vendaId} que originou a composição da NF-e).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "saga_execucao")
public class SagaExecucao extends BaseEntity {

    @Column(name = "saga_nome", nullable = false, length = 60)
    private String sagaNome;

    @Column(name = "referencia_tipo", nullable = false, length = 30)
    private String referenciaTipo;

    @Column(name = "referencia_id", nullable = false)
    private Long referenciaId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 20)
    private SituacaoSagaExecucao situacao = SituacaoSagaExecucao.EM_EXECUCAO;

    @Column(name = "passo_falhou_nome", length = 80)
    private String passoFalhouNome;

    @Column(name = "motivo_erro", length = 500)
    private String motivoErro;

    @Column(name = "data_inicio", nullable = false)
    private Instant dataInicio;

    @Column(name = "data_fim")
    private Instant dataFim;

    @Builder.Default
    @OneToMany(mappedBy = "sagaExecucao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SagaExecucaoPasso> passos = new ArrayList<>();
}
