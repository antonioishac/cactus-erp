package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.GrupoTributario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrupoTributarioRepository extends JpaRepository<GrupoTributario, Long> {

    Page<GrupoTributario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
