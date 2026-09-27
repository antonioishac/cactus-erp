package br.com.cactusdigital.erp.loja.entity;

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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Pedido — nasce do checkout de um {@link Carrinho} ({@code AGUARDANDO_PAGAMENTO}).
 * Diferente de {@code Carrinho}, fica atrás do RBAC de sempre (
 * {@code LOJA_CONSULTAR}/{@code LOJA_GERENCIAR}) — consulta/gestão é trabalho de
 * staff/lojista. {@code confirmarPagamento} é ação manual/webhook-ready nesta fatia
 * (sem gateway real integrado, ver CLAUDE.md "Controle financeiro").
 * <p>
 * Endereço de entrega achatado direto aqui (sem entidade própria) — simplificação
 * desta fatia, suficiente pro volume esperado de um pedido de e-commerce simples.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "pedido")
public class Pedido extends BaseEntity {

    @Column(name = "loja_id", nullable = false)
    private Long lojaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_loja_id", nullable = false)
    private ClienteLoja clienteLoja;

    @Column(name = "carrinho_id")
    private Long carrinhoId;

    @Column(name = "logradouro_entrega", length = 150)
    private String logradouroEntrega;

    @Column(name = "numero_entrega", length = 20)
    private String numeroEntrega;

    @Column(name = "complemento_entrega", length = 60)
    private String complementoEntrega;

    @Column(name = "bairro_entrega", length = 80)
    private String bairroEntrega;

    @Column(name = "cep_entrega", length = 8)
    private String cepEntrega;

    @Column(name = "municipio_entrega", length = 80)
    private String municipioEntrega;

    @Column(name = "uf_entrega", length = 2)
    private String ufEntrega;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false, length = 15)
    private FormaPagamentoLoja formaPagamento;

    @Builder.Default
    @Column(name = "valor_subtotal", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorSubtotal = BigDecimal.ZERO;

    @Column(name = "valor_frete", precision = 18, scale = 6)
    private BigDecimal valorFrete;

    @Builder.Default
    @Column(name = "valor_total", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 20)
    private SituacaoPedido situacao = SituacaoPedido.AGUARDANDO_PAGAMENTO;

    @Column(name = "data_pagamento")
    private Instant dataPagamento;

    @Column(name = "motivo_cancelamento", length = 250)
    private String motivoCancelamento;

    @Builder.Default
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemPedido> itens = new ArrayList<>();
}
