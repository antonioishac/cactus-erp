import { segurancaApi } from '@shared/api/httpClients'

/** Espelha `TrocarEmpresaResponse` (seguranca-service, dto/TrocarEmpresaResponse.java). */
export interface TrocarEmpresaResponse {
  accessToken: string
  tokenType: string
  expiresInSeconds: number
  empresaId: number
  authorities: string[]
}

/** Espelha `EmpresaDisponivelResponse` (seguranca-service, AuthController). */
export interface EmpresaDisponivel {
  empresaId: number
  papeis: string[]
}

/**
 * Chama `POST /api/v1/auth/trocar-empresa`. Recebe explicitamente o token a usar
 * como Bearer (em vez de deixar o interceptor padrão resolver `effectiveToken`)
 * porque o chamador mais crítico — a renovação silenciosa em `authStore.ts` —
 * precisa usar o access_token que ACABOU de ser renovado pelo `oidc-client-ts`,
 * não o `effectiveToken` antigo (possivelmente escopado pra outra empresa).
 */
export async function trocarEmpresa(
  empresaId: number,
  bearerToken: string,
): Promise<TrocarEmpresaResponse> {
  const response = await segurancaApi.post<TrocarEmpresaResponse>(
    '/api/v1/auth/trocar-empresa',
    { empresaId },
    { headers: { Authorization: `Bearer ${bearerToken}` } },
  )
  return response.data
}

/**
 * Chama `GET /api/v1/auth/empresas-disponiveis`. Usa sempre o token de sessão OIDC
 * (não o `effectiveToken`) porque este endpoint precisa responder mesmo antes de
 * qualquer troca de empresa ter acontecido.
 */
export async function buscarEmpresasDisponiveis(bearerToken: string): Promise<EmpresaDisponivel[]> {
  const response = await segurancaApi.get<EmpresaDisponivel[]>(
    '/api/v1/auth/empresas-disponiveis',
    {
      headers: { Authorization: `Bearer ${bearerToken}` },
    },
  )
  return response.data
}
