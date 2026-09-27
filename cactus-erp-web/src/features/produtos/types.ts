import type { Situacao } from '@shared/domain/situacao'

/** Ver `entity/TipoItemSped.java` — 12 códigos oficiais do Registro 0200 do SPED Fiscal. */
export type TipoItemSped =
  | 'MERCADORIA_PARA_REVENDA'
  | 'MATERIA_PRIMA'
  | 'EMBALAGEM'
  | 'PRODUTO_EM_PROCESSO'
  | 'PRODUTO_ACABADO'
  | 'SUBPRODUTO'
  | 'PRODUTO_INTERMEDIARIO'
  | 'MATERIAL_USO_CONSUMO'
  | 'ATIVO_IMOBILIZADO'
  | 'SERVICOS'
  | 'OUTROS_INSUMOS'
  | 'OUTRAS'

export const TIPO_ITEM_SPED_LABEL: Record<TipoItemSped, string> = {
  MERCADORIA_PARA_REVENDA: '00 — Mercadoria para revenda',
  MATERIA_PRIMA: '01 — Matéria-prima',
  EMBALAGEM: '02 — Embalagem',
  PRODUTO_EM_PROCESSO: '03 — Produto em processo',
  PRODUTO_ACABADO: '04 — Produto acabado',
  SUBPRODUTO: '05 — Subproduto',
  PRODUTO_INTERMEDIARIO: '06 — Produto intermediário',
  MATERIAL_USO_CONSUMO: '07 — Material de uso e consumo',
  ATIVO_IMOBILIZADO: '08 — Ativo imobilizado',
  SERVICOS: '09 — Serviços',
  OUTROS_INSUMOS: '10 — Outros insumos',
  OUTRAS: '99 — Outras',
}

/** Ver `entity/IndicadorArredondamentoTruncamento.java`. */
export type IndicadorArredondamentoTruncamento = 'ARREDONDAMENTO' | 'TRUNCAMENTO'

/** Ver `entity/IndicadorProprioTerceiro.java` — mercadoria própria ou em consignação de terceiro. */
export type IndicadorProprioTerceiro = 'PROPRIO' | 'TERCEIRO'

/** Ver `entity/CstIcms.java` — só leitura (campo cacheado, resolvido pelo motor tributário). */
export type CstIcms =
  | 'CST_00'
  | 'CST_10'
  | 'CST_20'
  | 'CST_30'
  | 'CST_40'
  | 'CST_41'
  | 'CST_50'
  | 'CST_51'
  | 'CST_60'
  | 'CST_70'
  | 'CST_90'

export const CST_ICMS_LABEL: Record<CstIcms, string> = {
  CST_00: '00 — Tributada integralmente',
  CST_10: '10 — Tributada com cobrança de ICMS por ST',
  CST_20: '20 — Com redução de base de cálculo',
  CST_30: '30 — Isenta/não tributada com cobrança de ICMS por ST',
  CST_40: '40 — Isenta',
  CST_41: '41 — Não tributada',
  CST_50: '50 — Suspensão',
  CST_51: '51 — Diferimento',
  CST_60: '60 — ICMS cobrado anteriormente por ST',
  CST_70: '70 — Redução de base de cálculo com cobrança de ICMS por ST',
  CST_90: '90 — Outras',
}

/** Ver `entity/Csosn.java` — só leitura, usado no lugar do CST quando CRT=Simples Nacional. */
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
  CSOSN_103: '103 — Isenção do ICMS para faixa de receita bruta',
  CSOSN_201: '201 — Tributada c/ permissão de crédito e cobrança de ICMS por ST',
  CSOSN_202: '202 — Tributada s/ permissão de crédito e cobrança de ICMS por ST',
  CSOSN_203: '203 — Isenção de faixa de receita bruta e cobrança de ICMS por ST',
  CSOSN_300: '300 — Imune',
  CSOSN_400: '400 — Não tributada pelo Simples Nacional',
  CSOSN_500: '500 — ICMS cobrado anteriormente por ST/antecipação',
  CSOSN_900: '900 — Outros',
}

/**
 * Faixa de preço por quantidade — UM conceito só cobrindo dois casos de uso
 * (ver Javadoc de `ProdutoFaixaPrecoDTO.java`): `quantidadeMinima=1` +
 * `dataInicio`/`dataFim` preenchidos = promoção temporária; `quantidadeMinima>1`
 * sem vigência = desconto permanente por atacado. Lista inteira é substituída
 * a cada `PUT /produtos/{id}` (sem id próprio no request/response).
 */
export interface ProdutoFaixaPrecoDTO {
  quantidadeMinima: number
  valorUnitario: number
  rotulo?: string
  dataInicio?: string // ISO yyyy-MM-dd
  dataFim?: string // ISO yyyy-MM-dd
  situacao: Situacao
}

/** Upload direto ainda não existe (depende do futuro GED) — só imagem já hospedada, referenciada por URL. */
export interface ProdutoImagemDTO {
  url: string
  hashArquivo?: string
  principal: boolean
  ordem?: number
  situacao: Situacao
}

/** Espelha `ProdutoRequest.java`. */
export interface ProdutoRequest {
  produtoUnidadeId: number
  produtoMarcaId?: number
  produtoSubgrupoId: number
  produtoTipoId?: number
  gtin?: string
  codigoInterno: string
  nome: string
  descricao?: string
  descricaoPdv?: string

  ncmId: number
  cestId?: number
  grupoTributarioId: number
  tipoItemSped?: TipoItemSped
  iat?: IndicadorArredondamentoTruncamento
  ippt?: IndicadorProprioTerceiro
  totalizadorParcial?: string
  codigoBalanca?: number
  pafPSt?: string

  valorCusto?: number
  valorCompra?: number
  valorVenda?: number
  estoqueMinimo?: number
  estoqueMaximo?: number

  situacao: Situacao
  faixasPreco: ProdutoFaixaPrecoDTO[]
  imagens: ProdutoImagemDTO[]
}

/** Espelha `ProdutoResponse.java` — inclui os nomes resolvidos de todo FK e a "tributação resolvida padrão" cacheada. */
export interface ProdutoResponse extends ProdutoRequest {
  id: number
  produtoUnidadeSigla?: string
  produtoMarcaNome?: string
  produtoSubgrupoNome?: string
  produtoGrupoId?: number
  produtoGrupoNome?: string
  produtoTipoDescricao?: string
  ncmCodigo?: string
  cestCodigo?: string
  grupoTributarioNome?: string

  cstIcmsPadrao?: CstIcms
  csosnPadrao?: Csosn
  aliquotaIcmsPadrao?: number
  aliquotaIpiPadrao?: number
  aliquotaPisPadrao?: number
  aliquotaCofinsPadrao?: number
  aliquotaIssqnPadrao?: number
  dataResolucaoPadrao?: string

  dataCriacao: string
  dataAtualizacao: string
}

export interface ProdutosFiltro {
  nome?: string
  codigoInterno?: string
  gtin?: string
  produtoSubgrupoId?: number
  produtoMarcaId?: number
  situacao?: Situacao
}

export interface RecalcularTributacaoPadraoRequest {
  operacaoFiscalId: number
  ufDestino: string
}

// --- Lookups do Produto (Grupo/Subgrupo/Tipo/Unidade/Marca) ---

export interface ProdutoGrupoRequest {
  nome: string
  descricao?: string
  situacao: Situacao
}
export interface ProdutoGrupoResponse extends ProdutoGrupoRequest {
  id: number
}

export interface ProdutoSubgrupoRequest {
  produtoGrupoId: number
  nome: string
  descricao?: string
  situacao: Situacao
}
export interface ProdutoSubgrupoResponse extends ProdutoSubgrupoRequest {
  id: number
  produtoGrupoNome?: string
}

export interface ProdutoTipoRequest {
  codigo: string
  descricao: string
  situacao: Situacao
}
export interface ProdutoTipoResponse extends ProdutoTipoRequest {
  id: number
}

export interface ProdutoUnidadeRequest {
  sigla: string
  descricao: string
  podeFracionar: boolean
  situacao: Situacao
}
export interface ProdutoUnidadeResponse extends ProdutoUnidadeRequest {
  id: number
}

export interface ProdutoMarcaRequest {
  nome: string
  situacao: Situacao
}
export interface ProdutoMarcaResponse extends ProdutoMarcaRequest {
  id: number
}

// --- Catálogos fiscais consumidos só como referência (sem "+" de criação nesta tela) ---

export interface NcmResponse {
  id: number
  codigo: string
  descricao: string
}

export interface CestResponse {
  id: number
  codigo: string
  descricao: string
  ncmId?: number
  ncmCodigo?: string
}

export interface GrupoTributarioResponse {
  id: number
  nome: string
  origemMercadoria: string // char(1), "0"-"9" — código de origem da mercadoria da NF-e
  observacao?: string
  situacao: Situacao
}

export interface OperacaoFiscalResponse {
  id: number
  nome: string
  descricaoNaNf?: string
  cfopId: number
  cfopCodigo?: number
  observacao?: string
  situacao: Situacao
}
