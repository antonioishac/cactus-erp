package br.com.cactusdigital.erp.financeiro.repository;

import br.com.cactusdigital.erp.financeiro.entity.ContaBancaria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, Long> {

    Page<ContaBancaria> findByEmpresaId(Long empresaId, Pageable pageable);

    List<ContaBancaria> findByEmpresaIdAndAtivoTrue(Long empresaId);

    /** Lock pessimista pra serializar concorrência sobre {@code saldoAtual} — dinheiro,
     * não é aceitável um lost-update entre duas baixas simultâneas na mesma conta. Usado
     * só dentro de {@code MovimentoCaixaBancoService#registrar}. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ContaBancaria c WHERE c.id = :id")
    Optional<ContaBancaria> buscarParaAtualizarSaldo(@Param("id") Long id);
}
