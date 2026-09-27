import { useEffect } from 'react'
import { Button, Flex, Typography } from 'antd'
import { Link } from 'react-router-dom'
import { authClient } from './authClient'
import { useAuthStore } from './authStore'

const { Title, Text } = Typography

/**
 * Landing page após o `post_logout_redirect_uri` do end-session do Authorization
 * Server (seedado como http://localhost:5173/logout). A limpeza de sessão real já
 * aconteceu no servidor antes de chegar aqui — isto é só a limpeza local + feedback.
 */
export default function LogoutPage() {
  const reset = useAuthStore((s) => s.reset)

  useEffect(() => {
    reset()
    // Cinto e suspensório: garante que nenhum resquício de usuário fique no
    // localStorage mesmo se o redirect de end-session tiver sido interrompido.
    authClient.removeUser().catch(() => undefined)
  }, [reset])

  return (
    <Flex align="center" justify="center" style={{ minHeight: '100vh', padding: 24 }}>
      <Flex vertical align="center" gap={16}>
        <Title level={3}>Você saiu do sistema</Title>
        <Text type="secondary">Sua sessão foi encerrada com segurança.</Text>
        <Link to="/login">
          <Button type="primary">Entrar novamente</Button>
        </Link>
      </Flex>
    </Flex>
  )
}
