package br.com.cactusdigital.erp.bi.repository;

import br.com.cactusdigital.erp.bi.entity.DepositoDim;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositoDimRepository extends JpaRepository<DepositoDim, Long> {

    List<DepositoDim> findByDepositoIdIn(List<Long> depositoIds);
}
