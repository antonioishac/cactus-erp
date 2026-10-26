package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.ClassificacaoTributariaIbsCbs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClassificacaoTributariaIbsCbsRepository extends JpaRepository<ClassificacaoTributariaIbsCbs, String> {

    /** Filtros opcionais: CST exato e busca livre no código ou na descrição. */
    @Query("""
            SELECT c FROM ClassificacaoTributariaIbsCbs c
            WHERE (:cst IS NULL OR c.cst.codigo = :cst)
              AND (:busca IS NULL OR c.codigo LIKE CONCAT(:busca, '%') OR LOWER(c.descricao) LIKE LOWER(CONCAT('%', :busca, '%')))
            ORDER BY c.codigo
            """)
    Page<ClassificacaoTributariaIbsCbs> buscar(@Param("cst") String cst, @Param("busca") String busca, Pageable pageable);
}
