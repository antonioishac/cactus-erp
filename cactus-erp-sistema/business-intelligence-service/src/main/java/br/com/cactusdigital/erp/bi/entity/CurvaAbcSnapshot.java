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

/** Snapshot histórico da Curva ABC — o {@code controle-estoque-service} só calcula sob
 * demanda ({@code GET /api/v1/relatorios/curva-abc}, não persiste); aqui persistimos por
 * data de referência pra permitir comparação ao longo do tempo. {@code classificacao} é
 * {@code String} (não enum) de propósito, espelhando o tipo do
 * {@code CurvaAbcItemResponse} de origem. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "curva_abc_snapshot")
public class CurvaAbcSnapshot extends BaseEntity {

    @Column(name = "data_referencia", nullable = false)
    private LocalDate dataReferencia;

    @Column(name = "deposito_id", nullable = false)
    private Long depositoId;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(name = "valor_movimentado", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorMovimentado;

    @Column(name = "percentual_participacao", nullable = false, precision = 9, scale = 4)
    private BigDecimal percentualParticipacao;

    @Column(name = "percentual_acumulado", nullable = false, precision = 9, scale = 4)
    private BigDecimal percentualAcumulado;

    @Column(name = "classificacao", nullable = false, length = 1)
    private String classificacao;
}
