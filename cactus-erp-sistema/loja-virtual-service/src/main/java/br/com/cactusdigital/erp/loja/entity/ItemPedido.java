package br.com.cactusdigital.erp.loja.entity;

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
 * Item do pedido — {@code skuSnapshot}/{@code nomeSnapshot} são denormalizados
 * (capturados no momento da compra) porque o histórico do pedido precisa sobreviver
 * mesmo que a variante seja renomeada/excluída depois; {@code varianteProdutoLojaId}
 * fica como referência solta (não FK — mesmo critério), só pra rastreabilidade.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "item_pedido")
public class ItemPedido extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(name = "variante_produto_loja_id", nullable = false)
    private Long varianteProdutoLojaId;

    @Column(name = "sku_snapshot", nullable = false, length = 50)
    private String skuSnapshot;

    @Column(name = "nome_snapshot", nullable = false, length = 150)
    private String nomeSnapshot;

    /** Congelados no momento da compra, mesmo espírito de {@code precoUnitario} — usados por {@code NotaFiscalConsumidorService} (Fase 13, sub-fase 3) pra montar o item da NFC-e sem depender do catálogo atual. */
    @Column(name = "ncm_snapshot", length = 8)
    private String ncmSnapshot;

    @Column(name = "csosn_snapshot", length = 3)
    private String csosnSnapshot;

    @Column(name = "quantidade", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 18, scale = 6)
    private BigDecimal precoUnitario;

    @Column(name = "valor_total", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTotal;
}
