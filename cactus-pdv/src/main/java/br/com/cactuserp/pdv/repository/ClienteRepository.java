package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Busca por nome ou nome fantasia (case-insensitive, contém).
     * Usa LOWER + LIKE para funcionar em qualquer banco.
     */
    @Query("""
           SELECT c FROM Cliente c
           WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :termo, '%'))
              OR LOWER(c.fantasia) LIKE LOWER(CONCAT('%', :termo, '%'))
           ORDER BY c.nome
           """)
    Page<Cliente> buscarPorNomeOuFantasia(@Param("termo") String termo, Pageable pageable);

    /**
     * Busca por CPF/CNPJ — começa com os dígitos informados.
     * O banco armazena sem máscara, então o service deve passar só números.
     */
    @Query("""
           SELECT c FROM Cliente c
           WHERE c.cpfCnpj LIKE CONCAT(:cpfCnpj, '%')
           ORDER BY c.nome
           """)
    Page<Cliente> buscarPorCpfCnpj(@Param("cpfCnpj") String cpfCnpj, Pageable pageable);

    /**
     * Busca exata por CPF/CNPJ — usada para validações (cliente já cadastrado, etc).
     */
    Optional<Cliente> findByCpfCnpj(String cpfCnpj);
}