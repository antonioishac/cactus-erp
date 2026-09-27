import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import type { AppError } from '@shared/api/problemDetail'
import {
  adicionarFornecedorCotacao,
  adicionarItemFornecedorCotacao,
  adicionarItemPedido,
  aprovarPedido,
  atualizarConfiguracaoCompras,
  buscarColaboradorResumo,
  buscarColaboradoresParaSelecao,
  buscarConfiguracaoCompras,
  buscarCotacao,
  buscarEmpresasParaSelecao,
  buscarFornecedorResumo,
  buscarFornecedoresParaSelecao,
  buscarPedido,
  buscarProdutosParaSelecao,
  buscarProdutoUnidadesParaSelecao,
  buscarRecebimento,
  buscarRequisicao,
  cancelarCotacao,
  cancelarPedido,
  cancelarRequisicao,
  criarCotacao,
  criarPedido,
  criarRecebimento,
  criarRequisicao,
  enviarPedido,
  gerarPedidoDeCotacao,
  listarCotacoes,
  listarDepositosAtivos,
  listarLotesParaSelecao,
  listarPedidos,
  listarRecebimentos,
  listarRequisicoes,
  removerItemPedido,
} from './api'
import type {
  ConfiguracaoComprasEmpresaRequest,
  CotacaoCompraRequest,
  FornecedorCotacaoItemRequest,
  FornecedorCotacaoRequest,
  GerarPedidoDeCotacaoRequest,
  PedidoCompraItemRequest,
  PedidoCompraRequest,
  PedidosFiltro,
  RecebimentoCompraRequest,
  RequisicaoCompraRequest,
  RequisicoesFiltro,
} from './types'

const QUERY_KEY_REQUISICOES = ['compras-requisicoes'] as const
const queryKeyRequisicao = (id: number) => ['compras-requisicoes', id] as const
const QUERY_KEY_PEDIDOS = ['compras-pedidos'] as const
const queryKeyPedido = (id: number) => ['compras-pedidos', id] as const

// --- Requisição de Compra ---

export function useRequisicoes(filtro: RequisicoesFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_REQUISICOES, filtro, pagina, tamanho],
    queryFn: () => listarRequisicoes(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useRequisicao(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyRequisicao(id ?? -1),
    queryFn: () => buscarRequisicao(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarRequisicao() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: RequisicaoCompraRequest) => criarRequisicao(request),
    onSuccess: (criada) => {
      queryClient.setQueryData(queryKeyRequisicao(criada.id), criada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_REQUISICOES })
    },
  })
}

export function useCancelarRequisicao(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => cancelarRequisicao(id),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyRequisicao(id), atualizada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_REQUISICOES })
    },
  })
}

// --- Cotação de Compra ---

export function useCotacoes(requisicaoId: number | undefined) {
  return useQuery({
    queryKey: ['compras-cotacoes', requisicaoId ?? -1],
    queryFn: () => listarCotacoes(requisicaoId as number),
    enabled: requisicaoId !== undefined,
  })
}

export function useCotacao(id: number | undefined) {
  return useQuery({
    queryKey: ['compras-cotacoes', 'detalhe', id ?? -1],
    queryFn: () => buscarCotacao(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarCotacao() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: CotacaoCompraRequest) => criarCotacao(request),
    onSuccess: (criada) => queryClient.invalidateQueries({ queryKey: ['compras-cotacoes', criada.requisicaoId] }),
  })
}

export function useCancelarCotacao(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => cancelarCotacao(id),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(['compras-cotacoes', 'detalhe', id], atualizada)
      queryClient.invalidateQueries({ queryKey: ['compras-cotacoes', atualizada.requisicaoId] })
    },
  })
}

export function useAdicionarFornecedorCotacao(cotacaoId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: FornecedorCotacaoRequest) => adicionarFornecedorCotacao(cotacaoId, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['compras-cotacoes', 'detalhe', cotacaoId] }),
  })
}

export function useAdicionarItemFornecedorCotacao(cotacaoId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ fornecedorCotacaoId, request }: { fornecedorCotacaoId: number; request: FornecedorCotacaoItemRequest }) =>
      adicionarItemFornecedorCotacao(cotacaoId, fornecedorCotacaoId, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['compras-cotacoes', 'detalhe', cotacaoId] }),
  })
}

export function useGerarPedidoDeCotacao(cotacaoId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ fornecedorCotacaoId, request }: { fornecedorCotacaoId: number; request: GerarPedidoDeCotacaoRequest }) =>
      gerarPedidoDeCotacao(cotacaoId, fornecedorCotacaoId, request),
    onSuccess: (pedidoCriado) => {
      queryClient.setQueryData(queryKeyPedido(pedidoCriado.id), pedidoCriado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_PEDIDOS })
      queryClient.invalidateQueries({ queryKey: ['compras-cotacoes', 'detalhe', cotacaoId] })
    },
  })
}

// --- Pedido de Compra ---

export function usePedidos(filtro: PedidosFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_PEDIDOS, filtro, pagina, tamanho],
    queryFn: () => listarPedidos(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function usePedido(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyPedido(id ?? -1),
    queryFn: () => buscarPedido(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarPedido() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: PedidoCompraRequest) => criarPedido(request),
    onSuccess: (criado) => {
      queryClient.setQueryData(queryKeyPedido(criado.id), criado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_PEDIDOS })
    },
  })
}

export function useAdicionarItemPedido(pedidoId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: PedidoCompraItemRequest) => adicionarItemPedido(pedidoId, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyPedido(pedidoId) }),
  })
}

export function useRemoverItemPedido(pedidoId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (itemId: number) => removerItemPedido(pedidoId, itemId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyPedido(pedidoId) }),
  })
}

export function useEnviarPedido(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => enviarPedido(id),
    onSuccess: (atualizado) => {
      queryClient.setQueryData(queryKeyPedido(id), atualizado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_PEDIDOS })
    },
  })
}

export function useAprovarPedido(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => aprovarPedido(id),
    onSuccess: (atualizado) => {
      queryClient.setQueryData(queryKeyPedido(id), atualizado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_PEDIDOS })
    },
  })
}

export function useCancelarPedido(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => cancelarPedido(id),
    onSuccess: (atualizado) => {
      queryClient.setQueryData(queryKeyPedido(id), atualizado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_PEDIDOS })
    },
  })
}

// --- Recebimento de Compra ---

export function useRecebimentos(pedidoId: number | undefined) {
  return useQuery({
    queryKey: ['compras-recebimentos', pedidoId ?? -1],
    queryFn: () => listarRecebimentos(pedidoId as number),
    enabled: pedidoId !== undefined,
  })
}

export function useRecebimento(id: number | undefined) {
  return useQuery({
    queryKey: ['compras-recebimentos', 'detalhe', id ?? -1],
    queryFn: () => buscarRecebimento(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarRecebimento(pedidoId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: RecebimentoCompraRequest) => criarRecebimento(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['compras-recebimentos', pedidoId] })
      queryClient.invalidateQueries({ queryKey: queryKeyPedido(pedidoId) })
    },
  })
}

// --- Configuração de Compras ---

export function useConfiguracaoCompras(empresaId: number | undefined) {
  return useQuery({
    queryKey: ['compras-configuracao', empresaId ?? -1],
    queryFn: () => buscarConfiguracaoCompras(empresaId as number),
    enabled: empresaId !== undefined,
  })
}

export function useAtualizarConfiguracaoCompras(empresaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ConfiguracaoComprasEmpresaRequest) => atualizarConfiguracaoCompras(empresaId, request),
    onSuccess: (atualizado) => queryClient.setQueryData(['compras-configuracao', empresaId], atualizado),
  })
}

// --- Lookups cross-serviço (Selects) ---

export function useBuscaProdutosParaSelecao(nome: string | undefined) {
  return useQuery({
    queryKey: ['produtos-para-selecao-compras', nome],
    queryFn: () => buscarProdutosParaSelecao(nome),
    enabled: nome !== undefined && nome.length >= 2,
  })
}

export function useProdutoUnidadesParaSelecao() {
  return useQuery({
    queryKey: ['produto-unidades-para-selecao-compras'],
    queryFn: () => buscarProdutoUnidadesParaSelecao(),
    staleTime: 5 * 60_000,
  })
}

export function useBuscaFornecedoresParaSelecao(nome: string | undefined) {
  return useQuery({
    queryKey: ['fornecedores-para-selecao-compras', nome],
    queryFn: () => buscarFornecedoresParaSelecao(nome),
    enabled: nome !== undefined && nome.length >= 2,
  })
}

/**
 * `retry` desliga especificamente pra 404 — `fornecedorId`/`colaboradorId` são
 * referências soltas cross-serviço (Compras não tem FK pro cadastro-base),
 * então um id "órfão" (dado de seed/teste que nunca existiu de fato no
 * cadastro-base) é uma condição permanente, não transitória. Mesmo padrão já
 * registrado em `useProdutoResumo` (achado real, 2026-09-02).
 */
export function useFornecedorResumo(id: number | undefined) {
  return useQuery({
    queryKey: ['fornecedor-resumo-compras', id],
    queryFn: () => buscarFornecedorResumo(id as number),
    enabled: id != null,
    staleTime: 5 * 60_000,
    retry: (failureCount, erro) => (erro as unknown as AppError).status !== 404 && failureCount < 1,
  })
}

export function useBuscaColaboradoresParaSelecao(nome: string | undefined) {
  return useQuery({
    queryKey: ['colaboradores-para-selecao-compras', nome],
    queryFn: () => buscarColaboradoresParaSelecao(nome),
    enabled: nome !== undefined && nome.length >= 2,
  })
}

export function useColaboradorResumo(id: number | undefined) {
  return useQuery({
    queryKey: ['colaborador-resumo-compras', id],
    queryFn: () => buscarColaboradorResumo(id as number),
    enabled: id != null,
    staleTime: 5 * 60_000,
    retry: (failureCount, erro) => (erro as unknown as AppError).status !== 404 && failureCount < 1,
  })
}

export function useBuscaEmpresasParaSelecao(nome: string | undefined) {
  return useQuery({
    queryKey: ['empresas-para-selecao-compras', nome],
    queryFn: () => buscarEmpresasParaSelecao(nome),
    enabled: nome !== undefined && nome.length >= 2,
  })
}

export function useDepositosAtivos(empresaId: number | undefined) {
  return useQuery({
    queryKey: ['depositos-ativos-compras', empresaId ?? -1],
    queryFn: () => listarDepositosAtivos(empresaId as number),
    enabled: empresaId !== undefined,
  })
}

export function useLotesParaSelecao(produtoId: number | undefined) {
  return useQuery({
    queryKey: ['lotes-para-selecao-compras', produtoId ?? -1],
    queryFn: () => listarLotesParaSelecao(produtoId),
    enabled: produtoId !== undefined,
  })
}
