package br.com.cactusdigital.erp.bi.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Snapshot do valor de estoque por produto×depósito numa data de referência — gerado a
 * partir de {@code saldo_estoque} (JDBC direto no controle-estoque-service). Idempotente
 * por {@code (dataReferencia, depositoId, produtoId)}: o ETL deleta e reinsere a mesma
 * referência a cada execução, reprocessável sem duplicar. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "estoque_valor_snapshot")
public class EstoqueValorSnapshot extends BaseEntity {

    @Column(name = "data_referencia", nullable = false)
    private LocalDate dataReferencia;

    @Column(name = "deposito_id", nullable = false)
    private Long depositoId;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(name = "quantidade", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidade;

    @Column(name = "custo_medio_ponderado", nullable = false, precision = 18, scale = 6)
    private BigDecimal custoMedioPonderado;

    @Column(name = "valor_total", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTotal;
}
