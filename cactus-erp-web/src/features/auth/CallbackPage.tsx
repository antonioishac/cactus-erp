import { useEffect, useRef, useState } from 'react'
import { Alert, Button, Flex, Spin, Typography } from 'antd'
import { Link, useNavigate } from 'react-router-dom'
import { authClient } from './authClient'
import { useAuthStore } from './authStore'

const { Text } = Typography

/**
 * Rota de retorno do fluxo Authorization Code + PKCE (`redirect_uri` seedado no
 * backend: http://localhost:5173/callback). `signinRedirectCallback()` troca o
 * `code` pelo token no `/oauth2/token` do seguranca-service (a lib cuida do
 * `code_verifier` sozinha) e resolve o `User` do oidc-client-ts.
 */
export default function CallbackPage() {
  const navigate = useNavigate()
  const [erro, setErro] = useState<string | null>(null)
  const setOidcUserFromLogin = useAuthStore((s) => s.setOidcUserFromLogin)
  // Guarda a CHAMADA em si (não só o resultado) contra o double-invoke do
  // React.StrictMode em dev — o `code` do Authorization Code Flow só pode ser
  // trocado uma vez; chamar signinRedirectCallback() duas vezes gera uma corrida
  // real contra o servidor, não algo que dê pra resolver só ignorando o 2º resultado.
  const iniciado = useRef(false)

  useEffect(() => {
    if (iniciado.current) return
    iniciado.current = true

    authClient
      .signinRedirectCallback()
      .then((user) => {
        setOidcUserFromLogin(user)
        navigate('/', { replace: true })
      })
      .catch((err: unknown) => {
        console.error('[auth] Falha ao concluir login', err)
        setErro(err instanceof Error ? err.message : 'Falha desconhecida ao concluir o login.')
      })
  }, [navigate, setOidcUserFromLogin])

  if (erro) {
    return (
      <Flex align="center" justify="center" style={{ minHeight: '100vh', padding: 24 }}>
        <Alert
          type="error"
          showIcon
          title="Não foi possível concluir o login"
          description={erro}
          style={{ maxWidth: 480 }}
          action={
            <Link to="/login">
              <Button size="small" danger>
                Tentar novamente
              </Button>
            </Link>
          }
        />
      </Flex>
    )
  }

  return (
    <Flex vertical align="center" justify="center" gap={16} style={{ minHeight: '100vh' }}>
      <Spin size="large" />
      <Text type="secondary">Concluindo login...</Text>
    </Flex>
  )
}
