package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.Transportadora;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransportadoraRepository extends JpaRepository<Transportadora, Long>, JpaSpecificationExecutor<Transportadora> {

    /** Evita N+1 nas 3 coleções de {@code Pessoa} que {@code TransportadoraResponse} expõe
     * — ver Javadoc de {@code ClienteRepository#hidratarEnderecos}. */
    @Query("SELECT DISTINCT t FROM Transportadora t LEFT JOIN FETCH t.pessoa p LEFT JOIN FETCH p.enderecos WHERE t.id IN :ids")
    List<Transportadora> hidratarEnderecos(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT t FROM Transportadora t LEFT JOIN FETCH t.pessoa p LEFT JOIN FETCH p.contatos WHERE t.id IN :ids")
    List<Transportadora> hidratarContatos(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT t FROM Transportadora t LEFT JOIN FETCH t.pessoa p LEFT JOIN FETCH p.telefones WHERE t.id IN :ids")
    List<Transportadora> hidratarTelefones(@Param("ids") List<Long> ids);
}
