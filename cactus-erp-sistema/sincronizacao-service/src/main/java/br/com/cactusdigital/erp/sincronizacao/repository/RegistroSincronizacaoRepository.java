package br.com.cactusdigital.erp.sincronizacao.repository;

import br.com.cactusdigital.erp.sincronizacao.entity.RegistroSincronizacao;
import br.com.cactusdigital.erp.sincronizacao.entity.StatusRegistroSincronizacao;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RegistroSincronizacaoRepository extends JpaRepository<RegistroSincronizacao, Long> {

    Optional<RegistroSincronizacao> findByChaveIdempotencia(String chaveIdempotencia);

    /** Usado pelo {@code OutboxRelayService} — registros ainda não publicados no RabbitMQ. */
    List<RegistroSincronizacao> findByStatus(StatusRegistroSincronizacao status);

    @Query("""
            SELECT r FROM RegistroSincronizacao r
            WHERE (:status IS NULL OR r.status = :status)
              AND (:lojaId IS NULL OR r.lojaId = :lojaId)
            ORDER BY r.id DESC
            """)
    Page<RegistroSincronizacao> buscar(@Param("status") StatusRegistroSincronizacao status, @Param("lojaId") Long lojaId, Pageable pageable);
}
