package br.com.cactusdigital.erp.compras.service;

import br.com.cactusdigital.erp.compras.dto.PedidoCompraRelatorioItemResponse;
import br.com.cactusdigital.erp.compras.entity.PedidoCompra;
import br.com.cactusdigital.erp.compras.entity.SituacaoPedidoCompra;
import br.com.cactusdigital.erp.compras.repository.PedidoCompraRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Relatório gerencial de pedidos de compra — calculado sob demanda a partir do próprio
 * {@link PedidoCompra} já existente, mesmo critério de {@code RelatorioVendasService}
 * (vendas-service)/{@code CurvaAbcService} (controle-estoque-service): sem snapshot
 * persistido, agregação em memória a partir de uma busca já filtrada por período.
 */
@Service
@RequiredArgsConstructor
public class RelatorioComprasService {

    private final PedidoCompraRepository repository;

    /** Filtro por {@code dataPedido} (pedido sem data definida nunca entra, mesmo
     * critério de exclusão natural já usado nos outros relatórios por período).
     * {@code fornecedorId}/{@code situacao} são filtros opcionais aplicados em memória,
     * igual {@code RelatorioVendasService#vendasPorPeriodo}. */
    @Transactional(readOnly = true)
    public List<PedidoCompraRelatorioItemResponse> pedidosPorPeriodo(
            LocalDate dataInicio, LocalDate dataFim, Long fornecedorId, SituacaoPedidoCompra situacao) {
        return repository.buscarComItensPorPeriodo(dataInicio, dataFim).stream()
                .filter(pedido -> fornecedorId == null || fornecedorId.equals(pedido.getFornecedorId()))
                .filter(pedido -> situacao == null || situacao == pedido.getSituacao())
                .sorted(Comparator.comparing(PedidoCompra::getDataPedido))
                .map(pedido -> new PedidoCompraRelatorioItemResponse(
                        pedido.getId(), pedido.getDataPedido(), pedido.getEmpresaId(), pedido.getFornecedorId(),
                        pedido.getTipo(), pedido.getSituacao(), pedido.getItens().size(), pedido.getValorTotal()))
                .toList();
    }
}
