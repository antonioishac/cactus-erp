package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.Cliente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {

    /**
     * As 3 hidratações abaixo evitam N+1 ao montar uma página de {@code ClienteResponse}
     * (que expõe {@code enderecos}/{@code contatos}/{@code telefones} de
     * {@code Cliente.pessoa}) — chamadas só pelo efeito colateral (populam as coleções
     * nas entidades já gerenciadas da página, via cache de 1º nível), nunca pelo valor
     * de retorno. Não dá pra fazer as 3 numa query só: Hibernate lança
     * {@code MultipleBagFetchException} pra duas ou mais coleções {@code List} (bag)
     * buscadas juntas, mesmo pertencendo à mesma entidade-neta ({@code pessoa}), não só
     * quando são diretamente irmãs da raiz da query. Ver
     * {@code br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport} pro mesmo
     * princípio aplicado onde a coleção pertence à própria entidade paginada.
     */
    @Query("SELECT DISTINCT c FROM Cliente c LEFT JOIN FETCH c.pessoa p LEFT JOIN FETCH p.enderecos WHERE c.id IN :ids")
    List<Cliente> hidratarEnderecos(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT c FROM Cliente c LEFT JOIN FETCH c.pessoa p LEFT JOIN FETCH p.contatos WHERE c.id IN :ids")
    List<Cliente> hidratarContatos(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT c FROM Cliente c LEFT JOIN FETCH c.pessoa p LEFT JOIN FETCH p.telefones WHERE c.id IN :ids")
    List<Cliente> hidratarTelefones(@Param("ids") List<Long> ids);
}
