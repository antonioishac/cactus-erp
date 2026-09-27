package br.com.cactusdigital.erp.estoque.repository;

import br.com.cactusdigital.erp.estoque.entity.InventarioFisicoItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioFisicoItemRepository extends JpaRepository<InventarioFisicoItem, Long> {

    List<InventarioFisicoItem> findByInventarioId(Long inventarioId);
}
