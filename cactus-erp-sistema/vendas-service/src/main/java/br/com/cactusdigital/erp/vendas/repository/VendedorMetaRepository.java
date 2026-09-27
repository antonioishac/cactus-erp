package br.com.cactusdigital.erp.vendas.repository;

import br.com.cactusdigital.erp.vendas.entity.VendedorMeta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendedorMetaRepository extends JpaRepository<VendedorMeta, Long> {

    List<VendedorMeta> findByVendedorId(Long vendedorId);
}
