import type { Situacao } from '@shared/domain/situacao'
import type { ContatoDTO, EnderecoDTO, TelefoneDTO, TipoPessoa } from '@shared/domain/pessoa'

export type { ContatoDTO, EnderecoDTO, TelefoneDTO, TipoPessoa }

/** Ver `entity/FidelidadeAviso.java`. */
export type FidelidadeAviso = 'QUANTIDADE' | 'VALOR'

/**
 * Corpo de `POST`/`PUT /api/v1/clientes` — espelha `ClienteRequest.java`. CPF
 * obrigatório quando `tipoPessoa=FISICA`, CNPJ quando `JURIDICA` (validado no
 * backend, regra cruzada entre campos — o form aqui replica a mesma condição
 * só pra UX, a fonte da verdade da regra continua sendo o backend).
 */
export interface ClienteRequest {
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
  url?: string
  enderecos: EnderecoDTO[]
  contatos: ContatoDTO[]
  telefones: TelefoneDTO[]
  limiteCredito?: number
  taxaDesconto?: number
  fidelidadeAviso?: FidelidadeAviso
  fidelidadeQuantidade?: number
  fidelidadeValor?: number
  situacao: Situacao
}

/** Espelha `ClienteResponse.java`. */
export interface ClienteResponse extends Omit<ClienteRequest, 'cpf' | 'cnpj'> {
  id: number
  pessoaId: number
  documento: string
  dataCadastro?: string
  dataCriacao: string
  dataAtualizacao: string
}

export interface ClientesFiltro {
  nome?: string
  documento?: string
  tipoPessoa?: TipoPessoa
  situacao?: Situacao
}
