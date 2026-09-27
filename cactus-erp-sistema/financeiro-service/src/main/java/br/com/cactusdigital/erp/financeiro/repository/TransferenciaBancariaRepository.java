package br.com.cactusdigital.erp.financeiro.repository;

import br.com.cactusdigital.erp.financeiro.entity.TransferenciaBancaria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransferenciaBancariaRepository extends JpaRepository<TransferenciaBancaria, Long> {

    /** {@code contaId} pode ser origem OU destino — não é "origem igual a X E destino
     * igual a Y", é "qualquer lado desta conta". */
    @Query("SELECT t FROM TransferenciaBancaria t WHERE t.contaOrigemId = :contaId OR t.contaDestinoId = :contaId")
    Page<TransferenciaBancaria> findPorConta(@Param("contaId") Long contaId, Pageable pageable);
}
