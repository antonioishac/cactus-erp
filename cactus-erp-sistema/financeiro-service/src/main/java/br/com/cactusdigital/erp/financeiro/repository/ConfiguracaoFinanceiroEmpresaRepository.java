package br.com.cactusdigital.erp.financeiro.repository;

import br.com.cactusdigital.erp.financeiro.entity.ConfiguracaoFinanceiroEmpresa;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracaoFinanceiroEmpresaRepository extends JpaRepository<ConfiguracaoFinanceiroEmpresa, Long> {

    Optional<ConfiguracaoFinanceiroEmpresa> findByEmpresaId(Long empresaId);
}
