package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "PDV_MOVIMENTO")
public class PdvMovimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_PDV_OPERADOR")
    private PdvOperador operador;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_PDV_CAIXA")
    private PdvCaixa caixa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_GERENTE_SUPERVISOR")
    private PdvOperador gerenteSupervisor;

    @Column(name = "DATA_ABERTURA")
    private LocalDate dataAbertura;

    @Column(name = "HORA_ABERTURA", length = 8)
    private String horaAbertura;

    @Column(name = "DATA_FECHAMENTO")
    private LocalDate dataFechamento;

    @Column(name = "HORA_FECHAMENTO", length = 8)
    private String horaFechamento;

    @Column(name = "VALOR_FUNDO_INICIAL", precision = 18, scale = 6)
    private BigDecimal valorFundoInicial;

    @Column(name = "TOTAL_SUPRIMENTO", precision = 18, scale = 6)
    private BigDecimal totalSuprimento;

    @Column(name = "TOTAL_SANGRIA", precision = 18, scale = 6)
    private BigDecimal totalSangria;

    @Column(name = "TOTAL_NAO_FISCAL", precision = 18, scale = 6)
    private BigDecimal totalNaoFiscal;

    @Column(name = "TOTAL_VENDA", precision = 18, scale = 6)
    private BigDecimal totalVenda;

    @Column(name = "TOTAL_DESCONTO", precision = 18, scale = 6)
    private BigDecimal totalDesconto;

    @Column(name = "TOTAL_ACRESCIMO", precision = 18, scale = 6)
    private BigDecimal totalAcrescimo;

    @Column(name = "TOTAL_FINAL", precision = 18, scale = 6)
    private BigDecimal totalFinal;

    @Column(name = "TOTAL_RECEBIDO", precision = 18, scale = 6)
    private BigDecimal totalRecebido;

    @Column(name = "TOTAL_TROCO", precision = 18, scale = 6)
    private BigDecimal totalTroco;

    @Column(name = "TOTAL_CANCELADO", precision = 18, scale = 6)
    private BigDecimal totalCancelado;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_MOVIMENTO", length = 1, nullable = false, columnDefinition = "CHAR(1)")
    private StatusMovimento statusMovimento;

    @Column(name = "ID_GERADO_DISPOSITIVO")
    private Integer idGeradoDispositivo;

    @Column(name = "ID_DISPOSITIVO", length = 100)
    private String idDispositivo;

    @Column(name = "VALOR_FISICO_CONFERIDO", precision = 18, scale = 6)
    private BigDecimal valorFisicoConferido;

    @Column(name = "VALOR_DIVERGENCIA", precision = 18, scale = 6)
    private BigDecimal valorDivergencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GERENTE_FECHAMENTO")
    private PdvOperador gerenteFechamento;

    public PdvMovimento() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PdvOperador getOperador() { return operador; }
    public void setOperador(PdvOperador o) { this.operador = o; }
    public PdvCaixa getCaixa() { return caixa; }
    public void setCaixa(PdvCaixa c) { this.caixa = c; }
    public PdvOperador getGerenteSupervisor() { return gerenteSupervisor; }
    public void setGerenteSupervisor(PdvOperador g) { this.gerenteSupervisor = g; }
    public LocalDate getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDate d) { this.dataAbertura = d; }
    public String getHoraAbertura() { return horaAbertura; }
    public void setHoraAbertura(String h) { this.horaAbertura = h; }
    public BigDecimal getValorFundoInicial() {
        return valorFundoInicial;
    }

    public void setValorFundoInicial(BigDecimal valorFundoInicial) {
        this.valorFundoInicial = valorFundoInicial;
    }
    public LocalDate getDataFechamento() { return dataFechamento; }
    public void setDataFechamento(LocalDate d) { this.dataFechamento = d; }
    public String getHoraFechamento() { return horaFechamento; }
    public void setHoraFechamento(String h) { this.horaFechamento = h; }
    public BigDecimal getTotalSuprimento() { return totalSuprimento; }
    public void setTotalSuprimento(BigDecimal v) { this.totalSuprimento = v; }
    public BigDecimal getTotalSangria() { return totalSangria; }
    public void setTotalSangria(BigDecimal v) { this.totalSangria = v; }
    public BigDecimal getTotalNaoFiscal() { return totalNaoFiscal; }
    public void setTotalNaoFiscal(BigDecimal v) { this.totalNaoFiscal = v; }
    public BigDecimal getTotalVenda() { return totalVenda; }
    public void setTotalVenda(BigDecimal v) { this.totalVenda = v; }
    public BigDecimal getTotalDesconto() { return totalDesconto; }
    public void setTotalDesconto(BigDecimal v) { this.totalDesconto = v; }
    public BigDecimal getTotalAcrescimo() { return totalAcrescimo; }
    public void setTotalAcrescimo(BigDecimal v) { this.totalAcrescimo = v; }
    public BigDecimal getTotalFinal() { return totalFinal; }
    public void setTotalFinal(BigDecimal v) { this.totalFinal = v; }
    public BigDecimal getTotalRecebido() { return totalRecebido; }
    public void setTotalRecebido(BigDecimal v) { this.totalRecebido = v; }
    public BigDecimal getTotalTroco() { return totalTroco; }
    public void setTotalTroco(BigDecimal v) { this.totalTroco = v; }
    public BigDecimal getTotalCancelado() { return totalCancelado; }
    public void setTotalCancelado(BigDecimal v) { this.totalCancelado = v; }
    public StatusMovimento getStatusMovimento() { return statusMovimento; }
    public void setStatusMovimento(StatusMovimento s) { this.statusMovimento = s; }
    public Integer getIdGeradoDispositivo() { return idGeradoDispositivo; }
    public void setIdGeradoDispositivo(Integer i) { this.idGeradoDispositivo = i; }
    public String getIdDispositivo() { return idDispositivo; }
    public void setIdDispositivo(String s) { this.idDispositivo = s; }

    public BigDecimal getValorFisicoConferido() {
        return valorFisicoConferido;
    }
    public void setValorFisicoConferido(BigDecimal valorFisicoConferido) {
        this.valorFisicoConferido = valorFisicoConferido;
    }

    public BigDecimal getValorDivergencia() {
        return valorDivergencia;
    }
    public void setValorDivergencia(BigDecimal valorDivergencia) {
        this.valorDivergencia = valorDivergencia;
    }

    public PdvOperador getGerenteFechamento() {
        return gerenteFechamento;
    }
    public void setGerenteFechamento(PdvOperador gerenteFechamento) {
        this.gerenteFechamento = gerenteFechamento;
    }
}