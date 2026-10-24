import { segurancaApi, sincronizacaoApi } from '@shared/api/httpClients'
import type { PageResponse } from '@shared/api/pageResponse'
import type {
  ClienteLojaRequest,
  ClienteLojaResponse,
  ClienteLojaResumoResponse,
  LoteSincronizacaoResumoResponse,
  LotesFiltro,
  RegistroSincronizacaoResponse,
  RegistrosFiltro,
} from './types'

const BASE = '/api/v1/sync'

// --- Auditoria: Lotes ---

export async function listarLotes(
  filtro: LotesFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<LoteSincronizacaoResumoResponse>> {
  const { data } = await sincronizacaoApi.get<PageResponse<LoteSincronizacaoResumoResponse>>(`${BASE}/lotes`, {
    params: { ...filtro, page: pagina, size: tamanho, sort: 'dataCriacao,desc' },
  })
  return data
}

// --- Auditoria: Registros ---

export async function listarRegistros(
  filtro: RegistrosFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<RegistroSincronizacaoResponse>> {
  const { data } = await sincronizacaoApi.get<PageResponse<RegistroSincronizacaoResponse>>(`${BASE}/registros`, {
    params: { ...filtro, page: pagina, size: tamanho, sort: 'dataCriacao,desc' },
  })
  return data
}

export async function reprocessarRegistro(id: number): Promise<RegistroSincronizacaoResponse> {
  const { data } = await sincronizacaoApi.post<RegistroSincronizacaoResponse>(`${BASE}/registros/${id}/reprocessar`)
  return data
}

// --- Clientes de Loja (seguranca-service, não sincronizacao-service) ---

const CLIENTES_LOJA_BASE = '/api/v1/sincronizacao/clientes-loja'

export async function listarClientesLoja(): Promise<ClienteLojaResumoResponse[]> {
  const { data } = await segurancaApi.get<ClienteLojaResumoResponse[]>(CLIENTES_LOJA_BASE)
  return data
}

export async function criarClienteLoja(request: ClienteLojaRequest): Promise<ClienteLojaResponse> {
  const { data } = await segurancaApi.post<ClienteLojaResponse>(CLIENTES_LOJA_BASE, request)
  return data
}
