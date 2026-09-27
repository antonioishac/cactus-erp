package br.com.cactusdigital.erp.financeiro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

/** Um upload de arquivo de retorno CNAB 240 — ver {@code Cnab240RetornoParser}. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "retorno_bancario")
public class RetornoBancario extends BaseEntity {

    @Column(name = "conta_bancaria_id", nullable = false)
    private Long contaBancariaId;

    @Column(name = "nome_arquivo", length = 250)
    private String nomeArquivo;

    @Column(name = "quantidade_registros", nullable = false)
    private int quantidadeRegistros;

    @Builder.Default
    @OneToMany(mappedBy = "retorno", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RetornoBancarioItem> itens = new ArrayList<>();
}
