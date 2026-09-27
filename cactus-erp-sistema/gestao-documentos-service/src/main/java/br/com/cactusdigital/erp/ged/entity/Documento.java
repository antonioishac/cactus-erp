package br.com.cactusdigital.erp.ged.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * O documento em si (equivalente ao {@code GED_DOCUMENTO_DETALHE} do fenix) — o conteúdo
 * físico de cada versão enviada mora em {@link DocumentoVersao}, nunca aqui.
 * {@code entidadeTipo}/{@code entidadeId} são o vínculo genérico cross-serviço: qualquer
 * módulo (Produto, Fornecedor, Pedido de Compra...) pode anexar um documento a uma
 * entidade própria sem o GED precisar conhecer o tipo dela — mesmo idioma
 * {@code origemTipo}/{@code origemId} já usado no resto do monorepo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "documento")
public class Documento extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pasta_id")
    private PastaDocumento pasta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_documento_id", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "descricao", length = 250)
    private String descricao;

    @Column(name = "palavras_chave", length = 250)
    private String palavrasChave;

    @Column(name = "entidade_tipo", length = 50)
    private String entidadeTipo;

    @Column(name = "entidade_id")
    private Long entidadeId;

    @Builder.Default
    @Column(name = "permite_exclusao", nullable = false)
    private boolean permiteExclusao = true;

    @Builder.Default
    @Column(name = "permite_alteracao", nullable = false)
    private boolean permiteAlteracao = true;

    @Builder.Default
    @Column(name = "assinado", nullable = false)
    private boolean assinado = false;

    @Column(name = "data_fim_vigencia")
    private LocalDate dataFimVigencia;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;

    @Builder.Default
    @OneToMany(mappedBy = "documento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("numeroVersao DESC")
    private List<DocumentoVersao> versoes = new ArrayList<>();
}
