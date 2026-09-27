package br.com.cactusdigital.erp.vendas.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
 * Condição de pagamento (ex: "BOLETO 30/60/90") — validado contra
 * {@code fenix.sql.VENDA_CONDICOES_PAGAMENTO}, agregado com {@link VendaCondicoesParcela}
 * (mesmo padrão de {@code Produto.faixasPreco}: lista, {@code orphanRemoval}, substituída
 * inteira a cada atualização — ver {@code VendaCondicoesPagamentoService}).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "venda_condicoes_pagamento")
public class VendaCondicoesPagamento extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Column(name = "descricao", length = 250)
    private String descricao;

    @Column(name = "faturamento_minimo", precision = 18, scale = 6)
    private BigDecimal faturamentoMinimo;

    @Column(name = "faturamento_maximo", precision = 18, scale = 6)
    private BigDecimal faturamentoMaximo;

    @Column(name = "indice_correcao", precision = 18, scale = 6)
    private BigDecimal indiceCorrecao;

    @Column(name = "dias_tolerancia")
    private Integer diasTolerancia;

    @Column(name = "valor_tolerancia", precision = 18, scale = 6)
    private BigDecimal valorTolerancia;

    @Column(name = "prazo_medio")
    private Integer prazoMedio;

    @Enumerated(EnumType.STRING)
    @Column(name = "vista_prazo", length = 10)
    private TipoPagamentoVenda vistaPrazo;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;

    @Builder.Default
    @OneToMany(mappedBy = "condicoesPagamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VendaCondicoesParcela> parcelas = new ArrayList<>();
}
