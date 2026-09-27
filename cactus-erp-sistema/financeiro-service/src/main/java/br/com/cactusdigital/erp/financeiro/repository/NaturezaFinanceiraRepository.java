package br.com.cactusdigital.erp.financeiro.repository;

import br.com.cactusdigital.erp.financeiro.entity.NaturezaFinanceira;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NaturezaFinanceiraRepository extends JpaRepository<NaturezaFinanceira, Long> {

    List<NaturezaFinanceira> findByEmpresaIdAndAtivoTrue(Long empresaId);
}
