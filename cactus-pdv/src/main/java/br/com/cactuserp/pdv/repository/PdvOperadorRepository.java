package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.NivelAutorizacao;
import br.com.cactuserp.pdv.entity.PdvOperador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PdvOperadorRepository extends JpaRepository<PdvOperador, Long> {

    Optional<PdvOperador> findByLoginAndSenha(String login, String senha);

    @Query("""
           SELECT o FROM PdvOperador o
           WHERE o.colaborador.nivelAutorizacao = :nivel
           ORDER BY o.colaborador.nome
           """)
    List<PdvOperador> findByNivel(@Param("nivel") NivelAutorizacao nivel);

    @Query("""
           SELECT o FROM PdvOperador o
           WHERE o.colaborador.nivelAutorizacao IN :niveis
           ORDER BY o.colaborador.nome
           """)
    List<PdvOperador> findByNiveis(@Param("niveis") List<NivelAutorizacao> niveis);

    Optional<PdvOperador> findByLogin(String login);
}