package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.PdvConfiguracaoBalanca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PdvConfiguracaoBalancaRepository extends JpaRepository<PdvConfiguracaoBalanca, Long> {

    Optional<PdvConfiguracaoBalanca> findFirstByConfiguracaoId(Long idConfiguracao);
}
