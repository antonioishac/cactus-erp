import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  atualizarCentroCusto,
  atualizarPlanoConta,
  buscarBalancete,
  buscarLancamento,
  buscarPlanoConta,
  buscarRazao,
  criarCentroCusto,
  criarLancamento,
  criarPlanoConta,
  estornarLancamento,
  excluirCentroCusto,
  excluirPlanoConta,
  listarCentrosCusto,
  listarLancamentos,
  listarPlanoContas,
  listarTodasContas,
  listarTodosCentrosCusto,
} from './api'
import type {
  CentroCustoRequest,
  EstornarLancamentoRequest,
  LancamentoContabilRequest,
  LancamentosFiltro,
  PlanoContaRequest,
} from './types'

const QUERY_KEY_PLANO_CONTAS = ['plano-contas'] as const
const queryKeyPlanoConta = (id: number) => ['plano-contas', id] as const
const QUERY_KEY_CENTROS_CUSTO = ['centros-custo'] as const
const QUERY_KEY_LANCAMENTOS = ['lancamentos-contabeis'] as const
const queryKeyLancamento = (id: number) => ['lancamentos-contabeis', id] as const

// --- Plano de Contas ---

export function usePlanoContas(empresaId: number, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_PLANO_CONTAS, empresaId, pagina, tamanho],
    queryFn: () => listarPlanoContas(empresaId, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useTodasContas(empresaId: number) {
  return useQuery({
    queryKey: ['plano-contas-todas', empresaId],
    queryFn: () => listarTodasContas(empresaId),
    staleTime: 2 * 60_000,
  })
}

export function usePlanoConta(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyPlanoConta(id ?? -1),
    queryFn: () => buscarPlanoConta(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarPlanoConta() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: PlanoContaRequest) => criarPlanoConta(request),
    onSuccess: (criada) => {
      queryClient.setQueryData(queryKeyPlanoConta(criada.id), criada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_PLANO_CONTAS })
      queryClient.invalidateQueries({ queryKey: ['plano-contas-todas'] })
    },
  })
}

export function useAtualizarPlanoConta() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: PlanoContaRequest }) => atualizarPlanoConta(id, request),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyPlanoConta(atualizada.id), atualizada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_PLANO_CONTAS })
      queryClient.invalidateQueries({ queryKey: ['plano-contas-todas'] })
    },
  })
}

export function useExcluirPlanoConta() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirPlanoConta(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_PLANO_CONTAS })
      queryClient.invalidateQueries({ queryKey: ['plano-contas-todas'] })
    },
  })
}

// --- Centro de Custo ---

export function useCentrosCusto(empresaId: number, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_CENTROS_CUSTO, empresaId, pagina, tamanho],
    queryFn: () => listarCentrosCusto(empresaId, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useTodosCentrosCusto(empresaId: number) {
  return useQuery({
    queryKey: ['centros-custo-todos', empresaId],
    queryFn: () => listarTodosCentrosCusto(empresaId),
    staleTime: 2 * 60_000,
  })
}

export function useCriarCentroCusto() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: CentroCustoRequest) => criarCentroCusto(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CENTROS_CUSTO })
      queryClient.invalidateQueries({ queryKey: ['centros-custo-todos'] })
    },
  })
}

export function useAtualizarCentroCusto() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: CentroCustoRequest }) => atualizarCentroCusto(id, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CENTROS_CUSTO })
      queryClient.invalidateQueries({ queryKey: ['centros-custo-todos'] })
    },
  })
}

export function useExcluirCentroCusto() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirCentroCusto(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CENTROS_CUSTO })
      queryClient.invalidateQueries({ queryKey: ['centros-custo-todos'] })
    },
  })
}

// --- Lançamento Contábil ---

export function useLancamentos(filtro: LancamentosFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_LANCAMENTOS, filtro, pagina, tamanho],
    queryFn: () => listarLancamentos(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useLancamento(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyLancamento(id ?? -1),
    queryFn: () => buscarLancamento(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarLancamento() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: LancamentoContabilRequest) => criarLancamento(request),
    onSuccess: (criado) => {
      queryClient.setQueryData(queryKeyLancamento(criado.id), criado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LANCAMENTOS })
    },
  })
}

export function useEstornarLancamento(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: EstornarLancamentoRequest) => estornarLancamento(id, request),
    onSuccess: (estornado) => {
      queryClient.setQueryData(queryKeyLancamento(id), estornado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LANCAMENTOS })
    },
  })
}

// --- Relatórios ---

export function useBalancete(
  empresaId: number,
  dataInicio: string | undefined,
  dataFim: string | undefined,
) {
  return useQuery({
    queryKey: ['balancete', empresaId, dataInicio, dataFim],
    queryFn: () => buscarBalancete(empresaId, dataInicio as string, dataFim as string),
    enabled: !!dataInicio && !!dataFim,
  })
}

export function useRazao(
  empresaId: number,
  contaId: number | undefined,
  dataInicio: string | undefined,
  dataFim: string | undefined,
) {
  return useQuery({
    queryKey: ['razao', empresaId, contaId, dataInicio, dataFim],
    queryFn: () => buscarRazao(empresaId, contaId as number, dataInicio as string, dataFim as string),
    enabled: !!contaId && !!dataInicio && !!dataFim,
  })
}
