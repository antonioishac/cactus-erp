import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import type { AppError } from '@shared/api/problemDetail'
import {
  aplicarFinanceiro,
  atualizarClassificacaoContaPagar,
  atualizarClassificacaoContaReceber,
  atualizarConfiguracaoFinanceiro,
  atualizarContaBancaria,
  atualizarNaturezaFinanceira,
  baixarArquivoRemessa,
  buscarClienteResumo,
  buscarClientesParaSelecao,
  buscarConfiguracaoFinanceiro,
  buscarContaBancaria,
  buscarContaPagar,
  buscarContaReceber,
  buscarFluxoCaixa,
  buscarFormasPagamentoParaSelecao,
  buscarFornecedorResumo,
  buscarFornecedoresParaSelecao,
  buscarPendenciasConciliacao,
  buscarPosicaoCaixa,
  buscarRemessaBancaria,
  cancelarChequeEmitido,
  cancelarContaPagar,
  cancelarContaReceber,
  compensarChequeEmitido,
  compensarChequeRecebido,
  conciliarAutomatico,
  conciliarManual,
  criarChequeEmitido,
  criarChequeRecebido,
  criarContaBancaria,
  criarContaPagar,
  criarContaReceber,
  criarNaturezaFinanceira,
  criarTipoDocumentoFinanceiro,
  depositarChequeRecebido,
  desconciliarItem,
  descontarChequeRecebido,
  devolverChequeEmitido,
  devolverChequeRecebido,
  estornarBaixaContaPagar,
  estornarBaixaContaReceber,
  gerarAjusteConciliacao,
  gerarContaPagarParcelada,
  gerarContaReceberParcelada,
  gerarRemessaBancaria,
  importarExtratoOfx,
  importarRetornoBancario,
  inativarContaBancaria,
  inativarNaturezaFinanceira,
  inativarTipoDocumentoFinanceiro,
  listarAplicacoesFinanceiras,
  listarBancos,
  listarChequesEmitidos,
  listarChequesRecebidos,
  listarContasBancarias,
  listarContasBancariasAtivas,
  listarContasPagar,
  listarContasReceber,
  listarImportacoesExtrato,
  listarItensImportacao,
  listarMovimentosCaixaBanco,
  listarNaturezasFinanceiras,
  listarRemessasBancarias,
  listarRetornosBancarios,
  listarTiposDocumentoFinanceiro,
  listarTransferenciasBancarias,
  reativarContaBancaria,
  registrarBaixaContaPagar,
  registrarBaixaContaReceber,
  registrarMovimentoManual,
  renegociarContaPagar,
  renegociarContaReceber,
  resgatarAplicacao,
  transferirEntreContas,
} from './api'
import type {
  AplicacaoFinanceiraRequest,
  AtualizarClassificacaoTituloRequest,
  BaixaRequest,
  CancelarChequeEmitidoRequest,
  CancelarTituloRequest,
  ChequeEmitidoRequest,
  ChequeRecebidoRequest,
  CompensarChequeEmitidoRequest,
  CompensarChequeRecebidoRequest,
  ConciliarManualRequest,
  ConfiguracaoFinanceiroEmpresaRequest,
  ContaBancariaRequest,
  ContaPagarRequest,
  ContaReceberRequest,
  ContasPagarFiltro,
  ContasReceberFiltro,
  DepositarChequeRecebidoRequest,
  DescontarChequeRecebidoRequest,
  DevolverChequeRequest,
  EstornarBaixaRequest,
  GerarAjusteConciliacaoRequest,
  GerarContaPagarParceladaRequest,
  GerarContaReceberParceladaRequest,
  GerarRemessaRequest,
  MovimentoCaixaBancoRequest,
  NaturezaFinanceiraRequest,
  RenegociarTituloRequest,
  ResgatarAplicacaoRequest,
  TipoDocumentoFinanceiroRequest,
  TransferenciaBancariaRequest,
} from './types'

const QUERY_KEY_CONTAS_RECEBER = ['financeiro-contas-receber'] as const
const queryKeyContaReceber = (id: number) => ['financeiro-contas-receber', id] as const
const QUERY_KEY_CONTAS_PAGAR = ['financeiro-contas-pagar'] as const
const queryKeyContaPagar = (id: number) => ['financeiro-contas-pagar', id] as const
const queryKeyNaturezas = (empresaId: number) => ['financeiro-naturezas', empresaId] as const
const QUERY_KEY_TIPOS_DOCUMENTO = ['financeiro-tipos-documento'] as const
const queryKeyConfiguracao = (empresaId: number) => ['financeiro-configuracao', empresaId] as const
const queryKeyContasBancariasAtivas = (empresaId: number) => ['financeiro-contas-bancarias-ativas', empresaId] as const
const QUERY_KEY_CONTAS_BANCARIAS = ['financeiro-contas-bancarias'] as const
const queryKeyContaBancaria = (id: number) => ['financeiro-contas-bancarias', id] as const
const QUERY_KEY_BANCOS = ['financeiro-bancos'] as const
const QUERY_KEY_MOVIMENTOS = ['financeiro-movimentos-caixa-banco'] as const
const QUERY_KEY_CHEQUES_EMITIDOS = ['financeiro-cheques-emitidos'] as const
const QUERY_KEY_CHEQUES_RECEBIDOS = ['financeiro-cheques-recebidos'] as const
const QUERY_KEY_TRANSFERENCIAS = ['financeiro-transferencias'] as const
const QUERY_KEY_APLICACOES = ['financeiro-aplicacoes'] as const
const queryKeyPosicaoCaixa = (empresaId: number) => ['financeiro-posicao-caixa', empresaId] as const
const queryKeyPendenciasConciliacao = (contaBancariaId: number) => ['financeiro-pendencias-conciliacao', contaBancariaId] as const
const QUERY_KEY_IMPORTACOES_EXTRATO = ['financeiro-importacoes-extrato'] as const
const QUERY_KEY_REMESSAS = ['financeiro-remessas'] as const
const queryKeyRemessa = (id: number) => ['financeiro-remessas', id] as const
const QUERY_KEY_RETORNOS = ['financeiro-retornos'] as const

const NAO_REPETIR_404 = (failureCount: number, erro: unknown) => (erro as AppError).status !== 404 && failureCount < 1

// --- Contas a Receber ---

export function useContasReceber(filtro: ContasReceberFiltro, empresaId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_CONTAS_RECEBER, filtro, empresaId, pagina, tamanho],
    queryFn: () => listarContasReceber(filtro, empresaId, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useContaReceber(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyContaReceber(id ?? -1),
    queryFn: () => buscarContaReceber(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarContaReceber() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ContaReceberRequest) => criarContaReceber(request),
    onSuccess: (criada) => {
      queryClient.setQueryData(queryKeyContaReceber(criada.id), criada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_RECEBER })
    },
  })
}

export function useGerarContaReceberParcelada() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: GerarContaReceberParceladaRequest) => gerarContaReceberParcelada(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_RECEBER }),
  })
}

export function useAtualizarClassificacaoContaReceber(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: AtualizarClassificacaoTituloRequest) => atualizarClassificacaoContaReceber(id, request),
    onSuccess: (atualizada) => queryClient.setQueryData(queryKeyContaReceber(id), atualizada),
  })
}

export function useRegistrarBaixaContaReceber(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: BaixaRequest) => registrarBaixaContaReceber(id, request),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyContaReceber(id), atualizada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_RECEBER })
    },
  })
}

export function useEstornarBaixaContaReceber(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ baixaId, request }: { baixaId: number; request: EstornarBaixaRequest }) => estornarBaixaContaReceber(id, baixaId, request),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyContaReceber(id), atualizada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_RECEBER })
    },
  })
}

export function useCancelarContaReceber(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: CancelarTituloRequest) => cancelarContaReceber(id, request),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyContaReceber(id), atualizada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_RECEBER })
    },
  })
}

export function useRenegociarContaReceber(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: RenegociarTituloRequest) => renegociarContaReceber(id, request),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyContaReceber(id), atualizada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_RECEBER })
    },
  })
}

// --- Contas a Pagar (mesmo molde de Contas a Receber) ---

export function useContasPagar(filtro: ContasPagarFiltro, empresaId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_CONTAS_PAGAR, filtro, empresaId, pagina, tamanho],
    queryFn: () => listarContasPagar(filtro, empresaId, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useContaPagar(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyContaPagar(id ?? -1),
    queryFn: () => buscarContaPagar(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarContaPagar() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ContaPagarRequest) => criarContaPagar(request),
    onSuccess: (criada) => {
      queryClient.setQueryData(queryKeyContaPagar(criada.id), criada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_PAGAR })
    },
  })
}

export function useGerarContaPagarParcelada() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: GerarContaPagarParceladaRequest) => gerarContaPagarParcelada(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_PAGAR }),
  })
}

export function useAtualizarClassificacaoContaPagar(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: AtualizarClassificacaoTituloRequest) => atualizarClassificacaoContaPagar(id, request),
    onSuccess: (atualizada) => queryClient.setQueryData(queryKeyContaPagar(id), atualizada),
  })
}

export function useRegistrarBaixaContaPagar(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: BaixaRequest) => registrarBaixaContaPagar(id, request),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyContaPagar(id), atualizada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_PAGAR })
    },
  })
}

export function useEstornarBaixaContaPagar(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ baixaId, request }: { baixaId: number; request: EstornarBaixaRequest }) => estornarBaixaContaPagar(id, baixaId, request),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyContaPagar(id), atualizada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_PAGAR })
    },
  })
}

export function useCancelarContaPagar(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: CancelarTituloRequest) => cancelarContaPagar(id, request),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyContaPagar(id), atualizada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_PAGAR })
    },
  })
}

export function useRenegociarContaPagar(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: RenegociarTituloRequest) => renegociarContaPagar(id, request),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyContaPagar(id), atualizada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_PAGAR })
    },
  })
}

// --- Natureza Financeira ---

export function useNaturezasFinanceiras(empresaId: number | undefined) {
  return useQuery({
    queryKey: queryKeyNaturezas(empresaId ?? -1),
    queryFn: () => listarNaturezasFinanceiras(empresaId as number),
    enabled: empresaId !== undefined,
  })
}

export function useCriarNaturezaFinanceira(empresaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: NaturezaFinanceiraRequest) => criarNaturezaFinanceira(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyNaturezas(empresaId) }),
  })
}

export function useAtualizarNaturezaFinanceira(empresaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: NaturezaFinanceiraRequest }) => atualizarNaturezaFinanceira(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyNaturezas(empresaId) }),
  })
}

export function useInativarNaturezaFinanceira(empresaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => inativarNaturezaFinanceira(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyNaturezas(empresaId) }),
  })
}

// --- Tipo de Documento Financeiro (catálogo GLOBAL) ---

export function useTiposDocumentoFinanceiro() {
  return useQuery({
    queryKey: QUERY_KEY_TIPOS_DOCUMENTO,
    queryFn: () => listarTiposDocumentoFinanceiro(),
  })
}

export function useCriarTipoDocumentoFinanceiro() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: TipoDocumentoFinanceiroRequest) => criarTipoDocumentoFinanceiro(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_TIPOS_DOCUMENTO }),
  })
}

export function useInativarTipoDocumentoFinanceiro() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => inativarTipoDocumentoFinanceiro(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_TIPOS_DOCUMENTO }),
  })
}

// --- Configuração Financeira ---

export function useConfiguracaoFinanceiro(empresaId: number | undefined) {
  return useQuery({
    queryKey: queryKeyConfiguracao(empresaId ?? -1),
    queryFn: () => buscarConfiguracaoFinanceiro(empresaId as number),
    enabled: empresaId !== undefined,
  })
}

export function useAtualizarConfiguracaoFinanceiro(empresaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ConfiguracaoFinanceiroEmpresaRequest) => atualizarConfiguracaoFinanceiro(empresaId, request),
    onSuccess: (atualizada) => queryClient.setQueryData(queryKeyConfiguracao(empresaId), atualizada),
  })
}

// --- Contas Bancárias (só o lookup ativas) ---

export function useContasBancariasAtivas(empresaId: number | undefined) {
  return useQuery({
    queryKey: queryKeyContasBancariasAtivas(empresaId ?? -1),
    queryFn: () => listarContasBancariasAtivas(empresaId as number),
    enabled: empresaId !== undefined,
  })
}

// --- Lookups cross-serviço (Selects) ---

export function useBuscaClientesParaSelecao(nome: string | undefined) {
  return useQuery({
    queryKey: ['clientes-para-selecao-financeiro', nome],
    queryFn: () => buscarClientesParaSelecao(nome),
    enabled: nome !== undefined && nome.length >= 2,
  })
}

export function useClienteResumo(id: number | undefined) {
  return useQuery({
    queryKey: ['cliente-resumo-financeiro', id],
    queryFn: () => buscarClienteResumo(id as number),
    enabled: id != null,
    staleTime: 5 * 60_000,
    retry: NAO_REPETIR_404,
  })
}

export function useBuscaFornecedoresParaSelecao(nome: string | undefined) {
  return useQuery({
    queryKey: ['fornecedores-para-selecao-financeiro', nome],
    queryFn: () => buscarFornecedoresParaSelecao(nome),
    enabled: nome !== undefined && nome.length >= 2,
  })
}

export function useFornecedorResumo(id: number | undefined) {
  return useQuery({
    queryKey: ['fornecedor-resumo-financeiro', id],
    queryFn: () => buscarFornecedorResumo(id as number),
    enabled: id != null,
    staleTime: 5 * 60_000,
    retry: NAO_REPETIR_404,
  })
}

export function useFormasPagamentoParaSelecao() {
  return useQuery({
    queryKey: ['formas-pagamento-para-selecao-financeiro'],
    queryFn: () => buscarFormasPagamentoParaSelecao(),
    staleTime: 5 * 60_000,
  })
}

// --- Contas Bancárias (Fase B) ---

export function useContasBancarias(empresaId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_CONTAS_BANCARIAS, empresaId, pagina, tamanho],
    queryFn: () => listarContasBancarias(empresaId, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useContaBancaria(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyContaBancaria(id ?? -1),
    queryFn: () => buscarContaBancaria(id as number),
    enabled: id !== undefined,
  })
}

function invalidarContasBancarias(queryClient: ReturnType<typeof useQueryClient>, empresaId: number | undefined) {
  queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_BANCARIAS })
  if (empresaId !== undefined) queryClient.invalidateQueries({ queryKey: queryKeyContasBancariasAtivas(empresaId) })
}

export function useCriarContaBancaria() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ContaBancariaRequest) => criarContaBancaria(request),
    onSuccess: (criada) => {
      queryClient.setQueryData(queryKeyContaBancaria(criada.id), criada)
      invalidarContasBancarias(queryClient, criada.empresaId)
    },
  })
}

export function useAtualizarContaBancaria(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ContaBancariaRequest) => atualizarContaBancaria(id, request),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyContaBancaria(id), atualizada)
      invalidarContasBancarias(queryClient, atualizada.empresaId)
    },
  })
}

export function useInativarContaBancaria() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => inativarContaBancaria(id),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyContaBancaria(atualizada.id), atualizada)
      invalidarContasBancarias(queryClient, atualizada.empresaId)
    },
  })
}

export function useReativarContaBancaria() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => reativarContaBancaria(id),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyContaBancaria(atualizada.id), atualizada)
      invalidarContasBancarias(queryClient, atualizada.empresaId)
    },
  })
}

export function useBancos() {
  return useQuery({
    queryKey: QUERY_KEY_BANCOS,
    queryFn: () => listarBancos(),
    staleTime: 60 * 60_000,
  })
}

// --- Movimento de Caixa e Banco ---

export function useMovimentosCaixaBanco(contaBancariaId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_MOVIMENTOS, contaBancariaId, pagina, tamanho],
    queryFn: () => listarMovimentosCaixaBanco(contaBancariaId, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useRegistrarMovimentoManual() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: MovimentoCaixaBancoRequest) => registrarMovimentoManual(request),
    onSuccess: (movimento) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_MOVIMENTOS })
      queryClient.invalidateQueries({ queryKey: queryKeyContaBancaria(movimento.contaBancariaId) })
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_BANCARIAS })
    },
  })
}

// --- Cheque Emitido ---

export function useChequesEmitidos(contaBancariaId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_CHEQUES_EMITIDOS, contaBancariaId, pagina, tamanho],
    queryFn: () => listarChequesEmitidos(contaBancariaId, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

function invalidarChequesEmitidos(queryClient: ReturnType<typeof useQueryClient>) {
  queryClient.invalidateQueries({ queryKey: QUERY_KEY_CHEQUES_EMITIDOS })
  queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_BANCARIAS })
  queryClient.invalidateQueries({ queryKey: QUERY_KEY_MOVIMENTOS })
}

export function useCriarChequeEmitido() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ChequeEmitidoRequest) => criarChequeEmitido(request),
    onSuccess: () => invalidarChequesEmitidos(queryClient),
  })
}

export function useCompensarChequeEmitido() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: CompensarChequeEmitidoRequest }) => compensarChequeEmitido(id, request),
    onSuccess: () => invalidarChequesEmitidos(queryClient),
  })
}

export function useDevolverChequeEmitido() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: DevolverChequeRequest }) => devolverChequeEmitido(id, request),
    onSuccess: () => invalidarChequesEmitidos(queryClient),
  })
}

export function useCancelarChequeEmitido() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: CancelarChequeEmitidoRequest }) => cancelarChequeEmitido(id, request),
    onSuccess: () => invalidarChequesEmitidos(queryClient),
  })
}

// --- Cheque Recebido ---

export function useChequesRecebidos(clienteId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_CHEQUES_RECEBIDOS, clienteId, pagina, tamanho],
    queryFn: () => listarChequesRecebidos(clienteId, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

function invalidarChequesRecebidos(queryClient: ReturnType<typeof useQueryClient>) {
  queryClient.invalidateQueries({ queryKey: QUERY_KEY_CHEQUES_RECEBIDOS })
  queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_BANCARIAS })
  queryClient.invalidateQueries({ queryKey: QUERY_KEY_MOVIMENTOS })
}

export function useCriarChequeRecebido() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ChequeRecebidoRequest) => criarChequeRecebido(request),
    onSuccess: () => invalidarChequesRecebidos(queryClient),
  })
}

export function useDepositarChequeRecebido() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: DepositarChequeRecebidoRequest }) => depositarChequeRecebido(id, request),
    onSuccess: () => invalidarChequesRecebidos(queryClient),
  })
}

export function useCompensarChequeRecebido() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: CompensarChequeRecebidoRequest }) => compensarChequeRecebido(id, request),
    onSuccess: () => invalidarChequesRecebidos(queryClient),
  })
}

export function useDescontarChequeRecebido() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: DescontarChequeRecebidoRequest }) => descontarChequeRecebido(id, request),
    onSuccess: () => invalidarChequesRecebidos(queryClient),
  })
}

export function useDevolverChequeRecebido() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: DevolverChequeRequest }) => devolverChequeRecebido(id, request),
    onSuccess: () => invalidarChequesRecebidos(queryClient),
  })
}

// --- Tesouraria: Transferência Bancária ---

export function useTransferenciasBancarias(contaBancariaId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_TRANSFERENCIAS, contaBancariaId, pagina, tamanho],
    queryFn: () => listarTransferenciasBancarias(contaBancariaId, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useTransferirEntreContas() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: TransferenciaBancariaRequest) => transferirEntreContas(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_TRANSFERENCIAS })
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_BANCARIAS })
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_MOVIMENTOS })
    },
  })
}

// --- Tesouraria: Aplicação Financeira ---

export function useAplicacoesFinanceiras(contaBancariaId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_APLICACOES, contaBancariaId, pagina, tamanho],
    queryFn: () => listarAplicacoesFinanceiras(contaBancariaId, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

function invalidarAplicacoes(queryClient: ReturnType<typeof useQueryClient>) {
  queryClient.invalidateQueries({ queryKey: QUERY_KEY_APLICACOES })
  queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_BANCARIAS })
  queryClient.invalidateQueries({ queryKey: QUERY_KEY_MOVIMENTOS })
}

export function useAplicarFinanceiro() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: AplicacaoFinanceiraRequest) => aplicarFinanceiro(request),
    onSuccess: () => invalidarAplicacoes(queryClient),
  })
}

export function useResgatarAplicacao() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: ResgatarAplicacaoRequest }) => resgatarAplicacao(id, request),
    onSuccess: () => invalidarAplicacoes(queryClient),
  })
}

// --- Tesouraria: Posição de Caixa ---

export function usePosicaoCaixa(empresaId: number | undefined) {
  return useQuery({
    queryKey: queryKeyPosicaoCaixa(empresaId ?? -1),
    queryFn: () => buscarPosicaoCaixa(empresaId as number),
    enabled: empresaId !== undefined,
  })
}

// --- Relatório: Fluxo de Caixa projetado ---

export function useFluxoCaixa(
  empresaId: number | undefined,
  dataInicio: string | undefined,
  dataFim: string | undefined,
  agrupamento: 'DIA' | 'MES',
) {
  return useQuery({
    queryKey: ['financeiro-fluxo-caixa', empresaId, dataInicio, dataFim, agrupamento],
    queryFn: () => buscarFluxoCaixa(empresaId as number, dataInicio as string, dataFim as string, agrupamento),
    enabled: empresaId !== undefined && dataInicio !== undefined && dataFim !== undefined,
  })
}

// --- Conciliação Bancária ---

export function useImportacoesExtrato(contaBancariaId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_IMPORTACOES_EXTRATO, contaBancariaId, pagina, tamanho],
    queryFn: () => listarImportacoesExtrato(contaBancariaId, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
    enabled: contaBancariaId !== undefined,
  })
}

export function useItensImportacao(importacaoId: number | undefined, pagina: number, tamanho = 50) {
  return useQuery({
    queryKey: ['financeiro-itens-importacao', importacaoId, pagina, tamanho],
    queryFn: () => listarItensImportacao(importacaoId as number, pagina, tamanho),
    enabled: importacaoId !== undefined,
  })
}

export function useImportarExtratoOfx() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ contaBancariaId, arquivo, usuarioId }: { contaBancariaId: number; arquivo: File; usuarioId: number | undefined }) =>
      importarExtratoOfx(contaBancariaId, arquivo, usuarioId),
    onSuccess: (importacao) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_IMPORTACOES_EXTRATO })
      queryClient.invalidateQueries({ queryKey: queryKeyPendenciasConciliacao(importacao.contaBancariaId) })
    },
  })
}

export function usePendenciasConciliacao(contaBancariaId: number | undefined) {
  return useQuery({
    queryKey: queryKeyPendenciasConciliacao(contaBancariaId ?? -1),
    queryFn: () => buscarPendenciasConciliacao(contaBancariaId as number),
    enabled: contaBancariaId !== undefined,
  })
}

export function useConciliarAutomatico(contaBancariaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => conciliarAutomatico(contaBancariaId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeyPendenciasConciliacao(contaBancariaId) })
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_MOVIMENTOS })
    },
  })
}

export function useConciliarManual(contaBancariaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ itemId, request }: { itemId: number; request: ConciliarManualRequest }) => conciliarManual(itemId, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyPendenciasConciliacao(contaBancariaId) }),
  })
}

export function useDesconciliarItem(contaBancariaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (itemId: number) => desconciliarItem(itemId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyPendenciasConciliacao(contaBancariaId) }),
  })
}

export function useGerarAjusteConciliacao(contaBancariaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ itemId, request }: { itemId: number; request: GerarAjusteConciliacaoRequest }) => gerarAjusteConciliacao(itemId, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeyPendenciasConciliacao(contaBancariaId) })
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_MOVIMENTOS })
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_BANCARIAS })
    },
  })
}

// --- EDI Bancário: Remessa/Retorno CNAB 240 ---

export function useRemessasBancarias(contaBancariaId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_REMESSAS, contaBancariaId, pagina, tamanho],
    queryFn: () => listarRemessasBancarias(contaBancariaId, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useRemessaBancaria(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyRemessa(id ?? -1),
    queryFn: () => buscarRemessaBancaria(id as number),
    enabled: id !== undefined,
  })
}

export function useGerarRemessaBancaria() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: GerarRemessaRequest) => gerarRemessaBancaria(request),
    onSuccess: (remessa) => {
      queryClient.setQueryData(queryKeyRemessa(remessa.id), remessa)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_REMESSAS })
    },
  })
}

export function useBaixarArquivoRemessa() {
  return useMutation({
    mutationFn: (id: number) => baixarArquivoRemessa(id),
  })
}

export function useRetornosBancarios(contaBancariaId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_RETORNOS, contaBancariaId, pagina, tamanho],
    queryFn: () => listarRetornosBancarios(contaBancariaId, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useImportarRetornoBancario() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ contaBancariaId, arquivo }: { contaBancariaId: number; arquivo: File }) => importarRetornoBancario(contaBancariaId, arquivo),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_RETORNOS })
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONTAS_RECEBER })
    },
  })
}
