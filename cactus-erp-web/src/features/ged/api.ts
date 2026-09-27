import { gedApi } from '@shared/api/httpClients'
import type { PageResponse } from '@shared/api/pageResponse'
import type {
  DocumentoRequest,
  DocumentoResponse,
  DocumentosFiltro,
  DocumentoVersaoResponse,
  PastaDocumentoRequest,
  PastaDocumentoResponse,
  TipoDocumentoRequest,
  TipoDocumentoResponse,
} from './types'

// --- Tipo de Documento ---

export async function listarTiposDocumento(pagina: number, tamanho = 10): Promise<PageResponse<TipoDocumentoResponse>> {
  const { data } = await gedApi.get<PageResponse<TipoDocumentoResponse>>('/api/v1/tipos-documento', {
    params: { page: pagina, size: tamanho },
  })
  return data
}

export async function listarTodosTiposDocumento(): Promise<TipoDocumentoResponse[]> {
  const { data } = await gedApi.get<PageResponse<TipoDocumentoResponse>>('/api/v1/tipos-documento', { params: { size: 200 } })
  return data.conteudo
}

export async function criarTipoDocumento(request: TipoDocumentoRequest): Promise<TipoDocumentoResponse> {
  const { data } = await gedApi.post<TipoDocumentoResponse>('/api/v1/tipos-documento', request)
  return data
}

export async function atualizarTipoDocumento(id: number, request: TipoDocumentoRequest): Promise<TipoDocumentoResponse> {
  const { data } = await gedApi.put<TipoDocumentoResponse>(`/api/v1/tipos-documento/${id}`, request)
  return data
}

// --- Pasta de Documento ---

export async function listarPastas(pagina: number, tamanho = 10): Promise<PageResponse<PastaDocumentoResponse>> {
  const { data } = await gedApi.get<PageResponse<PastaDocumentoResponse>>('/api/v1/pastas', {
    params: { page: pagina, size: tamanho },
  })
  return data
}

export async function listarTodasPastas(): Promise<PastaDocumentoResponse[]> {
  const { data } = await gedApi.get<PageResponse<PastaDocumentoResponse>>('/api/v1/pastas', { params: { size: 200 } })
  return data.conteudo
}

export async function criarPasta(request: PastaDocumentoRequest): Promise<PastaDocumentoResponse> {
  const { data } = await gedApi.post<PastaDocumentoResponse>('/api/v1/pastas', request)
  return data
}

export async function atualizarPasta(id: number, request: PastaDocumentoRequest): Promise<PastaDocumentoResponse> {
  const { data } = await gedApi.put<PastaDocumentoResponse>(`/api/v1/pastas/${id}`, request)
  return data
}

export async function excluirPasta(id: number): Promise<void> {
  await gedApi.delete(`/api/v1/pastas/${id}`)
}

// --- Documento ---

export async function listarDocumentos(
  filtro: DocumentosFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<DocumentoResponse>> {
  const { data } = await gedApi.get<PageResponse<DocumentoResponse>>('/api/v1/documentos', {
    params: { entidadeTipo: filtro.entidadeTipo, entidadeId: filtro.entidadeId, page: pagina, size: tamanho },
  })
  return data
}

export async function buscarDocumento(id: number): Promise<DocumentoResponse> {
  const { data } = await gedApi.get<DocumentoResponse>(`/api/v1/documentos/${id}`)
  return data
}

export async function listarVersoesDocumento(id: number): Promise<DocumentoVersaoResponse[]> {
  const { data } = await gedApi.get<DocumentoVersaoResponse[]>(`/api/v1/documentos/${id}/versoes`)
  return data
}

/** multipart/form-data: a parte `metadados` precisa ir como Blob `application/json` — enviar como objeto puro faz o axios tratar como campo de texto simples, e o `@RequestPart` do backend exige JSON de verdade. */
export async function criarDocumento(metadados: DocumentoRequest, arquivo: File): Promise<DocumentoResponse> {
  const form = new FormData()
  form.append('metadados', new Blob([JSON.stringify(metadados)], { type: 'application/json' }))
  form.append('arquivo', arquivo)
  const { data } = await gedApi.post<DocumentoResponse>('/api/v1/documentos', form)
  return data
}

export async function adicionarVersaoDocumento(
  id: number,
  arquivo: File,
  observacao: string | undefined,
): Promise<DocumentoVersaoResponse> {
  const form = new FormData()
  form.append('arquivo', arquivo)
  const { data } = await gedApi.post<DocumentoVersaoResponse>(`/api/v1/documentos/${id}/versoes`, form, {
    params: { observacao },
  })
  return data
}

async function baixarEDisparar(url: string, nomeSugerido: string): Promise<void> {
  const { data, headers } = await gedApi.get<Blob>(url, { responseType: 'blob' })
  const contentDisposition: string | undefined = headers['content-disposition']
  const match = contentDisposition?.match(/filename\*?=(?:UTF-8'')?"?([^";]+)"?/i)
  const nomeArquivo = match?.[1] ? decodeURIComponent(match[1]) : nomeSugerido

  const blobUrl = window.URL.createObjectURL(data)
  const link = document.createElement('a')
  link.href = blobUrl
  link.download = nomeArquivo
  document.body.appendChild(link)
  link.click()
  link.remove()
  window.URL.revokeObjectURL(blobUrl)
}

export async function baixarUltimaVersao(id: number, nomeSugerido: string): Promise<void> {
  await baixarEDisparar(`/api/v1/documentos/${id}/conteudo`, nomeSugerido)
}

export async function baixarVersao(id: number, versaoId: number, nomeSugerido: string): Promise<void> {
  await baixarEDisparar(`/api/v1/documentos/${id}/versoes/${versaoId}/conteudo`, nomeSugerido)
}

export async function excluirDocumento(id: number): Promise<void> {
  await gedApi.delete(`/api/v1/documentos/${id}`)
}
