package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.Ncm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NcmRepository extends JpaRepository<Ncm, Long> {

    Page<Ncm> findByCodigoContainingOrDescricaoContainingIgnoreCase(String codigo, String descricao, Pageable pageable);
}
