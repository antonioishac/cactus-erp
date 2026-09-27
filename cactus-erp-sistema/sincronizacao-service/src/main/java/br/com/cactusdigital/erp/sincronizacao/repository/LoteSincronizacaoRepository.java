package br.com.cactusdigital.erp.sincronizacao.repository;

import br.com.cactusdigital.erp.sincronizacao.entity.LoteSincronizacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoteSincronizacaoRepository extends JpaRepository<LoteSincronizacao, Long> {

    Page<LoteSincronizacao> findByLojaId(Long lojaId, Pageable pageable);
}
