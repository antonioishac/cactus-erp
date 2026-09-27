package br.com.cactusdigital.erp.estoque.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.estoque.dto.EstoqueMovimentoRequest;
import br.com.cactusdigital.erp.estoque.dto.TransferenciaEstoqueRequest;
import br.com.cactusdigital.erp.estoque.dto.TransferenciaEstoqueResponse;
import br.com.cactusdigital.erp.estoque.entity.Deposito;
import br.com.cactusdigital.erp.estoque.entity.OrigemMovimentoEstoque;
import br.com.cactusdigital.erp.estoque.entity.SituacaoTransferencia;
import br.com.cactusdigital.erp.estoque.entity.TipoMovimentoEstoque;
import br.com.cactusdigital.erp.estoque.entity.TransferenciaEstoque;
import br.com.cactusdigital.erp.estoque.exception.EstoqueErrorCode;
import br.com.cactusdigital.erp.estoque.mapper.EstoqueMapper;
import br.com.cactusdigital.erp.estoque.repository.TransferenciaEstoqueRepository;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@code enviar()} registra {@code TRANSFERENCIA_SAIDA} no depósito origem (via
 * {@link EstoqueMovimentoService}, mesmo ledger de qualquer outro movimento);
 * {@code receber()} registra {@code TRANSFERENCIA_ENTRADA} no destino. A quantidade "em
 * trânsito" entre os dois passos mora só nesta entidade — sem depósito virtual fake.
 */
@Service
@RequiredArgsConstructor
public class TransferenciaEstoqueService {

    private final TransferenciaEstoqueRepository repository;
    private final DepositoService depositoService;
    private final EstoqueMovimentoService movimentoService;
    private final SaldoEstoqueService saldoEstoqueService;
    private final EstoqueMapper mapper;

    @Transactional(readOnly = true)
    public Page<TransferenciaEstoqueResponse> listarPorDeposito(Long depositoId, Pageable pageable) {
        return repository.findByDepositoOrigemIdOrDepositoDestinoId(depositoId, depositoId, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TransferenciaEstoqueResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public TransferenciaEstoqueResponse criar(TransferenciaEstoqueRequest request) {
        if (request.depositoOrigemId().equals(request.depositoDestinoId())) {
            throw new BusinessException(EstoqueErrorCode.TRANSFERENCIA_DEPOSITO_IGUAL);
        }
        Deposito origem = depositoService.buscarEntidade(request.depositoOrigemId());
        Deposito destino = depositoService.buscarEntidade(request.depositoDestinoId());

        TransferenciaEstoque transferencia = TransferenciaEstoque.builder()
                .produtoId(request.produtoId())
                .depositoOrigem(origem)
                .depositoDestino(destino)
                .quantidade(request.quantidade())
                .situacao(SituacaoTransferencia.SOLICITADA)
                .observacao(request.observacao())
                .build();
        return mapper.toResponse(repository.save(transferencia));
    }

    @Transactional
    public TransferenciaEstoqueResponse enviar(Long id, Long usuarioId) {
        TransferenciaEstoque transferencia = buscarEntidade(id);
        exigirSituacao(transferencia, SituacaoTransferencia.SOLICITADA);

        // Captura o custo médio ponderado da origem ANTES do envio — receber() pode
        // acontecer dias depois, quando não dá mais pra reconsultar "quanto valia" na
        // origem (o saldo lá já pode ter mudado de custo com novas entradas).
        BigDecimal custoNaOrigem = saldoEstoqueService.buscarPorProdutoEDeposito(
                transferencia.getProdutoId(), transferencia.getDepositoOrigem().getId()).custoMedioPonderado();

        movimentoService.registrar(new EstoqueMovimentoRequest(
                transferencia.getProdutoId(), transferencia.getDepositoOrigem().getId(),
                transferencia.getLote() != null ? transferencia.getLote().getId() : null, null,
                TipoMovimentoEstoque.TRANSFERENCIA_SAIDA, null, transferencia.getQuantidade(), null,
                OrigemMovimentoEstoque.TRANSFERENCIA, transferencia.getId(), null,
                "Transferência #" + transferencia.getId() + " — envio"), usuarioId);

        transferencia.setCustoUnitario(custoNaOrigem);
        transferencia.setSituacao(SituacaoTransferencia.EM_TRANSITO);
        transferencia.setUsuarioEnvioId(usuarioId);
        transferencia.setDataEnvio(Instant.now());
        return mapper.toResponse(transferencia);
    }

    @Transactional
    public TransferenciaEstoqueResponse receber(Long id, Long usuarioId) {
        TransferenciaEstoque transferencia = buscarEntidade(id);
        exigirSituacao(transferencia, SituacaoTransferencia.EM_TRANSITO);

        movimentoService.registrar(new EstoqueMovimentoRequest(
                transferencia.getProdutoId(), transferencia.getDepositoDestino().getId(),
                transferencia.getLote() != null ? transferencia.getLote().getId() : null, null,
                TipoMovimentoEstoque.TRANSFERENCIA_ENTRADA, null, transferencia.getQuantidade(), transferencia.getCustoUnitario(),
                OrigemMovimentoEstoque.TRANSFERENCIA, transferencia.getId(), null,
                "Transferência #" + transferencia.getId() + " — recebimento"), usuarioId);

        transferencia.setSituacao(SituacaoTransferencia.CONCLUIDA);
        transferencia.setUsuarioRecebimentoId(usuarioId);
        transferencia.setDataRecebimento(Instant.now());
        return mapper.toResponse(transferencia);
    }

    @Transactional
    public TransferenciaEstoqueResponse cancelar(Long id) {
        TransferenciaEstoque transferencia = buscarEntidade(id);
        exigirSituacao(transferencia, SituacaoTransferencia.SOLICITADA);
        transferencia.setSituacao(SituacaoTransferencia.CANCELADA);
        return mapper.toResponse(transferencia);
    }

    private void exigirSituacao(TransferenciaEstoque transferencia, SituacaoTransferencia esperada) {
        if (transferencia.getSituacao() != esperada) {
            throw new ConflictException(EstoqueErrorCode.TRANSFERENCIA_SITUACAO_INVALIDA);
        }
    }

    private TransferenciaEstoque buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Transferência de estoque", id));
    }
}
