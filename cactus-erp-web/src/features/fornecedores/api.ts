import { cadastroBaseApi } from '@shared/api/httpClients'
import { baixarArquivo, type FormatoRelatorio } from '@shared/api/download'
import type { PageResponse } from '@shared/api/pageResponse'
import type { FornecedorRequest, FornecedorResponse, FornecedoresFiltro } from './types'

const BASE_URL = '/api/v1/fornecedores'

export async function listarFornecedores(
  filtro: FornecedoresFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<FornecedorResponse>> {
  const { data } = await cadastroBaseApi.get<PageResponse<FornecedorResponse>>(BASE_URL, {
    // sort=pessoa.nome,asc só funciona desde o fix de 2026-09-01 em
    // FornecedorSpecifications (mesmo achado de ClienteSpecifications — DISTINCT
    // incondicional quebrava ORDER BY em coluna joined no MySQL).
    params: { ...filtro, page: pagina, size: tamanho, sort: 'pessoa.nome,asc' },
  })
  return data
}

export async function buscarFornecedor(id: number): Promise<FornecedorResponse> {
  const { data } = await cadastroBaseApi.get<FornecedorResponse>(`${BASE_URL}/${id}`)
  return data
}

export async function criarFornecedor(request: FornecedorRequest): Promise<FornecedorResponse> {
  const { data } = await cadastroBaseApi.post<FornecedorResponse>(BASE_URL, request)
  return data
}

export async function atualizarFornecedor(
  id: number,
  request: FornecedorRequest,
): Promise<FornecedorResponse> {
  const { data } = await cadastroBaseApi.put<FornecedorResponse>(`${BASE_URL}/${id}`, request)
  return data
}

export async function exportarFornecedoresCadastro(filtro: FornecedoresFiltro, formato: FormatoRelatorio): Promise<void> {
  await baixarArquivo(cadastroBaseApi, `/api/v1/relatorios/fornecedores-cadastro/${formato}`, `fornecedores-cadastro.${formato}`, {
    ...filtro,
  })
}
