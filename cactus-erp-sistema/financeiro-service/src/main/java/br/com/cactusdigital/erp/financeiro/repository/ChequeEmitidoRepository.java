package br.com.cactusdigital.erp.financeiro.repository;

import br.com.cactusdigital.erp.financeiro.entity.ChequeEmitido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChequeEmitidoRepository extends JpaRepository<ChequeEmitido, Long> {

    Page<ChequeEmitido> findByContaBancariaId(Long contaBancariaId, Pageable pageable);
}
