package br.com.cactusdigital.erp.financeiro.service;

import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.financeiro.dto.ChequeRecebidoRequest;
import br.com.cactusdigital.erp.financeiro.dto.ChequeRecebidoResponse;
import br.com.cactusdigital.erp.financeiro.dto.CompensarChequeRecebidoRequest;
import br.com.cactusdigital.erp.financeiro.dto.DepositarChequeRecebidoRequest;
import br.com.cactusdigital.erp.financeiro.dto.DescontarChequeRecebidoRequest;
import br.com.cactusdigital.erp.financeiro.dto.DevolverChequeRequest;
import br.com.cactusdigital.erp.financeiro.entity.ChequeRecebido;
import br.com.cactusdigital.erp.financeiro.entity.OrigemMovimentoCaixa;
import br.com.cactusdigital.erp.financeiro.entity.SituacaoChequeRecebido;
import br.com.cactusdigital.erp.financeiro.entity.TipoMovimentoCaixa;
import br.com.cactusdigital.erp.financeiro.exception.FinanceiroErrorCode;
import br.com.cactusdigital.erp.financeiro.mapper.CaixaBancoMapper;
import br.com.cactusdigital.erp.financeiro.repository.ChequeRecebidoRepository;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cheque de terceiro recebido como forma de pagamento — {@code EM_CARTEIRA} →
 * {@code DEPOSITADO} (sem movimento ainda, só reserva a conta) → {@code COMPENSADO}
 * (gera {@code ENTRADA} real) | {@code EM_CARTEIRA} → {@code DESCONTADO} (gera
 * {@code ENTRADA} imediata — banco assume o risco) | {@code EM_CARTEIRA}/
 * {@code DEPOSITADO} → {@code DEVOLVIDO} (sem movimento — devolução depois de
 * {@code COMPENSADO}/{@code DESCONTADO} é estorno bancário de verdade, fora de escopo
 * desta fase, ver Javadoc de {@code ContaReceber} sobre não-saga).
 */
@Service
@RequiredArgsConstructor
public class ChequeRecebidoService {

    private static final Set<SituacaoChequeRecebido> DEVOLUCAO_PERMITIDA = Set.of(
            SituacaoChequeRecebido.EM_CARTEIRA, SituacaoChequeRecebido.DEPOSITADO);

    private final ChequeRecebidoRepository repository;
    private final MovimentoCaixaBancoService movimentoService;
    private final CaixaBancoMapper mapper;

    @Transactional(readOnly = true)
    public Page<ChequeRecebidoResponse> listar(Long clienteId, Pageable pageable) {
        Page<ChequeRecebido> pagina = clienteId != null ? repository.findByClienteId(clienteId, pageable) : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional
    public ChequeRecebidoResponse criar(ChequeRecebidoRequest request) {
        ChequeRecebido cheque = ChequeRecebido.builder()
                .clienteId(request.clienteId())
                .cpfCnpj(request.cpfCnpj())
                .nomeEmitente(request.nomeEmitente())
                .bancoCodigo(request.bancoCodigo())
                .agencia(request.agencia())
                .conta(request.conta())
                .numero(request.numero())
                .dataEmissao(request.dataEmissao())
                .bomPara(request.bomPara())
                .valor(request.valor())
                .build();
        return mapper.toResponse(repository.save(cheque));
    }

    @Transactional
    public ChequeRecebidoResponse depositar(Long id, DepositarChequeRecebidoRequest request) {
        ChequeRecebido cheque = exigirSituacao(id, SituacaoChequeRecebido.EM_CARTEIRA);
        cheque.setContaBancariaId(request.contaBancariaId());
        cheque.setSituacao(SituacaoChequeRecebido.DEPOSITADO);
        cheque.setDataSituacao(request.dataDeposito() != null ? request.dataDeposito() : LocalDate.now());
        return mapper.toResponse(cheque);
    }

    @Transactional
    public ChequeRecebidoResponse compensar(Long id, CompensarChequeRecebidoRequest request) {
        ChequeRecebido cheque = exigirSituacao(id, SituacaoChequeRecebido.DEPOSITADO);
        LocalDate data = request.dataCompensacao() != null ? request.dataCompensacao() : LocalDate.now();
        movimentoService.registrar(cheque.getContaBancariaId(), TipoMovimentoCaixa.ENTRADA, OrigemMovimentoCaixa.CHEQUE_COMPENSADO,
                cheque.getId(), cheque.getValor(), data, "Compensação cheque recebido de " + cheque.getNomeEmitente(), null);
        cheque.setSituacao(SituacaoChequeRecebido.COMPENSADO);
        cheque.setDataSituacao(data);
        return mapper.toResponse(cheque);
    }

    @Transactional
    public ChequeRecebidoResponse descontar(Long id, DescontarChequeRecebidoRequest request) {
        ChequeRecebido cheque = exigirSituacao(id, SituacaoChequeRecebido.EM_CARTEIRA);
        LocalDate data = request.dataDesconto() != null ? request.dataDesconto() : LocalDate.now();
        movimentoService.registrar(request.contaBancariaId(), TipoMovimentoCaixa.ENTRADA, OrigemMovimentoCaixa.CHEQUE_COMPENSADO,
                cheque.getId(), cheque.getValor(), data, "Desconto cheque recebido de " + cheque.getNomeEmitente(), null);
        cheque.setContaBancariaId(request.contaBancariaId());
        cheque.setSituacao(SituacaoChequeRecebido.DESCONTADO);
        cheque.setDataSituacao(data);
        return mapper.toResponse(cheque);
    }

    @Transactional
    public ChequeRecebidoResponse devolver(Long id, DevolverChequeRequest request) {
        ChequeRecebido cheque = repository.findById(id).orElseThrow(() -> NotFoundException.of("Cheque recebido", id));
        if (!DEVOLUCAO_PERMITIDA.contains(cheque.getSituacao())) {
            throw new ConflictException(FinanceiroErrorCode.CHEQUE_SITUACAO_INVALIDA_PARA_OPERACAO);
        }
        cheque.setSituacao(SituacaoChequeRecebido.DEVOLVIDO);
        cheque.setDataSituacao(request.dataDevolucao() != null ? request.dataDevolucao() : LocalDate.now());
        cheque.setMotivoSituacao(request.motivo());
        return mapper.toResponse(cheque);
    }

    private ChequeRecebido exigirSituacao(Long id, SituacaoChequeRecebido esperada) {
        ChequeRecebido cheque = repository.findById(id).orElseThrow(() -> NotFoundException.of("Cheque recebido", id));
        if (cheque.getSituacao() != esperada) {
            throw new ConflictException(FinanceiroErrorCode.CHEQUE_SITUACAO_INVALIDA_PARA_OPERACAO);
        }
        return cheque;
    }
}
