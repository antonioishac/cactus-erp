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
 * Espelha {@code CHEQUE}+{@code FIN_CHEQUE_EMITIDO} do {@code fenix.sql}, achatado numa
 * entidade só — <b>simplificação deliberada</b>: {@code TALONARIO_CHEQUE} (controle de
 * numeração impressa do talão) não foi modelado, {@code numero} é texto livre. O valor de
 * negócio real (rastrear status de um cheque emitido pela empresa) não depende de
 * numeração de talão controlada.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "cheque_emitido")
public class ChequeEmitido extends BaseEntity {

    @Column(name = "conta_bancaria_id", nullable = false)
    private Long contaBancariaId;

    @Column(name = "numero", nullable = false, length = 20)
    private String numero;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "bom_para")
    private LocalDate bomPara;

    @Column(name = "valor", nullable = false, precision = 18, scale = 6)
    private BigDecimal valor;

    @Column(name = "nominal_a", length = 150)
    private String nominalA;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 15)
    private SituacaoChequeEmitido situacao = SituacaoChequeEmitido.EMITIDO;

    @Column(name = "data_situacao")
    private LocalDate dataSituacao;

    @Column(name = "motivo_situacao", length = 250)
    private String motivoSituacao;
}
