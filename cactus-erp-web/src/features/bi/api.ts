import { biApi, estoqueApi } from '@shared/api/httpClients'
import { baixarArquivo, type FormatoRelatorio } from '@shared/api/download'
import type { PageResponse } from '@shared/api/pageResponse'
import type {
  ComprasFornecedorFiltro,
  ComprasFornecedorIndicadorResponse,
  CurvaAbcFiltro,
  CurvaAbcIndicadorResponse,
  DepositoResumo,
  EstoqueValorFiltro,
  EstoqueValorIndicadorResponse,
  EtlExecucaoRequest,
} from './types'

// --- Indicadores (só leitura, snapshots do ETL) ---

export async function listarEstoqueValor(
  filtro: EstoqueValorFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<EstoqueValorIndicadorResponse>> {
  const { data } = await biApi.get<PageResponse<EstoqueValorIndicadorResponse>>('/api/v1/indicadores/estoque-valor', {
    params: { dataReferencia: filtro.dataReferencia, depositoId: filtro.depositoId, page: pagina, size: tamanho },
  })
  return data
}

export async function listarCurvaAbc(
  filtro: CurvaAbcFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<CurvaAbcIndicadorResponse>> {
  const { data } = await biApi.get<PageResponse<CurvaAbcIndicadorResponse>>('/api/v1/indicadores/curva-abc', {
    params: { dataReferencia: filtro.dataReferencia, depositoId: filtro.depositoId, page: pagina, size: tamanho },
  })
  return data
}

export async function listarComprasFornecedor(
  filtro: ComprasFornecedorFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<ComprasFornecedorIndicadorResponse>> {
  const { data } = await biApi.get<PageResponse<ComprasFornecedorIndicadorResponse>>('/api/v1/indicadores/compras-fornecedor', {
    params: { anoMes: filtro.anoMes, page: pagina, size: tamanho },
  })
  return data
}

export async function exportarEstoqueValor(filtro: EstoqueValorFiltro, formato: FormatoRelatorio): Promise<void> {
  await baixarArquivo(biApi, `/api/v1/indicadores/estoque-valor/${formato}`, `estoque-valor.${formato}`, { ...filtro })
}

export async function exportarCurvaAbc(filtro: CurvaAbcFiltro, formato: FormatoRelatorio): Promise<void> {
  await baixarArquivo(biApi, `/api/v1/indicadores/curva-abc/${formato}`, `curva-abc-historico.${formato}`, { ...filtro })
}

export async function exportarComprasFornecedor(filtro: ComprasFornecedorFiltro, formato: FormatoRelatorio): Promise<void> {
  await baixarArquivo(biApi, `/api/v1/indicadores/compras-fornecedor/${formato}`, `compras-fornecedor.${formato}`, { ...filtro })
}

// --- ETL ---

export async function executarEtl(request: EtlExecucaoRequest): Promise<void> {
  await biApi.post('/api/v1/etl/executar', request)
}

// --- Lookup cross-serviço (controle-estoque-service), só pra alimentar o filtro de depósito ---

export async function listarDepositosAtivos(empresaId: number): Promise<DepositoResumo[]> {
  const { data } = await estoqueApi.get<DepositoResumo[]>(`/api/v1/depositos/empresa/${empresaId}/ativos`)
  return data
}
