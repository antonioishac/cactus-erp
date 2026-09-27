import { beforeEach, describe, expect, it, vi } from 'vitest'
import type { User } from 'oidc-client-ts'

// Mock do módulo inteiro de chamada HTTP a trocar-empresa — o authStore não deve
// precisar de env vars/axios reais pra ser testado (ver httpClients.ts, que exige
// VITE_SEGURANCA_API_URL definido; em modo de teste isso não está carregado).
vi.mock('./trocarEmpresa', () => ({
  trocarEmpresa: vi.fn(),
}))

import { useAuthStore } from './authStore'
import { trocarEmpresa } from './trocarEmpresa'

const trocarEmpresaMock = vi.mocked(trocarEmpresa)

// btoa() em vez de Buffer — mantém o arquivo compilável sob o tsconfig do app
// (só tipos de browser, "types": ["vite/client"], sem @types/node) e é
// suficiente aqui: claims de teste só usam ASCII (números/strings simples).
function base64UrlEncode(value: unknown): string {
  const base64 = btoa(JSON.stringify(value))
  return base64.replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '')
}

/** JWT "falso" só pra jwt-decode conseguir ler o payload — sem assinatura real,
 *  porque a validação de assinatura é responsabilidade dos Resource Servers,
 *  nunca do frontend (ver tokenClaims.ts). */
function fakeJwt(claims: {
  usuarioId: number
  login: string
  empresaId?: number
  authorities: string[]
}): string {
  const header = base64UrlEncode({ alg: 'none', typ: 'JWT' })
  const payload = base64UrlEncode({ sub: claims.login, iat: 0, exp: 9999999999, ...claims })
  return `${header}.${payload}.fake-signature`
}

function fakeUser(claims: {
  usuarioId: number
  login: string
  empresaId?: number
  authorities: string[]
}): User {
  return { access_token: fakeJwt(claims) } as User
}

describe('authStore', () => {
  beforeEach(() => {
    useAuthStore.getState().reset()
    trocarEmpresaMock.mockReset()
  })

  it('login inicial: empresa ativa e padrão começam iguais, token efetivo = token da sessão', () => {
    const user = fakeUser({ usuarioId: 1, login: 'ana', empresaId: 1, authorities: ['CADASTRO_CLIENTE_CONSULTAR'] })

    useAuthStore.getState().setOidcUserFromLogin(user)

    const state = useAuthStore.getState()
    expect(state.status).toBe('authenticated')
    expect(state.initialized).toBe(true)
    expect(state.empresaAtivaId).toBe(1)
    expect(state.empresaPadraoId).toBe(1)
    expect(state.effectiveToken).toBe(user.access_token)
    expect(state.effectiveAuthorities).toEqual(['CADASTRO_CLIENTE_CONSULTAR'])
    expect(state.login).toBe('ana')
  })

  it('troca de empresa atualiza effectiveToken/empresaAtivaId sem tocar no oidcUser', async () => {
    const user = fakeUser({ usuarioId: 1, login: 'ana', empresaId: 1, authorities: ['A'] })
    useAuthStore.getState().setOidcUserFromLogin(user)

    trocarEmpresaMock.mockResolvedValueOnce({
      accessToken: 'token-empresa-2',
      tokenType: 'Bearer',
      expiresInSeconds: 1800,
      empresaId: 2,
      authorities: ['B'],
    })

    await useAuthStore.getState().trocarEmpresaAtiva(2)

    const state = useAuthStore.getState()
    expect(trocarEmpresaMock).toHaveBeenCalledWith(2, user.access_token)
    expect(state.empresaAtivaId).toBe(2)
    expect(state.effectiveToken).toBe('token-empresa-2')
    expect(state.effectiveAuthorities).toEqual(['B'])
    // oidcUser (sessão original) não muda com a troca de empresa
    expect(state.oidcUser?.access_token).toBe(user.access_token)
  })

  it('renovação silenciosa NA empresa padrão: só acompanha o token renovado, sem chamar trocar-empresa', async () => {
    const user = fakeUser({ usuarioId: 1, login: 'ana', empresaId: 1, authorities: ['A'] })
    useAuthStore.getState().setOidcUserFromLogin(user)

    const renewedUser = fakeUser({ usuarioId: 1, login: 'ana', empresaId: 1, authorities: ['A', 'A2'] })
    await useAuthStore.getState().handleUserRenewed(renewedUser)

    expect(trocarEmpresaMock).not.toHaveBeenCalled()
    const state = useAuthStore.getState()
    expect(state.effectiveToken).toBe(renewedUser.access_token)
    expect(state.effectiveAuthorities).toEqual(['A', 'A2'])
    expect(state.empresaAtivaId).toBe(1)
  })

  it('renovação silenciosa FORA da empresa padrão: rechama trocar-empresa com o token recém-renovado', async () => {
    const user = fakeUser({ usuarioId: 1, login: 'ana', empresaId: 1, authorities: ['A'] })
    useAuthStore.getState().setOidcUserFromLogin(user)

    trocarEmpresaMock.mockResolvedValueOnce({
      accessToken: 'token-empresa-2',
      tokenType: 'Bearer',
      expiresInSeconds: 1800,
      empresaId: 2,
      authorities: ['B'],
    })
    await useAuthStore.getState().trocarEmpresaAtiva(2)
    trocarEmpresaMock.mockReset()

    // Renovação silenciosa: servidor sempre resolve a empresa PADRÃO de novo
    // (limitação documentada) — claims do token renovado voltam pra empresa 1.
    const renewedUser = fakeUser({ usuarioId: 1, login: 'ana', empresaId: 1, authorities: ['A'] })
    trocarEmpresaMock.mockResolvedValueOnce({
      accessToken: 'token-empresa-2-renovado',
      tokenType: 'Bearer',
      expiresInSeconds: 1800,
      empresaId: 2,
      authorities: ['B'],
    })

    await useAuthStore.getState().handleUserRenewed(renewedUser)

    expect(trocarEmpresaMock).toHaveBeenCalledWith(2, renewedUser.access_token)
    const state = useAuthStore.getState()
    expect(state.effectiveToken).toBe('token-empresa-2-renovado')
    expect(state.empresaAtivaId).toBe(2)
    expect(state.empresaPadraoId).toBe(1)
  })

  it('falha ao rechamar trocar-empresa na renovação força reset (relogin limpo)', async () => {
    const user = fakeUser({ usuarioId: 1, login: 'ana', empresaId: 1, authorities: ['A'] })
    useAuthStore.getState().setOidcUserFromLogin(user)
    trocarEmpresaMock.mockResolvedValueOnce({
      accessToken: 'token-empresa-2',
      tokenType: 'Bearer',
      expiresInSeconds: 1800,
      empresaId: 2,
      authorities: ['B'],
    })
    await useAuthStore.getState().trocarEmpresaAtiva(2)

    trocarEmpresaMock.mockReset()
    trocarEmpresaMock.mockRejectedValueOnce(new Error('sem acesso'))
    const renewedUser = fakeUser({ usuarioId: 1, login: 'ana', empresaId: 1, authorities: ['A'] })

    await useAuthStore.getState().handleUserRenewed(renewedUser)

    const state = useAuthStore.getState()
    expect(state.status).toBe('idle')
    expect(state.effectiveToken).toBeNull()
    expect(state.oidcUser).toBeNull()
  })

  it('handleUnauthorized/reset limpa todo o estado de sessão', () => {
    const user = fakeUser({ usuarioId: 1, login: 'ana', empresaId: 1, authorities: ['A'] })
    useAuthStore.getState().setOidcUserFromLogin(user)

    useAuthStore.getState().handleUnauthorized()

    const state = useAuthStore.getState()
    expect(state.status).toBe('idle')
    expect(state.oidcUser).toBeNull()
    expect(state.effectiveToken).toBeNull()
    expect(state.effectiveAuthorities).toEqual([])
    expect(state.empresaAtivaId).toBeNull()
    // initialized continua true — já sabemos que não há sessão, não precisa
    // voltar a mostrar o spinner de "checando sessão" (ver RequireAuth.tsx)
    expect(state.initialized).toBe(true)
  })
})
