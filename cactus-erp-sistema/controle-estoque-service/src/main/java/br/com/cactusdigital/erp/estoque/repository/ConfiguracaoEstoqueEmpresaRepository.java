package br.com.cactusdigital.erp.estoque.repository;

import br.com.cactusdigital.erp.estoque.entity.ConfiguracaoEstoqueEmpresa;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracaoEstoqueEmpresaRepository extends JpaRepository<ConfiguracaoEstoqueEmpresa, Long> {

    Optional<ConfiguracaoEstoqueEmpresa> findByEmpresaId(Long empresaId);
}
