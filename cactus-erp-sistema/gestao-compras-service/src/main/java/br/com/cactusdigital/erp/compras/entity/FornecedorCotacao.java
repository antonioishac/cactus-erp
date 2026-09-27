package br.com.cactusdigital.erp.compras.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Um fornecedor concorrendo numa {@link CotacaoCompra} — {@code valorSubtotal}/
 * {@code valorTotal} são recalculados a cada item adicionado/removido
 * ({@code FornecedorCotacaoService}), nunca informados diretamente pelo cliente da API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "fornecedor_cotacao")
public class FornecedorCotacao extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cotacao_id", nullable = false)
    private CotacaoCompra cotacao;

    @Column(name = "fornecedor_id", nullable = false)
    private Long fornecedorId;

    @Column(name = "prazo_entrega", length = 50)
    private String prazoEntrega;

    @Column(name = "condicoes_pagamento", length = 50)
    private String condicoesPagamento;

    @Builder.Default
    @Column(name = "valor_subtotal", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorSubtotal = BigDecimal.ZERO;

    @Column(name = "taxa_desconto", precision = 18, scale = 6)
    private BigDecimal taxaDesconto;

    @Builder.Default
    @Column(name = "valor_desconto", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "valor_total", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Builder.Default
    @OneToMany(mappedBy = "fornecedorCotacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FornecedorCotacaoItem> itens = new ArrayList<>();
}
