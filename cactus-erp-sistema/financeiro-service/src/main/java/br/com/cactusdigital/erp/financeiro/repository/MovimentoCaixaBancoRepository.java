package br.com.cactusdigital.erp.financeiro.repository;

import br.com.cactusdigital.erp.financeiro.entity.MovimentoCaixaBanco;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentoCaixaBancoRepository extends JpaRepository<MovimentoCaixaBanco, Long> {

    Page<MovimentoCaixaBanco> findByContaBancariaIdOrderByDataMovimentoDescIdDesc(Long contaBancariaId, Pageable pageable);

    /** Usado por {@code ConciliacaoBancariaService} — candidatos a match automático/lista de pendências. */
    List<MovimentoCaixaBanco> findByContaBancariaIdAndConciliadoFalse(Long contaBancariaId);
}
