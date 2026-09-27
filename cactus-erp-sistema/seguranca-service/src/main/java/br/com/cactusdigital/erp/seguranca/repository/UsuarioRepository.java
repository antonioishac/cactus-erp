package br.com.cactusdigital.erp.seguranca.repository;

import br.com.cactusdigital.erp.seguranca.entity.Usuario;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLogin(String login);

    Page<Usuario> findByNomeContainingIgnoreCaseOrLoginContainingIgnoreCase(String nome, String login, Pageable pageable);
}
