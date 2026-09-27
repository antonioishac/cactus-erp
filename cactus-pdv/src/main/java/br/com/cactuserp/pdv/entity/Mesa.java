package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;

/**
 * Cadastro de uma mesa física da loja.
 *
 * <p>READ-ONLY no PDV — quem cadastra/edita é o sistema back-office,
 * que replica via mensageria.
 *
 * <p>A mesa NÃO carrega o status "ocupada/livre" diretamente — isso
 * vem da existência de uma {@link ContaAberta} em status A vinculada
 * a ela. Mesa só guarda o cadastro físico (número, setor, capacidade).
 */
@Entity
@Table(name = "MESA")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NUMERO", nullable = false)
    private Integer numero;

    @Column(name = "SETOR", length = 50)
    private String setor;

    @Column(name = "CAPACIDADE")
    private Integer capacidade;

    /** A=Ativa (aparece no PDV), I=Inativa */
    @Column(name = "SITUACAO", length = 1, nullable = false)
    private String situacao;

    public Mesa() {}

    public boolean isAtiva() { return "A".equals(situacao); }

    // ====== Getters / Setters ======
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public String getSetor() { return setor; }
    public void setSetor(String setor) { this.setor = setor; }

    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }

    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
}