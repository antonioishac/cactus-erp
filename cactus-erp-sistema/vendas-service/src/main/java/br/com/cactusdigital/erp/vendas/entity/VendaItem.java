package br.com.cactusdigital.erp.vendas.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Item de {@link VendaCabecalho} — validado contra {@code fenix.sql.VENDA_DETALHE}.
 * {@code produtoId} é referência SOLTA. {@code depositoId} é enriquecimento (🆕, sem
 * equivalente em fenix.sql — lá o estoque é um campo flat único no produto): precisa
 * saber de qual depósito baixar a {@code SAIDA} na confirmação da venda, porque o
 * {@code controle-estoque-service} (Fase 2) modela saldo sempre por produto×depósito,
 * nunca um total único.
 * <p>
 * <b>Nullable na coluna, obrigatório só pra confirmar</b> (achado real ao validar ao
 * vivo): itens copiados de um {@link VendaOrcamento} (que não conhece depósito/estoque —
 * ver {@code VendaService#gerarAPartirDeOrcamento}) nascem sem depósito e precisam ser
 * complementados via {@code PUT /vendas/{id}/itens/{itemId}} antes da venda ser
 * confirmada — {@code VendaService#confirmar} valida isso explicitamente
 * ({@code VENDA_ITEM_SEM_DEPOSITO}). Coluna {@code NOT NULL} quebraria esse fluxo de
 * RASCUNHO no INSERT.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "venda_item")
public class VendaItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    private VendaCabecalho venda;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(name = "deposito_id")
    private Long depositoId;

    @Column(name = "quantidade", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorUnitario;

    @Column(name = "valor_subtotal", precision = 18, scale = 6)
    private BigDecimal valorSubtotal;

    @Column(name = "taxa_desconto", precision = 18, scale = 6)
    private BigDecimal taxaDesconto;

    @Column(name = "valor_desconto", precision = 18, scale = 6)
    private BigDecimal valorDesconto;

    @Column(name = "valor_total", precision = 18, scale = 6)
    private BigDecimal valorTotal;
}
