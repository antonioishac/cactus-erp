package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.PdvConferenciaCaixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PdvConferenciaCaixaRepository
        extends JpaRepository<PdvConferenciaCaixa, Long> {
}