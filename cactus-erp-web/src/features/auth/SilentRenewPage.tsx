import { useEffect } from 'react'
import { authClient } from './authClient'

/**
 * Alvo do iframe oculto de renovação silenciosa (`silent_redirect_uri`, ver
 * `authClient.ts`) — carregado automaticamente pela lib (`automaticSilentRenew: true`)
 * perto da expiração do access token, dentro de um iframe invisível que o usuário
 * nunca vê. `signinSilentCallback()` processa a resposta de
 * `/oauth2/authorize?...&prompt=none` e resolve via `postMessage` pro `UserManager`
 * da janela pai — não navega, não renderiza nada visível.
 * <p>
 * Rota pública (fora de `RequireAuth`, mesmo padrão de `/callback`) porque o iframe
 * carrega essa página sem nenhum estado de aplicação restaurado ainda.
 */
export default function SilentRenewPage() {
  useEffect(() => {
    authClient.signinSilentCallback().catch((err: unknown) => {
      // Sem UI própria aqui — quem trata falha de renovação é addSilentRenewError,
      // no AppProviders (roda na janela pai, não dentro deste iframe).
      console.error('[auth] Falha na renovação silenciosa', err)
    })
  }, [])

  return null
}
