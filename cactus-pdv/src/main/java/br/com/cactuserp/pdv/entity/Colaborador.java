package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "COLABORADOR")
public class Colaborador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOME", length = 100)
    private String nome;

    @Column(name = "CPF", length = 11)
    private String cpf;

    @Column(name = "TELEFONE", length = 15)
    private String telefone;

    @Column(name = "CELULAR", length = 15)
    private String celular;

    @Column(name = "EMAIL", length = 250)
    private String email;

    @Column(name = "COMISSAO_VISTA", precision = 18, scale = 6)
    private BigDecimal comissaoVista;

    @Column(name = "COMISSAO_PRAZO", precision = 18, scale = 6)
    private BigDecimal comissaoPrazo;

    @Enumerated(EnumType.STRING)
    @Column(name = "NIVEL_AUTORIZACAO", length = 1, columnDefinition = "CHAR(1)")
    private NivelAutorizacao nivelAutorizacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "ENTREGADOR_VEICULO", length = 1, columnDefinition = "CHAR(1)")
    private EntregadorVeiculo entregadorVeiculo;

    public Colaborador() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public BigDecimal getComissaoVista() { return comissaoVista; }
    public void setComissaoVista(BigDecimal v) { this.comissaoVista = v; }
    public BigDecimal getComissaoPrazo() { return comissaoPrazo; }
    public void setComissaoPrazo(BigDecimal v) { this.comissaoPrazo = v; }
    public NivelAutorizacao getNivelAutorizacao() { return nivelAutorizacao; }
    public void setNivelAutorizacao(NivelAutorizacao n) { this.nivelAutorizacao = n; }
    public EntregadorVeiculo getEntregadorVeiculo() { return entregadorVeiculo; }
    public void setEntregadorVeiculo(EntregadorVeiculo v) { this.entregadorVeiculo = v; }
}