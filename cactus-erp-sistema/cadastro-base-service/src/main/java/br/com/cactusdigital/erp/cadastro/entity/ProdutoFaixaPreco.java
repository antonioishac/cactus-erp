package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * {@code PRODUTO_FAIXA_PRECO} do cactus-pdv — **um conceito só** cobrindo desconto por
 * quantidade E preço promocional por vigência de data (ver seção própria no CLAUDE.md,
 * não são duas features). {@code quantidadeMinima=1} + {@code dataInicio}/{@code dataFim}
 * preenchidos = produto promocional; {@code quantidadeMinima>1} sem vigência = desconto
 * por atacado que vale sempre.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "produto_faixa_preco")
public class ProdutoFaixaPreco extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Builder.Default
    @Column(name = "quantidade_minima", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidadeMinima = BigDecimal.ONE;

    @Column(name = "valor_unitario", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorUnitario;

    @Column(name = "rotulo", length = 50)
    private String rotulo;

    /** {@code NULL} = vigência aberta, a faixa vale sempre (não é promoção, é preço por quantidade). */
    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
