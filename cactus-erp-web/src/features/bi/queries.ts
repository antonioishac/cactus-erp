import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { executarEtl, listarComprasFornecedor, listarCurvaAbc, listarDepositosAtivos, listarEstoqueValor } from './api'
import type { ComprasFornecedorFiltro, CurvaAbcFiltro, EstoqueValorFiltro, EtlExecucaoRequest } from './types'

export function useEstoqueValor(filtro: EstoqueValorFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: ['bi-estoque-valor', filtro, pagina, tamanho],
    queryFn: () => listarEstoqueValor(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useCurvaAbc(filtro: CurvaAbcFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: ['bi-curva-abc', filtro, pagina, tamanho],
    queryFn: () => listarCurvaAbc(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useComprasFornecedor(filtro: ComprasFornecedorFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: ['bi-compras-fornecedor', filtro, pagina, tamanho],
    queryFn: () => listarComprasFornecedor(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

/** Invalida os 3 indicadores — o ETL recalcula tudo junto (dimensões antes de fatos, ver `EtlService`). */
export function useExecutarEtl() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: EtlExecucaoRequest) => executarEtl(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['bi-estoque-valor'] })
      queryClient.invalidateQueries({ queryKey: ['bi-curva-abc'] })
      queryClient.invalidateQueries({ queryKey: ['bi-compras-fornecedor'] })
    },
  })
}

export function useDepositosAtivos(empresaId: number | undefined) {
  return useQuery({
    queryKey: ['bi-depositos-ativos', empresaId ?? -1],
    queryFn: () => listarDepositosAtivos(empresaId as number),
    enabled: empresaId !== undefined,
  })
}
