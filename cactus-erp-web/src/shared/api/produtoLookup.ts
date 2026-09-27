import { cadastroBaseApi } from './httpClients'

/**
 * Resumo mínimo de produto pra exibição em telas de outro serviço (Estoque,
 * futuramente Compras/BI) — essas telas só guardam `produtoId` solto
 * (referência cross-serviço sem FK, ver CLAUDE.md raiz), então o nome/código
 * precisa ser resolvido à parte, batendo direto no `cadastro-base-service`.
 * Deliberadamente não reaproveita `features/produtos/types.ts` (limite de
 * feature) — é só o suficiente pra rotular uma linha de tabela.
 */
export interface ProdutoResumo {
  id: number
  codigoInterno: string
  nome: string
}

export async function buscarProdutoResumo(id: number): Promise<ProdutoResumo> {
  const { data } = await cadastroBaseApi.get<ProdutoResumo>(`/api/v1/produtos/${id}`)
  return data
}
