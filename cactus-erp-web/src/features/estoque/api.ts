import { cadastroBaseApi, estoqueApi, segurancaApi } from '@shared/api/httpClients'
import { baixarArquivo, type FormatoRelatorio } from '@shared/api/download'
import type { PageResponse } from '@shared/api/pageResponse'
import type { ProdutoResumo } from '@shared/api/produtoLookup'
import type {
  BaixarKitRequest,
  ConfiguracaoEstoqueEmpresaRequest,
  ConfiguracaoEstoqueEmpresaResponse,
  CurvaAbcItemResponse,
  DepositoRequest,
  DepositoResponse,
  DepositosFiltro,
  EstoqueMovimentoRequest,
  EstoqueMovimentoResponse,
  InventarioFisicoAbrirRequest,
  InventarioFisicoContagemRequest,
  InventarioFisicoResponse,
  LoteRequest,
  LoteResponse,
  MovimentosFiltro,
  NumeroSerieRequest,
  NumeroSerieResponse,
  ProdutoKitComponenteRequest,
  ProdutoKitComponenteResponse,
  SaldoEstoqueResponse,
  SaldosFiltro,
  SituacaoNumeroSerie,
  TransferenciaEstoqueRequest,
  TransferenciaEstoqueResponse,
} from './types'

// --- Depósito ---

export async function listarDepositos(
  filtro: DepositosFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<DepositoResponse>> {
  const { data } = await estoqueApi.get<PageResponse<DepositoResponse>>('/api/v1/depositos', {
    params: { empresaId: filtro.empresaId, page: pagina, size: tamanho },
  })
  return data
}

export async function listarDepositosAtivos(empresaId: number): Promise<DepositoResponse[]> {
  const { data } = await estoqueApi.get<DepositoResponse[]>(`/api/v1/depositos/empresa/${empresaId}/ativos`)
  return data
}

export async function buscarDeposito(id: number): Promise<DepositoResponse> {
  const { data } = await estoqueApi.get<DepositoResponse>(`/api/v1/depositos/${id}`)
  return data
}

export async function criarDeposito(request: DepositoRequest): Promise<DepositoResponse> {
  const { data } = await estoqueApi.post<DepositoResponse>('/api/v1/depositos', request)
  return data
}

export async function atualizarDeposito(id: number, request: DepositoRequest): Promise<DepositoResponse> {
  const { data } = await estoqueApi.put<DepositoResponse>(`/api/v1/depositos/${id}`, request)
  return data
}

// --- Configuração de Estoque ---

export async function buscarConfiguracaoEstoque(empresaId: number): Promise<ConfiguracaoEstoqueEmpresaResponse> {
  const { data } = await estoqueApi.get<ConfiguracaoEstoqueEmpresaResponse>(`/api/v1/configuracao-estoque/empresa/${empresaId}`)
  return data
}

export async function atualizarConfiguracaoEstoque(
  empresaId: number,
  request: ConfiguracaoEstoqueEmpresaRequest,
): Promise<ConfiguracaoEstoqueEmpresaResponse> {
  const { data } = await estoqueApi.put<ConfiguracaoEstoqueEmpresaResponse>(
    `/api/v1/configuracao-estoque/empresa/${empresaId}`,
    request,
  )
  return data
}

// --- Saldo (só leitura) ---

export async function listarSaldos(
  filtro: SaldosFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<SaldoEstoqueResponse>> {
  const { data } = await estoqueApi.get<PageResponse<SaldoEstoqueResponse>>('/api/v1/saldos', {
    params: { produtoId: filtro.produtoId, depositoId: filtro.depositoId, page: pagina, size: tamanho },
  })
  return data
}

// --- Movimento (ledger append-only) ---

export async function listarMovimentos(
  filtro: MovimentosFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<EstoqueMovimentoResponse>> {
  const { data } = await estoqueApi.get<PageResponse<EstoqueMovimentoResponse>>('/api/v1/movimentos', {
    params: { produtoId: filtro.produtoId, depositoId: filtro.depositoId, page: pagina, size: tamanho, sort: 'dataMovimento,desc' },
  })
  return data
}

export async function registrarMovimento(request: EstoqueMovimentoRequest): Promise<EstoqueMovimentoResponse> {
  const { data } = await estoqueApi.post<EstoqueMovimentoResponse>('/api/v1/movimentos', request)
  return data
}

// --- Transferência ---

export async function listarTransferencias(
  depositoId: number,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<TransferenciaEstoqueResponse>> {
  const { data } = await estoqueApi.get<PageResponse<TransferenciaEstoqueResponse>>('/api/v1/transferencias', {
    params: { depositoId, page: pagina, size: tamanho },
  })
  return data
}

export async function criarTransferencia(request: TransferenciaEstoqueRequest): Promise<TransferenciaEstoqueResponse> {
  const { data } = await estoqueApi.post<TransferenciaEstoqueResponse>('/api/v1/transferencias', request)
  return data
}

export async function enviarTransferencia(id: number): Promise<TransferenciaEstoqueResponse> {
  const { data } = await estoqueApi.post<TransferenciaEstoqueResponse>(`/api/v1/transferencias/${id}/enviar`)
  return data
}

export async function receberTransferencia(id: number): Promise<TransferenciaEstoqueResponse> {
  const { data } = await estoqueApi.post<TransferenciaEstoqueResponse>(`/api/v1/transferencias/${id}/receber`)
  return data
}

export async function cancelarTransferencia(id: number): Promise<TransferenciaEstoqueResponse> {
  const { data } = await estoqueApi.post<TransferenciaEstoqueResponse>(`/api/v1/transferencias/${id}/cancelar`)
  return data
}

// --- Busca de produto por nome, só pra alimentar Selects (mesmo padrão de features/tabelas-preco/api.ts — limite de feature, não reaproveita features/produtos) ---

export async function buscarProdutosParaSelecao(nome: string | undefined): Promise<ProdutoResumo[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ProdutoResumo>>('/api/v1/produtos', {
    params: { nome, size: 20 },
  })
  return data.conteudo
}

// --- Busca de usuário por nome/login, só pra alimentar o Select de aprovador (seguranca-service, exige SEGURANCA_CONSULTAR) ---

export interface UsuarioResumo {
  id: number
  login: string
  nome: string
}

export async function buscarUsuariosParaSelecao(busca: string | undefined): Promise<UsuarioResumo[]> {
  const { data } = await segurancaApi.get<PageResponse<UsuarioResumo>>('/api/v1/usuarios', {
    params: { busca, size: 20 },
  })
  return data.conteudo
}

// --- Inventário Físico (Fase B) ---

export async function listarInventarios(
  depositoId: number,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<InventarioFisicoResponse>> {
  const { data } = await estoqueApi.get<PageResponse<InventarioFisicoResponse>>('/api/v1/inventarios', {
    params: { depositoId, page: pagina, size: tamanho },
  })
  return data
}

export async function buscarInventario(id: number): Promise<InventarioFisicoResponse> {
  const { data } = await estoqueApi.get<InventarioFisicoResponse>(`/api/v1/inventarios/${id}`)
  return data
}

export async function abrirInventario(request: InventarioFisicoAbrirRequest): Promise<InventarioFisicoResponse> {
  const { data } = await estoqueApi.post<InventarioFisicoResponse>('/api/v1/inventarios', request)
  return data
}

export async function iniciarContagemInventario(id: number): Promise<InventarioFisicoResponse> {
  const { data } = await estoqueApi.post<InventarioFisicoResponse>(`/api/v1/inventarios/${id}/iniciar-contagem`)
  return data
}

export async function lancarContagemInventario(
  id: number,
  itemId: number,
  request: InventarioFisicoContagemRequest,
): Promise<void> {
  await estoqueApi.put(`/api/v1/inventarios/${id}/itens/${itemId}/contagem`, request)
}

export async function finalizarInventario(id: number): Promise<InventarioFisicoResponse> {
  const { data } = await estoqueApi.post<InventarioFisicoResponse>(`/api/v1/inventarios/${id}/finalizar`)
  return data
}

export async function cancelarInventario(id: number): Promise<InventarioFisicoResponse> {
  const { data } = await estoqueApi.post<InventarioFisicoResponse>(`/api/v1/inventarios/${id}/cancelar`)
  return data
}

// --- Lote (Fase B) ---

export async function listarLotes(
  produtoId: number | undefined,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<LoteResponse>> {
  const { data } = await estoqueApi.get<PageResponse<LoteResponse>>('/api/v1/lotes', {
    params: { produtoId, page: pagina, size: tamanho },
  })
  return data
}

export async function listarLotesProximosDoVencimento(
  produtoId: number,
  dias: number,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<LoteResponse>> {
  const { data } = await estoqueApi.get<PageResponse<LoteResponse>>(`/api/v1/lotes/produto/${produtoId}/vencimento-proximo`, {
    params: { dias, page: pagina, size: tamanho },
  })
  return data
}

export async function criarLote(request: LoteRequest): Promise<LoteResponse> {
  const { data } = await estoqueApi.post<LoteResponse>('/api/v1/lotes', request)
  return data
}

export async function atualizarLote(id: number, request: LoteRequest): Promise<LoteResponse> {
  const { data } = await estoqueApi.put<LoteResponse>(`/api/v1/lotes/${id}`, request)
  return data
}

// --- Número de Série (Fase B) ---

export async function listarNumerosSerie(
  produtoId: number,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<NumeroSerieResponse>> {
  const { data } = await estoqueApi.get<PageResponse<NumeroSerieResponse>>('/api/v1/numeros-serie', {
    params: { produtoId, page: pagina, size: tamanho },
  })
  return data
}

export async function cadastrarNumeroSerie(request: NumeroSerieRequest): Promise<NumeroSerieResponse> {
  const { data } = await estoqueApi.post<NumeroSerieResponse>('/api/v1/numeros-serie', request)
  return data
}

export async function atualizarSituacaoNumeroSerie(
  id: number,
  situacao: SituacaoNumeroSerie,
  depositoId: number | undefined,
): Promise<NumeroSerieResponse> {
  const { data } = await estoqueApi.patch<NumeroSerieResponse>(`/api/v1/numeros-serie/${id}/situacao`, null, {
    params: { situacao, depositoId },
  })
  return data
}

// --- Kit / Produto Composto (Fase B) ---

export async function listarComponentesKit(produtoKitId: number): Promise<ProdutoKitComponenteResponse[]> {
  const { data } = await estoqueApi.get<ProdutoKitComponenteResponse[]>(`/api/v1/kits/${produtoKitId}/componentes`)
  return data
}

export async function adicionarComponenteKit(
  produtoKitId: number,
  request: ProdutoKitComponenteRequest,
): Promise<ProdutoKitComponenteResponse> {
  const { data } = await estoqueApi.post<ProdutoKitComponenteResponse>(`/api/v1/kits/${produtoKitId}/componentes`, request)
  return data
}

export async function removerComponenteKit(produtoKitId: number, componenteId: number): Promise<void> {
  await estoqueApi.delete(`/api/v1/kits/${produtoKitId}/componentes/${componenteId}`)
}

export async function baixarKit(produtoKitId: number, request: BaixarKitRequest): Promise<void> {
  await estoqueApi.post(`/api/v1/kits/${produtoKitId}/baixar`, request)
}

// --- Curva ABC (Fase B, relatório) ---

export async function calcularCurvaAbc(depositoId: number | undefined, dias: number): Promise<CurvaAbcItemResponse[]> {
  const { data } = await estoqueApi.get<CurvaAbcItemResponse[]>('/api/v1/relatorios/curva-abc', {
    params: { depositoId, dias },
  })
  return data
}

export async function exportarCurvaAbc(depositoId: number | undefined, dias: number, formato: FormatoRelatorio): Promise<void> {
  await baixarArquivo(estoqueApi, `/api/v1/relatorios/curva-abc/${formato}`, `curva-abc.${formato}`, { depositoId, dias })
}

// --- Relatórios: Saldo por Depósito e Ficha de Movimentação ---

export async function exportarSaldoEstoque(
  produtoId: number | undefined,
  depositoId: number | undefined,
  formato: FormatoRelatorio,
): Promise<void> {
  await baixarArquivo(estoqueApi, `/api/v1/relatorios/saldo-estoque/${formato}`, `saldo-estoque.${formato}`, {
    produtoId,
    depositoId,
  })
}

export async function buscarFichaMovimentacao(
  produtoId: number,
  dataInicio: string,
  dataFim: string,
  depositoId: number | undefined,
): Promise<EstoqueMovimentoResponse[]> {
  const { data } = await estoqueApi.get<EstoqueMovimentoResponse[]>('/api/v1/relatorios/ficha-movimentacao-produto', {
    params: { produtoId, dataInicio, dataFim, depositoId },
  })
  return data
}

export async function exportarFichaMovimentacao(
  produtoId: number,
  dataInicio: string,
  dataFim: string,
  depositoId: number | undefined,
  formato: FormatoRelatorio,
): Promise<void> {
  await baixarArquivo(
    estoqueApi,
    `/api/v1/relatorios/ficha-movimentacao-produto/${formato}`,
    `ficha-movimentacao-produto.${formato}`,
    { produtoId, dataInicio, dataFim, depositoId },
  )
}
