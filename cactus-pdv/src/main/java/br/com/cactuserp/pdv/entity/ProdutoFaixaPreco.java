package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Faixa de preço de um produto.
 *
 * <p>Modelo atacarejo: cada produto pode ter N faixas, e a faixa
 * aplicada é a MAIOR cujo {@link #quantidadeMinima} seja menor ou
 * igual à quantidade comprada.
 *
 * <p>Exemplo: produto com 2 faixas (5+ e 10+) e cliente bipou 7 unid
 * → aplica a faixa "5+" (a maior que cabe). Se bipou 12, aplica "10+".
 *
 * <p>O preço de UMA unidade (sem entrar em faixa) continua em
 * {@code PRODUTO.VALOR_VENDA}. Esta tabela só armazena faixas
 * ADICIONAIS de atacado.
 */
@Entity
@Table(name = "PRODUTO_FAIXA_PRECO")
public class ProdutoFaixaPreco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ID_PRODUTO", nullable = false)
    private Long idProduto;

    @Column(name = "QUANTIDADE_MINIMA", nullable = false, precision = 18, scale = 6)
    private BigDecimal quantidadeMinima;

    @Column(name = "VALOR_UNITARIO", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorUnitario;

    @Column(name = "ROTULO", length = 30)
    private String rotulo;

    @Column(name = "DATA_INICIO")
    private LocalDate dataInicio;

    @Column(name = "DATA_FIM")
    private LocalDate dataFim;

    /** S = Ativo | N = Inativo */
    @Column(name = "ATIVO", length = 1, nullable = false)
    private String ativo;

    // ====== Helpers ======================================================

    /**
     * Retorna true se a faixa está ativa E em vigência na data informada.
     */
    public boolean vigenteEm(LocalDate data) {
        if (!"S".equalsIgnoreCase(ativo)) return false;
        if (dataInicio != null && data.isBefore(dataInicio)) return false;
        if (dataFim != null && data.isAfter(dataFim)) return false;
        return true;
    }

    // ====== Getters / Setters ===========================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdProduto() { return idProduto; }
    public void setIdProduto(Long idProduto) { this.idProduto = idProduto; }

    public BigDecimal getQuantidadeMinima() { return quantidadeMinima; }
    public void setQuantidadeMinima(BigDecimal quantidadeMinima) { this.quantidadeMinima = quantidadeMinima; }

    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }

    public String getRotulo() { return rotulo; }
    public void setRotulo(String rotulo) { this.rotulo = rotulo; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public String getAtivo() { return ativo; }
    public void setAtivo(String ativo) { this.ativo = ativo; }
}