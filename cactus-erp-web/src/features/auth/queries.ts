import { useQuery } from '@tanstack/react-query'
import { segurancaApi } from '@shared/api/httpClients'
import { buscarEmpresasDisponiveis, type EmpresaDisponivel } from './trocarEmpresa'
import { useAuthStore } from './authStore'

interface MeResponse {
  usuarioId: number
  login: string
  empresaId: number | null
  authorities: string[]
}

/** `GET /api/v1/auth/me` — primeiro consumidor real do padrão TanStack Query
 *  desta entrega (as demais telas de listagem futuras seguem o mesmo padrão). */
export function useMe() {
  const isAuthenticated = useAuthStore((s) => s.status === 'authenticated')
  return useQuery({
    queryKey: ['auth', 'me'],
    queryFn: async () => (await segurancaApi.get<MeResponse>('/api/v1/auth/me')).data,
    enabled: isAuthenticated,
    staleTime: 60_000,
  })
}

/** `GET /api/v1/auth/empresas-disponiveis` — usa o token de sessão OIDC (não o
 *  effectiveToken), ver trocarEmpresa.ts para o porquê. */
export function useEmpresasDisponiveis() {
  const oidcAccessToken = useAuthStore((s) => s.oidcUser?.access_token ?? null)
  return useQuery<EmpresaDisponivel[]>({
    queryKey: ['auth', 'empresas-disponiveis'],
    queryFn: () => buscarEmpresasDisponiveis(oidcAccessToken as string),
    enabled: !!oidcAccessToken,
    staleTime: 60_000,
  })
}
