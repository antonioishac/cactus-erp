import { cadastroBaseApi } from '@shared/api/httpClients'
import type { PageResponse } from '@shared/api/pageResponse'
import type {
  ProdutoParaSelecaoResponse,
  TabelaPrecoRequest,
  TabelaPrecoResponse,
  TabelasPrecoFiltro,
} from './types'

const BASE_URL = '/api/v1/tabelas-precos'

export async function listarTabelasPreco(
  filtro: TabelasPrecoFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<TabelaPrecoResponse>> {
  const { data } = await cadastroBaseApi.get<PageResponse<TabelaPrecoResponse>>(BASE_URL, {
    params: { nome: filtro.nome, page: pagina, size: tamanho },
  })
  return data
}

export async function buscarTabelaPreco(id: number): Promise<TabelaPrecoResponse> {
  const { data } = await cadastroBaseApi.get<TabelaPrecoResponse>(`${BASE_URL}/${id}`)
  return data
}

export async function criarTabelaPreco(request: TabelaPrecoRequest): Promise<TabelaPrecoResponse> {
  const { data } = await cadastroBaseApi.post<TabelaPrecoResponse>(BASE_URL, request)
  return data
}

export async function atualizarTabelaPreco(id: number, request: TabelaPrecoRequest): Promise<TabelaPrecoResponse> {
  const { data } = await cadastroBaseApi.put<TabelaPrecoResponse>(`${BASE_URL}/${id}`, request)
  return data
}

/** Busca de produto por nome, só pra alimentar o Select de item da tabela — não reaproveita `features/produtos/api.ts` de propósito (limite de feature). */
export async function buscarProdutosParaSelecao(nome: string | undefined): Promise<ProdutoParaSelecaoResponse[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ProdutoParaSelecaoResponse>>('/api/v1/produtos', {
    params: { nome, size: 20 },
  })
  return data.conteudo
}
