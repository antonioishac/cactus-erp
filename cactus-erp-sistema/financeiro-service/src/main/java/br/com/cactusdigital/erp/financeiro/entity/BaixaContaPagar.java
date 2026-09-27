package br.com.cactusdigital.erp.financeiro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Ledger append-only de pagamentos parciais/totais — mesmo critério de
 * {@link BaixaContaReceber}, incluindo {@code estornado} (ver Javadoc lá).
 * <p>
 * {@code contaBancariaId} (2026-09-06) é a conta EFETIVA de onde o dinheiro saiu — sempre
 * obrigatório a partir desta data (validado em {@code BaixaRequest}, coluna fica nullable
 * no banco só por segurança de migration). Toda baixa registrada gera um
 * {@code MovimentoCaixaBanco} do tipo {@code SAIDA} nesta conta (ver
 * {@code MovimentoCaixaBancoService}); estornar a baixa gera a entrada compensatória
 * ({@code ENTRADA}/{@code ESTORNO_BAIXA_PAGAR}), nunca apaga o lançamento original do
 * ledger. {@code chequeEmitidoId} é preenchido quando a forma de pagamento é cheque.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "baixa_conta_pagar")
public class BaixaContaPagar extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_pagar_id", nullable = false)
    private ContaPagar contaPagar;

    @Column(name = "data_baixa", nullable = false)
    private LocalDate dataBaixa;

    @Column(name = "valor_baixa", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorBaixa;

    @Column(name = "forma_pagamento_id")
    private Long formaPagamentoId;

    /** Conta bancária EFETIVA de onde o dinheiro saiu — ver Javadoc da classe. */
    @Column(name = "conta_bancaria_id")
    private Long contaBancariaId;

    @Column(name = "cheque_emitido_id")
    private Long chequeEmitidoId;

    @Builder.Default
    @Column(name = "valor_desconto", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(name = "observacao", length = 250)
    private String observacao;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Builder.Default
    @Column(name = "estornado", nullable = false)
    private boolean estornado = false;

    @Column(name = "data_estorno")
    private LocalDate dataEstorno;

    @Column(name = "motivo_estorno", length = 250)
    private String motivoEstorno;
}
