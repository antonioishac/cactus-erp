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

/** Validado contra {@code fenix.sql.TRIBUT_ISS} — 1:1 direto com {@link OperacaoFiscal} (serviço, não mercadoria). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "iss_configuracao")
public class IssConfiguracao extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operacao_fiscal_id", nullable = false, unique = true)
    private OperacaoFiscal operacaoFiscal;

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidade_base_calculo", length = 20)
    private ModalidadeBaseCalculoIss modalidadeBaseCalculo;

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

    @Column(name = "item_lista_servico")
    private Integer itemListaServico;

    @Enumerated(EnumType.STRING)
    @Column(name = "codigo_tributacao", length = 20)
    private CodigoTributacaoIss codigoTributacao;
}
