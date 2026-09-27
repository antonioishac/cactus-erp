import { useEffect, type ReactNode } from 'react'
import { ConfigProvider, App as AntApp } from 'antd'
import ptBR from 'antd/locale/pt_BR'
import { QueryClientProvider } from '@tanstack/react-query'
import type { User } from 'oidc-client-ts'
import { authClient } from '@features/auth/authClient'
import { useAuthStore } from '@features/auth/authStore'
import { configureHttpAuth } from '@shared/api/httpClients'
import { queryClient } from '@shared/api/queryClient'
import { cactusTheme } from '@app/theme/antdTheme'

/**
 * Composition root: liga o `authStore` ao `oidc-client-ts` (restaura sessão
 * salva no boot, assina renovação silenciosa) e ao `httpClients` (injeção de
 * dependência — ver comentário em shared/api/httpClients.ts), e monta os
 * providers globais (tema antd, TanStack Query). Roda uma única vez, no topo
 * da árvore (ver main.tsx).
 */
export default function AppProviders({ children }: { children: ReactNode }) {
  const setOidcUserFromLogin = useAuthStore((s) => s.setOidcUserFromLogin)
  const handleUserRenewed = useAuthStore((s) => s.handleUserRenewed)
  const markInitialized = useAuthStore((s) => s.markInitialized)
  const handleUnauthorized = useAuthStore((s) => s.handleUnauthorized)

  useEffect(() => {
    configureHttpAuth(
      () => useAuthStore.getState().effectiveToken,
      () => useAuthStore.getState().handleUnauthorized(),
    )

    // Restaura sessão existente do localStorage (F5, nova aba) sem precisar
    // repetir o fluxo de redirect.
    authClient
      .getUser()
      .then((user) => {
        if (user && !user.expired) {
          setOidcUserFromLogin(user)
        } else {
          markInitialized()
        }
      })
      .catch(() => markInitialized())

    // Renovação silenciosa via iframe oculto + prompt=none (não refresh_token —
    // o Authorization Server não emite um pra client público, ver authClient.ts),
    // disparada automaticamente pela lib perto da expiração do access token.
    // `addUserLoaded` dispara igual não importa o mecanismo de renovação por
    // trás — ver authStore.handleUserRenewed para a interação com a empresa ativa.
    const onUserLoaded = (user: User) => {
      handleUserRenewed(user).catch(() => undefined)
    }
    const onSilentRenewError = () => {
      // Sessão do Authorization Server expirada/inválida (cookie de sessão,
      // TTL de 12h — ver seguranca-service/application.yml) ou usuário revogou
      // acesso — prompt=none não consegue renovar sem mostrar login. Sessão não
      // é mais recuperável em background, força relogin limpo.
      handleUnauthorized()
    }

    authClient.events.addUserLoaded(onUserLoaded)
    authClient.events.addSilentRenewError(onSilentRenewError)

    return () => {
      authClient.events.removeUserLoaded(onUserLoaded)
      authClient.events.removeSilentRenewError(onSilentRenewError)
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps -- só monta uma vez, ações do zustand são estáveis
  }, [])

  return (
    <ConfigProvider theme={cactusTheme} locale={ptBR}>
      <AntApp>
        <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
      </AntApp>
    </ConfigProvider>
  )
}
