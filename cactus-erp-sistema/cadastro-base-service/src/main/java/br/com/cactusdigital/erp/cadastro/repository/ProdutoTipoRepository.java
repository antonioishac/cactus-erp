package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.ProdutoTipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoTipoRepository extends JpaRepository<ProdutoTipo, Long> {

    Page<ProdutoTipo> findByDescricaoContainingIgnoreCase(String descricao, Pageable pageable);
}
