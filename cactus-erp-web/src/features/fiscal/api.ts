import { cadastroBaseApi, fiscalApi } from '@shared/api/httpClients'
import { baixarArquivo, type FormatoRelatorio } from '@shared/api/download'
import type { PageResponse } from '@shared/api/pageResponse'
import type {
  ClienteResumo,
  CteCabecalhoResponse,
  GerarCteDeVendaRequest,
  GerarNotaServicoRequest,
  ListaServicoMunicipalRequest,
  ListaServicoMunicipalResponse,
  NotaFiscalEletronicaResponse,
  NotaFiscalServicoResponse,
  SituacaoNotaFiscal,
  TransportadoraResumo,
} from './types'

// --- NF-e ---

export async function listarNotasFiscais(pagina: number, tamanho = 10): Promise<PageResponse<NotaFiscalEletronicaResponse>> {
  const { data } = await fiscalApi.get<PageResponse<NotaFiscalEletronicaResponse>>('/api/v1/notas-fiscais', {
    params: { page: pagina, size: tamanho },
  })
  return data
}

export async function buscarNotaFiscal(id: number): Promise<NotaFiscalEletronicaResponse> {
  const { data } = await fiscalApi.get<NotaFiscalEletronicaResponse>(`/api/v1/notas-fiscais/${id}`)
  return data
}

export async function gerarNotaFiscalDeVenda(vendaId: number): Promise<NotaFiscalEletronicaResponse> {
  const { data } = await fiscalApi.post<NotaFiscalEletronicaResponse>(`/api/v1/notas-fiscais/de-venda/${vendaId}`)
  return data
}

export async function emitirNotaFiscal(id: number): Promise<NotaFiscalEletronicaResponse> {
  const { data } = await fiscalApi.post<NotaFiscalEletronicaResponse>(`/api/v1/notas-fiscais/${id}/emitir`)
  return data
}

// --- CT-e ---

export async function listarCtes(pagina: number, tamanho = 10): Promise<PageResponse<CteCabecalhoResponse>> {
  const { data } = await fiscalApi.get<PageResponse<CteCabecalhoResponse>>('/api/v1/ctes', {
    params: { page: pagina, size: tamanho },
  })
  return data
}

export async function buscarCte(id: number): Promise<CteCabecalhoResponse> {
  const { data } = await fiscalApi.get<CteCabecalhoResponse>(`/api/v1/ctes/${id}`)
  return data
}

export async function gerarCteDeVenda(vendaId: number, request: GerarCteDeVendaRequest): Promise<CteCabecalhoResponse> {
  const { data } = await fiscalApi.post<CteCabecalhoResponse>(`/api/v1/ctes/de-venda/${vendaId}`, request)
  return data
}

export async function emitirCte(id: number): Promise<CteCabecalhoResponse> {
  const { data } = await fiscalApi.post<CteCabecalhoResponse>(`/api/v1/ctes/${id}/emitir`)
  return data
}

// --- NFS-e ---

export async function listarNotasServico(pagina: number, tamanho = 10): Promise<PageResponse<NotaFiscalServicoResponse>> {
  const { data } = await fiscalApi.get<PageResponse<NotaFiscalServicoResponse>>('/api/v1/notas-servico', {
    params: { page: pagina, size: tamanho },
  })
  return data
}

export async function buscarNotaServico(id: number): Promise<NotaFiscalServicoResponse> {
  const { data } = await fiscalApi.get<NotaFiscalServicoResponse>(`/api/v1/notas-servico/${id}`)
  return data
}

export async function gerarNotaServicoDeOs(osAberturaId: number, request: GerarNotaServicoRequest): Promise<NotaFiscalServicoResponse> {
  const { data } = await fiscalApi.post<NotaFiscalServicoResponse>(`/api/v1/notas-servico/de-os/${osAberturaId}`, request)
  return data
}

export async function emitirNotaServico(id: number): Promise<NotaFiscalServicoResponse> {
  const { data } = await fiscalApi.post<NotaFiscalServicoResponse>(`/api/v1/notas-servico/${id}/emitir`)
  return data
}

// --- Lista de Serviço Municipal (catálogo) ---

export async function listarListasServico(pagina: number, tamanho = 10): Promise<PageResponse<ListaServicoMunicipalResponse>> {
  const { data } = await fiscalApi.get<PageResponse<ListaServicoMunicipalResponse>>('/api/v1/listas-servico-municipal', {
    params: { page: pagina, size: tamanho },
  })
  return data
}

export async function listarTodasListasServico(): Promise<ListaServicoMunicipalResponse[]> {
  const { data } = await fiscalApi.get<PageResponse<ListaServicoMunicipalResponse>>('/api/v1/listas-servico-municipal', {
    params: { size: 200 },
  })
  return data.conteudo
}

export async function criarListaServico(request: ListaServicoMunicipalRequest): Promise<ListaServicoMunicipalResponse> {
  const { data } = await fiscalApi.post<ListaServicoMunicipalResponse>('/api/v1/listas-servico-municipal', request)
  return data
}

export async function atualizarListaServico(id: number, request: ListaServicoMunicipalRequest): Promise<ListaServicoMunicipalResponse> {
  const { data } = await fiscalApi.put<ListaServicoMunicipalResponse>(`/api/v1/listas-servico-municipal/${id}`, request)
  return data
}

export async function excluirListaServico(id: number): Promise<void> {
  await fiscalApi.delete(`/api/v1/listas-servico-municipal/${id}`)
}

// --- Lookups cross-serviço, só pra alimentar resumo/exibição ---

export async function buscarClienteResumo(id: number): Promise<ClienteResumo> {
  const { data } = await cadastroBaseApi.get<ClienteResumo>(`/api/v1/clientes/${id}`)
  return data
}

export async function buscarTransportadoraResumo(id: number): Promise<TransportadoraResumo> {
  const { data } = await cadastroBaseApi.get<TransportadoraResumo>(`/api/v1/transportadoras/${id}`)
  return data
}

// --- Relatório: Notas Emitidas por Período (unificado NF-e/NFC-e/NFS-e/CT-e) ---

export interface NotaFiscalPeriodoItem {
  tipoDocumento: 'NFE' | 'NFCE' | 'NFSE' | 'CTE'
  numero: string
  serie?: string
  dataEmissao?: string
  situacao: SituacaoNotaFiscal
  valorTotal: number
}

export async function buscarNotasPorPeriodo(
  dataInicio: string,
  dataFim: string,
  empresaId: number | undefined,
): Promise<NotaFiscalPeriodoItem[]> {
  const { data } = await fiscalApi.get<NotaFiscalPeriodoItem[]>('/api/v1/relatorios/notas-periodo', {
    params: { dataInicio, dataFim, empresaId },
  })
  return data
}

export async function exportarNotasPorPeriodo(
  dataInicio: string,
  dataFim: string,
  empresaId: number | undefined,
  formato: FormatoRelatorio,
): Promise<void> {
  await baixarArquivo(fiscalApi, `/api/v1/relatorios/notas-periodo/${formato}`, `notas-periodo.${formato}`, {
    dataInicio,
    dataFim,
    empresaId,
  })
}
