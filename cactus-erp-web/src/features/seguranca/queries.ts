import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  alterarSenhaUsuario,
  atribuirFuncionalidades,
  atribuirPapelEmpresa,
  atualizarPapel,
  atualizarUsuario,
  bloquearUsuario,
  buscarColaboradoresParaSelecao,
  buscarEmpresasParaSelecao,
  buscarEmpresaResumo,
  buscarColaboradorResumo,
  buscarPapel,
  buscarUsuario,
  criarPapel,
  criarUsuario,
  desbloquearUsuario,
  excluirPapel,
  excluirUsuario,
  inativarUsuario,
  listarFuncionalidades,
  listarModulos,
  listarPapeis,
  listarPapeisEmpresaDoUsuario,
  listarTodasFuncionalidades,
  listarTodosModulos,
  listarTodosPapeis,
  listarUsuarios,
  reativarUsuario,
  revogarPapelEmpresa,
} from './api'
import type {
  AlterarSenhaRequest,
  AtribuirFuncionalidadesRequest,
  PapelRequest,
  UsuarioPapelEmpresaRequest,
  UsuarioRequest,
  UsuariosFiltro,
} from './types'

const QUERY_KEY_USUARIOS = ['usuarios'] as const
const queryKeyUsuario = (id: number) => ['usuarios', id] as const
const QUERY_KEY_PAPEIS = ['papeis'] as const
const queryKeyPapel = (id: number) => ['papeis', id] as const

// --- Usuário ---

export function useUsuarios(filtro: UsuariosFiltro, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_USUARIOS, filtro, pagina, tamanho],
    queryFn: () => listarUsuarios(filtro, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useUsuario(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyUsuario(id ?? -1),
    queryFn: () => buscarUsuario(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarUsuario() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: UsuarioRequest) => criarUsuario(request),
    onSuccess: (criado) => {
      queryClient.setQueryData(queryKeyUsuario(criado.id), criado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_USUARIOS })
    },
  })
}

export function useAtualizarUsuario(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: UsuarioRequest) => atualizarUsuario(id, request),
    onSuccess: (atualizado) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_USUARIOS })
      queryClient.setQueryData(queryKeyUsuario(id), atualizado)
    },
  })
}

function useAcaoUsuario(acao: (id: number) => Promise<import('./types').UsuarioResponse>) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => acao(id),
    onSuccess: (atualizado, id) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_USUARIOS })
      queryClient.setQueryData(queryKeyUsuario(id), atualizado)
    },
  })
}

export function useBloquearUsuario() {
  return useAcaoUsuario(bloquearUsuario)
}
export function useDesbloquearUsuario() {
  return useAcaoUsuario(desbloquearUsuario)
}
export function useInativarUsuario() {
  return useAcaoUsuario(inativarUsuario)
}
export function useReativarUsuario() {
  return useAcaoUsuario(reativarUsuario)
}

export function useAlterarSenhaUsuario(id: number) {
  return useMutation({
    mutationFn: (request: AlterarSenhaRequest) => alterarSenhaUsuario(id, request),
  })
}

export function useExcluirUsuario() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirUsuario(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_USUARIOS }),
  })
}

// --- Papel por Empresa ---

export function usePapeisEmpresaDoUsuario(usuarioId: number | undefined) {
  return useQuery({
    queryKey: ['papeis-empresa', usuarioId ?? -1],
    queryFn: () => listarPapeisEmpresaDoUsuario(usuarioId as number),
    enabled: usuarioId !== undefined,
  })
}

export function useAtribuirPapelEmpresa(usuarioId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: UsuarioPapelEmpresaRequest) => atribuirPapelEmpresa(usuarioId, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['papeis-empresa', usuarioId] }),
  })
}

export function useRevogarPapelEmpresa(usuarioId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (papelEmpresaId: number) => revogarPapelEmpresa(usuarioId, papelEmpresaId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['papeis-empresa', usuarioId] }),
  })
}

// --- Papel ---

export function usePapeis(nome: string | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: [...QUERY_KEY_PAPEIS, nome, pagina, tamanho],
    queryFn: () => listarPapeis(nome, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useTodosPapeis() {
  return useQuery({ queryKey: ['papeis-todos'], queryFn: listarTodosPapeis, staleTime: 5 * 60_000 })
}

export function usePapel(id: number | undefined) {
  return useQuery({
    queryKey: queryKeyPapel(id ?? -1),
    queryFn: () => buscarPapel(id as number),
    enabled: id !== undefined,
  })
}

export function useCriarPapel() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: PapelRequest) => criarPapel(request),
    onSuccess: (criado) => {
      queryClient.setQueryData(queryKeyPapel(criado.id), criado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_PAPEIS })
      queryClient.invalidateQueries({ queryKey: ['papeis-todos'] })
    },
  })
}

export function useAtualizarPapel(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: PapelRequest) => atualizarPapel(id, request),
    onSuccess: (atualizado) => {
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_PAPEIS })
      queryClient.invalidateQueries({ queryKey: ['papeis-todos'] })
      queryClient.setQueryData(queryKeyPapel(id), atualizado)
    },
  })
}

export function useAtribuirFuncionalidades(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: AtribuirFuncionalidadesRequest) => atribuirFuncionalidades(id, request),
    onSuccess: (atualizado) => {
      queryClient.setQueryData(queryKeyPapel(id), atualizado)
      queryClient.invalidateQueries({ queryKey: QUERY_KEY_PAPEIS })
    },
  })
}

export function useExcluirPapel() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => excluirPapel(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY_PAPEIS }),
  })
}

// --- Funcionalidade / Módulo (só leitura) ---

export function useFuncionalidades(moduloId: number | undefined, pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: ['funcionalidades', moduloId, pagina, tamanho],
    queryFn: () => listarFuncionalidades(moduloId, pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useTodasFuncionalidades() {
  return useQuery({ queryKey: ['funcionalidades-todas'], queryFn: listarTodasFuncionalidades, staleTime: 5 * 60_000 })
}

export function useModulos(pagina: number, tamanho = 10) {
  return useQuery({
    queryKey: ['modulos', pagina, tamanho],
    queryFn: () => listarModulos(pagina, tamanho),
    placeholderData: (dadosAnteriores) => dadosAnteriores,
  })
}

export function useTodosModulos() {
  return useQuery({ queryKey: ['modulos-todos'], queryFn: listarTodosModulos, staleTime: 5 * 60_000 })
}

// --- Lookups cross-serviço ---

export function useBuscaEmpresasParaSelecao(nome: string | undefined) {
  return useQuery({
    queryKey: ['empresas-para-selecao-seguranca', nome],
    queryFn: () => buscarEmpresasParaSelecao(nome),
    enabled: nome !== undefined && nome.length >= 2,
  })
}

export function useEmpresaResumo(id: number | null | undefined) {
  return useQuery({
    queryKey: ['empresa-resumo-seguranca', id],
    queryFn: () => buscarEmpresaResumo(id as number),
    enabled: id != null,
    staleTime: 5 * 60_000,
  })
}

export function useBuscaColaboradoresParaSelecao(nome: string | undefined) {
  return useQuery({
    queryKey: ['colaboradores-para-selecao-seguranca', nome],
    queryFn: () => buscarColaboradoresParaSelecao(nome),
    enabled: nome !== undefined && nome.length >= 2,
  })
}

/**
 * `id` aceita `null` de propósito — `Usuario.colaboradorId` vem `null` no
 * JSON quando não há vínculo (campo opcional em Java, `Long` sem valor),
 * não `undefined` (que só existiria se a chave estivesse ausente do objeto).
 * Um guard `!== undefined` deixava passar `null` e disparava
 * `GET /colaboradores/null` (400 real, achado ao vivo, 2026-09-02).
 */
export function useColaboradorResumo(id: number | null | undefined) {
  return useQuery({
    queryKey: ['colaborador-resumo-seguranca', id],
    queryFn: () => buscarColaboradorResumo(id as number),
    enabled: id != null,
    staleTime: 5 * 60_000,
  })
}
