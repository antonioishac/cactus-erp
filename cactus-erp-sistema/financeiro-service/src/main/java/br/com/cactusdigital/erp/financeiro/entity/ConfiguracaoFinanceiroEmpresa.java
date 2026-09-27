package br.com.cactusdigital.erp.financeiro.entity;

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

/**
 * Política de juros/multa por atraso, por empresa — {@code percentualMultaAtraso}/
 * {@code percentualJurosDiaAtraso} nulos = sem cobrança (semântica permissiva, mesmo
 * padrão de {@code ConfiguracaoComprasEmpresa.limiteAprovacaoPedido} no
 * gestao-compras-service: sem config, nenhum título é penalizado por atraso).
 * {@code percentualMultaAtraso} é aplicado uma vez sobre {@code valorSaldo} assim que o
 * título vence; {@code percentualJurosDiaAtraso} é aplicado por dia de atraso, também
 * sobre {@code valorSaldo} — ver {@code FinanceiroMapper}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "configuracao_financeiro_empresa")
public class ConfiguracaoFinanceiroEmpresa extends BaseEntity {

    @Column(name = "empresa_id", nullable = false, unique = true)
    private Long empresaId;

    @Column(name = "percentual_multa_atraso", precision = 8, scale = 4)
    private BigDecimal percentualMultaAtraso;

    @Column(name = "percentual_juros_dia_atraso", precision = 8, scale = 4)
    private BigDecimal percentualJurosDiaAtraso;
}
