/** Único status que permite reprocessar — os outros 3 são etapas normais do fluxo. */
export type StatusRegistroSincronizacao = 'RECEBIDO' | 'PUBLICADO' | 'PROCESSADO' | 'ERRO'
export const STATUS_REGISTRO_LABEL: Record<StatusRegistroSincronizacao, string> = {
  RECEBIDO: 'Recebido (aguardando publicação)',
  PUBLICADO: 'Publicado (na fila)',
  PROCESSADO: 'Processado com sucesso',
  ERRO: 'Erro (esgotou tentativas)',
}

/** `ESTOQUE_MOVIMENTO` vai pro estoque; os 5 de caixa/fiado (Fase 6-B) vão pro financeiro. */
export type TipoRegistroSincronizacao =
  | 'ESTOQUE_MOVIMENTO'
  | 'SANGRIA'
  | 'SUPRIMENTO'
  | 'ABERTURA_CAIXA'
  | 'CONFERENCIA_CAIXA'
  | 'FIADO_LANCAMENTO'
export const TIPO_REGISTRO_LABEL: Record<TipoRegistroSincronizacao, string> = {
  ESTOQUE_MOVIMENTO: 'Movimento de estoque',
  SANGRIA: 'Sangria',
  SUPRIMENTO: 'Suprimento',
  ABERTURA_CAIXA: 'Abertura de caixa',
  CONFERENCIA_CAIXA: 'Conferência de caixa',
  FIADO_LANCAMENTO: 'Fiado',
}

export interface RegistroSincronizacaoResponse {
  id: number
  loteId: number
  lojaId: number
  dispositivoId: string
  tipoRegistro: TipoRegistroSincronizacao
  idOrigem: string
  chaveIdempotencia: string
  status: StatusRegistroSincronizacao
  tentativas: number
  motivoErro?: string
  dataCriacao: string
  dataAtualizacao: string
}

export interface LoteSincronizacaoResumoResponse {
  id: number
  lojaId: number
  dispositivoId: string
  tipoRegistro: TipoRegistroSincronizacao
  totalRegistros: number
  totalAceitos: number
  totalRejeitados: number
  dataCriacao: string
}

export interface RegistrosFiltro {
  status?: StatusRegistroSincronizacao
  lojaId?: number
}

export interface LotesFiltro {
  lojaId?: number
}

// --- Clientes de Loja (client OAuth2 por loja — vive no seguranca-service) ---

export interface ClienteLojaRequest {
  empresaId: number
  descricao: string
}

/** `clientSecret` só aparece nesta resposta, na criação — nunca mais recuperável depois. */
export interface ClienteLojaResponse {
  clientId: string
  clientSecret: string
  empresaId: number
  descricao: string
}

export interface ClienteLojaResumoResponse {
  clientId: string
  empresaId: number
  descricao: string
}
