package br.com.cactusdigital.erp.contabilidade.repository;

import br.com.cactusdigital.erp.contabilidade.entity.CentroCusto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CentroCustoRepository extends JpaRepository<CentroCusto, Long> {

    Page<CentroCusto> findByEmpresaId(Long empresaId, Pageable pageable);

    List<CentroCusto> findByEmpresaIdOrderByCodigoAsc(Long empresaId);

    boolean existsByEmpresaIdAndCodigo(Long empresaId, String codigo);
}
