package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.ProdutoMarca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoMarcaRepository extends JpaRepository<ProdutoMarca, Long> {

    Page<ProdutoMarca> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
