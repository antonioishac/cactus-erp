package br.com.cactusdigital.erp.cadastro.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

/**
 * Código de Classificação Tributária do IBS/CBS ({@code cClassTrib}) — catálogo OFICIAL, só leitura (ver
 * {@link CstIbsCbs}). Os 3 primeiros dígitos são o CST. {@code percentualReducao*} é a redução de alíquota
 * fixada em lei para a classificação (vira {@code pRedAliq} no XML).
 */
@Getter
@NoArgsConstructor
@Entity
@Immutable
@Table(name = "classificacao_tributaria_ibs_cbs")
public class ClassificacaoTributariaIbsCbs {

    @Id
    @Column(name = "codigo", length = 6)
    private String codigo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cst", nullable = false)
    private CstIbsCbs cst;

    @Column(name = "descricao", nullable = false, length = 1000)
    private String descricao;

    @Column(name = "nome_reduzido", length = 150)
    private String nomeReduzido;

    @Column(name = "tributacao_regular", nullable = false)
    private boolean tributacaoRegular;

    @Column(name = "permite_credito_presumido", nullable = false)
    private boolean permiteCreditoPresumido;

    @Column(name = "percentual_reducao_ibs", nullable = false, precision = 7, scale = 4)
    private BigDecimal percentualReducaoIbs;

    @Column(name = "percentual_reducao_cbs", nullable = false, precision = 7, scale = 4)
    private BigDecimal percentualReducaoCbs;

    /** {@code Byte} porque a coluna é TINYINT (a validação de schema do Hibernate exige o tipo exato). */
    @Column(name = "tipo_aliquota", nullable = false)
    private Byte tipoAliquota;

    @Column(name = "aceita_nfe", nullable = false)
    private boolean aceitaNfe;

    @Column(name = "aceita_nfce", nullable = false)
    private boolean aceitaNfce;

    @Column(name = "url_legislacao")
    private String urlLegislacao;

    @Column(name = "data_publicacao", nullable = false)
    private LocalDate dataPublicacao;

    @Column(name = "inicio_vigencia", nullable = false)
    private LocalDate inicioVigencia;

    @Column(name = "fim_vigencia")
    private LocalDate fimVigencia;

    public boolean vigenteEm(LocalDate data) {
        return !data.isBefore(inicioVigencia) && (fimVigencia == null || data.isBefore(fimVigencia));
    }
}
