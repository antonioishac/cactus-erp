package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.FormaPagamento;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FormaPagamentoRepository extends JpaRepository<FormaPagamento, Long> {

    Page<FormaPagamento> findByDescricaoContainingIgnoreCase(String descricao, Pageable pageable);

    /** {@code desde} = cursor de sincronização incremental (nulo = sem filtro). */
    @Query("SELECT f FROM FormaPagamento f WHERE (:desde IS NULL OR f.dataAtualizacao > :desde)")
    Page<FormaPagamento> buscarDesde(@Param("desde") Instant desde, Pageable pageable);
}
