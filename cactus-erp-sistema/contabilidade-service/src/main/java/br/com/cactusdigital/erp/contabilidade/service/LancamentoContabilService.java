package br.com.cactusdigital.erp.contabilidade.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.contabilidade.dto.EstornarLancamentoRequest;
import br.com.cactusdigital.erp.contabilidade.dto.LancamentoContabilRequest;
import br.com.cactusdigital.erp.contabilidade.dto.LancamentoContabilResponse;
import br.com.cactusdigital.erp.contabilidade.dto.PartidaContabilRequest;
import br.com.cactusdigital.erp.contabilidade.entity.CentroCusto;
import br.com.cactusdigital.erp.contabilidade.entity.LancamentoContabil;
import br.com.cactusdigital.erp.contabilidade.entity.PartidaContabil;
import br.com.cactusdigital.erp.contabilidade.entity.PlanoConta;
import br.com.cactusdigital.erp.contabilidade.entity.SituacaoLancamentoContabil;
import br.com.cactusdigital.erp.contabilidade.entity.TipoPartida;
import br.com.cactusdigital.erp.contabilidade.exception.ContabilidadeErrorCode;
import br.com.cactusdigital.erp.contabilidade.mapper.ContabilidadeMapper;
import br.com.cactusdigital.erp.contabilidade.repository.LancamentoContabilRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * O coração do módulo — lançamento contábil (Livro Diário) com partida dobrada validada
 * no servidor, nunca só confiada ao cliente ("não podemos admitir erros" é literal: um
 * lançamento desbalanceado corrompe Balancete/Razão/SPED ECD gerados a partir dele).
 * Imutável depois de lançado — sem {@code PUT}, só {@link #estornar}, mesmo critério de
 * {@code EstoqueMovimento}/{@code DocumentoVersao}/{@code BaixaContaReceber}.
 */
@Service
@RequiredArgsConstructor
public class LancamentoContabilService {

    private final LancamentoContabilRepository repository;
    private final PlanoContaService planoContaService;
    private final CentroCustoService centroCustoService;
    private final ContabilidadeMapper mapper;

    @Transactional(readOnly = true)
    public Page<LancamentoContabilResponse> listar(Long empresaId, LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        Page<LancamentoContabil> pagina = (dataInicio != null && dataFim != null)
                ? repository.findByEmpresaIdAndDataLancamentoBetween(empresaId, dataInicio, dataFim, pageable)
                : repository.findByEmpresaId(empresaId, pageable);

        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, LancamentoContabil::getId, repository::buscarComPartidas, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public LancamentoContabilResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public LancamentoContabilResponse criar(LancamentoContabilRequest request) {
        if (request.partidas().size() < 2) {
            throw new BusinessException(ContabilidadeErrorCode.LANCAMENTO_PARTIDAS_INSUFICIENTES);
        }
        validarPartidaDobrada(request.partidas());

        LancamentoContabil lancamento = LancamentoContabil.builder()
                .empresaId(request.empresaId())
                .dataLancamento(request.dataLancamento())
                .historico(request.historico())
                .numeroDocumento(request.numeroDocumento())
                .build();

        request.partidas().stream()
                .map(partidaRequest -> construirPartida(lancamento, request.empresaId(), partidaRequest))
                .forEach(lancamento.getPartidas()::add);

        return mapper.toResponse(repository.save(lancamento));
    }

    /**
     * Estorno = um NOVO lançamento com as partidas invertidas (débito↔crédito), nunca
     * uma edição/exclusão do original — mesmo padrão de {@code BaixaContaReceber.estornar}.
     * O original é marcado {@code ESTORNADO}, mas continua intacto no histórico (nunca
     * apagado, mesmo princípio de {@code EstoqueMovimento}).
     */
    @Transactional
    public LancamentoContabilResponse estornar(Long id, EstornarLancamentoRequest request) {
        LancamentoContabil original = buscarEntidade(id);
        if (original.getSituacao() == SituacaoLancamentoContabil.ESTORNADO) {
            throw new ConflictException(ContabilidadeErrorCode.LANCAMENTO_JA_ESTORNADO);
        }
        if (original.getEstornoDeId() != null) {
            throw new BusinessException(ContabilidadeErrorCode.LANCAMENTO_E_UM_ESTORNO);
        }

        LancamentoContabil estorno = LancamentoContabil.builder()
                .empresaId(original.getEmpresaId())
                .dataLancamento(LocalDate.now())
                .historico("Estorno de #%d (%s) — %s".formatted(original.getId(), original.getHistorico(), request.motivo()))
                .numeroDocumento(original.getNumeroDocumento())
                .origemTipo(original.getOrigemTipo())
                .origemId(original.getOrigemId())
                .estornoDeId(original.getId())
                .build();

        original.getPartidas().stream()
                .map(partidaOriginal -> PartidaContabil.builder()
                        .lancamentoContabil(estorno)
                        .conta(partidaOriginal.getConta())
                        .centroCusto(partidaOriginal.getCentroCusto())
                        .tipoPartida(inverterTipo(partidaOriginal.getTipoPartida()))
                        .valor(partidaOriginal.getValor())
                        .historicoComplementar(partidaOriginal.getHistoricoComplementar())
                        .build())
                .forEach(estorno.getPartidas()::add);

        LancamentoContabil estornoSalvo = repository.save(estorno);
        original.setSituacao(SituacaoLancamentoContabil.ESTORNADO);
        original.setEstornadoPorId(estornoSalvo.getId());

        return mapper.toResponse(estornoSalvo);
    }

    LancamentoContabil buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Lançamento contábil", id));
    }

    private static TipoPartida inverterTipo(TipoPartida tipo) {
        return switch (tipo) {
            case DEBITO -> TipoPartida.CREDITO;
            case CREDITO -> TipoPartida.DEBITO;
        };
    }

    /** Soma exata (sem tolerância de arredondamento) — contabilidade não admite "quase bater". */
    private void validarPartidaDobrada(List<PartidaContabilRequest> partidas) {
        BigDecimal totalDebito = somarPorTipo(partidas, TipoPartida.DEBITO);
        BigDecimal totalCredito = somarPorTipo(partidas, TipoPartida.CREDITO);
        if (totalDebito.compareTo(totalCredito) != 0) {
            throw new BusinessException(ContabilidadeErrorCode.LANCAMENTO_PARTIDA_DOBRADA_DIVERGENTE,
                    "Soma dos débitos (%s) é diferente da soma dos créditos (%s)".formatted(totalDebito, totalCredito));
        }
    }

    private static BigDecimal somarPorTipo(List<PartidaContabilRequest> partidas, TipoPartida tipo) {
        return partidas.stream()
                .filter(partida -> partida.tipoPartida() == tipo)
                .map(PartidaContabilRequest::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private PartidaContabil construirPartida(LancamentoContabil lancamento, Long empresaId, PartidaContabilRequest request) {
        PlanoConta conta = planoContaService.buscarEntidade(request.contaId());
        if (!conta.getEmpresaId().equals(empresaId)) {
            throw new BusinessException(ContabilidadeErrorCode.PLANO_CONTA_DE_OUTRA_EMPRESA);
        }
        if (!conta.isContaAnalitica()) {
            throw new BusinessException(ContabilidadeErrorCode.PLANO_CONTA_NAO_ANALITICA_PARA_LANCAMENTO);
        }

        CentroCusto centroCusto = null;
        if (request.centroCustoId() != null) {
            centroCusto = centroCustoService.buscarEntidade(request.centroCustoId());
            if (!centroCusto.getEmpresaId().equals(empresaId)) {
                throw new BusinessException(ContabilidadeErrorCode.CENTRO_CUSTO_DE_OUTRA_EMPRESA);
            }
        }

        return PartidaContabil.builder()
                .lancamentoContabil(lancamento)
                .conta(conta)
                .centroCusto(centroCusto)
                .tipoPartida(request.tipoPartida())
                .valor(request.valor())
                .historicoComplementar(request.historicoComplementar())
                .build();
    }
}
