package br.com.cactusdigital.erp.financeiro.repository;

import br.com.cactusdigital.erp.financeiro.entity.ExtratoBancarioImportacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtratoBancarioImportacaoRepository extends JpaRepository<ExtratoBancarioImportacao, Long> {

    Page<ExtratoBancarioImportacao> findByContaBancariaId(Long contaBancariaId, Pageable pageable);
}
