/**
 * Tipos compartilhados por toda especialização de Pessoa no backend (Cliente,
 * Fornecedor, Colaborador — `cadastro-base-service`) — extraído de
 * `features/clientes/` quando Fornecedor precisou exatamente da mesma forma
 * (2026-09-01). Cross-cutting de propósito: cada entidade ganha só os campos
 * próprios (`ClienteRequest`/`FornecedorRequest`/...) em cima disso.
 */

/** Ver `entity/TipoPessoa.java`. */
export type TipoPessoa = 'FISICA' | 'JURIDICA'

/** Ver `entity/TipoContato.java`. */
export type TipoContato = 'EMAIL' | 'RESPONSAVEL' | 'OUTRO'

/** Ver `entity/TipoTelefone.java`. */
export type TipoTelefone = 'FIXO' | 'CELULAR' | 'COMERCIAL' | 'WHATSAPP'

/**
 * Um endereço pode servir mais de um papel ao mesmo tempo (`entrega`/`cobranca`/
 * `correspondencia`, além de `principal`) — ver Javadoc de `EnderecoDTO.java` no
 * backend, validado contra `fenix.sql.PESSOA_ENDERECO`.
 */
export interface EnderecoDTO {
  logradouro?: string
  numero?: string
  complemento?: string
  bairro?: string
  cep?: string
  municipio?: string
  uf?: string
  codigoIbgeMunicipio?: number
  codigoIbgeUf?: number
  principal: boolean
  entrega: boolean
  cobranca: boolean
  correspondencia: boolean
}

export interface ContatoDTO {
  tipoContato: TipoContato
  valor: string
  principal: boolean
}

export interface TelefoneDTO {
  tipoTelefone: TipoTelefone
  numero: string
  principal: boolean
}
