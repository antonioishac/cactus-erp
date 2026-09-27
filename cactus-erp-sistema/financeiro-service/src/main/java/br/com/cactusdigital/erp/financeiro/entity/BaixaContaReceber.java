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
 * Ledger append-only de recebimentos parciais/totais — sem {@code PUT}/{@code DELETE},
 * mesmo critério de {@code EstoqueMovimento}/{@code DocumentoVersao}. {@code estornado}
 * é a única mutação permitida numa linha já gravada (2026-09-05) — não é um "desfazer"
 * físico do registro (o valor/data/forma de pagamento originais continuam intactos pra
 * auditoria), só marca a baixa como sem efeito nos totais do título a partir de então.
 * Ver {@code ContaReceberService#estornarBaixa}.
 * <p>
 * {@code contaBancariaId} (2026-09-06) é a conta EFETIVA de onde o dinheiro entrou —
 * sempre obrigatório a partir desta data (validado em {@code BaixaRequest}, coluna fica
 * nullable no banco só por segurança de migration). Toda baixa gera um
 * {@code MovimentoCaixaBanco} do tipo {@code ENTRADA} nesta conta; estornar gera a saída
 * compensatória. {@code chequeRecebidoId} é preenchido quando a forma de recebimento é
 * cheque de terceiro.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "baixa_conta_receber")
public class BaixaContaReceber extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_receber_id", nullable = false)
    private ContaReceber contaReceber;

    @Column(name = "data_baixa", nullable = false)
    private LocalDate dataBaixa;

    @Column(name = "valor_baixa", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorBaixa;

    /** Referência SOLTA pro {@code FormaPagamento} do cadastro-base-service. */
    @Column(name = "forma_pagamento_id")
    private Long formaPagamentoId;

    /** Conta bancária EFETIVA de onde o dinheiro entrou — ver Javadoc da classe. */
    @Column(name = "conta_bancaria_id")
    private Long contaBancariaId;

    @Column(name = "cheque_recebido_id")
    private Long chequeRecebidoId;

    @Builder.Default
    @Column(name = "valor_desconto", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(name = "observacao", length = 250)
    private String observacao;

    /** Quem registrou a baixa — referência SOLTA pro {@code Usuario} do seguranca-service. */
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
