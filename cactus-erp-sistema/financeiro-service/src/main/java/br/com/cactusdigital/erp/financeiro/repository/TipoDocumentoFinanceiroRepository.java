package br.com.cactusdigital.erp.financeiro.repository;

import br.com.cactusdigital.erp.financeiro.entity.TipoDocumentoFinanceiro;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoDocumentoFinanceiroRepository extends JpaRepository<TipoDocumentoFinanceiro, Long> {

    List<TipoDocumentoFinanceiro> findByAtivoTrue();
}
