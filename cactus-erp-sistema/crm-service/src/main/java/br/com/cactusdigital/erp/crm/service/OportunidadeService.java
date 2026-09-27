package br.com.cactusdigital.erp.crm.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.crm.dto.FunilEstagioResponse;
import br.com.cactusdigital.erp.crm.dto.MoverEstagioRequest;
import br.com.cactusdigital.erp.crm.dto.OportunidadeRequest;
import br.com.cactusdigital.erp.crm.dto.OportunidadeResponse;
import br.com.cactusdigital.erp.crm.entity.EstagioFunil;
import br.com.cactusdigital.erp.crm.entity.Lead;
import br.com.cactusdigital.erp.crm.entity.Oportunidade;
import br.com.cactusdigital.erp.crm.entity.SituacaoOportunidade;
import br.com.cactusdigital.erp.crm.entity.TipoEncerramentoFunil;
import br.com.cactusdigital.erp.crm.exception.CrmErrorCode;
import br.com.cactusdigital.erp.crm.mapper.CrmMapper;
import br.com.cactusdigital.erp.crm.repository.OportunidadeRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Oportunidade — {@code ABERTA} até ser movida pra um {@link EstagioFunil} com
 * {@code tipoEncerramento} {@code GANHO}/{@code PERDIDO} ({@link #moverEstagio}), que
 * dispara o fechamento automático (mesmo espírito de arrastar um card pra coluna
 * "Ganho"/"Perdido" num Kanban). Depois de fechada, não move mais de estágio (ver
 * {@link CrmErrorCode#OPORTUNIDADE_JA_FECHADA}) — reabrir exigiria uma ação própria,
 * fora de escopo desta v1.
 */
@Service
@RequiredArgsConstructor
public class OportunidadeService {

    private final OportunidadeRepository repository;
    private final EstagioFunilService estagioFunilService;
    private final LeadService leadService;
    private final CrmMapper mapper;

    @Transactional(readOnly = true)
    public Page<OportunidadeResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public OportunidadeResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    /** Visão Kanban: colunas na ordem do funil, cada uma com as oportunidades ABERTAS nela e o valor estimado somado. */
    @Transactional(readOnly = true)
    public List<FunilEstagioResponse> visaoFunil() {
        List<EstagioFunil> estagios = estagioFunilService.listarEntidadesOrdenado();
        List<Oportunidade> abertas = repository.findBySituacaoOrderByEstagioFunil_OrdemAsc(SituacaoOportunidade.ABERTA);
        Map<Long, List<Oportunidade>> porEstagio = abertas.stream()
                .collect(Collectors.groupingBy(o -> o.getEstagioFunil().getId()));

        return estagios.stream()
                .map(estagio -> {
                    List<Oportunidade> doEstagio = porEstagio.getOrDefault(estagio.getId(), List.of());
                    BigDecimal total = doEstagio.stream()
                            .map(Oportunidade::getValorEstimado)
                            .filter(java.util.Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new FunilEstagioResponse(
                            estagio.getId(), estagio.getNome(), estagio.getOrdem(), total,
                            doEstagio.stream().map(mapper::toResponse).toList());
                })
                .toList();
    }

    @Transactional
    public OportunidadeResponse criar(OportunidadeRequest request) {
        EstagioFunil estagio = estagioFunilService.buscarEntidade(request.estagioFunilId());
        Lead lead = request.leadId() != null ? leadService.buscarEntidade(request.leadId()) : null;

        Oportunidade oportunidade = Oportunidade.builder()
                .lead(lead)
                .clienteId(request.clienteId())
                .vendedorId(request.vendedorId())
                .nome(request.nome())
                .valorEstimado(request.valorEstimado())
                .probabilidadePercentual(request.probabilidadePercentual())
                .dataPrevisaoFechamento(request.dataPrevisaoFechamento())
                .estagioFunil(estagio)
                .observacao(request.observacao())
                .build();
        return mapper.toResponse(repository.save(oportunidade));
    }

    @Transactional
    public OportunidadeResponse atualizar(Long id, OportunidadeRequest request) {
        Oportunidade oportunidade = buscarEntidade(id);
        oportunidade.setClienteId(request.clienteId());
        oportunidade.setVendedorId(request.vendedorId());
        oportunidade.setNome(request.nome());
        oportunidade.setValorEstimado(request.valorEstimado());
        oportunidade.setProbabilidadePercentual(request.probabilidadePercentual());
        oportunidade.setDataPrevisaoFechamento(request.dataPrevisaoFechamento());
        oportunidade.setObservacao(request.observacao());
        if (request.leadId() != null) {
            oportunidade.setLead(leadService.buscarEntidade(request.leadId()));
        }
        return mapper.toResponse(oportunidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    /**
     * Move a oportunidade pra outro estágio — se o destino tem {@code tipoEncerramento}
     * {@code GANHO}/{@code PERDIDO}, fecha automaticamente (situação + data de
     * fechamento). {@code PERDIDO} exige {@code motivoPerda} no request.
     */
    @Transactional
    public OportunidadeResponse moverEstagio(Long id, MoverEstagioRequest request) {
        Oportunidade oportunidade = buscarEntidade(id);
        if (oportunidade.getSituacao() != SituacaoOportunidade.ABERTA) {
            throw new ConflictException(CrmErrorCode.OPORTUNIDADE_JA_FECHADA);
        }

        EstagioFunil estagioDestino = estagioFunilService.buscarEntidade(request.estagioFunilId());
        oportunidade.setEstagioFunil(estagioDestino);

        if (estagioDestino.getTipoEncerramento() == TipoEncerramentoFunil.GANHO) {
            oportunidade.setSituacao(SituacaoOportunidade.GANHA);
            oportunidade.setDataFechamento(Instant.now());
        } else if (estagioDestino.getTipoEncerramento() == TipoEncerramentoFunil.PERDIDO) {
            if (request.motivoPerda() == null || request.motivoPerda().isBlank()) {
                throw new BusinessException(CrmErrorCode.MOTIVO_PERDA_OBRIGATORIO);
            }
            oportunidade.setSituacao(SituacaoOportunidade.PERDIDA);
            oportunidade.setMotivoPerda(request.motivoPerda());
            oportunidade.setDataFechamento(Instant.now());
        }

        return mapper.toResponse(oportunidade);
    }

    Oportunidade buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Oportunidade", id));
    }
}
