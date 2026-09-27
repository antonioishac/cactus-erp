import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  atualizarTabelaPreco,
  buscarProdutosParaSelecao,
  buscarTabelaPreco,
  criarTabelaPreco,
  listarTabelasPreco,
} from './api'
import type { TabelaPrecoRequest, TabelasPrecoFiltro } from './types'

const QUERY_KEY_LISTA = ['tabelas-preco'] as const
const queryKeyDetalhe = (id: number) => ['tabelas-preco', id] as const

export function useTabelasPreco(filtro: TabelasPrecoFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_LISTA, filtro, pagina, tamanho],
    queryFn: () => listarTabelasPreco(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useTabelaPreco(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyDetalhe(id ?? -1),
    queryFn: () => buscarTabelaPreco(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarTabelaPreco() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: TabelaPrecoRequest) => criarTabelaPreco(request),
    onSuccess: (criada) => {
      queryClient.setQueryData(queryKeyDetalhe(criada.id), criada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LISTA })
    },
  })
}

export function useAtualizarTabelaPreco(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: TabelaPrecoRequest) => atualizarTabelaPreco(id, request),
    onSuccess: (atualizada) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LISTA })
      queryClient.setQueryData(queryKeyDetalhe(id), atualizada)
    },
  })
}

export function useBuscaProdutosParaSelecao(nome: string | undefined) {
  return useQuery({
    queryKey: ['produtos-para-selecao', nome],
    queryFn: () => buscarProdutosParaSelecao(nome),
    enabled: nome !== undefined && nome.length >= 2,
  })
}
