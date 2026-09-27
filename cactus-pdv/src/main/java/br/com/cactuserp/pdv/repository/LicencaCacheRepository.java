package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.LicencaCache;
import br.com.cactuserp.pdv.entity.OrigemLicencaCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface LicencaCacheRepository extends JpaRepository<LicencaCache, Long> {

    /** Última linha gravada, de qualquer origem (R/F/L) — usada pra descobrir a "situação anterior". */
    Optional<LicencaCache> findFirstByOrderByIdDesc();

    /**
     * Última linha de verificação remota ou bloqueio forçado (R/F), ignorando
     * leituras locais (L). É essa que alimenta a janela de tolerância offline
     * em {@code LicencaService} — uma leitura local não pode "resetar" a
     * contagem de dias sem contato com a retaguarda.
     */
    Optional<LicencaCache> findFirstByOrigemInOrderByIdDesc(Collection<OrigemLicencaCache> origens);
}
