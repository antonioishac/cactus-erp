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
 * Transportadora — referencia {@link Pessoa} (física ou jurídica), mesmo padrão de
 * {@link Cliente}/{@link Colaborador}/{@link Fornecedor}. Validado contra
 * {@code fenix.sql.TRANSPORTADORA} ({@code dataCadastro}/{@code observacao} — a tabela
 * do fenix é bem rasa, só isso). Gap real fechado na Fase 7 (Módulo Comercial):
 * {@code VENDA_CABECALHO}/{@code VENDA_ORCAMENTO_CABECALHO}/{@code VENDA_FRETE} do
 * fenix referenciam Transportadora e ela não existia no cadastro-base até agora.
 *
 * <p>{@code rntrc} é enriquecimento (🆕, sem equivalente em fenix.sql/BANCO_PDV.md) —
 * Registro Nacional de Transportadores Rodoviários de Cargas (ANTT), campo
 * obrigatório em qualquer CT-e emitido para transporte rodoviário de carga (Fase 11
 * do roadmap) — nasce aqui pra não precisar de ALTER TABLE quando essa fase chegar.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "transportadora")
public class Transportadora extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pessoa_id", nullable = false, unique = true)
    private Pessoa pessoa;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;

    /** Registro Nacional de Transportadores Rodoviários de Cargas (ANTT) — enriquecimento (🆕), obrigatório em CT-e. */
    @Column(name = "rntrc", length = 20)
    private String rntrc;

    @Column(name = "observacao", length = 250)
    private String observacao;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
