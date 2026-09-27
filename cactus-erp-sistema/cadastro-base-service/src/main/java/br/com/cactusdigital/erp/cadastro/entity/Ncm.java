package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Nomenclatura Comum do Mercosul — catálogo oficial da Receita Federal (~10 mil
 * códigos). Não seedamos a tabela oficial inteira via Flyway (inviável escrever à
 * mão); a migration só semeia uma amostra representativa. Importar a tabela completa
 * é tarefa operacional (rotina de carga), não código.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "ncm")
public class Ncm extends BaseEntity {

    @Column(name = "codigo", nullable = false, length = 8, unique = true)
    private String codigo;

    @Column(name = "descricao", nullable = false, length = 1000)
    private String descricao;
}
