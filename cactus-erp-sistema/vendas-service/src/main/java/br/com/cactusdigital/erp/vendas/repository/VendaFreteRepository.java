package br.com.cactusdigital.erp.vendas.repository;

import br.com.cactusdigital.erp.vendas.entity.VendaFrete;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendaFreteRepository extends JpaRepository<VendaFrete, Long> {

    Optional<VendaFrete> findByVendaId(Long vendaId);
}
