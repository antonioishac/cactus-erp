package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.PdvCaixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PdvCaixaRepository extends JpaRepository<PdvCaixa, Long> {
    List<PdvCaixa> findAllByOrderByNomeAsc();
}