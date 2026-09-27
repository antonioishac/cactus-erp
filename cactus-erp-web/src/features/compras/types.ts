// --- Enums ---

/** Ver `entity/TipoRequisicaoCompra.java`. */
export type TipoRequisicaoCompra = 'INTERNA' | 'EXTERNA' | 'MISTA'
export const TIPO_REQUISICAO_LABEL: Record<TipoRequisicaoCompra, string> = {
  INTERNA: 'Interna (uso próprio)',
  EXTERNA: 'Externa (revenda)',
  MISTA: 'Mista',
}

/** `PEDIDO_GERADO` setado automaticamente quando um pedido nasce desta requisição ou de uma cotação vinculada a ela. */
export type SituacaoRequisicaoCompra = 'ABERTA' | 'PEDIDO_GERADO' | 'CANCELADA'
export const SITUACAO_REQUISICAO_LABEL: Record<SituacaoRequisicaoCompra, string> = {
  ABERTA: 'Aberta',
  PEDIDO_GERADO: 'Pedido gerado',
  CANCELADA: 'Cancelada',
}

/** `FINALIZADA` setado automaticamente quando um dos fornecedores concorrentes vira pedido. */
export type SituacaoCotacaoCompra = 'ABERTA' | 'FINALIZADA' | 'CANCELADA'
export const SITUACAO_COTACAO_LABEL: Record<SituacaoCotacaoCompra, string> = {
  ABERTA: 'Aberta',
  FINALIZADA: 'Finalizada',
  CANCELADA: 'Cancelada',
}

/** RASCUNHO → (enviar) → AGUARDANDO_APROVACAO (só acima do limite) ou ENVIADO direto → PARCIALMENTE_RECEBIDO → RECEBIDO_TOTAL. */
export type SituacaoPedidoCompra =
  | 'RASCUNHO'
  | 'AGUARDANDO_APROVACAO'
  | 'ENVIADO'
  | 'PARCIALMENTE_RECEBIDO'
  | 'RECEBIDO_TOTAL'
  | 'CANCELADO'
export const SITUACAO_PEDIDO_LABEL: Record<SituacaoPedidoCompra, string> = {
  RASCUNHO: 'Rascunho',
  AGUARDANDO_APROVACAO: 'Aguardando aprovação',
  ENVIADO: 'Enviado',
  PARCIALMENTE_RECEBIDO: 'Parcialmente recebido',
  RECEBIDO_TOTAL: 'Recebido totalmente',
  CANCELADO: 'Cancelado',
}

/** NORMAL = entrega única, PLANEJADO = quantidade anual com entregas mensais, ABERTO = liberações conforme necessidade. */
export type TipoPedidoCompra = 'NORMAL' | 'PLANEJADO' | 'ABERTO'
export const TIPO_PEDIDO_LABEL: Record<TipoPedidoCompra, string> = {
  NORMAL: 'Normal (entrega única)',
  PLANEJADO: 'Planejado (entregas programadas)',
  ABERTO: 'Aberto (liberações sob demanda)',
}

/** De onde o pedido nasceu — `origemId` referencia Requisição ou Cotação, nulo em AVULSO. */
export type OrigemPedidoCompra = 'REQUISICAO' | 'COTACAO' | 'AVULSO'
export const ORIGEM_PEDIDO_LABEL: Record<OrigemPedidoCompra, string> = {
  REQUISICAO: 'Requisição',
  COTACAO: 'Cotação',
  AVULSO: 'Avulso',
}

export type TipoFrete = 'CIF' | 'FOB'
export const TIPO_FRETE_LABEL: Record<TipoFrete, string> = { CIF: 'CIF (por conta do fornecedor)', FOB: 'FOB (por conta do comprador)' }

/** Campo indPag da NF-e. */
export type IndicadorPagamentoNfe = 'A_VISTA' | 'A_PRAZO' | 'OUTROS'
export const INDICADOR_PAGAMENTO_LABEL: Record<IndicadorPagamentoNfe, string> = {
  A_VISTA: 'À vista',
  A_PRAZO: 'A prazo',
  OUTROS: 'Outros',
}

// --- Requisição de Compra ---

export interface RequisicaoCompraItemRequest {
  produtoId: number
  quantidade: number
}

export interface RequisicaoCompraItemResponse {
  id: number
  produtoId: number
  quantidade: number
}

export interface RequisicaoCompraRequest {
  colaboradorId: number
  tipo: TipoRequisicaoCompra
  descricao?: string
  dataRequisicao?: string
  observacao?: string
  itens: RequisicaoCompraItemRequest[]
}

export interface RequisicaoCompraResponse {
  id: number
  colaboradorId: number
  tipo: TipoRequisicaoCompra
  descricao?: string
  dataRequisicao?: string
  situacao: SituacaoRequisicaoCompra
  observacao?: string
  itens: RequisicaoCompraItemResponse[]
}

export interface RequisicoesFiltro {
  colaboradorId?: number
}

// --- Cotação de Compra ---

export interface CotacaoCompraRequest {
  requisicaoId: number
  dataCotacao?: string
  descricao?: string
}

export interface FornecedorCotacaoItemRequest {
  produtoId: number
  quantidade: number
  valorUnitario: number
  taxaDesconto?: number
}

export interface FornecedorCotacaoItemResponse {
  id: number
  produtoId: number
  quantidade: number
  valorUnitario: number
  taxaDesconto?: number
  valorDesconto: number
  valorTotal: number
}

export interface FornecedorCotacaoRequest {
  fornecedorId: number
  prazoEntrega?: string
  condicoesPagamento?: string
}

export interface FornecedorCotacaoResponse {
  id: number
  cotacaoId: number
  fornecedorId: number
  prazoEntrega?: string
  condicoesPagamento?: string
  valorSubtotal: number
  taxaDesconto?: number
  valorDesconto: number
  valorTotal: number
  itens: FornecedorCotacaoItemResponse[]
}

export interface CotacaoCompraResponse {
  id: number
  requisicaoId: number
  dataCotacao?: string
  descricao?: string
  situacao: SituacaoCotacaoCompra
  fornecedores: FornecedorCotacaoResponse[]
}

/** `colaboradorId` do pedido gerado é herdado do solicitante da requisição de origem. */
export interface GerarPedidoDeCotacaoRequest {
  empresaId: number
  tipo: TipoPedidoCompra
}

// --- Pedido de Compra ---

/** Itens não vão inline — adicionados via `POST /pedidos/{id}/itens` depois de criado. */
export interface PedidoCompraRequest {
  empresaId: number
  tipo: TipoPedidoCompra
  fornecedorId: number
  colaboradorId: number
  origemTipo: OrigemPedidoCompra
  origemId?: number
  dataPedido?: string
  dataPrevistaEntrega?: string
  dataPrevisaoPagamento?: string
  localEntrega?: string
  localCobranca?: string
  contato?: string
  tipoFrete?: TipoFrete
  indicadorPagamento?: IndicadorPagamentoNfe
  quantidadeParcelas?: number
  diaPrimeiroVencimento?: number
  intervaloEntreParcelas?: number
  diaFixoParcela?: number
  valorFrete?: number
  valorSeguro?: number
  valorOutrasDespesas?: number
  observacao?: string
}

/** `fatorConversaoEstoque` nulo assume 1 (compra e estoque na mesma unidade). */
export interface PedidoCompraItemRequest {
  produtoId: number
  unidadeCompraId?: number
  fatorConversaoEstoque?: number
  quantidade: number
  valorUnitario: number
  taxaDesconto?: number
}

export interface PedidoCompraItemResponse {
  id: number
  produtoId: number
  unidadeCompraId?: number
  fatorConversaoEstoque?: number
  quantidade: number
  quantidadeRecebida: number
  quantidadePendente: number
  valorUnitario: number
  taxaDesconto?: number
  valorDesconto: number
  valorTotal: number
}

export interface PedidoCompraResponse extends PedidoCompraRequest {
  id: number
  valorSubtotal: number
  taxaDesconto?: number
  valorDesconto: number
  valorTotal: number
  aprovadorId?: number
  dataAprovacao?: string
  situacao: SituacaoPedidoCompra
  itens: PedidoCompraItemResponse[]
}

export interface PedidosFiltro {
  fornecedorId?: number
}

// --- Recebimento de Compra ---

export interface RecebimentoCompraItemRequest {
  pedidoCompraItemId: number
  quantidadeRecebida: number
  depositoId: number
  loteId?: number
}

export interface RecebimentoCompraItemResponse {
  id: number
  pedidoCompraItemId: number
  produtoId: number
  quantidadeRecebida: number
  depositoId: number
  loteId?: number
}

/** `usuarioId` não vem do corpo — extraído do JWT de quem chama. */
export interface RecebimentoCompraRequest {
  pedidoId: number
  dataRecebimento: string
  numeroNotaFiscal?: string
  chaveAcessoNfe?: string
  observacao?: string
  itens: RecebimentoCompraItemRequest[]
}

export interface RecebimentoCompraResponse {
  id: number
  pedidoId: number
  usuarioId?: number
  dataRecebimento: string
  numeroNotaFiscal?: string
  chaveAcessoNfe?: string
  observacao?: string
  itens: RecebimentoCompraItemResponse[]
}

// --- Configuração de Compras ---

/** `limiteAprovacaoPedido` nulo = sem exigência de aprovação. */
export interface ConfiguracaoComprasEmpresaRequest {
  limiteAprovacaoPedido?: number
}

export interface ConfiguracaoComprasEmpresaResponse {
  id: number
  empresaId: number
  limiteAprovacaoPedido?: number
}

// --- Lookups cross-serviço, só pra alimentar Selects (padrão já usado em Estoque/Segurança — busca local, sem reaproveitar outras features) ---

export interface ProdutoResumo {
  id: number
  codigoInterno: string
  nome: string
}

export interface FornecedorResumo {
  id: number
  nome: string
}

export interface ColaboradorResumo {
  id: number
  nome: string
}

export interface EmpresaResumo {
  id: number
  razaoSocial: string
  nomeFantasia?: string
}

export interface DepositoResumo {
  id: number
  nome: string
}

export interface LoteResumo {
  id: number
  codigo: string
}

export interface ProdutoUnidadeResumo {
  id: number
  sigla: string
  descricao: string
}
