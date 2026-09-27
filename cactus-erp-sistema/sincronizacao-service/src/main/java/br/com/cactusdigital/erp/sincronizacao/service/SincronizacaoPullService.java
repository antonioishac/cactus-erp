package br.com.cactusdigital.erp.sincronizacao.service;

import br.com.cactusdigital.erp.sincronizacao.client.CadastroBaseClient;
import br.com.cactusdigital.erp.sincronizacao.client.EstoqueClient;
import br.com.cactusdigital.erp.sincronizacao.client.SegurancaClient;
import br.com.cactusdigital.erp.sincronizacao.dto.ClienteSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.ConfiguracaoLojaSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.FormaPagamentoSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.OperadorSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.ProdutoBalancaSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.ProdutoSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.PullResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.SaldoAgregadoSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.TabelaPrecoSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.UpstreamPage;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Composição do pull ERP→PDV (Fase 6) — orquestra os três clients (cadastro-base/
 * estoque/segurança). Facade deliberada: cada método é uma composição pequena e
 * independente (não há estado nem regra de negócio compartilhada entre eles), agrupar
 * num serviço só evita 6 classes de uma linha de orquestração cada — ver
 * FASE6_SINCRONIZACAO_PDV.md seção 4.2 ("compõe cadastro-base + saldo agregado...
 * trabalho de orquestração").
 * <p>
 * Este serviço NÃO decide regra de negócio nenhuma (não recalcula tributação, não
 * valida limite de crédito) — só traduz formato e compõe, ver seção 4.3.
 */
@Service
@RequiredArgsConstructor
public class SincronizacaoPullService {

    private static final int TAMANHO_PAGINA_PADRAO = 100;

    private final CadastroBaseClient cadastroBaseClient;
    private final EstoqueClient estoqueClient;
    private final SegurancaClient segurancaClient;

    public PullResponse<ProdutoSincronizacaoResponse> pullProdutos(Long lojaId, Instant desde, int pagina) {
        UpstreamPage<ProdutoSincronizacaoResponse> upstream = cadastroBaseClient.listarProdutos(desde, pagina, TAMANHO_PAGINA_PADRAO);

        List<Long> produtoIds = upstream.conteudo().stream().map(ProdutoSincronizacaoResponse::id).toList();
        Map<Long, SaldoAgregadoSincronizacaoResponse> saldosPorProduto = estoqueClient.agregarPorEmpresaEProdutos(lojaId, produtoIds).stream()
                .collect(Collectors.toMap(SaldoAgregadoSincronizacaoResponse::produtoId, Function.identity()));

        List<ProdutoSincronizacaoResponse> comEstoque = upstream.conteudo().stream()
                .map(produto -> {
                    SaldoAgregadoSincronizacaoResponse saldo = saldosPorProduto.get(produto.id());
                    return produto.comEstoque(saldo != null ? saldo.quantidadeDisponivel() : BigDecimal.ZERO);
                })
                .toList();

        Instant proximoCursor = maiorData(upstream.conteudo().stream().map(ProdutoSincronizacaoResponse::dataAtualizacao).toList(), desde);
        return PullResponse.of(new UpstreamPage<>(comEstoque, upstream.paginaAtual(), upstream.tamanhoPagina(),
                upstream.totalRegistros(), upstream.totalPaginas(), upstream.primeiraPagina(), upstream.ultimaPagina()), proximoCursor);
    }

    public PullResponse<ClienteSincronizacaoResponse> pullClientes(Instant desde, int pagina) {
        UpstreamPage<ClienteSincronizacaoResponse> upstream = cadastroBaseClient.listarClientes(desde, pagina, TAMANHO_PAGINA_PADRAO);
        Instant proximoCursor = maiorData(upstream.conteudo().stream().map(ClienteSincronizacaoResponse::dataAtualizacao).toList(), desde);
        return PullResponse.of(upstream, proximoCursor);
    }

    public PullResponse<TabelaPrecoSincronizacaoResponse> pullTabelasPreco(Instant desde, int pagina) {
        UpstreamPage<TabelaPrecoSincronizacaoResponse> upstream = cadastroBaseClient.listarTabelasPreco(desde, pagina, TAMANHO_PAGINA_PADRAO);
        Instant proximoCursor = maiorData(upstream.conteudo().stream().map(TabelaPrecoSincronizacaoResponse::dataAtualizacao).toList(), desde);
        return PullResponse.of(upstream, proximoCursor);
    }

    public PullResponse<FormaPagamentoSincronizacaoResponse> pullFormasPagamento(Instant desde, int pagina) {
        UpstreamPage<FormaPagamentoSincronizacaoResponse> upstream = cadastroBaseClient.listarFormasPagamento(desde, pagina, TAMANHO_PAGINA_PADRAO);
        Instant proximoCursor = maiorData(upstream.conteudo().stream().map(FormaPagamentoSincronizacaoResponse::dataAtualizacao).toList(), desde);
        return PullResponse.of(upstream, proximoCursor);
    }

    public ConfiguracaoLojaSincronizacaoResponse pullConfiguracaoLoja(Long lojaId) {
        return cadastroBaseClient.buscarConfiguracaoLoja(lojaId);
    }

    /** Sem cursor — lista completa a cada chamada (volume por loja é pequeno, ver
     * FASE6_SINCRONIZACAO_PDV.md seção 2.1 "Operador"). */
    public List<OperadorSincronizacaoResponse> pullOperadores(Long lojaId) {
        return segurancaClient.listarOperadoresPorEmpresa(lojaId);
    }

    /** Caminho B da seção 7.2 — payload reduzido pra balança de rede autônoma. */
    public PullResponse<ProdutoBalancaSincronizacaoResponse> pullProdutosBalanca(Long lojaId, Instant desde, int pagina) {
        UpstreamPage<ProdutoSincronizacaoResponse> upstream = cadastroBaseClient.listarProdutos(desde, pagina, TAMANHO_PAGINA_PADRAO);

        List<ProdutoBalancaSincronizacaoResponse> conteudo = upstream.conteudo().stream()
                .filter(produto -> produto.codigoBalanca() != null)
                .map(produto -> new ProdutoBalancaSincronizacaoResponse(
                        produto.codigoBalanca(),
                        truncar(produto.nome(), 20),
                        produto.valorVenda(),
                        "KG".equalsIgnoreCase(produto.produtoUnidadeSigla()),
                        null,
                        produto.dataAtualizacao()))
                .toList();

        Instant proximoCursor = maiorData(upstream.conteudo().stream().map(ProdutoSincronizacaoResponse::dataAtualizacao).toList(), desde);
        return new PullResponse<>(conteudo, proximoCursor, upstream.paginaAtual(), upstream.tamanhoPagina(),
                upstream.totalRegistros(), upstream.totalPaginas(), upstream.ultimaPagina());
    }

    private static String truncar(String texto, int tamanho) {
        if (texto == null) {
            return null;
        }
        return texto.length() <= tamanho ? texto : texto.substring(0, tamanho);
    }

    private static Instant maiorData(List<Instant> datas, Instant cursorAnterior) {
        return datas.stream().filter(Objects::nonNull).max(Instant::compareTo).orElse(cursorAnterior);
    }
}
