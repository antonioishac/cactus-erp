import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  atualizarContador,
  atualizarEmpresa,
  buscarConfiguracaoFiscal,
  buscarConfiguracaoOperacional,
  buscarEmpresa,
  criarContador,
  criarEmpresa,
  listarContadores,
  listarEmpresas,
  listarRegimeTributario,
  registrarRegimeTributario,
  salvarConfiguracaoFiscal,
  salvarConfiguracaoOperacional,
} from './api'
import type {
  ConfiguracaoOperacionalLojaRequest,
  ContadorRequest,
  EmpresaConfiguracaoFiscalRequest,
  EmpresaRegimeTributarioRequest,
  EmpresaRequest,
  EmpresasFiltro,
} from './types'

const QUERY_KEY_LISTA = ['empresas'] as const
const queryKeyDetalhe = (id: number) => ['empresas', id] as const
const queryKeyRegimeTributario = (empresaId: number) => ['empresas', empresaId, 'regime-tributario'] as const
const queryKeyConfigOperacional = (empresaId: number) => ['empresas', empresaId, 'configuracao-operacional'] as const
const queryKeyConfigFiscal = (empresaId: number) => ['empresas', empresaId, 'configuracao-fiscal'] as const

export function useEmpresas(filtro: EmpresasFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_LISTA, filtro, pagina, tamanho],
    queryFn: () => listarEmpresas(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useEmpresa(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyDetalhe(id ?? -1),
    queryFn: () => buscarEmpresa(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarEmpresa() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: EmpresaRequest) => criarEmpresa(request),
    onSuccess: (criada) => {
      // Popula o cache de detalhe com o resultado que a própria criação já
      // devolveu — sem isso, `EmpresaFormPage` navega pro modo edição e
      // `useEmpresa(id)` dispara um fetch novo do zero, com um flash de
      // `Skeleton` (e, em Empresa especificamente, as abas de sub-recurso
      // ficam `disabled` por esse instante, já que dependem de `modoEdicao`
      // — achado real ao vivo, 2026-09-01). Mesmo fix aplicado em
      // useCriarCliente/useCriarFornecedor/useCriarColaborador.
      queryClient.setQueryData(queryKeyDetalhe(criada.id), criada)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LISTA })
    },
  })
}

export function useAtualizarEmpresa(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: EmpresaRequest) => atualizarEmpresa(id, request),
    onSuccess: (atualizado) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_LISTA })
      queryClient.setQueryData(queryKeyDetalhe(id), atualizado)
    },
  })
}

export function useContadores() {
  return useQuery({ queryKey: ['contadores'], queryFn: listarContadores, staleTime: 5 * 60_000 })
}

// --- Regime tributário ---

export function useRegimeTributario(empresaId: number | undefined) {
  return useQuery({
    queryKey: queryKeyRegimeTributario(empresaId ?? -1),
    queryFn: () => listarRegimeTributario(empresaId as number),
    enabled: empresaId !== undefined,
  })
}

export function useRegistrarRegimeTributario(empresaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: EmpresaRegimeTributarioRequest) => registrarRegimeTributario(empresaId, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeyRegimeTributario(empresaId) })
    },
  })
}

// --- Configuração operacional ---

export function useConfiguracaoOperacional(empresaId: number | undefined) {
  return useQuery({
    queryKey: queryKeyConfigOperacional(empresaId ?? -1),
    queryFn: () => buscarConfiguracaoOperacional(empresaId as number),
    enabled: empresaId !== undefined,
  })
}

export function useSalvarConfiguracaoOperacional(empresaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ConfiguracaoOperacionalLojaRequest) => salvarConfiguracaoOperacional(empresaId, request),
    onSuccess: (atualizado) => {
      queryClient.setQueryData(queryKeyConfigOperacional(empresaId), atualizado)
    },
  })
}

// --- Configuração fiscal ---

export function useConfiguracaoFiscal(empresaId: number | undefined) {
  return useQuery({
    queryKey: queryKeyConfigFiscal(empresaId ?? -1),
    queryFn: () => buscarConfiguracaoFiscal(empresaId as number),
    enabled: empresaId !== undefined,
  })
}

export function useSalvarConfiguracaoFiscal(empresaId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: EmpresaConfiguracaoFiscalRequest) => salvarConfiguracaoFiscal(empresaId, request),
    onSuccess: (atualizado) => {
      queryClient.setQueryData(queryKeyConfigFiscal(empresaId), atualizado)
    },
  })
}

export function useCriarContador() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: ContadorRequest) => criarContador(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['contadores'] }),
  })
}

export function useAtualizarContador() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: ContadorRequest }) => atualizarContador(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['contadores'] }),
  })
}
