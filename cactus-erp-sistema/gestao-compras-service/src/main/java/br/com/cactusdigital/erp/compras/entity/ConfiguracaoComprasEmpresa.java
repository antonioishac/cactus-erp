package br.com.cactusdigital.erp.compras.entity;

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

/** {@code limiteAprovacaoPedido} nulo = sem exigência de aprovação (semântica
 * permissiva, mesmo padrão de {@code ConfiguracaoEstoqueEmpresa} no
 * controle-estoque-service — sem config, nenhum pedido exige aprovação). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "configuracao_compras_empresa")
public class ConfiguracaoComprasEmpresa extends BaseEntity {

    @Column(name = "empresa_id", nullable = false, unique = true)
    private Long empresaId;

    @Column(name = "limite_aprovacao_pedido", precision = 18, scale = 6)
    private BigDecimal limiteAprovacaoPedido;
}
