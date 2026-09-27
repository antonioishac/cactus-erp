package br.com.cactusdigital.erp.crm.repository;

import br.com.cactusdigital.erp.crm.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeadRepository extends JpaRepository<Lead, Long> {
}
