package br.com.cactusdigital.erp.bi.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Cache local denormalizado do produto (cadastro-base-service), reconstruído a cada
 * ETL — deliberado: BI serve relatório rápido sem depender de chamada síncrona a outro
 * serviço em tempo de leitura. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "produto_dim")
public class ProdutoDim extends BaseEntity {

    @Column(name = "produto_id", nullable = false, unique = true)
    private Long produtoId;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "codigo_interno", length = 50)
    private String codigoInterno;

    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao;
}
