package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.ConfiguracaoOperacionalLoja;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracaoOperacionalLojaRepository extends JpaRepository<ConfiguracaoOperacionalLoja, Long> {

    Optional<ConfiguracaoOperacionalLoja> findByEmpresaId(Long empresaId);
}
