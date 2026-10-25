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

/** Faturamento de um dia por empresa × canal — agregado de {@code venda_cabecalho}
 * (B2B, {@code CONFIRMADA}) e {@code venda_pdv} (PDV, {@code FECHADA}, líquida de
 * devolução), JDBC direto no vendas-service. Mesmo critério dos relatórios de vendas. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "faturamento_diario")
public class FaturamentoDiario extends BaseEntity {

    @Column(name = "data_referencia", nullable = false)
    private LocalDate dataReferencia;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "canal", nullable = false, length = 3)
    private String canal;

    @Column(name = "quantidade_vendas", nullable = false)
    private Integer quantidadeVendas;

    @Column(name = "valor_bruto", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorBruto;

    @Column(name = "valor_devolvido", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorDevolvido;

    @Column(name = "valor_liquido", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorLiquido;
}
