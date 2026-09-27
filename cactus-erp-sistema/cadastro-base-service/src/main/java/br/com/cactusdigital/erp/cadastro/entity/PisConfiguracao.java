package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Validado contra {@code fenix.sql.TRIBUT_PIS} — 1:1 com {@link ConfiguracaoTributaria}. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "pis_configuracao")
public class PisConfiguracao extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "configuracao_tributaria_id", nullable = false, unique = true)
    private ConfiguracaoTributaria configuracaoTributaria;

    @Enumerated(EnumType.STRING)
    @Column(name = "cst", length = 10)
    private CstPisCofins cst;

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidade_base_calculo", length = 20)
    private ModalidadeBaseCalculo modalidadeBaseCalculo;

    @Column(name = "porcento_base_calculo", precision = 18, scale = 6)
    private BigDecimal porcentoBaseCalculo;

    @Column(name = "aliquota_percentual", precision = 18, scale = 6)
    private BigDecimal aliquotaPercentual;

    @Column(name = "aliquota_unidade", precision = 18, scale = 6)
    private BigDecimal aliquotaUnidade;

    @Column(name = "valor_preco_maximo", precision = 18, scale = 6)
    private BigDecimal valorPrecoMaximo;

    @Column(name = "valor_pauta_fiscal", precision = 18, scale = 6)
    private BigDecimal valorPautaFiscal;
}
