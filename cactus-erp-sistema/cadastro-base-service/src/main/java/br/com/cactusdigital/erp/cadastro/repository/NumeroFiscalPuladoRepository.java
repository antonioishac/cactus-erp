package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.NumeroFiscalPulado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NumeroFiscalPuladoRepository extends JpaRepository<NumeroFiscalPulado, Long> {

    Page<NumeroFiscalPulado> findByEmpresaId(Long empresaId, Pageable pageable);
}
