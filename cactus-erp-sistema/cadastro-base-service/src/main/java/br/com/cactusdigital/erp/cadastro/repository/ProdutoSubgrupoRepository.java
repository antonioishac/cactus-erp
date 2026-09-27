package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.ProdutoSubgrupo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoSubgrupoRepository extends JpaRepository<ProdutoSubgrupo, Long> {

    Page<ProdutoSubgrupo> findByProdutoGrupoIdAndNomeContainingIgnoreCase(Long produtoGrupoId, String nome, Pageable pageable);

    Page<ProdutoSubgrupo> findByProdutoGrupoId(Long produtoGrupoId, Pageable pageable);
}
