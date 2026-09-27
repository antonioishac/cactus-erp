import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  atualizarProduto,
  atualizarProdutoGrupo,
  atualizarProdutoMarca,
  atualizarProdutoSubgrupo,
  atualizarProdutoTipo,
  atualizarProdutoUnidade,
  buscarCest,
  buscarNcm,
  buscarProduto,
  criarProduto,
  criarProdutoGrupo,
  criarProdutoMarca,
  criarProdutoSubgrupo,
  criarProdutoTipo,
  criarProdutoUnidade,
  listarGruposTributarios,
  listarOperacoesFiscais,
  listarProdutoGrupos,
  listarProdutoMarcas,
  listarProdutoSubgrupos,
  listarProdutoTipos,
  listarProdutoUnidades,
  listarProdutos,
  recalcularTributacaoPadrao,
} from './api'
import type {
  ProdutoGrupoRequest,
  ProdutoMarcaRequest,
  ProdutoRequest,
  ProdutoSubgrupoRequest,
  ProdutoTipoRequest,
  ProdutoUnidadeRequest,
  ProdutosFiltro,
  RecalcularTributacaoPadraoRequest,
} from './types'

const QUERY_KEY_LISTA = ['produtos'] as const
const queryKeyDetalhe = (id: number) => ['produtos', id] as const

export function useProdutos(filtro: ProdutosFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_LISTA, filtro, pagina, tamanho],
    queryFn: () => listarProdutos(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useProduto(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyDetalhe(id ?? -1),
    queryFn: () => buscarProduto(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarProduto() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ProdutoRequest) => criarProduto(request),
    onSuccess: (criado) => {
      // Mesmo fix já aplicado em Cliente/Fornecedor/Colaborador/Empresa: evita
      // flash de Skeleton ao navegar pro modo edição logo após criar.
      queryClient.setQueryData(queryKeyDetalhe(criado.id), criado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LISTA })
    },
  })
}

export function useAtualizarProduto(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ProdutoRequest) => atualizarProduto(id, request),
    onSuccess: (atualizado) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LISTA })
      queryClient.setQueryData(queryKeyDetalhe(id), atualizado)
    },
  })
}

export function useRecalcularTributacaoPadrao(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: RecalcularTributacaoPadraoRequest) => recalcularTributacaoPadrao(id, request),
    onSuccess: (atualizado) => {
      queryClient.setQueryData(queryKeyDetalhe(id), atualizado)
    },
  })
}

// --- Lookups ---

export function useProdutoGrupos() {
  return useQuery({ queryKey: ['produto-grupos'], queryFn: listarProdutoGrupos, staleTime: 5 * 60_000 })
}
export function useCriarProdutoGrupo() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ProdutoGrupoRequest) => criarProdutoGrupo(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['produto-grupos'] }),
  })
}
export function useAtualizarProdutoGrupo() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: ProdutoGrupoRequest }) => atualizarProdutoGrupo(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['produto-grupos'] }),
  })
}

export function useProdutoSubgrupos() {
  return useQuery({ queryKey: ['produto-subgrupos'], queryFn: listarProdutoSubgrupos, staleTime: 5 * 60_000 })
}
export function useCriarProdutoSubgrupo() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ProdutoSubgrupoRequest) => criarProdutoSubgrupo(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['produto-subgrupos'] }),
  })
}
export function useAtualizarProdutoSubgrupo() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: ProdutoSubgrupoRequest }) =>
      atualizarProdutoSubgrupo(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['produto-subgrupos'] }),
  })
}

export function useProdutoTipos() {
  return useQuery({ queryKey: ['produto-tipos'], queryFn: listarProdutoTipos, staleTime: 5 * 60_000 })
}
export function useCriarProdutoTipo() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ProdutoTipoRequest) => criarProdutoTipo(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['produto-tipos'] }),
  })
}
export function useAtualizarProdutoTipo() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: ProdutoTipoRequest }) => atualizarProdutoTipo(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['produto-tipos'] }),
  })
}

export function useProdutoUnidades() {
  return useQuery({ queryKey: ['produto-unidades'], queryFn: listarProdutoUnidades, staleTime: 5 * 60_000 })
}
export function useCriarProdutoUnidade() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ProdutoUnidadeRequest) => criarProdutoUnidade(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['produto-unidades'] }),
  })
}
export function useAtualizarProdutoUnidade() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: ProdutoUnidadeRequest }) =>
      atualizarProdutoUnidade(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['produto-unidades'] }),
  })
}

export function useProdutoMarcas() {
  return useQuery({ queryKey: ['produto-marcas'], queryFn: listarProdutoMarcas, staleTime: 5 * 60_000 })
}
export function useCriarProdutoMarca() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ProdutoMarcaRequest) => criarProdutoMarca(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['produto-marcas'] }),
  })
}
export function useAtualizarProdutoMarca() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: ProdutoMarcaRequest }) => atualizarProdutoMarca(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['produto-marcas'] }),
  })
}

// --- Catálogos fiscais (busca assíncrona) ---

export function useBuscaNcm(busca: string | undefined) {
  return useQuery({
    queryKey: ['ncm', busca],
    queryFn: () => buscarNcm(busca),
    enabled: busca !== undefined && busca.length >= 2,
  })
}

export function useBuscaCest(descricao: string | undefined) {
  return useQuery({
    queryKey: ['cest', descricao],
    queryFn: () => buscarCest(descricao),
    enabled: descricao !== undefined && descricao.length >= 2,
  })
}

export function useGruposTributarios(nome?: string) {
  return useQuery({ queryKey: ['grupos-tributarios', nome], queryFn: () => listarGruposTributarios(nome) })
}

export function useOperacoesFiscais(nome?: string) {
  return useQuery({ queryKey: ['operacoes-fiscais', nome], queryFn: () => listarOperacoesFiscais(nome) })
}
