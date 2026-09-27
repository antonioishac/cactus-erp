package br.com.cactusdigital.erp.estoque.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.estoque.dto.EstoqueMovimentoRequest;
import br.com.cactusdigital.erp.estoque.dto.InventarioFisicoAbrirRequest;
import br.com.cactusdigital.erp.estoque.dto.InventarioFisicoContagemRequest;
import br.com.cactusdigital.erp.estoque.dto.InventarioFisicoResponse;
import br.com.cactusdigital.erp.estoque.entity.Deposito;
import br.com.cactusdigital.erp.estoque.entity.DirecaoAjuste;
import br.com.cactusdigital.erp.estoque.entity.InventarioFisico;
import br.com.cactusdigital.erp.estoque.entity.InventarioFisicoItem;
import br.com.cactusdigital.erp.estoque.entity.OrigemMovimentoEstoque;
import br.com.cactusdigital.erp.estoque.entity.SaldoEstoque;
import br.com.cactusdigital.erp.estoque.entity.SituacaoInventario;
import br.com.cactusdigital.erp.estoque.entity.TipoMovimentoEstoque;
import br.com.cactusdigital.erp.estoque.exception.EstoqueErrorCode;
import br.com.cactusdigital.erp.estoque.mapper.EstoqueMapper;
import br.com.cactusdigital.erp.estoque.repository.InventarioFisicoItemRepository;
import br.com.cactusdigital.erp.estoque.repository.InventarioFisicoRepository;
import br.com.cactusdigital.erp.estoque.repository.SaldoEstoqueRepository;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@code abrir()} tira um snapshot de todos os saldos ativos do depósito na hora —
 * {@code quantidadeSistema} de cada item é fixo desde a abertura, mesmo que o saldo real
 * mude durante a contagem (é exatamente esse descasamento que a contagem física detecta).
 * {@code finalizar()} gera um {@code AJUSTE} (via {@link EstoqueMovimentoService}, mesmo
 * ledger de qualquer outro movimento) pra cada item com divergência ≠ 0.
 */
@Service
@RequiredArgsConstructor
public class InventarioFisicoService {

    private final InventarioFisicoRepository repository;
    private final InventarioFisicoItemRepository itemRepository;
    private final SaldoEstoqueRepository saldoRepository;
    private final DepositoService depositoService;
    private final EstoqueMovimentoService movimentoService;
    private final EstoqueMapper mapper;

    @Transactional(readOnly = true)
    public Page<InventarioFisicoResponse> listarPorDeposito(Long depositoId, Pageable pageable) {
        Page<InventarioFisico> pagina = repository.findByDepositoId(depositoId, pageable);
        if (!pagina.isEmpty()) {
            repository.hidratarItens(pagina.getContent().stream().map(InventarioFisico::getId).toList());
        }
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public InventarioFisicoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public InventarioFisicoResponse abrir(InventarioFisicoAbrirRequest request, Long responsavelId) {
        Deposito deposito = depositoService.buscarEntidade(request.depositoId());

        InventarioFisico inventario = InventarioFisico.builder()
                .empresaId(request.empresaId())
                .deposito(deposito)
                .tipo(request.tipo())
                .situacao(SituacaoInventario.ABERTO)
                .responsavelId(responsavelId)
                .dataAbertura(Instant.now())
                .build();
        inventario = repository.save(inventario);

        for (SaldoEstoque saldo : saldoRepository.findAllByDepositoIdAndQuantidadeGreaterThan(request.depositoId(), BigDecimal.ZERO)) {
            InventarioFisicoItem item = InventarioFisicoItem.builder()
                    .inventario(inventario)
                    .produtoId(saldo.getProdutoId())
                    .quantidadeSistema(saldo.getQuantidade())
                    .build();
            inventario.getItens().add(item);
        }
        return mapper.toResponse(inventario);
    }

    @Transactional
    public InventarioFisicoResponse iniciarContagem(Long id) {
        InventarioFisico inventario = buscarEntidade(id);
        exigirSituacao(inventario, SituacaoInventario.ABERTO);
        inventario.setSituacao(SituacaoInventario.EM_CONTAGEM);
        return mapper.toResponse(inventario);
    }

    @Transactional
    public void lancarContagem(Long inventarioId, Long itemId, InventarioFisicoContagemRequest request, Long contadorId) {
        InventarioFisico inventario = buscarEntidade(inventarioId);
        if (inventario.getSituacao() != SituacaoInventario.ABERTO && inventario.getSituacao() != SituacaoInventario.EM_CONTAGEM) {
            throw new ConflictException(EstoqueErrorCode.INVENTARIO_SITUACAO_INVALIDA);
        }
        InventarioFisicoItem item = itemRepository.findById(itemId)
                .filter(candidato -> candidato.getInventario().getId().equals(inventarioId))
                .orElseThrow(() -> NotFoundException.of("Item de inventário", itemId));
        item.setQuantidadeContada(request.quantidadeContada());
        item.setContadorId(contadorId);
        item.setDataContagem(Instant.now());
    }

    @Transactional
    public InventarioFisicoResponse finalizar(Long id, Long usuarioId) {
        InventarioFisico inventario = buscarEntidade(id);
        if (inventario.getSituacao() != SituacaoInventario.EM_CONTAGEM && inventario.getSituacao() != SituacaoInventario.ABERTO) {
            throw new ConflictException(EstoqueErrorCode.INVENTARIO_SITUACAO_INVALIDA);
        }

        java.util.List<InventarioFisicoItem> itens = itemRepository.findByInventarioId(id);
        for (InventarioFisicoItem item : itens) {
            if (item.getQuantidadeContada() == null) {
                throw new BusinessException(EstoqueErrorCode.INVENTARIO_ITEM_NAO_CONTADO);
            }
        }
        for (InventarioFisicoItem item : itens) {
            BigDecimal divergencia = item.getQuantidadeDivergencia();
            if (divergencia.signum() == 0) {
                continue;
            }
            movimentoService.registrar(new EstoqueMovimentoRequest(
                    item.getProdutoId(), inventario.getDeposito().getId(), item.getLote() != null ? item.getLote().getId() : null, null,
                    TipoMovimentoEstoque.AJUSTE, divergencia.signum() > 0 ? DirecaoAjuste.ACRESCIMO : DirecaoAjuste.DECRESCIMO,
                    divergencia.abs(), null, OrigemMovimentoEstoque.INVENTARIO, item.getId(), usuarioId,
                    "Ajuste automático — inventário #" + inventario.getId()), usuarioId);
        }

        inventario.setSituacao(SituacaoInventario.FINALIZADO);
        inventario.setDataFechamento(Instant.now());
        return mapper.toResponse(inventario);
    }

    @Transactional
    public InventarioFisicoResponse cancelar(Long id) {
        InventarioFisico inventario = buscarEntidade(id);
        if (inventario.getSituacao() == SituacaoInventario.FINALIZADO) {
            throw new ConflictException(EstoqueErrorCode.INVENTARIO_SITUACAO_INVALIDA);
        }
        inventario.setSituacao(SituacaoInventario.CANCELADO);
        return mapper.toResponse(inventario);
    }

    private void exigirSituacao(InventarioFisico inventario, SituacaoInventario esperada) {
        if (inventario.getSituacao() != esperada) {
            throw new ConflictException(EstoqueErrorCode.INVENTARIO_SITUACAO_INVALIDA);
        }
    }

    private InventarioFisico buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Inventário físico", id));
    }
}
