package br.com.cactusdigital.erp.compras.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Evento de recebimento (imutável, sem máquina de estados própria — é um fato que
 * aconteceu, não um documento em andamento). Cada item gera uma {@code ENTRADA} no
 * controle-estoque-service (ver {@code RecebimentoCompraService}/{@code EstoqueClient}).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "recebimento_compra")
public class RecebimentoCompra extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoCompra pedido;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "data_recebimento", nullable = false)
    private LocalDate dataRecebimento;

    @Column(name = "numero_nota_fiscal", length = 20)
    private String numeroNotaFiscal;

    @Column(name = "chave_acesso_nfe", length = 44)
    private String chaveAcessoNfe;

    @Column(name = "observacao", length = 250)
    private String observacao;

    @Builder.Default
    @OneToMany(mappedBy = "recebimento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RecebimentoCompraItem> itens = new ArrayList<>();
}
