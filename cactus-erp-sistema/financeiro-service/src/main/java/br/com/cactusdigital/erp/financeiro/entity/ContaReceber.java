package br.com.cactusdigital.erp.financeiro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Título de contas a receber — fecha o gap registrado desde a Fase 7:
 * {@code VendaCondicoesPagamento}/{@code VendaCondicoesParcela} (vendas-service)
 * modelavam a condição de pagamento da venda, mas não geravam nenhum lançamento de
 * contas a receber de verdade. {@code VendaService#confirmar} agora chama
 * {@code POST /contas-receber/gerar-de-venda} (novo client cross-serviço, mesmo padrão
 * de {@code EstoqueClient}) — 1 {@code ContaReceber} por parcela.
 * <p>
 * {@code empresaId}/{@code clienteId}/{@code origemId} são referências SOLTAS pro
 * cadastro-base-service/vendas-service/loja-virtual-service. {@code valorPago}/
 * {@code valorSaldo} são mantidos em sincronia a cada {@link BaixaContaReceber}
 * registrada (não recalculados a partir do zero a cada leitura, mesmo padrão de
 * {@code Pedido.valorTotal} recalculado em {@code CarrinhoService}).
 * <p>
 * Campos adicionados em 2026-09-06 fechando o mapeamento campo a campo contra
 * {@code FIN_LANCAMENTO_RECEBER}/{@code FIN_PARCELA_RECEBER} do {@code fenix.sql} (ver
 * CLAUDE.md, seção "Módulo Financeiro — Caixa/Bancos/Tesouraria/Conciliação/EDI"):
 * {@code naturezaFinanceiraId}/{@code tipoDocumentoId} (categorização, antes inexistente),
 * {@code contaBancariaId} (conta PREVISTA de recebimento — a conta EFETIVA fica em
 * {@code BaixaContaReceber.contaBancariaId}, podem divergir), {@code descontoAte}/
 * {@code percentualDesconto} (desconto por antecipação), {@code percentualMultaAtraso}/
 * {@code percentualJurosDiaAtraso} (override por título — {@code null} usa o default de
 * {@code ConfiguracaoFinanceiroEmpresa}), {@code loteId} (auto-referência — agrupa as N
 * parcelas nascidas do mesmo {@code gerarParcelado}; aponta pro id da primeira parcela do
 * lote, inclusive nela mesma; {@code null} = título avulso, não veio de parcelamento).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "conta_receber")
public class ContaReceber extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "origem_tipo", nullable = false, length = 15)
    private OrigemContaReceber origemTipo;

    @Column(name = "origem_id")
    private Long origemId;

    @Column(name = "numero_documento", length = 30)
    private String numeroDocumento;

    @Column(name = "descricao", length = 250)
    private String descricao;

    @Column(name = "natureza_financeira_id")
    private Long naturezaFinanceiraId;

    @Column(name = "tipo_documento_id")
    private Long tipoDocumentoId;

    /** Conta bancária PREVISTA de recebimento — a conta EFETIVA (onde o dinheiro de fato
     * entrou) fica em {@code BaixaContaReceber.contaBancariaId} e pode divergir desta. */
    @Column(name = "conta_bancaria_id")
    private Long contaBancariaId;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    /** Baixa registrada até esta data recebe {@code percentualDesconto} — nulo = sem
     * desconto por antecipação neste título. */
    @Column(name = "desconto_ate")
    private LocalDate descontoAte;

    @Column(name = "percentual_desconto", precision = 8, scale = 4)
    private BigDecimal percentualDesconto;

    /** Override por título do percentual de multa por atraso — {@code null} usa o
     * default de {@code ConfiguracaoFinanceiroEmpresa} (ver {@code ContaReceberService#mapComJurosMulta}). */
    @Column(name = "percentual_multa_atraso", precision = 8, scale = 4)
    private BigDecimal percentualMultaAtraso;

    @Column(name = "percentual_juros_dia_atraso", precision = 8, scale = 4)
    private BigDecimal percentualJurosDiaAtraso;

    /** Auto-referência: id da primeira parcela do lote gerado por {@code gerarParcelado}
     * (inclusive nela mesma). {@code null} = título avulso. */
    @Column(name = "lote_id")
    private Long loteId;

    @Column(name = "valor_original", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorOriginal;

    @Builder.Default
    @Column(name = "valor_pago", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorPago = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "valor_desconto", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(name = "valor_saldo", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorSaldo;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 12)
    private SituacaoTitulo situacao = SituacaoTitulo.ABERTA;

    @Column(name = "motivo_encerramento", length = 250)
    private String motivoEncerramento;

    @Builder.Default
    @OneToMany(mappedBy = "contaReceber", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BaixaContaReceber> baixas = new ArrayList<>();
}
