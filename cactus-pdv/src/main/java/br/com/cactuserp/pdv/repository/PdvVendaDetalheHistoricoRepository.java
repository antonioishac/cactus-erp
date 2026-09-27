package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.PdvVendaDetalheHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PdvVendaDetalheHistoricoRepository
        extends JpaRepository<PdvVendaDetalheHistorico, Long> {

    /**
     * Lista todo o histórico de um item específico, ordenado por versão.
     * Útil pra:
     * <ul>
     *   <li>Tela de auditoria de cupom (mostrar evolução do item)</li>
     *   <li>Debug de "como esse item virou isso"</li>
     *   <li>Validação de integridade fiscal PAF-NFC-e</li>
     * </ul>
     */
    List<PdvVendaDetalheHistorico>
    findByIdPdvVendaDetalheOrderByVersaoAsc(Long idPdvVendaDetalhe);
}