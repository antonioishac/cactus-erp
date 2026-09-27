import { UserManager, WebStorageStateStore } from 'oidc-client-ts'
import { env } from '@shared/config/env'

/**
 * Cliente OIDC (Authorization Code + PKCE) contra o `seguranca-service`. Client
 * registrado no backend em `OAuth2ClientSeeder.clientWeb()` — client_id, redirect
 * URIs e scopes abaixo precisam bater exatamente com o que está seedado lá.
 * PKCE é automático (`response_type: 'code'` + client público sem secret,
 * `requireProofKey(true)` no backend) — a lib gera/valida `code_verifier` sozinha,
 * nenhum código próprio de PKCE é necessário aqui.
 *
 * Armazenamento: localStorage (ver CLAUDE.md, seção "Decisões técnicas travadas",
 * para o trade-off de segurança explícito dessa escolha).
 *
 * `loadUserInfo: false` — não chamamos /userinfo, os dados de sessão vêm das
 * claims do próprio access token / do endpoint `GET /api/v1/auth/me`.
 *
 * `silent_redirect_uri`/renovação via `prompt=none` (não `refresh_token`): o
 * Spring Authorization Server não emite `refresh_token` pra client público
 * (`client_authentication_method=none`, correto pra SPA — ver CLAUDE.md, "Pendência 0").
 * `automaticSilentRenew` então usa o mecanismo padrão OIDC de iframe oculto
 * batendo em `/oauth2/authorize?...&prompt=none` (a lib seta `prompt=none`
 * sozinha em `signinSilent()`), reaproveitando o cookie de sessão do
 * Authorization Server (backend estendeu `server.servlet.session.timeout` pra
 * 12h de propósito, ver `seguranca-service/application.yml`) — sem precisar
 * mostrar login de novo enquanto essa sessão de servidor estiver viva. Ver
 * `SilentRenewPage.tsx` pro alvo desse iframe.
 */
export const authClient = new UserManager({
  authority: env.segurancaApiUrl,
  client_id: 'cactus-erp-web',
  redirect_uri: `${window.location.origin}/callback`,
  post_logout_redirect_uri: `${window.location.origin}/logout`,
  silent_redirect_uri: `${window.location.origin}/silent-renew`,
  response_type: 'code',
  scope: 'openid profile',
  loadUserInfo: false,
  automaticSilentRenew: true,
  userStore: new WebStorageStateStore({ store: window.localStorage }),
})
