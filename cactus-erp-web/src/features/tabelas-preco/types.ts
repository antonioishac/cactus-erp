import type { Situacao } from '@shared/domain/situacao'

/** Ver `entity/TipoAjustePreco.java`. */
export type TipoAjustePreco = 'VALOR_FIXO' | 'PERCENTUAL_SOBRE_VENDA'

export const TIPO_AJUSTE_PRECO_LABEL: Record<TipoAjustePreco, string> = {
  VALOR_FIXO: 'Valor fixo (ignora o valor de venda do produto)',
  PERCENTUAL_SOBRE_VENDA: 'Percentual sobre o valor de venda (- desconto / + acréscimo)',
}

/** `produtoNome`/`produtoCodigoInterno` só vêm preenchidos na resposta — ver `TabelaPrecoProdutoDTO.java`. */
export interface TabelaPrecoProdutoDTO {
  produtoId: number
  produtoNome?: string
  produtoCodigoInterno?: string
  valor?: number
  percentualAjuste?: number
}

/**
 * Precificação por canal/perfil (Varejo/Atacado/Cliente VIP) — mecanismo
 * COMPLEMENTAR à faixa de preço por quantidade do próprio Produto (ver seção
 * "PRODUTO_FAIXA_PRECO é UM conceito só" no CLAUDE.md raiz): aqui a tabela é
 * dona da lista de produtos precificados (`produtos` substitui a lista
 * inteira a cada PUT), não o contrário — por isso é tela própria, não aba de
 * Produto.
 */
export interface TabelaPrecoRequest {
  nome: string
  descricao?: string
  tipoAjuste: TipoAjustePreco
  situacao: Situacao
  produtos: TabelaPrecoProdutoDTO[]
}

export interface TabelaPrecoResponse extends TabelaPrecoRequest {
  id: number
  dataAtualizacao: string
}

export interface TabelasPrecoFiltro {
  nome?: string
}

/** Resultado mínimo de busca de produto por nome — feature local, deliberadamente não reaproveita `ProdutoResponse` de `features/produtos` (limite de feature). */
export interface ProdutoParaSelecaoResponse {
  id: number
  nome: string
  codigoInterno: string
}
