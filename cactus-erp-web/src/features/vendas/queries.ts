import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import type { AppError } from '@shared/api/problemDetail'
import {
  adicionarItemOrcamento,
  adicionarItemVenda,
  adicionarMetaVendedor,
  adicionarObjetivoComissaoPerfil,
  adicionarRotaVendedor,
  aprovarOrcamento,
  atualizarComissaoPerfil,
  atualizarCondicaoPagamento,
  atualizarItemVenda,
  atualizarNotaFiscalModelo,
  atualizarNotaFiscalTipo,
  atualizarVendedor,
  buscarClienteResumo,
  buscarClientesParaSelecao,
  buscarColaboradorResumo,
  buscarColaboradoresParaSelecao,
  buscarComissaoPerfil,
  buscarCondicaoPagamento,
  buscarFormasPagamentoParaSelecao,
  buscarFreteVenda,
  buscarOrcamento,
  buscarProdutosParaSelecao,
  buscarTransportadoraResumo,
  buscarTransportadorasParaSelecao,
  buscarVenda,
  buscarVendedor,
  cancelarOrcamento,
  cancelarVenda,
  confirmarVenda,
  criarComissaoPerfil,
  criarCondicaoPagamento,
  criarNotaFiscalModelo,
  criarNotaFiscalTipo,
  criarOrcamento,
  criarVenda,
  criarVendedor,
  definirFreteVenda,
  enviarOrcamento,
  expirarOrcamento,
  gerarVendaDeOrcamento,
  listarComissaoPerfis,
  listarComissoes,
  listarCondicoesPagamento,
  listarDepositosAtivos,
  listarMetasVendedor,
  listarNotasFiscaisModelos,
  listarNotasFiscaisTipos,
  listarObjetivosComissaoPerfil,
  listarOrcamentos,
  listarRotaVendedor,
  listarVendas,
  listarVendedores,
  removerItemOrcamento,
  removerItemVenda,
  removerMetaVendedor,
  removerObjetivoComissaoPerfil,
  removerRotaVendedor,
  buscarComissoesPorVendedor,
  buscarProdutosMaisVendidos,
  buscarVendasPorPeriodo,
} from './api'
import type {
  VendasPeriodoFiltro,
  ComissaoObjetivoRequest,
  ComissaoPerfilRequest,
  ComissoesFiltro,
  GerarVendaDeOrcamentoRequest,
  NotaFiscalModeloRequest,
  NotaFiscalTipoRequest,
  OrcamentosFiltro,
  VendaCondicoesPagamentoRequest,
  VendaFreteRequest,
  VendaItemRequest,
  VendaOrcamentoItemRequest,
  VendaOrcamentoRequest,
  VendaOrcamentoResponse,
  VendaRequest,
  VendasFiltro,
  VendedorMetaRequest,
  VendedorRequest,
  VendedorRotaRequest,
} from './types'

const QUERY_KEY_VENDEDORES = ['vendas-vendedores'] as const
const queryKeyVendedor = (id: number) => ['vendas-vendedores', id] as const
const QUERY_KEY_CONDICOES = ['vendas-condicoes-pagamento'] as const
const queryKeyCondicao = (id: number) => ['vendas-condicoes-pagamento', id] as const
const QUERY_KEY_ORCAMENTOS = ['vendas-orcamentos'] as const
const queryKeyOrcamento = (id: number) => ['vendas-orcamentos', id] as const
const QUERY_KEY_VENDAS = ['vendas-vendas'] as const
const queryKeyVenda = (id: number) => ['vendas-vendas', id] as const

// --- Vendedores ---

export function useVendedores(pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_VENDEDORES, pagina, tamanho],
    queryFn: () => listarVendedores(pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useVendedor(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyVendedor(id ?? -1),
    queryFn: () => buscarVendedor(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarVendedor() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: VendedorRequest) => criarVendedor(request),
    onSuccess: (criado) => {
      queryClient.setQueryData(queryKeyVendedor(criado.id), criado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_VENDEDORES })
    },
  })
}

export function useAtualizarVendedor(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: VendedorRequest) => atualizarVendedor(id, request),
    onSuccess: (atualizado) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_VENDEDORES })
      queryClient.setQueryData(queryKeyVendedor(id), atualizado)
    },
  })
}

// --- AFV: rota e metas do vendedor ---

const queryKeyRotaVendedor = (vendedorId: number) => ['vendas-vendedor-rota', vendedorId] as const
const queryKeyMetasVendedor = (vendedorId: number) => ['vendas-vendedor-metas', vendedorId] as const

export function useRotaVendedor(vendedorId: number) {
  return useQuery({
    queryKey: queryKeyRotaVendedor(vendedorId),
    queryFn: () => listarRotaVendedor(vendedorId),
    enabled: vendedorId > 0,
  })
}

export function useAdicionarRotaVendedor(vendedorId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: VendedorRotaRequest) => adicionarRotaVendedor(vendedorId, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyRotaVendedor(vendedorId) }),
  })
}

export function useRemoverRotaVendedor(vendedorId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (rotaId: number) => removerRotaVendedor(vendedorId, rotaId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyRotaVendedor(vendedorId) }),
  })
}

export function useMetasVendedor(vendedorId: number) {
  return useQuery({
    queryKey: queryKeyMetasVendedor(vendedorId),
    queryFn: () => listarMetasVendedor(vendedorId),
    enabled: vendedorId > 0,
  })
}

export function useAdicionarMetaVendedor(vendedorId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: VendedorMetaRequest) => adicionarMetaVendedor(vendedorId, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyMetasVendedor(vendedorId) }),
  })
}

export function useRemoverMetaVendedor(vendedorId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (metaId: number) => removerMetaVendedor(vendedorId, metaId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyMetasVendedor(vendedorId) }),
  })
}

// --- Perfis de Comissão ---

export function useComissaoPerfis() {
  return useQuery({
    queryKey: ['vendas-comissao-perfis'],
    queryFn: () => listarComissaoPerfis(),
    staleTime: 60_000,
  })
}

export function useCriarComissaoPerfil() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ComissaoPerfilRequest) => criarComissaoPerfil(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['vendas-comissao-perfis'] }),
  })
}

export function useComissaoPerfil(id: number | undefined) {
  return useQuery({
    queryKey: ['vendas-comissao-perfis', id],
    queryFn: () => buscarComissaoPerfil(id as number),
    enabled: id !== undefined,
  })
}

export function useAtualizarComissaoPerfil() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: ComissaoPerfilRequest }) => atualizarComissaoPerfil(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['vendas-comissao-perfis'] }),
  })
}

// --- Comissão por Objetivo (sub-recurso do Perfil de Comissão) ---

const queryKeyObjetivosComissaoPerfil = (comissaoPerfilId: number) => ['vendas-comissao-objetivos', comissaoPerfilId] as const

export function useObjetivosComissaoPerfil(comissaoPerfilId: number) {
  return useQuery({
    queryKey: queryKeyObjetivosComissaoPerfil(comissaoPerfilId),
    queryFn: () => listarObjetivosComissaoPerfil(comissaoPerfilId),
    enabled: comissaoPerfilId > 0,
  })
}

export function useAdicionarObjetivoComissaoPerfil(comissaoPerfilId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ComissaoObjetivoRequest) => adicionarObjetivoComissaoPerfil(comissaoPerfilId, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyObjetivosComissaoPerfil(comissaoPerfilId) }),
  })
}

export function useRemoverObjetivoComissaoPerfil(comissaoPerfilId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (objetivoId: number) => removerObjetivoComissaoPerfil(comissaoPerfilId, objetivoId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyObjetivosComissaoPerfil(comissaoPerfilId) }),
  })
}

// --- Condições de Pagamento ---

export function useCondicoesPagamento(pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_CONDICOES, pagina, tamanho],
    queryFn: () => listarCondicoesPagamento(pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useCondicaoPagamento(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyCondicao(id ?? -1),
    queryFn: () => buscarCondicaoPagamento(id as number),
    enabled: id !== undefined,
  })
}

export function useTodasCondicoesPagamento() {
  return useQuery({
    queryKey: ['vendas-condicoes-pagamento-todas'],
    queryFn: () => listarCondicoesPagamento(0, 100),
    staleTime: 60_000,
  })
}

export function useCriarCondicaoPagamento() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: VendaCondicoesPagamentoRequest) => criarCondicaoPagamento(request),
    onSuccess: (criada) => {
      queryClient.setQueryData(queryKeyCondicao(criada.id), criada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONDICOES })
      queryClient.invalidateQueries({ queryKey: ['vendas-condicoes-pagamento-todas'] })
    },
  })
}

export function useAtualizarCondicaoPagamento(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: VendaCondicoesPagamentoRequest) => atualizarCondicaoPagamento(id, request),
    onSuccess: (atualizada) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONDICOES })
      queryClient.invalidateQueries({ queryKey: ['vendas-condicoes-pagamento-todas'] })
      queryClient.setQueryData(queryKeyCondicao(id), atualizada)
    },
  })
}

// --- Notas Fiscais: Modelo/Tipo ---

export function useNotasFiscaisModelos() {
  return useQuery({
    queryKey: ['vendas-nf-modelos'],
    queryFn: () => listarNotasFiscaisModelos(),
    staleTime: 60_000,
  })
}

export function useCriarNotaFiscalModelo() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: NotaFiscalModeloRequest) => criarNotaFiscalModelo(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['vendas-nf-modelos'] }),
  })
}

export function useAtualizarNotaFiscalModelo() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: NotaFiscalModeloRequest }) => atualizarNotaFiscalModelo(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['vendas-nf-modelos'] }),
  })
}

export function useNotasFiscaisTipos() {
  return useQuery({
    queryKey: ['vendas-nf-tipos'],
    queryFn: () => listarNotasFiscaisTipos(),
    staleTime: 60_000,
  })
}

export function useCriarNotaFiscalTipo() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: NotaFiscalTipoRequest) => criarNotaFiscalTipo(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['vendas-nf-tipos'] }),
  })
}

export function useAtualizarNotaFiscalTipo() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: NotaFiscalTipoRequest }) => atualizarNotaFiscalTipo(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['vendas-nf-tipos'] }),
  })
}

// --- Orçamento ---

export function useOrcamentos(filtro: OrcamentosFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_ORCAMENTOS, filtro, pagina, tamanho],
    queryFn: () => listarOrcamentos(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useOrcamento(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyOrcamento(id ?? -1),
    queryFn: () => buscarOrcamento(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarOrcamento() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: VendaOrcamentoRequest) => criarOrcamento(request),
    onSuccess: (criado) => {
      queryClient.setQueryData(queryKeyOrcamento(criado.id), criado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_ORCAMENTOS })
    },
  })
}

export function useAdicionarItemOrcamento(orcamentoId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: VendaOrcamentoItemRequest) => adicionarItemOrcamento(orcamentoId, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyOrcamento(orcamentoId) }),
  })
}

export function useRemoverItemOrcamento(orcamentoId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (itemId: number) => removerItemOrcamento(orcamentoId, itemId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyOrcamento(orcamentoId) }),
  })
}

function useTransicaoOrcamento(id: number, transicao: (id: number) => Promise<VendaOrcamentoResponse>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => transicao(id),
    onSuccess: (atualizado) => {
      queryClient.setQueryData(queryKeyOrcamento(id), atualizado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_ORCAMENTOS })
    },
  })
}

export function useEnviarOrcamento(id: number) {
  return useTransicaoOrcamento(id, enviarOrcamento)
}

export function useAprovarOrcamento(id: number) {
  return useTransicaoOrcamento(id, aprovarOrcamento)
}

export function useExpirarOrcamento(id: number) {
  return useTransicaoOrcamento(id, expirarOrcamento)
}

export function useCancelarOrcamento(id: number) {
  return useTransicaoOrcamento(id, cancelarOrcamento)
}

export function useGerarVendaDeOrcamento(orcamentoId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: GerarVendaDeOrcamentoRequest) => gerarVendaDeOrcamento(orcamentoId, request),
    onSuccess: (vendaCriada) => {
      queryClient.setQueryData(queryKeyVenda(vendaCriada.id), vendaCriada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_VENDAS })
      queryClient.invalidateQueries({ queryKey: queryKeyOrcamento(orcamentoId) })
    },
  })
}

// --- Venda ---

export function useVendas(filtro: VendasFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_VENDAS, filtro, pagina, tamanho],
    queryFn: () => listarVendas(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useVenda(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyVenda(id ?? -1),
    queryFn: () => buscarVenda(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarVenda() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: VendaRequest) => criarVenda(request),
    onSuccess: (criada) => {
      queryClient.setQueryData(queryKeyVenda(criada.id), criada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_VENDAS })
    },
  })
}

export function useAdicionarItemVenda(vendaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: VendaItemRequest) => adicionarItemVenda(vendaId, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyVenda(vendaId) }),
  })
}

export function useAtualizarItemVenda(vendaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ itemId, request }: { itemId: number; request: VendaItemRequest }) => atualizarItemVenda(vendaId, itemId, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyVenda(vendaId) }),
  })
}

export function useRemoverItemVenda(vendaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (itemId: number) => removerItemVenda(vendaId, itemId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyVenda(vendaId) }),
  })
}

export function useFreteVenda(vendaId: number | undefined) {
  return useQuery({
    queryKey: ['vendas-frete', vendaId ?? -1],
    queryFn: () => buscarFreteVenda(vendaId as number),
    enabled: vendaId !== undefined,
  })
}

export function useDefinirFreteVenda(vendaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: VendaFreteRequest) => definirFreteVenda(vendaId, request),
    onSuccess: (atualizado) => queryClient.setQueryData(['vendas-frete', vendaId], atualizado),
  })
}

export function useConfirmarVenda(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => confirmarVenda(id),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyVenda(id), atualizada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_VENDAS })
    },
  })
}

export function useCancelarVenda(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => cancelarVenda(id),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyVenda(id), atualizada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_VENDAS })
    },
  })
}

// --- Comissões ---

export function useComissoes(filtro: ComissoesFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: ['vendas-comissoes', filtro, pagina, tamanho],
    queryFn: () => listarComissoes(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

// --- Lookups cross-serviço (Selects) ---

export function useBuscaProdutosParaSelecao(nome: string | undefined) {
  return useQuery({
    queryKey: ['produtos-para-selecao-vendas', nome],
    queryFn: () => buscarProdutosParaSelecao(nome),
    enabled: nome !== undefined && nome.length >= 2,
  })
}

export function useBuscaClientesParaSelecao(nome: string | undefined) {
  return useQuery({
    queryKey: ['clientes-para-selecao-vendas', nome],
    queryFn: () => buscarClientesParaSelecao(nome),
    enabled: nome !== undefined && nome.length >= 2,
  })
}

export function useClienteResumo(id: number | undefined) {
  return useQuery({
    queryKey: ['cliente-resumo-vendas', id],
    queryFn: () => buscarClienteResumo(id as number),
    enabled: id != null,
    staleTime: 5 * 60_000,
    retry: (failureCount, erro) => (erro as unknown as AppError).status !== 404 && failureCount < 1,
  })
}

export function useBuscaColaboradoresParaSelecao(nome: string | undefined) {
  return useQuery({
    queryKey: ['colaboradores-para-selecao-vendas', nome],
    queryFn: () => buscarColaboradoresParaSelecao(nome),
    enabled: nome !== undefined && nome.length >= 2,
  })
}

export function useColaboradorResumo(id: number | undefined) {
  return useQuery({
    queryKey: ['colaborador-resumo-vendas', id],
    queryFn: () => buscarColaboradorResumo(id as number),
    enabled: id != null,
    staleTime: 5 * 60_000,
    retry: (failureCount, erro) => (erro as unknown as AppError).status !== 404 && failureCount < 1,
  })
}

export function useBuscaTransportadorasParaSelecao(nome: string | undefined) {
  return useQuery({
    queryKey: ['transportadoras-para-selecao-vendas', nome],
    queryFn: () => buscarTransportadorasParaSelecao(nome),
    enabled: nome !== undefined && nome.length >= 2,
  })
}

export function useTransportadoraResumo(id: number | undefined) {
  return useQuery({
    queryKey: ['transportadora-resumo-vendas', id],
    queryFn: () => buscarTransportadoraResumo(id as number),
    enabled: id != null,
    staleTime: 5 * 60_000,
    retry: (failureCount, erro) => (erro as unknown as AppError).status !== 404 && failureCount < 1,
  })
}

export function useFormasPagamentoParaSelecao() {
  return useQuery({
    queryKey: ['formas-pagamento-para-selecao-vendas'],
    queryFn: () => buscarFormasPagamentoParaSelecao(),
    staleTime: 5 * 60_000,
  })
}

export function useDepositosAtivos(empresaId: number | undefined) {
  return useQuery({
    queryKey: ['depositos-ativos-vendas', empresaId ?? -1],
    queryFn: () => listarDepositosAtivos(empresaId as number),
    enabled: empresaId !== undefined,
  })
}

// --- Relatórios ---

export function useVendasPorPeriodo(filtro: VendasPeriodoFiltro, enabled: boolean) {
  return useQuery({
    queryKey: ['relatorio-vendas-periodo', filtro],
    queryFn: () => buscarVendasPorPeriodo(filtro),
    enabled,
  })
}

export function useProdutosMaisVendidos(dataInicio: string, dataFim: string, enabled: boolean) {
  return useQuery({
    queryKey: ['relatorio-produtos-mais-vendidos', dataInicio, dataFim],
    queryFn: () => buscarProdutosMaisVendidos(dataInicio, dataFim),
    enabled,
  })
}

export function useComissoesPorVendedor(dataInicio: string, dataFim: string, enabled: boolean) {
  return useQuery({
    queryKey: ['relatorio-comissoes-vendedor', dataInicio, dataFim],
    queryFn: () => buscarComissoesPorVendedor(dataInicio, dataFim),
    enabled,
  })
}
