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
 * Uma forma de pagamento de {@link VendaPdv} ({@code PDV_TOTAL_TIPO_PAGAMENTO}) — pagamento fracionado:
 * N por venda. {@code codigoPagamentoNfce} é o {@code tPag} oficial da SEFAZ (01=dinheiro), é por ele
 * que o dinheiro da gaveta é identificado, não pelo id da forma (que muda de cliente pra cliente).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "venda_pdv_pagamento")
public class VendaPdvPagamento extends BaseEntity {

    public static final String TPAG_DINHEIRO = "01";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_pdv_id", nullable = false)
    private VendaPdv venda;

    @Column(name = "forma_pagamento_id")
    private Long formaPagamentoId;

    @Column(name = "codigo_pagamento_nfce", nullable = false, length = 2)
    private String codigoPagamentoNfce;

    @Column(name = "descricao", length = 30)
    private String descricao;

    @Column(name = "valor", nullable = false, precision = 18, scale = 6)
    private BigDecimal valor;

    @Column(name = "nsu", length = 30)
    private String nsu;

    @Column(name = "rede", length = 10)
    private String rede;

    @Column(name = "cartao_debito_credito", length = 1)
    private String cartaoDebitoCredito;

    @Builder.Default
    @Column(name = "estornado", nullable = false)
    private boolean estornado = false;
}
