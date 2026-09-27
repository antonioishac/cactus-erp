package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * A célula da matriz {@link GrupoTributario} × {@link OperacaoFiscal} — validado
 * contra {@code fenix.sql.TRIBUT_CONFIGURA_OF_GT}. É aqui que penduram as regras de
 * fato: {@link IcmsConfiguracao} (1:N, uma por UF de destino), {@link PisConfiguracao}/
 * {@link CofinsConfiguracao}/{@link IpiConfiguracao} (1:1 cada). Gerenciados como um
 * agregado só (igual Pessoa+Endereco) — sem controller próprio pros filhos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "configuracao_tributaria",
        uniqueConstraints = @UniqueConstraint(name = "uk_config_trib_grupo_operacao", columnNames = {"grupo_tributario_id", "operacao_fiscal_id"}))
public class ConfiguracaoTributaria extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_tributario_id", nullable = false)
    private GrupoTributario grupoTributario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operacao_fiscal_id", nullable = false)
    private OperacaoFiscal operacaoFiscal;

    @Builder.Default
    @OneToMany(mappedBy = "configuracaoTributaria", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<IcmsConfiguracao> icmsPorUf = new ArrayList<>();

    @OneToOne(mappedBy = "configuracaoTributaria", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PisConfiguracao pis;

    @OneToOne(mappedBy = "configuracaoTributaria", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CofinsConfiguracao cofins;

    @OneToOne(mappedBy = "configuracaoTributaria", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private IpiConfiguracao ipi;
}
