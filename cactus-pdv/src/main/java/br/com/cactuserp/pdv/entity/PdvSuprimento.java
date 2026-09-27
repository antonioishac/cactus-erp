package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Registro de suprimento — entrada de dinheiro no caixa (reforço de troco).
 *
 * <p>Mapeia a tabela {@code PDV_SUPRIMENTO}. Toda operação está vinculada a um
 * {@link PdvMovimento} aberto e a um {@link PdvOperador} que a realizou.
 *
 * <p>Em geral suprimento é operação de baixo risco e não exige autorização
 * gerencial, mas o campo {@link #gerenteAutorizador} fica disponível para
 * o caso de a empresa decidir parametrizar essa exigência.
 */
@Entity
@Table(name = "PDV_SUPRIMENTO")
public class PdvSuprimento {

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

    @Column(name = "DATA_SUPRIMENTO")
    private LocalDate dataSuprimento;

    @Column(name = "HORA_SUPRIMENTO", length = 8)
    private String horaSuprimento;

    @Column(name = "VALOR", precision = 18, scale = 6)
    private BigDecimal valor;

    @Column(name = "OBSERVACAO", length = 250)
    private String observacao;

    @Column(name = "ID_GERADO_DISPOSITIVO")
    private Integer idGeradoDispositivo;

    @Column(name = "ID_DISPOSITIVO", length = 100)
    private String idDispositivo;

    public PdvSuprimento() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PdvMovimento getPdvMovimento() { return pdvMovimento; }
    public void setPdvMovimento(PdvMovimento m) { this.pdvMovimento = m; }

    public PdvOperador getOperador() { return operador; }
    public void setOperador(PdvOperador op) { this.operador = op; }

    public PdvOperador getGerenteAutorizador() { return gerenteAutorizador; }
    public void setGerenteAutorizador(PdvOperador g) { this.gerenteAutorizador = g; }

    public LocalDate getDataSuprimento() { return dataSuprimento; }
    public void setDataSuprimento(LocalDate d) { this.dataSuprimento = d; }

    public String getHoraSuprimento() { return horaSuprimento; }
    public void setHoraSuprimento(String h) { this.horaSuprimento = h; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal v) { this.valor = v; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String o) { this.observacao = o; }

    public Integer getIdGeradoDispositivo() { return idGeradoDispositivo; }
    public void setIdGeradoDispositivo(Integer i) { this.idGeradoDispositivo = i; }

    public String getIdDispositivo() { return idDispositivo; }
    public void setIdDispositivo(String d) { this.idDispositivo = d; }
}