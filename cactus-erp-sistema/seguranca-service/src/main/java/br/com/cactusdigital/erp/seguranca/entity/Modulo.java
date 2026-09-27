package br.com.cactusdigital.erp.seguranca.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Um módulo do Cactus ERP (CADASTRO_BASE, COMPRAS, ESTOQUE, GED, BI — um por serviço
 * planejado). Só leitura via API: um módulo corresponde a um serviço de fato
 * implantado, não faz sentido criar um "módulo" arbitrário por chamada REST — a lista
 * é semeada via Flyway conforme cada serviço nasce.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "modulo")
public class Modulo extends BaseEntity {

    @Column(name = "codigo", nullable = false, length = 30, unique = true)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
