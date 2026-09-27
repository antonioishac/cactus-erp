package br.com.cactusdigital.erp.financeiro.repository;

import br.com.cactusdigital.erp.financeiro.entity.AplicacaoFinanceira;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AplicacaoFinanceiraRepository extends JpaRepository<AplicacaoFinanceira, Long> {

    Page<AplicacaoFinanceira> findByContaBancariaId(Long contaBancariaId, Pageable pageable);
}
