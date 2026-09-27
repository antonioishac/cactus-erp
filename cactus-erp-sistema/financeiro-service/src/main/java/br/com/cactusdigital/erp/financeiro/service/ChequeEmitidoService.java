package br.com.cactusdigital.erp.financeiro.service;

import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.financeiro.dto.CancelarChequeEmitidoRequest;
import br.com.cactusdigital.erp.financeiro.dto.ChequeEmitidoRequest;
import br.com.cactusdigital.erp.financeiro.dto.ChequeEmitidoResponse;
import br.com.cactusdigital.erp.financeiro.dto.CompensarChequeEmitidoRequest;
import br.com.cactusdigital.erp.financeiro.dto.DevolverChequeRequest;
import br.com.cactusdigital.erp.financeiro.entity.ChequeEmitido;
import br.com.cactusdigital.erp.financeiro.entity.OrigemMovimentoCaixa;
import br.com.cactusdigital.erp.financeiro.entity.SituacaoChequeEmitido;
import br.com.cactusdigital.erp.financeiro.entity.TipoMovimentoCaixa;
import br.com.cactusdigital.erp.financeiro.exception.FinanceiroErrorCode;
import br.com.cactusdigital.erp.financeiro.mapper.CaixaBancoMapper;
import br.com.cactusdigital.erp.financeiro.repository.ChequeEmitidoRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cheque emitido pela própria empresa — {@code EMITIDO} → {@code COMPENSADO} (gera
 * {@code SAIDA} real no {@link MovimentoCaixaBancoService}) | {@code EMITIDO} →
 * {@code DEVOLVIDO}/{@code CANCELADO} (sem movimento — dinheiro só sai de fato na
 * compensação, nunca chegou a debitar antes disso).
 */
@Service
@RequiredArgsConstructor
public class ChequeEmitidoService {

    private final ChequeEmitidoRepository repository;
    private final MovimentoCaixaBancoService movimentoService;
    private final CaixaBancoMapper mapper;

    @Transactional(readOnly = true)
    public Page<ChequeEmitidoResponse> listar(Long contaBancariaId, Pageable pageable) {
        Page<ChequeEmitido> pagina = contaBancariaId != null
                ? repository.findByContaBancariaId(contaBancariaId, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional
    public ChequeEmitidoResponse criar(ChequeEmitidoRequest request) {
        ChequeEmitido cheque = ChequeEmitido.builder()
                .contaBancariaId(request.contaBancariaId())
                .numero(request.numero())
                .dataEmissao(request.dataEmissao())
                .bomPara(request.bomPara())
                .valor(request.valor())
                .nominalA(request.nominalA())
                .build();
        return mapper.toResponse(repository.save(cheque));
    }

    @Transactional
    public ChequeEmitidoResponse compensar(Long id, CompensarChequeEmitidoRequest request) {
        ChequeEmitido cheque = exigirSituacao(id, SituacaoChequeEmitido.EMITIDO);
        LocalDate data = request.dataCompensacao() != null ? request.dataCompensacao() : LocalDate.now();
        movimentoService.registrar(cheque.getContaBancariaId(), TipoMovimentoCaixa.SAIDA, OrigemMovimentoCaixa.CHEQUE_COMPENSADO,
                cheque.getId(), cheque.getValor(), data, "Compensação cheque emitido nº " + cheque.getNumero(), null);
        cheque.setSituacao(SituacaoChequeEmitido.COMPENSADO);
        cheque.setDataSituacao(data);
        return mapper.toResponse(cheque);
    }

    @Transactional
    public ChequeEmitidoResponse devolver(Long id, DevolverChequeRequest request) {
        ChequeEmitido cheque = exigirSituacao(id, SituacaoChequeEmitido.EMITIDO);
        cheque.setSituacao(SituacaoChequeEmitido.DEVOLVIDO);
        cheque.setDataSituacao(request.dataDevolucao() != null ? request.dataDevolucao() : LocalDate.now());
        cheque.setMotivoSituacao(request.motivo());
        return mapper.toResponse(cheque);
    }

    @Transactional
    public ChequeEmitidoResponse cancelar(Long id, CancelarChequeEmitidoRequest request) {
        ChequeEmitido cheque = exigirSituacao(id, SituacaoChequeEmitido.EMITIDO);
        cheque.setSituacao(SituacaoChequeEmitido.CANCELADO);
        cheque.setDataSituacao(LocalDate.now());
        cheque.setMotivoSituacao(request.motivo());
        return mapper.toResponse(cheque);
    }

    private ChequeEmitido exigirSituacao(Long id, SituacaoChequeEmitido esperada) {
        ChequeEmitido cheque = repository.findById(id).orElseThrow(() -> NotFoundException.of("Cheque emitido", id));
        if (cheque.getSituacao() != esperada) {
            throw new ConflictException(FinanceiroErrorCode.CHEQUE_SITUACAO_INVALIDA_PARA_OPERACAO);
        }
        return cheque;
    }
}
