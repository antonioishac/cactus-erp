import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { atualizarCliente, buscarCliente, criarCliente, listarClientes } from './api'
import type { ClienteRequest, ClientesFiltro } from './types'

const QUERY_KEY_LISTA = ['clientes'] as const
const queryKeyDetalhe = (id: number) => ['clientes', id] as const

export function useClientes(filtro: ClientesFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_LISTA, filtro, pagina, tamanho],
    queryFn: () => listarClientes(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useCliente(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyDetalhe(id ?? -1),
    queryFn: () => buscarCliente(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarCliente() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ClienteRequest) => criarCliente(request),
    onSuccess: (criado) => {
      // Popula o cache de detalhe com o resultado que a própria criação já
      // devolveu — sem isso, o form navega pro modo edição e `useCliente(id)`
      // dispara um fetch novo do zero, com um flash de `Skeleton` evitável
      // (achado real testando o mesmo padrão em Empresa, 2026-09-01).
      queryClient.setQueryData(queryKeyDetalhe(criado.id), criado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LISTA })
    },
  })
}

export function useAtualizarCliente(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ClienteRequest) => atualizarCliente(id, request),
    onSuccess: (atualizado) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LISTA })
      queryClient.setQueryData(queryKeyDetalhe(id), atualizado)
    },
  })
}
