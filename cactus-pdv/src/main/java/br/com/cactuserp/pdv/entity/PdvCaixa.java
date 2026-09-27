package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "PDV_CAIXA")
public class PdvCaixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOME", length = 30)
    private String nome;

    @Column(name = "DATA_CADASTRO")
    private LocalDate dataCadastro;

    public PdvCaixa() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate d) { this.dataCadastro = d; }

    @Override
    public String toString() {
        return nome != null ? nome : ("Caixa " + id);
    }
}