package br.com.cactusdigital.erp.vendas.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Uma parcela dentro de {@link VendaCondicoesPagamento} — validado contra
 * {@code fenix.sql.VENDA_CONDICOES_PARCELAS}. {@code taxa} é uma taxa de juros/correção
 * aplicável à parcela (não a fração do valor total que ela representa — todo dado real
 * já seedado neste monorepo tem {@code taxa=0}, o que não faria sentido se fosse peso de
 * distribuição), por isso {@code VendaService#gerarContasReceber} divide o valor da
 * venda IGUALMENTE entre as parcelas, não proporcionalmente a {@code taxa} — simplificação
 * deliberada, registrada, não uma leitura arriscada do campo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "venda_condicoes_parcela")
public class VendaCondicoesParcela extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condicoes_pagamento_id", nullable = false)
    private VendaCondicoesPagamento condicoesPagamento;

    @Column(name = "parcela", nullable = false)
    private Integer parcela;

    @Column(name = "dias")
    private Integer dias;

    @Column(name = "taxa", precision = 18, scale = 6)
    private BigDecimal taxa;
}
