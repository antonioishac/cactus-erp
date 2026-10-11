package br.com.cactusdigital.erp.crm.service;

import br.com.cactusdigital.erp.crm.dto.OportunidadeResponse;
import br.com.cactusdigital.erp.crm.entity.SituacaoOportunidade;
import br.com.cactusdigital.erp.crm.mapper.CrmMapper;
import br.com.cactusdigital.erp.crm.repository.OportunidadeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Funil de oportunidades — relatório plano (uma linha por oportunidade, estágio como
 * coluna) sobre o mesmo dado que já alimenta a visão Kanban
 * ({@code OportunidadeService#visaoFunil}), calculado sob demanda a partir do próprio
 * {@link br.com.cactusdigital.erp.crm.entity.Oportunidade}. {@code situacao} default
 * {@code ABERTA} quando não informada — o "funil" em si é o pipeline aberto; histórico
 * de ganhas/perdidas continua acessível informando a situação explicitamente.
 */
@Service
@RequiredArgsConstructor
public class RelatorioOportunidadeService {

    private final OportunidadeRepository repository;
    private final CrmMapper mapper;

    @Transactional(readOnly = true)
    public List<OportunidadeResponse> funil(SituacaoOportunidade situacao) {
        SituacaoOportunidade filtro = situacao != null ? situacao : SituacaoOportunidade.ABERTA;
        return repository.buscarParaRelatorio(filtro).stream().map(mapper::toResponse).toList();
    }
}
