package br.com.cactusdigital.erp.contabilidade.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Conta do plano de contas da empresa — hierárquica (auto-referência via
 * {@code contaPai}, mesmo padrão de {@code PastaDocumento} no gestao-documentos-service),
 * já que nenhum plano de contas real é uma lista flat: grupos sintéticos (ex: "1 —
 * Ativo" → "1.1 — Ativo Circulante") agrupam contas analíticas (ex: "1.1.01.001 — Caixa"),
 * que são as únicas que podem receber {@link PartidaContabil} de verdade
 * ({@link #contaAnalitica}). Greenfield — {@code fenix.sql} não tem nenhuma tabela de
 * plano de contas pra cruzar campo a campo (ver ANÁLISE: MÓDULO CONTÁBIL E FISCAL no
 * CLAUDE.md raiz).
 * <p>
 * {@link #codigoReferencialRfb} é o vínculo opcional com o plano de contas referencial
 * da Receita Federal (registro {@code I051} da ECD) — string livre porque este módulo
 * não tem o catálogo oficial completo carregado (não inventar um subconjunto arriscando
 * mapear errado; a empresa/contador preenche manualmente quando aplicável).
 * {@code empresaId} é referência solta pro {@code cadastro-base-service}, mesmo padrão
 * de {@code VendaCabecalho.empresaId}/{@code PedidoCompra.empresaId}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "plano_conta")
public class PlanoConta extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "codigo", nullable = false, length = 30)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_pai_id")
    private PlanoConta contaPai;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoContaContabil tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "natureza", nullable = false, length = 10)
    private NaturezaConta natureza;

    /** {@code true} = conta analítica (recebe lançamento). {@code false} = sintética/agrupadora (só organiza a árvore, nunca recebe partida — ver {@code ContabilidadeErrorCode#PLANO_CONTA_NAO_ANALITICA_PARA_LANCAMENTO}). */
    @Builder.Default
    @Column(name = "conta_analitica", nullable = false)
    private boolean contaAnalitica = true;

    @Column(name = "codigo_referencial_rfb", length = 30)
    private String codigoReferencialRfb;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
