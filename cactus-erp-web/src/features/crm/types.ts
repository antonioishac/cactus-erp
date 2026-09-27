import type { Situacao } from '@shared/domain/situacao'

// --- Estágio do Funil ---

export type TipoEncerramentoFunil = 'ABERTO' | 'GANHO' | 'PERDIDO'

export const TIPO_ENCERRAMENTO_FUNIL_LABEL: Record<TipoEncerramentoFunil, string> = {
  ABERTO: 'Aberto (em andamento)',
  GANHO: 'Fechamento — Ganho',
  PERDIDO: 'Fechamento — Perdido',
}

export interface EstagioFunilRequest {
  codigo?: string
  nome: string
  ordem: number
  tipoEncerramento: TipoEncerramentoFunil
}

export interface EstagioFunilResponse {
  id: number
  codigo?: string
  nome: string
  ordem: number
  tipoEncerramento: TipoEncerramentoFunil
  situacao: Situacao
}

// --- Lead ---

export type OrigemLead = 'SITE' | 'INDICACAO' | 'EVENTO' | 'COLD_CALL' | 'REDES_SOCIAIS' | 'OUTROS'

export const ORIGEM_LEAD_LABEL: Record<OrigemLead, string> = {
  SITE: 'Site',
  INDICACAO: 'Indicação',
  EVENTO: 'Evento',
  COLD_CALL: 'Cold call',
  REDES_SOCIAIS: 'Redes sociais',
  OUTROS: 'Outros',
}

export type SituacaoLead = 'NOVO' | 'EM_QUALIFICACAO' | 'QUALIFICADO' | 'DESQUALIFICADO' | 'CONVERTIDO'

export const SITUACAO_LEAD_LABEL: Record<SituacaoLead, string> = {
  NOVO: 'Novo',
  EM_QUALIFICACAO: 'Em qualificação',
  QUALIFICADO: 'Qualificado',
  DESQUALIFICADO: 'Desqualificado',
  CONVERTIDO: 'Convertido',
}

export interface LeadRequest {
  nome: string
  nomeEmpresa?: string
  email?: string
  telefone?: string
  origem?: OrigemLead
  vendedorId?: number
  observacao?: string
}

export interface LeadResponse {
  id: number
  nome: string
  nomeEmpresa?: string
  email?: string
  telefone?: string
  origem?: OrigemLead
  vendedorId?: number
  clienteId?: number
  situacao: SituacaoLead
  motivoDesqualificacao?: string
  observacao?: string
}

export interface DesqualificarLeadRequest {
  motivo: string
}

export interface ConverterLeadRequest {
  clienteId: number
}

// --- Oportunidade ---

export type SituacaoOportunidade = 'ABERTA' | 'GANHA' | 'PERDIDA'

export const SITUACAO_OPORTUNIDADE_LABEL: Record<SituacaoOportunidade, string> = {
  ABERTA: 'Aberta',
  GANHA: 'Ganha',
  PERDIDA: 'Perdida',
}

/** `leadId` é opcional — uma oportunidade pode nascer direto de um `clienteId` já existente, sem passar por Lead. */
export interface OportunidadeRequest {
  leadId?: number
  clienteId?: number
  vendedorId?: number
  nome: string
  valorEstimado?: number
  probabilidadePercentual?: number
  dataPrevisaoFechamento?: string
  estagioFunilId: number
  observacao?: string
}

export interface OportunidadeResponse {
  id: number
  leadId?: number
  clienteId?: number
  vendedorId?: number
  nome: string
  valorEstimado?: number
  probabilidadePercentual?: number
  dataPrevisaoFechamento?: string
  estagioFunilId: number
  estagioFunilNome: string
  situacao: SituacaoOportunidade
  motivoPerda?: string
  dataFechamento?: string
  observacao?: string
}

export interface MoverEstagioRequest {
  estagioFunilId: number
  motivoPerda?: string
}

/** Uma "coluna" do Kanban — estágio + oportunidades ABERTAS nele + soma do valor estimado. */
export interface FunilEstagioResponse {
  estagioFunilId: number
  estagioFunilNome: string
  ordem: number
  valorEstimadoTotal?: number
  oportunidades: OportunidadeResponse[]
}

// --- Atividade (tarefa agendada) ---

export type TipoAtividade = 'LIGACAO' | 'REUNIAO' | 'EMAIL' | 'VISITA' | 'TAREFA'

export const TIPO_ATIVIDADE_LABEL: Record<TipoAtividade, string> = {
  LIGACAO: 'Ligação',
  REUNIAO: 'Reunião',
  EMAIL: 'E-mail',
  VISITA: 'Visita',
  TAREFA: 'Tarefa',
}

/** Pelo menos um de `leadId`/`oportunidadeId` é obrigatório — validado no backend. */
export interface AtividadeRequest {
  leadId?: number
  oportunidadeId?: number
  tipo: TipoAtividade
  responsavelId?: number
  dataHora: string
  descricao?: string
}

export interface AtividadeResponse {
  id: number
  leadId?: number
  oportunidadeId?: number
  tipo: TipoAtividade
  responsavelId?: number
  dataHora: string
  concluida: boolean
  dataConclusao?: string
  descricao?: string
}

// --- Interação (histórico append-only) ---

export type TipoInteracao = 'LIGACAO' | 'EMAIL' | 'REUNIAO' | 'WHATSAPP' | 'NOTA'

export const TIPO_INTERACAO_LABEL: Record<TipoInteracao, string> = {
  LIGACAO: 'Ligação',
  EMAIL: 'E-mail',
  REUNIAO: 'Reunião',
  WHATSAPP: 'WhatsApp',
  NOTA: 'Nota',
}

/** Pelo menos um de `leadId`/`oportunidadeId` é obrigatório — validado no backend. Sem `PUT`/`DELETE`, append-only. */
export interface InteracaoRequest {
  leadId?: number
  oportunidadeId?: number
  tipo: TipoInteracao
  dataInteracao?: string
  usuarioId?: number
  descricao: string
}

export interface InteracaoResponse {
  id: number
  leadId?: number
  oportunidadeId?: number
  tipo: TipoInteracao
  dataInteracao?: string
  usuarioId?: number
  descricao: string
}

// --- Lookups cross-serviço ---

export interface ClienteResumo {
  id: number
  nome: string
}

/** Resolução em 2 hops (vendas-service → cadastro-base-service) — `Vendedor` não carrega
 * o nome do colaborador direto, só `colaboradorId` solto. */
export interface VendedorResumo {
  id: number
  nome: string
}

export interface UsuarioResumo {
  id: number
  login: string
  nome: string
}
