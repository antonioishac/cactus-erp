import type { Situacao } from '@shared/domain/situacao'

/** Ver `entity/TipoEmpresa.java` — validado contra `fenix.sql.EMPRESA.TIPO`. */
export type TipoEmpresa = 'MATRIZ' | 'FILIAL'

/** Ver `entity/TipoEstabelecimento.java`. */
export type TipoEstabelecimento = 'VAREJO' | 'RESTAURANTE' | 'BAR' | 'SUPERMERCADO' | 'AUTOCENTER' | 'OUTRO'

/** Ver `entity/AmbienteFiscal.java`. */
export type AmbienteFiscal = 'HOMOLOGACAO' | 'PRODUCAO'

/** A1 = arquivo (.pfx/.p12) | A3 = hardware (token/smartcard) — ver `entity/TipoCertificadoDigital.java`. */
export type TipoCertificadoDigital = 'A1' | 'A3'

/**
 * Corpo de `POST`/`PUT /api/v1/empresas` — espelha `EmpresaRequest.java`.
 * Diferente de Cliente/Fornecedor/Colaborador: **sem** lista de endereços —
 * um endereço único inline (empresa não tem múltiplos endereços cadastrados
 * aqui, o de fato usado é o da própria filial).
 */
export interface EmpresaRequest {
  razaoSocial: string
  nomeFantasia?: string
  cnpj?: string
  inscricaoEstadual?: string
  crt?: string // '1' | '2' | '3'
  email?: string
  site?: string
  contato?: string
  dataConstituicao?: string // ISO yyyy-MM-dd
  tipo?: TipoEmpresa
  tipoEstabelecimento: TipoEstabelecimento
  inscricaoJuntaComercial?: string
  dataInscricaoJuntaComercial?: string
  cei?: string
  codigoCnaePrincipal?: string
  contadorId?: number
  logradouro?: string
  numero?: string
  complemento?: string
  bairro?: string
  cep?: string
  municipio?: string
  uf?: string
  codigoIbgeMunicipio?: number
  codigoIbgeUf?: number
  telefone?: string
  situacao: Situacao
}

/** Espelha `EmpresaResponse.java`. */
export interface EmpresaResponse extends EmpresaRequest {
  id: number
  contadorNome?: string
}

export interface EmpresasFiltro {
  nome?: string
}

/** Um registro por ano-calendário — histórico append-only, nunca editado/removido (ver `EmpresaController`, só tem `POST`). */
export interface EmpresaRegimeTributarioRequest {
  anoCalendario: number
  crt: string
}

export interface EmpresaRegimeTributarioResponse {
  id: number
  empresaId: number
  anoCalendario: number
  crt: string
}

/** Sub-recurso 1:1, upsert (`PUT` cria na primeira chamada) — mesas/comandas/delivery + limites de desconto/acréscimo empurrados pro PDV. */
export interface ConfiguracaoOperacionalLojaRequest {
  permiteMesas: boolean
  permiteComandas: boolean
  permiteDelivery: boolean
  modoComanda?: string // char(1) opaco — domínio não documentado no PDV, ver ConfiguracaoOperacionalLoja.java
  permiteMesaAvulsa: boolean
  limiteDescontoItemPercentual?: number
  limiteDescontoVendaPercentual?: number
  limiteDescontoVendaValor?: number
  limiteAcrescimoItemPercentual?: number
  limiteAcrescimoVendaPercentual?: number
  permiteDescontoAcimaLimite: boolean
}

export interface ConfiguracaoOperacionalLojaResponse extends ConfiguracaoOperacionalLojaRequest {
  id: number
  empresaId: number
}

/**
 * Sub-recurso 1:1, upsert — certificado digital, série/numeração NFe/NFCe/NFSe,
 * ambiente SEFAZ e CSC. `certificadoAlias` referencia uma entrada de keystore/
 * secret manager — a senha do certificado nunca trafega aqui, nem existe campo
 * pra ela nesta tela.
 */
export interface EmpresaConfiguracaoFiscalRequest {
  tipoCertificado?: TipoCertificadoDigital
  certificadoAlias?: string
  certificadoValidade?: string // ISO yyyy-MM-dd
  ambienteFiscal: AmbienteFiscal
  serieNfe?: string
  proximoNumeroNfe?: number
  serieNfce?: string
  proximoNumeroNfce?: number
  serieNfse?: string
  proximoNumeroNfse?: number
  csc?: string
  cscIdToken?: string
}

export interface EmpresaConfiguracaoFiscalResponse extends EmpresaConfiguracaoFiscalRequest {
  id: number
  empresaId: number
}

/** Lookup simples — ver `dto/ContadorRequest.java`. */
export interface ContadorRequest {
  nome: string
  crc: string
  email?: string
  telefone?: string
  situacao: Situacao
}

/** Ver `dto/ContadorResponse.java`. */
export interface ContadorResponse extends ContadorRequest {
  id: number
}
