package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvConfiguracao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PdvConfiguracaoRepository extends JpaRepository<PdvConfiguracao, Long> {

    /** Busca a configuração vinculada a um caixa específico. */
    Optional<PdvConfiguracao> findFirstByCaixa(PdvCaixa caixa);

    /** Busca a configuração vinculada a um caixa pelo ID dele. */
    Optional<PdvConfiguracao> findFirstByCaixaId(Long caixaId);
}