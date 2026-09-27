package br.com.cactusdigital.erp.contabilidade.repository;

import br.com.cactusdigital.erp.contabilidade.entity.PlanoConta;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanoContaRepository extends JpaRepository<PlanoConta, Long> {

    Page<PlanoConta> findByEmpresaId(Long empresaId, Pageable pageable);

    /** Sem paginação, pra popular `Select` no frontend — mesmo padrão de `size=200` já usado em todo lookup pequeno do monorepo. */
    List<PlanoConta> findByEmpresaIdOrderByCodigoAsc(Long empresaId);

    boolean existsByEmpresaIdAndCodigo(Long empresaId, String codigo);

    boolean existsByContaPaiId(Long contaPaiId);
}
