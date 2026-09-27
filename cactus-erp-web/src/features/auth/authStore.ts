import type { User } from 'oidc-client-ts'
import { create } from 'zustand'
import { decodeClaims } from './tokenClaims'
import { trocarEmpresa } from './trocarEmpresa'

export type AuthStatus = 'idle' | 'authenticating' | 'authenticated' | 'error'

interface AuthState {
  /** Token de SESSÃO do oidc-client-ts — existe só pra manter login/refresh vivos.
   *  NUNCA usar `oidcUser.access_token` diretamente numa chamada de API de negócio. */
  oidcUser: User | null
  /** Empresa que o servidor resolveria "por padrão" a partir do token de sessão
   *  atual (primeira empresa do usuário — ver TokenClaimsService no backend). */
  empresaPadraoId: number | null
  /** Empresa que o usuário efetivamente escolheu operar (pode ser igual à padrão). */
  empresaAtivaId: number | null
  /** Token DE FATO anexado a toda chamada de API de negócio — ver httpClients.ts.
   *  É o access_token da sessão quando empresaAtiva === empresaPadrao, ou o token
   *  reemitido por trocar-empresa caso contrário. */
  effectiveToken: string | null
  effectiveAuthorities: string[]
  usuarioId: number | null
  login: string | null
  status: AuthStatus
  /** true assim que a checagem de sessão existente (no boot do app) terminou —
   *  distingue "ainda não sei se há sessão" de "sei que não há sessão". */
  initialized: boolean

  setOidcUserFromLogin: (user: User) => void
  handleUserRenewed: (user: User) => Promise<void>
  trocarEmpresaAtiva: (empresaId: number) => Promise<void>
  handleUnauthorized: () => void
  markInitialized: () => void
  reset: () => void
}

const initialState = {
  oidcUser: null as User | null,
  empresaPadraoId: null as number | null,
  empresaAtivaId: null as number | null,
  effectiveToken: null as string | null,
  effectiveAuthorities: [] as string[],
  usuarioId: null as number | null,
  login: null as string | null,
  status: 'idle' as AuthStatus,
  initialized: false,
}

export const useAuthStore = create<AuthState>((set, get) => ({
  ...initialState,

  setOidcUserFromLogin: (user) => {
    const claims = decodeClaims(user.access_token)
    const empresaId = claims.empresaId ?? null
    set({
      oidcUser: user,
      usuarioId: claims.usuarioId,
      login: claims.login,
      empresaPadraoId: empresaId,
      empresaAtivaId: empresaId,
      effectiveToken: user.access_token,
      effectiveAuthorities: claims.authorities,
      status: 'authenticated',
      initialized: true,
    })
  },

  handleUserRenewed: async (renewedUser) => {
    const claims = decodeClaims(renewedUser.access_token)
    const empresaPadraoId = claims.empresaId ?? null
    const empresaAtivaId = get().empresaAtivaId

    // Renovação silenciosa sempre resolve a empresa PADRÃO de novo (limitação
    // conhecida do backend — ver CLAUDE.md, "Sessão multi-empresa e refresh_token").
    // Se o usuário está operando na empresa padrão mesmo, o token renovado já serve.
    if (empresaAtivaId === null || empresaAtivaId === empresaPadraoId) {
      set({
        oidcUser: renewedUser,
        empresaPadraoId,
        empresaAtivaId: empresaPadraoId,
        effectiveToken: renewedUser.access_token,
        effectiveAuthorities: claims.authorities,
      })
      return
    }

    // Usuário estava numa empresa não-padrão: o token renovado reverteu pra
    // padrão silenciosamente — rechama trocar-empresa antes que qualquer chamada
    // de negócio use esse token errado.
    try {
      const response = await trocarEmpresa(empresaAtivaId, renewedUser.access_token)
      set({
        oidcUser: renewedUser,
        empresaPadraoId,
        empresaAtivaId: response.empresaId,
        effectiveToken: response.accessToken,
        effectiveAuthorities: response.authorities,
      })
    } catch {
      // Estado não pode ser garantido consistente (ex: usuário perdeu o papel
      // na empresa ativa entre uma renovação e outra) — força relogin limpo em
      // vez de arriscar operar com authorities erradas.
      get().reset()
    }
  },

  trocarEmpresaAtiva: async (empresaId) => {
    const oidcUser = get().oidcUser
    if (!oidcUser) return
    const response = await trocarEmpresa(empresaId, oidcUser.access_token)
    set({
      empresaAtivaId: response.empresaId,
      effectiveToken: response.accessToken,
      effectiveAuthorities: response.authorities,
    })
  },

  handleUnauthorized: () => {
    get().reset()
  },

  markInitialized: () => set({ initialized: true }),

  reset: () => set({ ...initialState, initialized: true }),
}))
