package br.com.cactusdigital.erp.fiscal.entity;

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
 * Componente do valor do frete (validado contra {@code fenix.sql.CTE_COMPONENTE}) —
 * exemplos reais: "FRETE PESO", "FRETE VALOR", "SEC/CAT", "ADEME", "AGENDAMENTO". Soma
 * dos componentes compõe {@code CteCabecalho.valorTotalServico}, mas não é validado
 * automaticamente (responsabilidade de quem gera o CT-e).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "cte_componente")
public class CteComponente extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cte_cabecalho_id", nullable = false)
    private CteCabecalho cteCabecalho;

    @Column(name = "nome", nullable = false, length = 15)
    private String nome;

    @Column(name = "valor", nullable = false, precision = 18, scale = 6)
    private BigDecimal valor;
}
