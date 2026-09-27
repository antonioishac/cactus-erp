package br.com.cactusdigital.erp.financeiro.repository;

import br.com.cactusdigital.erp.financeiro.entity.BaixaContaPagar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaixaContaPagarRepository extends JpaRepository<BaixaContaPagar, Long> {
}
