import type { ReactNode } from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth } from '@features/auth/useAuth'
import { hasAnyAuthority } from '@features/auth/authority'

interface RequireAuthorityProps {
  anyOf: string[]
  children: ReactNode
}

/**
 * Guarda de rota de nível 2 (autorização) — assume que `RequireAuth` já
 * garantiu que há usuário autenticado (esta guarda só decide SE ele pode ver
 * a seção, não SE ele está logado). Usuário sem nenhuma das autoridades
 * exigidas volta pro dashboard, não pra tela de login.
 */
export default function RequireAuthority({ anyOf, children }: RequireAuthorityProps) {
  const { authorities } = useAuth()

  if (!hasAnyAuthority(authorities, anyOf)) {
    return <Navigate to="/" replace />
  }

  return <>{children}</>
}
