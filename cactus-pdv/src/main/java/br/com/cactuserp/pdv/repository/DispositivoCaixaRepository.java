package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.DispositivoCaixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DispositivoCaixaRepository extends JpaRepository<DispositivoCaixa, Long> {

    Optional<DispositivoCaixa> findByHostnameAndAtivo(String hostname, String ativo);

    default Optional<DispositivoCaixa> findAtivoByHostname(String hostname) {
        return findByHostnameAndAtivo(hostname, "S");
    }
}