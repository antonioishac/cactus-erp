package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * {@code PRODUTO_IMAGEM} do cactus-pdv — galeria do produto. Guarda só a referência
 * ({@code url}), não o binário: armazenamento de arquivo em si é responsabilidade do
 * futuro {@code gestao-documentos-service} (Fase 4, GED com armazenamento abstraído por
 * interface) — aqui a imagem já entra como URL de algo já hospedado. Upload direto fica
 * pendente até o GED existir.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "produto_imagem")
public class ProdutoImagem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    /** SHA-256 do arquivo, pra deduplicar a mesma imagem re-enviada. */
    @Column(name = "hash_arquivo", length = 64)
    private String hashArquivo;

    @Builder.Default
    @Column(name = "principal", nullable = false)
    private boolean principal = false;

    @Builder.Default
    @Column(name = "ordem", nullable = false)
    private Integer ordem = 0;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
