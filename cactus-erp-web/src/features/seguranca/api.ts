import { cadastroBaseApi, segurancaApi } from '@shared/api/httpClients'
import type { PageResponse } from '@shared/api/pageResponse'
import type {
  AlterarSenhaRequest,
  AtribuirFuncionalidadesRequest,
  ColaboradorResumo,
  EmpresaResumo,
  FuncionalidadeResponse,
  ModuloResponse,
  PapelRequest,
  PapelResponse,
  UsuarioPapelEmpresaRequest,
  UsuarioPapelEmpresaResponse,
  UsuarioRequest,
  UsuarioResponse,
  UsuariosFiltro,
} from './types'

// --- Usuário ---

export async function listarUsuarios(
  filtro: UsuariosFiltro,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<UsuarioResponse>> {
  const { data } = await segurancaApi.get<PageResponse<UsuarioResponse>>('/api/v1/usuarios', {
    params: { busca: filtro.busca, page: pagina, size: tamanho },
  })
  return data
}

export async function buscarUsuario(id: number): Promise<UsuarioResponse> {
  const { data } = await segurancaApi.get<UsuarioResponse>(`/api/v1/usuarios/${id}`)
  return data
}

export async function criarUsuario(request: UsuarioRequest): Promise<UsuarioResponse> {
  const { data } = await segurancaApi.post<UsuarioResponse>('/api/v1/usuarios', request)
  return data
}

export async function atualizarUsuario(id: number, request: UsuarioRequest): Promise<UsuarioResponse> {
  const { data } = await segurancaApi.put<UsuarioResponse>(`/api/v1/usuarios/${id}`, request)
  return data
}

export async function alterarSenhaUsuario(id: number, request: AlterarSenhaRequest): Promise<void> {
  await segurancaApi.patch(`/api/v1/usuarios/${id}/senha`, request)
}

export async function bloquearUsuario(id: number): Promise<UsuarioResponse> {
  const { data } = await segurancaApi.patch<UsuarioResponse>(`/api/v1/usuarios/${id}/bloquear`)
  return data
}

export async function desbloquearUsuario(id: number): Promise<UsuarioResponse> {
  const { data } = await segurancaApi.patch<UsuarioResponse>(`/api/v1/usuarios/${id}/desbloquear`)
  return data
}

export async function inativarUsuario(id: number): Promise<UsuarioResponse> {
  const { data } = await segurancaApi.patch<UsuarioResponse>(`/api/v1/usuarios/${id}/inativar`)
  return data
}

export async function reativarUsuario(id: number): Promise<UsuarioResponse> {
  const { data } = await segurancaApi.patch<UsuarioResponse>(`/api/v1/usuarios/${id}/reativar`)
  return data
}

export async function excluirUsuario(id: number): Promise<void> {
  await segurancaApi.delete(`/api/v1/usuarios/${id}`)
}

// --- Papel por Empresa (sub-recurso de Usuário) ---

export async function listarPapeisEmpresaDoUsuario(usuarioId: number): Promise<UsuarioPapelEmpresaResponse[]> {
  const { data } = await segurancaApi.get<UsuarioPapelEmpresaResponse[]>(`/api/v1/usuarios/${usuarioId}/papeis-empresa`)
  return data
}

export async function atribuirPapelEmpresa(
  usuarioId: number,
  request: UsuarioPapelEmpresaRequest,
): Promise<UsuarioPapelEmpresaResponse> {
  const { data } = await segurancaApi.post<UsuarioPapelEmpresaResponse>(`/api/v1/usuarios/${usuarioId}/papeis-empresa`, request)
  return data
}

export async function revogarPapelEmpresa(usuarioId: number, papelEmpresaId: number): Promise<void> {
  await segurancaApi.delete(`/api/v1/usuarios/${usuarioId}/papeis-empresa/${papelEmpresaId}`)
}

// --- Papel ---

export async function listarPapeis(nome: string | undefined, pagina: number, tamanho = 10): Promise<PageResponse<PapelResponse>> {
  const { data } = await segurancaApi.get<PageResponse<PapelResponse>>('/api/v1/papeis', {
    params: { nome, page: pagina, size: tamanho },
  })
  return data
}

export async function listarTodosPapeis(): Promise<PapelResponse[]> {
  const { data } = await segurancaApi.get<PageResponse<PapelResponse>>('/api/v1/papeis', { params: { size: 200 } })
  return data.conteudo
}

export async function buscarPapel(id: number): Promise<PapelResponse> {
  const { data } = await segurancaApi.get<PapelResponse>(`/api/v1/papeis/${id}`)
  return data
}

export async function criarPapel(request: PapelRequest): Promise<PapelResponse> {
  const { data } = await segurancaApi.post<PapelResponse>('/api/v1/papeis', request)
  return data
}

export async function atualizarPapel(id: number, request: PapelRequest): Promise<PapelResponse> {
  const { data } = await segurancaApi.put<PapelResponse>(`/api/v1/papeis/${id}`, request)
  return data
}

export async function atribuirFuncionalidades(id: number, request: AtribuirFuncionalidadesRequest): Promise<PapelResponse> {
  const { data } = await segurancaApi.put<PapelResponse>(`/api/v1/papeis/${id}/funcionalidades`, request)
  return data
}

export async function excluirPapel(id: number): Promise<void> {
  await segurancaApi.delete(`/api/v1/papeis/${id}`)
}

// --- Funcionalidade (só leitura) ---

export async function listarFuncionalidades(
  moduloId: number | undefined,
  pagina: number,
  tamanho = 10,
): Promise<PageResponse<FuncionalidadeResponse>> {
  const { data } = await segurancaApi.get<PageResponse<FuncionalidadeResponse>>('/api/v1/funcionalidades', {
    params: { moduloId, page: pagina, size: tamanho },
  })
  return data
}

/** Catálogo inteiro, sem paginação — usado pra montar a matriz de funcionalidades no formulário de Papel. */
export async function listarTodasFuncionalidades(): Promise<FuncionalidadeResponse[]> {
  const { data } = await segurancaApi.get<PageResponse<FuncionalidadeResponse>>('/api/v1/funcionalidades', {
    params: { size: 500 },
  })
  return data.conteudo
}

// --- Módulo (só leitura) ---

export async function listarModulos(pagina: number, tamanho = 10): Promise<PageResponse<ModuloResponse>> {
  const { data } = await segurancaApi.get<PageResponse<ModuloResponse>>('/api/v1/modulos', {
    params: { page: pagina, size: tamanho },
  })
  return data
}

export async function listarTodosModulos(): Promise<ModuloResponse[]> {
  const { data } = await segurancaApi.get<PageResponse<ModuloResponse>>('/api/v1/modulos', { params: { size: 100 } })
  return data.conteudo
}

// --- Lookups cross-serviço (cadastro-base), só pra Selects ---

export async function buscarEmpresasParaSelecao(nome: string | undefined): Promise<EmpresaResumo[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<EmpresaResumo>>('/api/v1/empresas', {
    params: { nome, size: 20 },
  })
  return data.conteudo
}

export async function buscarEmpresaResumo(id: number): Promise<EmpresaResumo> {
  const { data } = await cadastroBaseApi.get<EmpresaResumo>(`/api/v1/empresas/${id}`)
  return data
}

export async function buscarColaboradoresParaSelecao(nome: string | undefined): Promise<ColaboradorResumo[]> {
  const { data } = await cadastroBaseApi.get<PageResponse<ColaboradorResumo>>('/api/v1/colaboradores', {
    params: { nome, size: 20 },
  })
  return data.conteudo
}

export async function buscarColaboradorResumo(id: number): Promise<ColaboradorResumo> {
  const { data } = await cadastroBaseApi.get<ColaboradorResumo>(`/api/v1/colaboradores/${id}`)
  return data
}
