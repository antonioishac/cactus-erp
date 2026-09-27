package br.com.cactusdigital.erp.ged.repository;

import br.com.cactusdigital.erp.ged.entity.TipoDocumento;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long> {

    Optional<TipoDocumento> findByNome(String nome);
}
