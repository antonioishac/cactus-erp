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
 * Transferência de estoque entre depósitos/filiais. {@code enviar()} (no service) gera
 * um {@link EstoqueMovimento} {@code TRANSFERENCIA_SAIDA} no depósito origem e marca
 * {@code situacao=EM_TRANSITO}; {@code receber()} gera {@code TRANSFERENCIA_ENTRADA} no
 * destino e marca {@code CONCLUIDA}. A quantidade "em trânsito" mora nesta própria linha
 * — não existe depósito virtual fake pra isso.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "transferencia_estoque")
public class TransferenciaEstoque extends BaseEntity {

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposito_origem_id", nullable = false)
    private Deposito depositoOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposito_destino_id", nullable = false)
    private Deposito depositoDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @Column(name = "quantidade", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidade;

    /** Capturado do custo médio ponderado da origem em {@code enviar()} — usado pra
     * atualizar o custo médio do destino em {@code receber()}, que pode acontecer dias
     * depois (não dá pra reconsultar a origem na hora de receber). */
    @Column(name = "custo_unitario", precision = 18, scale = 6)
    private BigDecimal custoUnitario;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 20)
    private SituacaoTransferencia situacao;

    @Column(name = "usuario_envio_id")
    private Long usuarioEnvioId;

    @Column(name = "data_envio")
    private Instant dataEnvio;

    @Column(name = "usuario_recebimento_id")
    private Long usuarioRecebimentoId;

    @Column(name = "data_recebimento")
    private Instant dataRecebimento;

    @Column(name = "observacao", length = 250)
    private String observacao;
}
