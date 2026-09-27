package br.com.cactusdigital.erp.vendas.repository;

import br.com.cactusdigital.erp.vendas.entity.VendaComissao;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendaComissaoRepository extends JpaRepository<VendaComissao, Long> {

    Page<VendaComissao> findByVendedorId(Long vendedorId, Pageable pageable);

    /** Usado pelo relatório de comissões por vendedor (Fase 17). */
    List<VendaComissao> findByDataLancamentoBetween(LocalDate dataInicio, LocalDate dataFim);
}
