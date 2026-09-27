package br.com.cactusdigital.erp.compras.repository;

import br.com.cactusdigital.erp.compras.entity.FornecedorCotacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FornecedorCotacaoRepository extends JpaRepository<FornecedorCotacao, Long> {

    /** Hidrata {@code itens} dos fornecedores de uma ou mais cotações dentro da mesma
     * sessão (persistence context) — usada só pelo efeito colateral, não pelo valor de
     * retorno em si, ver {@code CotacaoCompraService#buscarComFornecedoresEItens}. */
    @Query("SELECT DISTINCT f FROM FornecedorCotacao f LEFT JOIN FETCH f.itens WHERE f.cotacao.id IN :cotacaoIds")
    List<FornecedorCotacao> carregarItensPorCotacaoIds(@Param("cotacaoIds") List<Long> cotacaoIds);
}
