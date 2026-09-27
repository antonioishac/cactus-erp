package br.com.cactusdigital.erp.vendas.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Meta de comissão por produto dentro de um {@link ComissaoPerfil} — validado contra
 * {@code fenix.sql.COMISSAO_OBJETIVO}. Quando existe um objetivo pro produto vendido, a
 * taxa/valor daqui prevalece sobre o {@code Vendedor.comissaoPercentual} genérico (ver
 * {@code VendaService#gerarComissao}). {@code produtoId} é referência SOLTA pro
 * cadastro-base-service.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "comissao_objetivo")
public class ComissaoObjetivo extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comissao_perfil_id", nullable = false)
    private ComissaoPerfil comissaoPerfil;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(name = "codigo", length = 3)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "descricao", length = 250)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", length = 10)
    private TipoPagamentoVenda formaPagamento;

    @Column(name = "taxa_pagamento", precision = 18, scale = 6)
    private BigDecimal taxaPagamento;

    @Column(name = "valor_pagamento", precision = 18, scale = 6)
    private BigDecimal valorPagamento;

    @Column(name = "valor_meta", precision = 18, scale = 6)
    private BigDecimal valorMeta;

    @Column(name = "quantidade", precision = 18, scale = 6)
    private BigDecimal quantidade;
}
