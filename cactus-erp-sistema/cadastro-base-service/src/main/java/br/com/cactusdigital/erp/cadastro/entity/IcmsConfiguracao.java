package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * ICMS por UF de destino — 1 linha por estado dentro de uma {@link ConfiguracaoTributaria}.
 * Validado contra {@code fenix.sql.TRIBUT_ICMS_UF}. {@code cstIcms} (Regime Normal) e
 * {@code csosn} (Simples Nacional) são mutuamente exclusivos — qual dos dois vale
 * depende do CRT da empresa vendedora (ver {@code EmpresaRegimeTributario}); a
 * resolução de qual usar é regra de negócio, não de banco.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "icms_configuracao",
        uniqueConstraints = @UniqueConstraint(name = "uk_icms_config_uf", columnNames = {"configuracao_tributaria_id", "uf_destino"}))
public class IcmsConfiguracao extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "configuracao_tributaria_id", nullable = false)
    private ConfiguracaoTributaria configuracaoTributaria;

    @Column(name = "uf_destino", nullable = false, length = 2)
    private String ufDestino;

    @Enumerated(EnumType.STRING)
    @Column(name = "cst_icms", length = 10)
    private CstIcms cstIcms;

    @Enumerated(EnumType.STRING)
    @Column(name = "csosn", length = 10)
    private Csosn csosn;

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidade_base_calculo", length = 30)
    private ModalidadeBaseCalculoIcms modalidadeBaseCalculo;

    @Column(name = "aliquota", precision = 18, scale = 6)
    private BigDecimal aliquota;

    @Column(name = "valor_pauta", precision = 18, scale = 6)
    private BigDecimal valorPauta;

    @Column(name = "valor_preco_maximo", precision = 18, scale = 6)
    private BigDecimal valorPrecoMaximo;

    @Column(name = "mva", precision = 18, scale = 6)
    private BigDecimal mva;

    @Column(name = "porcento_base_calculo", precision = 18, scale = 6)
    private BigDecimal porcentoBaseCalculo;

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidade_base_calculo_st", length = 30)
    private ModalidadeBaseCalculoIcmsSt modalidadeBaseCalculoSt;

    @Column(name = "aliquota_interna_st", precision = 18, scale = 6)
    private BigDecimal aliquotaInternaSt;

    @Column(name = "aliquota_interestadual_st", precision = 18, scale = 6)
    private BigDecimal aliquotaInterestadualSt;

    @Column(name = "porcento_base_calculo_st", precision = 18, scale = 6)
    private BigDecimal porcentoBaseCalculoSt;

    @Column(name = "aliquota_icms_st", precision = 18, scale = 6)
    private BigDecimal aliquotaIcmsSt;

    @Column(name = "valor_pauta_st", precision = 18, scale = 6)
    private BigDecimal valorPautaSt;

    @Column(name = "valor_preco_maximo_st", precision = 18, scale = 6)
    private BigDecimal valorPrecoMaximoSt;
}
