package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;

/**
 * Configuração de balança vinculada a um {@link PdvConfiguracao}.
 *
 * <p>Mapeia {@code PDV_CONFIGURACAO_BALANCA} — tabela que já existia no
 * schema (ver {@code PDV.md}) mas não tinha entidade até a fase 7
 * (periféricos). Os campos seriais (handshake/paridade/stop bits/baud
 * rate) são passados direto pro {@code BalancaService}, que os traduz
 * pras constantes do jSerialComm.
 */
@Entity
@Table(name = "PDV_CONFIGURACAO_BALANCA")
public class PdvConfiguracaoBalanca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PDV_CONFIGURACAO", nullable = false)
    private PdvConfiguracao configuracao;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "MODELO")
    private ModeloBalanca modelo;

    @Column(name = "IDENTIFICADOR", length = 1, columnDefinition = "CHAR(1)")
    private String identificador;

    /** 0=Nenhum, 1=XON_XOFF, 2=RTS_CTS, 3=DTR_DSR — ver PDV.md. */
    @Column(name = "HAND_SHAKE")
    private Integer handShake;

    /** 0=None, 1=Odd, 2=Even, 3=Mark, 4=Space. */
    @Column(name = "PARITY")
    private Integer parity;

    /** 0=1, 1=1eMeio, 2=2. */
    @Column(name = "STOP_BITS")
    private Integer stopBits;

    @Column(name = "DATA_BITS")
    private Integer dataBits;

    @Column(name = "BAUD_RATE")
    private Integer baudRate;

    @Column(name = "PORTA", length = 4, columnDefinition = "CHAR(4)")
    private String porta;

    @Column(name = "TIMEOUT")
    private Integer timeout;

    /**
     * Máscara do código de barras com peso/valor embutido, ex:
     * {@code 2CCCCCTTTTTTDV} — ver {@code BalancaCodigoDecoder}.
     */
    @Column(name = "TIPO_CONFIGURACAO", length = 20)
    private String tipoConfiguracao;

    public PdvConfiguracaoBalanca() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PdvConfiguracao getConfiguracao() { return configuracao; }
    public void setConfiguracao(PdvConfiguracao configuracao) { this.configuracao = configuracao; }

    public ModeloBalanca getModelo() { return modelo; }
    public void setModelo(ModeloBalanca modelo) { this.modelo = modelo; }

    public String getIdentificador() { return identificador; }
    public void setIdentificador(String identificador) { this.identificador = identificador; }

    public Integer getHandShake() { return handShake; }
    public void setHandShake(Integer handShake) { this.handShake = handShake; }

    public Integer getParity() { return parity; }
    public void setParity(Integer parity) { this.parity = parity; }

    public Integer getStopBits() { return stopBits; }
    public void setStopBits(Integer stopBits) { this.stopBits = stopBits; }

    public Integer getDataBits() { return dataBits; }
    public void setDataBits(Integer dataBits) { this.dataBits = dataBits; }

    public Integer getBaudRate() { return baudRate; }
    public void setBaudRate(Integer baudRate) { this.baudRate = baudRate; }

    public String getPorta() { return porta; }
    public void setPorta(String porta) { this.porta = porta; }

    public Integer getTimeout() { return timeout; }
    public void setTimeout(Integer timeout) { this.timeout = timeout; }

    public String getTipoConfiguracao() { return tipoConfiguracao; }
    public void setTipoConfiguracao(String tipoConfiguracao) { this.tipoConfiguracao = tipoConfiguracao; }
}
