package br.com.cactusdigital.erp.compras.repository;

import br.com.cactusdigital.erp.compras.entity.PedidoCompraItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoCompraItemRepository extends JpaRepository<PedidoCompraItem, Long> {
}
