package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.EstoqueMovimento;
import br.com.cactuserp.pdv.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstoqueMovimentoRepository extends JpaRepository<EstoqueMovimento, Long> {

    /** Histórico de movimentações de um produto, mais recente primeiro. */
    List<EstoqueMovimento> findByProdutoOrderByIdDesc(Produto produto);

    /** Idempotência: evita gerar dois movimentos de saída pro mesmo item de venda. */
    boolean existsByVendaDetalheId(Long idVendaDetalhe);
}
