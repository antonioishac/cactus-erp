package br.com.cactusdigital.erp.crm.repository;

import br.com.cactusdigital.erp.crm.entity.EstagioFunil;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstagioFunilRepository extends JpaRepository<EstagioFunil, Long> {

    List<EstagioFunil> findAllByOrderByOrdemAsc();
}
