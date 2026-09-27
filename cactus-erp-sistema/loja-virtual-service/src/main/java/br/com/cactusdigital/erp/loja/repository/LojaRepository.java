package br.com.cactusdigital.erp.loja.repository;

import br.com.cactusdigital.erp.loja.entity.Loja;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LojaRepository extends JpaRepository<Loja, Long> {

    boolean existsBySubdominio(String subdominio);

    boolean existsBySubdominioAndIdNot(String subdominio, Long id);
}
