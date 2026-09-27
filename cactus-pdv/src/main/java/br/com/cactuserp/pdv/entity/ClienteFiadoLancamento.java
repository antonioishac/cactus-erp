package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Lançamento no livro-razão do fiado (crédito ao cliente).
 *
 * <p>Mapeia {@code CLIENTE_FIADO_LANCAMENTO}. Mesmo padrão de
 * {@link EstoqueMovimento}: o saldo devedor do cliente nunca é um campo
 * solto — é sempre a soma dos lançamentos (compras C somam, pagamentos P
 * subtraem), calculada por {@code FiadoService}.
 */
@Entity
@Table(name = "CLIENTE_FIADO_LANCAMENTO")
public class ClienteFiadoLancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CLIENTE", nullable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_LANCAMENTO", length = 1, nullable = false, columnDefinition = "CHAR(1)")
    private TipoLancamentoFiado tipoLancamento;

    @Column(name = "VALOR", precision = 18, scale = 6, nullable = false)
    private BigDecimal valor;

    @Column(name = "SALDO_ANTERIOR", precision = 18, scale = 6, nullable = false)
    private BigDecimal saldoAnterior;

    @Column(name = "SALDO_NOVO", precision = 18, scale = 6, nullable = false)
    private BigDecimal saldoNovo;

    /** Preenchido quando o lançamento vem de uma venda fiado (TIPO_LANCAMENTO = C). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PDV_VENDA_CABECALHO")
    private PdvVendaCabecalho vendaCabecalho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_OPERADOR")
    private PdvOperador operador;

    @Column(name = "DATA_LANCAMENTO", nullable = false)
    private LocalDate dataLancamento;

    @Column(name = "HORA_LANCAMENTO", length = 8, nullable = false)
    private String horaLancamento;

    @Column(name = "OBSERVACAO", length = 250)
    private String observacao;

    @Column(name = "ID_DISPOSITIVO", length = 100)
    private String idDispositivo;

    public ClienteFiadoLancamento() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public TipoLancamentoFiado getTipoLancamento() { return tipoLancamento; }
    public void setTipoLancamento(TipoLancamentoFiado tipoLancamento) { this.tipoLancamento = tipoLancamento; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public BigDecimal getSaldoAnterior() { return saldoAnterior; }
    public void setSaldoAnterior(BigDecimal saldoAnterior) { this.saldoAnterior = saldoAnterior; }

    public BigDecimal getSaldoNovo() { return saldoNovo; }
    public void setSaldoNovo(BigDecimal saldoNovo) { this.saldoNovo = saldoNovo; }

    public PdvVendaCabecalho getVendaCabecalho() { return vendaCabecalho; }
    public void setVendaCabecalho(PdvVendaCabecalho vendaCabecalho) { this.vendaCabecalho = vendaCabecalho; }

    public PdvOperador getOperador() { return operador; }
    public void setOperador(PdvOperador operador) { this.operador = operador; }

    public LocalDate getDataLancamento() { return dataLancamento; }
    public void setDataLancamento(LocalDate dataLancamento) { this.dataLancamento = dataLancamento; }

    public String getHoraLancamento() { return horaLancamento; }
    public void setHoraLancamento(String horaLancamento) { this.horaLancamento = horaLancamento; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public String getIdDispositivo() { return idDispositivo; }
    public void setIdDispositivo(String idDispositivo) { this.idDispositivo = idDispositivo; }
}
