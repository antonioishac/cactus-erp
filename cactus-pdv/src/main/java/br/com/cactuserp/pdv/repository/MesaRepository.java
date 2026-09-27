package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório de mesas (cadastro READ-ONLY no PDV).
 */
@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    /** Lista todas as mesas ATIVAS, em ordem de número. */
    List<Mesa> findBySituacaoOrderByNumeroAsc(String situacao);

    /** Busca uma mesa pelo número (deve ser único entre ativas). */
    Optional<Mesa> findFirstByNumeroAndSituacao(Integer numero, String situacao);
}