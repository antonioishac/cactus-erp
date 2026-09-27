import type { Situacao } from '@shared/domain/situacao'
import type { ContatoDTO, EnderecoDTO, TelefoneDTO, TipoPessoa } from '@shared/domain/pessoa'

export type { ContatoDTO, EnderecoDTO, TelefoneDTO, TipoPessoa }

/**
 * Corpo de `POST`/`PUT /api/v1/transportadoras` — espelha `TransportadoraRequest.java`.
 * Mesmo padrão de `FornecedorRequest` (CPF obrigatório se FISICA, CNPJ se
 * JURIDICA, validado no backend) — sem `url`/comercial/fidelidade, com campo
 * próprio `rntrc` (Registro Nacional de Transportadores, obrigatório em CT-e).
 */
export interface TransportadoraRequest {
  nome: string
  tipoPessoa: TipoPessoa
  cpf?: string
  rg?: string
  dataNascimento?: string // ISO yyyy-MM-dd
  cnpj?: string
  nomeFantasia?: string
  inscricaoEstadual?: string
  inscricaoMunicipal?: string
  email?: string
  enderecos: EnderecoDTO[]
  contatos: ContatoDTO[]
  telefones: TelefoneDTO[]
  rntrc?: string
  observacao?: string
  situacao: Situacao
}

/** Espelha `TransportadoraResponse.java`. */
export interface TransportadoraResponse extends Omit<TransportadoraRequest, 'cpf' | 'cnpj'> {
  id: number
  pessoaId: number
  documento: string
  dataCadastro?: string
  dataCriacao: string
  dataAtualizacao: string
}

export interface TransportadorasFiltro {
  nome?: string
  documento?: string
  tipoPessoa?: TipoPessoa
  situacao?: Situacao
}
