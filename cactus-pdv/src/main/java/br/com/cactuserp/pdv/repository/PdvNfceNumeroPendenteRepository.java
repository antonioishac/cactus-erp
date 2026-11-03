package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.PdvNfceNumeroPendente;
import br.com.cactuserp.pdv.entity.SituacaoNumeroPendente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PdvNfceNumeroPendenteRepository extends JpaRepository<PdvNfceNumeroPendente, Long> {

    List<PdvNfceNumeroPendente> findBySituacaoOrderByDataRegistroAsc(SituacaoNumeroPendente situacao);

    boolean existsByIdPdvCaixaAndSerieAndNumero(Long idPdvCaixa, Integer serie, Long numero);
}
