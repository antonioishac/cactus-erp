import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  adicionarOpcaoAtributo,
  atualizarAtributo,
  atualizarCategoria,
  atualizarLoja,
  atualizarProduto,
  atualizarVariante,
  buscarLoja,
  buscarPedido,
  buscarProduto,
  cancelarAssinaturaLoja,
  cancelarPedido,
  confirmarPagamentoPedido,
  criarAtributo,
  criarCategoria,
  criarLoja,
  criarProduto,
  criarVariante,
  excluirAtributo,
  excluirCategoria,
  excluirOpcaoAtributo,
  excluirProduto,
  excluirVariante,
  listarAtributos,
  listarCategorias,
  listarLojas,
  listarPedidos,
  listarProdutos,
  listarTodasCategorias,
  listarTodasLojas,
  listarTodosAtributos,
  marcarPedidoEmSeparacao,
  marcarPedidoEntregue,
  marcarPedidoEnviado,
  reativarAssinaturaLoja,
} from './api'
import type {
  AtributoCategoriaOpcaoRequest,
  AtributoCategoriaRequest,
  CancelarPedidoRequest,
  CategoriaLojaRequest,
  LojaRequest,
  PedidosFiltro,
  ProdutoLojaRequest,
  VarianteProdutoLojaRequest,
} from './types'

const QUERY_KEY_LOJAS = ['lojas'] as const
const queryKeyLoja = (id: number) => ['lojas', id] as const
const QUERY_KEY_CATEGORIAS = ['categorias-loja'] as const
const QUERY_KEY_ATRIBUTOS = ['atributos-categoria'] as const
const QUERY_KEY_PRODUTOS = ['produtos-loja'] as const
const queryKeyProduto = (id: number) => ['produtos-loja', id] as const
const QUERY_KEY_PEDIDOS = ['pedidos-loja'] as const
const queryKeyPedido = (id: number) => ['pedidos-loja', id] as const

// --- Loja ---

export function useLojas(pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_LOJAS, pagina, tamanho],
    queryFn: () => listarLojas(pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useTodasLojas() {
  return useQuery({ queryKey: ['lojas-todas'], queryFn: listarTodasLojas, staleTime: 2 * 60_000 })
}

export function useLoja(id: number | undefined) {
  return useQuery({ queryKey: queryKeyLoja(id ?? -1), queryFn: () => buscarLoja(id as number), enabled: id !== undefined })
}

export function useCriarLoja() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: LojaRequest) => criarLoja(request),
    onSuccess: (criada) => {
      queryClient.setQueryData(queryKeyLoja(criada.id), criada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LOJAS })
      queryClient.invalidateQueries({ queryKey: ['lojas-todas'] })
    },
  })
}

export function useAtualizarLoja() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: LojaRequest }) => atualizarLoja(id, request),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyLoja(atualizada.id), atualizada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LOJAS })
      queryClient.invalidateQueries({ queryKey: ['lojas-todas'] })
    },
  })
}

export function useCancelarAssinaturaLoja() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => cancelarAssinaturaLoja(id),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyLoja(atualizada.id), atualizada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LOJAS })
    },
  })
}

export function useReativarAssinaturaLoja() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => reativarAssinaturaLoja(id),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyLoja(atualizada.id), atualizada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LOJAS })
    },
  })
}

// --- Categoria da Loja ---

export function useCategorias(lojaId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_CATEGORIAS, lojaId, pagina, tamanho],
    queryFn: () => listarCategorias(lojaId, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useTodasCategorias(lojaId: number | undefined) {
  return useQuery({
    queryKey: ['categorias-loja-todas', lojaId],
    queryFn: () => listarTodasCategorias(lojaId as number),
    enabled: lojaId !== undefined,
    staleTime: 2 * 60_000,
  })
}

export function useCriarCategoria() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: CategoriaLojaRequest) => criarCategoria(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CATEGORIAS })
      queryClient.invalidateQueries({ queryKey: ['categorias-loja-todas'] })
    },
  })
}

export function useAtualizarCategoria() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: CategoriaLojaRequest }) => atualizarCategoria(id, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CATEGORIAS })
      queryClient.invalidateQueries({ queryKey: ['categorias-loja-todas'] })
    },
  })
}

export function useExcluirCategoria() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirCategoria(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CATEGORIAS })
      queryClient.invalidateQueries({ queryKey: ['categorias-loja-todas'] })
    },
  })
}

// --- Atributo de Categoria ---

export function useAtributos(categoriaLojaId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_ATRIBUTOS, categoriaLojaId, pagina, tamanho],
    queryFn: () => listarAtributos(categoriaLojaId, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useTodosAtributos(categoriaLojaId: number | undefined) {
  return useQuery({
    queryKey: ['atributos-categoria-todos', categoriaLojaId],
    queryFn: () => listarTodosAtributos(categoriaLojaId as number),
    enabled: categoriaLojaId !== undefined,
    staleTime: 60_000,
  })
}

export function useCriarAtributo() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: AtributoCategoriaRequest) => criarAtributo(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_ATRIBUTOS })
      queryClient.invalidateQueries({ queryKey: ['atributos-categoria-todos'] })
    },
  })
}

export function useAtualizarAtributo() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: AtributoCategoriaRequest }) => atualizarAtributo(id, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_ATRIBUTOS })
      queryClient.invalidateQueries({ queryKey: ['atributos-categoria-todos'] })
    },
  })
}

export function useExcluirAtributo() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirAtributo(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_ATRIBUTOS })
      queryClient.invalidateQueries({ queryKey: ['atributos-categoria-todos'] })
    },
  })
}

export function useAdicionarOpcaoAtributo() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ atributoId, request }: { atributoId: number; request: AtributoCategoriaOpcaoRequest }) =>
      adicionarOpcaoAtributo(atributoId, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_ATRIBUTOS })
      queryClient.invalidateQueries({ queryKey: ['atributos-categoria-todos'] })
    },
  })
}

export function useExcluirOpcaoAtributo() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ atributoId, opcaoId }: { atributoId: number; opcaoId: number }) => excluirOpcaoAtributo(atributoId, opcaoId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_ATRIBUTOS })
      queryClient.invalidateQueries({ queryKey: ['atributos-categoria-todos'] })
    },
  })
}

// --- Produto da Loja ---

export function useProdutos(lojaId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_PRODUTOS, lojaId, pagina, tamanho],
    queryFn: () => listarProdutos(lojaId, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useProduto(id: number | undefined) {
  return useQuery({ queryKey: queryKeyProduto(id ?? -1), queryFn: () => buscarProduto(id as number), enabled: id !== undefined })
}

export function useCriarProduto() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ProdutoLojaRequest) => criarProduto(request),
    onSuccess: (criado) => {
      queryClient.setQueryData(queryKeyProduto(criado.id), criado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_PRODUTOS })
    },
  })
}

export function useAtualizarProduto() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: ProdutoLojaRequest }) => atualizarProduto(id, request),
    onSuccess: (atualizado) => {
      queryClient.setQueryData(queryKeyProduto(atualizado.id), atualizado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_PRODUTOS })
    },
  })
}

export function useExcluirProduto() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirProduto(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_PRODUTOS }),
  })
}

export function useCriarVariante(produtoId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: VarianteProdutoLojaRequest) => criarVariante(produtoId, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyProduto(produtoId) }),
  })
}

export function useAtualizarVariante(produtoId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: VarianteProdutoLojaRequest }) => atualizarVariante(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyProduto(produtoId) }),
  })
}

export function useExcluirVariante(produtoId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirVariante(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyProduto(produtoId) }),
  })
}

// --- Pedido ---

export function usePedidos(filtro: PedidosFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_PEDIDOS, filtro, pagina, tamanho],
    queryFn: () => listarPedidos(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function usePedido(id: number | undefined) {
  return useQuery({ queryKey: queryKeyPedido(id ?? -1), queryFn: () => buscarPedido(id as number), enabled: id !== undefined })
}

function useTransicaoPedido(id: number, mutationFn: (id: number) => Promise<unknown>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => mutationFn(id),
    onSuccess: (atualizado) => {
      queryClient.setQueryData(queryKeyPedido(id), atualizado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_PEDIDOS })
    },
  })
}

export function useConfirmarPagamentoPedido(id: number) {
  return useTransicaoPedido(id, confirmarPagamentoPedido)
}

export function useMarcarPedidoEmSeparacao(id: number) {
  return useTransicaoPedido(id, marcarPedidoEmSeparacao)
}

export function useMarcarPedidoEnviado(id: number) {
  return useTransicaoPedido(id, marcarPedidoEnviado)
}

export function useMarcarPedidoEntregue(id: number) {
  return useTransicaoPedido(id, marcarPedidoEntregue)
}

export function useCancelarPedido(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: CancelarPedidoRequest) => cancelarPedido(id, request),
    onSuccess: (atualizado) => {
      queryClient.setQueryData(queryKeyPedido(id), atualizado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_PEDIDOS })
    },
  })
}
