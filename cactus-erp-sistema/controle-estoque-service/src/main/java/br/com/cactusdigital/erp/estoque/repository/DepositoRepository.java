package br.com.cactusdigital.erp.estoque.repository;

import br.com.cactusdigital.erp.estoque.entity.Deposito;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositoRepository extends JpaRepository<Deposito, Long> {

    Page<Deposito> findByEmpresaId(Long empresaId, Pageable pageable);

    List<Deposito> findByEmpresaIdOrderByCodigoAsc(Long empresaId);

    Optional<Deposito> findByEmpresaIdAndCodigo(Long empresaId, String codigo);
}
