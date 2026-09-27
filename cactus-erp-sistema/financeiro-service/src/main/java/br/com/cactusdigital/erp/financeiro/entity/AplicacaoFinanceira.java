package br.com.cactusdigital.erp.financeiro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Aplicação financeira de curto prazo (CDB/fundo/Tesouro Direto/poupança) — debita
 * {@code contaBancariaId} em {@link #aplicar}, credita de volta em {@link #resgatar}.
 * {@code rendimento} (pode ser negativo — fundo/Tesouro Direto marcado a mercado podem
 * perder valor, não só CDB/poupança que só rendem) é calculado só no resgate
 * ({@code valorResgatado - valorAplicado}), nunca antes — sem cotação diária/marcação a
 * mercado nesta fase, é histórico simples de aplicação→resgate.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "aplicacao_financeira")
public class AplicacaoFinanceira extends BaseEntity {

    @Column(name = "conta_bancaria_id", nullable = false)
    private Long contaBancariaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_aplicacao", nullable = false, length = 15)
    private TipoAplicacaoFinanceira tipoAplicacao;

    @Column(name = "descricao", length = 150)
    private String descricao;

    @Column(name = "data_aplicacao", nullable = false)
    private LocalDate dataAplicacao;

    @Column(name = "valor_aplicado", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorAplicado;

    @Column(name = "data_resgate")
    private LocalDate dataResgate;

    @Column(name = "valor_resgatado", precision = 18, scale = 6)
    private BigDecimal valorResgatado;

    @Column(name = "rendimento", precision = 18, scale = 6)
    private BigDecimal rendimento;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 10)
    private SituacaoAplicacaoFinanceira situacao = SituacaoAplicacaoFinanceira.ATIVA;
}
