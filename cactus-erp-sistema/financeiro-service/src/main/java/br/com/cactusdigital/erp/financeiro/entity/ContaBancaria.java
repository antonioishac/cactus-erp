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
 * Espelha {@code BANCO_AGENCIA}+{@code BANCO_CONTA_CAIXA} do {@code fenix.sql}, achatado
 * numa entidade só (agência é 1:1 na prática de cada conta cadastrada — não há reuso real
 * de agência entre contas diferentes que justifique a tabela separada). {@code bancoId}
 * nulo quando {@link #getTipo()}{@code == CAIXA_INTERNO} (dinheiro em espécie, sem banco).
 * <p>
 * {@code cedenteNome}/{@code cedenteDocumento} são denormalizados aqui (não é uma chamada
 * ao {@code cadastro-base-service} pra resolver {@code Empresa}) — decisão deliberada pra
 * não criar uma dependência síncrona nova só pra isso; o operador financeiro preenche na
 * hora de cadastrar a conta, mesmo padrão de {@code EmpresaConfiguracaoFiscal.certificadoAlias}
 * (metadado local, não uma referência resolvida a outro serviço).
 * <p>
 * {@code saldoAtual} é a única fonte de saldo "corrente" da conta — nunca alterado por
 * {@code UPDATE} direto fora de {@code MovimentoCaixaBancoService#registrar}, que também
 * grava a linha correspondente no ledger ({@link MovimentoCaixaBanco}). Mesmo princípio
 * de {@code SaldoEstoque} no controle-estoque-service, só que sem a dimensão produto×depósito
 * (1 conta = 1 saldo, então o cache fica na própria entidade em vez de numa tabela agregada
 * à parte).
 * <p>
 * {@code codigoConvenioCobranca}/{@code codigoCedenteCobranca}/{@code carteiraCobranca}/
 * {@code proximoNossoNumero} (2026-09-07, EDI Bancário) são metadado do convênio de
 * cobrança desta conta no banco — preenchidos pelo operador financeiro a partir do
 * contrato de cobrança registrada, mesmo espírito de {@code cedenteNome}/{@code cedenteDocumento}
 * (metadado local, sem resolução automática). Usados por {@code Cnab240RemessaBuilder}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "conta_bancaria")
public class ContaBancaria extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "banco_id")
    private Long bancoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 15)
    private TipoContaBancaria tipo;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "agencia", length = 20)
    private String agencia;

    @Column(name = "agencia_digito", length = 2)
    private String agenciaDigito;

    @Column(name = "numero", length = 20)
    private String numero;

    @Column(name = "numero_digito", length = 2)
    private String numeroDigito;

    @Column(name = "cedente_nome", length = 150)
    private String cedenteNome;

    @Column(name = "cedente_documento", length = 20)
    private String cedenteDocumento;

    @Column(name = "codigo_convenio_cobranca", length = 20)
    private String codigoConvenioCobranca;

    @Column(name = "codigo_cedente_cobranca", length = 20)
    private String codigoCedenteCobranca;

    @Column(name = "carteira_cobranca", length = 5)
    private String carteiraCobranca;

    @Column(name = "proximo_nosso_numero")
    private Long proximoNossoNumero;

    @Column(name = "data_saldo_inicial", nullable = false)
    private LocalDate dataSaldoInicial;

    @Column(name = "saldo_inicial", nullable = false, precision = 18, scale = 6)
    private BigDecimal saldoInicial;

    @Column(name = "saldo_atual", nullable = false, precision = 18, scale = 6)
    private BigDecimal saldoAtual;

    /** Fase 6-B: preenchido só na conta {@code CAIXA_INTERNO} criada automaticamente pra um terminal
     * do PDV ({@code PDV_CAIXA.ID}, id local da loja) — uma conta por terminal, decisão confirmada com
     * o usuário em 2026-10-24. Único por empresa ({@code uk_conta_bancaria_pdv_caixa}). */
    @Column(name = "pdv_caixa_id")
    private Long pdvCaixaId;

    @Builder.Default
    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;
}
