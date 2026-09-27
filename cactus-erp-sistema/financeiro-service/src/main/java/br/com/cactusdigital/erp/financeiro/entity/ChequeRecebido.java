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
 * Espelha {@code FIN_CHEQUE_RECEBIDO} do {@code fenix.sql} — cheque de terceiro recebido
 * como forma de pagamento numa baixa de {@link ContaReceber}. {@code clienteId} é
 * referência solta pro cadastro-base-service (opcional — cheque pode vir de alguém não
 * cadastrado como Cliente, mesmo critério do fenix: {@code ID_PESSOA} nulo é aceito, os
 * dados do emitente ficam denormalizados aqui). Dados bancários do EMITENTE do cheque
 * (banco/agência/conta) são texto livre — não é uma {@link ContaBancaria} nossa, é de
 * terceiro, não faz sentido cadastrar como tal.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "cheque_recebido")
public class ChequeRecebido extends BaseEntity {

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "cpf_cnpj", length = 20)
    private String cpfCnpj;

    @Column(name = "nome_emitente", nullable = false, length = 150)
    private String nomeEmitente;

    @Column(name = "banco_codigo", length = 3)
    private String bancoCodigo;

    @Column(name = "agencia", length = 20)
    private String agencia;

    @Column(name = "conta", length = 20)
    private String conta;

    @Column(name = "numero", length = 20)
    private String numero;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "bom_para")
    private LocalDate bomPara;

    @Column(name = "valor", nullable = false, precision = 18, scale = 6)
    private BigDecimal valor;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 15)
    private SituacaoChequeRecebido situacao = SituacaoChequeRecebido.EM_CARTEIRA;

    @Column(name = "data_situacao")
    private LocalDate dataSituacao;

    @Column(name = "motivo_situacao", length = 250)
    private String motivoSituacao;

    /** Conta bancária de depósito/desconto — preenchida em {@code depositar()}/
     * {@code descontar()}, usada em {@code compensar()} pra saber onde lançar a
     * {@code ENTRADA}. {@code null} enquanto {@code situacao == EM_CARTEIRA}. */
    @Column(name = "conta_bancaria_id")
    private Long contaBancariaId;
}
