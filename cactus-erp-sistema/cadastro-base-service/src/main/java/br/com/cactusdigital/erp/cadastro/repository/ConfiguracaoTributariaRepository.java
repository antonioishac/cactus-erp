package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.ConfiguracaoTributaria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConfiguracaoTributariaRepository extends JpaRepository<ConfiguracaoTributaria, Long> {

    boolean existsByGrupoTributarioIdAndOperacaoFiscalId(Long grupoTributarioId, Long operacaoFiscalId);

    /** Usados por {@code ConfiguracaoTributariaService#listar} — os 2 filtros opcionais do
     * `GET /tributacao/configuracoes` (achado real: o serviço ignorava os 2 params e sempre
     * chamava `findAll`, mesmo com a Swagger documentando o filtro). */
    Page<ConfiguracaoTributaria> findByGrupoTributarioId(Long grupoTributarioId, Pageable pageable);

    Page<ConfiguracaoTributaria> findByOperacaoFiscalId(Long operacaoFiscalId, Pageable pageable);

    Page<ConfiguracaoTributaria> findByGrupoTributarioIdAndOperacaoFiscalId(Long grupoTributarioId, Long operacaoFiscalId, Pageable pageable);

    /** {@code LEFT JOIN FETCH} evita N+1 ao montar a resposta com a lista de ICMS por UF já carregada. */
    @Query("""
            SELECT DISTINCT c FROM ConfiguracaoTributaria c
            LEFT JOIN FETCH c.icmsPorUf
            WHERE c.id = :id
            """)
    Optional<ConfiguracaoTributaria> buscarComIcmsPorId(@Param("id") Long id);

    @Query("""
            SELECT DISTINCT c FROM ConfiguracaoTributaria c
            LEFT JOIN FETCH c.icmsPorUf
            WHERE c.grupoTributario.id = :grupoTributarioId AND c.operacaoFiscal.id = :operacaoFiscalId
            """)
    Optional<ConfiguracaoTributaria> buscarComIcmsPorGrupoEOperacao(
            @Param("grupoTributarioId") Long grupoTributarioId, @Param("operacaoFiscalId") Long operacaoFiscalId);

    /** Evita N+1 numa listagem paginada: {@code icmsPorUf} (coleção) + {@code pis}/
     * {@code cofins}/{@code ipi} (1:1 inverso — lazy `@OneToOne(mappedBy=...)` sem
     * bytecode enhancement dispara 1 query cada quando acessado, mesmo problema de fundo
     * do N+1 aplicado a relação singular em vez de coleção). Combinar 1 coleção + N
     * relações singulares na mesma query é seguro (só coleção duplicada dispara
     * {@code MultipleBagFetchException}). Chamada só pelo efeito colateral, ver
     * {@code ClienteRepository#hidratarEnderecos}. */
    @Query("""
            SELECT DISTINCT c FROM ConfiguracaoTributaria c
            LEFT JOIN FETCH c.icmsPorUf
            LEFT JOIN FETCH c.pis
            LEFT JOIN FETCH c.cofins
            LEFT JOIN FETCH c.ipi
            WHERE c.id IN :ids
            """)
    List<ConfiguracaoTributaria> hidratarColecoes(@Param("ids") List<Long> ids);
}
