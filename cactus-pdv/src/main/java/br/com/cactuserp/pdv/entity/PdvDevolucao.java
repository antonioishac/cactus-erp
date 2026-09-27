package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Devolução/troca pós-fechamento de um item de venda já finalizada.
 *
 * <p>Mapeia {@code PDV_DEVOLUCAO}. Diferente do cancelamento de item
 * (usado enquanto o cupom está aberto, ver {@link PdvVendaDetalhe#getCancelado()}),
 * a devolução acontece depois que o cupom já foi fechado (status F) — o
 * item original nunca é alterado (imutabilidade fiscal), só é gerado um
 * registro de devolução + reentrada de estoque
 * ({@link EstoqueMovimento} tipo {@code D}).
 *
 * <p>Sempre exige autorização gerencial — não há alçada por valor como em
 * desconto/sangria.
 *
 * <p><b>Não emite documento fiscal de devolução.</b> Uma devolução fiscal
 * de verdade (NFC-e de entrada, cancelamento) depende da integração ACBr,
 * que ainda não foi validada contra uma instância real (ver
 * {@code EmissaoFiscalService}). Esta entidade cobre só o lado operacional
 * (estoque + auditoria interna).
 */
@Entity
@Table(name = "PDV_DEVOLUCAO")
public class PdvDevolucao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PDV_VENDA_CABECALHO", nullable = false)
    private PdvVendaCabecalho vendaCabecalho;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PDV_VENDA_DETALHE", nullable = false)
    private PdvVendaDetalhe vendaDetalhe;

    @Column(name = "QUANTIDADE_DEVOLVIDA", precision = 18, scale = 6, nullable = false)
    private BigDecimal quantidadeDevolvida;

    @Column(name = "VALOR_DEVOLVIDO", precision = 18, scale = 6, nullable = false)
    private BigDecimal valorDevolvido;

    @Column(name = "MOTIVO", length = 250)
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_OPERADOR", nullable = false)
    private PdvOperador operador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_GERENTE_AUTORIZADOR", nullable = false)
    private PdvOperador gerenteAutorizador;

    @Column(name = "DATA_DEVOLUCAO", nullable = false)
    private LocalDate dataDevolucao;

    @Column(name = "HORA_DEVOLUCAO", length = 8, nullable = false)
    private String horaDevolucao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ESTOQUE_MOVIMENTO")
    private EstoqueMovimento estoqueMovimento;

    @Column(name = "ID_DISPOSITIVO", length = 100)
    private String idDispositivo;

    public PdvDevolucao() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PdvVendaCabecalho getVendaCabecalho() { return vendaCabecalho; }
    public void setVendaCabecalho(PdvVendaCabecalho vendaCabecalho) { this.vendaCabecalho = vendaCabecalho; }

    public PdvVendaDetalhe getVendaDetalhe() { return vendaDetalhe; }
    public void setVendaDetalhe(PdvVendaDetalhe vendaDetalhe) { this.vendaDetalhe = vendaDetalhe; }

    public BigDecimal getQuantidadeDevolvida() { return quantidadeDevolvida; }
    public void setQuantidadeDevolvida(BigDecimal quantidadeDevolvida) { this.quantidadeDevolvida = quantidadeDevolvida; }

    public BigDecimal getValorDevolvido() { return valorDevolvido; }
    public void setValorDevolvido(BigDecimal valorDevolvido) { this.valorDevolvido = valorDevolvido; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public PdvOperador getOperador() { return operador; }
    public void setOperador(PdvOperador operador) { this.operador = operador; }

    public PdvOperador getGerenteAutorizador() { return gerenteAutorizador; }
    public void setGerenteAutorizador(PdvOperador gerenteAutorizador) { this.gerenteAutorizador = gerenteAutorizador; }

    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDate dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    public String getHoraDevolucao() { return horaDevolucao; }
    public void setHoraDevolucao(String horaDevolucao) { this.horaDevolucao = horaDevolucao; }

    public EstoqueMovimento getEstoqueMovimento() { return estoqueMovimento; }
    public void setEstoqueMovimento(EstoqueMovimento estoqueMovimento) { this.estoqueMovimento = estoqueMovimento; }

    public String getIdDispositivo() { return idDispositivo; }
    public void setIdDispositivo(String idDispositivo) { this.idDispositivo = idDispositivo; }
}
