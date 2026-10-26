import { useQuery, useQueryClient, useMutation } from '@tanstack/react-query'
import {
  listarClassificacoesIbsCbs,
  atualizarConfiguracaoTributaria,
  atualizarGrupoTributario,
  atualizarOperacaoFiscal,
  buscarCfop,
  buscarConfiguracaoTributaria,
  criarConfiguracaoTributaria,
  criarGrupoTributario,
  criarOperacaoFiscal,
  excluirConfiguracaoTributaria,
  excluirGrupoTributario,
  excluirOperacaoFiscal,
  listarConfiguracoesTributarias,
  listarGruposTributarios,
  listarOperacoesFiscais,
  listarTodasOperacoesFiscais,
  listarTodosGruposTributarios,
} from './api'
import type { ConfiguracaoTributariaRequest, GrupoTributarioRequest, OperacaoFiscalRequest } from './types'

const QUERY_KEY_GRUPOS = ['grupos-tributarios']
const QUERY_KEY_OPERACOES = ['operacoes-fiscais']
const QUERY_KEY_CONFIGURACOES = ['configuracoes-tributarias']

// --- Grupo Tributário ---

export function useGruposTributariosPaginado(pagina: number, tamanho = 10, nome?: string) {
  return useQuery({
    queryKey: [...QUERY_KEY_GRUPOS, pagina, tamanho, nome],
    queryFn: () => listarGruposTributarios(pagina, tamanho, nome),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

/** Catálogo oficial de cClassTrib — muda só com migration nova, então fica em cache a sessão toda. */
export function useClassificacoesIbsCbs() {
  return useQuery({
    queryKey: ['tributacao-ibs-cbs-classificacoes'],
    queryFn: () => listarClassificacoesIbsCbs(),
    staleTime: Infinity,
  })
}

export function useTodosGruposTributarios() {
  return useQuery({
    queryKey: [...QUERY_KEY_GRUPOS, 'todos'],
    queryFn: () => listarTodosGruposTributarios(),
  })
}

export function useCriarGrupoTributario() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: GrupoTributarioRequest) => criarGrupoTributario(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_GRUPOS }),
  })
}

export function useAtualizarGrupoTributario() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: GrupoTributarioRequest }) => atualizarGrupoTributario(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_GRUPOS }),
  })
}

export function useExcluirGrupoTributario() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirGrupoTributario(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_GRUPOS }),
  })
}

// --- Operação Fiscal ---

export function useOperacoesFiscaisPaginado(pagina: number, tamanho = 10, nome?: string) {
  return useQuery({
    queryKey: [...QUERY_KEY_OPERACOES, pagina, tamanho, nome],
    queryFn: () => listarOperacoesFiscais(pagina, tamanho, nome),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useTodasOperacoesFiscais() {
  return useQuery({
    queryKey: [...QUERY_KEY_OPERACOES, 'todas'],
    queryFn: () => listarTodasOperacoesFiscais(),
  })
}

export function useCriarOperacaoFiscal() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: OperacaoFiscalRequest) => criarOperacaoFiscal(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_OPERACOES }),
  })
}

export function useAtualizarOperacaoFiscal() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: OperacaoFiscalRequest }) => atualizarOperacaoFiscal(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_OPERACOES }),
  })
}

export function useExcluirOperacaoFiscal() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirOperacaoFiscal(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_OPERACOES }),
  })
}

// --- CFOP (busca assíncrona, mesmo padrão de `useBuscaNcm`) ---

export function useBuscaCfop(busca: string | undefined) {
  return useQuery({
    queryKey: ['cfop', busca],
    queryFn: () => buscarCfop(busca),
    enabled: busca !== undefined && busca.length >= 2,
  })
}

// --- Configuração Tributária ---

export function useConfiguracoesTributariasPaginado(pagina: number, tamanho = 10, grupoTributarioId?: number, operacaoFiscalId?: number) {
  return useQuery({
    queryKey: [...QUERY_KEY_CONFIGURACOES, pagina, tamanho, grupoTributarioId, operacaoFiscalId],
    queryFn: () => listarConfiguracoesTributarias(pagina, tamanho, grupoTributarioId, operacaoFiscalId),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useConfiguracaoTributaria(id: number | undefined) {
  return useQuery({
    queryKey: [...QUERY_KEY_CONFIGURACOES, id],
    queryFn: () => buscarConfiguracaoTributaria(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarConfiguracaoTributaria() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ConfiguracaoTributariaRequest) => criarConfiguracaoTributaria(request),
    onSuccess: (dados) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONFIGURACOES })
      queryClient.setQueryData([...QUERY_KEY_CONFIGURACOES, dados.id], dados)
    },
  })
}

export function useAtualizarConfiguracaoTributaria() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: ConfiguracaoTributariaRequest }) => atualizarConfiguracaoTributaria(id, request),
    onSuccess: (dados) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONFIGURACOES })
      queryClient.setQueryData([...QUERY_KEY_CONFIGURACOES, dados.id], dados)
    },
  })
}

export function useExcluirConfiguracaoTributaria() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirConfiguracaoTributaria(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_CONFIGURACOES }),
  })
}
