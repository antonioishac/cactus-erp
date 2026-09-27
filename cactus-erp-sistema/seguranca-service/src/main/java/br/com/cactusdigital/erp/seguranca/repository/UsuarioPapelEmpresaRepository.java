package br.com.cactusdigital.erp.seguranca.repository;

import br.com.cactusdigital.erp.seguranca.entity.UsuarioPapelEmpresa;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioPapelEmpresaRepository extends JpaRepository<UsuarioPapelEmpresa, Long> {

    List<UsuarioPapelEmpresa> findByUsuarioId(Long usuarioId);

    List<UsuarioPapelEmpresa> findByUsuarioIdOrderByIdAsc(Long usuarioId);

    /** Usado pelo pull de operador do sincronizacao-service (Fase 6) — todos os usuários
     * com papel numa empresa/loja, pra provisionar PDV_OPERADOR. */
    List<UsuarioPapelEmpresa> findByEmpresaId(Long empresaId);

    Optional<UsuarioPapelEmpresa> findByUsuarioIdAndEmpresaId(Long usuarioId, Long empresaId);

    boolean existsByUsuarioIdAndPapelIdAndEmpresaId(Long usuarioId, Long papelId, Long empresaId);
}
