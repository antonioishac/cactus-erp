package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;
import br.com.cactuserp.pdv.entity.Produto;
import br.com.cactuserp.pdv.entity.SimNao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório de itens (detalhes) de venda ({@link PdvVendaDetalhe}).
 *
 * <p>Itens nunca são removidos fisicamente — cancelamento é via flag
 * {@link SimNao#S} no campo {@code cancelado} (exigência PAF-NFC-e).
 */
@Repository
public interface PdvVendaDetalheRepository extends JpaRepository<PdvVendaDetalhe, Long> {

    /**
     * Retorna todos os itens de um cupom, ordenados pela sequência no cupom.
     * Inclui cancelados — quem filtra é o consumidor.
     *
     * <p><b>Atenção:</b> usa proxy LAZY para o relacionamento com Produto.
     * Use {@link #buscarDetalhesComProduto(PdvVendaCabecalho)} quando for
     * acessar o produto fora da transação (ex: na UI após recuperação).
     */
    List<PdvVendaDetalhe> findByPdvVendaCabecalhoOrderByItemAsc(
            PdvVendaCabecalho pdvVendaCabecalho);

    /**
     * Igual ao {@link #findByPdvVendaCabecalhoOrderByItemAsc}, mas faz
     * JOIN FETCH no {@code Produto} — assim os dados do produto vêm
     * carregados e podem ser acessados fora da transação sem
     * {@code LazyInitializationException}.
     *
     * <p>Usado pela recuperação de cupom após queda, onde o controller
     * precisa ler {@code produto.getDescricaoPdv()} para repopular a UI.
     */
    @Query("SELECT d FROM PdvVendaDetalhe d "
            + "JOIN FETCH d.produto "
            + "WHERE d.pdvVendaCabecalho = :cupom "
            + "ORDER BY d.item ASC")
    List<PdvVendaDetalhe> buscarDetalhesComProduto(
            @Param("cupom") PdvVendaCabecalho cupom);

    /**
     * Retorna apenas os itens ATIVOS (não cancelados) de um cupom.
     */
    List<PdvVendaDetalhe> findByPdvVendaCabecalhoAndCanceladoOrderByItemAsc(
            PdvVendaCabecalho pdvVendaCabecalho, SimNao cancelado);

    /**
     * Conta quantos itens existem no cupom (cancelados ou não).
     * Útil para gerar o próximo número sequencial.
     */
    long countByPdvVendaCabecalho(PdvVendaCabecalho pdvVendaCabecalho);

    /**
     * Conta itens por status de cancelamento — para os contadores da UI
     * sem trazer as entidades inteiras.
     */
    long countByPdvVendaCabecalhoAndCancelado(
            PdvVendaCabecalho pdvVendaCabecalho, SimNao cancelado);

    /**
     * Busca o item NÃO cancelado de um produto específico num cupom.
     * Usado pelo agrupamento automático: se o operador bipa um produto
     * que já está no cupom, o service soma na linha existente em vez
     * de criar duplicada.
     *
     * <p>Importante: filtra por {@code cancelado = 'N'}. Se o operador
     * cancelou a linha anterior (F1) e bipa o produto de novo, deve
     * criar linha nova — não "ressuscitar" a cancelada.
     */
    Optional<PdvVendaDetalhe> findByPdvVendaCabecalhoAndProdutoAndCancelado(
            PdvVendaCabecalho cupom, Produto produto, SimNao cancelado);
}