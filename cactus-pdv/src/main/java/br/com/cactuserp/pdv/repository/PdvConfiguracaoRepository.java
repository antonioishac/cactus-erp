package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvConfiguracao;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PdvConfiguracaoRepository extends JpaRepository<PdvConfiguracao, Long> {

    /** Busca a configuração vinculada a um caixa específico. */
    Optional<PdvConfiguracao> findFirstByCaixa(PdvCaixa caixa);

    /** Busca a configuração vinculada a um caixa pelo ID dele. */
    Optional<PdvConfiguracao> findFirstByCaixaId(Long caixaId);

    /**
     * Configuração do caixa com {@code SELECT ... FOR UPDATE}: serializa a reserva do número da NFC-e do terminal
     * (duas vendas nunca recebem o mesmo número, mesmo com o job de reprocessamento rodando em paralelo).
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM PdvConfiguracao c WHERE c.caixa.id = :caixaId")
    Optional<PdvConfiguracao> travarPorCaixa(@Param("caixaId") Long caixaId);
}