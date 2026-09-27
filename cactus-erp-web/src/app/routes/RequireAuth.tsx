import type { ReactNode } from 'react'
import { Flex, Spin } from 'antd'
import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '@features/auth/useAuth'

interface RequireAuthProps {
  children: ReactNode
}

/**
 * Guarda de rota de nível 1 (autenticação). `initialized` distingue "app ainda
 * checando se há sessão salva no localStorage" de "sei que não há sessão" —
 * sem isso, todo F5 pisca a tela de login antes de redirecionar de volta.
 */
export default function RequireAuth({ children }: RequireAuthProps) {
  const { isAuthenticated, initialized } = useAuth()
  const location = useLocation()

  if (!initialized) {
    return (
      <Flex align="center" justify="center" style={{ minHeight: '100vh' }}>
        <Spin size="large" />
      </Flex>
    )
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />
  }

  return <>{children}</>
}
