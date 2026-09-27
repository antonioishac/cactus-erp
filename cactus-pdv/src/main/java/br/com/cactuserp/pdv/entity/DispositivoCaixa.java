package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DISPOSITIVO_CAIXA")
public class DispositivoCaixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_PDV_CAIXA", nullable = false)
    private PdvCaixa caixa;

    @Column(name = "HOSTNAME", length = 100, nullable = false, unique = true)
    private String hostname;

    @Column(name = "MAC_ADDRESS", length = 50)
    private String macAddress;

    @Column(name = "SO_NOME", length = 50)
    private String soNome;

    @Column(name = "DATA_VINCULO", nullable = false)
    private LocalDateTime dataVinculo;

    @Column(name = "ULTIMO_LOGIN")
    private LocalDateTime ultimoLogin;

    @Column(name = "ATIVO", length = 1, nullable = false)
    private String ativo = "S";

    public DispositivoCaixa() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PdvCaixa getCaixa() { return caixa; }
    public void setCaixa(PdvCaixa caixa) { this.caixa = caixa; }
    public String getHostname() { return hostname; }
    public void setHostname(String h) { this.hostname = h; }
    public String getMacAddress() { return macAddress; }
    public void setMacAddress(String m) { this.macAddress = m; }
    public String getSoNome() { return soNome; }
    public void setSoNome(String s) { this.soNome = s; }
    public LocalDateTime getDataVinculo() { return dataVinculo; }
    public void setDataVinculo(LocalDateTime d) { this.dataVinculo = d; }
    public LocalDateTime getUltimoLogin() { return ultimoLogin; }
    public void setUltimoLogin(LocalDateTime u) { this.ultimoLogin = u; }
    public String getAtivo() { return ativo; }
    public void setAtivo(String a) { this.ativo = a; }

    public boolean isAtivo() { return "S".equals(ativo); }
}