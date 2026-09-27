package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.Cest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CestRepository extends JpaRepository<Cest, Long> {

    Page<Cest> findByDescricaoContainingIgnoreCase(String descricao, Pageable pageable);
}
