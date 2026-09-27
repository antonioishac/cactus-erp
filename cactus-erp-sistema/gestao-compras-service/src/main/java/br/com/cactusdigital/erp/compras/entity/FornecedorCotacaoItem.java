package br.com.cactusdigital.erp.compras.entity;

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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "fornecedor_cotacao_item")
public class FornecedorCotacaoItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_cotacao_id", nullable = false)
    private FornecedorCotacao fornecedorCotacao;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(name = "quantidade", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorUnitario;

    @Column(name = "taxa_desconto", precision = 18, scale = 6)
    private BigDecimal taxaDesconto;

    @Column(name = "valor_desconto", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorDesconto;

    @Column(name = "valor_total", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTotal;
}
