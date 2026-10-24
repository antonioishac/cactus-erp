import { contabilidadeApi } from '@shared/api/httpClients'
import { baixarArquivo, type FormatoRelatorio } from '@shared/api/download'
import type { PageResponse } from '@shared/api/pageResponse'
import type {
  BalanceteItemResponse,
  CentroCustoRequest,
  CentroCustoResponse,
  EstornarLancamentoRequest,
  LancamentoContabilRequest,
  LancamentoContabilResponse,
  LancamentosFiltro,
  PlanoContaRequest,
  PlanoContaResponse,
  RazaoItemResponse,
} from './types'

const BASE = '/api/v1'

// --- Plano de Contas ---

export async function listarPlanoContas(
  empresaId: number,
  pagina: number,
  tamanhoPagina: number,
): Promise<PageResponse<PlanoContaResponse>> {
  const { data } = await contabilidadeApi.get<PageResponse<PlanoContaResponse>>(`${BASE}/plano-contas`, {
    params: { empresaId, page: pagina, size: tamanhoPagina, sort: 'codigo,asc' },
  })
  return data
}

export async function listarTodasContas(empresaId: number): Promise<PlanoContaResponse[]> {
  const { data } = await contabilidadeApi.get<PlanoContaResponse[]>(`${BASE}/plano-contas/todas`, {
    params: { empresaId },
  })
  return data
}

export async function buscarPlanoConta(id: number): Promise<PlanoContaResponse> {
  const { data } = await contabilidadeApi.get<PlanoContaResponse>(`${BASE}/plano-contas/${id}`)
  return data
}

export async function criarPlanoConta(request: PlanoContaRequest): Promise<PlanoContaResponse> {
  const { data } = await contabilidadeApi.post<PlanoContaResponse>(`${BASE}/plano-contas`, request)
  return data
}

export async function atualizarPlanoConta(id: number, request: PlanoContaRequest): Promise<PlanoContaResponse> {
  const { data } = await contabilidadeApi.put<PlanoContaResponse>(`${BASE}/plano-contas/${id}`, request)
  return data
}

export async function excluirPlanoConta(id: number): Promise<void> {
  await contabilidadeApi.delete(`${BASE}/plano-contas/${id}`)
}

// --- Centro de Custo ---

export async function listarCentrosCusto(
  empresaId: number,
  pagina: number,
  tamanhoPagina: number,
): Promise<PageResponse<CentroCustoResponse>> {
  const { data } = await contabilidadeApi.get<PageResponse<CentroCustoResponse>>(`${BASE}/centros-custo`, {
    params: { empresaId, page: pagina, size: tamanhoPagina, sort: 'codigo,asc' },
  })
  return data
}

export async function listarTodosCentrosCusto(empresaId: number): Promise<CentroCustoResponse[]> {
  const { data } = await contabilidadeApi.get<CentroCustoResponse[]>(`${BASE}/centros-custo/todos`, {
    params: { empresaId },
  })
  return data
}

export async function criarCentroCusto(request: CentroCustoRequest): Promise<CentroCustoResponse> {
  const { data } = await contabilidadeApi.post<CentroCustoResponse>(`${BASE}/centros-custo`, request)
  return data
}

export async function atualizarCentroCusto(id: number, request: CentroCustoRequest): Promise<CentroCustoResponse> {
  const { data } = await contabilidadeApi.put<CentroCustoResponse>(`${BASE}/centros-custo/${id}`, request)
  return data
}

export async function excluirCentroCusto(id: number): Promise<void> {
  await contabilidadeApi.delete(`${BASE}/centros-custo/${id}`)
}

// --- Lançamento Contábil ---

export async function listarLancamentos(
  filtro: LancamentosFiltro,
  pagina: number,
  tamanhoPagina: number,
): Promise<PageResponse<LancamentoContabilResponse>> {
  const { data } = await contabilidadeApi.get<PageResponse<LancamentoContabilResponse>>(
    `${BASE}/lancamentos-contabeis`,
    { params: { ...filtro, page: pagina, size: tamanhoPagina, sort: 'dataLancamento,desc' } },
  )
  return data
}

export async function buscarLancamento(id: number): Promise<LancamentoContabilResponse> {
  const { data } = await contabilidadeApi.get<LancamentoContabilResponse>(`${BASE}/lancamentos-contabeis/${id}`)
  return data
}

export async function criarLancamento(request: LancamentoContabilRequest): Promise<LancamentoContabilResponse> {
  const { data } = await contabilidadeApi.post<LancamentoContabilResponse>(`${BASE}/lancamentos-contabeis`, request)
  return data
}

export async function estornarLancamento(
  id: number,
  request: EstornarLancamentoRequest,
): Promise<LancamentoContabilResponse> {
  const { data } = await contabilidadeApi.post<LancamentoContabilResponse>(
    `${BASE}/lancamentos-contabeis/${id}/estornar`,
    request,
  )
  return data
}

// --- Relatórios ---

export async function buscarBalancete(
  empresaId: number,
  dataInicio: string,
  dataFim: string,
): Promise<BalanceteItemResponse[]> {
  const { data } = await contabilidadeApi.get<BalanceteItemResponse[]>(`${BASE}/relatorios/balancete`, {
    params: { empresaId, dataInicio, dataFim },
  })
  return data
}

export async function exportarBalancete(
  empresaId: number,
  dataInicio: string,
  dataFim: string,
  formato: FormatoRelatorio,
): Promise<void> {
  await baixarArquivo(contabilidadeApi, `${BASE}/relatorios/balancete/${formato}`, `balancete.${formato}`, {
    empresaId,
    dataInicio,
    dataFim,
  })
}

export async function buscarRazao(
  empresaId: number,
  contaId: number,
  dataInicio: string,
  dataFim: string,
): Promise<RazaoItemResponse[]> {
  const { data } = await contabilidadeApi.get<RazaoItemResponse[]>(`${BASE}/relatorios/razao`, {
    params: { empresaId, contaId, dataInicio, dataFim },
  })
  return data
}

export async function exportarRazao(
  empresaId: number,
  contaId: number,
  dataInicio: string,
  dataFim: string,
  formato: FormatoRelatorio,
): Promise<void> {
  await baixarArquivo(contabilidadeApi, `${BASE}/relatorios/razao/${formato}`, `razao.${formato}`, {
    empresaId,
    contaId,
    dataInicio,
    dataFim,
  })
}
