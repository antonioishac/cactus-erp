package br.com.cactusdigital.erp.compras.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
 * Requisição de compra — pode gerar uma ou mais {@link CotacaoCompra} ao longo do tempo,
 * ou virar {@link PedidoCompra} direto (sem cotação). {@code situacao} vira
 * {@code PEDIDO_GERADO} quando qualquer pedido nasce a partir dela ou de uma cotação
 * vinculada (ver {@code PedidoCompraService.criar}).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "requisicao_compra")
public class RequisicaoCompra extends BaseEntity {

    @Column(name = "colaborador_id", nullable = false)
    private Long colaboradorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoRequisicaoCompra tipo;

    @Column(name = "descricao", length = 100)
    private String descricao;

    @Column(name = "data_requisicao")
    private LocalDate dataRequisicao;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 15)
    private SituacaoRequisicaoCompra situacao;

    @Column(name = "observacao", length = 250)
    private String observacao;

    @Builder.Default
    @OneToMany(mappedBy = "requisicao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RequisicaoCompraItem> itens = new ArrayList<>();
}
