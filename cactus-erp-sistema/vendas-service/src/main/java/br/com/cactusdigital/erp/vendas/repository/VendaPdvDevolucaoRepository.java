package br.com.cactusdigital.erp.vendas.repository;

import br.com.cactusdigital.erp.vendas.entity.VendaPdvDevolucao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendaPdvDevolucaoRepository extends JpaRepository<VendaPdvDevolucao, Long> {

    boolean existsByRegistroSincronizacaoId(Long registroSincronizacaoId);
}
