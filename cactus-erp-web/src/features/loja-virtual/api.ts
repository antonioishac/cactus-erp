import { lojaApi } from '@shared/api/httpClients'
import { baixarArquivo, type FormatoRelatorio } from '@shared/api/download'
import type { PageResponse } from '@shared/api/pageResponse'
import type {
  AtributoCategoriaOpcaoRequest,
  AtributoCategoriaOpcaoResponse,
  AtributoCategoriaRequest,
  AtributoCategoriaResponse,
  CancelarPedidoRequest,
  CategoriaLojaRequest,
  CategoriaLojaResponse,
  LojaRequest,
  LojaResponse,
  PedidoResponse,
  PedidosFiltro,
  ProdutoLojaRequest,
  ProdutoLojaResponse,
  VarianteProdutoLojaRequest,
  VarianteProdutoLojaResponse,
} from './types'

const BASE = '/api/v1'
/** Backend não tem endpoint "listar todos" pra nenhum catálogo deste módulo — usado como teto prático pra popular Select (volume esperado: dezenas de lojas/categorias por plataforma). */
const SEM_PAGINACAO = 1000

// --- Loja ---

export async function listarLojas(pagina: number, tamanho = 10): Promise<PageResponse<LojaResponse>> {
  const { data } = await lojaApi.get<PageResponse<LojaResponse>>(`${BASE}/lojas`, { params: { page: pagina, size: tamanho } })
  return data
}

export async function listarTodasLojas(): Promise<LojaResponse[]> {
  const { data } = await lojaApi.get<PageResponse<LojaResponse>>(`${BASE}/lojas`, { params: { size: SEM_PAGINACAO } })
  return data.conteudo
}

export async function buscarLoja(id: number): Promise<LojaResponse> {
  const { data } = await lojaApi.get<LojaResponse>(`${BASE}/lojas/${id}`)
  return data
}

export async function criarLoja(request: LojaRequest): Promise<LojaResponse> {
  const { data } = await lojaApi.post<LojaResponse>(`${BASE}/lojas`, request)
  return data
}

export async function atualizarLoja(id: number, request: LojaRequest): Promise<LojaResponse> {
  const { data } = await lojaApi.put<LojaResponse>(`${BASE}/lojas/${id}`, request)
  return data
}

export async function cancelarAssinaturaLoja(id: number): Promise<LojaResponse> {
  const { data } = await lojaApi.post<LojaResponse>(`${BASE}/lojas/${id}/cancelar-assinatura`)
  return data
}

export async function reativarAssinaturaLoja(id: number): Promise<LojaResponse> {
  const { data } = await lojaApi.post<LojaResponse>(`${BASE}/lojas/${id}/reativar-assinatura`)
  return data
}

// --- Categoria da Loja ---

export async function listarCategorias(
  lojaId: number | undefined,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<CategoriaLojaResponse>> {
  const { data } = await lojaApi.get<PageResponse<CategoriaLojaResponse>>(`${BASE}/categorias-loja`, {
    params: { lojaId, page: pagina, size: tamanho },
  })
  return data
}

export async function listarTodasCategorias(lojaId: number): Promise<CategoriaLojaResponse[]> {
  const { data } = await lojaApi.get<PageResponse<CategoriaLojaResponse>>(`${BASE}/categorias-loja`, {
    params: { lojaId, size: SEM_PAGINACAO },
  })
  return data.conteudo
}

export async function criarCategoria(request: CategoriaLojaRequest): Promise<CategoriaLojaResponse> {
  const { data } = await lojaApi.post<CategoriaLojaResponse>(`${BASE}/categorias-loja`, request)
  return data
}

export async function atualizarCategoria(id: number, request: CategoriaLojaRequest): Promise<CategoriaLojaResponse> {
  const { data } = await lojaApi.put<CategoriaLojaResponse>(`${BASE}/categorias-loja/${id}`, request)
  return data
}

export async function excluirCategoria(id: number): Promise<void> {
  await lojaApi.delete(`${BASE}/categorias-loja/${id}`)
}

// --- Atributo de Categoria ---

export async function listarAtributos(
  categoriaLojaId: number | undefined,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<AtributoCategoriaResponse>> {
  const { data } = await lojaApi.get<PageResponse<AtributoCategoriaResponse>>(`${BASE}/atributos-categoria`, {
    params: { categoriaLojaId, page: pagina, size: tamanho },
  })
  return data
}

export async function listarTodosAtributos(categoriaLojaId: number): Promise<AtributoCategoriaResponse[]> {
  const { data } = await lojaApi.get<PageResponse<AtributoCategoriaResponse>>(`${BASE}/atributos-categoria`, {
    params: { categoriaLojaId, size: SEM_PAGINACAO },
  })
  return data.conteudo
}

export async function criarAtributo(request: AtributoCategoriaRequest): Promise<AtributoCategoriaResponse> {
  const { data } = await lojaApi.post<AtributoCategoriaResponse>(`${BASE}/atributos-categoria`, request)
  return data
}

export async function atualizarAtributo(id: number, request: AtributoCategoriaRequest): Promise<AtributoCategoriaResponse> {
  const { data } = await lojaApi.put<AtributoCategoriaResponse>(`${BASE}/atributos-categoria/${id}`, request)
  return data
}

export async function excluirAtributo(id: number): Promise<void> {
  await lojaApi.delete(`${BASE}/atributos-categoria/${id}`)
}

export async function adicionarOpcaoAtributo(
  atributoId: number,
  request: AtributoCategoriaOpcaoRequest,
): Promise<AtributoCategoriaOpcaoResponse> {
  const { data } = await lojaApi.post<AtributoCategoriaOpcaoResponse>(`${BASE}/atributos-categoria/${atributoId}/opcoes`, request)
  return data
}

export async function excluirOpcaoAtributo(atributoId: number, opcaoId: number): Promise<void> {
  await lojaApi.delete(`${BASE}/atributos-categoria/${atributoId}/opcoes/${opcaoId}`)
}

// --- Produto da Loja ---

export async function listarProdutos(
  lojaId: number | undefined,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<ProdutoLojaResponse>> {
  const { data } = await lojaApi.get<PageResponse<ProdutoLojaResponse>>(`${BASE}/produtos-loja`, {
    params: { lojaId, page: pagina, size: tamanho },
  })
  return data
}

export async function buscarProduto(id: number): Promise<ProdutoLojaResponse> {
  const { data } = await lojaApi.get<ProdutoLojaResponse>(`${BASE}/produtos-loja/${id}`)
  return data
}

export async function criarProduto(request: ProdutoLojaRequest): Promise<ProdutoLojaResponse> {
  const { data } = await lojaApi.post<ProdutoLojaResponse>(`${BASE}/produtos-loja`, request)
  return data
}

export async function atualizarProduto(id: number, request: ProdutoLojaRequest): Promise<ProdutoLojaResponse> {
  const { data } = await lojaApi.put<ProdutoLojaResponse>(`${BASE}/produtos-loja/${id}`, request)
  return data
}

export async function excluirProduto(id: number): Promise<void> {
  await lojaApi.delete(`${BASE}/produtos-loja/${id}`)
}

export async function criarVariante(produtoId: number, request: VarianteProdutoLojaRequest): Promise<VarianteProdutoLojaResponse> {
  const { data } = await lojaApi.post<VarianteProdutoLojaResponse>(`${BASE}/produtos-loja/${produtoId}/variantes`, request)
  return data
}

export async function atualizarVariante(id: number, request: VarianteProdutoLojaRequest): Promise<VarianteProdutoLojaResponse> {
  const { data } = await lojaApi.put<VarianteProdutoLojaResponse>(`${BASE}/variantes-produto-loja/${id}`, request)
  return data
}

export async function excluirVariante(id: number): Promise<void> {
  await lojaApi.delete(`${BASE}/variantes-produto-loja/${id}`)
}

// --- Pedido ---

export async function listarPedidos(filtro: PedidosFiltro, pagina: number, tamanho = 10): Promise<PageResponse<PedidoResponse>> {
  const { data } = await lojaApi.get<PageResponse<PedidoResponse>>(`${BASE}/pedidos`, {
    params: { ...filtro, page: pagina, size: tamanho },
  })
  return data
}

export async function buscarPedido(id: number): Promise<PedidoResponse> {
  const { data } = await lojaApi.get<PedidoResponse>(`${BASE}/pedidos/${id}`)
  return data
}

export async function confirmarPagamentoPedido(id: number): Promise<PedidoResponse> {
  const { data } = await lojaApi.post<PedidoResponse>(`${BASE}/pedidos/${id}/confirmar-pagamento`)
  return data
}

export async function marcarPedidoEmSeparacao(id: number): Promise<PedidoResponse> {
  const { data } = await lojaApi.post<PedidoResponse>(`${BASE}/pedidos/${id}/marcar-em-separacao`)
  return data
}

export async function marcarPedidoEnviado(id: number): Promise<PedidoResponse> {
  const { data } = await lojaApi.post<PedidoResponse>(`${BASE}/pedidos/${id}/marcar-enviado`)
  return data
}

export async function marcarPedidoEntregue(id: number): Promise<PedidoResponse> {
  const { data } = await lojaApi.post<PedidoResponse>(`${BASE}/pedidos/${id}/marcar-entregue`)
  return data
}

export async function cancelarPedido(id: number, request: CancelarPedidoRequest): Promise<PedidoResponse> {
  const { data } = await lojaApi.post<PedidoResponse>(`${BASE}/pedidos/${id}/cancelar`, request)
  return data
}

// --- Relatório: Pedidos da Loja por Período ---

export async function exportarPedidosLojaPeriodo(
  dataInicio: string,
  dataFim: string,
  lojaId: number | undefined,
  situacao: string | undefined,
  formato: FormatoRelatorio,
): Promise<void> {
  await baixarArquivo(lojaApi, `${BASE}/relatorios/pedidos-loja-periodo/${formato}`, `pedidos-loja-periodo.${formato}`, {
    dataInicio,
    dataFim,
    lojaId,
    situacao,
  })
}
