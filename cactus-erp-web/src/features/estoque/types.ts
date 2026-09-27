import type { Situacao } from '@shared/domain/situacao'

/** Ver `entity/TipoDeposito.java`. */
export type TipoDeposito = 'PRINCIPAL' | 'FILIAL' | 'CD' | 'TRANSITO'

export const TIPO_DEPOSITO_LABEL: Record<TipoDeposito, string> = {
  PRINCIPAL: 'Principal',
  FILIAL: 'Filial',
  CD: 'Centro de Distribuição',
  TRANSITO: 'Trânsito',
}

/** Ver `entity/MetodoCusteio.java`. */
export type MetodoCusteio = 'MEDIA_PONDERADA' | 'FIFO'

export const METODO_CUSTEIO_LABEL: Record<MetodoCusteio, string> = {
  MEDIA_PONDERADA: 'Média ponderada móvel',
  FIFO: 'FIFO (primeiro que entra, primeiro que sai)',
}

/**
 * Ver `entity/TipoMovimentoEstoque.java`. `TRANSFERENCIA_SAIDA`/`TRANSFERENCIA_ENTRADA`
 * ficam de fora do Select de lançamento manual — são gerados automaticamente por
 * `TransferenciaEstoqueService.enviar()`/`receber()`, nunca lançados direto pelo usuário.
 */
export type TipoMovimentoEstoque =
  | 'ENTRADA'
  | 'SAIDA'
  | 'AJUSTE'
  | 'PERDA'
  | 'DEVOLUCAO'
  | 'TRANSFERENCIA_SAIDA'
  | 'TRANSFERENCIA_ENTRADA'

export const TIPO_MOVIMENTO_LANCAVEL: { value: TipoMovimentoEstoque; label: string }[] = [
  { value: 'ENTRADA', label: 'Entrada' },
  { value: 'SAIDA', label: 'Saída' },
  { value: 'AJUSTE', label: 'Ajuste' },
  { value: 'PERDA', label: 'Perda' },
  { value: 'DEVOLUCAO', label: 'Devolução' },
]

export const TIPO_MOVIMENTO_LABEL: Record<TipoMovimentoEstoque, string> = {
  ENTRADA: 'Entrada',
  SAIDA: 'Saída',
  AJUSTE: 'Ajuste',
  PERDA: 'Perda',
  DEVOLUCAO: 'Devolução',
  TRANSFERENCIA_SAIDA: 'Transferência (saída)',
  TRANSFERENCIA_ENTRADA: 'Transferência (entrada)',
}

/** Ver `entity/DirecaoAjuste.java` — só relevante quando `tipoMovimento=AJUSTE`. */
export type DirecaoAjuste = 'ACRESCIMO' | 'DECRESCIMO'

/** Ver `entity/OrigemMovimentoEstoque.java`. */
export type OrigemMovimentoEstoque =
  | 'COMPRA'
  | 'VENDA_PDV'
  | 'AJUSTE_MANUAL'
  | 'TRANSFERENCIA'
  | 'INVENTARIO'
  | 'DEVOLUCAO_PDV'
  | 'KIT'

export const ORIGEM_MOVIMENTO_LABEL: Record<OrigemMovimentoEstoque, string> = {
  COMPRA: 'Compra',
  VENDA_PDV: 'Venda (PDV)',
  AJUSTE_MANUAL: 'Ajuste manual',
  TRANSFERENCIA: 'Transferência',
  INVENTARIO: 'Inventário',
  DEVOLUCAO_PDV: 'Devolução (PDV)',
  KIT: 'Kit',
}

/** Ver `entity/SituacaoTransferencia.java`. */
export type SituacaoTransferencia = 'SOLICITADA' | 'EM_TRANSITO' | 'CONCLUIDA' | 'CANCELADA'

export const SITUACAO_TRANSFERENCIA_LABEL: Record<SituacaoTransferencia, string> = {
  SOLICITADA: 'Solicitada',
  EM_TRANSITO: 'Em trânsito',
  CONCLUIDA: 'Concluída',
  CANCELADA: 'Cancelada',
}

// --- Depósito ---

export interface DepositoRequest {
  empresaId: number
  codigo: string
  nome: string
  tipo: TipoDeposito
  situacao: Situacao
}

export interface DepositoResponse extends DepositoRequest {
  id: number
}

export interface DepositosFiltro {
  empresaId?: number
}

// --- Configuração de Estoque (por empresa) ---

export interface ConfiguracaoEstoqueEmpresaRequest {
  metodoCusteio: MetodoCusteio
  limiteAprovacaoAjuste: number
}

export interface ConfiguracaoEstoqueEmpresaResponse extends ConfiguracaoEstoqueEmpresaRequest {
  id: number
  empresaId: number
}

// --- Saldo de Estoque (só leitura) ---

export interface SaldoEstoqueResponse {
  id: number
  produtoId: number
  depositoId: number
  depositoNome: string
  quantidade: number
  quantidadeReservada: number
  quantidadeDisponivel: number
  custoMedioPonderado: number
  dataUltimaMovimentacao: string
}

export interface SaldosFiltro {
  produtoId?: number
  depositoId?: number
}

// --- Movimento de Estoque (ledger append-only) ---

export interface EstoqueMovimentoRequest {
  produtoId: number
  depositoId: number
  loteId?: number
  numeroSerieId?: number
  tipoMovimento: TipoMovimentoEstoque
  direcaoAjuste?: DirecaoAjuste
  quantidade: number
  custoUnitario?: number
  origemTipo: OrigemMovimentoEstoque
  origemId?: number
  aprovadorId?: number
  observacao?: string
}

export interface EstoqueMovimentoResponse {
  id: number
  produtoId: number
  depositoId: number
  loteId?: number
  numeroSerieId?: number
  tipoMovimento: TipoMovimentoEstoque
  direcaoAjuste?: DirecaoAjuste
  quantidade: number
  quantidadeAnterior: number
  quantidadeNova: number
  custoUnitario?: number
  origemTipo: OrigemMovimentoEstoque
  origemId?: number
  usuarioId?: number
  aprovadorId?: number
  observacao?: string
  dataMovimento: string
}

export interface MovimentosFiltro {
  produtoId?: number
  depositoId?: number
}

// --- Transferência de Estoque ---

export interface TransferenciaEstoqueRequest {
  produtoId: number
  depositoOrigemId: number
  depositoDestinoId: number
  loteId?: number
  quantidade: number
  observacao?: string
}

export interface TransferenciaEstoqueResponse {
  id: number
  produtoId: number
  depositoOrigemId: number
  depositoOrigemNome: string
  depositoDestinoId: number
  depositoDestinoNome: string
  loteId?: number
  quantidade: number
  custoUnitario?: number
  situacao: SituacaoTransferencia
  usuarioEnvioId?: number
  dataEnvio?: string
  usuarioRecebimentoId?: number
  dataRecebimento?: string
  observacao?: string
}

// --- Inventário Físico (Fase B) ---

/** Ver `entity/TipoInventario.java`. CEGA: a tela NÃO mostra `quantidadeSistema` durante a contagem — força contagem real, sem viés. */
export type TipoInventario = 'CEGA' | 'POR_SALDO'

export const TIPO_INVENTARIO_LABEL: Record<TipoInventario, string> = {
  CEGA: 'Cega (contador não vê o saldo do sistema)',
  POR_SALDO: 'Por saldo (contador vê o saldo esperado)',
}

/** Ver `entity/SituacaoInventario.java`. */
export type SituacaoInventario = 'ABERTO' | 'EM_CONTAGEM' | 'FINALIZADO' | 'CANCELADO'

export const SITUACAO_INVENTARIO_LABEL: Record<SituacaoInventario, string> = {
  ABERTO: 'Aberto',
  EM_CONTAGEM: 'Em contagem',
  FINALIZADO: 'Finalizado',
  CANCELADO: 'Cancelado',
}

export interface InventarioFisicoAbrirRequest {
  empresaId: number
  depositoId: number
  tipo: TipoInventario
}

export interface InventarioFisicoContagemRequest {
  quantidadeContada: number
}

export interface InventarioFisicoItemResponse {
  id: number
  produtoId: number
  loteId?: number
  quantidadeSistema: number
  quantidadeContada?: number
  quantidadeDivergencia?: number
  contadorId?: number
  dataContagem?: string
}

export interface InventarioFisicoResponse {
  id: number
  empresaId: number
  depositoId: number
  depositoNome: string
  tipo: TipoInventario
  situacao: SituacaoInventario
  responsavelId?: number
  dataAbertura: string
  dataFechamento?: string
  itens: InventarioFisicoItemResponse[]
}

// --- Lote (Fase B) ---

export interface LoteRequest {
  produtoId: number
  codigo: string
  dataFabricacao?: string
  dataValidade?: string
  situacao: Situacao
}

export interface LoteResponse extends LoteRequest {
  id: number
}

// --- Número de Série (Fase B) ---

/** Ver `entity/SituacaoNumeroSerie.java`. */
export type SituacaoNumeroSerie = 'DISPONIVEL' | 'VENDIDO' | 'EM_TRANSITO' | 'BAIXADO'

export const SITUACAO_NUMERO_SERIE_LABEL: Record<SituacaoNumeroSerie, string> = {
  DISPONIVEL: 'Disponível',
  VENDIDO: 'Vendido',
  EM_TRANSITO: 'Em trânsito',
  BAIXADO: 'Baixado',
}

export interface NumeroSerieRequest {
  produtoId: number
  numeroSerie: string
  depositoId?: number
  situacao: SituacaoNumeroSerie
}

export interface NumeroSerieResponse extends NumeroSerieRequest {
  id: number
  depositoNome?: string
}

// --- Kit / Produto Composto (Fase B) ---

export interface ProdutoKitComponenteRequest {
  produtoComponenteId: number
  quantidade: number
}

export interface ProdutoKitComponenteResponse {
  id: number
  produtoKitId: number
  produtoComponenteId: number
  quantidade: number
}

export interface BaixarKitRequest {
  depositoId: number
  quantidade: number
}

// --- Curva ABC (Fase B, relatório) ---

export interface CurvaAbcItemResponse {
  produtoId: number
  valorMovimentado: number
  percentualParticipacao: number
  percentualAcumulado: number
  classificacao: 'A' | 'B' | 'C'
}
