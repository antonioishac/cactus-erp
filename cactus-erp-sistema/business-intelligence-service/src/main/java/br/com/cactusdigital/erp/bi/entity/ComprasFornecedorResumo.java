package br.com.cactusdigital.erp.bi.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Resumo mensal de compras por fornecedor — agregado de {@code pedido_compra} (JDBC
 * direto no gestao-compras-service), considerando pedidos ENVIADO/PARCIALMENTE_RECEBIDO/
 * RECEBIDO_TOTAL (exclui RASCUNHO/AGUARDANDO_APROVACAO/CANCELADO). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "compras_fornecedor_resumo")
public class ComprasFornecedorResumo extends BaseEntity {

    @Column(name = "ano_mes", nullable = false, length = 7)
    private String anoMes;

    @Column(name = "fornecedor_id", nullable = false)
    private Long fornecedorId;

    @Column(name = "quantidade_pedidos", nullable = false)
    private Integer quantidadePedidos;

    @Column(name = "valor_total_comprado", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTotalComprado;
}
