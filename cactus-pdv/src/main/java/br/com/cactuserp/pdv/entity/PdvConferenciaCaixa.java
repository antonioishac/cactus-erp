package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Cabeçalho de uma conferência de caixa.
 *
 * <p>Uma conferência é o ato do operador (com autorização gerencial) contar
 * o dinheiro físico + outras formas de pagamento e confrontar com o que
 * o sistema espera ter. Mantemos histórico de cada conferência pra
 * auditoria.
 */
@Entity
@Table(name = "PDV_CONFERENCIA_CAIXA")
public class PdvConferenciaCaixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PDV_MOVIMENTO", nullable = false)
    private PdvMovimento pdvMovimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_OPERADOR", nullable = false)
    private PdvOperador operador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GERENTE_AUTORIZADOR", nullable = false)
    private PdvOperador gerenteAutorizador;

    @Column(name = "DATA_CONFERENCIA", nullable = false)
    private LocalDate dataConferencia;

    @Column(name = "HORA_CONFERENCIA", length = 8, nullable = false)
    private String horaConferencia;

    @Column(name = "VALOR_TOTAL_ESPERADO", precision = 18, scale = 6)
    private BigDecimal valorTotalEsperado;

    @Column(name = "VALOR_TOTAL_CONTADO", precision = 18, scale = 6)
    private BigDecimal valorTotalContado;

    @Column(name = "VALOR_DIVERGENCIA", precision = 18, scale = 6)
    private BigDecimal valorDivergencia;

    @Column(name = "OBSERVACAO", length = 500)
    private String observacao;

    @Column(name = "HASH_REGISTRO", length = 64)
    private String hashRegistro;

    @OneToMany(mappedBy = "conferenciaCaixa",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<PdvConferenciaCaixaDetalhe> detalhes = new ArrayList<>();

    public PdvConferenciaCaixa() {}

    public void adicionarDetalhe(PdvConferenciaCaixaDetalhe det) {
        detalhes.add(det);
        det.setConferenciaCaixa(this);
    }

    // ====== Getters / Setters ======
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PdvMovimento getPdvMovimento() { return pdvMovimento; }
    public void setPdvMovimento(PdvMovimento v) { this.pdvMovimento = v; }

    public PdvOperador getOperador() { return operador; }
    public void setOperador(PdvOperador v) { this.operador = v; }

    public PdvOperador getGerenteAutorizador() { return gerenteAutorizador; }
    public void setGerenteAutorizador(PdvOperador v) { this.gerenteAutorizador = v; }

    public LocalDate getDataConferencia() { return dataConferencia; }
    public void setDataConferencia(LocalDate v) { this.dataConferencia = v; }

    public String getHoraConferencia() { return horaConferencia; }
    public void setHoraConferencia(String v) { this.horaConferencia = v; }

    public BigDecimal getValorTotalEsperado() { return valorTotalEsperado; }
    public void setValorTotalEsperado(BigDecimal v) { this.valorTotalEsperado = v; }

    public BigDecimal getValorTotalContado() { return valorTotalContado; }
    public void setValorTotalContado(BigDecimal v) { this.valorTotalContado = v; }

    public BigDecimal getValorDivergencia() { return valorDivergencia; }
    public void setValorDivergencia(BigDecimal v) { this.valorDivergencia = v; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String v) { this.observacao = v; }

    public String getHashRegistro() { return hashRegistro; }
    public void setHashRegistro(String v) { this.hashRegistro = v; }

    public List<PdvConferenciaCaixaDetalhe> getDetalhes() { return detalhes; }
    public void setDetalhes(List<PdvConferenciaCaixaDetalhe> v) { this.detalhes = v; }
}