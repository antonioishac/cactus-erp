// --- Enums ---

/** `ABERTA`→(baixa parcial)→`PARCIAL`→(saldo zerado)→`PAGA`. `CANCELADA` a partir de `ABERTA`/`PARCIAL`
 * (só sem baixa ATIVA registrada). `RENEGOCIADA` — terminal, saldo substituído por título(s) novo(s)
 * via `renegociar()`, ver `entity/SituacaoTitulo.java`. `vencido` não é um valor desta enum — vem
 * calculado à parte na `Response`. */
export type SituacaoTitulo = 'ABERTA' | 'PARCIAL' | 'PAGA' | 'CANCELADA' | 'RENEGOCIADA'
export const SITUACAO_TITULO_LABEL: Record<SituacaoTitulo, string> = {
  ABERTA: 'Aberta',
  PARCIAL: 'Parcial',
  PAGA: 'Paga',
  CANCELADA: 'Cancelada',
  RENEGOCIADA: 'Renegociada',
}

/** `VENDA` — gerada automaticamente pelo vendas-service (1 por parcela). `PEDIDO_LOJA`/`MANUAL`
 * (avulso ou parcelado via `gerarParcelado`). `RENEGOCIACAO` — nascido de `renegociar()`. */
export type OrigemContaReceber = 'VENDA' | 'PEDIDO_LOJA' | 'MANUAL' | 'RENEGOCIACAO' | 'FIADO_PDV'
export const ORIGEM_CONTA_RECEBER_LABEL: Record<OrigemContaReceber, string> = {
  VENDA: 'Venda',
  PEDIDO_LOJA: 'Pedido (Loja Virtual)',
  MANUAL: 'Manual',
  RENEGOCIACAO: 'Renegociação',
  FIADO_PDV: 'Fiado (PDV)',
}

/** `COMPRA` — gerada automaticamente pelo gestao-compras-service quando um PedidoCompra vira
 * RECEBIDO_TOTAL (1 por parcela). `MANUAL` (avulso ou parcelado). `RENEGOCIACAO` — nascido de `renegociar()`. */
export type OrigemContaPagar = 'COMPRA' | 'MANUAL' | 'RENEGOCIACAO'
export const ORIGEM_CONTA_PAGAR_LABEL: Record<OrigemContaPagar, string> = {
  COMPRA: 'Compra',
  MANUAL: 'Manual',
  RENEGOCIACAO: 'Renegociação',
}

export type TipoNaturezaFinanceira = 'RECEITA' | 'DESPESA'
export const TIPO_NATUREZA_FINANCEIRA_LABEL: Record<TipoNaturezaFinanceira, string> = {
  RECEITA: 'Receita',
  DESPESA: 'Despesa',
}

// --- Baixa (compartilhada entre Contas a Receber/Pagar — mesmo DTO no backend) ---

/** `contaBancariaId` obrigatório — toda baixa gera um `MovimentoCaixaBanco` real na conta.
 * `chequeEmitidoId` só considerado em baixa de Contas a Pagar, `chequeRecebidoId` só em Contas a
 * Receber (nenhum dos dois exposto nesta fase — CRUD de Cheque é sub-fase futura do Caixa e Bancos).
 * `valorDesconto` só aceito se `dataBaixa <= título.descontoAte`. */
export interface BaixaRequest {
  dataBaixa?: string
  valorBaixa: number
  valorDesconto?: number
  formaPagamentoId?: number
  contaBancariaId: number
  observacao?: string
  usuarioId?: number
}

export interface BaixaResponse {
  id: number
  dataBaixa: string
  valorBaixa: number
  valorDesconto: number
  formaPagamentoId?: number
  contaBancariaId?: number
  chequeEmitidoId?: number
  chequeRecebidoId?: number
  observacao?: string
  usuarioId?: number
  estornado: boolean
  dataEstorno?: string
  motivoEstorno?: string
}

export interface EstornarBaixaRequest {
  motivo: string
}

export interface CancelarTituloRequest {
  motivo: string
}

export interface NovaParcelaRenegociacaoRequest {
  dataVencimento: string
  valor: number
}

export interface RenegociarTituloRequest {
  motivo: string
  novasParcelas: NovaParcelaRenegociacaoRequest[]
}

/** Nunca mexe em valor/vencimento/situação — só categorização. Fecha a lacuna de títulos nascidos
 * de integração (VENDA/COMPRA), que não têm como informar isso na geração automática. */
export interface AtualizarClassificacaoTituloRequest {
  naturezaFinanceiraId: number
  tipoDocumentoId: number
  contaBancariaId?: number
  descontoAte?: string
  percentualDesconto?: number
  percentualMultaAtraso?: number
  percentualJurosDiaAtraso?: number
}

// --- Contas a Receber ---

export interface ContaReceberRequest {
  empresaId: number
  clienteId: number
  naturezaFinanceiraId: number
  tipoDocumentoId: number
  contaBancariaId?: number
  numeroDocumento?: string
  descricao?: string
  dataEmissao: string
  dataVencimento: string
  descontoAte?: string
  percentualDesconto?: number
  percentualMultaAtraso?: number
  percentualJurosDiaAtraso?: number
  valorOriginal: number
}

export interface GerarContaReceberParceladaRequest {
  empresaId: number
  clienteId: number
  naturezaFinanceiraId: number
  tipoDocumentoId: number
  contaBancariaId?: number
  numeroDocumento?: string
  descricao?: string
  dataEmissao: string
  quantidadeParcelas: number
  valorTotal: number
  primeiroVencimento: string
  intervaloEntreParcelasDias: number
  descontoAte?: string
  percentualDesconto?: number
  percentualMultaAtraso?: number
  percentualJurosDiaAtraso?: number
}

/** `vencido`/`diasAtraso`/`valorMulta`/`valorJuros`/`valorAtualizado` são calculados (não
 * persistidos) — usam o override do título quando presente, senão o default de
 * `ConfiguracaoFinanceiroEmpresa`. */
export interface ContaReceberResponse {
  id: number
  empresaId: number
  clienteId: number
  origemTipo: OrigemContaReceber
  origemId?: number
  numeroDocumento?: string
  descricao?: string
  naturezaFinanceiraId?: number
  tipoDocumentoId?: number
  contaBancariaId?: number
  loteId?: number
  dataEmissao: string
  dataVencimento: string
  descontoAte?: string
  percentualDesconto?: number
  percentualMultaAtraso?: number
  percentualJurosDiaAtraso?: number
  valorOriginal: number
  valorPago: number
  valorDesconto: number
  valorSaldo: number
  situacao: SituacaoTitulo
  vencido: boolean
  diasAtraso?: number
  valorMulta: number
  valorJuros: number
  valorAtualizado: number
  motivoEncerramento?: string
  baixas: BaixaResponse[]
}

export interface ContasReceberFiltro {
  clienteId?: number
}

// --- Contas a Pagar (espelho exato de Contas a Receber, só clienteId→fornecedorId) ---

export interface ContaPagarRequest {
  empresaId: number
  fornecedorId: number
  naturezaFinanceiraId: number
  tipoDocumentoId: number
  contaBancariaId?: number
  numeroDocumento?: string
  descricao?: string
  dataEmissao: string
  dataVencimento: string
  descontoAte?: string
  percentualDesconto?: number
  percentualMultaAtraso?: number
  percentualJurosDiaAtraso?: number
  valorOriginal: number
}

export interface GerarContaPagarParceladaRequest {
  empresaId: number
  fornecedorId: number
  naturezaFinanceiraId: number
  tipoDocumentoId: number
  contaBancariaId?: number
  numeroDocumento?: string
  descricao?: string
  dataEmissao: string
  quantidadeParcelas: number
  valorTotal: number
  primeiroVencimento: string
  intervaloEntreParcelasDias: number
  descontoAte?: string
  percentualDesconto?: number
  percentualMultaAtraso?: number
  percentualJurosDiaAtraso?: number
}

export interface ContaPagarResponse {
  id: number
  empresaId: number
  fornecedorId: number
  origemTipo: OrigemContaPagar
  origemId?: number
  numeroDocumento?: string
  descricao?: string
  naturezaFinanceiraId?: number
  tipoDocumentoId?: number
  contaBancariaId?: number
  loteId?: number
  dataEmissao: string
  dataVencimento: string
  descontoAte?: string
  percentualDesconto?: number
  percentualMultaAtraso?: number
  percentualJurosDiaAtraso?: number
  valorOriginal: number
  valorPago: number
  valorDesconto: number
  valorSaldo: number
  situacao: SituacaoTitulo
  vencido: boolean
  diasAtraso?: number
  valorMulta: number
  valorJuros: number
  valorAtualizado: number
  motivoEncerramento?: string
  baixas: BaixaResponse[]
}

export interface ContasPagarFiltro {
  fornecedorId?: number
}

// --- Natureza Financeira (catálogo por empresa) ---

export interface NaturezaFinanceiraRequest {
  empresaId: number
  codigo: string
  descricao: string
  tipo: TipoNaturezaFinanceira
}

export interface NaturezaFinanceiraResponse {
  id: number
  empresaId: number
  codigo: string
  descricao: string
  tipo: TipoNaturezaFinanceira
  ativo: boolean
}

// --- Tipo de Documento Financeiro (catálogo GLOBAL) ---

export interface TipoDocumentoFinanceiroRequest {
  codigo: string
  descricao: string
}

export interface TipoDocumentoFinanceiroResponse {
  id: number
  codigo: string
  descricao: string
  ativo: boolean
}

// --- Configuração Financeira (1 por empresa) ---

/** `percentualMultaAtraso`/`percentualJurosDiaAtraso` nulos = sem cobrança de atraso (default). */
export interface ConfiguracaoFinanceiroEmpresaRequest {
  percentualMultaAtraso?: number
  percentualJurosDiaAtraso?: number
}

export interface ConfiguracaoFinanceiroEmpresaResponse {
  id: number
  empresaId: number
  percentualMultaAtraso?: number
  percentualJurosDiaAtraso?: number
}

// --- Lookups cross-serviço, só pra alimentar Selects (mesmo padrão já usado em Vendas/Compras) ---

export interface ClienteResumo {
  id: number
  nome: string
}

export interface FornecedorResumo {
  id: number
  nome: string
}

export interface FormaPagamentoResumo {
  id: number
  codigo: string
  descricao: string
}

export type TipoContaBancaria = 'CORRENTE' | 'POUPANCA' | 'CAIXA_INTERNO' | 'APLICACAO'
export const TIPO_CONTA_BANCARIA_LABEL: Record<TipoContaBancaria, string> = {
  CORRENTE: 'Conta Corrente',
  POUPANCA: 'Poupança',
  CAIXA_INTERNO: 'Caixa Interno',
  APLICACAO: 'Aplicação',
}

/** Resumo mínimo pra Select — CRUD completo de Conta Bancária fica pra uma sub-fase futura
 * (Caixa e Bancos), mas a baixa de título já exige `contaBancariaId` desde já. */
export interface ContaBancariaResumo {
  id: number
  nome: string
  tipo: TipoContaBancaria
  numero?: string
}

// --- Fase B: Caixa e Bancos, Cheques, Tesouraria, Fluxo de Caixa, Conciliação, EDI Bancário ---

export interface BancoResponse {
  id: number
  codigo: string
  nome: string
}

/** `bancoId` obrigatório quando `tipo !== 'CAIXA_INTERNO'` (validado no backend, não
 * expressável só com Bean Validation declarativo). Campos de convênio de cobrança
 * (`codigoConvenioCobranca`/`codigoCedenteCobranca`/`carteiraCobranca`) só fazem sentido
 * quando a conta gera remessa CNAB 240 (EDI Bancário). */
export interface ContaBancariaRequest {
  empresaId: number
  bancoId?: number
  tipo: TipoContaBancaria
  nome: string
  agencia?: string
  agenciaDigito?: string
  numero?: string
  numeroDigito?: string
  cedenteNome?: string
  cedenteDocumento?: string
  codigoConvenioCobranca?: string
  codigoCedenteCobranca?: string
  carteiraCobranca?: string
  dataSaldoInicial: string
  saldoInicial: number
}

/** `saldoAtual` só muda via `MovimentoCaixaBanco` — `PUT` desta conta nunca mexe em saldo. */
export interface ContaBancariaResponse extends ContaBancariaRequest {
  id: number
  proximoNossoNumero?: number
  saldoAtual: number
  /** Só nas contas criadas automaticamente pra um terminal do PDV (Fase 6-B). */
  pdvCaixaId?: number | null
  /** Instalação do PDV dona do terminal — a conta do terminal é única por dispositivo + terminal. */
  pdvDispositivoId?: string | null
  ativo: boolean
}

// --- Movimento de Caixa e Banco (ledger append-only) ---

export type TipoMovimentoCaixa = 'ENTRADA' | 'SAIDA' | 'TRANSFERENCIA_ENTRADA' | 'TRANSFERENCIA_SAIDA'
export const TIPO_MOVIMENTO_CAIXA_LABEL: Record<TipoMovimentoCaixa, string> = {
  ENTRADA: 'Entrada',
  SAIDA: 'Saída',
  TRANSFERENCIA_ENTRADA: 'Transferência (entrada)',
  TRANSFERENCIA_SAIDA: 'Transferência (saída)',
}

export type OrigemMovimentoCaixa =
  | 'BAIXA_CONTA_RECEBER'
  | 'BAIXA_CONTA_PAGAR'
  | 'ESTORNO_BAIXA_RECEBER'
  | 'ESTORNO_BAIXA_PAGAR'
  | 'TRANSFERENCIA'
  | 'APLICACAO_FINANCEIRA'
  | 'RESGATE_APLICACAO'
  | 'CHEQUE_COMPENSADO'
  | 'CHEQUE_DEVOLVIDO'
  | 'AJUSTE'
  | 'CONCILIACAO'
  | 'SANGRIA_PDV'
  | 'SUPRIMENTO_PDV'
  | 'ABERTURA_CAIXA_PDV'
  | 'CONFERENCIA_CAIXA_PDV'
  | 'VENDA_PDV'
export const ORIGEM_MOVIMENTO_CAIXA_LABEL: Record<OrigemMovimentoCaixa, string> = {
  BAIXA_CONTA_RECEBER: 'Baixa de conta a receber',
  BAIXA_CONTA_PAGAR: 'Baixa de conta a pagar',
  ESTORNO_BAIXA_RECEBER: 'Estorno de baixa a receber',
  ESTORNO_BAIXA_PAGAR: 'Estorno de baixa a pagar',
  TRANSFERENCIA: 'Transferência entre contas',
  APLICACAO_FINANCEIRA: 'Aplicação financeira',
  RESGATE_APLICACAO: 'Resgate de aplicação',
  CHEQUE_COMPENSADO: 'Cheque compensado',
  CHEQUE_DEVOLVIDO: 'Cheque devolvido',
  AJUSTE: 'Ajuste manual',
  CONCILIACAO: 'Conciliação bancária',
  SANGRIA_PDV: 'Sangria (PDV)',
  SUPRIMENTO_PDV: 'Suprimento (PDV)',
  ABERTURA_CAIXA_PDV: 'Abertura de caixa (PDV)',
  CONFERENCIA_CAIXA_PDV: 'Conferência de caixa (PDV)',
  VENDA_PDV: 'Venda em dinheiro (PDV)',
}

/** Lançamento manual de ajuste (tarifa, IOF, correção) — só `ENTRADA`/`SAIDA` (transferência
 * tem endpoint próprio em Tesouraria), sempre `origemTipo=AJUSTE`. */
export interface MovimentoCaixaBancoRequest {
  contaBancariaId: number
  tipoMovimento: 'ENTRADA' | 'SAIDA'
  valor: number
  dataMovimento?: string
  historico: string
  usuarioId?: number
}

export interface MovimentoCaixaBancoResponse {
  id: number
  contaBancariaId: number
  tipoMovimento: TipoMovimentoCaixa
  origemTipo: OrigemMovimentoCaixa
  origemId?: number
  dataMovimento: string
  historico: string
  valor: number
  saldoAnterior: number
  saldoPosterior: number
  usuarioId?: number
}

// --- Cheque Emitido ---

export type SituacaoChequeEmitido = 'EMITIDO' | 'COMPENSADO' | 'DEVOLVIDO' | 'CANCELADO'
export const SITUACAO_CHEQUE_EMITIDO_LABEL: Record<SituacaoChequeEmitido, string> = {
  EMITIDO: 'Emitido',
  COMPENSADO: 'Compensado',
  DEVOLVIDO: 'Devolvido',
  CANCELADO: 'Cancelado',
}

export interface ChequeEmitidoRequest {
  contaBancariaId: number
  numero: string
  dataEmissao: string
  bomPara?: string
  valor: number
  nominalA?: string
}

export interface ChequeEmitidoResponse extends ChequeEmitidoRequest {
  id: number
  situacao: SituacaoChequeEmitido
  dataSituacao?: string
  motivoSituacao?: string
}

export interface CompensarChequeEmitidoRequest {
  dataCompensacao?: string
}

export interface CancelarChequeEmitidoRequest {
  motivo: string
}

/** Compartilhado entre cheque emitido e recebido. */
export interface DevolverChequeRequest {
  dataDevolucao?: string
  motivo: string
}

// --- Cheque Recebido ---

export type SituacaoChequeRecebido = 'EM_CARTEIRA' | 'DEPOSITADO' | 'COMPENSADO' | 'DEVOLVIDO' | 'DESCONTADO'
export const SITUACAO_CHEQUE_RECEBIDO_LABEL: Record<SituacaoChequeRecebido, string> = {
  EM_CARTEIRA: 'Em carteira',
  DEPOSITADO: 'Depositado',
  COMPENSADO: 'Compensado',
  DEVOLVIDO: 'Devolvido',
  DESCONTADO: 'Descontado',
}

export interface ChequeRecebidoRequest {
  clienteId?: number
  cpfCnpj?: string
  nomeEmitente: string
  bancoCodigo?: string
  agencia?: string
  conta?: string
  numero?: string
  dataEmissao: string
  bomPara?: string
  valor: number
}

export interface ChequeRecebidoResponse extends ChequeRecebidoRequest {
  id: number
  situacao: SituacaoChequeRecebido
  dataSituacao?: string
  motivoSituacao?: string
  contaBancariaId?: number
}

export interface DepositarChequeRecebidoRequest {
  contaBancariaId: number
  dataDeposito?: string
}

export interface CompensarChequeRecebidoRequest {
  dataCompensacao?: string
}

export interface DescontarChequeRecebidoRequest {
  contaBancariaId: number
  dataDesconto?: string
}

// --- Tesouraria: Transferência Bancária ---

export interface TransferenciaBancariaRequest {
  contaOrigemId: number
  contaDestinoId: number
  dataTransferencia?: string
  valor: number
  historico?: string
  usuarioId?: number
}

export interface TransferenciaBancariaResponse extends TransferenciaBancariaRequest {
  id: number
}

// --- Tesouraria: Aplicação Financeira ---

export type TipoAplicacaoFinanceira = 'CDB' | 'POUPANCA' | 'FUNDO' | 'TESOURO_DIRETO' | 'OUTRO'
export const TIPO_APLICACAO_FINANCEIRA_LABEL: Record<TipoAplicacaoFinanceira, string> = {
  CDB: 'CDB',
  POUPANCA: 'Poupança',
  FUNDO: 'Fundo de Investimento',
  TESOURO_DIRETO: 'Tesouro Direto',
  OUTRO: 'Outro',
}

export type SituacaoAplicacaoFinanceira = 'ATIVA' | 'RESGATADA'
export const SITUACAO_APLICACAO_FINANCEIRA_LABEL: Record<SituacaoAplicacaoFinanceira, string> = {
  ATIVA: 'Ativa',
  RESGATADA: 'Resgatada',
}

export interface AplicacaoFinanceiraRequest {
  contaBancariaId: number
  tipoAplicacao: TipoAplicacaoFinanceira
  descricao?: string
  dataAplicacao?: string
  valorAplicado: number
}

export interface AplicacaoFinanceiraResponse extends AplicacaoFinanceiraRequest {
  id: number
  dataResgate?: string
  valorResgatado?: number
  rendimento?: number
  situacao: SituacaoAplicacaoFinanceira
}

/** `valorResgatado` pode ser maior ou menor que `valorAplicado` — rendimento positivo ou
 * negativo (fundo/Tesouro Direto marcado a mercado pode perder valor). */
export interface ResgatarAplicacaoRequest {
  dataResgate?: string
  valorResgatado: number
}

// --- Tesouraria: Posição de Caixa ---

export interface PosicaoCaixaTipoResponse {
  tipo: TipoContaBancaria
  quantidadeContas: number
  saldoTotal: number
}

export interface PosicaoCaixaResponse {
  saldoGeral: number
  porTipo: PosicaoCaixaTipoResponse[]
}

// --- Relatório: Fluxo de Caixa projetado ---

export type AgrupamentoFluxoCaixa = 'DIA' | 'MES'

export interface FluxoCaixaPeriodoResponse {
  periodo: string
  totalEntradas: number
  totalSaidas: number
  saldoPeriodo: number
  saldoAcumulado: number
}

// --- Relatório: Títulos em aberto (Contas a Receber/Pagar) ---

export type TipoTituloRelatorio = 'RECEBER' | 'PAGAR'

/** `pessoaId` é o cliente (RECEBER) ou o fornecedor (PAGAR); vencimento omitido = carteira inteira. */
export interface TitulosEmAbertoFiltro {
  tipo: TipoTituloRelatorio
  empresaId: number
  dataInicio?: string
  dataFim?: string
  pessoaId?: number
}

export interface TituloEmAbertoResponse {
  tituloId: number
  pessoaId: number
  numeroDocumento?: string
  descricao?: string
  dataEmissao: string
  dataVencimento: string
  situacao: SituacaoTitulo
  diasAtraso?: number
  valorOriginal: number
  valorPago: number
  valorSaldo: number
  valorMulta: number
  valorJuros: number
  valorAtualizado: number
}

// --- Conciliação Bancária ---

export interface ExtratoBancarioImportacaoResponse {
  id: number
  contaBancariaId: number
  nomeArquivo?: string
  quantidadeItens: number
  quantidadeDuplicados: number
  usuarioId?: number
}

export interface ExtratoBancarioItemResponse {
  id: number
  importacaoId: number
  contaBancariaId: number
  dataMovimento: string
  historico?: string
  tipoOfx?: string
  valor: number
  fitId?: string
  conciliado: boolean
  movimentoCaixaBancoId?: number
}

export interface PendenciasConciliacaoResponse {
  itensExtratoPendentes: ExtratoBancarioItemResponse[]
  movimentosPendentes: MovimentoCaixaBancoResponse[]
}

export interface ConciliarAutomaticoResponse {
  quantidadeConciliada: number
  quantidadePendente: number
}

export interface ConciliarManualRequest {
  movimentoCaixaBancoId: number
}

/** `historico` nulo = usa o histórico da própria linha do extrato. */
export interface GerarAjusteConciliacaoRequest {
  historico?: string
}

// --- EDI Bancário: Remessa/Retorno CNAB 240 ---

export interface ItemRemessaRequest {
  contaReceberId: number
  sacadoNumeroInscricao: string
  sacadoNome: string
  sacadoLogradouro?: string
  sacadoBairro?: string
  sacadoCep?: string
  sacadoCidade?: string
  sacadoUf?: string
}

export interface GerarRemessaRequest {
  contaBancariaId: number
  empresaId: number
  itens: ItemRemessaRequest[]
}

export interface RemessaBancariaItemResponse {
  id: number
  contaReceberId: number
  nossoNumero: string
  numeroDocumento?: string
  dataVencimento: string
  valorTitulo: number
  sacadoNome: string
}

/** `conteudoArquivo` (o texto CNAB 240 em si) não vem aqui — baixa via
 * `GET /remessas/{id}/arquivo` dedicado, pra não pesar a listagem/detalhe. */
export interface RemessaBancariaResponse {
  id: number
  contaBancariaId: number
  empresaId: number
  dataGeracao: string
  numeroSequencialArquivo: number
  quantidadeTitulos: number
  valorTotal: number
  itens: RemessaBancariaItemResponse[]
}

export interface RetornoBancarioItemResponse {
  id: number
  nossoNumero?: string
  numeroDocumento?: string
  codigoMovimento?: string
  dataOcorrencia?: string
  valorTitulo?: number
  valorPago?: number
  contaReceberId?: number
  processado: boolean
  observacao?: string
}

export interface RetornoBancarioResponse {
  id: number
  contaBancariaId: number
  nomeArquivo?: string
  quantidadeRegistros: number
  itens: RetornoBancarioItemResponse[]
}
