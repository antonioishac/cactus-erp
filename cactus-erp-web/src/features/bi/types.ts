/**
 * Schema de leitura do BI — tudo aqui é snapshot gerado pelo ETL, não
 * calculado em tempo real (ver Javadoc de `IndicadoresController.java`).
 * Diferente de todo outro módulo do sistema, os nomes já vêm denormalizados
 * (`produtoNome`/`depositoNome`/`fornecedorNome`) — decisão deliberada de
 * arquitetura do BI (ver ANÁLISE no CLAUDE.md raiz, "dimensões denormalizadas
 * são deliberadas aqui"), então não existe lookup cross-serviço nesta
 * feature como em Estoque/Compras. `null` num desses campos = id "órfão"
 * (produto/fornecedor usado em Estoque/Compras mas nunca cadastrado de fato
 * no cadastro-base) — comportamento esperado, não bug, exibir como `#id`.
 */

export interface EstoqueValorIndicadorResponse {
  dataReferencia: string
  depositoId: number
  depositoNome: string
  produtoId: number
  produtoNome: string | null
  quantidade: number
  custoMedioPonderado: number
  valorTotal: number
}

/** `classificacao`: A (≤80% acumulado) | B (≤95%) | C (resto) — ver `EtlService.java`. */
export type ClassificacaoAbc = 'A' | 'B' | 'C'

export interface CurvaAbcIndicadorResponse {
  dataReferencia: string
  depositoId: number
  depositoNome: string
  produtoId: number
  produtoNome: string | null
  valorMovimentado: number
  percentualParticipacao: number
  percentualAcumulado: number
  classificacao: ClassificacaoAbc
}

export interface ComprasFornecedorIndicadorResponse {
  anoMes: string
  fornecedorId: number
  fornecedorNome: string | null
  quantidadePedidos: number
  valorTotalComprado: number
}

export interface EstoqueValorFiltro {
  dataReferencia?: string // ISO yyyy-MM-dd, default hoje (aplicado pelo backend)
  depositoId?: number
}

export interface CurvaAbcFiltro {
  dataReferencia?: string
  depositoId?: number
}

/** `anoMes` no formato YYYY-MM — default mês atual (aplicado pelo backend). */
export interface ComprasFornecedorFiltro {
  anoMes?: string
}

/** `B2B` = venda do vendas-service; `PDV` = venda de balcão sincronizada do cactus-pdv. */
export type CanalVenda = 'B2B' | 'PDV'

/** Uma linha por dia × empresa × canal. Valores do PDV já são líquidos de devolução. */
export interface FaturamentoDiarioIndicadorResponse {
  dataReferencia: string
  empresaId: number
  canal: CanalVenda
  quantidadeVendas: number
  valorBruto: number
  valorDevolvido: number
  valorLiquido: number
  ticketMedio: number
}

/** `ticketMedio` do total = líquido do período / vendas do período (não a média das médias diárias). */
export interface FaturamentoIndicadorResponse {
  quantidadeVendas: number
  valorBruto: number
  valorDevolvido: number
  valorLiquido: number
  ticketMedio: number
  dias: FaturamentoDiarioIndicadorResponse[]
}

export interface FaturamentoFiltro {
  dataInicio: string
  dataFim: string
  empresaId?: number
  canal?: CanalVenda
}

/** `dataReferencia` nula assume hoje — ver Javadoc de `EtlExecucaoRequest.java`. */
export interface EtlExecucaoRequest {
  dataReferencia?: string
}

/** Lookup cross-serviço só pra alimentar o filtro de depósito (feature-local, mesmo padrão de Estoque/Compras). */
export interface DepositoResumo {
  id: number
  nome: string
}
