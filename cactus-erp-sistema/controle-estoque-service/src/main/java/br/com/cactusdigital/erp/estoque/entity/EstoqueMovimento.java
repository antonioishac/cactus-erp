package br.com.cactusdigital.erp.estoque.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Ledger append-only — <b>todo saldo muda exclusivamente através de um registro aqui</b>,
 * nunca por `UPDATE` direto em {@link SaldoEstoque} (que é só a materialização de
 * leitura rápida, atualizada na mesma transação). Sem {@code @Setter} de negócio
 * exposto via service — uma vez criado, este registro nunca é alterado nem excluído
 * (só {@code GET}, nunca {@code PUT}/{@code DELETE} no controller).
 * <p>
 * Validado contra {@code ESTOQUE_MOVIMENTO} do cactus-pdv: mesmo padrão de snapshot
 * {@code quantidadeAnterior}/{@code quantidadeNova} a cada lançamento.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "estoque_movimento")
public class EstoqueMovimento extends BaseEntity {

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposito_id", nullable = false)
    private Deposito deposito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "numero_serie_id")
    private NumeroSerie numeroSerie;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimento", nullable = false, length = 25)
    private TipoMovimentoEstoque tipoMovimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "direcao_ajuste", length = 12)
    private DirecaoAjuste direcaoAjuste;

    @Column(name = "quantidade", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidade;

    @Column(name = "quantidade_anterior", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidadeAnterior;

    @Column(name = "quantidade_nova", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidadeNova;

    @Column(name = "custo_unitario", precision = 18, scale = 6)
    private BigDecimal custoUnitario;

    @Enumerated(EnumType.STRING)
    @Column(name = "origem_tipo", nullable = false, length = 25)
    private OrigemMovimentoEstoque origemTipo;

    @Column(name = "origem_id")
    private Long origemId;

    /** Nulo quando o movimento nasce de uma chamada serviço-a-serviço (Client
     * Credentials, ex: {@code gestao-compras-service} confirmando um recebimento de
     * compra) — o token de serviço não tem claim {@code usuarioId}, só um humano tem. O
     * ator humano responsável, se houver, continua rastreável via {@code origemTipo}/
     * {@code origemId} no serviço de origem (ex: {@code RecebimentoCompra.usuarioId}). */
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "aprovador_id")
    private Long aprovadorId;

    @Column(name = "observacao", length = 250)
    private String observacao;

    @Column(name = "data_movimento", nullable = false)
    private Instant dataMovimento;
}
