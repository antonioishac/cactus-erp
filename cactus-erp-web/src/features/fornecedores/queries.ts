import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { atualizarFornecedor, buscarFornecedor, criarFornecedor, listarFornecedores } from './api'
import type { FornecedorRequest, FornecedoresFiltro } from './types'

const QUERY_KEY_LISTA = ['fornecedores'] as const
const queryKeyDetalhe = (id: number) => ['fornecedores', id] as const

export function useFornecedores(filtro: FornecedoresFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_LISTA, filtro, pagina, tamanho],
    queryFn: () => listarFornecedores(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useFornecedor(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyDetalhe(id ?? -1),
    queryFn: () => buscarFornecedor(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarFornecedor() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: FornecedorRequest) => criarFornecedor(request),
    onSuccess: (criado) => {
      // Ver comentário equivalente em features/clientes/queries.ts — evita o
      // flash de Skeleton ao navegar pro modo edição logo após criar.
      queryClient.setQueryData(queryKeyDetalhe(criado.id), criado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LISTA })
    },
  })
}

export function useAtualizarFornecedor(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: FornecedorRequest) => atualizarFornecedor(id, request),
    onSuccess: (atualizado) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LISTA })
      queryClient.setQueryData(queryKeyDetalhe(id), atualizado)
    },
  })
}
