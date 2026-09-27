package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.Fornecedor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long>, JpaSpecificationExecutor<Fornecedor> {

    /** Evita N+1 nas 3 coleções de {@code Pessoa} que {@code FornecedorResponse} expõe
     * — ver Javadoc de {@code ClienteRepository#hidratarEnderecos}. */
    @Query("SELECT DISTINCT f FROM Fornecedor f LEFT JOIN FETCH f.pessoa p LEFT JOIN FETCH p.enderecos WHERE f.id IN :ids")
    List<Fornecedor> hidratarEnderecos(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT f FROM Fornecedor f LEFT JOIN FETCH f.pessoa p LEFT JOIN FETCH p.contatos WHERE f.id IN :ids")
    List<Fornecedor> hidratarContatos(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT f FROM Fornecedor f LEFT JOIN FETCH f.pessoa p LEFT JOIN FETCH p.telefones WHERE f.id IN :ids")
    List<Fornecedor> hidratarTelefones(@Param("ids") List<Long> ids);
}
