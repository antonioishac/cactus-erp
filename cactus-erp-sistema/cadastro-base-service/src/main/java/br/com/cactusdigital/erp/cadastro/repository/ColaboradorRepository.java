package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.Colaborador;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Long>, JpaSpecificationExecutor<Colaborador> {

    /** Evita N+1 nas 3 coleções de {@code Pessoa} que {@code ColaboradorResponse} expõe
     * — ver Javadoc de {@code ClienteRepository#hidratarEnderecos}. */
    @Query("SELECT DISTINCT c FROM Colaborador c LEFT JOIN FETCH c.pessoa p LEFT JOIN FETCH p.enderecos WHERE c.id IN :ids")
    List<Colaborador> hidratarEnderecos(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT c FROM Colaborador c LEFT JOIN FETCH c.pessoa p LEFT JOIN FETCH p.contatos WHERE c.id IN :ids")
    List<Colaborador> hidratarContatos(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT c FROM Colaborador c LEFT JOIN FETCH c.pessoa p LEFT JOIN FETCH p.telefones WHERE c.id IN :ids")
    List<Colaborador> hidratarTelefones(@Param("ids") List<Long> ids);
}
