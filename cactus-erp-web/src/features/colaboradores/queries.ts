import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  atualizarCargo,
  atualizarColaborador,
  atualizarSetor,
  buscarColaborador,
  criarCargo,
  criarColaborador,
  criarSetor,
  listarCargos,
  listarColaboradores,
  listarSetores,
} from './api'
import type { CargoRequest, ColaboradorRequest, ColaboradoresFiltro, SetorRequest } from './types'

const QUERY_KEY_LISTA = ['colaboradores'] as const
const queryKeyDetalhe = (id: number) => ['colaboradores', id] as const

export function useColaboradores(filtro: ColaboradoresFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_LISTA, filtro, pagina, tamanho],
    queryFn: () => listarColaboradores(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useColaborador(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyDetalhe(id ?? -1),
    queryFn: () => buscarColaborador(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarColaborador() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ColaboradorRequest) => criarColaborador(request),
    onSuccess: (criado) => {
      // Ver comentário equivalente em features/clientes/queries.ts — evita o
      // flash de Skeleton ao navegar pro modo edição logo após criar.
      queryClient.setQueryData(queryKeyDetalhe(criado.id), criado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LISTA })
    },
  })
}

export function useAtualizarColaborador(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ColaboradorRequest) => atualizarColaborador(id, request),
    onSuccess: (atualizado) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LISTA })
      queryClient.setQueryData(queryKeyDetalhe(id), atualizado)
    },
  })
}

/** Cargos/setores mudam raramente — staleTime alto evita refetch a cada abertura do form. */
export function useCargos() {
  return useQuery({ queryKey: ['cargos'], queryFn: listarCargos, staleTime: 5 * 60_000 })
}

export function useSetores() {
  return useQuery({ queryKey: ['setores'], queryFn: listarSetores, staleTime: 5 * 60_000 })
}

export function useCriarCargo() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: CargoRequest) => criarCargo(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['cargos'] }),
  })
}

export function useAtualizarCargo() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: CargoRequest }) => atualizarCargo(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['cargos'] }),
  })
}

export function useCriarSetor() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: SetorRequest) => criarSetor(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['setores'] }),
  })
}

export function useAtualizarSetor() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: SetorRequest }) => atualizarSetor(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['setores'] }),
  })
}
