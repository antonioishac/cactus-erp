package br.com.cactusdigital.erp.estoque.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Uma linha por empresa. {@code limiteAprovacaoAjuste}: acima desse valor (quantidade ×
 * custo unitário de um {@code AJUSTE}/{@code PERDA}), {@link EstoqueMovimento#getAprovadorId()}
 * deixa de ser opcional — mesmo padrão "aprovação de superior acima de limiar" da
 * ANÁLISE: HIERARQUIA DE USUÁRIOS do CLAUDE.md.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "configuracao_estoque_empresa")
public class ConfiguracaoEstoqueEmpresa extends BaseEntity {

    @Column(name = "empresa_id", nullable = false, unique = true)
    private Long empresaId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_custeio", nullable = false, length = 20)
    private MetodoCusteio metodoCusteio = MetodoCusteio.MEDIA_PONDERADA;

    @Builder.Default
    @Column(name = "limite_aprovacao_ajuste", nullable = false, precision = 18, scale = 6)
    private BigDecimal limiteAprovacaoAjuste = BigDecimal.ZERO;
}
