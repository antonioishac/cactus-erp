package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Registro de sangria — saída de dinheiro do caixa.
 *
 * <p>Mapeia a tabela {@code PDV_SANGRIA}. Toda sangria está vinculada a um
 * {@link PdvMovimento} aberto e a um {@link PdvOperador} que a realizou.
 *
 * <p>Quando o valor excede o limite parametrizado da empresa, um
 * gerente/supervisor precisa autorizar — registrado em
 * {@link #gerenteAutorizador}.
 */
@Entity
@Table(name = "PDV_SANGRIA")
public class PdvSangria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PDV_MOVIMENTO", nullable = false)
    private PdvMovimento pdvMovimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_OPERADOR")
    private PdvOperador operador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GERENTE_AUTORIZADOR")
    private PdvOperador gerenteAutorizador;

    @Column(name = "DATA_SANGRIA")
    private LocalDate dataSangria;

    @Column(name = "HORA_SANGRIA", length = 8)
    private String horaSangria;

    @Column(name = "VALOR", precision = 18, scale = 6)
    private BigDecimal valor;

    @Column(name = "OBSERVACAO", length = 250)
    private String observacao;

    @Column(name = "ID_GERADO_DISPOSITIVO")
    private Integer idGeradoDispositivo;

    @Column(name = "ID_DISPOSITIVO", length = 100)
    private String idDispositivo;

    public PdvSangria() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PdvMovimento getPdvMovimento() { return pdvMovimento; }
    public void setPdvMovimento(PdvMovimento m) { this.pdvMovimento = m; }

    public PdvOperador getOperador() { return operador; }
    public void setOperador(PdvOperador op) { this.operador = op; }

    public PdvOperador getGerenteAutorizador() { return gerenteAutorizador; }
    public void setGerenteAutorizador(PdvOperador g) { this.gerenteAutorizador = g; }

    public LocalDate getDataSangria() { return dataSangria; }
    public void setDataSangria(LocalDate d) { this.dataSangria = d; }

    public String getHoraSangria() { return horaSangria; }
    public void setHoraSangria(String h) { this.horaSangria = h; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal v) { this.valor = v; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String o) { this.observacao = o; }

    public Integer getIdGeradoDispositivo() { return idGeradoDispositivo; }
    public void setIdGeradoDispositivo(Integer i) { this.idGeradoDispositivo = i; }

    public String getIdDispositivo() { return idDispositivo; }
    public void setIdDispositivo(String d) { this.idDispositivo = d; }
}