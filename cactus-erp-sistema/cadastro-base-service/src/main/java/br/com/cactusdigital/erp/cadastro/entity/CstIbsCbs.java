package br.com.cactusdigital.erp.cadastro.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

/**
 * CST do IBS/CBS — catálogo OFICIAL (tabela da SVRS, ver {@code V13__ibs_cbs_catalogos.sql}), só leitura:
 * muda por nova migration quando a tabela oficial muda, nunca pela API. Os indicadores dizem quais grupos
 * do XML o CST exige (redução, diferimento, monofásica...).
 */
@Getter
@NoArgsConstructor
@Entity
@Immutable
@Table(name = "cst_ibs_cbs")
public class CstIbsCbs {

    @Id
    @Column(name = "codigo", length = 3)
    private String codigo;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "exige_tributacao", nullable = false)
    private boolean exigeTributacao;

    @Column(name = "reducao_aliquota", nullable = false)
    private boolean reducaoAliquota;

    @Column(name = "reducao_base_calculo", nullable = false)
    private boolean reducaoBaseCalculo;

    @Column(name = "diferimento", nullable = false)
    private boolean diferimento;

    @Column(name = "monofasica", nullable = false)
    private boolean monofasica;

    @Column(name = "transferencia_credito", nullable = false)
    private boolean transferenciaCredito;

    @Column(name = "credito_presumido_zfm", nullable = false)
    private boolean creditoPresumidoZfm;

    @Column(name = "ajuste_competencia", nullable = false)
    private boolean ajusteCompetencia;

    @Column(name = "inicio_vigencia", nullable = false)
    private LocalDate inicioVigencia;

    @Column(name = "fim_vigencia")
    private LocalDate fimVigencia;
}
