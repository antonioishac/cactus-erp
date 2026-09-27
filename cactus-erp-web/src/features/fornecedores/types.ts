import type { Situacao } from '@shared/domain/situacao'
import type { ContatoDTO, EnderecoDTO, TelefoneDTO, TipoPessoa } from '@shared/domain/pessoa'

export type { ContatoDTO, EnderecoDTO, TelefoneDTO, TipoPessoa }

/**
 * Corpo de `POST`/`PUT /api/v1/fornecedores` — espelha `FornecedorRequest.java`.
 * Mesmo padrão de `ClienteRequest` (CPF obrigatório se FISICA, CNPJ se
 * JURIDICA, validado no backend) — sem `url`/limite de crédito/fidelidade
 * (não fazem sentido pro lado fornecedor), com campos próprios de compra:
 * `fornecedorDesde`/`prazoEntregaDias`/`observacao`.
 */
export interface FornecedorRequest {
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
  fornecedorDesde?: string // ISO yyyy-MM-dd
  prazoEntregaDias?: number
  observacao?: string
  situacao: Situacao
}

/** Espelha `FornecedorResponse.java`. */
export interface FornecedorResponse extends Omit<FornecedorRequest, 'cpf' | 'cnpj'> {
  id: number
  pessoaId: number
  documento: string
  dataCadastro?: string
  dataCriacao: string
  dataAtualizacao: string
}

export interface FornecedoresFiltro {
  nome?: string
  documento?: string
  tipoPessoa?: TipoPessoa
  situacao?: Situacao
}
