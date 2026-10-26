package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Regra do IBS/CBS da célula grupo tributário × operação fiscal (1:1, mesmo papel de {@link PisConfiguracao}):
 * só a classificação tributária. CST, reduções e indicadores vêm do catálogo oficial; as alíquotas vêm da
 * vigência por ano ({@link AliquotaIbsCbsVigencia}) — nada disso é digitado pelo usuário.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "ibs_cbs_configuracao")
public class IbsCbsConfiguracao extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "configuracao_tributaria_id", nullable = false)
    private ConfiguracaoTributaria configuracaoTributaria;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "classificacao_tributaria", nullable = false)
    private ClassificacaoTributariaIbsCbs classificacaoTributaria;
}
