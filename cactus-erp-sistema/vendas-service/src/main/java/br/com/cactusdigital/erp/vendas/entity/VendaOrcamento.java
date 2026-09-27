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
 * Orçamento/pedido de venda — validado contra {@code fenix.sql.VENDA_ORCAMENTO_CABECALHO}.
 * {@code clienteId}/{@code transportadoraId} são referências SOLTAS pro
 * cadastro-base-service. Máquina de estados em {@code VendaOrcamentoService} (ver
 * {@link SituacaoOrcamento} — melhoria sobre o fenix, que não tinha status explícito).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "venda_orcamento")
public class VendaOrcamento extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condicoes_pagamento_id", nullable = false)
    private VendaCondicoesPagamento condicoesPagamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Vendedor vendedor;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "transportadora_id")
    private Long transportadoraId;

    @Column(name = "codigo", length = 20)
    private String codigo;

    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;

    @Column(name = "data_entrega")
    private LocalDate dataEntrega;

    @Column(name = "validade")
    private LocalDate validade;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_frete", length = 3)
    private TipoFrete tipoFrete;

    @Builder.Default
    @Column(name = "valor_subtotal", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorSubtotal = BigDecimal.ZERO;

    @Column(name = "valor_frete", precision = 18, scale = 6)
    private BigDecimal valorFrete;

    @Column(name = "taxa_comissao", precision = 18, scale = 6)
    private BigDecimal taxaComissao;

    @Column(name = "valor_comissao", precision = 18, scale = 6)
    private BigDecimal valorComissao;

    @Column(name = "taxa_desconto", precision = 18, scale = 6)
    private BigDecimal taxaDesconto;

    @Builder.Default
    @Column(name = "valor_desconto", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "valor_total", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "observacao", length = 250)
    private String observacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 15)
    private SituacaoOrcamento situacao;

    @Builder.Default
    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VendaOrcamentoItem> itens = new ArrayList<>();
}
