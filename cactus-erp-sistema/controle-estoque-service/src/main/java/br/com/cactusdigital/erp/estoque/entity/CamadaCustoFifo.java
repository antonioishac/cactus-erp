package br.com.cactusdigital.erp.estoque.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Camada de custo pro método {@link MetodoCusteio#FIFO} — só é criada/consumida quando
 * {@link ConfiguracaoEstoqueEmpresa#getMetodoCusteio()} da empresa é {@code FIFO}. Uma
 * {@code ENTRADA} cria uma camada nova; uma {@code SAIDA} consome camadas em ordem de
 * {@code dataEntrada} (mais antiga primeiro) até completar a quantidade.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "camada_custo_fifo")
public class CamadaCustoFifo extends BaseEntity {

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposito_id", nullable = false)
    private Deposito deposito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @Column(name = "quantidade_restante", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidadeRestante;

    @Column(name = "custo_unitario", nullable = false, precision = 18, scale = 6)
    private BigDecimal custoUnitario;

    @Column(name = "data_entrada", nullable = false)
    private Instant dataEntrada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movimento_entrada_id", nullable = false)
    private EstoqueMovimento movimentoEntrada;
}
