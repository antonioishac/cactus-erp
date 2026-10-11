import { cadastroBaseApi, financeiroApi } from '@shared/api/httpClients'
import { baixarArquivo, type FormatoRelatorio } from '@shared/api/download'
import type { PageResponse } from '@shared/api/pageResponse'
import type {
  AplicacaoFinanceiraRequest,
  AplicacaoFinanceiraResponse,
  AtualizarClassificacaoTituloRequest,
  BaixaRequest,
  BancoResponse,
  CancelarChequeEmitidoRequest,
  CancelarTituloRequest,
  ChequeEmitidoRequest,
  ChequeEmitidoResponse,
  ChequeRecebidoRequest,
  ChequeRecebidoResponse,
  ClienteResumo,
  CompensarChequeEmitidoRequest,
  CompensarChequeRecebidoRequest,
  ConciliarAutomaticoResponse,
  ConciliarManualRequest,
  ConfiguracaoFinanceiroEmpresaRequest,
  ConfiguracaoFinanceiroEmpresaResponse,
  ContaBancariaRequest,
  ContaBancariaResponse,
  ContaBancariaResumo,
  ContaPagarRequest,
  ContaPagarResponse,
  ContaReceberRequest,
  ContaReceberResponse,
  ContasPagarFiltro,
  ContasReceberFiltro,
  DepositarChequeRecebidoRequest,
  DescontarChequeRecebidoRequest,
  DevolverChequeRequest,
  EstornarBaixaRequest,
  ExtratoBancarioImportacaoResponse,
  ExtratoBancarioItemResponse,
  FluxoCaixaPeriodoResponse,
  TituloEmAbertoResponse,
  TitulosEmAbertoFiltro,
  FormaPagamentoResumo,
  FornecedorResumo,
  GerarAjusteConciliacaoRequest,
  GerarContaPagarParceladaRequest,
  GerarContaReceberParceladaRequest,
  GerarRemessaRequest,
  MovimentoCaixaBancoRequest,
  MovimentoCaixaBancoResponse,
  NaturezaFinanceiraRequest,
  NaturezaFinanceiraResponse,
  PendenciasConciliacaoResponse,
  PosicaoCaixaResponse,
  RemessaBancariaResponse,
  RenegociarTituloRequest,
  ResgatarAplicacaoRequest,
  RetornoBancarioResponse,
  TipoDocumentoFinanceiroRequest,
  TipoDocumentoFinanceiroResponse,
  TransferenciaBancariaRequest,
  TransferenciaBancariaResponse,
} from './types'

// --- Contas a Receber ---

export async function listarContasReceber(
  filtro: ContasReceberFiltro,
  empresaId: number | undefined,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<ContaReceberResponse>> {
  const { data } = await financeiroApi.get<PageResponse<ContaReceberResponse>>('/api/v1/contas-receber', {
    params: { empresaId, clienteId: filtro.clienteId, page: pagina, size: tamanho },
  })
  return data
}

export async function buscarContaReceber(id: number): Promise<ContaReceberResponse> {
  const { data } = await financeiroApi.get<ContaReceberResponse>(`/api/v1/contas-receber/${id}`)
  return data
}

export async function criarContaReceber(request: ContaReceberRequest): Promise<ContaReceberResponse> {
  const { data } = await financeiroApi.post<ContaReceberResponse>('/api/v1/contas-receber', request)
  return data
}

export async function gerarContaReceberParcelada(request: GerarContaReceberParceladaRequest): Promise<ContaReceberResponse[]> {
  const { data } = await financeiroApi.post<ContaReceberResponse[]>('/api/v1/contas-receber/gerar-parcelado', request)
  return data
}

export async function atualizarClassificacaoContaReceber(
  id: number,
  request: AtualizarClassificacaoTituloRequest,
): Promise<ContaReceberResponse> {
  const { data } = await financeiroApi.put<ContaReceberResponse>(`/api/v1/contas-receber/${id}/classificacao`, request)
  return data
}

export async function registrarBaixaContaReceber(id: number, request: BaixaRequest): Promise<ContaReceberResponse> {
  const { data } = await financeiroApi.post<ContaReceberResponse>(`/api/v1/contas-receber/${id}/baixas`, request)
  return data
}

export async function estornarBaixaContaReceber(id: number, baixaId: number, request: EstornarBaixaRequest): Promise<ContaReceberResponse> {
  const { data } = await financeiroApi.post<ContaReceberResponse>(`/api/v1/contas-receber/${id}/baixas/${baixaId}/estornar`, request)
  return data
}

export async function cancelarContaReceber(id: number, request: CancelarTituloRequest): Promise<ContaReceberResponse> {
  const { data } = await financeiroApi.post<ContaReceberResponse>(`/api/v1/contas-receber/${id}/cancelar`, request)
  return data
}

export async function renegociarContaReceber(id: number, request: RenegociarTituloRequest): Promise<ContaReceberResponse> {
  const { data } = await financeiroApi.post<ContaReceberResponse>(`/api/v1/contas-receber/${id}/renegociar`, request)
  return data
}

// --- Contas a Pagar (espelho exato de Contas a Receber) ---

export async function listarContasPagar(
  filtro: ContasPagarFiltro,
  empresaId: number | undefined,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<ContaPagarResponse>> {
  const { data } = await financeiroApi.get<PageResponse<ContaPagarResponse>>('/api/v1/contas-pagar', {
    params: { empresaId, fornecedorId: filtro.fornecedorId, page: pagina, size: tamanho },
  })
  return data
}

export async function buscarContaPagar(id: number): Promise<ContaPagarResponse> {
  const { data } = await financeiroApi.get<ContaPagarResponse>(`/api/v1/contas-pagar/${id}`)
  return data
}

export async function criarContaPagar(request: ContaPagarRequest): Promise<ContaPagarResponse> {
  const { data } = await financeiroApi.post<ContaPagarResponse>('/api/v1/contas-pagar', request)
  return data
}

export async function gerarContaPagarParcelada(request: GerarContaPagarParceladaRequest): Promise<ContaPagarResponse[]> {
  const { data } = await financeiroApi.post<ContaPagarResponse[]>('/api/v1/contas-pagar/gerar-parcelado', request)
  return data
}

export async function atualizarClassificacaoContaPagar(id: number, request: AtualizarClassificacaoTituloRequest): Promise<ContaPagarResponse> {
  const { data } = await financeiroApi.put<ContaPagarResponse>(`/api/v1/contas-pagar/${id}/classificacao`, request)
  return data
}

export async function registrarBaixaContaPagar(id: number, request: BaixaRequest): Promise<ContaPagarResponse> {
  const { data } = await financeiroApi.post<ContaPagarResponse>(`/api/v1/contas-pagar/${id}/baixas`, request)
  return data
}

export async function estornarBaixaContaPagar(id: number, baixaId: number, request: EstornarBaixaRequest): Promise<ContaPagarResponse> {
  const { data } = await financeiroApi.post<ContaPagarResponse>(`/api/v1/contas-pagar/${id}/baixas/${baixaId}/estornar`, request)
  return data
}

export async function cancelarContaPagar(id: number, request: CancelarTituloRequest): Promise<ContaPagarResponse> {
  const { data } = await financeiroApi.post<ContaPagarResponse>(`/api/v1/contas-pagar/${id}/cancelar`, request)
  return data
}

export async function renegociarContaPagar(id: number, request: RenegociarTituloRequest): Promise<ContaPagarResponse> {
  const { data } = await financeiroApi.post<ContaPagarResponse>(`/api/v1/contas-pagar/${id}/renegociar`, request)
  return data
}

// --- Natureza Financeira (catálogo por empresa) ---

export async function listarNaturezasFinanceiras(empresaId: number): Promise<NaturezaFinanceiraResponse[]> {
  const { data } = await financeiroApi.get<NaturezaFinanceiraResponse[]>('/api/v1/naturezas-financeiras', {
    params: { empresaId },
  })
  return data
}

export async function criarNaturezaFinanceira(request: NaturezaFinanceiraRequest): Promise<NaturezaFinanceiraResponse> {
  const { data } = await financeiroApi.post<NaturezaFinanceiraResponse>('/api/v1/naturezas-financeiras', request)
  return data
}

export async function atualizarNaturezaFinanceira(id: number, request: NaturezaFinanceiraRequest): Promise<NaturezaFinanceiraResponse> {
  const { data } = await financeiroApi.put<NaturezaFinanceiraResponse>(`/api/v1/naturezas-financeiras/${id}`, request)
  return data
}

export async function inativarNaturezaFinanceira(id: number): Promise<void> {
  await financeiroApi.delete(`/api/v1/naturezas-financeiras/${id}`)
}

// --- Tipo de Documento Financeiro (catálogo GLOBAL) ---

export async function listarTiposDocumentoFinanceiro(): Promise<TipoDocumentoFinanceiroResponse[]> {
  const { data } = await financeiroApi.get<TipoDocumentoFinanceiroResponse[]>('/api/v1/tipos-documento-financeiro')
  return data
}

export async function criarTipoDocumentoFinanceiro(request: TipoDocumentoFinanceiroRequest): Promise<TipoDocumentoFinanceiroResponse> {
  const { data } = await financeiroApi.post<TipoDocumentoFinanceiroResponse>('/api/v1/tipos-documento-financeiro', request)
  return data
}

export async function inativarTipoDocumentoFinanceiro(id: number): Promise<void> {
  await financeiroApi.delete(`/api/v1/tipos-documento-financeiro/${id}`)
}

// --- Configuração Financeira (1 por empresa) ---

export async function buscarConfiguracaoFinanceiro(empresaId: number): Promise<ConfiguracaoFinanceiroEmpresaResponse> {
  const { data } = await financeiroApi.get<ConfiguracaoFinanceiroEmpresaResponse>(`/api/v1/configuracao-financeiro/empresa/${empresaId}`)
  return data
}

export async function atualizarConfiguracaoFinanceiro(
  empresaId: number,
  request: ConfiguracaoFinanceiroEmpresaRequest,
): Promise<ConfiguracaoFinanceiroEmpresaResponse> {
  const { data } = await financeiroApi.put<ConfiguracaoFinanceiroEmpresaResponse>(
    `/api/v1/configuracao-financeiro/empresa/${empresaId}`,
    request,
  )
  return data
}

// --- Contas Bancárias (Fase B — CRUD completo; `listarContasBancariasAtivas` já existia desde a Fase A, só o lookup) ---

export async function listarContasBancariasAtivas(empresaId: number): Promise<ContaBancariaResumo[]> {
  const { data } = await financeiroApi.get<ContaBancariaResumo[]>('/api/v1/contas-bancarias/ativas', {
    params: { empresaId },
  })
  return data
}

export async function listarContasBancarias(empresaId: number | undefined, pagina: number, tamanho = 10): Promise<PageResponse<ContaBancariaResponse>> {
  const { data } = await financeiroApi.get<PageResponse<ContaBancariaResponse>>('/api/v1/contas-bancarias', {
    params: { empresaId, page: pagina, size: tamanho },
  })
  return data
}

export async function buscarContaBancaria(id: number): Promise<ContaBancariaResponse> {
  const { data } = await financeiroApi.get<ContaBancariaResponse>(`/api/v1/contas-bancarias/${id}`)
  return data
}

export async function criarContaBancaria(request: ContaBancariaRequest): Promise<ContaBancariaResponse> {
  const { data } = await financeiroApi.post<ContaBancariaResponse>('/api/v1/contas-bancarias', request)
  return data
}

export async function atualizarContaBancaria(id: number, request: ContaBancariaRequest): Promise<ContaBancariaResponse> {
  const { data } = await financeiroApi.put<ContaBancariaResponse>(`/api/v1/contas-bancarias/${id}`, request)
  return data
}

export async function inativarContaBancaria(id: number): Promise<ContaBancariaResponse> {
  const { data } = await financeiroApi.post<ContaBancariaResponse>(`/api/v1/contas-bancarias/${id}/inativar`)
  return data
}

export async function reativarContaBancaria(id: number): Promise<ContaBancariaResponse> {
  const { data } = await financeiroApi.post<ContaBancariaResponse>(`/api/v1/contas-bancarias/${id}/reativar`)
  return data
}

export async function listarBancos(): Promise<BancoResponse[]> {
  const { data } = await financeiroApi.get<BancoResponse[]>('/api/v1/bancos')
  return data
}

// --- Movimento de Caixa e Banco (ledger append-only) ---

export async function listarMovimentosCaixaBanco(
  contaBancariaId: number | undefined,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<MovimentoCaixaBancoResponse>> {
  const { data } = await financeiroApi.get<PageResponse<MovimentoCaixaBancoResponse>>('/api/v1/movimentos-caixa-banco', {
    params: { contaBancariaId, page: pagina, size: tamanho },
  })
  return data
}

export async function registrarMovimentoManual(request: MovimentoCaixaBancoRequest): Promise<MovimentoCaixaBancoResponse> {
  const { data } = await financeiroApi.post<MovimentoCaixaBancoResponse>('/api/v1/movimentos-caixa-banco', request)
  return data
}

// --- Cheque Emitido ---

export async function listarChequesEmitidos(
  contaBancariaId: number | undefined,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<ChequeEmitidoResponse>> {
  const { data } = await financeiroApi.get<PageResponse<ChequeEmitidoResponse>>('/api/v1/cheques-emitidos', {
    params: { contaBancariaId, page: pagina, size: tamanho },
  })
  return data
}

export async function criarChequeEmitido(request: ChequeEmitidoRequest): Promise<ChequeEmitidoResponse> {
  const { data } = await financeiroApi.post<ChequeEmitidoResponse>('/api/v1/cheques-emitidos', request)
  return data
}

export async function compensarChequeEmitido(id: number, request: CompensarChequeEmitidoRequest): Promise<ChequeEmitidoResponse> {
  const { data } = await financeiroApi.post<ChequeEmitidoResponse>(`/api/v1/cheques-emitidos/${id}/compensar`, request)
  return data
}

export async function devolverChequeEmitido(id: number, request: DevolverChequeRequest): Promise<ChequeEmitidoResponse> {
  const { data } = await financeiroApi.post<ChequeEmitidoResponse>(`/api/v1/cheques-emitidos/${id}/devolver`, request)
  return data
}

export async function cancelarChequeEmitido(id: number, request: CancelarChequeEmitidoRequest): Promise<ChequeEmitidoResponse> {
  const { data } = await financeiroApi.post<ChequeEmitidoResponse>(`/api/v1/cheques-emitidos/${id}/cancelar`, request)
  return data
}

// --- Cheque Recebido ---

export async function listarChequesRecebidos(
  clienteId: number | undefined,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<ChequeRecebidoResponse>> {
  const { data } = await financeiroApi.get<PageResponse<ChequeRecebidoResponse>>('/api/v1/cheques-recebidos', {
    params: { clienteId, page: pagina, size: tamanho },
  })
  return data
}

export async function criarChequeRecebido(request: ChequeRecebidoRequest): Promise<ChequeRecebidoResponse> {
  const { data } = await financeiroApi.post<ChequeRecebidoResponse>('/api/v1/cheques-recebidos', request)
  return data
}

export async function depositarChequeRecebido(id: number, request: DepositarChequeRecebidoRequest): Promise<ChequeRecebidoResponse> {
  const { data } = await financeiroApi.post<ChequeRecebidoResponse>(`/api/v1/cheques-recebidos/${id}/depositar`, request)
  return data
}

export async function compensarChequeRecebido(id: number, request: CompensarChequeRecebidoRequest): Promise<ChequeRecebidoResponse> {
  const { data } = await financeiroApi.post<ChequeRecebidoResponse>(`/api/v1/cheques-recebidos/${id}/compensar`, request)
  return data
}

export async function descontarChequeRecebido(id: number, request: DescontarChequeRecebidoRequest): Promise<ChequeRecebidoResponse> {
  const { data } = await financeiroApi.post<ChequeRecebidoResponse>(`/api/v1/cheques-recebidos/${id}/descontar`, request)
  return data
}

export async function devolverChequeRecebido(id: number, request: DevolverChequeRequest): Promise<ChequeRecebidoResponse> {
  const { data } = await financeiroApi.post<ChequeRecebidoResponse>(`/api/v1/cheques-recebidos/${id}/devolver`, request)
  return data
}

// --- Tesouraria: Transferência Bancária ---

export async function listarTransferenciasBancarias(
  contaBancariaId: number | undefined,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<TransferenciaBancariaResponse>> {
  const { data } = await financeiroApi.get<PageResponse<TransferenciaBancariaResponse>>('/api/v1/transferencias-bancarias', {
    params: { contaBancariaId, page: pagina, size: tamanho },
  })
  return data
}

export async function transferirEntreContas(request: TransferenciaBancariaRequest): Promise<TransferenciaBancariaResponse> {
  const { data } = await financeiroApi.post<TransferenciaBancariaResponse>('/api/v1/transferencias-bancarias', request)
  return data
}

// --- Tesouraria: Aplicação Financeira ---

export async function listarAplicacoesFinanceiras(
  contaBancariaId: number | undefined,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<AplicacaoFinanceiraResponse>> {
  const { data } = await financeiroApi.get<PageResponse<AplicacaoFinanceiraResponse>>('/api/v1/aplicacoes-financeiras', {
    params: { contaBancariaId, page: pagina, size: tamanho },
  })
  return data
}

export async function aplicarFinanceiro(request: AplicacaoFinanceiraRequest): Promise<AplicacaoFinanceiraResponse> {
  const { data } = await financeiroApi.post<AplicacaoFinanceiraResponse>('/api/v1/aplicacoes-financeiras', request)
  return data
}

export async function resgatarAplicacao(id: number, request: ResgatarAplicacaoRequest): Promise<AplicacaoFinanceiraResponse> {
  const { data } = await financeiroApi.post<AplicacaoFinanceiraResponse>(`/api/v1/aplicacoes-financeiras/${id}/resgatar`, request)
  return data
}

// --- Tesouraria: Posição de Caixa ---

export async function buscarPosicaoCaixa(empresaId: number): Promise<PosicaoCaixaResponse> {
  const { data } = await financeiroApi.get<PosicaoCaixaResponse>('/api/v1/tesouraria/posicao-caixa', {
    params: { empresaId },
  })
  return data
}

// --- Relatório: Fluxo de Caixa projetado ---

export async function buscarFluxoCaixa(
  empresaId: number,
  dataInicio: string,
  dataFim: string,
  agrupamento: 'DIA' | 'MES',
): Promise<FluxoCaixaPeriodoResponse[]> {
  const { data } = await financeiroApi.get<FluxoCaixaPeriodoResponse[]>('/api/v1/relatorios/fluxo-caixa', {
    params: { empresaId, dataInicio, dataFim, agrupamento },
  })
  return data
}

export async function exportarFluxoCaixa(
  empresaId: number,
  dataInicio: string,
  dataFim: string,
  agrupamento: 'DIA' | 'MES',
  formato: FormatoRelatorio,
): Promise<void> {
  await baixarArquivo(financeiroApi, `/api/v1/relatorios/fluxo-caixa/${formato}`, `fluxo-caixa.${formato}`, {
    empresaId,
    dataInicio,
    dataFim,
    agrupamento,
  })
}

// --- Relatório: Títulos em aberto (Contas a Receber/Pagar) ---

export async function buscarTitulosEmAberto(filtro: TitulosEmAbertoFiltro): Promise<TituloEmAbertoResponse[]> {
  const { data } = await financeiroApi.get<TituloEmAbertoResponse[]>('/api/v1/relatorios/titulos-em-aberto', {
    params: filtro,
  })
  return data
}

export async function exportarTitulosEmAberto(filtro: TitulosEmAbertoFiltro, formato: FormatoRelatorio): Promise<void> {
  const nome = filtro.tipo === 'RECEBER' ? 'contas-receber-em-aberto' : 'contas-pagar-em-aberto'
  await baixarArquivo(financeiroApi, `/api/v1/relatorios/titulos-em-aberto/${formato}`, `${nome}.${formato}`, { ...filtro })
}

// --- Conciliação Bancária ---

export async function listarImportacoesExtrato(
  contaBancariaId: number | undefined,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<ExtratoBancarioImportacaoResponse>> {
  const { data } = await financeiroApi.get<PageResponse<ExtratoBancarioImportacaoResponse>>('/api/v1/conciliacao-bancaria/importacoes', {
    params: { contaBancariaId, page: pagina, size: tamanho },
  })
  return data
}

export async function listarItensImportacao(importacaoId: number, pagina: number, tamanho = 50): Promise<PageResponse<ExtratoBancarioItemResponse>> {
  const { data } = await financeiroApi.get<PageResponse<ExtratoBancarioItemResponse>>(
    `/api/v1/conciliacao-bancaria/importacoes/${importacaoId}/itens`,
    { params: { page: pagina, size: tamanho } },
  )
  return data
}

export async function importarExtratoOfx(
  contaBancariaId: number,
  arquivo: File,
  usuarioId: number | undefined,
): Promise<ExtratoBancarioImportacaoResponse> {
  const formData = new FormData()
  formData.append('arquivo', arquivo)
  const { data } = await financeiroApi.post<ExtratoBancarioImportacaoResponse>('/api/v1/conciliacao-bancaria/importar-extrato', formData, {
    params: { contaBancariaId, usuarioId },
  })
  return data
}

export async function buscarPendenciasConciliacao(contaBancariaId: number): Promise<PendenciasConciliacaoResponse> {
  const { data } = await financeiroApi.get<PendenciasConciliacaoResponse>('/api/v1/conciliacao-bancaria/pendencias', {
    params: { contaBancariaId },
  })
  return data
}

export async function conciliarAutomatico(contaBancariaId: number): Promise<ConciliarAutomaticoResponse> {
  const { data } = await financeiroApi.post<ConciliarAutomaticoResponse>(`/api/v1/conciliacao-bancaria/${contaBancariaId}/conciliar-automatico`)
  return data
}

export async function conciliarManual(itemId: number, request: ConciliarManualRequest): Promise<ExtratoBancarioItemResponse> {
  const { data } = await financeiroApi.post<ExtratoBancarioItemResponse>(`/api/v1/conciliacao-bancaria/itens/${itemId}/conciliar-manual`, request)
  return data
}

export async function desconciliarItem(itemId: number): Promise<ExtratoBancarioItemResponse> {
  const { data } = await financeiroApi.post<ExtratoBancarioItemResponse>(`/api/v1/conciliacao-bancaria/itens/${itemId}/desconciliar`)
  return data
}

export async function gerarAjusteConciliacao(itemId: number, request: GerarAjusteConciliacaoRequest): Promise<ExtratoBancarioItemResponse> {
  const { data } = await financeiroApi.post<ExtratoBancarioItemResponse>(`/api/v1/conciliacao-bancaria/itens/${itemId}/gerar-ajuste`, request)
  return data
}

// --- EDI Bancário: Remessa/Retorno CNAB 240 ---

export async function listarRemessasBancarias(
  contaBancariaId: number | undefined,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<RemessaBancariaResponse>> {
  const { data } = await financeiroApi.get<PageResponse<RemessaBancariaResponse>>('/api/v1/edi-bancario/remessas', {
    params: { contaBancariaId, page: pagina, size: tamanho },
  })
  return data
}

export async function buscarRemessaBancaria(id: number): Promise<RemessaBancariaResponse> {
  const { data } = await financeiroApi.get<RemessaBancariaResponse>(`/api/v1/edi-bancario/remessas/${id}`)
  return data
}

export async function gerarRemessaBancaria(request: GerarRemessaRequest): Promise<RemessaBancariaResponse> {
  const { data } = await financeiroApi.post<RemessaBancariaResponse>('/api/v1/edi-bancario/remessas/gerar', request)
  return data
}

export async function baixarArquivoRemessa(id: number): Promise<void> {
  await baixarArquivo(financeiroApi, `/api/v1/edi-bancario/remessas/${id}/arquivo`, `remessa-${id}.rem`)
}

export async function listarRetornosBancarios(
  contaBancariaId: number | undefined,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<RetornoBancarioResponse>> {
  const { data } = await financeiroApi.get<PageResponse<RetornoBancarioResponse>>('/api/v1/edi-bancario/retornos', {
    params: { contaBancariaId, page: pagina, size: tamanho },
  })
  return data
}

export async function importarRetornoBancario(contaBancariaId: number, arquivo: File): Promise<RetornoBancarioResponse> {
  const formData = new FormData()
  formData.append('arquivo', arquivo)
  const { data } = await financeiroApi.post<RetornoBancarioResponse>('/api/v1/edi-bancario/retornos/importar', formData, {
    params: { contaBancariaId },
  })
  return data
}

// --- Lookups cross-serviço, só pra alimentar Selects (mesmo padrão já usado em Vendas/Compras) ---

export async function buscarClientesParaSelecao(nome: string | undefined): Promise<ClienteResumo[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ClienteResumo>>('/api/v1/clientes', {
    params: { nome, size: 20 },
  })
  return data.conteudo
}

export async function buscarClienteResumo(id: number): Promise<ClienteResumo> {
  const { data } = await cadastroBaseApi.get<ClienteResumo>(`/api/v1/clientes/${id}`)
  return data
}

export async function buscarFornecedoresParaSelecao(nome: string | undefined): Promise<FornecedorResumo[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<FornecedorResumo>>('/api/v1/fornecedores', {
    params: { nome, size: 20 },
  })
  return data.conteudo
}

export async function buscarFornecedorResumo(id: number): Promise<FornecedorResumo> {
  const { data } = await cadastroBaseApi.get<FornecedorResumo>(`/api/v1/fornecedores/${id}`)
  return data
}

export async function buscarFormasPagamentoParaSelecao(): Promise<FormaPagamentoResumo[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<FormaPagamentoResumo>>('/api/v1/formas-pagamento', {
    params: { size: 100 },
  })
  return data.conteudo
}
