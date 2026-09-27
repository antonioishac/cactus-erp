package br.com.cactusdigital.erp.financeiro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Transferência entre duas {@link ContaBancaria} da própria empresa — <b>simplificação
 * deliberada</b> sobre o padrão "em trânsito" de {@code TransferenciaEstoque}
 * (controle-estoque-service): dinheiro entre contas que já controlamos é debitado da
 * origem e creditado no destino no MESMO instante (sem estado intermediário), diferente
 * de mercadoria física que leva tempo pra chegar — não modelamos liquidação bancária real
 * (TED T+1 etc.) porque não há integração bancária de verdade por trás disso ainda (ver
 * EDI Bancário, fora de escopo). Gera 2 {@code MovimentoCaixaBanco} atomicamente (mesma
 * transação): {@code TRANSFERENCIA_SAIDA} na origem, {@code TRANSFERENCIA_ENTRADA} no
 * destino, ambos com {@code origemTipo=TRANSFERENCIA}/{@code origemId=this.id}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "transferencia_bancaria")
public class TransferenciaBancaria extends BaseEntity {

    @Column(name = "conta_origem_id", nullable = false)
    private Long contaOrigemId;

    @Column(name = "conta_destino_id", nullable = false)
    private Long contaDestinoId;

    @Column(name = "data_transferencia", nullable = false)
    private LocalDate dataTransferencia;

    @Column(name = "valor", nullable = false, precision = 18, scale = 6)
    private BigDecimal valor;

    @Column(name = "historico", length = 250)
    private String historico;

    @Column(name = "usuario_id")
    private Long usuarioId;
}
