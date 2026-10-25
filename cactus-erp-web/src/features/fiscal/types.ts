export type SituacaoNotaFiscal = 'RASCUNHO' | 'ENVIADA' | 'AUTORIZADA' | 'REJEITADA' | 'ERRO' | 'CANCELADA' | 'ENCERRADA'
export type AmbienteFiscal = 'PRODUCAO' | 'HOMOLOGACAO'
export type TipoTomadorCte = 'REMETENTE' | 'EXPEDIDOR' | 'RECEBEDOR' | 'DESTINATARIO' | 'OUTROS'

export const SITUACAO_NOTA_FISCAL_LABEL: Record<SituacaoNotaFiscal, string> = {
  RASCUNHO: 'Rascunho',
  ENVIADA: 'Enviada',
  AUTORIZADA: 'Autorizada',
  REJEITADA: 'Rejeitada',
  ERRO: 'Erro',
  CANCELADA: 'Cancelada',
  ENCERRADA: 'Encerrada',
}

export const AMBIENTE_FISCAL_LABEL: Record<AmbienteFiscal, string> = {
  PRODUCAO: 'Produção',
  HOMOLOGACAO: 'Homologação',
}

export const TIPO_TOMADOR_CTE_LABEL: Record<TipoTomadorCte, string> = {
  REMETENTE: 'Remetente',
  EXPEDIDOR: 'Expedidor',
  RECEBEDOR: 'Recebedor',
  DESTINATARIO: 'Destinatário',
  OUTROS: 'Outros',
}

// --- NF-e ---

export interface NotaFiscalEletronicaItemResponse {
  id: number
  numeroItem: number
  produtoId: number
  descricao: string
  gtin?: string
  ncm?: string
  cfop?: string
  unidade?: string
  quantidade: number
  valorUnitario: number
  valorTotal: number
  valorDesconto?: number
  cstIcms?: string
  csosn?: string
  aliquotaIcms?: number
  valorIcms?: number
  aliquotaIpi?: number
  valorIpi?: number
  aliquotaPis?: number
  valorPis?: number
  aliquotaCofins?: number
  valorCofins?: number
}

export interface NotaFiscalEletronicaResponse {
  id: number
  vendaId: number
  empresaId: number
  clienteId: number
  transportadoraId?: number
  modelo?: string
  serie?: string
  numero?: number
  chaveAcesso?: string
  protocoloAutorizacao?: string
  dataEmissao?: string
  ambiente?: AmbienteFiscal
  valorTotal: number
  situacao: SituacaoNotaFiscal
  mensagemSefaz?: string
  itens: NotaFiscalEletronicaItemResponse[]
}

// --- CT-e ---

export interface CteComponenteResponse {
  id: number
  nome: string
  valor: number
}

export interface CteCabecalhoResponse {
  id: number
  vendaId: number
  empresaId: number
  clienteId: number
  transportadoraId?: number
  modelo?: string
  modal?: string
  serie?: string
  numero?: number
  chaveAcesso?: string
  protocoloAutorizacao?: string
  dataEmissao?: string
  ambiente?: AmbienteFiscal
  tomador: TipoTomadorCte
  naturezaOperacao?: string
  cfop?: string
  produtoPredominante?: string
  observacoesGerais?: string
  rntrc?: string
  dataPrevistaEntrega?: string
  indicadorLotacao?: string
  ciot?: string
  placa?: string
  ufPlaca?: string
  pesoBruto?: number
  quantidadeVolume?: number
  valorTotalServico: number
  valorReceber?: number
  valorTotalCarga?: number
  cst?: string
  baseCalculoIcms?: number
  aliquotaIcms?: number
  valorIcms?: number
  situacao: SituacaoNotaFiscal
  mensagemSefaz?: string
  componentes: CteComponenteResponse[]
}

export interface GerarCteComponenteRequest {
  nome: string
  valor: number
}

export interface GerarCteDeVendaRequest {
  tomador: TipoTomadorCte
  valorTotalServico: number
  aliquotaIcms?: number
  produtoPredominante?: string
  observacoesGerais?: string
  indicadorLotacao?: string
  dataPrevistaEntrega?: string
  ciot?: string
  componentes?: GerarCteComponenteRequest[]
}

// --- NFS-e ---

export interface ListaServicoMunicipalRequest {
  codigo: string
  descricao: string
  codigoTributacaoMunicipio?: string
  aliquotaIssPadrao?: number
}

export interface ListaServicoMunicipalResponse {
  id: number
  codigo: string
  descricao: string
  codigoTributacaoMunicipio?: string
  aliquotaIssPadrao?: number
}

export interface NotaFiscalServicoItemResponse {
  id: number
  listaServicoId: number
  listaServicoDescricao: string
  discriminacao?: string
  codigoTributacaoMunicipio?: string
  valorServicos: number
  aliquotaIss?: number
  valorIss?: number
}

export interface NotaFiscalServicoResponse {
  id: number
  osAberturaId: number
  empresaId: number
  clienteId: number
  numeroRps?: string
  serieRps?: string
  numero?: string
  codigoVerificacao?: string
  competencia?: string
  dataEmissao?: string
  ambiente?: AmbienteFiscal
  valorServicos: number
  valorIss: number
  valorLiquido?: number
  situacao: SituacaoNotaFiscal
  mensagemSefaz?: string
  itens: NotaFiscalServicoItemResponse[]
}

/** `listaServicoId` é aplicado a TODOS os itens SERVICO da OS de origem (simplificação deliberada, ver Javadoc do backend). */
export interface GerarNotaServicoRequest {
  numeroRps: string
  serieRps?: string
  competencia: string
  listaServicoId: number
}

// --- Lookups cross-serviço ---

export interface ClienteResumo {
  id: number
  nome: string
}

export interface TransportadoraResumo {
  id: number
  nome: string
}

// --- MDF-e ---

export interface MdfeDocumentoVinculadoResponse {
  id: number
  cteCabecalhoId: number
  cteSerie?: string
  cteNumero?: number
  cteChaveAcesso?: string
  municipioDescargaCodigoIbge?: number
  municipioDescargaNome?: string
}

export interface MdfeCabecalhoResponse {
  id: number
  empresaId: number
  modelo: string
  modal: string
  serie?: string
  numero?: number
  chaveAcesso?: string
  protocoloAutorizacao?: string
  dataEmissao?: string
  ambiente?: AmbienteFiscal
  uf: string
  rntrc?: string
  veiculoPlaca?: string
  veiculoUf?: string
  veiculoRenavam?: string
  veiculoTaraKg?: number
  veiculoCapacidadeKg?: number
  condutorNome?: string
  condutorCpf?: string
  municipioCarregamentoCodigoIbge?: number
  municipioCarregamentoNome?: string
  dataInicioViagem?: string
  valorTotalCarga?: number
  pesoBrutoTotal?: number
  unidadeMedidaCarga: string
  situacao: SituacaoNotaFiscal
  mensagemSefaz?: string
  dataEncerramento?: string
  municipioEncerramentoCodigoIbge?: number
  protocoloEncerramento?: string
  dataCancelamento?: string
  justificativaCancelamento?: string
  protocoloCancelamento?: string
  documentos: MdfeDocumentoVinculadoResponse[]
}

export interface CteDisponivelMdfe {
  id: number
  serie?: string
  numero?: number
  vendaId: number
  valorTotalCarga?: number
  pesoBruto?: number
}

export interface GerarMdfeRequest {
  empresaId: number
  cteIds: number[]
  veiculoPlaca: string
  veiculoUf?: string
  veiculoRenavam?: string
  veiculoTaraKg: number
  veiculoCapacidadeKg?: number
  condutorNome: string
  condutorCpf: string
  rntrc?: string
  dataInicioViagem?: string
}

export interface EncerrarMdfeRequest {
  dataEncerramento?: string
  municipioCodigoIbge?: number
}

export interface CancelarMdfeRequest {
  justificativa: string
}

export const COR_SITUACAO_MDFE: Record<SituacaoNotaFiscal, string> = {
  RASCUNHO: 'default',
  ENVIADA: 'processing',
  AUTORIZADA: 'success',
  REJEITADA: 'error',
  ERRO: 'error',
  CANCELADA: 'default',
  ENCERRADA: 'blue',
}
