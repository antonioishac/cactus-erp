package br.com.cactusdigital.erp.seguranca.repository;

import br.com.cactusdigital.erp.seguranca.entity.Funcionalidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionalidadeRepository extends JpaRepository<Funcionalidade, Long> {

    Page<Funcionalidade> findByModuloId(Long moduloId, Pageable pageable);
}
