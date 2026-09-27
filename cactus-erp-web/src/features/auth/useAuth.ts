import { useAuthStore } from './authStore'
import { authClient } from './authClient'

/**
 * Fachada de hook sobre o `authStore` — telas/componentes consomem só isto,
 * nunca `useAuthStore` diretamente fora da feature `auth` (mantém o modelo
 * oidcUser/effectiveToken como detalhe de implementação encapsulado aqui).
 */
export function useAuth() {
  const status = useAuthStore((s) => s.status)
  const initialized = useAuthStore((s) => s.initialized)
  const login = useAuthStore((s) => s.login)
  const usuarioId = useAuthStore((s) => s.usuarioId)
  const empresaAtivaId = useAuthStore((s) => s.empresaAtivaId)
  const empresaPadraoId = useAuthStore((s) => s.empresaPadraoId)
  const authorities = useAuthStore((s) => s.effectiveAuthorities)
  const trocarEmpresaAtiva = useAuthStore((s) => s.trocarEmpresaAtiva)

  return {
    status,
    initialized,
    login,
    usuarioId,
    empresaAtivaId,
    empresaPadraoId,
    authorities,
    isAuthenticated: status === 'authenticated',
    trocarEmpresaAtiva,
    signIn: () => authClient.signinRedirect(),
    signOut: () => authClient.signoutRedirect(),
  }
}
