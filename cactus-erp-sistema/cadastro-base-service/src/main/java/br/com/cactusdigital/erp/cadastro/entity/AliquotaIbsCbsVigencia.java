package br.com.cactusdigital.erp.cadastro.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

/**
 * Alíquotas do IBS (UF e município) e da CBS por ano de emissão, fixadas em lei (LC 214/2025, período de
 * transição) e validadas pela SEFAZ (NT 2025.002-RTC UB18-10/UB37-10/UB56-10). Só existem linhas para anos
 * com alíquota definida — ano sem linha faz a emissão falhar com mensagem, nunca usa alíquota inventada.
 */
@Getter
@NoArgsConstructor
@Entity
@Immutable
@Table(name = "aliquota_ibs_cbs_vigencia")
public class AliquotaIbsCbsVigencia {

    /** {@code Short} porque a coluna é SMALLINT (a validação de schema do Hibernate exige o tipo exato). */
    @Id
    @Column(name = "ano")
    private Short ano;

    @Column(name = "aliquota_ibs_uf", nullable = false, precision = 7, scale = 4)
    private BigDecimal aliquotaIbsUf;

    @Column(name = "aliquota_ibs_municipio", nullable = false, precision = 7, scale = 4)
    private BigDecimal aliquotaIbsMunicipio;

    @Column(name = "aliquota_cbs", nullable = false, precision = 7, scale = 4)
    private BigDecimal aliquotaCbs;

    @Column(name = "fundamento", nullable = false)
    private String fundamento;
}
