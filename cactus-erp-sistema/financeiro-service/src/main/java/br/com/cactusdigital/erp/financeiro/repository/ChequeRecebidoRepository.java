package br.com.cactusdigital.erp.financeiro.repository;

import br.com.cactusdigital.erp.financeiro.entity.ChequeRecebido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChequeRecebidoRepository extends JpaRepository<ChequeRecebido, Long> {

    Page<ChequeRecebido> findByClienteId(Long clienteId, Pageable pageable);
}
