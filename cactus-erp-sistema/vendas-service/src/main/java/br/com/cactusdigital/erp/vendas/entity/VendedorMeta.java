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
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Meta de venda do vendedor por cliente/período (AFV) — validado contra
 * {@code fenix.sql.VENDEDOR_META}. {@code clienteId} é referência SOLTA pro
 * cadastro-base-service. {@code metaRealizada} não é recalculada automaticamente pelo
 * confirmar-venda nesta fase (exigiria agregação por cliente/período a cada venda) —
 * fica como atualização manual/relatório futuro, registrado como simplificação.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "vendedor_meta")
public class VendedorMeta extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Vendedor vendedor;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodo_meta", length = 10)
    private PeriodoMeta periodoMeta;

    @Column(name = "meta_orcada", precision = 18, scale = 6)
    private BigDecimal metaOrcada;

    @Builder.Default
    @Column(name = "meta_realizada", nullable = false, precision = 18, scale = 6)
    private BigDecimal metaRealizada = BigDecimal.ZERO;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;
}
