package br.com.cactusdigital.erp.vendas.repository;

import br.com.cactusdigital.erp.vendas.entity.ComissaoObjetivo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComissaoObjetivoRepository extends JpaRepository<ComissaoObjetivo, Long> {

    List<ComissaoObjetivo> findByComissaoPerfilId(Long comissaoPerfilId);

    Optional<ComissaoObjetivo> findByComissaoPerfilIdAndProdutoId(Long comissaoPerfilId, Long produtoId);
}
