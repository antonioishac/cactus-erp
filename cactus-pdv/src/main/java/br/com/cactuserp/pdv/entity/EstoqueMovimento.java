package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Movimentação de estoque — livro-razão de todas as alterações em
 * {@link Produto#getQuantidadeEstoque()}.
 *
 * <p>Mapeia a tabela {@code ESTOQUE_MOVIMENTO}. Todo ponto do sistema que
 * precise alterar o saldo de estoque de um produto deve fazer isso através
 * de {@code EstoqueMovimentoService#registrarMovimento(...)}, que grava um
 * registro aqui e só então atualiza {@code PRODUTO.QUANTIDADE_ESTOQUE} —
 * nunca alterar o saldo diretamente por fora desse fluxo, sob pena de
 * perder o histórico auditável.
 *
 * <p>{@link #quantidade} é sempre um valor positivo; o efeito sobre o saldo
 * (soma ou subtração) é determinado pelo {@link #tipoMovimento}.
 *
 * @see TipoMovimentoEstoque
 */
@Entity
@Table(name = "ESTOQUE_MOVIMENTO")
public class EstoqueMovimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PRODUTO", nullable = false)
    private Produto produto;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_MOVIMENTO", length = 1, nullable = false, columnDefinition = "CHAR(1)")
    private TipoMovimentoEstoque tipoMovimento;

    @Column(name = "QUANTIDADE", precision = 18, scale = 6, nullable = false)
    private BigDecimal quantidade;

    @Column(name = "QUANTIDADE_ANTERIOR", precision = 18, scale = 6, nullable = false)
    private BigDecimal quantidadeAnterior;

    @Column(name = "QUANTIDADE_NOVA", precision = 18, scale = 6, nullable = false)
    private BigDecimal quantidadeNova;

    /** Preenchido quando o movimento vem de uma venda (TIPO_MOVIMENTO = S). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PDV_VENDA_DETALHE")
    private PdvVendaDetalhe vendaDetalhe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_OPERADOR")
    private PdvOperador operador;

    @Column(name = "DATA_MOVIMENTO", nullable = false)
    private LocalDate dataMovimento;

    @Column(name = "HORA_MOVIMENTO", length = 8, nullable = false)
    private String horaMovimento;

    @Column(name = "OBSERVACAO", length = 250)
    private String observacao;

    @Column(name = "ID_DISPOSITIVO", length = 100)
    private String idDispositivo;

    public EstoqueMovimento() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public TipoMovimentoEstoque getTipoMovimento() { return tipoMovimento; }
    public void setTipoMovimento(TipoMovimentoEstoque tipoMovimento) { this.tipoMovimento = tipoMovimento; }

    public BigDecimal getQuantidade() { return quantidade; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }

    public BigDecimal getQuantidadeAnterior() { return quantidadeAnterior; }
    public void setQuantidadeAnterior(BigDecimal quantidadeAnterior) { this.quantidadeAnterior = quantidadeAnterior; }

    public BigDecimal getQuantidadeNova() { return quantidadeNova; }
    public void setQuantidadeNova(BigDecimal quantidadeNova) { this.quantidadeNova = quantidadeNova; }

    public PdvVendaDetalhe getVendaDetalhe() { return vendaDetalhe; }
    public void setVendaDetalhe(PdvVendaDetalhe vendaDetalhe) { this.vendaDetalhe = vendaDetalhe; }

    public PdvOperador getOperador() { return operador; }
    public void setOperador(PdvOperador operador) { this.operador = operador; }

    public LocalDate getDataMovimento() { return dataMovimento; }
    public void setDataMovimento(LocalDate dataMovimento) { this.dataMovimento = dataMovimento; }

    public String getHoraMovimento() { return horaMovimento; }
    public void setHoraMovimento(String horaMovimento) { this.horaMovimento = horaMovimento; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public String getIdDispositivo() { return idDispositivo; }
    public void setIdDispositivo(String idDispositivo) { this.idDispositivo = idDispositivo; }
}
