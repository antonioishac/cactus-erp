package br.com.cactusdigital.erp.ged.repository;

import br.com.cactusdigital.erp.common.domain.Situacao;
import br.com.cactusdigital.erp.ged.entity.Documento;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    Page<Documento> findByEntidadeTipoAndEntidadeId(String entidadeTipo, Long entidadeId, Pageable pageable);

    /** {@code LEFT JOIN FETCH} pra evitar N+1 ao montar a página com as versões já
     * carregadas — ver {@link br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT d FROM Documento d LEFT JOIN FETCH d.versoes WHERE d.id IN :ids")
    List<Documento> buscarComVersoes(@Param("ids") List<Long> ids);

    /**
     * Variante não paginada pra exportação (documento inteiro, não uma página de grade)
     * — filtro por pasta/tipo/situação, todos opcionais. {@code JOIN FETCH} de
     * {@code pasta}/{@code tipoDocumento} (singulares, resolvidos por
     * {@code GedMapper#toResponse} via {@code pastaNome}/{@code tipoDocumentoNome}) E de
     * {@code versoes} (bag) na MESMA query — só 1 bag aqui, não colide com
     * {@code MultipleBagFetchException} (que só dispara com 2+ bags juntas). Sem isso, o
     * mapper dispararia 1 SELECT por linha só pra resolver os nomes e as versões — mesmo
     * achado real já corrigido em {@code cadastro-base-service.RelatorioCadastroService}
     * (hidratar-ou-não não é opcional quando o mapper sempre monta o record inteiro).
     */
    @Query("""
            SELECT DISTINCT d FROM Documento d
            LEFT JOIN FETCH d.pasta LEFT JOIN FETCH d.tipoDocumento LEFT JOIN FETCH d.versoes
            WHERE (:pastaId IS NULL OR d.pasta.id = :pastaId)
            AND (:tipoDocumentoId IS NULL OR d.tipoDocumento.id = :tipoDocumentoId)
            AND (:situacao IS NULL OR d.situacao = :situacao)
            """)
    List<Documento> buscarParaRelatorio(
            @Param("pastaId") Long pastaId, @Param("tipoDocumentoId") Long tipoDocumentoId, @Param("situacao") Situacao situacao);
}
