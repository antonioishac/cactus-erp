package br.com.cactusdigital.erp.vendas.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
 * Venda — validado contra {@code fenix.sql.VENDA_CABECALHO}. Duas melhorias
 * deliberadas sobre o fenix (ver CLAUDE.md, seção do Módulo Comercial):
 * <ul>
 *   <li>{@code formaPagamentoId} referencia o {@code FormaPagamento} já existente no
 *   cadastro-base (Fase 1c) em vez do {@code CHAR(1)} solto do fenix;</li>
 *   <li>{@code situacao} (RASCUNHO/CONFIRMADA/CANCELADA) explícita — o fenix não tem
 *   status na venda, só implícito (venda "existe" = confirmada). Máquina de estados em
 *   {@code VendaService}.</li>
 * </ul>
 * {@code clienteId}/{@code transportadoraId} são referências SOLTAS pro cadastro-base-service.
 * {@code vendaOrcamentoId} é FK de verdade (mesmo schema), opcional — venda pode nascer avulsa.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "venda_cabecalho")
public class VendaCabecalho extends BaseEntity {

    /** Filial que está vendendo — referência SOLTA pro {@code Empresa} do cadastro-base-service.
     * Mesmo padrão de {@code PedidoCompra.empresaId} (gestao-compras-service). Necessário
     * pra Fase 8 (fiscal-service) resolver qual {@code EmpresaConfiguracaoFiscal}
     * (certificado/série/numeração/ambiente) usar na emissão. */
    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_orcamento_id")
    private VendaOrcamento orcamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condicoes_pagamento_id", nullable = false)
    private VendaCondicoesPagamento condicoesPagamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nota_fiscal_tipo_id", nullable = false)
    private NotaFiscalTipo notaFiscalTipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Vendedor vendedor;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "transportadora_id")
    private Long transportadoraId;

    /** Referência SOLTA pro {@code FormaPagamento} do cadastro-base (Fase 1c) — substitui o {@code CHAR(1)} solto do fenix. */
    @Column(name = "forma_pagamento_id", nullable = false)
    private Long formaPagamentoId;

    @Column(name = "data_venda")
    private LocalDate dataVenda;

    @Column(name = "data_saida")
    private LocalDate dataSaida;

    @Column(name = "hora_saida", length = 8)
    private String horaSaida;

    @Column(name = "numero_fatura")
    private Integer numeroFatura;

    @Column(name = "local_entrega", length = 100)
    private String localEntrega;

    @Column(name = "local_cobranca", length = 100)
    private String localCobranca;

    @Builder.Default
    @Column(name = "valor_subtotal", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorSubtotal = BigDecimal.ZERO;

    @Column(name = "taxa_comissao", precision = 18, scale = 6)
    private BigDecimal taxaComissao;

    @Builder.Default
    @Column(name = "valor_comissao", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorComissao = BigDecimal.ZERO;

    @Column(name = "taxa_desconto", precision = 18, scale = 6)
    private BigDecimal taxaDesconto;

    @Builder.Default
    @Column(name = "valor_desconto", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "valor_total", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_frete", length = 3)
    private TipoFrete tipoFrete;

    @Column(name = "valor_frete", precision = 18, scale = 6)
    private BigDecimal valorFrete;

    @Column(name = "valor_seguro", precision = 18, scale = 6)
    private BigDecimal valorSeguro;

    @Column(name = "dia_fixo_parcela")
    private Integer diaFixoParcela;

    @Column(name = "observacao", length = 250)
    private String observacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 15)
    private SituacaoVenda situacao;

    @Builder.Default
    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VendaItem> itens = new ArrayList<>();
}
