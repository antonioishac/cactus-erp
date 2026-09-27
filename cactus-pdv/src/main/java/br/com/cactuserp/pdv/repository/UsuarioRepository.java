package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.PdvOperador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<PdvOperador, Long> {
    Optional<PdvOperador> findByLogin(String username);
    boolean existsByLogin(String username);
}