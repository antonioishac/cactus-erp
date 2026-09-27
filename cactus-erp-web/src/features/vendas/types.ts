import type { Situacao } from '@shared/domain/situacao'

export type TipoFrete = 'CIF' | 'FOB'
export type TipoPagamentoVenda = 'VISTA' | 'PRAZO'
export type SituacaoOrcamento = 'RASCUNHO' | 'ENVIADO' | 'APROVADO' | 'CONVERTIDO' | 'EXPIRADO' | 'CANCELADO'
export type SituacaoVenda = 'RASCUNHO' | 'CONFIRMADA' | 'CANCELADA'
export type SituacaoComissao = 'PENDENTE' | 'PAGO' | 'CANCELADO'

export const TIPO_FRETE_LABEL: Record<TipoFrete, string> = {
  CIF: 'CIF (por conta do emitente)',
  FOB: 'FOB (por conta do destinatário)',
}

export const TIPO_PAGAMENTO_VENDA_LABEL: Record<TipoPagamentoVenda, string> = {
  VISTA: 'À vista',
  PRAZO: 'A prazo',
}

export const SITUACAO_ORCAMENTO_LABEL: Record<SituacaoOrcamento, string> = {
  RASCUNHO: 'Rascunho',
  ENVIADO: 'Enviado',
  APROVADO: 'Aprovado',
  CONVERTIDO: 'Convertido',
  EXPIRADO: 'Expirado',
  CANCELADO: 'Cancelado',
}

export const SITUACAO_VENDA_LABEL: Record<SituacaoVenda, string> = {
  RASCUNHO: 'Rascunho',
  CONFIRMADA: 'Confirmada',
  CANCELADA: 'Cancelada',
}

export const SITUACAO_COMISSAO_LABEL: Record<SituacaoComissao, string> = {
  PENDENTE: 'Pendente',
  PAGO: 'Pago',
  CANCELADO: 'Cancelado',
}

// --- Comissão / Vendedor ---

export interface ComissaoPerfilRequest {
  codigo?: string
  nome: string
  situacao: Situacao
}

export interface ComissaoPerfilResponse extends ComissaoPerfilRequest {
  id: number
  dataCriacao: string
  dataAtualizacao: string
}

export interface VendedorRequest {
  colaboradorId: number
  comissaoPerfilId?: number
  comissaoPercentual?: number
  metaVenda?: number
  situacao: Situacao
}

export interface VendedorResponse extends VendedorRequest {
  id: number
  comissaoPerfilNome?: string
  dataCriacao: string
  dataAtualizacao: string
}

// --- AFV (Força de Vendas) — rota e metas do vendedor ---

export interface VendedorRotaRequest {
  clienteId: number
  posicao?: number
}

export interface VendedorRotaResponse {
  id: number
  vendedorId: number
  clienteId: number
  posicao?: number
}

export type PeriodoMeta = 'DIARIO' | 'SEMANAL' | 'MENSAL' | 'ANUAL'

export const PERIODO_META_LABEL: Record<PeriodoMeta, string> = {
  DIARIO: 'Diário',
  SEMANAL: 'Semanal',
  MENSAL: 'Mensal',
  ANUAL: 'Anual',
}

export interface VendedorMetaRequest {
  clienteId: number
  periodoMeta?: PeriodoMeta
  metaOrcada?: number
  dataInicio?: string
  dataFim?: string
}

export interface VendedorMetaResponse {
  id: number
  vendedorId: number
  clienteId: number
  periodoMeta?: PeriodoMeta
  metaOrcada?: number
  metaRealizada?: number
  dataInicio?: string
  dataFim?: string
}

// --- Comissão por Objetivo (meta de comissão por produto, dentro de um Perfil) ---

export interface ComissaoObjetivoRequest {
  produtoId: number
  codigo?: string
  nome: string
  descricao?: string
  formaPagamento?: TipoPagamentoVenda
  taxaPagamento?: number
  valorPagamento?: number
  valorMeta?: number
  quantidade?: number
}

export interface ComissaoObjetivoResponse {
  id: number
  comissaoPerfilId: number
  produtoId: number
  codigo?: string
  nome: string
  descricao?: string
  formaPagamento?: TipoPagamentoVenda
  taxaPagamento?: number
  valorPagamento?: number
  valorMeta?: number
  quantidade?: number
}

// --- Condições de Pagamento ---

export interface VendaCondicoesParcelaDTO {
  parcela: number
  dias?: number
  taxa?: number
}

export interface VendaCondicoesPagamentoRequest {
  nome: string
  descricao?: string
  faturamentoMinimo?: number
  faturamentoMaximo?: number
  indiceCorrecao?: number
  diasTolerancia?: number
  valorTolerancia?: number
  prazoMedio?: number
  vistaPrazo?: TipoPagamentoVenda
  parcelas?: VendaCondicoesParcelaDTO[]
  situacao: Situacao
}

export interface VendaCondicoesPagamentoResponse extends VendaCondicoesPagamentoRequest {
  id: number
}

// --- Nota Fiscal (Modelo/Tipo) ---

export interface NotaFiscalModeloRequest {
  codigo: string
  descricao?: string
  modelo?: string
}

export interface NotaFiscalModeloResponse extends NotaFiscalModeloRequest {
  id: number
}

export interface NotaFiscalTipoRequest {
  notaFiscalModeloId: number
  nome: string
  descricao?: string
  serie?: string
  serieScan?: string
  situacao: Situacao
}

export interface NotaFiscalTipoResponse extends NotaFiscalTipoRequest {
  id: number
  notaFiscalModeloDescricao?: string
  ultimoNumero?: number
}

// --- Orçamento ---

export interface VendaOrcamentoRequest {
  condicoesPagamentoId: number
  vendedorId: number
  clienteId: number
  transportadoraId?: number
  codigo?: string
  dataEntrega?: string
  validade?: string
  tipoFrete?: TipoFrete
  valorFrete?: number
  taxaComissao?: number
  taxaDesconto?: number
  observacao?: string
}

export interface VendaOrcamentoItemRequest {
  produtoId: number
  quantidade: number
  valorUnitario: number
  taxaDesconto?: number
}

export interface VendaOrcamentoItemResponse extends VendaOrcamentoItemRequest {
  id: number
  valorSubtotal: number
  valorDesconto: number
  valorTotal: number
}

export interface VendaOrcamentoResponse extends VendaOrcamentoRequest {
  id: number
  dataCadastro: string
  valorSubtotal: number
  valorComissao: number
  valorDesconto: number
  valorTotal: number
  situacao: SituacaoOrcamento
  itens: VendaOrcamentoItemResponse[]
}

export interface GerarVendaDeOrcamentoRequest {
  empresaId: number
  notaFiscalTipoId: number
  formaPagamentoId: number
  dataVenda?: string
  dataSaida?: string
  horaSaida?: string
  numeroFatura?: number
}

// --- Venda ---

export interface VendaRequest {
  empresaId: number
  condicoesPagamentoId: number
  notaFiscalTipoId: number
  vendedorId: number
  clienteId: number
  transportadoraId?: number
  formaPagamentoId: number
  dataVenda?: string
  dataSaida?: string
  horaSaida?: string
  numeroFatura?: number
  localEntrega?: string
  localCobranca?: string
  tipoFrete?: TipoFrete
  taxaComissao?: number
  taxaDesconto?: number
  valorFrete?: number
  valorSeguro?: number
  diaFixoParcela?: number
  observacao?: string
}

export interface VendaItemRequest {
  produtoId: number
  depositoId: number
  quantidade: number
  valorUnitario: number
  taxaDesconto?: number
}

export interface VendaItemResponse extends VendaItemRequest {
  id: number
  valorSubtotal: number
  valorDesconto: number
  valorTotal: number
}

export interface VendaResponse extends VendaRequest {
  id: number
  vendaOrcamentoId?: number
  valorSubtotal: number
  valorComissao: number
  valorDesconto: number
  valorTotal: number
  situacao: SituacaoVenda
  itens: VendaItemResponse[]
}

export interface VendaFreteRequest {
  transportadoraId?: number
  conhecimento?: string
  responsavel?: TipoFrete
  placa?: string
  ufPlaca?: string
  seloFiscal?: string
  quantidadeVolume?: number
  marcaVolume?: string
  especieVolume?: string
  pesoBruto?: number
  pesoLiquido?: number
}

export interface VendaFreteResponse extends VendaFreteRequest {
  id: number
  vendaId: number
}

export interface VendaComissaoResponse {
  id: number
  vendaId: number
  vendedorId: number
  valorVenda: number
  tipoContabil: TipoPagamentoVenda
  valorComissao: number
  situacao: SituacaoComissao
  dataLancamento: string
}

// --- Filtros de listagem ---

export interface OrcamentosFiltro {
  clienteId?: number
}

export interface VendasFiltro {
  clienteId?: number
}

export interface ComissoesFiltro {
  vendedorId?: number
}

// --- Lookups cross-serviço ---

export interface ProdutoResumo {
  id: number
  codigoInterno: string
  nome: string
}

export interface ClienteResumo {
  id: number
  nome: string
}

export interface ColaboradorResumo {
  id: number
  nome: string
}

export interface TransportadoraResumo {
  id: number
  nome: string
}

export interface DepositoResumo {
  id: number
  codigo: string
  nome: string
}

export interface FormaPagamentoResumo {
  id: number
  codigo: string
  descricao: string
}
