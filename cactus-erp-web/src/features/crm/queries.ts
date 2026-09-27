import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import type { AppError } from '@shared/api/problemDetail'
import {
  atualizarEstagioFunil,
  atualizarLead,
  atualizarOportunidade,
  buscarClienteResumo,
  buscarClientesParaSelecao,
  buscarLead,
  buscarOportunidade,
  buscarUsuarioResumo,
  buscarUsuariosParaSelecao,
  buscarVendedorResumo,
  buscarVisaoFunil,
  concluirAtividade,
  converterLead,
  criarAtividade,
  criarEstagioFunil,
  criarInteracao,
  criarLead,
  criarOportunidade,
  desqualificarLead,
  excluirAtividade,
  excluirEstagioFunil,
  excluirLead,
  excluirOportunidade,
  listarAtividades,
  listarEstagiosFunil,
  listarEstagiosFunilOrdenado,
  listarInteracoes,
  listarLeads,
  listarOportunidades,
  listarVendedoresComNome,
  moverEstagioOportunidade,
  qualificarLead,
} from './api'
import type {
  AtividadeRequest,
  ConverterLeadRequest,
  DesqualificarLeadRequest,
  EstagioFunilRequest,
  LeadRequest,
  MoverEstagioRequest,
  InteracaoRequest,
  OportunidadeRequest,
} from './types'

const QUERY_KEY_ESTAGIOS = ['crm-estagios-funil'] as const
const QUERY_KEY_LEADS = ['crm-leads'] as const
const queryKeyLead = (id: number) => ['crm-leads', id] as const
const QUERY_KEY_OPORTUNIDADES = ['crm-oportunidades'] as const
const queryKeyOportunidade = (id: number) => ['crm-oportunidades', id] as const
const QUERY_KEY_FUNIL = ['crm-funil'] as const

// --- Estágios do Funil ---

export function useEstagiosFunilPaginado(pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_ESTAGIOS, pagina, tamanho],
    queryFn: () => listarEstagiosFunil(pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useEstagiosFunilOrdenado() {
  return useQuery({
    queryKey: [...QUERY_KEY_ESTAGIOS, 'ordenado'],
    queryFn: () => listarEstagiosFunilOrdenado(),
  })
}

export function useCriarEstagioFunil() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: EstagioFunilRequest) => criarEstagioFunil(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_ESTAGIOS }),
  })
}

export function useAtualizarEstagioFunil() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: EstagioFunilRequest }) => atualizarEstagioFunil(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_ESTAGIOS }),
  })
}

export function useExcluirEstagioFunil() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirEstagioFunil(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_ESTAGIOS }),
  })
}

// --- Leads ---

export function useLeads(pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_LEADS, pagina, tamanho],
    queryFn: () => listarLeads(pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useLead(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyLead(id ?? -1),
    queryFn: () => buscarLead(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarLead() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: LeadRequest) => criarLead(request),
    onSuccess: (criado) => {
      queryClient.setQueryData(queryKeyLead(criado.id), criado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LEADS })
    },
  })
}

export function useAtualizarLead(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: LeadRequest) => atualizarLead(id, request),
    onSuccess: (atualizado) => {
      queryClient.setQueryData(queryKeyLead(id), atualizado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LEADS })
    },
  })
}

export function useExcluirLead() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirLead(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_LEADS }),
  })
}

export function useQualificarLead(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => qualificarLead(id),
    onSuccess: (atualizado) => {
      queryClient.setQueryData(queryKeyLead(id), atualizado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LEADS })
    },
  })
}

export function useDesqualificarLead(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: DesqualificarLeadRequest) => desqualificarLead(id, request),
    onSuccess: (atualizado) => {
      queryClient.setQueryData(queryKeyLead(id), atualizado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LEADS })
    },
  })
}

export function useConverterLead(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ConverterLeadRequest) => converterLead(id, request),
    onSuccess: (atualizado) => {
      queryClient.setQueryData(queryKeyLead(id), atualizado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LEADS })
    },
  })
}

// --- Oportunidades ---

export function useOportunidades(pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_OPORTUNIDADES, pagina, tamanho],
    queryFn: () => listarOportunidades(pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useVisaoFunil() {
  return useQuery({
    queryKey: QUERY_KEY_FUNIL,
    queryFn: () => buscarVisaoFunil(),
  })
}

export function useOportunidade(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyOportunidade(id ?? -1),
    queryFn: () => buscarOportunidade(id as number),
    enabled: id !== undefined,
  })
}

function invalidarFunilEOportunidades(queryClient: ReturnType<typeof useQueryClient>) {
  queryClient.invalidateQueries({ queryKey: QUERY_KEY_OPORTUNIDADES })
  queryClient.invalidateQueries({ queryKey: QUERY_KEY_FUNIL })
}

export function useCriarOportunidade() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: OportunidadeRequest) => criarOportunidade(request),
    onSuccess: (criada) => {
      queryClient.setQueryData(queryKeyOportunidade(criada.id), criada)
      invalidarFunilEOportunidades(queryClient)
    },
  })
}

export function useAtualizarOportunidade(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: OportunidadeRequest) => atualizarOportunidade(id, request),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyOportunidade(id), atualizada)
      invalidarFunilEOportunidades(queryClient)
    },
  })
}

export function useExcluirOportunidade() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirOportunidade(id),
    onSuccess: () => invalidarFunilEOportunidades(queryClient),
  })
}

export function useMoverEstagioOportunidade(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: MoverEstagioRequest) => moverEstagioOportunidade(id, request),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyOportunidade(id), atualizada)
      invalidarFunilEOportunidades(queryClient)
    },
  })
}

/**
 * Mesma mutação de `useMoverEstagioOportunidade`, mas com o `id` viajando
 * como variável em vez de fixado na criação do hook — necessário pro
 * drag-and-drop do Kanban (`OportunidadesKanbanPage`), onde um único
 * `onDragEnd` precisa mover qualquer card solto, não um id conhecido de
 * antemão como no fluxo de modal (`MoverEstagioModal`).
 */
export function useMoverEstagioOportunidadeGenerico() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: MoverEstagioRequest }) => moverEstagioOportunidade(id, request),
    onSuccess: (atualizada) => {
      queryClient.setQueryData(queryKeyOportunidade(atualizada.id), atualizada)
      invalidarFunilEOportunidades(queryClient)
    },
  })
}

// --- Atividades ---

const queryKeyAtividades = (leadId: number | undefined, oportunidadeId: number | undefined) =>
  ['crm-atividades', leadId, oportunidadeId] as const

export function useAtividades(leadId: number | undefined, oportunidadeId: number | undefined, pagina = 0, tamanho = 20) {
  return useQuery({
    queryKey: [...queryKeyAtividades(leadId, oportunidadeId), pagina, tamanho],
    queryFn: () => listarAtividades(leadId, oportunidadeId, pagina, tamanho),
    enabled: leadId !== undefined || oportunidadeId !== undefined,
  })
}

export function useCriarAtividade(leadId: number | undefined, oportunidadeId: number | undefined) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: AtividadeRequest) => criarAtividade(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyAtividades(leadId, oportunidadeId) }),
  })
}

export function useConcluirAtividade(leadId: number | undefined, oportunidadeId: number | undefined) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => concluirAtividade(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyAtividades(leadId, oportunidadeId) }),
  })
}

export function useExcluirAtividade(leadId: number | undefined, oportunidadeId: number | undefined) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirAtividade(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyAtividades(leadId, oportunidadeId) }),
  })
}

// --- Interações ---

const queryKeyInteracoes = (leadId: number | undefined, oportunidadeId: number | undefined) =>
  ['crm-interacoes', leadId, oportunidadeId] as const

export function useInteracoes(leadId: number | undefined, oportunidadeId: number | undefined, pagina = 0, tamanho = 20) {
  return useQuery({
    queryKey: [...queryKeyInteracoes(leadId, oportunidadeId), pagina, tamanho],
    queryFn: () => listarInteracoes(leadId, oportunidadeId, pagina, tamanho),
    enabled: leadId !== undefined || oportunidadeId !== undefined,
  })
}

export function useCriarInteracao(leadId: number | undefined, oportunidadeId: number | undefined) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: InteracaoRequest) => criarInteracao(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeyInteracoes(leadId, oportunidadeId) }),
  })
}

// --- Lookups cross-serviço ---

export function useBuscaClientesParaSelecao(nome: string | undefined) {
  return useQuery({
    queryKey: ['clientes-para-selecao-crm', nome],
    queryFn: () => buscarClientesParaSelecao(nome),
    enabled: nome !== undefined && nome.length >= 2,
  })
}

export function useClienteResumo(id: number | undefined) {
  return useQuery({
    queryKey: ['cliente-resumo-crm', id],
    queryFn: () => buscarClienteResumo(id as number),
    enabled: id != null,
    staleTime: 5 * 60_000,
    retry: (failureCount, erro) => (erro as unknown as AppError).status !== 404 && failureCount < 1,
  })
}

/** `Vendedor`/`Usuario` são referências cross-serviço fora do domínio do CRM
 * (vendas-service/seguranca-service) — um usuário só-CRM pode não ter `VENDAS_CONSULTAR`/
 * `SEGURANCA_CONSULTAR`, daí o 403 também entra na lista de "não vale repetir" (a
 * permissão não muda numa nova tentativa, mesmo critério do 404). */
const NAO_REPETIR_404_OU_403 = (failureCount: number, erro: unknown) => {
  const status = (erro as unknown as AppError).status
  return status !== 404 && status !== 403 && failureCount < 1
}

export function useVendedorResumo(id: number | undefined) {
  return useQuery({
    queryKey: ['vendedor-resumo-crm', id],
    queryFn: () => buscarVendedorResumo(id as number),
    enabled: id != null,
    staleTime: 5 * 60_000,
    retry: NAO_REPETIR_404_OU_403,
  })
}

export function useVendedoresComNome() {
  return useQuery({
    queryKey: ['vendedores-com-nome-crm'],
    queryFn: listarVendedoresComNome,
    staleTime: 5 * 60_000,
    retry: NAO_REPETIR_404_OU_403,
  })
}

export function useUsuarioResumo(id: number | undefined) {
  return useQuery({
    queryKey: ['usuario-resumo-crm', id],
    queryFn: () => buscarUsuarioResumo(id as number),
    enabled: id != null,
    staleTime: 5 * 60_000,
    retry: NAO_REPETIR_404_OU_403,
  })
}

export function useBuscaUsuariosParaSelecao(busca: string | undefined) {
  return useQuery({
    queryKey: ['usuarios-para-selecao-crm', busca],
    queryFn: () => buscarUsuariosParaSelecao(busca),
    enabled: busca !== undefined && busca.length >= 2,
  })
}
