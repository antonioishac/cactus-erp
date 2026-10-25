package br.com.cactusdigital.erp.vendas.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Devolução pós-venda de um item de {@link VendaPdv} ({@code PDV_DEVOLUCAO}). Só registra o fato:
 * a reentrada de estoque já chega pelo {@code ESTOQUE_MOVIMENTO} (tipo D) do PDV, e o PDV não decide
 * como o dinheiro volta ao cliente (sangria, crédito em loja, estorno de cartão — ver
 * {@code DevolucaoService} do PDV). Se for em dinheiro, a saída já chega como {@code SANGRIA}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "venda_pdv_devolucao")
public class VendaPdvDevolucao extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_pdv_id", nullable = false)
    private VendaPdv venda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_pdv_item_id", nullable = false)
    private VendaPdvItem item;

    @Column(name = "registro_sincronizacao_id", nullable = false, unique = true)
    private Long registroSincronizacaoId;

    @Column(name = "quantidade_devolvida", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidadeDevolvida;

    @Column(name = "valor_devolvido", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorDevolvido;

    @Column(name = "motivo", length = 250)
    private String motivo;

    @Column(name = "pdv_operador_id")
    private Long pdvOperadorId;

    @Column(name = "pdv_gerente_autorizador_id", nullable = false)
    private Long pdvGerenteAutorizadorId;

    @Column(name = "data_hora_devolucao", nullable = false)
    private Instant dataHoraDevolucao;
}
