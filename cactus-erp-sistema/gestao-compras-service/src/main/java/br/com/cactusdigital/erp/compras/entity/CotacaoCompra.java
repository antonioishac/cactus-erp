package br.com.cactusdigital.erp.compras.entity;

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
 * Cotação de compra — vinculada a uma {@link RequisicaoCompra}, agrega N
 * {@link FornecedorCotacao} concorrentes. {@code gerarPedido()} (no service, a partir de
 * um fornecedor específico) marca esta cotação como {@code FINALIZADA}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "cotacao_compra")
public class CotacaoCompra extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisicao_id", nullable = false)
    private RequisicaoCompra requisicao;

    @Column(name = "data_cotacao")
    private LocalDate dataCotacao;

    @Column(name = "descricao", length = 100)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 15)
    private SituacaoCotacaoCompra situacao;

    @Builder.Default
    @OneToMany(mappedBy = "cotacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FornecedorCotacao> fornecedores = new ArrayList<>();
}
