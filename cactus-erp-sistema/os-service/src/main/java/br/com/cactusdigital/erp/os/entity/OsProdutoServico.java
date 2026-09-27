package br.com.cactusdigital.erp.os.entity;

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
 * Item de peça (produto) ou mão de obra (serviço) de uma {@link OsAbertura} — validado
 * contra {@code fenix.sql.OS_PRODUTO_SERVICO}. {@code produtoId} é referência SOLTA pro
 * cadastro-base-service, obrigatória só quando {@code tipo=PRODUTO} (mão de obra não
 * referencia produto, só {@code complemento} descrevendo o serviço).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "os_produto_servico")
public class OsProdutoServico extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "os_abertura_id", nullable = false)
    private OsAbertura osAbertura;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoItemOs tipo;

    @Column(name = "produto_id")
    private Long produtoId;

    @Column(name = "complemento", length = 250)
    private String complemento;

    @Column(name = "quantidade", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorUnitario;

    @Column(name = "valor_subtotal", precision = 18, scale = 6)
    private BigDecimal valorSubtotal;

    @Column(name = "taxa_desconto", precision = 18, scale = 6)
    private BigDecimal taxaDesconto;

    @Column(name = "valor_desconto", precision = 18, scale = 6)
    private BigDecimal valorDesconto;

    @Column(name = "valor_total", precision = 18, scale = 6)
    private BigDecimal valorTotal;
}
