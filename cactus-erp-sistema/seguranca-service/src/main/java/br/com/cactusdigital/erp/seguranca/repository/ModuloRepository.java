package br.com.cactusdigital.erp.seguranca.repository;

import br.com.cactusdigital.erp.seguranca.entity.Modulo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuloRepository extends JpaRepository<Modulo, Long> {

    Optional<Modulo> findByCodigo(String codigo);
}
