package br.com.cactusdigital.erp.estoque.repository;

import br.com.cactusdigital.erp.estoque.entity.TransferenciaEstoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferenciaEstoqueRepository extends JpaRepository<TransferenciaEstoque, Long> {

    Page<TransferenciaEstoque> findByDepositoOrigemIdOrDepositoDestinoId(Long depositoOrigemId, Long depositoDestinoId, Pageable pageable);
}
