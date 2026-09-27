package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.ContaAberta;
import br.com.cactuserp.pdv.entity.Mesa;
import br.com.cactuserp.pdv.entity.TipoContaAberta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContaAbertaRepository extends JpaRepository<ContaAberta, Long> {

    /**
     * Conta aberta atual de uma mesa (status A ou E).
     * Por regra, só pode haver UMA conta ativa por mesa.
     */
    @Query("SELECT c FROM ContaAberta c "
            + "WHERE c.mesa = :mesa "
            + "  AND c.status IN (br.com.cactuserp.pdv.entity.StatusContaAberta.A, "
            + "                   br.com.cactuserp.pdv.entity.StatusContaAberta.E) "
            + "ORDER BY c.id DESC")
    Optional<ContaAberta> buscarContaAtivaPorMesa(@Param("mesa") Mesa mesa);

    /**
     * Conta aberta atual de uma comanda (status A ou E).
     * Por regra, só pode haver UMA conta ativa por código de comanda.
     */
    @Query("SELECT c FROM ContaAberta c "
            + "WHERE c.codigoComanda = :codigo "
            + "  AND c.status IN (br.com.cactuserp.pdv.entity.StatusContaAberta.A, "
            + "                   br.com.cactuserp.pdv.entity.StatusContaAberta.E) "
            + "ORDER BY c.id DESC")
    Optional<ContaAberta> buscarContaAtivaPorComanda(@Param("codigo") String codigo);

    /**
     * Lista todas contas ativas (A ou E) — usado pelos paineis "salão"
     * e "comandas abertas" do PDV.
     */
    @Query("SELECT c FROM ContaAberta c "
            + "LEFT JOIN FETCH c.mesa "
            + "LEFT JOIN FETCH c.operadorAbertura "
            + "WHERE c.tipo = :tipo "
            + "  AND c.status IN (br.com.cactuserp.pdv.entity.StatusContaAberta.A, "
            + "                   br.com.cactuserp.pdv.entity.StatusContaAberta.E) "
            + "ORDER BY c.dataAbertura DESC, c.horaAbertura DESC, c.id DESC")
    List<ContaAberta> listarAtivasPorTipo(@Param("tipo") TipoContaAberta tipo);

    /** Lista TODAS contas ativas (qualquer tipo) — busca geral. */
    @Query("SELECT c FROM ContaAberta c "
            + "LEFT JOIN FETCH c.mesa "
            + "LEFT JOIN FETCH c.operadorAbertura "
            + "WHERE c.status IN (br.com.cactuserp.pdv.entity.StatusContaAberta.A, "
            + "                   br.com.cactuserp.pdv.entity.StatusContaAberta.E) "
            + "ORDER BY c.tipo, c.dataAbertura DESC, c.horaAbertura DESC")
    List<ContaAberta> listarAtivas();

    /** Conta com itens + mesa + produto carregados (uso da tela de detalhe). */
    @Query("SELECT c FROM ContaAberta c "
            + "LEFT JOIN FETCH c.mesa "
            + "LEFT JOIN FETCH c.itens i "
            + "LEFT JOIN FETCH i.produto "
            + "WHERE c.id = :id")
    Optional<ContaAberta> buscarComItens(@Param("id") Long id);
}