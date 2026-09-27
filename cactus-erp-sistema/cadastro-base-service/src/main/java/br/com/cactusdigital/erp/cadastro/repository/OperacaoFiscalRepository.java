package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.OperacaoFiscal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperacaoFiscalRepository extends JpaRepository<OperacaoFiscal, Long> {

    Page<OperacaoFiscal> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
