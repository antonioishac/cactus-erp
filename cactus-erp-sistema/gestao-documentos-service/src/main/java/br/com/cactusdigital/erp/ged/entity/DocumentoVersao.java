package br.com.cactusdigital.erp.ged.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Cada arquivo físico enviado — {@code chaveArmazenamento} é opaca (resolvida por
 * {@link DocumentoStorageService}, nunca um caminho de filesystem exposto direto).
 * Nula em {@code tipoAcao=EXCLUSAO} (evento sem novo arquivo).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "documento_versao")
public class DocumentoVersao extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documento_id", nullable = false)
    private Documento documento;

    /** Quem enviou esta versão — vem da claim {@code usuarioId} do JWT, nunca do corpo
     * da requisição. */
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "numero_versao", nullable = false)
    private Integer numeroVersao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_acao", nullable = false, length = 10)
    private TipoAcaoVersao tipoAcao;

    @Column(name = "hash_arquivo", length = 64)
    private String hashArquivo;

    @Column(name = "chave_armazenamento", length = 250)
    private String chaveArmazenamento;

    @Column(name = "nome_arquivo_original", length = 255)
    private String nomeArquivoOriginal;

    @Column(name = "tamanho_bytes")
    private Long tamanhoBytes;

    @Column(name = "content_type", length = 150)
    private String contentType;

    @Column(name = "observacao", length = 250)
    private String observacao;
}
