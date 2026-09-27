import { cadastroBaseApi, comprasApi, estoqueApi } from '@shared/api/httpClients'
import type { PageResponse } from '@shared/api/pageResponse'
import type {
  ColaboradorResumo,
  ConfiguracaoComprasEmpresaRequest,
  ConfiguracaoComprasEmpresaResponse,
  CotacaoCompraRequest,
  CotacaoCompraResponse,
  DepositoResumo,
  EmpresaResumo,
  FornecedorCotacaoItemRequest,
  FornecedorCotacaoRequest,
  FornecedorCotacaoResponse,
  FornecedorResumo,
  GerarPedidoDeCotacaoRequest,
  LoteResumo,
  PedidoCompraItemRequest,
  PedidoCompraItemResponse,
  PedidoCompraRequest,
  PedidoCompraResponse,
  PedidosFiltro,
  ProdutoResumo,
  ProdutoUnidadeResumo,
  RecebimentoCompraRequest,
  RecebimentoCompraResponse,
  RequisicaoCompraRequest,
  RequisicaoCompraResponse,
  RequisicoesFiltro,
} from './types'

// --- Requisição de Compra ---

export async function listarRequisicoes(
  filtro: RequisicoesFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<RequisicaoCompraResponse>> {
  const { data } = await comprasApi.get<PageResponse<RequisicaoCompraResponse>>('/api/v1/requisicoes', {
    params: { colaboradorId: filtro.colaboradorId, page: pagina, size: tamanho },
  })
  return data
}

export async function buscarRequisicao(id: number): Promise<RequisicaoCompraResponse> {
  const { data } = await comprasApi.get<RequisicaoCompraResponse>(`/api/v1/requisicoes/${id}`)
  return data
}

export async function criarRequisicao(request: RequisicaoCompraRequest): Promise<RequisicaoCompraResponse> {
  const { data } = await comprasApi.post<RequisicaoCompraResponse>('/api/v1/requisicoes', request)
  return data
}

export async function cancelarRequisicao(id: number): Promise<RequisicaoCompraResponse> {
  const { data } = await comprasApi.post<RequisicaoCompraResponse>(`/api/v1/requisicoes/${id}/cancelar`)
  return data
}

// --- Cotação de Compra (sempre no contexto de uma requisição) ---

export async function listarCotacoes(requisicaoId: number): Promise<PageResponse<CotacaoCompraResponse>> {
  const { data } = await comprasApi.get<PageResponse<CotacaoCompraResponse>>('/api/v1/cotacoes', {
    params: { requisicaoId, size: 50 },
  })
  return data
}

export async function buscarCotacao(id: number): Promise<CotacaoCompraResponse> {
  const { data } = await comprasApi.get<CotacaoCompraResponse>(`/api/v1/cotacoes/${id}`)
  return data
}

export async function criarCotacao(request: CotacaoCompraRequest): Promise<CotacaoCompraResponse> {
  const { data } = await comprasApi.post<CotacaoCompraResponse>('/api/v1/cotacoes', request)
  return data
}

export async function cancelarCotacao(id: number): Promise<CotacaoCompraResponse> {
  const { data } = await comprasApi.post<CotacaoCompraResponse>(`/api/v1/cotacoes/${id}/cancelar`)
  return data
}

export async function adicionarFornecedorCotacao(
  cotacaoId: number,
  request: FornecedorCotacaoRequest,
): Promise<FornecedorCotacaoResponse> {
  const { data } = await comprasApi.post<FornecedorCotacaoResponse>(`/api/v1/cotacoes/${cotacaoId}/fornecedores`, request)
  return data
}

export async function adicionarItemFornecedorCotacao(
  cotacaoId: number,
  fornecedorCotacaoId: number,
  request: FornecedorCotacaoItemRequest,
): Promise<FornecedorCotacaoResponse> {
  const { data } = await comprasApi.post<FornecedorCotacaoResponse>(
    `/api/v1/cotacoes/${cotacaoId}/fornecedores/${fornecedorCotacaoId}/itens`,
    request,
  )
  return data
}

export async function gerarPedidoDeCotacao(
  cotacaoId: number,
  fornecedorCotacaoId: number,
  request: GerarPedidoDeCotacaoRequest,
): Promise<PedidoCompraResponse> {
  const { data } = await comprasApi.post<PedidoCompraResponse>(
    `/api/v1/cotacoes/${cotacaoId}/fornecedores/${fornecedorCotacaoId}/gerar-pedido`,
    request,
  )
  return data
}

// --- Pedido de Compra ---

export async function listarPedidos(
  filtro: PedidosFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<PedidoCompraResponse>> {
  const { data } = await comprasApi.get<PageResponse<PedidoCompraResponse>>('/api/v1/pedidos', {
    params: { fornecedorId: filtro.fornecedorId, page: pagina, size: tamanho },
  })
  return data
}

export async function buscarPedido(id: number): Promise<PedidoCompraResponse> {
  const { data } = await comprasApi.get<PedidoCompraResponse>(`/api/v1/pedidos/${id}`)
  return data
}

/** Cria um pedido AVULSO em `RASCUNHO`, sem itens — itens entram via `adicionarItemPedido`. */
export async function criarPedido(request: PedidoCompraRequest): Promise<PedidoCompraResponse> {
  const { data } = await comprasApi.post<PedidoCompraResponse>('/api/v1/pedidos', request)
  return data
}

export async function adicionarItemPedido(
  pedidoId: number,
  request: PedidoCompraItemRequest,
): Promise<PedidoCompraItemResponse> {
  const { data } = await comprasApi.post<PedidoCompraItemResponse>(`/api/v1/pedidos/${pedidoId}/itens`, request)
  return data
}

export async function removerItemPedido(pedidoId: number, itemId: number): Promise<void> {
  await comprasApi.delete(`/api/v1/pedidos/${pedidoId}/itens/${itemId}`)
}

export async function enviarPedido(id: number): Promise<PedidoCompraResponse> {
  const { data } = await comprasApi.post<PedidoCompraResponse>(`/api/v1/pedidos/${id}/enviar`)
  return data
}

export async function aprovarPedido(id: number): Promise<PedidoCompraResponse> {
  const { data } = await comprasApi.post<PedidoCompraResponse>(`/api/v1/pedidos/${id}/aprovar`)
  return data
}

export async function cancelarPedido(id: number): Promise<PedidoCompraResponse> {
  const { data } = await comprasApi.post<PedidoCompraResponse>(`/api/v1/pedidos/${id}/cancelar`)
  return data
}

// --- Recebimento de Compra (sempre no contexto de um pedido) ---

export async function listarRecebimentos(pedidoId: number): Promise<PageResponse<RecebimentoCompraResponse>> {
  const { data } = await comprasApi.get<PageResponse<RecebimentoCompraResponse>>('/api/v1/recebimentos', {
    params: { pedidoId, size: 50 },
  })
  return data
}

export async function buscarRecebimento(id: number): Promise<RecebimentoCompraResponse> {
  const { data } = await comprasApi.get<RecebimentoCompraResponse>(`/api/v1/recebimentos/${id}`)
  return data
}

export async function criarRecebimento(request: RecebimentoCompraRequest): Promise<RecebimentoCompraResponse> {
  const { data } = await comprasApi.post<RecebimentoCompraResponse>('/api/v1/recebimentos', request)
  return data
}

// --- Configuração de Compras ---

export async function buscarConfiguracaoCompras(empresaId: number): Promise<ConfiguracaoComprasEmpresaResponse> {
  const { data } = await comprasApi.get<ConfiguracaoComprasEmpresaResponse>(`/api/v1/configuracao-compras/empresa/${empresaId}`)
  return data
}

export async function atualizarConfiguracaoCompras(
  empresaId: number,
  request: ConfiguracaoComprasEmpresaRequest,
): Promise<ConfiguracaoComprasEmpresaResponse> {
  const { data } = await comprasApi.put<ConfiguracaoComprasEmpresaResponse>(
    `/api/v1/configuracao-compras/empresa/${empresaId}`,
    request,
  )
  return data
}

// --- Lookups cross-serviço, só pra alimentar Selects (padrão já usado em Estoque/Segurança — busca local, sem reaproveitar outras features) ---

export async function buscarProdutosParaSelecao(nome: string | undefined): Promise<ProdutoResumo[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ProdutoResumo>>('/api/v1/produtos', {
    params: { nome, size: 20 },
  })
  return data.conteudo
}

export async function buscarProdutoUnidadesParaSelecao(): Promise<ProdutoUnidadeResumo[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ProdutoUnidadeResumo>>('/api/v1/produtos-unidade', {
    params: { size: 100 },
  })
  return data.conteudo
}

export async function buscarFornecedoresParaSelecao(nome: string | undefined): Promise<FornecedorResumo[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<FornecedorResumo>>('/api/v1/fornecedores', {
    params: { nome, size: 20 },
  })
  return data.conteudo
}

export async function buscarFornecedorResumo(id: number): Promise<FornecedorResumo> {
  const { data } = await cadastroBaseApi.get<FornecedorResumo>(`/api/v1/fornecedores/${id}`)
  return data
}

export async function buscarColaboradoresParaSelecao(nome: string | undefined): Promise<ColaboradorResumo[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ColaboradorResumo>>('/api/v1/colaboradores', {
    params: { nome, size: 20 },
  })
  return data.conteudo
}

export async function buscarColaboradorResumo(id: number): Promise<ColaboradorResumo> {
  const { data } = await cadastroBaseApi.get<ColaboradorResumo>(`/api/v1/colaboradores/${id}`)
  return data
}

export async function buscarEmpresasParaSelecao(nome: string | undefined): Promise<EmpresaResumo[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<EmpresaResumo>>('/api/v1/empresas', {
    params: { nome, size: 20 },
  })
  return data.conteudo
}

export async function listarDepositosAtivos(empresaId: number): Promise<DepositoResumo[]> {
  const { data } = await estoqueApi.get<DepositoResumo[]>(`/api/v1/depositos/empresa/${empresaId}/ativos`)
  return data
}

export async function listarLotesParaSelecao(produtoId: number | undefined): Promise<LoteResumo[]> {
  const { data } = await estoqueApi.get<PageResponse<LoteResumo>>('/api/v1/lotes', {
    params: { produtoId, size: 50 },
  })
  return data.conteudo
}
