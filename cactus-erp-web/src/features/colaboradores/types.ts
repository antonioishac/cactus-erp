import type { Situacao } from '@shared/domain/situacao'
import type { ContatoDTO, EnderecoDTO, TelefoneDTO, TipoPessoa } from '@shared/domain/pessoa'

export type { ContatoDTO, EnderecoDTO, TelefoneDTO, TipoPessoa }

/** Ver `entity/TipoVeiculoEntregador.java` — só relevante quando o colaborador atua como entregador. */
export type TipoVeiculoEntregador = 'CARRO' | 'MOTO' | 'BICICLETA' | 'APLICATIVO'

/** Lookup simples — ver `dto/CargoRequest.java`. */
export interface CargoRequest {
  nome: string
  descricao?: string
  salario?: number
  cbo1994?: string
  cbo2002?: string
  situacao: Situacao
}

/** Ver `dto/CargoResponse.java`. */
export interface CargoResponse extends CargoRequest {
  id: number
}

/** Lookup simples — ver `dto/SetorRequest.java`. */
export interface SetorRequest {
  nome: string
  descricao?: string
  situacao: Situacao
}

/** Ver `dto/SetorResponse.java`. */
export interface SetorResponse extends SetorRequest {
  id: number
}

/**
 * Corpo de `POST`/`PUT /api/v1/colaboradores` — espelha `ColaboradorRequest.java`.
 * Mesma base de Pessoa de Cliente/Fornecedor, mais RH (cargo/setor/matrícula/
 * admissão/CTPS) e comissão/entregador — validado contra `BANCO_PDV.md.COLABORADOR`.
 */
export interface ColaboradorRequest {
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
  cargoId?: number
  setorId?: number
  matricula?: string
  dataAdmissao?: string
  dataDemissao?: string
  ctpsNumero?: string
  ctpsSerie?: string
  ctpsDataExpedicao?: string
  ctpsUf?: string
  comissaoVista?: number
  comissaoPrazo?: number
  entregadorVeiculo?: TipoVeiculoEntregador
  observacao?: string
  situacao: Situacao
}

/** Espelha `ColaboradorResponse.java`. */
export interface ColaboradorResponse extends Omit<ColaboradorRequest, 'cpf' | 'cnpj'> {
  id: number
  pessoaId: number
  documento: string
  cargoNome?: string
  setorNome?: string
  dataCriacao: string
  dataAtualizacao: string
}

export interface ColaboradoresFiltro {
  nome?: string
  documento?: string
  cargoId?: number
  setorId?: number
  situacao?: Situacao
}
