package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.LojaConfiguracao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório da configuração da loja (1 linha apenas).
 *
 * <p>READ-ONLY no PDV. O cadastro é responsabilidade do back-office.
 */
@Repository
public interface LojaConfiguracaoRepository extends JpaRepository<LojaConfiguracao, Long> {

    /**
     * Atalho para pegar A configuração da loja. Por contrato, só existe
     * 1 linha — retorna a primeira (ou vazio se ainda não foi seedada).
     */
    default Optional<LojaConfiguracao> buscarConfiguracao() {
        return findAll().stream().findFirst();
    }
}