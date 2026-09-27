package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.Cfop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CfopRepository extends JpaRepository<Cfop, Long> {

    Page<Cfop> findByDescricaoContainingIgnoreCase(String descricao, Pageable pageable);
}
