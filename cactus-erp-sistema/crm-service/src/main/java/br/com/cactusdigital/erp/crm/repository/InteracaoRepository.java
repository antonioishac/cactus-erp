package br.com.cactusdigital.erp.crm.repository;

import br.com.cactusdigital.erp.crm.entity.Interacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InteracaoRepository extends JpaRepository<Interacao, Long> {

    Page<Interacao> findByLead_Id(Long leadId, Pageable pageable);

    Page<Interacao> findByOportunidade_Id(Long oportunidadeId, Pageable pageable);
}
