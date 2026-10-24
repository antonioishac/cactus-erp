import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import type { AppError } from '@shared/api/problemDetail'
import {
  atualizarListaServico,
  buscarClienteResumo,
  buscarCte,
  buscarNotaFiscal,
  buscarNotaServico,
  buscarTransportadoraResumo,
  criarListaServico,
  emitirCte,
  emitirNotaFiscal,
  emitirNotaServico,
  excluirListaServico,
  gerarCteDeVenda,
  gerarNotaFiscalDeVenda,
  gerarNotaServicoDeOs,
  listarCtes,
  listarListasServico,
  buscarNotasPorPeriodo,
  listarNotasFiscais,
  listarNotasServico,
  listarTodasListasServico,
} from './api'
import type { GerarCteDeVendaRequest, GerarNotaServicoRequest, ListaServicoMunicipalRequest } from './types'

const QUERY_KEY_NOTAS_FISCAIS = ['fiscal-notas-fiscais'] as const
const queryKeyNotaFiscal = (id: number) => ['fiscal-notas-fiscais', id] as const
const QUERY_KEY_CTES = ['fiscal-ctes'] as const
const queryKeyCte = (id: number) => ['fiscal-ctes', id] as const
const QUERY_KEY_NOTAS_SERVICO = ['fiscal-notas-servico'] as const
const queryKeyNotaServico = (id: number) => ['fiscal-notas-servico', id] as const
const QUERY_KEY_LISTAS_SERVICO = ['fiscal-listas-servico'] as const

// --- NF-e ---

export function useNotasFiscais(pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_NOTAS_FISCAIS, pagina, tamanho],
    queryFn: () => listarNotasFiscais(pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useNotaFiscal(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyNotaFiscal(id ?? -1),
    queryFn: () => buscarNotaFiscal(id as number),
    enabled: id !== undefined,
  })
}

export function useGerarNotaFiscalDeVenda() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (vendaId: number) => gerarNotaFiscalDeVenda(vendaId),
    onSuccess: (criada) => {
      queryClient.setQueryData(queryKeyNotaFiscal(criada.id), criada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_NOTAS_FISCAIS })
    },
  })
}

export function useEmitirNotaFiscal(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => emitirNotaFiscal(id),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyNotaFiscal(id), atualizada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_NOTAS_FISCAIS })
    },
  })
}

// --- CT-e ---

export function useCtes(pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_CTES, pagina, tamanho],
    queryFn: () => listarCtes(pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useCte(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyCte(id ?? -1),
    queryFn: () => buscarCte(id as number),
    enabled: id !== undefined,
  })
}

export function useGerarCteDeVenda(vendaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: GerarCteDeVendaRequest) => gerarCteDeVenda(vendaId, request),
    onSuccess: (criado) => {
      queryClient.setQueryData(queryKeyCte(criado.id), criado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CTES })
    },
  })
}

export function useEmitirCte(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => emitirCte(id),
    onSuccess: (atualizado) => {
      queryClient.setQueryData(queryKeyCte(id), atualizado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CTES })
    },
  })
}

// --- NFS-e ---

export function useNotasServico(pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_NOTAS_SERVICO, pagina, tamanho],
    queryFn: () => listarNotasServico(pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useNotaServico(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyNotaServico(id ?? -1),
    queryFn: () => buscarNotaServico(id as number),
    enabled: id !== undefined,
  })
}

export function useGerarNotaServicoDeOs(osAberturaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: GerarNotaServicoRequest) => gerarNotaServicoDeOs(osAberturaId, request),
    onSuccess: (criada) => {
      queryClient.setQueryData(queryKeyNotaServico(criada.id), criada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_NOTAS_SERVICO })
    },
  })
}

export function useEmitirNotaServico(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => emitirNotaServico(id),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyNotaServico(id), atualizada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_NOTAS_SERVICO })
    },
  })
}

// --- Lista de Serviço Municipal ---

export function useListasServicoPaginado(pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_LISTAS_SERVICO, pagina, tamanho],
    queryFn: () => listarListasServico(pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useTodasListasServico() {
  return useQuery({
    queryKey: [...QUERY_KEY_LISTAS_SERVICO, 'todas'],
    queryFn: () => listarTodasListasServico(),
  })
}

export function useCriarListaServico() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ListaServicoMunicipalRequest) => criarListaServico(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_LISTAS_SERVICO }),
  })
}

export function useAtualizarListaServico() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: ListaServicoMunicipalRequest }) => atualizarListaServico(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_LISTAS_SERVICO }),
  })
}

export function useExcluirListaServico() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirListaServico(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_LISTAS_SERVICO }),
  })
}

// --- Lookups cross-serviço ---

export function useClienteResumo(id: number | undefined) {
  return useQuery({
    queryKey: ['cliente-resumo-fiscal', id],
    queryFn: () => buscarClienteResumo(id as number),
    enabled: id != null,
    staleTime: 5 * 60_000,
    retry: (failureCount, erro) => (erro as unknown as AppError).status !== 404 && failureCount < 1,
  })
}

export function useTransportadoraResumo(id: number | undefined) {
  return useQuery({
    queryKey: ['transportadora-resumo-fiscal', id],
    queryFn: () => buscarTransportadoraResumo(id as number),
    enabled: id != null,
    staleTime: 5 * 60_000,
    retry: (failureCount, erro) => (erro as unknown as AppError).status !== 404 && failureCount < 1,
  })
}

// --- Relatório: Notas Emitidas por Período ---

export function useNotasPorPeriodo(dataInicio: string | undefined, dataFim: string | undefined, empresaId: number | undefined) {
  return useQuery({
    queryKey: ['notas-periodo', dataInicio, dataFim, empresaId],
    queryFn: () => buscarNotasPorPeriodo(dataInicio as string, dataFim as string, empresaId),
    enabled: !!dataInicio && !!dataFim,
  })
}
