package br.com.cactusdigital.erp.financeiro.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.financeiro.dto.MovimentoCaixaBancoRequest;
import br.com.cactusdigital.erp.financeiro.dto.MovimentoCaixaBancoResponse;
import br.com.cactusdigital.erp.financeiro.entity.ContaBancaria;
import br.com.cactusdigital.erp.financeiro.entity.MovimentoCaixaBanco;
import br.com.cactusdigital.erp.financeiro.entity.OrigemMovimentoCaixa;
import br.com.cactusdigital.erp.financeiro.entity.TipoMovimentoCaixa;
import br.com.cactusdigital.erp.financeiro.exception.FinanceiroErrorCode;
import br.com.cactusdigital.erp.financeiro.mapper.CaixaBancoMapper;
import br.com.cactusdigital.erp.financeiro.repository.ContaBancariaRepository;
import br.com.cactusdigital.erp.financeiro.repository.MovimentoCaixaBancoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Núcleo do ledger de Caixa e Bancos — toda mudança de {@link ContaBancaria#getSaldoAtual()}
 * passa por {@link #registrar}, nunca um {@code UPDATE} direto de saldo fora daqui. Mesmo
 * princípio de {@code EstoqueMovimentoService} no controle-estoque-service, adaptado pra
 * dinheiro: {@link ContaBancariaRepository#buscarParaAtualizarSaldo} usa lock pessimista
 * (não existe no Estoque) porque o custo de um lost-update em saldo bancário é mais sério
 * que em saldo de produto.
 */
@Service
@RequiredArgsConstructor
public class MovimentoCaixaBancoService {

    private static final Set<TipoMovimentoCaixa> CREDITO = EnumSet.of(TipoMovimentoCaixa.ENTRADA, TipoMovimentoCaixa.TRANSFERENCIA_ENTRADA);

    private final MovimentoCaixaBancoRepository repository;
    private final ContaBancariaRepository contaRepository;
    private final CaixaBancoMapper mapper;

    @Transactional(readOnly = true)
    public Page<MovimentoCaixaBancoResponse> listar(Long contaBancariaId, Pageable pageable) {
        Page<MovimentoCaixaBanco> pagina = contaBancariaId != null
                ? repository.findByContaBancariaIdOrderByDataMovimentoDescIdDesc(contaBancariaId, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional
    public MovimentoCaixaBancoResponse registrarManual(MovimentoCaixaBancoRequest request) {
        if (request.tipoMovimento() != TipoMovimentoCaixa.ENTRADA && request.tipoMovimento() != TipoMovimentoCaixa.SAIDA) {
            throw new BusinessException(FinanceiroErrorCode.TIPO_MOVIMENTO_INVALIDO_PARA_LANCAMENTO_MANUAL);
        }
        MovimentoCaixaBanco movimento = registrar(
                request.contaBancariaId(), request.tipoMovimento(), OrigemMovimentoCaixa.AJUSTE, null,
                request.valor(), request.dataMovimento() != null ? request.dataMovimento() : LocalDate.now(),
                request.historico(), request.usuarioId());
        return mapper.toResponse(movimento);
    }

    /**
     * Núcleo reaproveitado por toda regra de negócio que precisa mexer em saldo (baixa de
     * título, transferência, aplicação/resgate, cheque). {@code valor} é sempre a
     * MAGNITUDE positiva — {@code tipo} decide se soma ou subtrai (mesmo critério de
     * {@code EstoqueMovimento}). Lock pessimista na conta evita lost-update entre duas
     * chamadas concorrentes na mesma conta.
     */
    @Transactional
    public MovimentoCaixaBanco registrar(Long contaBancariaId, TipoMovimentoCaixa tipo, OrigemMovimentoCaixa origemTipo,
            Long origemId, BigDecimal valor, LocalDate dataMovimento, String historico, Long usuarioId) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(FinanceiroErrorCode.VALOR_MOVIMENTO_DEVE_SER_POSITIVO);
        }
        ContaBancaria conta = contaRepository.buscarParaAtualizarSaldo(contaBancariaId)
                .orElseThrow(() -> NotFoundException.of("Conta bancária", contaBancariaId));
        if (!conta.isAtivo()) {
            throw new ConflictException(FinanceiroErrorCode.CONTA_BANCARIA_INATIVA);
        }

        // O saldo vem de uma leitura NATIVA e travada, nunca da entidade. Achado real (2026-10-25): quando a conta já
        // estava no contexto de persistência (o consumidor do PDV acha a conta do terminal antes de lançar), o
        // SELECT ... FOR UPDATE da consulta acima trava a linha mas o Hibernate devolve a instância em memória, com o
        // saldo lido ANTES do lock — e nem refresh() relê nesse caso. 11 vendas simultâneas no mesmo terminal somavam
        // R$ 101 e o saldo ficava em R$ 31. A consulta nativa faz leitura atual (não a foto do REPEATABLE READ).
        BigDecimal saldoAnterior = contaRepository.lerSaldoTravado(contaBancariaId);
        BigDecimal saldoPosterior = CREDITO.contains(tipo) ? saldoAnterior.add(valor) : saldoAnterior.subtract(valor);
        conta.setSaldoAtual(saldoPosterior);
        contaRepository.save(conta);

        return repository.save(MovimentoCaixaBanco.builder()
                .contaBancariaId(contaBancariaId)
                .tipoMovimento(tipo)
                .origemTipo(origemTipo)
                .origemId(origemId)
                .dataMovimento(dataMovimento != null ? dataMovimento : LocalDate.now())
                .historico(historico)
                .valor(valor)
                .saldoAnterior(saldoAnterior)
                .saldoPosterior(saldoPosterior)
                .usuarioId(usuarioId)
                .build());
    }

    /** Usado por {@code ConciliacaoBancariaService} pra decidir a direção esperada do
     * lançamento correspondente a uma linha de extrato (valor positivo = crédito). */
    public boolean isCredito(TipoMovimentoCaixa tipo) {
        return CREDITO.contains(tipo);
    }
}
