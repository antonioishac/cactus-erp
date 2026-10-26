import { cadastroBaseApi } from '@shared/api/httpClients'
import type { PageResponse } from '@shared/api/pageResponse'
import type {
  CfopResponse,
  ClassificacaoTributariaIbsCbsResponse,
  ConfiguracaoTributariaRequest,
  ConfiguracaoTributariaResponse,
  GrupoTributarioRequest,
  GrupoTributarioResponse,
  OperacaoFiscalRequest,
  OperacaoFiscalResponse,
} from './types'

// --- IBS/CBS (catálogo oficial, só leitura) ---

/** Catálogo inteiro (173 códigos na tabela oficial de 2026-10): pequeno o bastante pra carregar de uma vez e filtrar na tela. */
export async function listarClassificacoesIbsCbs(): Promise<ClassificacaoTributariaIbsCbsResponse[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ClassificacaoTributariaIbsCbsResponse>>('/api/v1/tributacao/ibs-cbs/classificacoes', {
    params: { page: 0, size: 500 },
  })
  return data.conteudo
}

// --- Grupo Tributário ---

export async function listarGruposTributarios(pagina: number, tamanho: number, nome?: string): Promise<PageResponse<GrupoTributarioResponse>> {
  const { data } = await cadastroBaseApi.get<PageResponse<GrupoTributarioResponse>>('/api/v1/tributacao/grupos-tributarios', {
    params: { page: pagina, size: tamanho, nome },
  })
  return data
}

export async function criarGrupoTributario(request: GrupoTributarioRequest): Promise<GrupoTributarioResponse> {
  const { data } = await cadastroBaseApi.post<GrupoTributarioResponse>('/api/v1/tributacao/grupos-tributarios', request)
  return data
}

export async function atualizarGrupoTributario(id: number, request: GrupoTributarioRequest): Promise<GrupoTributarioResponse> {
  const { data } = await cadastroBaseApi.put<GrupoTributarioResponse>(`/api/v1/tributacao/grupos-tributarios/${id}`, request)
  return data
}

export async function excluirGrupoTributario(id: number): Promise<void> {
  await cadastroBaseApi.delete(`/api/v1/tributacao/grupos-tributarios/${id}`)
}

export async function listarTodosGruposTributarios(): Promise<GrupoTributarioResponse[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<GrupoTributarioResponse>>('/api/v1/tributacao/grupos-tributarios', {
    params: { size: 200 },
  })
  return data.conteudo
}

// --- Operação Fiscal ---

export async function listarOperacoesFiscais(pagina: number, tamanho: number, nome?: string): Promise<PageResponse<OperacaoFiscalResponse>> {
  const { data } = await cadastroBaseApi.get<PageResponse<OperacaoFiscalResponse>>('/api/v1/tributacao/operacoes-fiscais', {
    params: { page: pagina, size: tamanho, nome },
  })
  return data
}

export async function criarOperacaoFiscal(request: OperacaoFiscalRequest): Promise<OperacaoFiscalResponse> {
  const { data } = await cadastroBaseApi.post<OperacaoFiscalResponse>('/api/v1/tributacao/operacoes-fiscais', request)
  return data
}

export async function atualizarOperacaoFiscal(id: number, request: OperacaoFiscalRequest): Promise<OperacaoFiscalResponse> {
  const { data } = await cadastroBaseApi.put<OperacaoFiscalResponse>(`/api/v1/tributacao/operacoes-fiscais/${id}`, request)
  return data
}

export async function excluirOperacaoFiscal(id: number): Promise<void> {
  await cadastroBaseApi.delete(`/api/v1/tributacao/operacoes-fiscais/${id}`)
}

export async function listarTodasOperacoesFiscais(): Promise<OperacaoFiscalResponse[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<OperacaoFiscalResponse>>('/api/v1/tributacao/operacoes-fiscais', {
    params: { size: 200 },
  })
  return data.conteudo
}

// --- CFOP (busca assíncrona — catálogo oficial, grande demais pra carregar inteiro) ---

export async function buscarCfop(descricao: string | undefined): Promise<CfopResponse[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<CfopResponse>>('/api/v1/cfop', {
    params: { descricao, size: 20 },
  })
  return data.conteudo
}

// --- Configuração Tributária (a célula Grupo × Operação) ---

export async function listarConfiguracoesTributarias(
  pagina: number,
  tamanho: number,
  grupoTributarioId?: number,
  operacaoFiscalId?: number,
): Promise<PageResponse<ConfiguracaoTributariaResponse>> {
  const { data } = await cadastroBaseApi.get<PageResponse<ConfiguracaoTributariaResponse>>('/api/v1/tributacao/configuracoes', {
    params: { page: pagina, size: tamanho, grupoTributarioId, operacaoFiscalId },
  })
  return data
}

export async function buscarConfiguracaoTributaria(id: number): Promise<ConfiguracaoTributariaResponse> {
  const { data } = await cadastroBaseApi.get<ConfiguracaoTributariaResponse>(`/api/v1/tributacao/configuracoes/${id}`)
  return data
}

export async function criarConfiguracaoTributaria(request: ConfiguracaoTributariaRequest): Promise<ConfiguracaoTributariaResponse> {
  const { data } = await cadastroBaseApi.post<ConfiguracaoTributariaResponse>('/api/v1/tributacao/configuracoes', request)
  return data
}

export async function atualizarConfiguracaoTributaria(id: number, request: ConfiguracaoTributariaRequest): Promise<ConfiguracaoTributariaResponse> {
  const { data } = await cadastroBaseApi.put<ConfiguracaoTributariaResponse>(`/api/v1/tributacao/configuracoes/${id}`, request)
  return data
}

export async function excluirConfiguracaoTributaria(id: number): Promise<void> {
  await cadastroBaseApi.delete(`/api/v1/tributacao/configuracoes/${id}`)
}
