package br.com.cactuserp.pdv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Locale;
import java.util.Set;

/**
 * Unidade de medida de um {@link Produto} (UN, KG, LT, GR, ML, etc).
 *
 * <p>Mapeia {@code PRODUTO_UNIDADE} — tabela que já existia no schema (ver
 * {@code PDV.md}) desde antes da fase 7, mas sem entidade de verdade
 * (existia só um stub vazio). {@code Produto.ID_PRODUTO_UNIDADE} continua
 * mapeado como {@code Long} cru em {@link Produto} (consistente com o
 * resto daquela entidade, que é toda flat, sem relações JPA) — quem
 * precisa saber se um produto é vendido por peso usa
 * {@link br.com.cactuserp.pdv.service.BalancaService#vendidoPorPeso}, que
 * consulta esta entidade pelo ID.
 *
 * <p><b>Duas perguntas diferentes, não confundir</b> (bug real corrigido
 * 2026-08-23 — LT ficava travado tentando ler balança física):
 * <ul>
 *   <li>{@link #podeSerFracionado()} — {@code PODE_FRACIONAR='S'} — a
 *       unidade admite quantidade com casas decimais (KG, LT, GR, ML).
 *       Não diz NADA sobre balança.</li>
 *   <li>{@link #ehUnidadeDePeso()} — a unidade é de MASSA (KG/GR) e
 *       portanto pode ser lida numa balança física. LT e ML são
 *       fracionáveis (dá pra vender 1,5 litro) mas ninguém põe um litro
 *       de óleo numa balança de checkout — a quantidade desses é sempre
 *       digitada manualmente. É esta pergunta, não
 *       {@code podeSerFracionado()}, que decide se o PDV tenta ler a
 *       balança.</li>
 * </ul>
 */
@Entity
@Table(name = "PRODUTO_UNIDADE")
public class ProdutoUnidade {

    /** Siglas de unidade de massa — as únicas que fazem sentido pesar numa balança de checkout. */
    private static final Set<String> SIGLAS_DE_PESO = Set.of("KG", "G", "GR", "GRAMA", "GRAMAS");

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "SIGLA", length = 10)
    private String sigla;

    @Column(name = "DESCRICAO", length = 250)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "PODE_FRACIONAR", length = 1, columnDefinition = "CHAR(1)")
    private SimNao podeFracionar;

    public ProdutoUnidade() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSigla() { return sigla; }
    public void setSigla(String sigla) { this.sigla = sigla; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public SimNao getPodeFracionar() { return podeFracionar; }
    public void setPodeFracionar(SimNao podeFracionar) { this.podeFracionar = podeFracionar; }

    /** {@code true} se a unidade admite quantidade fracionária (peso/volume) — ver ressalva na classe. */
    public boolean podeSerFracionado() {
        return podeFracionar != null && podeFracionar.ehSim();
    }

    /** {@code true} só pra unidade de MASSA (KG/GR) — a que decide se o PDV tenta ler balança física. */
    public boolean ehUnidadeDePeso() {
        return sigla != null && SIGLAS_DE_PESO.contains(sigla.trim().toUpperCase(Locale.ROOT));
    }
}
