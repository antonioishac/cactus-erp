import { cadastroBaseApi, osApi } from '@shared/api/httpClients'
import type { PageResponse } from '@shared/api/pageResponse'
import type {
  ClienteResumo,
  ColaboradorResumo,
  OsAberturaEquipamentoRequest,
  OsAberturaEquipamentoResponse,
  OsAberturaRequest,
  OsAberturaResponse,
  OsEquipamentoRequest,
  OsEquipamentoResponse,
  OsEvolucaoRequest,
  OsEvolucaoResponse,
  OsProdutoServicoRequest,
  OsProdutoServicoResponse,
  OsStatusRequest,
  OsStatusResponse,
  ProdutoResumo,
} from './types'

// --- OsStatus ---

export async function listarOsStatus(pagina: number, tamanho = 10): Promise<PageResponse<OsStatusResponse>> {
  const { data } = await osApi.get<PageResponse<OsStatusResponse>>('/api/v1/os-status', {
    params: { page: pagina, size: tamanho },
  })
  return data
}

export async function listarTodosOsStatus(): Promise<OsStatusResponse[]> {
  const { data } = await osApi.get<PageResponse<OsStatusResponse>>('/api/v1/os-status', { params: { size: 200 } })
  return data.conteudo
}

export async function criarOsStatus(request: OsStatusRequest): Promise<OsStatusResponse> {
  const { data } = await osApi.post<OsStatusResponse>('/api/v1/os-status', request)
  return data
}

export async function atualizarOsStatus(id: number, request: OsStatusRequest): Promise<OsStatusResponse> {
  const { data } = await osApi.put<OsStatusResponse>(`/api/v1/os-status/${id}`, request)
  return data
}

export async function excluirOsStatus(id: number): Promise<void> {
  await osApi.delete(`/api/v1/os-status/${id}`)
}

// --- OsEquipamento (tipo de equipamento) ---

export async function listarOsEquipamentos(pagina: number, tamanho = 10): Promise<PageResponse<OsEquipamentoResponse>> {
  const { data } = await osApi.get<PageResponse<OsEquipamentoResponse>>('/api/v1/os-equipamentos', {
    params: { page: pagina, size: tamanho },
  })
  return data
}

export async function listarTodosOsEquipamentos(): Promise<OsEquipamentoResponse[]> {
  const { data } = await osApi.get<PageResponse<OsEquipamentoResponse>>('/api/v1/os-equipamentos', { params: { size: 200 } })
  return data.conteudo
}

export async function criarOsEquipamento(request: OsEquipamentoRequest): Promise<OsEquipamentoResponse> {
  const { data } = await osApi.post<OsEquipamentoResponse>('/api/v1/os-equipamentos', request)
  return data
}

export async function atualizarOsEquipamento(id: number, request: OsEquipamentoRequest): Promise<OsEquipamentoResponse> {
  const { data } = await osApi.put<OsEquipamentoResponse>(`/api/v1/os-equipamentos/${id}`, request)
  return data
}

export async function excluirOsEquipamento(id: number): Promise<void> {
  await osApi.delete(`/api/v1/os-equipamentos/${id}`)
}

// --- OsAbertura (agregado) ---

export async function listarOsAberturas(
  clienteId: number | undefined,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<OsAberturaResponse>> {
  const { data } = await osApi.get<PageResponse<OsAberturaResponse>>('/api/v1/os-aberturas', {
    params: { clienteId, page: pagina, size: tamanho },
  })
  return data
}

export async function buscarOsAbertura(id: number): Promise<OsAberturaResponse> {
  const { data } = await osApi.get<OsAberturaResponse>(`/api/v1/os-aberturas/${id}`)
  return data
}

export async function criarOsAbertura(request: OsAberturaRequest): Promise<OsAberturaResponse> {
  const { data } = await osApi.post<OsAberturaResponse>('/api/v1/os-aberturas', request)
  return data
}

export async function trocarStatusOsAbertura(id: number, osStatusId: number): Promise<OsAberturaResponse> {
  const { data } = await osApi.post<OsAberturaResponse>(`/api/v1/os-aberturas/${id}/status/${osStatusId}`)
  return data
}

export async function aprovarOrcamentoOsAbertura(id: number): Promise<OsAberturaResponse> {
  const { data } = await osApi.post<OsAberturaResponse>(`/api/v1/os-aberturas/${id}/aprovar-orcamento`)
  return data
}

export async function encerrarOsAbertura(id: number): Promise<OsAberturaResponse> {
  const { data } = await osApi.post<OsAberturaResponse>(`/api/v1/os-aberturas/${id}/encerrar`)
  return data
}

export async function adicionarEquipamentoOs(
  osAberturaId: number,
  request: OsAberturaEquipamentoRequest,
): Promise<OsAberturaEquipamentoResponse> {
  const { data } = await osApi.post<OsAberturaEquipamentoResponse>(`/api/v1/os-aberturas/${osAberturaId}/equipamentos`, request)
  return data
}

export async function removerEquipamentoOs(osAberturaId: number, vinculoId: number): Promise<void> {
  await osApi.delete(`/api/v1/os-aberturas/${osAberturaId}/equipamentos/${vinculoId}`)
}

export async function adicionarItemOs(osAberturaId: number, request: OsProdutoServicoRequest): Promise<OsProdutoServicoResponse> {
  const { data } = await osApi.post<OsProdutoServicoResponse>(`/api/v1/os-aberturas/${osAberturaId}/itens`, request)
  return data
}

export async function removerItemOs(osAberturaId: number, itemId: number): Promise<void> {
  await osApi.delete(`/api/v1/os-aberturas/${osAberturaId}/itens/${itemId}`)
}

export async function adicionarEvolucaoOs(osAberturaId: number, request: OsEvolucaoRequest): Promise<OsEvolucaoResponse> {
  const { data } = await osApi.post<OsEvolucaoResponse>(`/api/v1/os-aberturas/${osAberturaId}/evolucoes`, request)
  return data
}

// --- Lookups cross-serviço, só pra alimentar Selects/células de resumo ---

export async function buscarClientesParaSelecao(nome: string | undefined): Promise<ClienteResumo[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ClienteResumo>>('/api/v1/clientes', {
    params: { nome, size: 20 },
  })
  return data.conteudo
}

export async function buscarClienteResumo(id: number): Promise<ClienteResumo> {
  const { data } = await cadastroBaseApi.get<ClienteResumo>(`/api/v1/clientes/${id}`)
  return data
}

export async function buscarColaboradoresParaSelecao(nome: string | undefined): Promise<ColaboradorResumo[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ColaboradorResumo>>('/api/v1/colaboradores', {
    params: { nome, size: 20 },
  })
  return data.conteudo
}

export async function buscarColaboradorResumo(id: number): Promise<ColaboradorResumo> {
  const { data } = await cadastroBaseApi.get<ColaboradorResumo>(`/api/v1/colaboradores/${id}`)
  return data
}

export async function buscarProdutosParaSelecao(nome: string | undefined): Promise<ProdutoResumo[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ProdutoResumo>>('/api/v1/produtos', {
    params: { nome, size: 20 },
  })
  return data.conteudo
}
