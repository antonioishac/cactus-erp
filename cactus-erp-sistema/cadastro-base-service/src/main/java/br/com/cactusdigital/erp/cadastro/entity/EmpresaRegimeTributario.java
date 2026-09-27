package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Histórico de regime tributário por ano-calendário — {@code Empresa.crt} sozinho não
 * basta (CRT pode mudar de um ano pro outro, ex: saiu do Simples Nacional), e o cálculo
 * de imposto retroativo depende de saber qual regime valia em cada ano.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "empresa_regime_tributario",
        uniqueConstraints = @UniqueConstraint(name = "uk_empresa_regime_ano", columnNames = {"empresa_id", "ano_calendario"}))
public class EmpresaRegimeTributario extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(name = "ano_calendario", nullable = false)
    private Integer anoCalendario;

    @Column(name = "crt", nullable = false, length = 1)
    private String crt;
}
