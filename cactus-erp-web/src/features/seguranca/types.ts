import type { Situacao } from '@shared/domain/situacao'

// --- Módulo (só leitura, semeado via migration — 1 por serviço real) ---

export interface ModuloResponse {
  id: number
  codigo: string
  nome: string
  situacao: Situacao
}

// --- Funcionalidade (só leitura — código MODULO_RECURSO_ACAO, vira authority do JWT) ---

export interface FuncionalidadeResponse {
  id: number
  moduloId: number
  moduloCodigo: string
  codigo: string
  nome: string
  situacao: Situacao
}

// --- Papel (agrupador de Funcionalidades — "perfil" de acesso) ---

export interface PapelRequest {
  nome: string
  descricao?: string
  situacao: Situacao
}

export interface PapelResponse extends PapelRequest {
  id: number
  funcionalidades: FuncionalidadeResponse[]
}

/** PUT idempotente — substitui por completo o conjunto de funcionalidades do papel. */
export interface AtribuirFuncionalidadesRequest {
  funcionalidadeIds: number[]
}

// --- Usuário ---

/**
 * `senha` só é enviada na criação (obrigatória lá) — na atualização o campo
 * nem é incluído no request (troca de senha tem endpoint/modal próprio, ver
 * `AlterarSenhaRequest`). `ativo` só é lido pelo backend na criação; um PUT
 * de edição o ignora (`UsuarioService` nunca reescreve `ativo`/`bloqueado`
 * via PUT genérico — só via `inativar`/`reativar`/`bloquear`/`desbloquear`,
 * ver `UsuarioController`), então o formulário de edição nunca envia esse
 * campo para não sugerir um controle que não tem efeito.
 */
export interface UsuarioRequest {
  login: string
  senha?: string
  nome: string
  email?: string
  colaboradorId?: number
  ativo: boolean
}

export interface UsuarioResponse {
  id: number
  login: string
  nome: string
  email?: string
  /** `null` (não `undefined`) quando não há vínculo — é assim que o Jackson serializa um `Long` ausente em Java, não omitindo a chave. */
  colaboradorId: number | null
  ativo: boolean
  bloqueado: boolean
  dataUltimoLogin?: string
}

export interface UsuariosFiltro {
  busca?: string
}

export interface AlterarSenhaRequest {
  novaSenha: string
}

// --- Papel por Empresa (é essa associação que define as authorities do token) ---

export interface UsuarioPapelEmpresaRequest {
  papelId: number
  empresaId: number
}

/** `papelNome` vem resolvido (Papel mora neste mesmo serviço); `empresaNome` não — Empresa mora no cadastro-base, referência solta cross-serviço. */
export interface UsuarioPapelEmpresaResponse {
  id: number
  papelId: number
  papelNome: string
  empresaId: number
}

// --- Lookups cross-serviço (cadastro-base), só pra alimentar Selects — mesmo padrão de features/estoque (busca local, não reaproveita features/empresas ou features/colaboradores) ---

export interface EmpresaResumo {
  id: number
  razaoSocial: string
  nomeFantasia?: string
}

export interface ColaboradorResumo {
  id: number
  nome: string
}
