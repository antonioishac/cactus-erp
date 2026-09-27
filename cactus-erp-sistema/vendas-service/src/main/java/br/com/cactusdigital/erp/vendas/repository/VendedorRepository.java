package br.com.cactusdigital.erp.vendas.repository;

import br.com.cactusdigital.erp.vendas.entity.Vendedor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendedorRepository extends JpaRepository<Vendedor, Long> {

    boolean existsByColaboradorId(Long colaboradorId);

    Optional<Vendedor> findByColaboradorId(Long colaboradorId);
}
