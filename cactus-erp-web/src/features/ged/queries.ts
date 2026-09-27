import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  adicionarVersaoDocumento,
  atualizarPasta,
  atualizarTipoDocumento,
  baixarUltimaVersao,
  baixarVersao,
  buscarDocumento,
  criarDocumento,
  criarPasta,
  criarTipoDocumento,
  excluirDocumento,
  excluirPasta,
  listarDocumentos,
  listarPastas,
  listarTiposDocumento,
  listarTodasPastas,
  listarTodosTiposDocumento,
  listarVersoesDocumento,
} from './api'
import type { DocumentoRequest, DocumentosFiltro, PastaDocumentoRequest, TipoDocumentoRequest } from './types'

const QUERY_KEY_DOCUMENTOS = ['documentos'] as const
const queryKeyDocumento = (id: number) => ['documentos', id] as const
const QUERY_KEY_PASTAS = ['pastas'] as const
const QUERY_KEY_TIPOS_DOCUMENTO = ['tipos-documento'] as const

// --- Tipo de Documento ---

export function useTiposDocumento(pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_TIPOS_DOCUMENTO, pagina, tamanho],
    queryFn: () => listarTiposDocumento(pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useTodosTiposDocumento() {
  return useQuery({ queryKey: ['tipos-documento-todos'], queryFn: listarTodosTiposDocumento, staleTime: 5 * 60_000 })
}

export function useCriarTipoDocumento() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: TipoDocumentoRequest) => criarTipoDocumento(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_TIPOS_DOCUMENTO })
      queryClient.invalidateQueries({ queryKey: ['tipos-documento-todos'] })
    },
  })
}

export function useAtualizarTipoDocumento() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: TipoDocumentoRequest }) => atualizarTipoDocumento(id, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_TIPOS_DOCUMENTO })
      queryClient.invalidateQueries({ queryKey: ['tipos-documento-todos'] })
    },
  })
}

// --- Pasta de Documento ---

export function usePastas(pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_PASTAS, pagina, tamanho],
    queryFn: () => listarPastas(pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useTodasPastas() {
  return useQuery({ queryKey: ['pastas-todas'], queryFn: listarTodasPastas, staleTime: 2 * 60_000 })
}

export function useCriarPasta() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: PastaDocumentoRequest) => criarPasta(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_PASTAS })
      queryClient.invalidateQueries({ queryKey: ['pastas-todas'] })
    },
  })
}

export function useAtualizarPasta() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: PastaDocumentoRequest }) => atualizarPasta(id, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_PASTAS })
      queryClient.invalidateQueries({ queryKey: ['pastas-todas'] })
    },
  })
}

export function useExcluirPasta() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirPasta(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_PASTAS })
      queryClient.invalidateQueries({ queryKey: ['pastas-todas'] })
    },
  })
}

// --- Documento ---

export function useDocumentos(filtro: DocumentosFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_DOCUMENTOS, filtro, pagina, tamanho],
    queryFn: () => listarDocumentos(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useDocumento(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyDocumento(id ?? -1),
    queryFn: () => buscarDocumento(id as number),
    enabled: id !== undefined,
  })
}

export function useVersoesDocumento(id: number | undefined) {
  return useQuery({
    queryKey: ['versoes-documento', id ?? -1],
    queryFn: () => listarVersoesDocumento(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarDocumento() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ metadados, arquivo }: { metadados: DocumentoRequest; arquivo: File }) => criarDocumento(metadados, arquivo),
    onSuccess: (criado) => {
      queryClient.setQueryData(queryKeyDocumento(criado.id), criado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_DOCUMENTOS })
    },
  })
}

export function useAdicionarVersaoDocumento(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ arquivo, observacao }: { arquivo: File; observacao?: string }) =>
      adicionarVersaoDocumento(id, arquivo, observacao),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeyDocumento(id) })
      queryClient.invalidateQueries({ queryKey: ['versoes-documento', id] })
    },
  })
}

export function useBaixarUltimaVersao() {
  return useMutation({
    mutationFn: ({ id, nomeSugerido }: { id: number; nomeSugerido: string }) => baixarUltimaVersao(id, nomeSugerido),
  })
}

export function useBaixarVersao() {
  return useMutation({
    mutationFn: ({ id, versaoId, nomeSugerido }: { id: number; versaoId: number; nomeSugerido: string }) =>
      baixarVersao(id, versaoId, nomeSugerido),
  })
}

export function useExcluirDocumento() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirDocumento(id),
    onSuccess: (_dados, id) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_DOCUMENTOS })
      queryClient.invalidateQueries({ queryKey: queryKeyDocumento(id) })
    },
  })
}
