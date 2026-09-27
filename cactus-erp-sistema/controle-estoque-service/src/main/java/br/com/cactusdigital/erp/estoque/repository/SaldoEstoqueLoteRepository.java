package br.com.cactusdigital.erp.estoque.repository;

import br.com.cactusdigital.erp.estoque.entity.SaldoEstoqueLote;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaldoEstoqueLoteRepository extends JpaRepository<SaldoEstoqueLote, Long> {

    Optional<SaldoEstoqueLote> findByProdutoIdAndDepositoIdAndLoteId(Long produtoId, Long depositoId, Long loteId);

    List<SaldoEstoqueLote> findByProdutoIdAndDepositoId(Long produtoId, Long depositoId);
}
