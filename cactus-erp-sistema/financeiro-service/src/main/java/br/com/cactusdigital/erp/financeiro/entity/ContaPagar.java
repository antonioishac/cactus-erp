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
 * Título de contas a pagar — mesmo desenho de {@link ContaReceber}, espelhado pro lado
 * de fornecedor. Desde 2026-09-05, geração automática a partir do
 * {@code gestao-compras-service} também existe (1 título por parcela, quando um
 * {@code PedidoCompra} vira {@code RECEBIDO_TOTAL} — ver {@code ContaPagarService#gerarDeCompra}),
 * mesmo padrão da integração real que {@code ContaReceber} já tinha com
 * {@code vendas-service}. {@code MANUAL} continua sendo o único caminho de criação direta
 * pela API.
 * <p>
 * Campos adicionados em 2026-09-06 — ver Javadoc de {@link ContaReceber}, espelhado
 * aqui pro lado de fornecedor.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "conta_pagar")
public class ContaPagar extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "fornecedor_id", nullable = false)
    private Long fornecedorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "origem_tipo", nullable = false, length = 15)
    private OrigemContaPagar origemTipo;

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

    /** Conta bancária PREVISTA de pagamento — a conta EFETIVA (de onde o dinheiro de fato
     * saiu) fica em {@code BaixaContaPagar.contaBancariaId} e pode divergir desta. */
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
     * default de {@code ConfiguracaoFinanceiroEmpresa} (ver {@code ContaPagarService#mapComJurosMulta}). */
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
    @OneToMany(mappedBy = "contaPagar", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BaixaContaPagar> baixas = new ArrayList<>();
}
