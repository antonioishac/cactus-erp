import { useQuery } from '@tanstack/react-query'
import { vendasApi } from '@shared/api/httpClients'
import type { PageResponse } from '@shared/api/pageResponse'

/**
 * Venda de balcão do PDV (vendas-service, `/api/v1/vendas-pdv`) — entidade própria, separada da venda
 * B2B (`VendaCabecalho`). Só leitura: nasce no cactus-pdv e chega pela sincronização. Não baixa estoque
 * (o PDV já manda o `ESTOQUE_MOVIMENTO`); só o dinheiro líquido vai pro caixa do terminal.
 */

export type SituacaoVendaPdv = 'FECHADA' | 'CANCELADA'
export type OrigemVendaPdv = 'BALCAO' | 'MESA' | 'COMANDA' | 'DELIVERY' | 'EXTERNA'

export const ORIGEM_VENDA_PDV_LABEL: Record<OrigemVendaPdv, string> = {
  BALCAO: 'Balcão',
  MESA: 'Mesa',
  COMANDA: 'Comanda',
  DELIVERY: 'Delivery',
  EXTERNA: 'Externa',
}

/** `tPag` oficial da SEFAZ — é por ele que o pagamento é identificado, não pelo id da forma. */
export const TPAG_LABEL: Record<string, string> = {
  '01': 'Dinheiro',
  '02': 'Cheque',
  '03': 'Cartão de crédito',
  '04': 'Cartão de débito',
  '05': 'Crédito loja (fiado)',
  '10': 'Vale alimentação',
  '11': 'Vale refeição',
  '12': 'Vale presente',
  '13': 'Vale combustível',
  '15': 'Boleto',
  '16': 'Depósito',
  '17': 'PIX',
  '18': 'Transferência',
  '19': 'Fidelidade/cashback',
  '90': 'Sem pagamento',
  '99': 'Outros',
}

export interface VendaPdvItem {
  id: number
  numeroItem: number
  produtoId: number
  gtin?: string | null
  quantidade: number
  valorUnitario: number
  valorDesconto: number
  valorAcrescimo: number
  valorTotal: number
  cfop?: number | null
  cst?: string | null
  cancelado: boolean
  quantidadeDevolvida: number
}

export interface VendaPdvPagamento {
  id: number
  formaPagamentoId?: number | null
  codigoPagamentoNfce: string
  descricao?: string | null
  valor: number
  nsu?: string | null
  rede?: string | null
  cartaoDebitoCredito?: string | null
  estornado: boolean
}

export interface VendaPdvDevolucao {
  id: number
  vendaPdvItemId: number
  numeroItem: number
  quantidadeDevolvida: number
  valorDevolvido: number
  motivo?: string | null
  pdvOperadorId?: number | null
  pdvGerenteAutorizadorId: number
  dataHoraDevolucao: string
}

export interface VendaPdv {
  id: number
  empresaId: number
  idOrigem: string
  pdvCaixaId: number
  pdvCaixaNome?: string | null
  pdvOperadorId?: number | null
  clienteId?: number | null
  nomeCliente?: string | null
  cpfCnpjCliente?: string | null
  dataHoraVenda: string
  origemVenda: OrigemVendaPdv
  situacao: SituacaoVendaPdv
  tipoOperacaoFiscal?: string | null
  chaveAcessoFiscal?: string | null
  valorTotalProdutos: number
  valorDesconto: number
  valorAcrescimo: number
  valorFinal: number
  valorRecebido: number
  valorTroco: number
  valorDinheiroLiquido: number
  itens: VendaPdvItem[]
  pagamentos: VendaPdvPagamento[]
  devolucoes: VendaPdvDevolucao[]
}

export interface VendasPdvFiltro {
  empresaId: number
  dataInicio: string
  dataFim: string
  pdvCaixaId?: number
}

export async function listarVendasPdv(filtro: VendasPdvFiltro, pagina: number, tamanho = 10): Promise<PageResponse<VendaPdv>> {
  const { data } = await vendasApi.get<PageResponse<VendaPdv>>('/api/v1/vendas-pdv', {
    params: { ...filtro, page: pagina, size: tamanho },
  })
  return data
}

export function useVendasPdv(filtro: VendasPdvFiltro | null, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: ['vendas-pdv', filtro, pagina, tamanho],
    queryFn: () => listarVendasPdv(filtro as VendasPdvFiltro, pagina, tamanho),
    enabled: filtro != null,
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}
