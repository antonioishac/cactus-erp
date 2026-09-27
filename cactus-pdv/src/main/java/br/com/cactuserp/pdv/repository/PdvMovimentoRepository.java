package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.PdvMovimento;
import br.com.cactuserp.pdv.entity.StatusMovimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PdvMovimentoRepository extends JpaRepository<PdvMovimento, Long> {

    Optional<PdvMovimento> findFirstByCaixaIdAndStatusMovimentoOrderByIdDesc(
            Long caixaId, StatusMovimento status);

    Optional<PdvMovimento> findFirstByOperadorIdAndStatusMovimentoOrderByIdDesc(
            Long operadorId, StatusMovimento status);
}