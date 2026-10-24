import type { Situacao } from '@shared/domain/situacao'

// --- Enums ---

/** Decide como o Balancete calcula o saldo (débito−crédito ou crédito−débito) — convenção contábil universal, não decisão de projeto. */
export type NaturezaConta = 'DEVEDORA' | 'CREDORA'
export const NATUREZA_CONTA_LABEL: Record<NaturezaConta, string> = {
  DEVEDORA: 'Devedora (débito aumenta)',
  CREDORA: 'Credora (crédito aumenta)',
}

/** Grupo patrimonial/resultado — mesma classificação usada no referencial da RFB (Bloco I da ECD, registro I050). */
export type TipoContaContabil = 'ATIVO' | 'PASSIVO' | 'PATRIMONIO_LIQUIDO' | 'RECEITA' | 'DESPESA' | 'RESULTADO'
export const TIPO_CONTA_CONTABIL_LABEL: Record<TipoContaContabil, string> = {
  ATIVO: 'Ativo',
  PASSIVO: 'Passivo',
  PATRIMONIO_LIQUIDO: 'Patrimônio Líquido',
  RECEITA: 'Receita',
  DESPESA: 'Despesa',
  RESULTADO: 'Resultado',
}

export type TipoPartida = 'DEBITO' | 'CREDITO'
export const TIPO_PARTIDA_LABEL: Record<TipoPartida, string> = { DEBITO: 'Débito', CREDITO: 'Crédito' }

/** `MANUAL` é o único caminho exercitado hoje — contabilização automática por evento de negócio é sub-fase futura. */
export type OrigemLancamentoContabil = 'MANUAL' | 'VENDA' | 'COMPRA' | 'FINANCEIRO'
export const ORIGEM_LANCAMENTO_LABEL: Record<OrigemLancamentoContabil, string> = {
  MANUAL: 'Manual',
  VENDA: 'Venda',
  COMPRA: 'Compra',
  FINANCEIRO: 'Financeiro',
}

/** Lançamento é imutável depois de lançado — a única forma de desfazer é um segundo lançamento ESTORNADO com as partidas invertidas. */
export type SituacaoLancamentoContabil = 'LANCADO' | 'ESTORNADO'
export const SITUACAO_LANCAMENTO_LABEL: Record<SituacaoLancamentoContabil, string> = {
  LANCADO: 'Lançado',
  ESTORNADO: 'Estornado',
}

// --- Plano de Contas ---

export interface PlanoContaRequest {
  empresaId: number
  codigo: string
  nome: string
  contaPaiId?: number
  tipo: TipoContaContabil
  natureza: NaturezaConta
  contaAnalitica: boolean
  codigoReferencialRfb?: string
  situacao?: Situacao
}

export interface PlanoContaResponse {
  id: number
  empresaId: number
  codigo: string
  nome: string
  contaPaiId?: number
  contaPaiNome?: string
  tipo: TipoContaContabil
  natureza: NaturezaConta
  contaAnalitica: boolean
  codigoReferencialRfb?: string
  situacao: Situacao
}

// --- Centro de Custo ---

export interface CentroCustoRequest {
  empresaId: number
  codigo: string
  nome: string
  situacao?: Situacao
}

export interface CentroCustoResponse {
  id: number
  empresaId: number
  codigo: string
  nome: string
  situacao: Situacao
}

// --- Lançamento Contábil ---

export interface PartidaContabilRequest {
  contaId: number
  centroCustoId?: number
  tipoPartida: TipoPartida
  valor: number
  historicoComplementar?: string
}

export interface PartidaContabilResponse {
  id: number
  contaId: number
  contaCodigo: string
  contaNome: string
  centroCustoId?: number
  centroCustoNome?: string
  tipoPartida: TipoPartida
  valor: number
  historicoComplementar?: string
}

/** `origemTipo`/`origemId` não são campos do request — todo lançamento criado por esta API nasce MANUAL. */
export interface LancamentoContabilRequest {
  empresaId: number
  dataLancamento: string
  historico: string
  numeroDocumento?: string
  partidas: PartidaContabilRequest[]
}

export interface LancamentoContabilResponse {
  id: number
  empresaId: number
  dataLancamento: string
  historico: string
  numeroDocumento?: string
  origemTipo: OrigemLancamentoContabil
  origemId?: number
  situacao: SituacaoLancamentoContabil
  estornoDeId?: number
  estornadoPorId?: number
  partidas: PartidaContabilResponse[]
}

export interface EstornarLancamentoRequest {
  motivo: string
}

// --- Relatórios ---

export interface BalanceteItemResponse {
  contaId: number
  contaCodigo: string
  contaNome: string
  tipo: TipoContaContabil
  natureza: NaturezaConta
  totalDebito: number
  totalCredito: number
  saldo: number
}

export interface RazaoItemResponse {
  lancamentoContabilId: number
  dataLancamento: string
  historico: string
  tipoPartida: TipoPartida
  valor: number
  saldoCorrido: number
}

export interface LancamentosFiltro {
  empresaId: number
  dataInicio?: string
  dataFim?: string
}
