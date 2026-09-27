package br.com.cactusdigital.erp.loja.repository;

import br.com.cactusdigital.erp.loja.entity.ClienteLoja;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteLojaRepository extends JpaRepository<ClienteLoja, Long> {

    Optional<ClienteLoja> findByLojaIdAndEmail(Long lojaId, String email);
}
