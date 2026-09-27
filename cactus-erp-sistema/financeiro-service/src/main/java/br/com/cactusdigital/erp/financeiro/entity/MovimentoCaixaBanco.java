package br.com.cactusdigital.erp.financeiro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Ledger append-only de toda movimentação de {@link ContaBancaria} — nunca
 * {@code UPDATE}/{@code DELETE} fora de {@code MovimentoCaixaBancoService}. Mesmo
 * critério de {@code EstoqueMovimento}/{@code DocumentoVersao}: snapshot de
 * {@code saldoAnterior}/{@code saldoPosterior} a cada linha, corrigir é lançar uma nova
 * linha compensatória (ver {@code OrigemMovimentoCaixa#ESTORNO_BAIXA_RECEBER}/{@code _PAGAR}),
 * nunca reescrever uma já existente.
 * <p>
 * {@code conciliado} (2026-09-07, Conciliação Bancária) é a ÚNICA exceção ao "nunca
 * UPDATE" — não é uma mudança de valor/saldo, só um flag de auditoria (bateu ou não com
 * uma linha do extrato bancário real) mantido por {@code ConciliacaoBancariaService}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "movimento_caixa_banco")
public class MovimentoCaixaBanco extends BaseEntity {

    @Column(name = "conta_bancaria_id", nullable = false)
    private Long contaBancariaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimento", nullable = false, length = 25)
    private TipoMovimentoCaixa tipoMovimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "origem_tipo", nullable = false, length = 25)
    private OrigemMovimentoCaixa origemTipo;

    @Column(name = "origem_id")
    private Long origemId;

    @Column(name = "data_movimento", nullable = false)
    private LocalDate dataMovimento;

    @Column(name = "historico", length = 250)
    private String historico;

    @Column(name = "valor", nullable = false, precision = 18, scale = 6)
    private BigDecimal valor;

    @Column(name = "saldo_anterior", nullable = false, precision = 18, scale = 6)
    private BigDecimal saldoAnterior;

    @Column(name = "saldo_posterior", nullable = false, precision = 18, scale = 6)
    private BigDecimal saldoPosterior;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Builder.Default
    @Column(name = "conciliado", nullable = false)
    private boolean conciliado = false;
}
