import { cadastroBaseApi } from '@shared/api/httpClients'
import type { PageResponse } from '@shared/api/pageResponse'
import type { TransportadoraRequest, TransportadoraResponse, TransportadorasFiltro } from './types'

const BASE_URL = '/api/v1/transportadoras'

export async function listarTransportadoras(
  filtro: TransportadorasFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<TransportadoraResponse>> {
  const { data } = await cadastroBaseApi.get<PageResponse<TransportadoraResponse>>(BASE_URL, {
    // sort=pessoa.nome,asc — mesmo fix de DISTINCT incondicional já aplicado em
    // Cliente/Fornecedor/Colaborador (ver CLAUDE.md raiz, Fase 7).
    params: { ...filtro, page: pagina, size: tamanho, sort: 'pessoa.nome,asc' },
  })
  return data
}

export async function buscarTransportadora(id: number): Promise<TransportadoraResponse> {
  const { data } = await cadastroBaseApi.get<TransportadoraResponse>(`${BASE_URL}/${id}`)
  return data
}

export async function criarTransportadora(request: TransportadoraRequest): Promise<TransportadoraResponse> {
  const { data } = await cadastroBaseApi.post<TransportadoraResponse>(BASE_URL, request)
  return data
}

export async function atualizarTransportadora(
  id: number,
  request: TransportadoraRequest,
): Promise<TransportadoraResponse> {
  const { data } = await cadastroBaseApi.put<TransportadoraResponse>(`${BASE_URL}/${id}`, request)
  return data
}
