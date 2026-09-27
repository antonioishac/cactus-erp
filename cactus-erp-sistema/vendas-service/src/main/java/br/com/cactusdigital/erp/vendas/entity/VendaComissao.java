package br.com.cactusdigital.erp.vendas.entity;

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
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Lançamento de comissão gerado automaticamente na confirmação de uma
 * {@link VendaCabecalho} — validado contra {@code fenix.sql.VENDA_COMISSAO}. Ver
 * {@code VendaService#gerarComissao} pra regra de cálculo (taxa do {@link Vendedor} ou,
 * se existir {@link ComissaoObjetivo} pro produto, a taxa/valor específico).
 * {@code situacao} PENDENTE/PAGO/CANCELADO — liquidação de fato fica pro futuro módulo
 * Financeiro (gap registrado no CLAUDE.md).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "venda_comissao")
public class VendaComissao extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    private VendaCabecalho venda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Vendedor vendedor;

    @Column(name = "valor_venda", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorVenda;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contabil", length = 10)
    private TipoPagamentoVenda tipoContabil;

    @Column(name = "valor_comissao", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorComissao;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 10)
    private SituacaoComissao situacao = SituacaoComissao.PENDENTE;

    @Column(name = "data_lancamento")
    private LocalDate dataLancamento;
}
