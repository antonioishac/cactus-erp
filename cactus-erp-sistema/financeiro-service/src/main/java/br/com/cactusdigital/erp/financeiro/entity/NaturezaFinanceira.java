package br.com.cactusdigital.erp.financeiro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Catálogo local (não é referência solta cross-serviço — natureza financeira é dado
 * fino de cada empresa financeira, vive só aqui) — espelha {@code FIN_NATUREZA_FINANCEIRA}
 * do {@code fenix.sql}: categoriza um título de {@link ContaPagar}/{@link ContaReceber}
 * (ex: "Aluguel", "Venda de Mercadoria", "Salários"), separado por {@link TipoNaturezaFinanceira}.
 * Fecha o gap registrado na análise de 2026-09-06 (root CLAUDE.md) — a primeira versão de
 * Contas a Pagar/Receber não tinha nenhuma categorização.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "natureza_financeira")
public class NaturezaFinanceira extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "codigo", nullable = false, length = 10)
    private String codigo;

    @Column(name = "descricao", nullable = false, length = 100)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoNaturezaFinanceira tipo;

    @Builder.Default
    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;
}
