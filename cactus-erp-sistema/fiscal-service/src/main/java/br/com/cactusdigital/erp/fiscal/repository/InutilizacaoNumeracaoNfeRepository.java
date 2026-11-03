package br.com.cactusdigital.erp.fiscal.repository;

import br.com.cactusdigital.erp.fiscal.entity.InutilizacaoNumeracaoNfe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InutilizacaoNumeracaoNfeRepository extends JpaRepository<InutilizacaoNumeracaoNfe, Long> {

    Page<InutilizacaoNumeracaoNfe> findByEmpresaId(Long empresaId, Pageable pageable);

    /** Há faixa HOMOLOGADA da mesma empresa/série que se sobrepõe a {@code [inicial, final]}? */
    @Query("""
            SELECT COUNT(i) > 0 FROM InutilizacaoNumeracaoNfe i
            WHERE i.empresaId = :empresaId AND i.serie = :serie
              AND i.situacao = br.com.cactusdigital.erp.fiscal.entity.SituacaoInutilizacao.HOMOLOGADA
              AND i.numeroInicial <= :final AND i.numeroFinal >= :inicial
            """)
    boolean existeHomologadaSobreposta(@Param("empresaId") Long empresaId, @Param("serie") String serie,
                                       @Param("inicial") Long inicial, @Param("final") Long numeroFinal);
}
