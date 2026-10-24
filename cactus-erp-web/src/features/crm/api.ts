import { cadastroBaseApi, crmApi, segurancaApi, vendasApi } from '@shared/api/httpClients'
import { baixarArquivo, type FormatoRelatorio } from '@shared/api/download'
import type { PageResponse } from '@shared/api/pageResponse'
import type {
  AtividadeRequest,
  AtividadeResponse,
  ClienteResumo,
  ConverterLeadRequest,
  DesqualificarLeadRequest,
  EstagioFunilRequest,
  EstagioFunilResponse,
  FunilEstagioResponse,
  InteracaoRequest,
  InteracaoResponse,
  LeadRequest,
  LeadResponse,
  MoverEstagioRequest,
  OportunidadeRequest,
  OportunidadeResponse,
  SituacaoOportunidade,
  UsuarioResumo,
  VendedorResumo,
} from './types'

// --- Estágios do Funil ---

export async function listarEstagiosFunil(pagina: number, tamanho = 10): Promise<PageResponse<EstagioFunilResponse>> {
  const { data } = await crmApi.get<PageResponse<EstagioFunilResponse>>('/api/v1/estagios-funil', {
    params: { page: pagina, size: tamanho },
  })
  return data
}

export async function listarEstagiosFunilOrdenado(): Promise<EstagioFunilResponse[]> {
  const { data } = await crmApi.get<EstagioFunilResponse[]>('/api/v1/estagios-funil/ordenado')
  return data
}

export async function criarEstagioFunil(request: EstagioFunilRequest): Promise<EstagioFunilResponse> {
  const { data } = await crmApi.post<EstagioFunilResponse>('/api/v1/estagios-funil', request)
  return data
}

export async function atualizarEstagioFunil(id: number, request: EstagioFunilRequest): Promise<EstagioFunilResponse> {
  const { data } = await crmApi.put<EstagioFunilResponse>(`/api/v1/estagios-funil/${id}`, request)
  return data
}

export async function excluirEstagioFunil(id: number): Promise<void> {
  await crmApi.delete(`/api/v1/estagios-funil/${id}`)
}

// --- Leads ---

export async function listarLeads(pagina: number, tamanho = 10): Promise<PageResponse<LeadResponse>> {
  const { data } = await crmApi.get<PageResponse<LeadResponse>>('/api/v1/leads', {
    params: { page: pagina, size: tamanho },
  })
  return data
}

export async function buscarLead(id: number): Promise<LeadResponse> {
  const { data } = await crmApi.get<LeadResponse>(`/api/v1/leads/${id}`)
  return data
}

export async function criarLead(request: LeadRequest): Promise<LeadResponse> {
  const { data } = await crmApi.post<LeadResponse>('/api/v1/leads', request)
  return data
}

export async function atualizarLead(id: number, request: LeadRequest): Promise<LeadResponse> {
  const { data } = await crmApi.put<LeadResponse>(`/api/v1/leads/${id}`, request)
  return data
}

export async function excluirLead(id: number): Promise<void> {
  await crmApi.delete(`/api/v1/leads/${id}`)
}

export async function qualificarLead(id: number): Promise<LeadResponse> {
  const { data } = await crmApi.post<LeadResponse>(`/api/v1/leads/${id}/qualificar`)
  return data
}

export async function desqualificarLead(id: number, request: DesqualificarLeadRequest): Promise<LeadResponse> {
  const { data } = await crmApi.post<LeadResponse>(`/api/v1/leads/${id}/desqualificar`, request)
  return data
}

export async function converterLead(id: number, request: ConverterLeadRequest): Promise<LeadResponse> {
  const { data } = await crmApi.post<LeadResponse>(`/api/v1/leads/${id}/converter`, request)
  return data
}

// --- Oportunidades ---

export async function listarOportunidades(pagina: number, tamanho = 10): Promise<PageResponse<OportunidadeResponse>> {
  const { data } = await crmApi.get<PageResponse<OportunidadeResponse>>('/api/v1/oportunidades', {
    params: { page: pagina, size: tamanho },
  })
  return data
}

export async function buscarVisaoFunil(): Promise<FunilEstagioResponse[]> {
  const { data } = await crmApi.get<FunilEstagioResponse[]>('/api/v1/oportunidades/funil')
  return data
}

export async function buscarOportunidade(id: number): Promise<OportunidadeResponse> {
  const { data } = await crmApi.get<OportunidadeResponse>(`/api/v1/oportunidades/${id}`)
  return data
}

export async function criarOportunidade(request: OportunidadeRequest): Promise<OportunidadeResponse> {
  const { data } = await crmApi.post<OportunidadeResponse>('/api/v1/oportunidades', request)
  return data
}

export async function atualizarOportunidade(id: number, request: OportunidadeRequest): Promise<OportunidadeResponse> {
  const { data } = await crmApi.put<OportunidadeResponse>(`/api/v1/oportunidades/${id}`, request)
  return data
}

export async function excluirOportunidade(id: number): Promise<void> {
  await crmApi.delete(`/api/v1/oportunidades/${id}`)
}

export async function moverEstagioOportunidade(id: number, request: MoverEstagioRequest): Promise<OportunidadeResponse> {
  const { data } = await crmApi.post<OportunidadeResponse>(`/api/v1/oportunidades/${id}/mover-estagio`, request)
  return data
}

// --- Atividades ---

export async function listarAtividades(
  leadId: number | undefined,
  oportunidadeId: number | undefined,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<AtividadeResponse>> {
  const { data } = await crmApi.get<PageResponse<AtividadeResponse>>('/api/v1/atividades', {
    params: { leadId, oportunidadeId, page: pagina, size: tamanho },
  })
  return data
}

export async function criarAtividade(request: AtividadeRequest): Promise<AtividadeResponse> {
  const { data } = await crmApi.post<AtividadeResponse>('/api/v1/atividades', request)
  return data
}

export async function concluirAtividade(id: number): Promise<AtividadeResponse> {
  const { data } = await crmApi.post<AtividadeResponse>(`/api/v1/atividades/${id}/concluir`)
  return data
}

export async function excluirAtividade(id: number): Promise<void> {
  await crmApi.delete(`/api/v1/atividades/${id}`)
}

// --- Interações ---

export async function listarInteracoes(
  leadId: number | undefined,
  oportunidadeId: number | undefined,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<InteracaoResponse>> {
  const { data } = await crmApi.get<PageResponse<InteracaoResponse>>('/api/v1/interacoes', {
    params: { leadId, oportunidadeId, page: pagina, size: tamanho },
  })
  return data
}

export async function criarInteracao(request: InteracaoRequest): Promise<InteracaoResponse> {
  const { data } = await crmApi.post<InteracaoResponse>('/api/v1/interacoes', request)
  return data
}

// --- Lookups cross-serviço ---

export async function buscarClientesParaSelecao(nome: string | undefined): Promise<ClienteResumo[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ClienteResumo>>('/api/v1/clientes', {
    params: { nome, size: 20 },
  })
  return data.conteudo
}

export async function buscarClienteResumo(id: number): Promise<ClienteResumo> {
  const { data } = await cadastroBaseApi.get<ClienteResumo>(`/api/v1/clientes/${id}`)
  return data
}

interface VendedorResponseParcial {
  id: number
  colaboradorId: number
}

interface ColaboradorResumo {
  id: number
  nome: string
}

/** 2 hops: vendas-service (`Vendedor.colaboradorId`) → cadastro-base-service (nome do
 * colaborador) — `VendedorResponse` não carrega o nome direto (ver Javadoc de `VendedorResumo`). */
export async function buscarVendedorResumo(id: number): Promise<VendedorResumo> {
  const { data: vendedor } = await vendasApi.get<VendedorResponseParcial>(`/api/v1/vendedores/${id}`)
  const { data: colaborador } = await cadastroBaseApi.get<ColaboradorResumo>(`/api/v1/colaboradores/${vendedor.colaboradorId}`)
  return { id: vendedor.id, nome: colaborador.nome }
}

/** Sem filtro por nome no `VendedorController` (lista pequena por empresa) — carrega tudo
 * (até 100) e resolve o nome de cada um, pra popular um `Select` comum (não `AsyncSelect`). */
export async function listarVendedoresComNome(): Promise<VendedorResumo[]> {
  const { data } = await vendasApi.get<PageResponse<VendedorResponseParcial>>('/api/v1/vendedores', { params: { size: 100 } })
  return Promise.all(
    data.conteudo.map(async (vendedor) => {
      const { data: colaborador } = await cadastroBaseApi.get<ColaboradorResumo>(`/api/v1/colaboradores/${vendedor.colaboradorId}`)
      return { id: vendedor.id, nome: colaborador.nome }
    }),
  )
}

export async function buscarUsuarioResumo(id: number): Promise<UsuarioResumo> {
  const { data } = await segurancaApi.get<UsuarioResumo>(`/api/v1/usuarios/${id}`)
  return data
}

export async function buscarUsuariosParaSelecao(busca: string | undefined): Promise<UsuarioResumo[]> {
  const { data } = await segurancaApi.get<PageResponse<UsuarioResumo>>('/api/v1/usuarios', {
    params: { busca, size: 20 },
  })
  return data.conteudo
}

// --- Relatório: Funil de Oportunidades ---

export async function exportarFunilOportunidades(
  situacao: SituacaoOportunidade | undefined,
  formato: FormatoRelatorio,
): Promise<void> {
  await baixarArquivo(crmApi, `/api/v1/relatorios/funil-oportunidades/${formato}`, `funil-oportunidades.${formato}`, { situacao })
}
