package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.PdvTotalTipoPagamento;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;
import br.com.cactuserp.pdv.entity.StatusVenda;
import br.com.cactuserp.pdv.repository.PdvTotalTipoPagamentoRepository;
import br.com.cactuserp.pdv.repository.PdvVendaCabecalhoRepository;
import br.com.cactuserp.pdv.repository.PdvVendaDetalheRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service de consulta de cupons finalizados/cancelados pra tela
 * {@code ConsultaCupomController} (F12 → Consultar Cupom).
 *
 * <p>Encapsula a busca por período + status + número opcional, e
 * carrega o detalhe completo (itens + pagamentos) sob demanda quando
 * o operador seleciona um cupom específico.
 *
 * <p>Read-only — não altera nada no banco.
 */
@Service
public class ConsultaCupomService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaCupomService.class);

    private final PdvVendaCabecalhoRepository cabecalhoRepository;
    private final PdvVendaDetalheRepository detalheRepository;
    private final PdvTotalTipoPagamentoRepository pagamentoRepository;

    @PersistenceContext
    private EntityManager em;

    public ConsultaCupomService(PdvVendaCabecalhoRepository cabecalhoRepository,
                                PdvVendaDetalheRepository detalheRepository,
                                PdvTotalTipoPagamentoRepository pagamentoRepository) {
        this.cabecalhoRepository = cabecalhoRepository;
        this.detalheRepository = detalheRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    /**
     * Busca cupons com filtros combinados.
     *
     * @param dataInicio data inicial inclusiva
     * @param dataFim    data final inclusiva
     * @param statuses   lista de status aceitos (não pode ser vazia)
     * @param numero     número COO opcional (null = ignora filtro)
     */
    @Transactional(readOnly = true)
    public List<PdvVendaCabecalho> buscar(LocalDate dataInicio, LocalDate dataFim,
                                          List<StatusVenda> statuses, Integer numero,
                                          String cpfCnpj) {
        if (dataInicio == null || dataFim == null) {
            return Collections.emptyList();
        }
        if (statuses == null || statuses.isEmpty()) {
            return Collections.emptyList();
        }

        // Normaliza CPF/CNPJ (remove pontuação)
        String cpfNorm = null;
        if (cpfCnpj != null) {
            String somenteDigitos = cpfCnpj.replaceAll("[^0-9]", "");
            if (!somenteDigitos.isEmpty()) cpfNorm = somenteDigitos;
        }

        StringBuilder jpql = new StringBuilder(
                "SELECT DISTINCT v FROM PdvVendaCabecalho v "
                        + "LEFT JOIN FETCH v.colaborador "
                        + "LEFT JOIN v.cliente cli "
                        + "WHERE v.dataVenda BETWEEN :ini AND :fim "
                        + "  AND v.statusVenda IN :statuses");

        if (numero != null) {
            jpql.append(" AND (v.coo = :num OR v.id = :numLong)");
        }
        if (cpfNorm != null) {
            jpql.append(" AND (v.cpfCnpjCliente = :cpf OR cli.cpf = :cpf)");
        }
        jpql.append(" ORDER BY v.dataVenda DESC, v.horaVenda DESC, v.id DESC");

        jakarta.persistence.TypedQuery<PdvVendaCabecalho> q =
                em.createQuery(jpql.toString(), PdvVendaCabecalho.class);
        q.setParameter("ini", dataInicio);
        q.setParameter("fim", dataFim);
        q.setParameter("statuses", statuses);
        if (numero != null) {
            q.setParameter("num", numero);
            q.setParameter("numLong", numero.longValue());
        }
        if (cpfNorm != null) {
            q.setParameter("cpf", cpfNorm);
        }
        q.setMaxResults(500);

        return q.getResultList();
    }

    /**
     * Carrega itens e pagamentos do cupom (sob demanda — só quando o
     * operador clica no detalhe).
     */
    @Transactional(readOnly = true)
    public DetalheCompleto carregarDetalhe(PdvVendaCabecalho cupom) {
        if (cupom == null) {
            return new DetalheCompleto(Collections.emptyList(), Collections.emptyList());
        }

        // Itens com produto carregado (evita LazyInit na UI)
        List<PdvVendaDetalhe> itens = detalheRepository.buscarDetalhesComProduto(cupom);
        if (itens == null) itens = new ArrayList<>();

        // Pagamentos
        List<PdvTotalTipoPagamento> pagamentos =
                pagamentoRepository.findByIdPdvVendaCabecalho(cupom.getId());
        if (pagamentos == null) pagamentos = new ArrayList<>();

        return new DetalheCompleto(itens, pagamentos);
    }

    // ====================================================================
    //  Result type
    // ====================================================================

    public static class DetalheCompleto {
        private final List<PdvVendaDetalhe> itens;
        private final List<PdvTotalTipoPagamento> pagamentos;

        public DetalheCompleto(List<PdvVendaDetalhe> itens,
                               List<PdvTotalTipoPagamento> pagamentos) {
            this.itens = itens;
            this.pagamentos = pagamentos;
        }

        public List<PdvVendaDetalhe> getItens() { return itens; }
        public List<PdvTotalTipoPagamento> getPagamentos() { return pagamentos; }
    }
}