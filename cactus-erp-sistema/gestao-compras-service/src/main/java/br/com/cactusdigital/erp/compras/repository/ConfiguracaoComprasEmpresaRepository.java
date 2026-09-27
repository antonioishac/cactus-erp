package br.com.cactusdigital.erp.compras.repository;

import br.com.cactusdigital.erp.compras.entity.ConfiguracaoComprasEmpresa;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracaoComprasEmpresaRepository extends JpaRepository<ConfiguracaoComprasEmpresa, Long> {

    Optional<ConfiguracaoComprasEmpresa> findByEmpresaId(Long empresaId);
}
