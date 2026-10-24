import { cadastroBaseApi } from '@shared/api/httpClients'
import { baixarArquivo, type FormatoRelatorio } from '@shared/api/download'
import type { PageResponse } from '@shared/api/pageResponse'
import type {
  CestResponse,
  GrupoTributarioResponse,
  NcmResponse,
  OperacaoFiscalResponse,
  ProdutoGrupoRequest,
  ProdutoGrupoResponse,
  ProdutoMarcaRequest,
  ProdutoMarcaResponse,
  ProdutoRequest,
  ProdutoResponse,
  ProdutoSubgrupoRequest,
  ProdutoSubgrupoResponse,
  ProdutoTipoRequest,
  ProdutoTipoResponse,
  ProdutoUnidadeRequest,
  ProdutoUnidadeResponse,
  ProdutosFiltro,
  RecalcularTributacaoPadraoRequest,
} from './types'

const BASE_URL = '/api/v1/produtos'

export async function listarProdutos(
  filtro: ProdutosFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<ProdutoResponse>> {
  const { data } = await cadastroBaseApi.get<PageResponse<ProdutoResponse>>(BASE_URL, {
    params: { ...filtro, page: pagina, size: tamanho },
  })
  return data
}

export async function buscarProduto(id: number): Promise<ProdutoResponse> {
  const { data } = await cadastroBaseApi.get<ProdutoResponse>(`${BASE_URL}/${id}`)
  return data
}

export async function criarProduto(request: ProdutoRequest): Promise<ProdutoResponse> {
  const { data } = await cadastroBaseApi.post<ProdutoResponse>(BASE_URL, request)
  return data
}

export async function atualizarProduto(id: number, request: ProdutoRequest): Promise<ProdutoResponse> {
  const { data } = await cadastroBaseApi.put<ProdutoResponse>(`${BASE_URL}/${id}`, request)
  return data
}

export async function exportarProdutosCadastro(filtro: ProdutosFiltro, formato: FormatoRelatorio): Promise<void> {
  await baixarArquivo(cadastroBaseApi, `/api/v1/relatorios/produtos-cadastro/${formato}`, `produtos-cadastro.${formato}`, {
    ...filtro,
  })
}

export async function recalcularTributacaoPadrao(
  id: number,
  request: RecalcularTributacaoPadraoRequest,
): Promise<ProdutoResponse> {
  const { data } = await cadastroBaseApi.post<ProdutoResponse>(
    `${BASE_URL}/${id}/recalcular-tributacao-padrao`,
    request,
  )
  return data
}

// --- Lookups (nome+situação, catálogo pequeno — carregado inteiro, size=200) ---

export async function listarProdutoGrupos(): Promise<ProdutoGrupoResponse[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ProdutoGrupoResponse>>('/api/v1/produtos-grupo', {
    params: { size: 200 },
  })
  return data.conteudo
}
export async function criarProdutoGrupo(request: ProdutoGrupoRequest): Promise<ProdutoGrupoResponse> {
  const { data } = await cadastroBaseApi.post<ProdutoGrupoResponse>('/api/v1/produtos-grupo', request)
  return data
}
export async function atualizarProdutoGrupo(id: number, request: ProdutoGrupoRequest): Promise<ProdutoGrupoResponse> {
  const { data } = await cadastroBaseApi.put<ProdutoGrupoResponse>(`/api/v1/produtos-grupo/${id}`, request)
  return data
}

export async function listarProdutoSubgrupos(): Promise<ProdutoSubgrupoResponse[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ProdutoSubgrupoResponse>>('/api/v1/produtos-subgrupo', {
    params: { size: 200 },
  })
  return data.conteudo
}
export async function criarProdutoSubgrupo(request: ProdutoSubgrupoRequest): Promise<ProdutoSubgrupoResponse> {
  const { data } = await cadastroBaseApi.post<ProdutoSubgrupoResponse>('/api/v1/produtos-subgrupo', request)
  return data
}
export async function atualizarProdutoSubgrupo(
  id: number,
  request: ProdutoSubgrupoRequest,
): Promise<ProdutoSubgrupoResponse> {
  const { data } = await cadastroBaseApi.put<ProdutoSubgrupoResponse>(`/api/v1/produtos-subgrupo/${id}`, request)
  return data
}

export async function listarProdutoTipos(): Promise<ProdutoTipoResponse[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ProdutoTipoResponse>>('/api/v1/produtos-tipos', {
    params: { size: 200 },
  })
  return data.conteudo
}
export async function criarProdutoTipo(request: ProdutoTipoRequest): Promise<ProdutoTipoResponse> {
  const { data } = await cadastroBaseApi.post<ProdutoTipoResponse>('/api/v1/produtos-tipos', request)
  return data
}
export async function atualizarProdutoTipo(id: number, request: ProdutoTipoRequest): Promise<ProdutoTipoResponse> {
  const { data } = await cadastroBaseApi.put<ProdutoTipoResponse>(`/api/v1/produtos-tipos/${id}`, request)
  return data
}

export async function listarProdutoUnidades(): Promise<ProdutoUnidadeResponse[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ProdutoUnidadeResponse>>('/api/v1/produtos-unidade', {
    params: { size: 200 },
  })
  return data.conteudo
}
export async function criarProdutoUnidade(request: ProdutoUnidadeRequest): Promise<ProdutoUnidadeResponse> {
  const { data } = await cadastroBaseApi.post<ProdutoUnidadeResponse>('/api/v1/produtos-unidade', request)
  return data
}
export async function atualizarProdutoUnidade(
  id: number,
  request: ProdutoUnidadeRequest,
): Promise<ProdutoUnidadeResponse> {
  const { data } = await cadastroBaseApi.put<ProdutoUnidadeResponse>(`/api/v1/produtos-unidade/${id}`, request)
  return data
}

export async function listarProdutoMarcas(): Promise<ProdutoMarcaResponse[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ProdutoMarcaResponse>>('/api/v1/produtos-marca', {
    params: { size: 200 },
  })
  return data.conteudo
}
export async function criarProdutoMarca(request: ProdutoMarcaRequest): Promise<ProdutoMarcaResponse> {
  const { data } = await cadastroBaseApi.post<ProdutoMarcaResponse>('/api/v1/produtos-marca', request)
  return data
}
export async function atualizarProdutoMarca(id: number, request: ProdutoMarcaRequest): Promise<ProdutoMarcaResponse> {
  const { data } = await cadastroBaseApi.put<ProdutoMarcaResponse>(`/api/v1/produtos-marca/${id}`, request)
  return data
}

// --- Catálogos fiscais (busca assíncrona — NCM em especial é grande demais pra carregar inteiro) ---

export async function buscarNcm(busca: string | undefined): Promise<NcmResponse[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<NcmResponse>>('/api/v1/ncm', {
    params: { busca, size: 20 },
  })
  return data.conteudo
}

export async function buscarCest(descricao: string | undefined): Promise<CestResponse[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<CestResponse>>('/api/v1/cest', {
    params: { descricao, size: 20 },
  })
  return data.conteudo
}

export async function listarGruposTributarios(nome?: string): Promise<GrupoTributarioResponse[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<GrupoTributarioResponse>>(
    '/api/v1/tributacao/grupos-tributarios',
    { params: { nome, size: 50 } },
  )
  return data.conteudo
}

export async function listarOperacoesFiscais(nome?: string): Promise<OperacaoFiscalResponse[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<OperacaoFiscalResponse>>(
    '/api/v1/tributacao/operacoes-fiscais',
    { params: { nome, size: 50 } },
  )
  return data.conteudo
}
