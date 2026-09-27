package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Fornecedor — referencia {@link Pessoa} (física ou jurídica), mesmo padrão de
 * {@link Cliente}/{@link Colaborador}. Validado contra {@code fenix.sql.FORNECEDOR}
 * ({@code fornecedorDesde}/{@code dataCadastro}/{@code observacao}), mais
 * {@code prazoEntregaDias} — enriquecimento próprio (🆕, sem equivalente em
 * fenix.sql/BANCO_PDV.md), útil pro futuro {@code gestao-compras-service} (Fase 3)
 * estimar prazo de recebimento.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "fornecedor")
public class Fornecedor extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pessoa_id", nullable = false, unique = true)
    private Pessoa pessoa;

    @Column(name = "fornecedor_desde")
    private LocalDate fornecedorDesde;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;

    /** Prazo médio de entrega, em dias — enriquecimento (🆕), pro futuro módulo de compras estimar recebimento. */
    @Column(name = "prazo_entrega_dias")
    private Integer prazoEntregaDias;

    @Column(name = "observacao", length = 250)
    private String observacao;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
