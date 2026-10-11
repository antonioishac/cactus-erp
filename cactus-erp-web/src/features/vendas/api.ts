import { cadastroBaseApi, estoqueApi, vendasApi } from '@shared/api/httpClients'
import { baixarArquivo, type FormatoRelatorio } from '@shared/api/download'
import type { PageResponse } from '@shared/api/pageResponse'
import type { AppError } from '@shared/api/problemDetail'
import type {
  ComissaoVendedorPeriodoResponse,
  ProdutoMaisVendidoResponse,
  VendaPeriodoItemResponse,
  VendasPeriodoFiltro,
  ClienteResumo,
  ColaboradorResumo,
  ComissaoObjetivoRequest,
  ComissaoObjetivoResponse,
  ComissaoPerfilRequest,
  ComissaoPerfilResponse,
  ComissoesFiltro,
  DepositoResumo,
  FormaPagamentoResumo,
  GerarVendaDeOrcamentoRequest,
  NotaFiscalModeloRequest,
  NotaFiscalModeloResponse,
  NotaFiscalTipoRequest,
  NotaFiscalTipoResponse,
  OrcamentosFiltro,
  ProdutoResumo,
  TransportadoraResumo,
  VendaCondicoesPagamentoRequest,
  VendaCondicoesPagamentoResponse,
  VendaComissaoResponse,
  VendaFreteRequest,
  VendaFreteResponse,
  VendaItemRequest,
  VendaItemResponse,
  VendaOrcamentoItemRequest,
  VendaOrcamentoItemResponse,
  VendaOrcamentoRequest,
  VendaOrcamentoResponse,
  VendaRequest,
  VendaResponse,
  VendasFiltro,
  VendedorMetaRequest,
  VendedorMetaResponse,
  VendedorRequest,
  VendedorResponse,
  VendedorRotaRequest,
  VendedorRotaResponse,
} from './types'

// --- Vendedores ---

export async function listarVendedores(pagina: number, tamanho = 10): Promise<PageResponse<VendedorResponse>> {
  const { data } = await vendasApi.get<PageResponse<VendedorResponse>>('/api/v1/vendedores', {
    params: { page: pagina, size: tamanho },
  })
  return data
}

export async function buscarVendedor(id: number): Promise<VendedorResponse> {
  const { data } = await vendasApi.get<VendedorResponse>(`/api/v1/vendedores/${id}`)
  return data
}

export async function criarVendedor(request: VendedorRequest): Promise<VendedorResponse> {
  const { data } = await vendasApi.post<VendedorResponse>('/api/v1/vendedores', request)
  return data
}

export async function atualizarVendedor(id: number, request: VendedorRequest): Promise<VendedorResponse> {
  const { data } = await vendasApi.put<VendedorResponse>(`/api/v1/vendedores/${id}`, request)
  return data
}

// --- AFV: rota e metas do vendedor (sub-recursos, sem paginação própria) ---

export async function listarRotaVendedor(vendedorId: number): Promise<VendedorRotaResponse[]> {
  const { data } = await vendasApi.get<VendedorRotaResponse[]>(`/api/v1/vendedores/${vendedorId}/rota`)
  return data
}

export async function adicionarRotaVendedor(vendedorId: number, request: VendedorRotaRequest): Promise<VendedorRotaResponse> {
  const { data } = await vendasApi.post<VendedorRotaResponse>(`/api/v1/vendedores/${vendedorId}/rota`, request)
  return data
}

export async function removerRotaVendedor(vendedorId: number, rotaId: number): Promise<void> {
  await vendasApi.delete(`/api/v1/vendedores/${vendedorId}/rota/${rotaId}`)
}

export async function listarMetasVendedor(vendedorId: number): Promise<VendedorMetaResponse[]> {
  const { data } = await vendasApi.get<VendedorMetaResponse[]>(`/api/v1/vendedores/${vendedorId}/metas`)
  return data
}

export async function adicionarMetaVendedor(vendedorId: number, request: VendedorMetaRequest): Promise<VendedorMetaResponse> {
  const { data } = await vendasApi.post<VendedorMetaResponse>(`/api/v1/vendedores/${vendedorId}/metas`, request)
  return data
}

export async function removerMetaVendedor(vendedorId: number, metaId: number): Promise<void> {
  await vendasApi.delete(`/api/v1/vendedores/${vendedorId}/metas/${metaId}`)
}

// --- Perfis de Comissão ---

export async function listarComissaoPerfis(): Promise<PageResponse<ComissaoPerfilResponse>> {
  const { data } = await vendasApi.get<PageResponse<ComissaoPerfilResponse>>('/api/v1/comissao-perfis', {
    params: { size: 100 },
  })
  return data
}

export async function criarComissaoPerfil(request: ComissaoPerfilRequest): Promise<ComissaoPerfilResponse> {
  const { data } = await vendasApi.post<ComissaoPerfilResponse>('/api/v1/comissao-perfis', request)
  return data
}

export async function buscarComissaoPerfil(id: number): Promise<ComissaoPerfilResponse> {
  const { data } = await vendasApi.get<ComissaoPerfilResponse>(`/api/v1/comissao-perfis/${id}`)
  return data
}

export async function atualizarComissaoPerfil(
  id: number,
  request: ComissaoPerfilRequest,
): Promise<ComissaoPerfilResponse> {
  const { data } = await vendasApi.put<ComissaoPerfilResponse>(`/api/v1/comissao-perfis/${id}`, request)
  return data
}

// --- Comissão por Objetivo (meta de comissão por produto, sub-recurso do Perfil) ---

export async function listarObjetivosComissaoPerfil(comissaoPerfilId: number): Promise<ComissaoObjetivoResponse[]> {
  const { data } = await vendasApi.get<ComissaoObjetivoResponse[]>(`/api/v1/comissao-perfis/${comissaoPerfilId}/objetivos`)
  return data
}

export async function adicionarObjetivoComissaoPerfil(
  comissaoPerfilId: number,
  request: ComissaoObjetivoRequest,
): Promise<ComissaoObjetivoResponse> {
  const { data } = await vendasApi.post<ComissaoObjetivoResponse>(`/api/v1/comissao-perfis/${comissaoPerfilId}/objetivos`, request)
  return data
}

export async function removerObjetivoComissaoPerfil(comissaoPerfilId: number, objetivoId: number): Promise<void> {
  await vendasApi.delete(`/api/v1/comissao-perfis/${comissaoPerfilId}/objetivos/${objetivoId}`)
}

// --- Condições de Pagamento ---

export async function listarCondicoesPagamento(
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<VendaCondicoesPagamentoResponse>> {
  const { data } = await vendasApi.get<PageResponse<VendaCondicoesPagamentoResponse>>('/api/v1/condicoes-pagamento', {
    params: { page: pagina, size: tamanho },
  })
  return data
}

export async function buscarCondicaoPagamento(id: number): Promise<VendaCondicoesPagamentoResponse> {
  const { data } = await vendasApi.get<VendaCondicoesPagamentoResponse>(`/api/v1/condicoes-pagamento/${id}`)
  return data
}

export async function criarCondicaoPagamento(
  request: VendaCondicoesPagamentoRequest,
): Promise<VendaCondicoesPagamentoResponse> {
  const { data } = await vendasApi.post<VendaCondicoesPagamentoResponse>('/api/v1/condicoes-pagamento', request)
  return data
}

export async function atualizarCondicaoPagamento(
  id: number,
  request: VendaCondicoesPagamentoRequest,
): Promise<VendaCondicoesPagamentoResponse> {
  const { data } = await vendasApi.put<VendaCondicoesPagamentoResponse>(`/api/v1/condicoes-pagamento/${id}`, request)
  return data
}

// --- Notas Fiscais: Modelo/Tipo ---

export async function listarNotasFiscaisModelos(): Promise<PageResponse<NotaFiscalModeloResponse>> {
  const { data } = await vendasApi.get<PageResponse<NotaFiscalModeloResponse>>('/api/v1/notas-fiscais-modelos', {
    params: { size: 100 },
  })
  return data
}

export async function criarNotaFiscalModelo(request: NotaFiscalModeloRequest): Promise<NotaFiscalModeloResponse> {
  const { data } = await vendasApi.post<NotaFiscalModeloResponse>('/api/v1/notas-fiscais-modelos', request)
  return data
}

export async function atualizarNotaFiscalModelo(
  id: number,
  request: NotaFiscalModeloRequest,
): Promise<NotaFiscalModeloResponse> {
  const { data } = await vendasApi.put<NotaFiscalModeloResponse>(`/api/v1/notas-fiscais-modelos/${id}`, request)
  return data
}

export async function listarNotasFiscaisTipos(): Promise<PageResponse<NotaFiscalTipoResponse>> {
  const { data } = await vendasApi.get<PageResponse<NotaFiscalTipoResponse>>('/api/v1/notas-fiscais-tipos', {
    params: { size: 100 },
  })
  return data
}

export async function criarNotaFiscalTipo(request: NotaFiscalTipoRequest): Promise<NotaFiscalTipoResponse> {
  const { data } = await vendasApi.post<NotaFiscalTipoResponse>('/api/v1/notas-fiscais-tipos', request)
  return data
}

export async function atualizarNotaFiscalTipo(
  id: number,
  request: NotaFiscalTipoRequest,
): Promise<NotaFiscalTipoResponse> {
  const { data } = await vendasApi.put<NotaFiscalTipoResponse>(`/api/v1/notas-fiscais-tipos/${id}`, request)
  return data
}

// --- Orçamento ---

export async function listarOrcamentos(
  filtro: OrcamentosFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<VendaOrcamentoResponse>> {
  const { data } = await vendasApi.get<PageResponse<VendaOrcamentoResponse>>('/api/v1/orcamentos', {
    params: { ...filtro, page: pagina, size: tamanho },
  })
  return data
}

export async function buscarOrcamento(id: number): Promise<VendaOrcamentoResponse> {
  const { data } = await vendasApi.get<VendaOrcamentoResponse>(`/api/v1/orcamentos/${id}`)
  return data
}

/** Cria um orçamento em `RASCUNHO`, sem itens — itens entram via `adicionarItemOrcamento`. */
export async function criarOrcamento(request: VendaOrcamentoRequest): Promise<VendaOrcamentoResponse> {
  const { data } = await vendasApi.post<VendaOrcamentoResponse>('/api/v1/orcamentos', request)
  return data
}

export async function adicionarItemOrcamento(
  orcamentoId: number,
  request: VendaOrcamentoItemRequest,
): Promise<VendaOrcamentoItemResponse> {
  const { data } = await vendasApi.post<VendaOrcamentoItemResponse>(`/api/v1/orcamentos/${orcamentoId}/itens`, request)
  return data
}

export async function removerItemOrcamento(orcamentoId: number, itemId: number): Promise<void> {
  await vendasApi.delete(`/api/v1/orcamentos/${orcamentoId}/itens/${itemId}`)
}

export async function enviarOrcamento(id: number): Promise<VendaOrcamentoResponse> {
  const { data } = await vendasApi.post<VendaOrcamentoResponse>(`/api/v1/orcamentos/${id}/enviar`)
  return data
}

export async function aprovarOrcamento(id: number): Promise<VendaOrcamentoResponse> {
  const { data } = await vendasApi.post<VendaOrcamentoResponse>(`/api/v1/orcamentos/${id}/aprovar`)
  return data
}

export async function expirarOrcamento(id: number): Promise<VendaOrcamentoResponse> {
  const { data } = await vendasApi.post<VendaOrcamentoResponse>(`/api/v1/orcamentos/${id}/expirar`)
  return data
}

export async function cancelarOrcamento(id: number): Promise<VendaOrcamentoResponse> {
  const { data } = await vendasApi.post<VendaOrcamentoResponse>(`/api/v1/orcamentos/${id}/cancelar`)
  return data
}

export async function gerarVendaDeOrcamento(
  orcamentoId: number,
  request: GerarVendaDeOrcamentoRequest,
): Promise<VendaResponse> {
  const { data } = await vendasApi.post<VendaResponse>(`/api/v1/orcamentos/${orcamentoId}/gerar-venda`, request)
  return data
}

// --- Venda ---

export async function listarVendas(
  filtro: VendasFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<VendaResponse>> {
  const { data } = await vendasApi.get<PageResponse<VendaResponse>>('/api/v1/vendas', {
    params: { ...filtro, page: pagina, size: tamanho },
  })
  return data
}

export async function buscarVenda(id: number): Promise<VendaResponse> {
  const { data } = await vendasApi.get<VendaResponse>(`/api/v1/vendas/${id}`)
  return data
}

/** Cria uma venda AVULSA em `RASCUNHO`, sem itens — itens entram via `adicionarItemVenda`. */
export async function criarVenda(request: VendaRequest): Promise<VendaResponse> {
  const { data } = await vendasApi.post<VendaResponse>('/api/v1/vendas', request)
  return data
}

export async function adicionarItemVenda(vendaId: number, request: VendaItemRequest): Promise<VendaItemResponse> {
  const { data } = await vendasApi.post<VendaItemResponse>(`/api/v1/vendas/${vendaId}/itens`, request)
  return data
}

export async function atualizarItemVenda(
  vendaId: number,
  itemId: number,
  request: VendaItemRequest,
): Promise<VendaItemResponse> {
  const { data } = await vendasApi.put<VendaItemResponse>(`/api/v1/vendas/${vendaId}/itens/${itemId}`, request)
  return data
}

export async function removerItemVenda(vendaId: number, itemId: number): Promise<void> {
  await vendasApi.delete(`/api/v1/vendas/${vendaId}/itens/${itemId}`)
}

export async function definirFreteVenda(vendaId: number, request: VendaFreteRequest): Promise<VendaFreteResponse> {
  const { data } = await vendasApi.put<VendaFreteResponse>(`/api/v1/vendas/${vendaId}/frete`, request)
  return data
}

export async function buscarFreteVenda(vendaId: number): Promise<VendaFreteResponse | null> {
  try {
    const { data } = await vendasApi.get<VendaFreteResponse>(`/api/v1/vendas/${vendaId}/frete`)
    return data
  } catch (erro) {
    if ((erro as AppError).status === 404) return null
    throw erro
  }
}

export async function confirmarVenda(id: number): Promise<VendaResponse> {
  const { data } = await vendasApi.post<VendaResponse>(`/api/v1/vendas/${id}/confirmar`)
  return data
}

export async function cancelarVenda(id: number): Promise<VendaResponse> {
  const { data } = await vendasApi.post<VendaResponse>(`/api/v1/vendas/${id}/cancelar`)
  return data
}

// --- Comissões (só consulta, geradas automaticamente na confirmação da venda) ---

export async function listarComissoes(
  filtro: ComissoesFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<VendaComissaoResponse>> {
  const { data } = await vendasApi.get<PageResponse<VendaComissaoResponse>>('/api/v1/comissoes', {
    params: { ...filtro, page: pagina, size: tamanho },
  })
  return data
}

// --- Lookups cross-serviço, só pra alimentar Selects ---

export async function buscarProdutosParaSelecao(nome: string | undefined): Promise<ProdutoResumo[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ProdutoResumo>>('/api/v1/produtos', {
    params: { nome, size: 20 },
  })
  return data.conteudo
}

export async function buscarClientesParaSelecao(nome: string | undefined): Promise<ClienteResumo[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ClienteResumo>>('/api/v1/clientes', {
    params: { nome, size: 20 },
  })
  return data.conteudo
}

export async function buscarClienteResumo(id: number): Promise<ClienteResumo> {
  const { data } = await cadastroBaseApi.get<ClienteResumo>(`/api/v1/clientes/${id}`)
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

export async function buscarTransportadorasParaSelecao(nome: string | undefined): Promise<TransportadoraResumo[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<TransportadoraResumo>>('/api/v1/transportadoras', {
    params: { nome, size: 20 },
  })
  return data.conteudo
}

export async function buscarTransportadoraResumo(id: number): Promise<TransportadoraResumo> {
  const { data } = await cadastroBaseApi.get<TransportadoraResumo>(`/api/v1/transportadoras/${id}`)
  return data
}

export async function buscarFormasPagamentoParaSelecao(): Promise<FormaPagamentoResumo[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<FormaPagamentoResumo>>('/api/v1/formas-pagamento', {
    params: { size: 100 },
  })
  return data.conteudo
}

export async function listarDepositosAtivos(empresaId: number): Promise<DepositoResumo[]> {
  const { data } = await estoqueApi.get<DepositoResumo[]>(`/api/v1/depositos/empresa/${empresaId}/ativos`)
  return data
}

// --- Relatórios (JSON pra tela + PDF/Excel com os mesmos filtros) ---

export async function buscarVendasPorPeriodo(filtro: VendasPeriodoFiltro): Promise<VendaPeriodoItemResponse[]> {
  const { data } = await vendasApi.get<VendaPeriodoItemResponse[]>('/api/v1/relatorios/vendas-periodo', { params: filtro })
  return data
}

export async function exportarVendasPorPeriodo(filtro: VendasPeriodoFiltro, formato: FormatoRelatorio): Promise<void> {
  await baixarArquivo(vendasApi, `/api/v1/relatorios/vendas-periodo/${formato}`, `vendas-periodo.${formato}`, { ...filtro })
}

export async function buscarProdutosMaisVendidos(dataInicio: string, dataFim: string): Promise<ProdutoMaisVendidoResponse[]> {
  const { data } = await vendasApi.get<ProdutoMaisVendidoResponse[]>('/api/v1/relatorios/produtos-mais-vendidos', {
    params: { dataInicio, dataFim },
  })
  return data
}

export async function exportarProdutosMaisVendidos(dataInicio: string, dataFim: string, formato: FormatoRelatorio): Promise<void> {
  await baixarArquivo(vendasApi, `/api/v1/relatorios/produtos-mais-vendidos/${formato}`, `produtos-mais-vendidos.${formato}`, {
    dataInicio,
    dataFim,
  })
}

export async function buscarComissoesPorVendedor(dataInicio: string, dataFim: string): Promise<ComissaoVendedorPeriodoResponse[]> {
  const { data } = await vendasApi.get<ComissaoVendedorPeriodoResponse[]>('/api/v1/relatorios/comissoes-vendedor', {
    params: { dataInicio, dataFim },
  })
  return data
}

export async function exportarComissoesPorVendedor(dataInicio: string, dataFim: string, formato: FormatoRelatorio): Promise<void> {
  await baixarArquivo(vendasApi, `/api/v1/relatorios/comissoes-vendedor/${formato}`, `comissoes-vendedor.${formato}`, {
    dataInicio,
    dataFim,
  })
}
