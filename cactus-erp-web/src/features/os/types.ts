import type { Situacao } from '@shared/domain/situacao'

// --- Catálogos (OsStatus/OsEquipamento) ---

export interface OsStatusRequest {
  codigo?: string
  nome: string
  situacao: Situacao
}

export interface OsStatusResponse {
  id: number
  codigo?: string
  nome: string
  situacao: Situacao
}

export interface OsEquipamentoRequest {
  nome: string
  descricao?: string
  situacao: Situacao
}

export interface OsEquipamentoResponse {
  id: number
  nome: string
  descricao?: string
  situacao: Situacao
}

// --- Ordem de Serviço (agregado) ---

/** Nasce sem equipamentos/itens/evolução — adicione via sub-recursos (POST .../itens, .../equipamentos). */
export interface OsAberturaRequest {
  osStatusId: number
  empresaId: number
  clienteId: number
  colaboradorId?: number
  numero?: string
  dataInicio?: string
  horaInicio?: string
  dataPrevisao?: string
  horaPrevisao?: string
  nomeContato?: string
  foneContato?: string
  observacaoCliente?: string
  observacaoAbertura?: string
}

export interface OsAberturaResponse {
  id: number
  osStatusId: number
  osStatusNome: string
  empresaId: number
  clienteId: number
  colaboradorId?: number
  numero?: string
  dataInicio?: string
  horaInicio?: string
  dataPrevisao?: string
  horaPrevisao?: string
  dataFim?: string
  horaFim?: string
  nomeContato?: string
  foneContato?: string
  observacaoCliente?: string
  observacaoAbertura?: string
  orcamentoAprovado: boolean
  dataAprovacaoOrcamento?: string
  valorTotal: number
  equipamentos: OsAberturaEquipamentoResponse[]
  itens: OsProdutoServicoResponse[]
  evolucoes: OsEvolucaoResponse[]
}

export interface OsAberturaEquipamentoRequest {
  osEquipamentoId: number
  numeroSerie?: string
  /** Domínio de valores não confirmado contra o backend (campo cru, ver Javadoc do backend) — não inventar enum. */
  tipoCobertura?: string
}

export interface OsAberturaEquipamentoResponse {
  id: number
  osEquipamentoId: number
  osEquipamentoNome: string
  numeroSerie?: string
  tipoCobertura?: string
}

export type TipoItemOs = 'PRODUTO' | 'SERVICO'

export const TIPO_ITEM_OS_LABEL: Record<TipoItemOs, string> = {
  PRODUTO: 'Produto (peça)',
  SERVICO: 'Serviço (mão de obra)',
}

/** `produtoId` obrigatório quando `tipo=PRODUTO` — validado no backend (`ITEM_PRODUTO_SEM_PRODUTO_ID`). */
export interface OsProdutoServicoRequest {
  tipo: TipoItemOs
  produtoId?: number
  complemento?: string
  quantidade: number
  valorUnitario: number
  taxaDesconto?: number
}

export interface OsProdutoServicoResponse {
  id: number
  tipo: TipoItemOs
  produtoId?: number
  complemento?: string
  quantidade: number
  valorUnitario: number
  valorSubtotal: number
  taxaDesconto?: number
  valorDesconto: number
  valorTotal: number
}

/** Append-only — sem edição/remoção depois de criado. */
export interface OsEvolucaoRequest {
  observacao?: string
  enviarEmail: boolean
}

export interface OsEvolucaoResponse {
  id: number
  dataRegistro: string
  horaRegistro: string
  observacao?: string
  enviarEmail: boolean
}

// --- Lookups cross-serviço, só pra alimentar Selects/células de resumo ---

export interface ClienteResumo {
  id: number
  nome: string
}

export interface ColaboradorResumo {
  id: number
  nome: string
}

export interface ProdutoResumo {
  id: number
  codigoInterno: string
  nome: string
}
