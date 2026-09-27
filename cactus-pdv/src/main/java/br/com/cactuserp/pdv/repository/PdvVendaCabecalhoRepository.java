package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.Cliente;
import br.com.cactuserp.pdv.entity.PdvMovimento;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.StatusVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositório de cabeçalhos de venda ({@link PdvVendaCabecalho}).
 *
 * <p>Concentra as consultas usadas pelo VendaService para gerenciar o ciclo
 * de vida do cupom: abrir, recuperar pendência, listar do movimento, etc.
 */
@Repository
public interface PdvVendaCabecalhoRepository extends JpaRepository<PdvVendaCabecalho, Long> {

    /**
     * Busca o cupom em aberto (status A) do movimento informado.
     *
     * <p>Usado na <b>recuperação após queda</b>: ao inicializar o PDV, se
     * existir cupom 'A' no movimento ativo, oferecemos ao operador a opção
     * de retomar ou cancelar.
     *
     * <p>A regra de negócio garante no máximo 1 cupom aberto por movimento
     * — se houver mais de um (situação anômala), retornamos o mais recente.
     */
    Optional<PdvVendaCabecalho> findFirstByPdvMovimentoAndStatusVendaOrderByIdDesc(
            PdvMovimento pdvMovimento, StatusVenda statusVenda);

    /**
     * Lista todas as vendas de um movimento, em ordem cronológica.
     * Usado pelo fechamento de caixa para somar totais.
     */
    List<PdvVendaCabecalho> findByPdvMovimentoOrderByIdAsc(PdvMovimento pdvMovimento);

    /**
     * Lista vendas de um movimento filtradas por status.
     * Útil para relatórios e para o fechamento Z (apenas status F).
     */
    List<PdvVendaCabecalho> findByPdvMovimentoAndStatusVendaOrderByIdAsc(
            PdvMovimento pdvMovimento, StatusVenda statusVenda);

    /**
     * Conta quantas vendas existem no movimento com determinado status.
     * Útil para validações ("não pode fechar movimento com cupom em aberto").
     */
    long countByPdvMovimentoAndStatusVenda(PdvMovimento pdvMovimento, StatusVenda statusVenda);

    /**
     * Soma o {@code VALOR_TOTAL_DOCUMENTO} das vendas FECHADAS do movimento.
     * Usado pelo relatório Z (fechamento de caixa) — vendas com status F
     * representam dinheiro efetivamente entrado.
     */
    @Query("SELECT COALESCE(SUM(v.valorTotalDocumento), 0) "
            + "FROM PdvVendaCabecalho v "
            + "WHERE v.pdvMovimento = :movimento "
            + "  AND v.statusVenda = br.com.cactuserp.pdv.entity.StatusVenda.F")
    Optional<BigDecimal> somarTotalVendasFechadas(
            @Param("movimento") PdvMovimento movimento);

    /**
     * Soma o {@code VALOR_CANCELADO} das vendas do movimento — engloba
     * tanto cupons inteiros cancelados quanto itens cancelados dentro
     * de cupons que foram finalizados.
     */
    @Query("SELECT COALESCE(SUM(v.valorCancelado), 0) "
            + "FROM PdvVendaCabecalho v "
            + "WHERE v.pdvMovimento = :movimento")
    Optional<BigDecimal> somarTotalCancelado(
            @Param("movimento") PdvMovimento movimento);

    /**
     * Cupons com emissão fiscal pendente de reprocessamento
     * ({@code STATUS_FISCAL = 'P'}). Usado por
     * {@code ReprocessamentoFiscalService}.
     */
    List<PdvVendaCabecalho> findByStatusFiscal(String statusFiscal);

    /**
     * Busca o cupom com {@code cliente} já carregado (JOIN FETCH) — evita
     * {@code LazyInitializationException} quando o caller precisa acessar
     * os dados do cliente (ex: {@code FidelidadeService}) fora da
     * transação em que o cupom original foi salvo/finalizado.
     */
    @Query("SELECT v FROM PdvVendaCabecalho v LEFT JOIN FETCH v.cliente WHERE v.id = :id")
    Optional<PdvVendaCabecalho> buscarComCliente(@Param("id") Long id);

    /**
     * Conta quantas vendas FECHADAS um cliente já fez — usado por
     * {@code FidelidadeService} pro modo "pela quantidade de uso" (Q).
     */
    long countByClienteAndStatusVenda(Cliente cliente, StatusVenda statusVenda);

    /**
     * Soma o valor total documento das vendas FECHADAS de um cliente —
     * usado por {@code FidelidadeService} pro modo "pelo valor utilizado" (V).
     */
    @Query("SELECT COALESCE(SUM(v.valorTotalDocumento), 0) "
            + "FROM PdvVendaCabecalho v "
            + "WHERE v.cliente = :cliente "
            + "  AND v.statusVenda = br.com.cactuserp.pdv.entity.StatusVenda.F")
    Optional<BigDecimal> somarValorComprasDoCliente(@Param("cliente") Cliente cliente);
}