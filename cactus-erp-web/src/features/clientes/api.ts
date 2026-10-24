import { cadastroBaseApi } from '@shared/api/httpClients'
import { baixarArquivo, type FormatoRelatorio } from '@shared/api/download'
import type { PageResponse } from '@shared/api/pageResponse'
import type { ClienteRequest, ClienteResponse, ClientesFiltro } from './types'

const BASE_URL = '/api/v1/clientes'

export async function listarClientes(
  filtro: ClientesFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<ClienteResponse>> {
  const { data } = await cadastroBaseApi.get<PageResponse<ClienteResponse>>(BASE_URL, {
    // sort=pessoa.nome,asc — path de propriedade join, funciona desde que o
    // cadastro-base-service pare de forçar DISTINCT incondicional na Specification
    // (achado real, corrigido em ClienteSpecifications.java — MySQL rejeita
    // DISTINCT + ORDER BY numa coluna fora da projeção, ver CLAUDE.md).
    params: { ...filtro, page: pagina, size: tamanho, sort: 'pessoa.nome,asc' },
  })
  return data
}

export async function buscarCliente(id: number): Promise<ClienteResponse> {
  const { data } = await cadastroBaseApi.get<ClienteResponse>(`${BASE_URL}/${id}`)
  return data
}

export async function criarCliente(request: ClienteRequest): Promise<ClienteResponse> {
  const { data } = await cadastroBaseApi.post<ClienteResponse>(BASE_URL, request)
  return data
}

export async function atualizarCliente(
  id: number,
  request: ClienteRequest,
): Promise<ClienteResponse> {
  const { data } = await cadastroBaseApi.put<ClienteResponse>(`${BASE_URL}/${id}`, request)
  return data
}

export async function exportarClientesCadastro(filtro: ClientesFiltro, formato: FormatoRelatorio): Promise<void> {
  await baixarArquivo(cadastroBaseApi, `/api/v1/relatorios/clientes-cadastro/${formato}`, `clientes-cadastro.${formato}`, {
    ...filtro,
  })
}
