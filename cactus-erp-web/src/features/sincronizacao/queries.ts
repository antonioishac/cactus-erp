import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { criarClienteLoja, listarClientesLoja, listarLotes, listarRegistros, reprocessarRegistro } from './api'
import type { ClienteLojaRequest, LotesFiltro, RegistrosFiltro } from './types'

const QUERY_KEY_LOTES = ['sincronizacao-lotes'] as const
const QUERY_KEY_REGISTROS = ['sincronizacao-registros'] as const
const QUERY_KEY_CLIENTES_LOJA = ['sincronizacao-clientes-loja'] as const

export function useLotesSincronizacao(filtro: LotesFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_LOTES, filtro, pagina, tamanho],
    queryFn: () => listarLotes(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useRegistrosSincronizacao(filtro: RegistrosFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_REGISTROS, filtro, pagina, tamanho],
    queryFn: () => listarRegistros(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useReprocessarRegistro() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => reprocessarRegistro(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_REGISTROS }),
  })
}

export function useClientesLoja() {
  return useQuery({ queryKey: QUERY_KEY_CLIENTES_LOJA, queryFn: listarClientesLoja })
}

export function useCriarClienteLoja() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ClienteLojaRequest) => criarClienteLoja(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_CLIENTES_LOJA }),
  })
}
