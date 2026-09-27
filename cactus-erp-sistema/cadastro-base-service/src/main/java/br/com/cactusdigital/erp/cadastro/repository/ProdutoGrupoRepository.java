package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.ProdutoGrupo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoGrupoRepository extends JpaRepository<ProdutoGrupo, Long> {

    Page<ProdutoGrupo> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
