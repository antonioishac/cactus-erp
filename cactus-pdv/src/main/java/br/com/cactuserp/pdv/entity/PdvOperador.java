package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "PDV_OPERADOR")
public class PdvOperador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_COLABORADOR", nullable = false)
    private Colaborador colaborador;

    @Column(name = "LOGIN", length = 20)
    private String login;

    @Column(name = "SENHA", length = 100)
    private String senha;

    public PdvOperador() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Colaborador getColaborador() { return colaborador; }
    public void setColaborador(Colaborador c) { this.colaborador = c; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}