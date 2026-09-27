import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import type { AppError } from '@shared/api/problemDetail'
import {
  adicionarEquipamentoOs,
  adicionarEvolucaoOs,
  adicionarItemOs,
  aprovarOrcamentoOsAbertura,
  atualizarOsEquipamento,
  atualizarOsStatus,
  buscarClienteResumo,
  buscarClientesParaSelecao,
  buscarColaboradorResumo,
  buscarColaboradoresParaSelecao,
  buscarOsAbertura,
  buscarProdutosParaSelecao,
  criarOsAbertura,
  criarOsEquipamento,
  criarOsStatus,
  encerrarOsAbertura,
  excluirOsEquipamento,
  excluirOsStatus,
  listarOsAberturas,
  listarOsEquipamentos,
  listarOsStatus,
  listarTodosOsEquipamentos,
  listarTodosOsStatus,
  removerEquipamentoOs,
  removerItemOs,
  trocarStatusOsAbertura,
} from './api'
import type {
  OsAberturaEquipamentoRequest,
  OsAberturaRequest,
  OsEquipamentoRequest,
  OsEvolucaoRequest,
  OsProdutoServicoRequest,
  OsStatusRequest,
} from './types'

const QUERY_KEY_OS_STATUS = ['os-status'] as const
const QUERY_KEY_OS_EQUIPAMENTOS = ['os-equipamentos'] as const
const QUERY_KEY_OS_ABERTURAS = ['os-aberturas'] as const
const queryKeyOsAbertura = (id: number) => ['os-aberturas', id] as const

// --- OsStatus ---

export function useOsStatusPaginado(pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_OS_STATUS, pagina, tamanho],
    queryFn: () => listarOsStatus(pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useTodosOsStatus() {
  return useQuery({
    queryKey: [...QUERY_KEY_OS_STATUS, 'todos'],
    queryFn: () => listarTodosOsStatus(),
  })
}

export function useCriarOsStatus() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: OsStatusRequest) => criarOsStatus(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_OS_STATUS }),
  })
}

export function useAtualizarOsStatus() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: OsStatusRequest }) => atualizarOsStatus(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_OS_STATUS }),
  })
}

export function useExcluirOsStatus() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirOsStatus(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_OS_STATUS }),
  })
}

// --- OsEquipamento ---

export function useOsEquipamentosPaginado(pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_OS_EQUIPAMENTOS, pagina, tamanho],
    queryFn: () => listarOsEquipamentos(pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useTodosOsEquipamentos() {
  return useQuery({
    queryKey: [...QUERY_KEY_OS_EQUIPAMENTOS, 'todos'],
    queryFn: () => listarTodosOsEquipamentos(),
  })
}

export function useCriarOsEquipamento() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: OsEquipamentoRequest) => criarOsEquipamento(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_OS_EQUIPAMENTOS }),
  })
}

export function useAtualizarOsEquipamento() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: OsEquipamentoRequest }) => atualizarOsEquipamento(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_OS_EQUIPAMENTOS }),
  })
}

export function useExcluirOsEquipamento() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirOsEquipamento(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_OS_EQUIPAMENTOS }),
  })
}

// --- OsAbertura ---

export function useOsAberturas(clienteId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_OS_ABERTURAS, clienteId, pagina, tamanho],
    queryFn: () => listarOsAberturas(clienteId, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useOsAbertura(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyOsAbertura(id ?? -1),
    queryFn: () => buscarOsAbertura(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarOsAbertura() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: OsAberturaRequest) => criarOsAbertura(request),
    onSuccess: (criada) => {
      queryClient.setQueryData(queryKeyOsAbertura(criada.id), criada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_OS_ABERTURAS })
    },
  })
}

export function useTrocarStatusOsAbertura(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (osStatusId: number) => trocarStatusOsAbertura(id, osStatusId),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyOsAbertura(id), atualizada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_OS_ABERTURAS })
    },
  })
}

export function useAprovarOrcamentoOsAbertura(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => aprovarOrcamentoOsAbertura(id),
    onSuccess: (atualizada) => queryClient.setQueryData(queryKeyOsAbertura(id), atualizada),
  })
}

export function useEncerrarOsAbertura(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => encerrarOsAbertura(id),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyOsAbertura(id), atualizada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_OS_ABERTURAS })
    },
  })
}

export function useAdicionarEquipamentoOs(osAberturaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: OsAberturaEquipamentoRequest) => adicionarEquipamentoOs(osAberturaId, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyOsAbertura(osAberturaId) }),
  })
}

export function useRemoverEquipamentoOs(osAberturaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (vinculoId: number) => removerEquipamentoOs(osAberturaId, vinculoId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyOsAbertura(osAberturaId) }),
  })
}

export function useAdicionarItemOs(osAberturaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: OsProdutoServicoRequest) => adicionarItemOs(osAberturaId, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyOsAbertura(osAberturaId) }),
  })
}

export function useRemoverItemOs(osAberturaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (itemId: number) => removerItemOs(osAberturaId, itemId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyOsAbertura(osAberturaId) }),
  })
}

export function useAdicionarEvolucaoOs(osAberturaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: OsEvolucaoRequest) => adicionarEvolucaoOs(osAberturaId, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyOsAbertura(osAberturaId) }),
  })
}

// --- Lookups cross-serviço ---

export function useBuscaClientesParaSelecao(nome: string | undefined) {
  return useQuery({
    queryKey: ['clientes-para-selecao-os', nome],
    queryFn: () => buscarClientesParaSelecao(nome),
    enabled: nome !== undefined && nome.length >= 2,
  })
}

export function useClienteResumo(id: number | undefined) {
  return useQuery({
    queryKey: ['cliente-resumo-os', id],
    queryFn: () => buscarClienteResumo(id as number),
    enabled: id != null,
    staleTime: 5 * 60_000,
    retry: (failureCount, erro) => (erro as unknown as AppError).status !== 404 && failureCount < 1,
  })
}

export function useBuscaColaboradoresParaSelecao(nome: string | undefined) {
  return useQuery({
    queryKey: ['colaboradores-para-selecao-os', nome],
    queryFn: () => buscarColaboradoresParaSelecao(nome),
    enabled: nome !== undefined && nome.length >= 2,
  })
}

export function useColaboradorResumo(id: number | undefined) {
  return useQuery({
    queryKey: ['colaborador-resumo-os', id],
    queryFn: () => buscarColaboradorResumo(id as number),
    enabled: id != null,
    staleTime: 5 * 60_000,
    retry: (failureCount, erro) => (erro as unknown as AppError).status !== 404 && failureCount < 1,
  })
}

export function useBuscaProdutosParaSelecao(nome: string | undefined) {
  return useQuery({
    queryKey: ['produtos-para-selecao-os', nome],
    queryFn: () => buscarProdutosParaSelecao(nome),
    enabled: nome !== undefined && nome.length >= 2,
  })
}
