package br.com.cactusdigital.erp.loja.repository;

import br.com.cactusdigital.erp.loja.entity.CategoriaLoja;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaLojaRepository extends JpaRepository<CategoriaLoja, Long> {

    Page<CategoriaLoja> findByLojaId(Long lojaId, Pageable pageable);
}
