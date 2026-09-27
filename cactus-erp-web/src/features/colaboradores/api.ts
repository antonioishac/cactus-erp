import { cadastroBaseApi } from '@shared/api/httpClients'
import type { PageResponse } from '@shared/api/pageResponse'
import type {
  CargoRequest,
  CargoResponse,
  ColaboradorRequest,
  ColaboradorResponse,
  ColaboradoresFiltro,
  SetorRequest,
  SetorResponse,
} from './types'

const BASE_URL = '/api/v1/colaboradores'

export async function listarColaboradores(
  filtro: ColaboradoresFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<ColaboradorResponse>> {
  const { data } = await cadastroBaseApi.get<PageResponse<ColaboradorResponse>>(BASE_URL, {
    params: { ...filtro, page: pagina, size: tamanho, sort: 'pessoa.nome,asc' },
  })
  return data
}

export async function buscarColaborador(id: number): Promise<ColaboradorResponse> {
  const { data } = await cadastroBaseApi.get<ColaboradorResponse>(`${BASE_URL}/${id}`)
  return data
}

export async function criarColaborador(request: ColaboradorRequest): Promise<ColaboradorResponse> {
  const { data } = await cadastroBaseApi.post<ColaboradorResponse>(BASE_URL, request)
  return data
}

export async function atualizarColaborador(
  id: number,
  request: ColaboradorRequest,
): Promise<ColaboradorResponse> {
  const { data } = await cadastroBaseApi.put<ColaboradorResponse>(`${BASE_URL}/${id}`, request)
  return data
}

/** Sem paginação de propósito, pra popular os `Select` de RH — volume esperado
 *  é "dezenas de cargos/setores por empresa", cabe numa página só. */
export async function listarCargos(): Promise<CargoResponse[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<CargoResponse>>('/api/v1/cargos', {
    params: { size: 200 },
  })
  return data.conteudo
}

export async function listarSetores(): Promise<SetorResponse[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<SetorResponse>>('/api/v1/setores', {
    params: { size: 200 },
  })
  return data.conteudo
}

export async function criarCargo(request: CargoRequest): Promise<CargoResponse> {
  const { data } = await cadastroBaseApi.post<CargoResponse>('/api/v1/cargos', request)
  return data
}

export async function atualizarCargo(id: number, request: CargoRequest): Promise<CargoResponse> {
  const { data } = await cadastroBaseApi.put<CargoResponse>(`/api/v1/cargos/${id}`, request)
  return data
}

export async function criarSetor(request: SetorRequest): Promise<SetorResponse> {
  const { data } = await cadastroBaseApi.post<SetorResponse>('/api/v1/setores', request)
  return data
}

export async function atualizarSetor(id: number, request: SetorRequest): Promise<SetorResponse> {
  const { data } = await cadastroBaseApi.put<SetorResponse>(`/api/v1/setores/${id}`, request)
  return data
}
