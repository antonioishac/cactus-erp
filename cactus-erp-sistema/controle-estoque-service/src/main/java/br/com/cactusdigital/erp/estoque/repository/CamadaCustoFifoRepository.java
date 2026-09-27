package br.com.cactusdigital.erp.estoque.repository;

import br.com.cactusdigital.erp.estoque.entity.CamadaCustoFifo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CamadaCustoFifoRepository extends JpaRepository<CamadaCustoFifo, Long> {

    /** Ordem de consumo FIFO: camada mais antiga primeiro, só as que ainda têm saldo. */
    List<CamadaCustoFifo> findByProdutoIdAndDepositoIdAndQuantidadeRestanteGreaterThanOrderByDataEntradaAsc(
            Long produtoId, Long depositoId, java.math.BigDecimal quantidadeMinima);
}
