import type { Situacao } from '@shared/domain/situacao'

// --- Grupo Tributário ---

export interface GrupoTributarioRequest {
  nome: string
  origemMercadoria: string
  observacao?: string
  situacao: Situacao
}

export interface GrupoTributarioResponse {
  id: number
  nome: string
  origemMercadoria: string
  observacao?: string
  situacao: Situacao
}

/** Campo `orig` da NFe (dígito 0-9) — validado contra `fenix.sql`/legislação, não um catálogo do backend. */
export const ORIGEM_MERCADORIA_OPCOES = [
  { value: '0', label: '0 — Nacional' },
  { value: '1', label: '1 — Estrangeira (importação direta)' },
  { value: '2', label: '2 — Estrangeira (adquirida no mercado interno)' },
  { value: '3', label: '3 — Nacional (conteúdo de importação > 40%)' },
  { value: '4', label: '4 — Nacional (processos produtivos básicos)' },
  { value: '5', label: '5 — Nacional (conteúdo de importação ≤ 40%)' },
  { value: '6', label: '6 — Estrangeira (importação direta, sem similar nacional)' },
  { value: '7', label: '7 — Estrangeira (mercado interno, sem similar nacional)' },
  { value: '8', label: '8 — Nacional (conteúdo de importação > 70%)' },
]

// --- Operação Fiscal + ISS (1:1) ---

export type ModalidadeBaseCalculoIss = 'VALOR_OPERACAO' | 'OUTROS'
export type CodigoTributacaoIss = 'NORMAL' | 'RETIDA' | 'SUBSTITUTA' | 'ISENTA'

export const MODALIDADE_BASE_CALCULO_ISS_LABEL: Record<ModalidadeBaseCalculoIss, string> = {
  VALOR_OPERACAO: 'Valor da operação',
  OUTROS: 'Outros',
}

export const CODIGO_TRIBUTACAO_ISS_LABEL: Record<CodigoTributacaoIss, string> = {
  NORMAL: 'Normal',
  RETIDA: 'Retida',
  SUBSTITUTA: 'Substituta',
  ISENTA: 'Isenta',
}

export interface IssConfiguracaoDTO {
  modalidadeBaseCalculo?: ModalidadeBaseCalculoIss
  porcentoBaseCalculo?: number
  aliquotaPercentual?: number
  aliquotaUnidade?: number
  valorPrecoMaximo?: number
  valorPautaFiscal?: number
  itemListaServico?: number
  codigoTributacao?: CodigoTributacaoIss
}

export interface OperacaoFiscalRequest {
  nome: string
  descricaoNaNf?: string
  cfopId?: number
  observacao?: string
  situacao: Situacao
  issConfiguracao?: IssConfiguracaoDTO
}

export interface OperacaoFiscalResponse {
  id: number
  nome: string
  descricaoNaNf?: string
  cfopId?: number
  cfopCodigo?: number
  observacao?: string
  situacao: Situacao
  issConfiguracao?: IssConfiguracaoDTO
}

export interface CfopResponse {
  id: number
  codigo: number
  descricao: string
  aplicacao?: string
}

// --- Configuração Tributária (a célula Grupo × Operação) ---

export type ModalidadeBaseCalculo = 'PERCENTUAL' | 'UNIDADE'
export const MODALIDADE_BASE_CALCULO_LABEL: Record<ModalidadeBaseCalculo, string> = {
  PERCENTUAL: 'Percentual',
  UNIDADE: 'Por unidade',
}

export type ModalidadeBaseCalculoIcms = 'MARGEM_VALOR_AGREGADO' | 'PAUTA' | 'PRECO_TABELADO_MAXIMO' | 'VALOR_OPERACAO'
export const MODALIDADE_BASE_CALCULO_ICMS_LABEL: Record<ModalidadeBaseCalculoIcms, string> = {
  MARGEM_VALOR_AGREGADO: 'Margem de valor agregado',
  PAUTA: 'Pauta',
  PRECO_TABELADO_MAXIMO: 'Preço tabelado máximo',
  VALOR_OPERACAO: 'Valor da operação',
}

export type ModalidadeBaseCalculoIcmsSt =
  | 'PRECO_TABELADO_OU_MAXIMO'
  | 'LISTA_NEGATIVA'
  | 'LISTA_POSITIVA'
  | 'LISTA_NEUTRA'
  | 'MARGEM_VALOR_AGREGADO'
  | 'PAUTA'
export const MODALIDADE_BASE_CALCULO_ICMS_ST_LABEL: Record<ModalidadeBaseCalculoIcmsSt, string> = {
  PRECO_TABELADO_OU_MAXIMO: 'Preço tabelado ou máximo',
  LISTA_NEGATIVA: 'Lista negativa',
  LISTA_POSITIVA: 'Lista positiva',
  LISTA_NEUTRA: 'Lista neutra',
  MARGEM_VALOR_AGREGADO: 'Margem de valor agregado',
  PAUTA: 'Pauta',
}

/** Regime Normal — mutuamente exclusivo com `Csosn` (Simples Nacional); qual preencher depende do CRT da empresa, não validado aqui (regra de negócio do backend). */
export type CstIcms = 'CST_00' | 'CST_10' | 'CST_20' | 'CST_30' | 'CST_40' | 'CST_41' | 'CST_50' | 'CST_51' | 'CST_60' | 'CST_70' | 'CST_90'
export const CST_ICMS_LABEL: Record<CstIcms, string> = {
  CST_00: '00 — Tributada integralmente',
  CST_10: '10 — Tributada e com cobrança do ICMS por substituição tributária',
  CST_20: '20 — Com redução de base de cálculo',
  CST_30: '30 — Isenta ou não tributada e com cobrança do ICMS por substituição tributária',
  CST_40: '40 — Isenta',
  CST_41: '41 — Não tributada',
  CST_50: '50 — Suspensão',
  CST_51: '51 — Diferimento',
  CST_60: '60 — ICMS cobrado anteriormente por substituição tributária',
  CST_70: '70 — Com redução de base de cálculo e cobrança do ICMS por substituição tributária',
  CST_90: '90 — Outras',
}

export type Csosn =
  | 'CSOSN_101'
  | 'CSOSN_102'
  | 'CSOSN_103'
  | 'CSOSN_201'
  | 'CSOSN_202'
  | 'CSOSN_203'
  | 'CSOSN_300'
  | 'CSOSN_400'
  | 'CSOSN_500'
  | 'CSOSN_900'
export const CSOSN_LABEL: Record<Csosn, string> = {
  CSOSN_101: '101 — Tributada pelo Simples Nacional com permissão de crédito',
  CSOSN_102: '102 — Tributada pelo Simples Nacional sem permissão de crédito',
  CSOSN_103: '103 — Isenção do ICMS no Simples Nacional para faixa de receita bruta',
  CSOSN_201: '201 — Tributada pelo Simples Nacional com permissão de crédito e com cobrança do ICMS por ST',
  CSOSN_202: '202 — Tributada pelo Simples Nacional sem permissão de crédito e com cobrança do ICMS por ST',
  CSOSN_203: '203 — Isenção do ICMS no Simples Nacional para faixa de receita bruta e com cobrança do ICMS por ST',
  CSOSN_300: '300 — Imune',
  CSOSN_400: '400 — Não tributada pelo Simples Nacional',
  CSOSN_500: '500 — ICMS cobrado anteriormente por substituição tributária ou por antecipação',
  CSOSN_900: '900 — Outros',
}

export type CstPisCofins = 'CST_01' | 'CST_02' | 'CST_03' | 'CST_04' | 'CST_05' | 'CST_06' | 'CST_07' | 'CST_08' | 'CST_09' | 'CST_49'
export const CST_PIS_COFINS_LABEL: Record<CstPisCofins, string> = {
  CST_01: '01 — Operação Tributável com Alíquota Básica',
  CST_02: '02 — Operação Tributável com Alíquota Diferenciada',
  CST_03: '03 — Operação Tributável com Alíquota por Unidade de Medida de Produto',
  CST_04: '04 — Operação Tributável Monofásica - Revenda a Alíquota Zero',
  CST_05: '05 — Operação Tributável por Substituição Tributária',
  CST_06: '06 — Operação Tributável a Alíquota Zero',
  CST_07: '07 — Operação Isenta da Contribuição',
  CST_08: '08 — Operação sem Incidência da Contribuição',
  CST_09: '09 — Operação com Suspensão da Contribuição',
  CST_49: '49 — Outras Operações de Saída',
}

export type CstIpi = 'CST_50' | 'CST_51' | 'CST_52' | 'CST_53' | 'CST_54' | 'CST_55' | 'CST_99'
export const CST_IPI_LABEL: Record<CstIpi, string> = {
  CST_50: '50 — Saída tributada',
  CST_51: '51 — Saída tributada com alíquota zero',
  CST_52: '52 — Saída isenta',
  CST_53: '53 — Saída não-tributada',
  CST_54: '54 — Saída imune',
  CST_55: '55 — Saída com suspensão',
  CST_99: '99 — Outras saídas',
}

/** ICMS de UMA UF de destino — a célula tem 1 linha por UF (`icmsPorUf`, lista, substituída por inteiro a cada save, mesmo padrão de `ProdutoFaixaPreco`). */
export interface IcmsConfiguracaoDTO {
  ufDestino: string
  cstIcms?: CstIcms
  csosn?: Csosn
  modalidadeBaseCalculo?: ModalidadeBaseCalculoIcms
  aliquota?: number
  valorPauta?: number
  valorPrecoMaximo?: number
  mva?: number
  porcentoBaseCalculo?: number
  modalidadeBaseCalculoSt?: ModalidadeBaseCalculoIcmsSt
  aliquotaInternaSt?: number
  aliquotaInterestadualSt?: number
  porcentoBaseCalculoSt?: number
  aliquotaIcmsSt?: number
  valorPautaSt?: number
  valorPrecoMaximoSt?: number
}

export interface PisConfiguracaoDTO {
  cst?: CstPisCofins
  modalidadeBaseCalculo?: ModalidadeBaseCalculo
  porcentoBaseCalculo?: number
  aliquotaPercentual?: number
  aliquotaUnidade?: number
  valorPrecoMaximo?: number
  valorPautaFiscal?: number
}

export interface CofinsConfiguracaoDTO {
  cst?: CstPisCofins
  modalidadeBaseCalculo?: ModalidadeBaseCalculo
  porcentoBaseCalculo?: number
  aliquotaPercentual?: number
  aliquotaUnidade?: number
  valorPrecoMaximo?: number
  valorPautaFiscal?: number
}

export interface IpiConfiguracaoDTO {
  cst?: CstIpi
  modalidadeBaseCalculo?: ModalidadeBaseCalculo
  porcentoBaseCalculo?: number
  aliquotaPercentual?: number
  aliquotaUnidade?: number
  valorPrecoMaximo?: number
  valorPautaFiscal?: number
}

/**
 * IBS/CBS (Reforma Tributária, LC 214/2025): a célula só guarda a classificação oficial (cClassTrib).
 * CST, redução e indicadores vêm do catálogo oficial da SVRS; as alíquotas, da lei (por ano de emissão).
 * Ausente no PUT = remove a classificação — a tela precisa sempre reenviar o valor atual.
 */
export interface IbsCbsConfiguracaoRequest {
  classificacaoTributaria: string
}

export interface IbsCbsConfiguracaoResponse {
  classificacaoTributaria: string
  cst: string
  descricao: string
  percentualReducaoIbs: number
  percentualReducaoCbs: number
}

/** Catálogo oficial. `motivoNaoSuportada` preenchido = o ERP ainda não emite documento com esta classificação. */
export interface ClassificacaoTributariaIbsCbsResponse {
  codigo: string
  cst: string
  exigeGrupoTributacao: boolean
  reducaoAliquota: boolean
  descricao: string
  nomeReduzido: string | null
  percentualReducaoIbs: number
  percentualReducaoCbs: number
  tributacaoRegular: boolean
  aceitaNfe: boolean
  aceitaNfce: boolean
  urlLegislacao: string | null
  inicioVigencia: string
  fimVigencia: string | null
  motivoNaoSuportada: string | null
}

export interface ConfiguracaoTributariaRequest {
  grupoTributarioId: number
  operacaoFiscalId: number
  icmsPorUf: IcmsConfiguracaoDTO[]
  pis?: PisConfiguracaoDTO
  cofins?: CofinsConfiguracaoDTO
  ipi?: IpiConfiguracaoDTO
  ibsCbs?: IbsCbsConfiguracaoRequest
}

export interface ConfiguracaoTributariaResponse {
  id: number
  grupoTributarioId: number
  grupoTributarioNome: string
  operacaoFiscalId: number
  operacaoFiscalNome: string
  icmsPorUf: IcmsConfiguracaoDTO[]
  pis?: PisConfiguracaoDTO
  cofins?: CofinsConfiguracaoDTO
  ipi?: IpiConfiguracaoDTO
  ibsCbs?: IbsCbsConfiguracaoResponse | null
}
