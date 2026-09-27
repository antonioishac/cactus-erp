package br.com.cactusdigital.erp.contabilidade.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Uma linha (débito OU crédito) de um {@link LancamentoContabil} — {@link #valor} é
 * sempre positivo (mesmo critério de {@code MovimentoCaixaBanco}/{@code EstoqueMovimento}
 * no resto do monorepo), {@link #tipoPartida} decide o lado. {@link #conta} tem que ser
 * {@code contaAnalitica=true} (validado no service, nunca na entidade) — contas
 * sintéticas só agrupam, nunca recebem lançamento de verdade, mesma regra contábil que
 * fundamenta o registro {@code I050.IND_CTA} da ECD.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "partida_contabil")
public class PartidaContabil extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lancamento_contabil_id", nullable = false)
    private LancamentoContabil lancamentoContabil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", nullable = false)
    private PlanoConta conta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_custo_id")
    private CentroCusto centroCusto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_partida", nullable = false, length = 10)
    private TipoPartida tipoPartida;

    @Column(name = "valor", nullable = false, precision = 18, scale = 6)
    private BigDecimal valor;

    @Column(name = "historico_complementar", length = 250)
    private String historicoComplementar;
}
