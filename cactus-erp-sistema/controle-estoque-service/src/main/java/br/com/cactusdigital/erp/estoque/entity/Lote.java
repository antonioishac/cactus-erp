package br.com.cactusdigital.erp.estoque.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Lote de fabricação de um produto — obrigatório pra rastreabilidade em
 * alimentício/farma (FEFO: {@link #dataValidade} orienta qual lote sai primeiro). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "lote", uniqueConstraints = @UniqueConstraint(name = "uk_lote_produto_codigo", columnNames = {"produto_id", "codigo"}))
public class Lote extends BaseEntity {

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(name = "codigo", nullable = false, length = 60)
    private String codigo;

    @Column(name = "data_fabricacao")
    private LocalDate dataFabricacao;

    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
