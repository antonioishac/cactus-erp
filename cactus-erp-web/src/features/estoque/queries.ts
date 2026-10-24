import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  abrirInventario,
  adicionarComponenteKit,
  atualizarConfiguracaoEstoque,
  atualizarDeposito,
  atualizarLote,
  atualizarSituacaoNumeroSerie,
  baixarKit,
  buscarConfiguracaoEstoque,
  buscarDeposito,
  buscarInventario,
  buscarProdutosParaSelecao,
  buscarUsuariosParaSelecao,
  cadastrarNumeroSerie,
  buscarFichaMovimentacao,
  calcularCurvaAbc,
  cancelarInventario,
  cancelarTransferencia,
  criarDeposito,
  criarLote,
  criarTransferencia,
  enviarTransferencia,
  finalizarInventario,
  iniciarContagemInventario,
  lancarContagemInventario,
  listarComponentesKit,
  listarDepositos,
  listarDepositosAtivos,
  listarInventarios,
  listarLotes,
  listarLotesProximosDoVencimento,
  listarMovimentos,
  listarNumerosSerie,
  listarSaldos,
  listarTransferencias,
  receberTransferencia,
  registrarMovimento,
  removerComponenteKit,
} from './api'
import type {
  BaixarKitRequest,
  ConfiguracaoEstoqueEmpresaRequest,
  DepositoRequest,
  DepositosFiltro,
  EstoqueMovimentoRequest,
  InventarioFisicoAbrirRequest,
  InventarioFisicoContagemRequest,
  LoteRequest,
  MovimentosFiltro,
  NumeroSerieRequest,
  ProdutoKitComponenteRequest,
  SaldosFiltro,
  SituacaoNumeroSerie,
  TransferenciaEstoqueRequest,
} from './types'

const QUERY_KEY_DEPOSITOS = ['depositos'] as const
const queryKeyDeposito = (id: number) => ['depositos', id] as const
const QUERY_KEY_MOVIMENTOS = ['movimentos'] as const
const QUERY_KEY_SALDOS = ['saldos'] as const

// --- Depósito ---

export function useDepositos(filtro: DepositosFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_DEPOSITOS, filtro, pagina, tamanho],
    queryFn: () => listarDepositos(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useDepositosAtivos(empresaId: number | undefined) {
  return useQuery({
    queryKey: ['depositos-ativos', empresaId ?? -1],
    queryFn: () => listarDepositosAtivos(empresaId as number),
    enabled: empresaId !== undefined,
  })
}

export function useDeposito(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyDeposito(id ?? -1),
    queryFn: () => buscarDeposito(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarDeposito() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: DepositoRequest) => criarDeposito(request),
    onSuccess: (criado) => {
      queryClient.setQueryData(queryKeyDeposito(criado.id), criado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_DEPOSITOS })
      queryClient.invalidateQueries({ queryKey: ['depositos-ativos'] })
    },
  })
}

export function useAtualizarDeposito(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: DepositoRequest) => atualizarDeposito(id, request),
    onSuccess: (atualizado) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_DEPOSITOS })
      queryClient.invalidateQueries({ queryKey: ['depositos-ativos'] })
      queryClient.setQueryData(queryKeyDeposito(id), atualizado)
    },
  })
}

// --- Configuração de Estoque ---

export function useConfiguracaoEstoque(empresaId: number | undefined) {
  return useQuery({
    queryKey: ['configuracao-estoque', empresaId ?? -1],
    queryFn: () => buscarConfiguracaoEstoque(empresaId as number),
    enabled: empresaId !== undefined,
  })
}

export function useAtualizarConfiguracaoEstoque(empresaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ConfiguracaoEstoqueEmpresaRequest) => atualizarConfiguracaoEstoque(empresaId, request),
    onSuccess: (atualizado) => {
      queryClient.setQueryData(['configuracao-estoque', empresaId], atualizado)
    },
  })
}

// --- Saldo ---

export function useSaldos(filtro: SaldosFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_SALDOS, filtro, pagina, tamanho],
    queryFn: () => listarSaldos(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

// --- Movimento ---

export function useMovimentos(filtro: MovimentosFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_MOVIMENTOS, filtro, pagina, tamanho],
    queryFn: () => listarMovimentos(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useRegistrarMovimento() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: EstoqueMovimentoRequest) => registrarMovimento(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_MOVIMENTOS })
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_SALDOS })
    },
  })
}

// --- Transferência ---

export function useTransferencias(depositoId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: ['transferencias', depositoId ?? -1, pagina, tamanho],
    queryFn: () => listarTransferencias(depositoId as number, pagina, tamanho),
    enabled: depositoId !== undefined,
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useCriarTransferencia() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: TransferenciaEstoqueRequest) => criarTransferencia(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['transferencias'] }),
  })
}

export function useEnviarTransferencia() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => enviarTransferencia(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['transferencias'] })
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_SALDOS })
    },
  })
}

export function useReceberTransferencia() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => receberTransferencia(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['transferencias'] })
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_SALDOS })
    },
  })
}

export function useCancelarTransferencia() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => cancelarTransferencia(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['transferencias'] }),
  })
}

// --- Busca de produto (Select assíncrono) ---

export function useBuscaProdutosParaSelecao(nome: string | undefined) {
  return useQuery({
    queryKey: ['produtos-para-selecao-estoque', nome],
    queryFn: () => buscarProdutosParaSelecao(nome),
    enabled: nome !== undefined && nome.length >= 2,
  })
}

// --- Busca de usuário (Select assíncrono do aprovador) ---

export function useBuscaUsuariosParaSelecao(busca: string | undefined) {
  return useQuery({
    queryKey: ['usuarios-para-selecao-estoque', busca],
    queryFn: () => buscarUsuariosParaSelecao(busca),
    enabled: busca !== undefined && busca.length >= 2,
  })
}

// --- Inventário Físico (Fase B) ---

export function useInventarios(depositoId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: ['inventarios', depositoId ?? -1, pagina, tamanho],
    queryFn: () => listarInventarios(depositoId as number, pagina, tamanho),
    enabled: depositoId !== undefined,
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useInventario(id: number | undefined) {
  return useQuery({
    queryKey: ['inventarios', id ?? -1],
    queryFn: () => buscarInventario(id as number),
    enabled: id !== undefined,
  })
}

export function useAbrirInventario() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: InventarioFisicoAbrirRequest) => abrirInventario(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['inventarios'] }),
  })
}

export function useIniciarContagemInventario(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => iniciarContagemInventario(id),
    onSuccess: (atualizado) => queryClient.setQueryData(['inventarios', id], atualizado),
  })
}

export function useLancarContagemInventario(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ itemId, request }: { itemId: number; request: InventarioFisicoContagemRequest }) =>
      lancarContagemInventario(id, itemId, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['inventarios', id] }),
  })
}

export function useFinalizarInventario(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => finalizarInventario(id),
    onSuccess: (atualizado) => {
      queryClient.setQueryData(['inventarios', id], atualizado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_SALDOS })
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_MOVIMENTOS })
    },
  })
}

export function useCancelarInventario(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => cancelarInventario(id),
    onSuccess: (atualizado) => queryClient.setQueryData(['inventarios', id], atualizado),
  })
}

// --- Lote (Fase B) ---

export function useLotes(produtoId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: ['lotes', produtoId, pagina, tamanho],
    queryFn: () => listarLotes(produtoId, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

/** Só ativa com um produto selecionado — `vencimento-proximo` exige `produtoId` no path (ver `LoteController`). */
export function useLotesProximosDoVencimento(produtoId: number | undefined, dias: number, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: ['lotes-vencimento-proximo', produtoId ?? -1, dias, pagina, tamanho],
    queryFn: () => listarLotesProximosDoVencimento(produtoId as number, dias, pagina, tamanho),
    enabled: produtoId !== undefined,
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useCriarLote() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: LoteRequest) => criarLote(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['lotes'] }),
  })
}

export function useAtualizarLote() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: LoteRequest }) => atualizarLote(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['lotes'] }),
  })
}

// --- Número de Série (Fase B) ---

export function useNumerosSerie(produtoId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: ['numeros-serie', produtoId ?? -1, pagina, tamanho],
    queryFn: () => listarNumerosSerie(produtoId as number, pagina, tamanho),
    enabled: produtoId !== undefined,
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useCadastrarNumeroSerie() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: NumeroSerieRequest) => cadastrarNumeroSerie(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['numeros-serie'] }),
  })
}

export function useAtualizarSituacaoNumeroSerie() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, situacao, depositoId }: { id: number; situacao: SituacaoNumeroSerie; depositoId?: number }) =>
      atualizarSituacaoNumeroSerie(id, situacao, depositoId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['numeros-serie'] }),
  })
}

// --- Kit / Produto Composto (Fase B) ---

export function useComponentesKit(produtoKitId: number | undefined) {
  return useQuery({
    queryKey: ['kit-componentes', produtoKitId ?? -1],
    queryFn: () => listarComponentesKit(produtoKitId as number),
    enabled: produtoKitId !== undefined,
  })
}

export function useAdicionarComponenteKit(produtoKitId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ProdutoKitComponenteRequest) => adicionarComponenteKit(produtoKitId, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['kit-componentes', produtoKitId] }),
  })
}

export function useRemoverComponenteKit(produtoKitId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (componenteId: number) => removerComponenteKit(produtoKitId, componenteId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['kit-componentes', produtoKitId] }),
  })
}

export function useBaixarKit(produtoKitId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: BaixarKitRequest) => baixarKit(produtoKitId, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_SALDOS })
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_MOVIMENTOS })
    },
  })
}

// --- Curva ABC (Fase B, relatório) ---

export function useCurvaAbc(depositoId: number | undefined, dias: number, enabled: boolean) {
  return useQuery({
    queryKey: ['curva-abc', depositoId, dias],
    queryFn: () => calcularCurvaAbc(depositoId, dias),
    enabled,
  })
}

export function useFichaMovimentacao(
  produtoId: number | undefined,
  dataInicio: string | undefined,
  dataFim: string | undefined,
  depositoId: number | undefined,
) {
  return useQuery({
    queryKey: ['ficha-movimentacao-produto', produtoId, dataInicio, dataFim, depositoId],
    queryFn: () => buscarFichaMovimentacao(produtoId as number, dataInicio as string, dataFim as string, depositoId),
    enabled: !!produtoId && !!dataInicio && !!dataFim,
  })
}
