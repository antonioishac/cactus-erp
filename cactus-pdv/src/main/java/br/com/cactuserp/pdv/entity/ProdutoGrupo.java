package br.com.cactuserp.pdv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "PRODUTO_GRUPO")
public class ProdutoGrupo {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOME", length = 100)
    private String nome;

    @Column(name = "DESCRICAO", length = 250)
    private String descricao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
