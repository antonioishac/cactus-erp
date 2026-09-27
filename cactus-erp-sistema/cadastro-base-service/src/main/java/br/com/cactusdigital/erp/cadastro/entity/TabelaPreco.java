package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Mecanismo de precificação por canal/perfil (ex: "Varejo", "Atacado", "Cliente VIP") —
 * validado contra {@code fenix.sql.TABELA_PRECO}. **Complementar**, não substituto, de
 * {@link ProdutoFaixaPreco} (que é desconto por quantidade/promoção por vigência dentro
 * de UMA venda) — ver ANÁLISE de validação cruzada com o fenix.sql no CLAUDE.md.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "tabela_preco")
public class TabelaPreco extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 100, unique = true)
    private String nome;

    @Column(name = "descricao", length = 250)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ajuste", nullable = false, length = 30)
    private TipoAjustePreco tipoAjuste;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;

    @Builder.Default
    @OneToMany(mappedBy = "tabelaPreco", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TabelaPrecoProduto> produtos = new ArrayList<>();
}
