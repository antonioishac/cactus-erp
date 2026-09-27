package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.EmpresaConfiguracaoFiscal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaConfiguracaoFiscalRepository extends JpaRepository<EmpresaConfiguracaoFiscal, Long> {

    Optional<EmpresaConfiguracaoFiscal> findByEmpresaId(Long empresaId);
}
