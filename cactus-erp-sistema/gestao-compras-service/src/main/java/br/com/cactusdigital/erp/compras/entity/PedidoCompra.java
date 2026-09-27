package br.com.cactusdigital.erp.compras.entity;

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
import java.time.Instant;
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
 * Pedido de compra — pode nascer direto de uma {@link RequisicaoCompra}, de uma
 * {@link CotacaoCompra} (fornecedor vencedor) ou totalmente avulso ({@code origemTipo}).
 * Máquina de estados em {@code PedidoCompraService} (ver {@link SituacaoPedidoCompra}).
 * {@code aprovadorId}/{@code dataAprovacao} são setados automaticamente a partir do JWT
 * de quem aprova — nunca vêm do corpo da requisição (mesma disciplina de
 * {@code usuarioId} em {@code EstoqueMovimentoRequest}, no controle-estoque-service).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "pedido_compra")
public class PedidoCompra extends BaseEntity {

    /** Filial que está comprando — resolve qual {@code ConfiguracaoComprasEmpresa}
     * (limite de aprovação) se aplica a este pedido. */
    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoPedidoCompra tipo;

    @Column(name = "fornecedor_id", nullable = false)
    private Long fornecedorId;

    @Column(name = "colaborador_id", nullable = false)
    private Long colaboradorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "origem_tipo", nullable = false, length = 15)
    private OrigemPedidoCompra origemTipo;

    @Column(name = "origem_id")
    private Long origemId;

    @Column(name = "data_pedido")
    private LocalDate dataPedido;

    @Column(name = "data_prevista_entrega")
    private LocalDate dataPrevistaEntrega;

    @Column(name = "data_previsao_pagamento")
    private LocalDate dataPrevisaoPagamento;

    @Column(name = "local_entrega", length = 100)
    private String localEntrega;

    @Column(name = "local_cobranca", length = 100)
    private String localCobranca;

    @Column(name = "contato", length = 50)
    private String contato;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_frete", length = 3)
    private TipoFrete tipoFrete;

    @Enumerated(EnumType.STRING)
    @Column(name = "indicador_pagamento", length = 10)
    private IndicadorPagamentoNfe indicadorPagamento;

    @Column(name = "quantidade_parcelas")
    private Integer quantidadeParcelas;

    @Column(name = "dia_primeiro_vencimento")
    private Integer diaPrimeiroVencimento;

    @Column(name = "intervalo_entre_parcelas")
    private Integer intervaloEntreParcelas;

    @Column(name = "dia_fixo_parcela")
    private Integer diaFixoParcela;

    @Builder.Default
    @Column(name = "valor_subtotal", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorSubtotal = BigDecimal.ZERO;

    @Column(name = "taxa_desconto", precision = 18, scale = 6)
    private BigDecimal taxaDesconto;

    @Builder.Default
    @Column(name = "valor_desconto", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(name = "valor_frete", precision = 18, scale = 6)
    private BigDecimal valorFrete;

    @Column(name = "valor_seguro", precision = 18, scale = 6)
    private BigDecimal valorSeguro;

    @Column(name = "valor_outras_despesas", precision = 18, scale = 6)
    private BigDecimal valorOutrasDespesas;

    @Builder.Default
    @Column(name = "valor_total", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "aprovador_id")
    private Long aprovadorId;

    @Column(name = "data_aprovacao")
    private Instant dataAprovacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 25)
    private SituacaoPedidoCompra situacao;

    @Column(name = "observacao", length = 250)
    private String observacao;

    @Builder.Default
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PedidoCompraItem> itens = new ArrayList<>();
}
