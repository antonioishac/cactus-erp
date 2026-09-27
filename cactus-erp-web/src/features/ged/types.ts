import type { Situacao } from '@shared/domain/situacao'

// --- Tipo de Documento ---

export interface TipoDocumentoRequest {
  nome: string
  tamanhoMaximoMb: number
  situacao: Situacao
}

export interface TipoDocumentoResponse extends TipoDocumentoRequest {
  id: number
}

// --- Pasta de Documento ---

export interface PastaDocumentoRequest {
  nome: string
  descricao?: string
  pastaPaiId?: number
}

export interface PastaDocumentoResponse {
  id: number
  nome: string
  descricao?: string
  pastaPaiId?: number
  pastaPaiNome?: string
}

// --- Documento ---

/**
 * `permiteExclusao`/`permiteAlteracao` nulos assumem `true` (permissivo por
 * padrão) no backend; `assinado` nulo assume `false` — ver Javadoc de
 * `DocumentoRequest.java`. Aqui sempre mandamos os 3 explícitos (via
 * `initialValues` do form) pra não depender desse default implícito.
 */
export interface DocumentoRequest {
  pastaId?: number
  tipoDocumentoId: number
  nome: string
  descricao?: string
  palavrasChave?: string
  entidadeTipo?: string
  entidadeId?: number
  permiteExclusao?: boolean
  permiteAlteracao?: boolean
  assinado?: boolean
  dataFimVigencia?: string // ISO yyyy-MM-dd
}

export interface DocumentoResponse {
  id: number
  pastaId?: number
  pastaNome?: string
  tipoDocumentoId: number
  tipoDocumentoNome: string
  nome: string
  descricao?: string
  palavrasChave?: string
  entidadeTipo?: string
  entidadeId?: number
  permiteExclusao: boolean
  permiteAlteracao: boolean
  assinado: boolean
  dataFimVigencia?: string
  situacao: Situacao
  versoes: DocumentoVersaoResponse[]
}

export interface DocumentosFiltro {
  entidadeTipo?: string
  entidadeId?: number
}

/** Ver `entity/TipoAcaoVersao.java` — `EXCLUSAO` não tem arquivo associado, é só o registro do evento. */
export type TipoAcaoVersao = 'INCLUSAO' | 'ALTERACAO' | 'EXCLUSAO'

export const TIPO_ACAO_VERSAO_LABEL: Record<TipoAcaoVersao, string> = {
  INCLUSAO: 'Inclusão',
  ALTERACAO: 'Alteração',
  EXCLUSAO: 'Exclusão',
}

/** `chaveArmazenamento` deliberadamente não exposto pela API — detalhe interno de storage. */
export interface DocumentoVersaoResponse {
  id: number
  documentoId: number
  usuarioId?: number
  numeroVersao: number
  tipoAcao: TipoAcaoVersao
  hashArquivo?: string
  nomeArquivoOriginal?: string
  tamanhoBytes?: number
  contentType?: string
  observacao?: string
  dataCriacao: string
}
