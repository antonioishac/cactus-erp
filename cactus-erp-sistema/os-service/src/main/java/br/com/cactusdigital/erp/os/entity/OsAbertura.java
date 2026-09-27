package br.com.cactusdigital.erp.os.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Ordem de Serviço — validado contra {@code fenix.sql.OS_ABERTURA}. {@code clienteId}/
 * {@code colaboradorId} (técnico responsável) são referências SOLTAS pro
 * cadastro-base-service. {@code osStatus} é FK de verdade pro catálogo livre
 * {@link OsStatus} (não um enum fixo — ver Javadoc de lá).
 * <p>
 * <b>Melhorias sobre o fenix</b> (registradas no CLAUDE.md, ANÁLISE: MÓDULO COMERCIAL —
 * "fenix é raso, sem aprovação de orçamento"): {@code orcamentoAprovado}/
 * {@code dataAprovacaoOrcamento} — o fenix não rastreia se o cliente aprovou o orçamento
 * antes do serviço ser executado, só abre/fecha a OS. Assinatura digital do cliente fica
 * fora de escopo (sem infraestrutura de e-signature, mesmo critério já registrado pro
 * GED — "fluxo de assinatura eletrônica... fora de escopo").
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "os_abertura")
public class OsAbertura extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "os_status_id", nullable = false)
    private OsStatus osStatus;

    /** Filial que prestou o serviço — referência SOLTA pro {@code Empresa} do cadastro-base-service.
     * Mesmo padrão de {@code VendaCabecalho.empresaId} (vendas-service, mesmo gap
     * fechado na Fase 8) — necessário pra Fase 9 (NFS-e) resolver qual
     * {@code EmpresaConfiguracaoFiscal} usar na emissão. */
    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    /** Técnico responsável — referência SOLTA pro Colaborador do cadastro-base. */
    @Column(name = "colaborador_id")
    private Long colaboradorId;

    @Column(name = "numero", length = 20)
    private String numero;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "hora_inicio", length = 8)
    private String horaInicio;

    /** Previsão de conclusão — o "SLA" básico que o fenix já tinha, mesmo sem um módulo de SLA formal. */
    @Column(name = "data_previsao")
    private LocalDate dataPrevisao;

    @Column(name = "hora_previsao", length = 8)
    private String horaPrevisao;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "hora_fim", length = 8)
    private String horaFim;

    @Column(name = "nome_contato", length = 50)
    private String nomeContato;

    @Column(name = "fone_contato", length = 15)
    private String foneContato;

    @Column(name = "observacao_cliente", length = 500)
    private String observacaoCliente;

    @Column(name = "observacao_abertura", length = 500)
    private String observacaoAbertura;

    /** Melhoria sobre o fenix (🆕) — ver Javadoc da classe. */
    @Builder.Default
    @Column(name = "orcamento_aprovado", nullable = false)
    private boolean orcamentoAprovado = false;

    @Column(name = "data_aprovacao_orcamento")
    private Instant dataAprovacaoOrcamento;

    @Builder.Default
    @Column(name = "valor_total", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Builder.Default
    @OneToMany(mappedBy = "osAbertura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OsAberturaEquipamento> equipamentos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "osAbertura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OsProdutoServico> itens = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "osAbertura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OsEvolucao> evolucoes = new ArrayList<>();
}
