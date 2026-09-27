package br.com.cactusdigital.erp.vendas.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Roteiro de visita do vendedor (AFV — Força de Vendas) — validado contra
 * {@code fenix.sql.VENDEDOR_ROTA}, que é raso (só {@code posicao}). Mercado real de AFV
 * tem check-in geolocalizado/pedido offline/foto do ponto de venda — registrado como
 * enriquecimento futuro no roadmap (CLAUDE.md), não bloqueia esta fase. {@code clienteId}
 * é referência SOLTA pro cadastro-base-service.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "vendedor_rota")
public class VendedorRota extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Vendedor vendedor;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    /** Ordem de visita dentro da rota do vendedor. */
    @Column(name = "posicao")
    private Integer posicao;
}
