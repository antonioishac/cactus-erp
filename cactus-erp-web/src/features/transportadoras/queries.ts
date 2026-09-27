import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { atualizarTransportadora, buscarTransportadora, criarTransportadora, listarTransportadoras } from './api'
import type { TransportadoraRequest, TransportadorasFiltro } from './types'

const QUERY_KEY_LISTA = ['transportadoras'] as const
const queryKeyDetalhe = (id: number) => ['transportadoras', id] as const

export function useTransportadoras(filtro: TransportadorasFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_LISTA, filtro, pagina, tamanho],
    queryFn: () => listarTransportadoras(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useTransportadora(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyDetalhe(id ?? -1),
    queryFn: () => buscarTransportadora(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarTransportadora() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: TransportadoraRequest) => criarTransportadora(request),
    onSuccess: (criada) => {
      queryClient.setQueryData(queryKeyDetalhe(criada.id), criada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LISTA })
    },
  })
}

export function useAtualizarTransportadora(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: TransportadoraRequest) => atualizarTransportadora(id, request),
    onSuccess: (atualizada) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LISTA })
      queryClient.setQueryData(queryKeyDetalhe(id), atualizada)
    },
  })
}
