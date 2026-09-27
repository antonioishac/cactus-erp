import { cadastroBaseApi } from '@shared/api/httpClients'
import type { PageResponse } from '@shared/api/pageResponse'
import type { AppError } from '@shared/api/problemDetail'
import type {
  ConfiguracaoOperacionalLojaRequest,
  ConfiguracaoOperacionalLojaResponse,
  ContadorRequest,
  ContadorResponse,
  EmpresaConfiguracaoFiscalRequest,
  EmpresaConfiguracaoFiscalResponse,
  EmpresaRegimeTributarioRequest,
  EmpresaRegimeTributarioResponse,
  EmpresaRequest,
  EmpresaResponse,
  EmpresasFiltro,
} from './types'

const BASE_URL = '/api/v1/empresas'

export async function listarEmpresas(
  filtro: EmpresasFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<EmpresaResponse>> {
  const { data } = await cadastroBaseApi.get<PageResponse<EmpresaResponse>>(BASE_URL, {
    params: { nome: filtro.nome, page: pagina, size: tamanho },
  })
  return data
}

export async function buscarEmpresa(id: number): Promise<EmpresaResponse> {
  const { data } = await cadastroBaseApi.get<EmpresaResponse>(`${BASE_URL}/${id}`)
  return data
}

export async function criarEmpresa(request: EmpresaRequest): Promise<EmpresaResponse> {
  const { data } = await cadastroBaseApi.post<EmpresaResponse>(BASE_URL, request)
  return data
}

export async function atualizarEmpresa(id: number, request: EmpresaRequest): Promise<EmpresaResponse> {
  const { data } = await cadastroBaseApi.put<EmpresaResponse>(`${BASE_URL}/${id}`, request)
  return data
}

// --- Regime tributário (histórico, append-only) ---

export async function listarRegimeTributario(empresaId: number): Promise<EmpresaRegimeTributarioResponse[]> {
  const { data } = await cadastroBaseApi.get<EmpresaRegimeTributarioResponse[]>(
    `${BASE_URL}/${empresaId}/regime-tributario`,
  )
  return data
}

export async function registrarRegimeTributario(
  empresaId: number,
  request: EmpresaRegimeTributarioRequest,
): Promise<EmpresaRegimeTributarioResponse> {
  const { data } = await cadastroBaseApi.post<EmpresaRegimeTributarioResponse>(
    `${BASE_URL}/${empresaId}/regime-tributario`,
    request,
  )
  return data
}

// --- Configuração operacional (1:1, upsert) ---

export async function buscarConfiguracaoOperacional(
  empresaId: number,
): Promise<ConfiguracaoOperacionalLojaResponse | null> {
  try {
    const { data } = await cadastroBaseApi.get<ConfiguracaoOperacionalLojaResponse>(
      `${BASE_URL}/${empresaId}/configuracao-operacional`,
    )
    return data
  } catch (erro) {
    // 404 = empresa ainda não tem configuração salva — estado válido, não um erro real.
    if ((erro as AppError).status === 404) return null
    throw erro
  }
}

export async function salvarConfiguracaoOperacional(
  empresaId: number,
  request: ConfiguracaoOperacionalLojaRequest,
): Promise<ConfiguracaoOperacionalLojaResponse> {
  const { data } = await cadastroBaseApi.put<ConfiguracaoOperacionalLojaResponse>(
    `${BASE_URL}/${empresaId}/configuracao-operacional`,
    request,
  )
  return data
}

// --- Configuração fiscal (1:1, upsert) ---

export async function buscarConfiguracaoFiscal(
  empresaId: number,
): Promise<EmpresaConfiguracaoFiscalResponse | null> {
  try {
    const { data } = await cadastroBaseApi.get<EmpresaConfiguracaoFiscalResponse>(
      `${BASE_URL}/${empresaId}/configuracao-fiscal`,
    )
    return data
  } catch (erro) {
    if ((erro as AppError).status === 404) return null
    throw erro
  }
}

export async function salvarConfiguracaoFiscal(
  empresaId: number,
  request: EmpresaConfiguracaoFiscalRequest,
): Promise<EmpresaConfiguracaoFiscalResponse> {
  const { data } = await cadastroBaseApi.put<EmpresaConfiguracaoFiscalResponse>(
    `${BASE_URL}/${empresaId}/configuracao-fiscal`,
    request,
  )
  return data
}

// --- Contador (lookup) ---

export async function listarContadores(): Promise<ContadorResponse[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ContadorResponse>>('/api/v1/contadores', {
    params: { size: 200 },
  })
  return data.conteudo
}

export async function criarContador(request: ContadorRequest): Promise<ContadorResponse> {
  const { data } = await cadastroBaseApi.post<ContadorResponse>('/api/v1/contadores', request)
  return data
}

export async function atualizarContador(id: number, request: ContadorRequest): Promise<ContadorResponse> {
  const { data } = await cadastroBaseApi.put<ContadorResponse>(`/api/v1/contadores/${id}`, request)
  return data
}
