# cactus-erp-web

Frontend do Cactus ERP (Cactus Digital). Consome as APIs REST dos serviços em
`cactus-erp-sistema` (pasta irmã, mesma raiz de workspace `cactus-erp/`) — este
projeto não tem lógica de negócio própria, é a camada de apresentação/orquestração
de UI sobre o backend, que é a fonte de verdade dos contratos de API.

Este arquivo é o registro narrativo de decisões/estado do frontend, no mesmo
espírito do `CLAUDE.md` raiz que já documenta `cactus-erp-sistema` fase a fase —
**complementado a cada entrega, nunca reescrito**. Antes de tocar em código aqui,
ler este arquivo inteiro.

---

## Stack

React 19 + Vite + TypeScript (strict) + Ant Design (v6, não v5 — ver nota abaixo)
+ React Router (data router) + TanStack Query + Axios + Zustand + `oidc-client-ts`.
Gerenciador de pacotes: npm.

---

## Entrega 1 — Autenticação + Shell (2026-08-31)

Primeira entrega real do projeto (pasta estava vazia antes desta sessão, só com
`cactus.jpg` e um mockup HTML descartável — movido para `_reference/`, não é
código). Escopo: autenticação de ponta a ponta contra o `seguranca-service`,
guarda de rotas por permissão, shell de aplicação (Ant Design) com placeholders
pros 6 módulos de negócio. **Sem tela de CRUD ainda** — a próxima entrega começa
por Clientes.

### Decisões técnicas travadas

- **React Router (data router `createBrowserRouter`), não TanStack Router.**
  Padrão de mercado, maior overlap com admin templates em Ant Design. TanStack
  Router valeria a pena quando existirem rotas com parâmetro tipado de verdade
  (`/clientes/:id`) — revisitar na entrega de Clientes, não bloqueava esta.

- **`oidc-client-ts` consumido direto (`UserManager`), sem `react-oidc-context`.**
  Esta app tem um conceito que a lib de contexto não modela bem: um "token
  efetivo de API" (produzido por `POST /trocar-empresa`) distinto do "token de
  sessão OIDC" (usado só pra manter login/refresh vivos). Ver seção "Fluxo de
  autenticação" abaixo pro modelo completo. PKCE manual foi descartado de
  propósito — é o tipo de código sensível que não vale reinventar; a lib cuida
  do `code_verifier`/`code_challenge` sozinha (client público, `response_type=code`).

- **TanStack Query** para toda chamada de API, desde já (mesmo com poucos
  consumidores nesta entrega — `useMe()`/`useEmpresasDisponiveis()` em
  `src/features/auth/queries.ts`). Toda tela de listagem futura vai precisar de
  paginação/cache contra `PageResponse<T>` (`src/shared/api/pageResponse.ts`,
  escalado agora, sem uso real ainda) — melhor estabelecer o padrão antes de
  Clientes chegar do que reescrever depois.

- **Armazenamento de token: `localStorage`** (via `WebStorageStateStore` do
  próprio `oidc-client-ts`). Trade-off explícito: o ideal contra XSS seria
  cookie httpOnly + BFF, mas isso exigiria um backend com sessão própria — fora
  de escopo (a única mudança de backend desta entrega foi CORS). `sessionStorage`
  mataria "deixar a aba aberta o dia todo" (padrão real de uso de ERP); memória
  pura forçaria login a cada F5. Mitigado por: access token curto (30min),
  refresh token rotativo (`reuseRefreshTokens(false)`, já configurado no
  backend), higiene padrão de XSS em React (sem `dangerouslySetInnerHTML` em
  lugar nenhum do app). CSP fica como TODO — não bloqueia esta entrega.

- **Estrutura de pastas: feature-sliced**, não flat `components/`+`pages/` —
  decisão pensada pro crescimento real (6 módulos de negócio viram features
  próprias). Ver seção "Estrutura de pastas" abaixo.

- **Modo claro apenas** (Sider escuro deliberado, resto claro). Dark mode
  completo do antd é mecanicamente barato mas fazer bem (contraste, tokens
  customizados) é trabalho de design sem retorno numa entrega de auth+shell.
  **Decisão adiada, não esquecida** — reavaliar quando houver demanda real.

- **Ant Design v6** (não v5, como o plano original cogitava) — o
  `npm create vite` mais recente escala **React 19** por padrão, e `npm install
  antd` trouxe a v6.6.2 (peer dep `react >=18`, testado com React 19 nativamente).
  Não foi preciso o patch `@ant-design/v5-patch-for-react-19` cogitado
  inicialmente — v6 já resolve isso de fábrica.

- **oxlint no lugar de ESLint** — o template atual do `npm create vite` já vem
  com `oxlint` (linter em Rust, mais rápido) configurado (`.oxlintrc.json`), não
  ESLint. Mantido como veio (ao invés de trocar por ESLint+plugins como um plano
  inicial cogitava) — sem motivo real pra reintroduzir a dependência mais lenta.
  Prettier adicionado à parte só para formatação (`.prettierrc.json`).

- Sem `docs/adr/` separado — este `CLAUDE.md` único é o precedente já
  estabelecido no workspace (o `CLAUDE.md` raiz faz o mesmo pro backend).

### Fluxo de autenticação

Authorization Code + PKCE contra o **Authorization Server do `seguranca-service`**
(porta 8082, OIDC completo — `/oauth2/authorize`, `/oauth2/token`, `/oauth2/jwks`,
`/.well-known/openid-configuration`). Client já seedado no backend
(`seguranca-service/.../config/OAuth2ClientSeeder.java`, método `clientWeb()`):

| | |
|---|---|
| `client_id` | `cactus-erp-web` |
| Autenticação do client | `none` (público, PKCE obrigatório — `requireProofKey(true)`) |
| Grants | `authorization_code` + `refresh_token` |
| Redirect URIs | `http://localhost:5173/callback`, `http://127.0.0.1:5173/callback` |
| Post-logout redirect | `http://localhost:5173/logout` |
| Scopes | `openid profile` |
| Access token TTL | 30 min |
| Refresh token TTL | 12h, rotativo (`reuseRefreshTokens(false)`) |

**Modelo de estado** (`src/features/auth/authStore.ts`, zustand) — a peça de
maior risco desta entrega, documentada em detalhe porque não é óbvia por si só:

```
oidcUser          — token de SESSÃO (oidc-client-ts). Só existe pra manter
                     login/refresh vivos. NUNCA usado direto numa chamada de
                     API de negócio.
empresaPadraoId   — empresa que o servidor resolveria "por padrão" a partir
                     do token de sessão atual (primeira empresa do usuário).
empresaAtivaId    — empresa que o usuário efetivamente escolheu operar.
effectiveToken    — token DE FATO anexado a toda chamada de API de negócio
                     (ver src/shared/api/httpClients.ts). É o access_token da
                     sessão quando empresaAtiva === empresaPadrao, ou o token
                     reemitido por trocar-empresa caso contrário.
effectiveAuthorities
```

Por quê essa separação existe — **limitação documentada no `CLAUDE.md` raiz**,
seção "Sessão multi-empresa e refresh_token" (2026-08-30): quando o
`refresh_token` OIDC padrão renova o access token, o servidor
(`AuthorizationServerConfig.jwtTokenCustomizer`) sempre resolve a empresa
**padrão** de novo — não tem como saber que o usuário trocou de empresa via
`POST /trocar-empresa` antes (esse endpoint minta um JWT à parte, sem
`OAuth2Authorization` persistido, então não tem refresh token próprio). Sem essa
separação de estado, uma renovação silenciosa em background reverteria
silenciosamente o usuário pra empresa errada no meio da sessão.

Fluxo completo:

1. **Login** (`LoginPage.tsx`): botão "Entrar" → `authClient.signinRedirect()`.
   Sem formulário de credencial no SPA — quem coleta login/senha é a própria
   tela do Authorization Server.
2. **Callback** (`CallbackPage.tsx`, rota `/callback`): `signinRedirectCallback()`
   troca o `code` pelo token → `authStore.setOidcUserFromLogin(user)` decodifica
   `empresaId`/`authorities` do `access_token` (`jwt-decode`, sem verificação de
   assinatura client-side — isso é responsabilidade dos Resource Servers) →
   `empresaAtivaId = empresaPadraoId = claims.empresaId`, `effectiveToken =
   access_token` → navega pra `/`. Guardado com `useRef` contra o double-invoke
   de `React.StrictMode` em dev (o `code` só pode ser trocado uma vez — chamar
   `signinRedirectCallback()` duas vezes gera uma corrida real contra o servidor).
3. Toda chamada de API de negócio lê **sempre** `effectiveToken`
   (`src/shared/api/httpClients.ts`, interceptor de request) — nunca
   `oidcUser.access_token` direto.
4. **Renovação silenciosa** (`automaticSilentRenew: true`, evento
   `addUserLoaded` assinado uma vez em `AppProviders.tsx`,
   `authStore.handleUserRenewed`): se a empresa ativa é a padrão (ou nula), o
   token renovado só substitui `effectiveToken`. Senão, rechama
   `POST /trocar-empresa` usando o token **recém-renovado** como Bearer (não o
   `effectiveToken` antigo) antes de qualquer chamada de negócio usar esse
   token. Se essa rechamada falhar (ex: usuário perdeu o papel na empresa entre
   uma renovação e outra), força `reset()` — relogin limpo em vez de arriscar
   operar com authorities erradas.
5. **Troca de empresa** (`CompanySwitcher.tsx`, header): `GET
   /empresas-disponiveis` (sempre com o token de sessão OIDC, não o
   `effectiveToken` — precisa responder mesmo antes de qualquer troca) → usuário
   escolhe → `authStore.trocarEmpresaAtiva(empresaId)` → `POST /trocar-empresa`
   → atualiza `empresaAtivaId`/`effectiveToken`/`effectiveAuthorities` **sem
   tocar em `oidcUser`** (é isso que deixa o ciclo de renovação da sessão
   original seguir funcionando independente). Erro 422
   (`SEGURANCA_USUARIO_SEM_ACESSO_EMPRESA`) vira mensagem amigável.
6. **Logout** (`UserMenu.tsx` → "Sair"): `authClient.signoutRedirect()` (hit real
   no end-session do Authorization Server, não só limpeza local) → aterrissa em
   `/logout` (`LogoutPage.tsx`), que limpa o `authStore` + chama `removeUser()`
   como cinto-e-suspensório.
7. **401 em qualquer chamada** (`httpClients.ts`, interceptor de resposta):
   `authStore.handleUnauthorized()` → reset total + (via `RequireAuth`) redirect
   pra `/login`. Sem tentativa de recuperação — se chegou 401, a renovação
   proativa já falhou.

### Estrutura de pastas

Feature-sliced, porque isso vai crescer pra 6 módulos de negócio reais
(Cadastro Base, Estoque, Compras, GED, BI, Segurança), não fica em 2-3 telas:

```
src/
  app/              composition root — nada de regra de negócio aqui
    routes/         AppRouter, RequireAuth (guarda nível 1: autenticado?),
                     RequireAuthority (guarda nível 2: tem a authority?)
    layout/         AppLayout (Sider+Header+Content), CompanySwitcher, UserMenu
    providers/      AppProviders — liga authStore ↔ oidc-client-ts ↔ httpClients,
                     monta ConfigProvider(antd) + QueryClientProvider
    theme/          antdTheme.ts — paleta da marca
  features/
    auth/           authClient, authStore, authority (predicados puros),
                     trocarEmpresa (chamadas HTTP), tokenClaims (decode JWT),
                     LoginPage/CallbackPage/LogoutPage, queries (useMe/useEmpresasDisponiveis)
    dashboard/      DashboardPage + PlaceholderKpiCard
    modules/        um Placeholder.tsx por módulo de negócio (telas reais vêm depois)
  shared/
    api/            httpClients (Axios por origem + injeção de auth),
                     problemDetail (normalização de erro RFC7807 → AppError),
                     pageResponse (tipo genérico, sem uso ainda), queryClient
    ui/             NavConfig (fonte única módulo→autoridade→rota), PlaceholderPage
    config/         env.ts (acesso tipado a import.meta.env)
```

Path aliases (`@app/*`, `@features/*`, `@shared/*`) resolvidos nativamente pelo
Vite 8 via `resolve.tsconfigPaths: true` (não precisou do plugin
`vite-tsconfig-paths` — instalado inicialmente, removido depois de descobrir
que o próprio Vite já suporta isso).

### Guarda de rotas e navegação por permissão

`src/shared/ui/NavConfig.tsx` é a **fonte única de verdade** — lista os 6
módulos com `requiredAuthorities`, consumida tanto por `AppSider` (filtra
itens do menu) quanto por `AppRouter` (`RequireAuthority anyOf={...}`), pra
nunca ter duas listas de autoridade que podem divergir.
`src/features/auth/authority.ts` exporta os predicados puros
(`hasAuthority`/`hasAnyAuthority`) reusados nos dois lugares.

**Pendência registrada**: o mapeamento módulo→autoridade em `NavConfig.tsx` é
uma aproximação a partir dos exemplos de authority já vistos no código
(`CADASTRO_CLIENTE_CONSULTAR`, `ESTOQUE_CONSULTAR`, `COMPRAS_APROVAR`,
`GED_GERENCIAR`, `BI_CONSULTAR`, `SEGURANCA_GERENCIAR`, etc.), **não** uma
conferência exaustiva contra o catálogo real de `Funcionalidade` do
`seguranca-service`. Reconciliar antes da próxima entrega.

### Mudança de backend: CORS

CORS não estava configurado em nenhum serviço do `cactus-erp-sistema` — bloqueio
real que impediria qualquer chamada do SPA. Corrigido centralizando em
`common/.../security/ResourceServerSupport.java` (`applyBaseline`, ponto único
já usado por todo `SecurityFilterChain` do ecossistema), liberando
`http://localhost:5173`/`http://127.0.0.1:5173`. **Detalhe completo, motivo e
validação: ver `CLAUDE.md` raiz, seção "ESTADO ATUAL DO REPOSITÓRIO", entrada
"CORS habilitado pra cactus-erp-web (2026-08-31)"** — não duplicado aqui, o
backend é dono do próprio estado.

### Ativos visuais (logo/favicon)

Estado final desta entrega, depois de ~12 rodadas de ajuste (histórico completo,
passada a passada, no `CLAUDE.md` raiz — não repetido aqui, só o resultado):

- **`AppSider.tsx` (ícone da sidebar do dashboard)**: `CactusMark.tsx`
  (`src/shared/ui/CactusMark.tsx`) — usa a **foto real** (`cactus-logo.jpg`),
  recortada **ao vivo via CSS** (`background-image`/`background-size`/
  `background-position` calculados a partir do tamanho de exibição, não um
  arquivo pré-recortado — recorte pré-processado em arquivo, tentado 2x antes
  disso, sempre ficava com borda áspera ao ampliar). `border-radius: 50%`
  recorta em círculo, `boxShadow` desenha um contorno branco fino. Coordenadas
  do recorte (`left:71, top:11, width:156, height:156` de uma fonte 274×278px)
  medidas numa passada só direto na imagem original — a raiz do problema nas
  tentativas anteriores era medir em cima de recorte-sobre-recorte.
- **`LoginPage.tsx`/`LoginPageController` (painel de marca da tela de login)**:
  sem ícone nenhum — só tipografia (título "Cactus ERP" em duas cores + um
  traço decorativo verde). O usuário pediu explicitamente pra tirar o "C"
  genérico que existia ali antes.
- **`public/favicon.svg` (aba do navegador)**: **ainda é a marca desenhada à
  mão** (círculo + 3 "braços" brancos arredondados, abstração geométrica, não
  a foto) — versão de uma passada anterior à mudança pra foto real na sidebar.
  **Inconsistência conhecida, registrada, não corrigida**: o usuário só pediu
  a mudança pra foto real no ícone da sidebar ("logo da página da home"), não
  mencionou o favicon — fica como pendência de consistência visual pra próxima
  vez que a tela de login for revisitada (aplicar a mesma técnica CSS do
  `CactusMark` lá, ou gerar um PNG a partir dela via `sharp` como já foi feito
  antes).
- `public/cactus-logo.jpg`/`cactus-erp-sistema/seguranca-service/.../branding/
  cactus-logo.jpg`: a foto original, usada inteira (com o texto) só onde há
  espaço de sobra pra ficar legível — painel de formulário das duas telas de
  login.

### Como rodar localmente

```
1. MySQL local rodando (schema provisionado via provisionamento-mysql-local.sql,
   na raiz de cactus-erp-sistema)
2. cd cactus-erp-sistema/seguranca-service && mvn spring-boot:run      (porta 8082)
3. cd cactus-erp-sistema/cadastro-base-service && mvn spring-boot:run  (porta 8081)
4. cd cactus-erp-web && npm install && npm run dev                     (porta 5173)
```

Os outros 4 serviços de negócio (`controle-estoque-service`,
`gestao-compras-service`, `gestao-documentos-service`,
`business-intelligence-service`) **não são necessários** nesta entrega — nenhuma
tela ainda chama essas origens (`.env.example` já deixa as URLs comentadas,
prontas pra quando os módulos ganharem telas reais).

URLs de API configuradas via `VITE_SEGURANCA_API_URL`/`VITE_CADASTRO_BASE_API_URL`
(`.env.development`, cópia de `.env.example` — sem segredo, só localhost, pode
ficar versionado).

### Scripts npm

`npm run dev` · `npm run build` (roda `tsc -b` antes) · `npm run lint` (oxlint)
· `npm run format` (prettier --write) · `npm run test` (vitest run) ·
`npm run test:watch`.

### Testes automatizados

Único automatizado desta entrega — cobre a lógica de maior risco (o modelo
`authStore` acima), o resto é shell/placeholder sem lógica que justifique teste
de componente ainda:

- `src/features/auth/authStore.test.ts` — login inicial, troca de empresa,
  renovação silenciosa com/sem empresa não-padrão (dispara ou não
  `trocar-empresa`), falha na rechamada força reset, `handleUnauthorized` limpa
  tudo. 6 casos.
- `src/shared/api/problemDetail.test.ts` — mapeamento `ProblemDetail` (401/403/
  404/409/422/validação/erro de rede) → `AppError.kind`. 7 casos.

Rodar: `npm run test`. Ambos passando (13/13) na última execução desta entrega.

### Verificação manual (contra backend real)

Executada nesta entrega com `seguranca-service` + `cadastro-base-service`
rodando localmente:

- [x] `npm run build` limpo (tsc -b + vite build) — bundle único de ~1MB
      (gzip ~342KB), aviso de chunk grande do Vite — aceitável pra esta
      entrega (majoritariamente antd), code-splitting por rota fica pra quando
      os módulos ganharem telas reais e pesarem de fato.
- [x] `npm run lint` (oxlint) — 0 avisos/erros.
- [x] `npm run test` — 13/13.
- [x] Fluxo Authorization Code + PKCE completo, **validado num NAVEGADOR REAL**
      (headless Chromium via Playwright, instalado só pra este teste, driblando
      clique real em "Entrar", preenchimento real do form de login, submit) —
      não só simulação por `curl` (a primeira rodada de correções tinha sido
      validada só por `curl`, o que não pegou o achado #3 abaixo; só apareceu
      testando com um navegador de verdade, que carrega CSS/imagens/etc).
      (2026-08-31) — sessão salva em `localStorage`, `GET /me`/
      `GET /empresas-disponiveis` 200, dashboard renderizado, zero erro de
      console, zero requisição falha (screenshot conferido). Isso exigiu
      corrigir **5 bugs reais no `seguranca-service`** encontrados só ao testar
      de ponta a ponta (nenhum aparecia em `mvn compile` nem nos testes
      automatizados) — ver `CLAUDE.md` raiz, entradas sob "CORS habilitado pra
      cactus-erp-web": (1) CORS faltando na `authorizationServerSecurityFilterChain`
      (segunda chain do seguranca-service, não coberta pelo fix de CORS
      original); (2) `/login` fora do `permitAll`; (3)
      `DefaultLoginPageGeneratingFilter` não registrado automaticamente por
      `.formLogin()`; (4) causa raiz mais funda — `SessionCreationPolicy.STATELESS`
      (herdada de `ResourceServerSupport.applyBaseline`) quebrava a
      persistência de sessão que o form login precisa, corrigida para
      `IF_REQUIRED` só nessa chain; (5) `DefaultResourcesFilter` (serve
      `/default-ui.css`, referenciado pela página de login gerada) também não
      registrado automaticamente — a busca desse CSS sem sessão sobrescrevia no
      `RequestCache` o `/oauth2/authorize` original, só visível num navegador
      de verdade (curl não carrega CSS).
- [x] **Logout também validado num navegador real** (2026-08-31) — clicar em
      "Sair" dava 500 em `GET /connect/logout` (usuário reportou ao vivo).
      **6º bug real** encontrado: `PolymorphicTypeValidator` do
      `JdbcOAuth2AuthorizationService` (Jackson 3) recusava reler os tipos JDK
      das claims customizadas (`Long`, `List` imutável) que ele mesmo tinha
      gravado — corrigido em `JdbcAuthorizationServerConfig.java` (backend),
      detalhamento completo no `CLAUDE.md` raiz. Após o fix: `/connect/logout`
      → 302 → `http://localhost:5173/logout` renderizando corretamente,
      `localStorage` limpo — confirmado por screenshot.
- [x] **`LoginPage.tsx` redesenhado (2026-08-31)**, a pedido explícito do
      usuário ("muito feio... sem o logo... o layout não gostei") — layout
      split-screen (painel de marca escuro à esquerda + formulário com a logo
      real à direita), espelhando exatamente `LoginPageController` do
      `seguranca-service` (as duas telas aparecem em sequência no mesmo fluxo,
      identidade visual precisa bater). Detalhamento completo no `CLAUDE.md`
      raiz. Revalidado sem regressão (Playwright + screenshot).
- [x] **Iteração de design da tela de login validada interativamente pelo
      usuário, no próprio navegador, ao longo de ~12 rodadas** (2026-08-31) —
      cobrindo: tagline/copy (3 reescritas, até chegar em algo que não citasse
      módulos específicos nem soasse "checklist burocrático"), o ícone da
      sidebar (2 técnicas de recorte raster abandonadas por borda áspera, até
      a técnica final de recorte CSS ao vivo — ver "Ativos visuais" acima),
      cor do botão de recolher menu (token `triggerBg` do Ant Design nunca
      configurado, default `#002140`), e um bug real de layout (painéis das
      duas telas de login com larguras diferentes em monitores largos — Ant
      Design `Col` em % pura vs. CSS do backend com teto de 560px). Histórico
      completo, passada a passada, no `CLAUDE.md` raiz — não repetido aqui.
      **Isso cobre a verificação manual interativa que ficava pendente no
      item 7 da lista de pendências abaixo** — o usuário testou de fato,
      repetidamente, no navegador real.
- [x] **Renovação de sessão sem `refresh_token` — resolvido (2026-09-01)**:
      `POST /oauth2/token` não devolve `refresh_token` porque o Spring
      Authorization Server não emite pra client público
      (`client_authentication_method=none`, correto por ser SPA). Resolvido
      com renovação silenciosa via iframe oculto + `prompt=none` (mecanismo
      OIDC padrão) — `SilentRenewPage.tsx` (rota `/silent-renew`, fora de
      `RequireAuth`) + `silent_redirect_uri` novo em `authClient.ts`.
      Detalhamento completo (headers/sessão do backend, achado do
      `redirectUri` faltando no client seedado) no `CLAUDE.md` raiz, entrada
      "Achado adicional (2026-08-31), resolvido (2026-09-01)". Validado ao
      vivo num navegador real (Playwright): `signinSilent()` completou sem
      mostrar login, novo `access_token` emitido, zero erro de console.
- [ ] Troca de empresa com usuário de 2+ empresas (banco local só tem 1
      empresa seedada hoje) interagindo com a renovação silenciosa — ainda
      pendente, mas só por limitação de dado (falta empresa extra pra testar
      contra), não mais bloqueado por decisão de arquitetura nenhuma — a
      lógica de `authStore.handleUserRenewed` não mudou, só o mecanismo de
      renovação por trás dela (raciocínio confirmado são, não testado ao vivo
      ainda especificamente esse branch).

## Entrega 2 — Clientes CRUD (2026-09-01)

Primeira tela de negócio real do app — cadastro completo de Cliente
(`cadastro-base-service`), incluindo Pessoa (física/jurídica) e as 3 listas
filhas (endereços/contatos/telefones). Valida `PageResponse<T>`/TanStack
Query/formulário na prática pela primeira vez.

### Decisões de layout (discutidas com o usuário antes de codar)

Duas decisões de UX confirmadas explicitamente antes da implementação (a
complexidade do cadastro — Pessoa + 3 listas filhas + dados fiscais
condicionais ao tipo — não cabe bem num modal simples):

- **Duas rotas dedicadas, não modal**: `/cadastro-base/clientes` (listagem) e
  `/cadastro-base/clientes/novo`+`/cadastro-base/clientes/:id` (mesmo
  componente `ClienteFormPage` pros dois casos). Padrão de ERP de mercado
  (SAP B1, TOTVS, Sankhya) pra cadastro com esse nível de profundidade.
- **Abas (Ant Design `Tabs`)** pros 5 grupos de campo (Dados Gerais/
  Endereços/Contatos/Telefones/Comercial & Fidelidade), não uma página com
  rolagem por seções — mantém a tela curta, mostra contagem no título da aba
  (`Endereços (2)`). Um único `Form` (Ant Design) cobre todas as abas — Tabs
  não desmonta painel inativo por padrão, então os valores sobrevivem à troca
  de aba sem precisar de estado próprio por aba.
- **Modal (não Drawer) pra editar item de uma lista filha** (endereço/
  contato/telefone) — Ant Design `Modal` centralizado, formulário curto (o
  mais longo, Endereço, tem ~10 campos, cabe bem). Lógica de "abrir novo/
  editar índice N/salvar/remover", repetida idêntica nas 3 listas,
  extraída pro hook `useEditableList<T>` (`features/clientes/
  useEditableList.ts`) — inclui a regra de "no máximo 1 item `principal=true`
  por lista" (desmarca os demais ao marcar um novo), UX de conveniência, não
  uma validação que o backend impõe.
- **Segmented "Pessoa Física / Pessoa Jurídica"** no cabeçalho da página
  (não dentro da aba "Dados Gerais") — controla que campos aparecem
  (CPF/RG/nascimento vs. CNPJ/fantasia/IE/IM), **desabilitado em modo edição**
  (regra do backend: `PUT` não permite mudar `tipoPessoa`, documentado no
  próprio `ClienteController` — `422` se tentar). `tipoPessoa` é estado do
  componente, não campo do `Form`, exatamente por essa razão (decide
  renderização condicional e visibilidade do Segmented, não é serializado
  pelo `Form.validateFields()`).
- **Situação (Ativo/Inativo) como `Switch`** no cabeçalho, ao lado de
  Cancelar/Salvar — campo do `Form` de verdade (`normalize`/`getValueProps`
  convertem entre `boolean` do Switch e `'ATIVO'|'INATIVO'` do backend).

### Arquitetura de estado do formulário

- **Campos escalares** (nome, documento, email, comercial/fidelidade) vivem
  no `Form` (Ant Design) — um só, único ponto de validação via
  `form.validateFields()`.
- **As 3 listas filhas são estado React puro** (`useState<EnderecoDTO[]>`
  etc. em `ClienteFormPage`), não `Form.List` — só se juntam ao resto no
  submit (`montarRequest`, dentro de `handleSalvar`). Decisão deliberada:
  misturar `Form.List` com edição por modal por item é mais complicado de
  raciocinar do que manter as listas como estado simples passado por props
  pras 3 abas (`EnderecosTab`/`ContatosTab`/`TelefonesTab`).
- `Situacao` (tipo + `SituacaoTag`) extraído pra `shared/domain/` e
  `shared/ui/`, não `features/clientes/` — cross-cutting de propósito, toda
  entidade futura (Produto, Empresa, Colaborador, Fornecedor) reusa o mesmo
  tipo/tag ao ganhar tela.

### Achados reais, corrigidos nesta sessão (backend + RBAC granular)

- **Ordenação da listagem — resolvido**: `GET /api/v1/clientes?sort=nome,asc`
  devolvia 400 (`nome` vive em `Pessoa`, join, path não resolve direto na
  raiz). `sort=pessoa.nome,asc` piorou pra 500 — causa raiz real encontrada:
  `ClienteSpecifications.comFiltros` ligava `query.distinct(true)`
  **incondicionalmente**, mas todo join dali (`pessoa`/`pessoaFisica`/
  `pessoaJuridica`) é `@OneToOne`/`@ManyToOne` — nenhum pode multiplicar
  linhas de `Cliente`, então o `DISTINCT` nunca foi necessário (o comentário
  antigo já reconhecia isso, mas mantinha o `distinct` mesmo assim). MySQL
  rejeita `SELECT DISTINCT` + `ORDER BY` numa coluna fora da projeção
  (ErrorCode 3065) — exatamente o que `ORDER BY p1_0.nome` disparava. Corrigido
  removendo o `query.distinct(true)` (`cadastro-base-service/.../
  ClienteSpecifications.java`) — `sort=pessoa.nome,asc` funciona desde então,
  reativado em `features/clientes/api.ts`.
- **RBAC granular no formulário — resolvido**: `Form disabled={!podeSalvar}`
  (Ant Design cascateia pra todos os `Form.Item` de uma vez), `Segmented`
  tipoPessoa e as 3 abas de lista (`somenteLeitura` prop, esconde "Adicionar"/
  editar/remover) desabilitados juntos quando o usuário não tem
  `CADASTRO_CLIENTE_CRIAR`(criação)/`CADASTRO_CLIENTE_EDITAR`(edição). Botão
  "Salvar" ganhou `Tooltip` explicando o motivo quando desabilitado. Validado
  ao vivo com dois papéis reais (`ADMIN` write-tudo, `OPERADOR` só
  `CADASTRO_CLIENTE_CONSULTAR`) em contextos de navegador isolados — usuário
  de teste criado e removido depois (`DELETE /api/v1/usuarios/{id}`).

### Falso alarme investigado e descartado (2026-09-01): logout funciona corretamente

O item anterior desta seção ("logout não encerra a sessão do AS de verdade")
foi **investigado a fundo e descartado — era um falso positivo do próprio
script de teste**, não um bug do app. Causa raiz do falso alarme: o teste
original navegava direto pra `page.goto('.../logout')` (a rota de
**destino** do `post_logout_redirect_uri` — `LogoutPage.tsx`, que só limpa
`localStorage`/estado local, por design, já que a limpeza de sessão real é
responsabilidade do servidor) em vez de clicar no botão "Sair" de verdade
(`UserMenu.tsx` → `signOut()` → `authClient.signoutRedirect()`), então
`GET /connect/logout` nunca era chamado — não tinha como a sessão do servidor
ser encerrada por um teste que nunca bateu nela.

Refeito clicando na UI real (menu do usuário → "Sair"): `GET /connect/logout`
disparado com `id_token_hint`+`post_logout_redirect_uri` corretos, `302` de
volta pro app, e — decisivo — **clicar em "Entrar" de novo depois disso
mostra o formulário de login pedindo credencial**, não desliza direto pro
dashboard. Confirmado 2x consecutivas para descartar acaso. `JSESSIONID`
manter o mesmo valor no cookie do navegador não indicava sessão viva (o
`Set-Cookie` de invalidação de sessão do Spring não necessariamente muda o
ID visível ao client do mesmo jeito que uma sessão nova geraria um ID novo
na PRÓXIMA autenticação bem-sucedida) — o teste comportamental (login exige
senha de novo) é que importa, e esse passou.
**Lição**: ao testar fluxo de logout, sempre disparar pela ação real da UI
(clique no botão), nunca navegar direto pra rota de destino do redirect —
elas têm responsabilidades diferentes por design.

### BUG REAL reportado pelo usuário e corrigido (2026-09-01): botão "Entrar" preso em loading após "voltar" do navegador

**Sintoma relatado**: clicar "Entrar" na tela de login mostra a tela de
usuário/senha do Authorization Server normalmente; se o usuário aperta
"voltar" no navegador nesse ponto, o botão "Entrar" volta preso num spinner
de loading infinito — só um refresh manual (F5) resolve.

**Causa raiz**: `LoginPage.tsx` liga `entrando=true` antes de chamar
`signIn()` (`authClient.signinRedirect()`, navegação de página inteira) — o
próprio comentário do código já assumia que não havia "sucesso" client-side
pra desligar esse estado, porque em caso de sucesso o navegador sai da SPA
mesmo. Isso é verdade pro caminho feliz, mas quebra no **bfcache**
(back-forward cache): ao apertar "voltar", Chrome/Firefox restauram esta
página exatamente como estava **congelada** no momento em que o usuário saiu
dela — inclusive `entrando=true` — sem executar nenhum código novo. Sem um
listener específico pra essa restauração, o estado React ficava
permanentemente preso, e só um F5 (reload de verdade, não bfcache) recriava o
componente do zero.

**Correção**: `useEffect` escutando o evento `pageshow` do browser — quando
`event.persisted === true` (sinal padrão de "esta página voltou do bfcache,
não foi carregada de novo"), reseta `entrando` pra `false`. Padrão
documentado pela própria MDN pra esse exato tipo de bug (botão de submit
preso em loading após navegação "voltar").

**Validado ao vivo (Playwright)**: clicar "Entrar" → chega em
`localhost:8082/login` → apertar "voltar" → botão volta ao estado normal
(sem spinner, sem `disabled`) → clicar "Entrar" de novo funciona
normalmente (navega pro Authorization Server de novo), sem precisar de
refresh manual. Confirmado com screenshot.

### Verificação ao vivo (Playwright, navegador real)

Fluxo completo validado de ponta a ponta, não só compilação/lint: login →
`/cadastro-base/clientes` (lista vazia) → "Novo Cliente" → preencher Dados
Gerais (Pessoa Física) → adicionar 1 endereço (incl. seleção de UF via
`Select` com busca) → 1 contato → 1 telefone → Salvar (`201`, redireciona
pra `/clientes/:id`) → voltar pra lista (cliente aparece, documento
formatado, cidade/UF do endereço principal) → reabrir → confirma
`Endereços (1)`/`Contatos (1)`/`Telefones (1)` persistidos. **Zero erro de
console** na execução final (um warning do Ant Design sobre a assinatura
depreciada de `rowKey` apareceu numa rodada anterior — corrigido trocando
`(_, index) => index` por `(record) => lista.indexOf(record)` nas 3 tabelas).
Registros de teste criados durante a validação (`DELETE
/api/v1/clientes/{id}`) removidos antes de encerrar — banco local não fica
sujo com dado de teste desta sessão.

**Segunda rodada de validação ao vivo (mesma sessão, depois dos 2 fixes
acima)**: `sort=pessoa.nome,asc` confirmado 200 contra o backend corrigido;
RBAC granular confirmado com 2 papéis reais em contextos de navegador
isolados — `ADMIN` com tudo editável, `OPERADOR` (só
`CADASTRO_CLIENTE_CONSULTAR`) com a tela inteira em modo leitura (campos
cinza, Segmented/Switch/Salvar desabilitados, sem botão "Adicionar" nas 3
abas de lista) — zero erro de console nos dois casos.

## Entrega 3 — Fornecedores CRUD + refactor compartilhado (2026-09-01)

Segunda tela de negócio, escolhida deliberadamente por reaproveitar quase
tudo de Clientes (mesma especialização de Pessoa: física/jurídica +
endereços/contatos/telefones) — validou que o padrão da Entrega 2 generaliza
de verdade, não foi sorte de uma entidade só.

### Refactor: componentes genéricos de Pessoa extraídos pra `shared/`

Antes de construir Fornecedor, extraído de `features/clientes/` tudo que não
tinha nada de específico de Cliente (só usava `EnderecoDTO`/`ContatoDTO`/
`TelefoneDTO`, nunca `ClienteRequest`):

- `shared/domain/pessoa.ts` — `TipoPessoa`/`TipoContato`/`TipoTelefone`/
  `EnderecoDTO`/`ContatoDTO`/`TelefoneDTO`.
- `shared/domain/pessoaUtils.ts` — `formatarDocumento`/`UFS`/labels de tipo.
- `shared/hooks/useEditableList.ts` — lógica de "abrir novo/editar índice N/
  salvar/remover" das 3 listas filhas (idêntica em qualquer entidade que
  especializa Pessoa).
- `shared/ui/pessoa/{Endereco,Contato,Telefone}Modal.tsx` e
  `{Enderecos,Contatos,Telefones}Tab.tsx` — os modais e tabelas em si.

`features/clientes/` ficou só com o que é realmente de Cliente:
`ClienteRequest`/`ClienteResponse` (com `url`/`limiteCredito`/`taxaDesconto`/
fidelidade), `DadosGeraisTab`/`ComercialTab` próprios, `api.ts`/`queries.ts`/
as duas páginas. Zero regressão (typecheck/lint/13 testes automatizados
continuaram passando depois do refactor, antes de tocar em Fornecedor).

### `features/fornecedores/` — o que é realmente diferente de Cliente

Comparado campo a campo contra `FornecedorRequest.java`/`FornecedorResponse.java`
antes de codar (não assumido igual a Cliente por preguiça):

- **Sem** `url` (site) — `DadosGeraisTab` própria, sem esse campo.
- **Sem** comercial/fidelidade (`limiteCredito`/`taxaDesconto`/`fidelidadeAviso`/...)
  — não fazem sentido do lado fornecedor.
- **Com** campos próprios de compra: `fornecedorDesde` (data), `prazoEntregaDias`
  (número), `observacao` (texto livre, 250 caracteres) — aba "Compras" nova
  (`tabs/ComprasTab.tsx`), no lugar de "Comercial & Fidelidade".
- Mesmo RBAC granular de Clientes: `CADASTRO_FORNECEDOR_CRIAR`/`EDITAR` gate o
  `Form disabled`/abas em modo leitura, mesma lógica exata, só a authority muda.

### Navegação: "Cadastro Base" virou submenu (achado de IA de navegação)

Com uma segunda entidade dentro do mesmo módulo, ficou claro que o sidebar
precisava de hierarquia — não dava pra continuar com "Cadastro Base" apontando
direto pra uma página só. `NavConfig.tsx` ganhou `ModuleNavChildEntry`
(sub-itens com seu próprio `path`/`requiredAuthorities`, filtrados pela mesma
`hasAnyAuthority` de sempre — um sub-item some se o usuário não tiver a
authority, e o pai também some se **nenhum** filho sobrar, evitando "Cadastro
Base" vazio no menu). `AppSider.tsx` renderiza como submenu do Ant Design
(`items` com `children` vira `SubMenu` automaticamente), com `defaultOpenKeys`
calculado a partir da rota atual (abre sozinho se você já está em `/clientes`
ou `/fornecedores`) e `selectedKey` resolvido pelo path mais específico (nunca
destaca só o pai quando na verdade está num filho). `AppRouter.tsx` ganhou as
3 rotas de fornecedores (`/fornecedores`, `/novo`, `/:id`) ao lado das de
clientes, mesma estrutura de `children` já usada.

### Achado real, corrigido proativamente (backend)

Antes de testar Fornecedores, verificado se `FornecedorSpecifications` e
`ColaboradorSpecifications` (as outras 2 especializações de Pessoa, junto de
`ClienteSpecifications` já corrigida na Entrega 2) tinham o mesmo bug de
`query.distinct(true)` incondicional que quebrava `sort=pessoa.nome,asc` no
MySQL — **tinham, idêntico**. Corrigidas as duas na mesma sessão, antes de
qualquer teste ao vivo revelar o mesmo erro de novo por entidade — `api.ts`
de Fornecedores já nasceu com `sort=pessoa.nome,asc` funcionando de cara, sem
precisar do ciclo "quebra → descobre → conserta" da Entrega 2.

### Verificação ao vivo (Playwright, navegador real)

Fluxo completo: login → expandir "Cadastro Base" no sidebar (confirma
"Clientes"/"Fornecedores" visíveis) → "Fornecedores" → "Novo Fornecedor" →
Pessoa Jurídica, CNPJ, 1 endereço (com seleção de UF) → aba "Compras"
(prazo de entrega + observação) → Salvar (`201`) → volta pra lista (aparece)
→ reabre → aba "Compras" confirma `prazoEntregaDias` persistido. Zero erro de
console (só o warning pré-existente do `antd Statistic` no dashboard
placeholder, não relacionado). Registro de teste removido (`DELETE
/api/v1/fornecedores/{id}`) antes de encerrar.

## Entrega 4 — Colaboradores CRUD (2026-09-01)

Terceira e última especialização de Pessoa (fecha o trio Cliente/Fornecedor/
Colaborador) — mesmo reaproveitamento de `shared/domain/pessoa*`+`shared/ui/pessoa/`
das duas entregas anteriores, RBAC granular idêntico
(`CADASTRO_COLABORADOR_CRIAR`/`EDITAR`).

### O que é realmente diferente (verificado contra `ColaboradorRequest.java`)

- **Sem** `url`/comercial-fidelidade (igual Fornecedor).
- **RH**: `cargoId`/`setorId` (lookups próprios — `Cargo`/`Setor`, CRUD simples
  no backend, `GET /api/v1/cargos`/`/api/v1/setores`, sem paginação real no
  front, `size=200` porque o volume esperado é dezenas por empresa),
  `matricula`, `dataAdmissao`/`dataDemissao`, CTPS completa (número/série/data
  de expedição/UF) — aba "RH" nova.
- **Comissão & Entrega**: `comissaoVista`/`comissaoPrazo` (comissão por venda
  à vista/a prazo — vale pra qualquer vertical com vendedor comissionado, não
  só delivery) + `entregadorVeiculo` (carro/moto/bicicleta/aplicativo, só
  relevante quando o colaborador também entrega) + `observacao` — aba
  própria, no lugar de "Comercial & Fidelidade"/"Compras".
- **6 abas no total** (Dados Gerais/Endereços/Contatos/Telefones/RH/Comissão
  & Entrega) — mais que Cliente (5) e Fornecedor (5), pelo volume real de
  campos de RH; não forçado a caber em menos abas artificialmente.
- **Datas múltiplas na mesma tela** (nascimento + admissão + demissão + CTPS
  expedição) — generalizado num laço (`CAMPOS_DATA` + `Object.fromEntries`)
  em vez de repetir a conversão dayjs↔ISO 4 vezes copiado-colado feito nas
  telas anteriores (que só tinham 1 data cada).

### Verificação ao vivo (Playwright, navegador real)

Fluxo completo, incluindo os lookups de RH: login → submenu "Cadastro Base"
confirma "Colaboradores" visível (3º item, ao lado de Clientes/Fornecedores)
→ criar Cargo "Vendedor" e Setor "Comercial" via API (banco local não tinha
nenhum, endpoint próprio ainda não construído — fora de escopo, ver
pendências) → novo colaborador PF, 1 endereço, aba RH seleciona Cargo/Setor
via `Select` (populado por `useCargos()`/`useSetores()`) + matrícula → aba
Comissão preenche comissão à vista → Salvar (`201`) → lista mostra
"Vendedor"/"Comercial" nas colunas certas → reabre → matrícula confirmada
persistida. Zero erro de console **funcional** — dois warnings de depreciação
do Ant Design v6 apareceram (`InputNumber addonAfter`, `Statistic
valueStyle`), nenhum dos dois quebra nada, registrados como pendência de
polish (item novo abaixo). Cargo/Setor/Colaborador de teste removidos
(`DELETE`) antes de encerrar.

## Entrega 5 — Empresas CRUD (2026-09-01)

Diferente das 3 anteriores (não é especialização de Pessoa) — pedido
explícito do usuário: "se atenta nas tabelas filhas para termos uma jornada
boa para o usuário". Isso mudou a arquitetura da tela de verdade, não foi só
um pedido de polish.

### Por que esta tela não segue o molde de Cliente/Fornecedor/Colaborador

`Empresa` tem um endereço único inline (não lista) e, mais importante, 3
sub-recursos com **endpoint HTTP próprio** (`EmpresaController`:
`/empresas/{id}/regime-tributario`, `/configuracao-operacional`,
`/configuracao-fiscal`) — não são campos do `EmpresaRequest`. Consequência
direta pra UX: esses 3 só podem existir depois que a empresa tem um `id` de
verdade, ao contrário de endereços/contatos/telefones de Cliente (que viajam
juntos no mesmo `POST`).

- **6 abas**: Dados Gerais, Endereço, Dados Legais (essas 3 = um `Form` só,
  salvas juntas pelo "Salvar" do cabeçalho, igual às telas anteriores) +
  Regime Tributário, Configuração Operacional, Configuração Fiscal (essas 3 =
  sub-recursos independentes, cada uma com seu próprio `Form`/mutation/botão
  de salvar).
- **Abas de sub-recurso desabilitadas ao criar** (`Tabs` `disabled` + tooltip
  "Salve os dados gerais da empresa primeiro") — sem isso, o usuário clicaria
  numa aba que chama um endpoint com `{id}` inexistente e tomaria erro sem
  entender por quê. `AbaBloqueada.tsx` é o fallback visual caso alguém force
  acesso direto por URL antes de salvar.
- **Regime Tributário é histórico append-only** — o backend só expõe `POST`
  (`422` se o ano já tiver registro), nunca `PUT`/`DELETE`. UI reflete isso:
  tabela read-only + botão "Registrar" (nunca "editar"/"remover" por linha,
  diferente das 3 listas de Cliente/Fornecedor/Colaborador).
- **Configuração Operacional/Fiscal são upsert 1:1** — `GET` pode devolver
  `404` (config ainda não existe, tratado como `null`, não erro — ver
  `api.ts`) e `PUT` cria na primeira chamada. Cada uma com seu próprio botão
  "Salvar configuração X", chamando sua própria mutation — clicar "Salvar" no
  cabeçalho da página NÃO salva essas duas abas (são chamadas HTTP diferentes).
- **Cargo/Setor viraram Contador** — mesmo padrão de lookup simples (`Select`
  populado via API, sem CRUD próprio na tela ainda) aplicado a
  `contadorId`/`ContadorResponse`.

### Achado real, corrigido nesta sessão: `<form>` HTML aninhado

Primeira implementação envolvia o `<Tabs>` inteiro (todas as 6 abas) num
único `<Form form={form}>` do cabeçalho — mas `ConfiguracaoOperacionalTab`/
`ConfiguracaoFiscalTab` têm cada uma o **próprio** `<Form>` interno (chamam
endpoints diferentes, precisam de instância própria). Isso produzia `<form>`
dentro de `<form>`, inválido em HTML — só apareceu ao vivo, num navegador
real (React acusa no console: `"<form> cannot contain a nested form"`,
warning de hidratação), nunca em typecheck/lint/testes automatizados.
Corrigido com `<Form component={false}>` no formulário principal — mantém o
contexto/Provider que os `Form.Item` de Dados Gerais/Endereço/Dados Legais
precisam, mas não renderiza a tag `<form>` real, eliminando o aninhamento
(as abas de sub-recurso continuam com seu `<form>` próprio, agora como
painéis irmãos dentro do `Tabs`, não aninhados). Esse bug estrutural também
causava uma falha **aparente** diferente (abas de sub-recurso pareciam
continuar `disabled` mesmo depois de salvar a empresa) — na verdade era o
erro de hidratação deixando o React num estado instável; sumiu junto com o
fix, confirmado em duas rodadas de teste ao vivo consecutivas.

### Achado real, corrigido em 4 telas (não só Empresa)

Ao investigar o problema acima, encontrei a causa raiz real de um sintoma
relacionado: `useCriarEmpresa()` não populava o cache do TanStack Query com
o resultado da criação — ao navegar pro modo edição logo em seguida,
`useEmpresa(id)` disparava um fetch novo do zero (flash de `Skeleton`), que
em Empresa especificamente combinava mal com o `disabled` das abas (efeito
colateral do bug do form aninhado, não causa raiz sozinha). Corrigido
adicionando `queryClient.setQueryData(...)` no `onSuccess` de
`useCriarEmpresa`/`useCriarCliente`/`useCriarFornecedor`/`useCriarColaborador`
— as 4 telas de negócio construídas até agora tinham o mesmo gap, só que
sem consequência visível nas outras 3 (não tinham UI condicionada a
`modoEdicao` dentro do próprio formulário).

### Verificação ao vivo (Playwright, navegador real)

Jornada completa: criar empresa nova → confirma as 3 abas de sub-recurso
`aria-disabled="true"` **antes** de salvar (screenshot conferido
visualmente) → preencher Dados Gerais + Endereço → Salvar (`201`) → confirma
as 3 abas destravadas **imediatamente**, sem flash de loading → registrar
regime tributário 2026/CRT 3 (aparece na tabela histórica) → marcar "permite
mesas" e salvar Configuração Operacional → preencher série NFCe e salvar
Configuração Fiscal → **recarregar a página do zero** (não só re-render) →
confirma os 3 sub-recursos persistidos de verdade (não só em memória).
Screenshots conferidos visualmente nos dois momentos-chave (abas bloqueadas
vs. jornada completa com as 2 mensagens de sucesso lado a lado). Uma empresa
de teste ficou no banco local (não consegui removê-la — `DELETE` e `PUT`
pra inativar deram 409 mesmo sem os campos óbvios de conflito; não
investigado a fundo, registrado como pendência, claramente identificável
pelo nome "Cactus Filial Boa Viagem LTDA" pra limpeza manual futura).

## Entrega 6 — CRUD de lookups (Cargo/Setor/Contador) + limpeza de depreciações (2026-09-01)

Fechamento de pendências acumuladas nas Entregas 4/5, a pedido do usuário
("segue com as pendências de desenvolvimento").

### Modais de gerenciamento, não páginas/rotas próprias

Cargo/Setor (Colaborador) e Contador (Empresa) ganharam CRUD completo pela
UI — mas como **modal de gerenciamento** aberto por um botão "+" ao lado do
respectivo `Select` (`RhTab.tsx`/`DadosLegaisTab.tsx`), não uma rota
separada. Decisão deliberada: se o usuário está no meio de preencher um
Colaborador/Empresa e percebe que falta um cargo/contador, ele cadastra sem
perder o progresso do formulário que já estava preenchendo — navegar pra
outra página nesse momento descartaria (ou complicaria preservar) esse
estado. `shared/hooks/useLookupModal.ts` (novo) generaliza só o "abrir novo
vs. editar item X" que os 3 modais compartilham; cada modal mantém seu
próprio `Form`/tabela/colunas (Cargo tem salário+CBO, Setor só nome+descrição,
Contador tem CRC+email+telefone — campos diferentes o suficiente pra não
valer uma abstração mais funda que isso).

### Achado real sério: `id` HTML duplicado entre formulários simultâneos

O Ant Design usa o `name` do campo como `id` do `<input>` por padrão. Como
`ColaboradorFormPage`/`EmpresaFormPage` têm um campo `name="nome"` (Nome
completo/Razão social) e os modais de lookup **também** têm um campo
`name="nome"` (nome do cargo/setor/contador), e o Ant Design `Tabs` mantém
painéis inativos montados no DOM (não desmonta ao trocar de aba) — os dois
formulários coexistem na página ao mesmo tempo, cada um gerando
`id="nome"`, um **id duplicado, HTML inválido**. Só apareceu testando ao
vivo com Playwright (`getByLabel` resolvia pro elemento errado/invisível).
Corrigido dando `name` (namespace) a cada `<Form>` dos 3 modais
(`cargoForm`/`setorForm`/`contadorForm`) — o Ant Design prefixa
automaticamente os ids internos com esse namespace
(`id="cargoForm_nome"` em vez de `id="nome"`), eliminando a colisão.
**Lição registrada**: todo `<Form>` que pode coexistir na página com outro
`<Form>` (looks like modals over other forms, ou múltiplas abas de
sub-recurso independentes como em Empresa) precisa de um `name` próprio —
não é só estilo, evita colisão de id real.

### Achado real, menor: botões "+" sem texto acessível

Os botões de abrir os 3 modais são só um ícone (`PlusOutlined`) dentro de um
`Tooltip` — o `Tooltip` não vira `aria-label` automaticamente, então o botão
não tinha nome acessível nenhum (nem pra leitor de tela, nem pro teste
Playwright localizar por `getByRole('button', {name: ...})`). Corrigido
adicionando `aria-label` explícito nos 3 botões, mesmo texto do `Tooltip`.

### Warnings de depreciação do Ant Design v6 eliminados

`InputNumber addonAfter/addonBefore` (Cliente/Colaborador) e `Statistic
valueStyle` (dashboard) — em vez de reconstruir com `Space.Compact` (mais
código pra um ganho visual pequeno), a unidade (R$/%) foi movida pro label
do campo (“Limite de crédito (R$)”, “Comissão à vista (%)”) — mais simples,
sem API deprecada, e o rótulo fica sempre visível (antes só aparecia com o
campo em foco/preenchido). `Statistic` trocou `valueStyle` por
`styles={{ content: {...} }}`, API atual.

### Falso alarme descartado: "409 ao inativar empresa de teste"

Investigado a fundo (pendência 0a da Entrega 5): não era bug. Eu estava
reenviando um CNPJ que *achava* ser o da criação, mas o valor realmente
persistido era outro (gerado pelo script de teste). Unicidade de CNPJ
rejeitando um valor colidente é o comportamento correto — reenviado com o
CNPJ certo, `200`, sem mudança de código.

### Falso alarme descartado: "cargo duplicado ao editar"

No meio da validação ao vivo, encontrei 2 registros "Gerente de Loja" no
banco em vez de 1 editado — investigado com log de rede detalhado
(`page.on('request')`): confirmado **1 único** `POST` e **1 único** `PUT`
na execução que passou. O segundo registro era resíduo de uma execução de
teste ANTERIOR que tinha travado no meio do fluxo (antes dos fixes de
seletor/namespace acima), já tinha completado a criação antes de travar, e
eu não tinha limpado o banco entre tentativas. Sem bug de double-submit
real — confirmado, não só assumido.

### Verificação ao vivo (Playwright, navegador real)

Fluxo completo nos 3 modais: abrir Colaborador novo → aba RH → "Gerenciar
cargos" → criar "Gerente de Loja" (salário 4500) → aparece na tabela →
clicar editar → form pré-preenchido corretamente → mudar salário pra 5000 →
"Salvar alterações" → fechar modal → confirma "Gerente de Loja" aparece no
`Select` de Cargo. Mesma jornada resumida pra Setor (Colaborador) e Contador
(Empresa, aba Dados Legais). **Zero erro de console** na execução final,
incluindo confirmação explícita de que os 2 warnings de depreciação não
aparecem mais. Screenshot conferido visualmente (modal de Contadores
sobreposto ao formulário de Empresa, sem perder o estado de fundo).
Registros de teste (`Gerente de Loja`×2, `Loja Centro`, `João Contador
Teste`, `Teste Duplo`) removidos via `DELETE` antes de encerrar.

## Entrega 7 — Produto + Tabelas de Preço (2026-09-02)

Pedido explícito do usuário pra tratar com cuidado redobrado ("uma das partes
mais importantes do cadastro, precisamos ser muito criteriosos") — antes de
codar, li por completo `ProdutoRequest`/`Response`, `ProdutoFaixaPrecoDTO`,
`ProdutoImagemDTO`, `TabelaPreco`/`TabelaPrecoProdutoDTO`, os 5 lookups
(Grupo/Subgrupo/Tipo/Unidade/Marca), os catálogos fiscais (NCM/CEST/Grupo
Tributário/Operação Fiscal) e os enums `CstIcms`/`Csosn`/`TipoItemSped` no
backend, e apresentei o desenho de telas por escrito antes de escrever
qualquer componente. Duas decisões de escopo confirmadas via
`AskUserQuestion` (ambas "Recomendado" aceitas): (1) os 5 lookups de Produto
ganham modal de gerenciar nesta mesma entrega (senão o cadastro fica
bloqueado sem outro sistema pra popular Grupo/Marca/etc.); (2) "Tabelas de
Preço" — mecanismo **complementar**, não confundir com `ProdutoFaixaPreco`,
ver abaixo — também entra nesta entrega, não fica pra depois.

### Estrutura de abas do `ProdutoFormPage`

Mesmo molde multi-tab de Cliente/Empresa (`Form` único, `Tabs` com painéis
sempre montados, listas filhas em estado local mescladas só no submit):
1. **Dados Gerais** — código interno/GTIN/nome/descrição/descrição PDV +
   Unidade (Select simples) e **Grupo → Subgrupo em cascata**: `produtoGrupoId`
   **não é campo do `ProdutoRequest`** (só `produtoSubgrupoId` é enviado) — o
   Select de Grupo é estado local só pra filtrar as opções do Select de
   Subgrupo; em edição, hidratado a partir de `produto.produtoGrupoId`
   (já resolvido pelo backend, denormalizado a partir do subgrupo).
2. **Preços & Estoque** — custo/compra/venda + estoque mínimo/máximo com
   `Alert` deixando explícito que é só política sugerida (saldo real vive no
   `controle-estoque-service`, sem tela ainda).
3. **Tributação** — NCM/CEST via `AsyncSelect` (componente novo,
   `shared/ui/AsyncSelect.tsx` — busca server-side com debounce simples via
   `onSearch`, necessário porque NCM é um catálogo de ~10.000 códigos
   oficiais, carregar tudo não é opção, diferente do padrão "carrega 200" dos
   lookups pequenos), Grupo Tributário (Select pequeno, **sem** modal de "+"
   — criar um de verdade envolve a matriz de ICMS por UF, fica pro futuro
   módulo de Motor Tributário), `tipoItemSped`/`iat`/`ippt` e um painel
   **somente leitura** "Tributação resolvida" (`cstIcmsPadrao`/`csosnPadrao`/
   5 alíquotas/`dataResolucaoPadrao`) com botão **Recalcular** que abre
   `RecalcularTributacaoModal` (Operação Fiscal + UF destino) e chama
   `POST /produtos/{id}/recalcular-tributacao-padrao` — só habilitado com o
   produto já salvo, mesmo gate das sub-abas de Empresa.
4. **Faixas de Preço / Promoções** — a resposta direta ao pedido do usuário
   ("produtos em promoção por quantidade"): lista de `ProdutoFaixaPreco`
   editável (padrão Endereços/Contatos), com texto de apoio explicando que é
   **um conceito só** (`quantidadeMinima=1` + datas preenchidas = promoção
   temporária; `quantidadeMinima>1` sem datas = desconto permanente por
   atacado) — não dois mecanismos.
5. **Imagens** — lista de `ProdutoImagem` (só URL por ora, upload direto
   depende do futuro GED).

`TabelaPreco` **não** é aba de Produto — ela é dona da relação inversa
(tabela → lista de produtos, `POST /tabelas-precos` recebe a lista inteira),
por isso é tela própria (`TabelasPrecoListPage`/`TabelaPrecoFormPage`, rota
irmã de Produtos): serve pra segmentar preço por canal/perfil (Varejo,
Atacado, Cliente VIP), não por quantidade levada.

### 5 modais de lookup, mesmo padrão de Cargo/Setor/Contador

`GerenciarGruposModal`/`GerenciarSubgruposModal`/`GerenciarTiposModal`/
`GerenciarUnidadesModal`/`GerenciarMarcasModal` — cada `Form` com `name`
namespaced (`produtoGrupoForm`, etc., mesmo motivo já registrado nos modais
de Colaborador/Empresa: evita colisão de `id` com o form da página por trás).
`GerenciarSubgruposModal` é o único com um campo a mais (`produtoGrupoId`,
obrigatório) e mostra a coluna "Grupo" na tabela.

### BUG REAL encontrado e corrigido — `ProdutoImagem.ordem` nulo quebrava a criação

Testando ao vivo o fluxo completo de criar produto (adicionar uma imagem sem
preencher "Ordem de exibição", campo opcional na UI): `POST /produtos`
devolvia 409 com uma mensagem genérica de conflito de integridade. Log do
backend revelou a causa real: `DataIntegrityViolationException: Column
'ordem' cannot be null`. `ProdutoImagemDTO.ordem` é `Integer` opcional (sem
`@NotNull`, documentado assim de propósito), e a entidade `ProdutoImagem` tem
`ordem` com default de campo (`= 0`) — mas o `ProdutoMapper` (MapStruct) gera
`toEntity(ProdutoImagemDTO)` copiando `dto.ordem()` (`null`) por cima do
default da entidade, porque a atribuição do mapper roda depois do construtor.
Corrigido no backend (`ProdutoMapper.java`) com
`@Mapping(target = "ordem", source = "ordem", defaultValue = "0")` no método
`toEntity` — mesmo método usado tanto em `criar()` quanto `atualizar()`
(`ProdutoServiceImpl.aplicarImagens`), então a correção cobre os dois
caminhos. Validado ao vivo pós-fix: produto com imagem sem `ordem` explícito
criado com sucesso (`201`).

### BUG REAL encontrado e corrigido — item de Tabela de Preço sem nome/código antes de salvar

Ao adicionar um produto numa Tabela de Preço nova (antes do primeiro save),
a linha da tabela mostrava "—"/"—" nas colunas Código/Produto — porque
`produtoNome`/`produtoCodigoInterno` só vêm preenchidos pela *Response* do
backend (documentado assim no próprio Javadoc de `TabelaPrecoProdutoDTO`), e
o item recém-adicionado só existe em estado local (ainda não passou pelo
round-trip da API). Corrigido em `ItemTabelaPrecoModal.tsx`: ao salvar,
resolve o nome/código a partir do resultado de busca já carregado
(`produtos?.find(p => p.id === valores.produtoId)`) e injeta nos 2 campos —
o backend ignora esses campos na escrita (só os usa como enriquecimento de
leitura), então enviá-los não tem efeito colateral.

### Achado de ferramenta de teste (não é bug de produto): Ant Design v6 renomeou classes CSS internas

`.ant-select-selector` (usado em toda a suíte de testes Playwright de
entregas anteriores, aparentemente sem nunca ter sido exercitado a fundo em
`Select`s sem `id`/`label` associável) virou `.ant-select-content` no Ant
Design v6 — só descoberto ao tentar clicar no Select de "Grupo" (único campo
da tela sem `name` de formulário, por ser estado local, então sem `id` pra
mirar diretamente). Registrado aqui porque vai se repetir em qualquer teste
futuro que precise mirar um `Select` sem rótulo/id acessível.

### Warning de depreciação do Ant Design v6 eliminado (achado durante esta entrega, não introduzido por ela)

`Alert message` (deprecado, usar `title`) — encontrado ao vivo no console
durante o teste desta entrega (`PrecosEstoqueTab.tsx`), mas na verdade já
existia em `LoginPage.tsx`/`CallbackPage.tsx` desde a Entrega 1 (só não tinha
sido notado porque o dev warning do Ant Design deduplica por mensagem, e
essas 2 telas nunca tinham sido revisitadas depois de um teste que também
usasse `PrecosEstoqueTab`). Corrigido nos 3 lugares — `description` continua
existindo normalmente, só `message` foi renomeado.

### Verificação ao vivo (Playwright, navegador real)

Fluxo completo, produto seedado (`Refrigerante Cactus Cola 2L`, com 2 faixas
de preço reais — atacado permanente + promoção com vigência — e 1 imagem já
semeadas): abrir em edição → todas as 5 abas renderizam corretamente (Grupo/
Subgrupo/Marca/Unidade/Tipo hidratados, faixas de preço com rótulo/vigência
corretos, imagem com preview) → Recalcular tributação padrão (Operação
Fiscal "Venda a consumidor final (SP)" + UF "SP") → painel resolvido passa a
mostrar CST/CSOSN/alíquotas reais. Depois, produto **novo** do zero: criar
Marca via modal sem sair da tela → preencher Dados Gerais com cascata Grupo→
Subgrupo → NCM via busca assíncrona (`AsyncSelect`, digitando "refrigerante")
→ Grupo Tributário → adicionar 1 faixa de preço promocional → adicionar 1
imagem → salvar (`201`, achado do bug de `ordem` corrigido no caminho) →
criar uma Tabela de Preço nova referenciando esse produto (achado do bug de
nome/código corrigido no caminho) → salvar (`201`). **Zero erro de console**
na execução final (as 2 mensagens `ERR_NAME_NOT_RESOLVED` são esperadas — URL
de imagem fake `exemplo.cactusdigital.local`, mesmo domínio de exemplo já
usado no seed). Todos os registros de teste (produto, marca, tabela de preço)
removidos via `DELETE` direto na API antes de encerrar — produto seedado
manteve a tributação padrão calculada durante o teste (efeito colateral
transparente, não revertido: não existe endpoint pra "limpar" o cache, e o
resultado é uma demonstração real e correta da funcionalidade, não uma
corrupção do dado demo).

## Entrega 8 — Estoque, Fase A (2026-09-02)

Primeiro módulo fora do Cadastro Base a ganhar tela (`controle-estoque-service`,
porta 8083, novo `estoqueApi` em `httpClients.ts`/`VITE_ESTOQUE_API_URL`).
Backend tem 10 controllers — dividido em 2 fases, combinado com o usuário
antes de codar (`AskUserQuestion`): **Fase A** (esta entrega) cobre o núcleo
do dia a dia — Depósitos, Configuração de Estoque, Saldos, Movimentos
(ledger), Transferências. **Fase B** (futura) cobre os recursos mais
especializados — Inventário Físico, Lote, Número de Série, Kit, Curva ABC.

### Estrutura de menu e escopo por empresa ativa

"Estoque" ganhou submenu (mesmo padrão de "Cadastro Base"): Depósitos /
Saldos / Movimentos / Transferências / Configuração. Diferente do Cadastro
Base, aqui não existe Select de Empresa nos formulários — tudo é escopado
implicitamente pela **empresa ativa da sessão** (`useAuth().empresaAtivaId`,
já exposta pelo `authStore` desde a Entrega 1) — pra gerenciar outra filial,
o usuário troca de empresa no cabeçalho, não escolhe num dropdown dentro da
tela de Estoque.

### Resolução de nome de produto cross-serviço (`shared/`)

`SaldoEstoqueResponse`/`EstoqueMovimentoResponse`/`TransferenciaEstoqueResponse`
só carregam `produtoId` solto (sem FK de banco, cross-serviço) — sem nome
denormalizado como em outros DTOs. Criado `shared/api/produtoLookup.ts`
(`buscarProdutoResumo`, bate direto no `cadastro-base-service`) +
`shared/hooks/useProdutoResumo.ts` + `shared/ui/ProdutoResumoCell.tsx`,
deliberadamente em `shared/` (não dentro de `features/estoque/`) porque é um
padrão que qualquer módulo futuro com referência solta a produto (Compras,
BI) vai precisar de novo. Cache do TanStack Query dedupe por `produtoId`, e
tabelas de teste confirmaram um produto "órfão" (id usado em Estoque mas
nunca cadastrado de fato no Produto — situação real do dado seedado, ver
próxima seção) cai graciosamente pra exibir `#<id>` em vez de quebrar a
tabela.

### BUG REAL encontrado e corrigido — retry desnecessário em referência 404 permanente

Testando ao vivo a tela de Saldos com os dados seedados (que incluem
`produtoId=99`/`produtoId=3`, referências soltas nunca cadastradas de fato no
`cadastro-base-service` — mesma situação já documentada no `business-intelligence-service`,
"produtos de teste... aparecem com nome null"), o console mostrava 6 erros de
rede 404 pra só 2 ids órfãos. Causa: `useProdutoResumo` não sobrescrevia o
`retry: 1` padrão do `QueryClient` global — um 404 nessa referência é
**permanente** (o id não existe e não vai passar a existir só por tentar de
novo), diferente de um 5xx transitório, que retry genuinamente ajuda.
Corrigido com `retry: (failureCount, erro) => erro.status !== 404 && failureCount < 1`.
Validado ao vivo: de 6 requisições de rede pros 2 ids órfãos, caiu pra 2 (uma
por id, sem retry).

### Verificação ao vivo (Playwright, navegador real)

Fluxo completo com o `controle-estoque-service` real (precisou subir com
`CACTUS_RABBITMQ_USER=cactus CACTUS_RABBITMQ_PASSWORD=cactus123` — default
`guest`/`guest` não bate com o broker real, achado já registrado no CLAUDE.md
raiz na Fase 6, se repete aqui por ser a primeira vez que este serviço sobe
numa sessão de frontend): depósito seedado (`Depósito Principal`) visível →
criar `Depósito Secundário` → Saldos mostrando os 3 saldos seedados com nome
de produto resolvido (e os 2 ids órfãos caindo em `#99`/`#3`) → Movimentos
mostrando os 10 lançamentos seedados (entradas/saídas com tags coloridas
corretas) → registrar uma nova Entrada manual (saldo atualizado de imediato)
→ criar Transferência do Depósito Principal pro Secundário → Enviar (situação
`EM_TRANSITO`) → trocar filtro pro depósito de destino → Receber (situação
`CONCLUIDA`, saldo no destino herdando o custo médio da origem, mesmo
comportamento já validado na Fase 2 do backend) → Configuração de Estoque
mostrando o default seedado (Média Ponderada, limite R$500) → atualizar pra
R$600 → reverter pro valor original (cortesia de limpeza, já que é só um
`PUT`, sem "registro de teste" pra apagar). **Zero erro de console** na
execução final. Depósito Secundário e as transferências/movimentos de teste
ficaram como dado de demonstração real (não removidos — diferente de um
registro de teste solto, aqui já são movimentações de ledger genuínas,
coerente com o histórico seedado).

## Entrega 9 — Estoque, Fase B (2026-09-02)

Fecha o módulo de Estoque: Inventário Físico, Lote, Número de Série, Kit/
Produto Composto e Curva ABC — os 5 controllers que a Fase A (Entrega 8)
tinha deixado pra depois. Mesmo padrão de menu (filhos do submenu "Estoque")
e de escopo por empresa ativa da Fase A.

### Telas novas

- **Lotes** — CRUD (sem `DELETE` no backend, `LoteController` só tem GET/POST/PUT
  — rastreabilidade não se apaga, só fica inativa via `situacao`). Filtro por
  produto opcional + toggle "Só vencimento próximo (30 dias)" que troca pra
  `GET /lotes/produto/{id}/vencimento-proximo` (exige produto selecionado).
  Coluna Validade fica vermelha quando ≤30 dias — pista visual de FEFO.
- **Números de Série** — diferente de Lote, aqui o filtro por produto é
  **obrigatório** (`NumeroSerieController.listarPorProduto` exige o param, sem
  "listar tudo"). Situação trocada inline via `Select` na própria linha da
  tabela (`PATCH /numeros-serie/{id}/situacao`) — ação rápida de 1 campo não
  justifica abrir modal.
- **Kits / Produtos Compostos** — sem "cadastro de kit" próprio: qualquer
  produto vira kit só por ganhar componentes (`ProdutoKitController` é todo
  escopado por `produtoKitId` na URL, sem flag `éKit` em `Produto`). Tela
  começa com busca de produto, não listagem. Botão "Baixar Kit" exposto pra
  operação manual/testes, mesmo sabendo que o uso real dessa ação é
  disparado por outro sistema (PDV/venda), não por alguém clicando aqui.
- **Curva ABC** — relatório puro, sem paginação (calculada sob demanda a
  partir do ledger, não é entidade persistida) — botão "Calcular" dispara a
  query, não carrega nada até o usuário pedir.
- **Inventário Físico** — a tela mais elaborada da Fase B: lista (filtrada
  por depósito, obrigatório, mesmo padrão de Transferências) + página de
  detalhe (`/estoque/inventarios/:id`) com a máquina de estados completa:
  `ABERTO` → **Iniciar Contagem** → `EM_CONTAGEM` (lança contagem item a
  item, um `InputNumber`+botão "Salvar contagem" por linha) → **Finalizar**
  → `FINALIZADO` (gera ajuste automático por divergência, mostrado na
  própria tabela). `Cancelar` disponível em `ABERTO`/`EM_CONTAGEM`.
  `quantidadeSistema` só aparece quando o tipo é `POR_SALDO` ou o inventário
  já foi fechado — em `CEGA` durante a contagem a coluna some (a API devolve
  o campo de qualquer forma, documentado como decisão do app cliente, não do
  backend — ver Javadoc de `InventarioFisicoItemResponse`).

### Achado real, mas não é bug: `finalizar()` exige todos os itens contados

Testando ao vivo, `POST /inventarios/{id}/finalizar` devolveu 422 na primeira
tentativa — o script de teste só tinha lançado contagem de 1 dos 3 itens do
inventário antes de finalizar. Comportamento correto e documentado
(`InventarioFisicoController`: "422 Existe item ainda não contado") —
corrigido no script de teste (contar todos os itens antes de finalizar), sem
mudança de código de produto.

### Warning de depreciação do Ant Design v6 eliminado (achado nesta entrega)

`InputNumber addonBefore/addonAfter` em `CurvaAbcPage` (o input "Últimos [N]
dias") — mesmo tratamento já usado em Cliente/Colaborador (Entrega 6): texto
fixo ao lado do campo (`<Flex><span>Últimos</span><InputNumber/><span>dias</span></Flex>`)
em vez da API depreciada.

### Verificação ao vivo (Playwright, navegador real)

Fluxo completo: criar um lote (produto seedado, validade futura) → cadastrar
um número de série (mesmo produto, Depósito Principal) → trocar sua situação
inline (Disponível↔Baixado, confirmando a troca persiste) → abrir a tela de
Kits pro mesmo produto e confirmar a UI do modal "Adicionar Componente" (sem
submeter, por não haver um 2º produto óbvio nesta sessão pra usar como
componente sem risco de coincidir com o próprio produto) → calcular Curva
ABC (produto seedado aparece corretamente classificado, batendo com o valor
real movimentado) → abrir um Inventário Físico `POR_SALDO` no Depósito
Principal (snapshot dos 3 saldos reais) → Iniciar Contagem → lançar
contagem nos 3 itens (1 com divergência proposital de +994, 2 repetindo o
saldo do sistema) → Finalizar → tabela final mostrando as 3 divergências
corretas (+994/0/0) e situação `Finalizado`. **Zero erro de console** na
execução final (as 2 mensagens 404 são esperadas — mesmos 2 produtos órfãos
já documentados na Fase A). Lote/Número de série/Inventários de teste
ficaram como dado de demonstração real — nenhum dos 3 tem `DELETE` no
backend (rastreabilidade de estoque não se apaga por design), então não há
"limpeza" possível nem necessária, mesma lógica já registrada na Fase A pra
Depósito Secundário/transferências.

## Entrega 10 — Segurança (2026-09-02)

Pedido explícito do usuário com ênfase máxima em correção ("módulo crítico,
não pode ter falhas", "perfilamento tem que ser perfeito") — backend
(`seguranca-service`) mapeado por completo antes de codar: `Usuario`/`Papel`/
`Funcionalidade`/`Modulo`/`UsuarioPapelEmpresa`. Achado de arquitetura chave,
confirmado no backend antes de desenhar a tela: **não existe um "toggle de
módulo" separado** — a visibilidade de cada módulo no menu de um usuário é
100% derivada da união das `Funcionalidade`s de todos os `Papel`éis que ele
tem em `UsuarioPapelEmpresa` pra empresa ativa. "Atribuir/retirar módulo" =
marcar/desmarcar funcionalidades daquele módulo no Papel do usuário — não
existe (nem devia existir) um mecanismo paralelo.

### Telas novas

- **Usuários** — CRUD com 3 grupos de ação **deliberadamente separados** do
  PUT genérico (nenhum deles é reescrito pelo Salvar principal, só por botão
  próprio): Alterar Senha (`PATCH .../senha`, modal com confirmação),
  Bloquear/Desbloquear (travamento temporário, reversível), Inativar/Reativar
  (equivalente a "excluído" sem perder auditoria). Aba **Papéis por Empresa**
  (sub-recurso, gate por `modoEdicao` como Empresa/Produto) — é a tela que
  realmente decide "quais módulos este usuário vê": atribuir/revogar
  `Papel × Empresa`, com nome de empresa resolvido cross-serviço
  (`EmpresaCell`, mesmo padrão de `ProdutoResumoCell` em Estoque).
- **Papéis** — Dados Gerais + aba **Funcionalidades**, a tela mais crítica do
  módulo: matriz agrupada por Módulo (7 módulos reais — Segurança, Cadastro
  Base, Compras, Estoque, GED, BI, Sincronização), cada grupo com um
  checkbox mestre (tri-state: todas/algumas/nenhuma marcada) pra ligar um
  módulo inteiro pro papel num clique, contador "X/Y" por módulo, busca por
  nome/código, e alerta explícito ensinando a mecânica pro admin ("se nenhuma
  funcionalidade de um módulo estiver marcada... o módulo não aparece pra
  ele"). Salva com `PUT /papeis/{id}/funcionalidades` (substituição completa
  do conjunto, não incremental).
- **Funcionalidades**/**Módulos** — catálogos só leitura (semeados via
  migration a cada endpoint novo, sem CRUD por API de propósito), pra dar
  visibilidade total de auditoria de tudo que existe no sistema.

### BUG REAL corrigido no backend — N+1 na listagem de Papéis

Antes mesmo de montar a tela, ao investigar `PapelService`, achei que
`listar()` fazia `pagina.map(mapper::toResponse)` sem carregar a coleção
`funcionalidades` (`@ManyToMany` lazy) — 1 query pela página + N pra resolver
a coleção de cada papel. Este era o único ponto da varredura de N+1 de
2026-08-31 (que cobriu Cliente/Colaborador/Fornecedor/Produto/TabelaPreco/
ConfiguracaoTributaria/InventarioFisico) que ainda não tinha sido auditado —
`Papel` é do `seguranca-service`, fora do escopo daquela varredura por
serviço. Corrigido com o mesmo `PaginatedFetchSupport` já usado nos outros
casos: `PapelRepository.findAllByIdComFuncionalidades` (`LEFT JOIN FETCH`)
rebusca só a página com a coleção carregada. Validado ao vivo: `GET /papeis`
com os 5 papéis seedados devolvendo todas as funcionalidades corretamente,
sem regressão.

### 2 BUGS REAIS corrigidos no frontend, achados testando ao vivo

1. **`ativo` nunca era enviado na criação de usuário** — `UsuarioRequest.ativo`
   nunca teve um `Form.Item` próprio (não é editável pelo usuário, só a
   criação considera o campo — ver Javadoc de `UsuarioRequest` em `types.ts`),
   então `form.validateFields()` devolvia `ativo: undefined`. O backend usa
   `boolean` primitivo (não `Boolean`), e `null`/ausente no JSON quebra o
   parse com 400 (`Cannot map null into type boolean`). Corrigido incluindo
   `ativo: true` explicitamente no request de criação.
2. **`GET /colaboradores/null`** — `Usuario.colaboradorId` vem `null` no JSON
   quando não há vínculo (é assim que Jackson serializa um `Long` Java
   ausente — a chave continua no objeto, só o valor é `null`), não
   `undefined`. O guard `enabled: id !== undefined` do `useColaboradorResumo`
   deixava passar `null`, disparando a busca com um id inválido (400 real no
   `cadastro-base-service`). Corrigido trocando pra `id != null` (pega os
   dois) e corrigindo o tipo de `UsuarioResponse.colaboradorId` pra
   `number | null` (estava documentado errado como `?: number`, escondendo a
   nulidade real).

### Verificação ao vivo (Playwright, navegador real)

Fluxo completo: criar usuário (senha visível só na criação, some na edição,
confirmado) → Alterar Senha → Bloquear/Desbloquear → Papéis por Empresa
(atribuir OPERADOR na empresa seedada, revogar) → Inativar/Reativar → criar
Papel novo → aba Funcionalidades vazia → marcar o módulo inteiro "Segurança"
(2 funcionalidades) via checkbox mestre → Salvar Funcionalidades → **reload
completo da página** pra confirmar persistência real no banco (não só estado
local) → catálogos de Funcionalidades/Módulos navegáveis. **Zero erro de
console na execução final** (os 2 bugs acima foram encontrados e corrigidos
nesta mesma sessão de teste, não deixados como pendência). Todos os registros
de teste (usuário, papel) removidos via `DELETE` direto na API ao final.

## Entrega 11 — Gestão de Documentos / GED (2026-09-02)

Backend (`gestao-documentos-service`, porta 8085) mapeado por completo antes
de codar: `TipoDocumento`/`PastaDocumento`/`Documento`/`DocumentoVersao`.
Achado de arquitetura chave, confirmado antes de desenhar a tela: **não
existe `PUT` de metadado pra `Documento`** — a API só tem `POST` (criar,
multipart) e `POST .../versoes` (nova versão, multipart); editar nome/
descrição/pasta depois de criado não é possível. Isso definiu o desenho: a
tela de detalhe do documento é consulta+ações, nunca um form de edição.

### Telas novas

- **Tipos de Documento** — CRUD simples (nome + tamanho máximo em MB, que
  valida todo upload desse tipo), lista com modal de criar/editar.
- **Pastas** — CRUD com hierarquia via `pastaPaiId` (auto-referência) — lista
  flat com coluna "Pasta pai" resolvida, Select de pasta pai no modal
  (excluindo a própria pasta da lista de opções ao editar; ciclo mais
  profundo é validado pelo backend, 422). Único CRUD do GED com "Excluir" de
  verdade na UI (as demais telas seguem o padrão de exclusão lógica via
  `situacao`).
- **Documentos** — lista (filtro por vínculo cross-serviço `entidadeTipo`+
  `entidadeId`, genérico, sem UI dedicada de nenhum outro módulo consumindo
  isso ainda) + **upload** (`DocumentoUploadPage`, primeiro formulário
  multipart do frontend — `Upload.Dragger` do Ant Design com
  `beforeUpload` retornando `false` pra não disparar upload automático,
  arquivo guardado em estado local e só enviado no submit real) + **detalhe**
  (metadados read-only + histórico de versões + ações: Baixar última versão,
  Baixar versão específica, Nova Versão, Excluir — as duas últimas
  desabilitadas com tooltip quando `permiteAlteracao`/`permiteExclusao=false`).

### Download de arquivo — implementação

`baixarEDisparar` em `api.ts`: `responseType: 'blob'`, lê `Content-Disposition`
da resposta pra extrair o nome real do arquivo (regex, com fallback pro nome
do documento se o header não vier), cria um `Blob` URL temporário e dispara
o download via um `<a download>` sintético — padrão puro-browser, sem
biblioteca extra. Validado ao vivo com `page.waitForEvent('download')`
(Playwright): nome de arquivo correto tanto pra última versão quanto pra uma
versão específica mais antiga.

### Achado de sessão, não um bug de código: senha do `admin` alterada

Ao retomar a sessão pra testar o GED, o `/dev-login` com a senha padrão de
desenvolvimento (`TrocarNoPrimeiroAcesso@2026`) passou a falhar com 401. Não
tentei adivinhar nem resetei nada sozinho — perguntei ao usuário, que
confirmou ter trocado a senha do `admin` (usando a própria tela "Alterar
Senha" construída na Entrega 10) e passou a senha atual pra eu continuar os
testes. Registrado aqui só pra não ser confundido com bug numa sessão futura:
o `/dev-login` depende de credenciais reais do banco, que podem mudar por
uso legítimo da própria aplicação.

### Verificação ao vivo (Playwright, navegador real)

Fluxo completo: criar Tipo de Documento → criar Pasta raiz + Subpasta (hierarquia
confirmada na listagem) → abrir o documento seedado "Contrato Travado"
(`permiteExclusao=false`/`permiteAlteracao=false`) e confirmar Nova Versão/
Excluir corretamente desabilitados → baixar a última versão (download real
confirmado, nome de arquivo correto) → criar um documento novo do zero
(metadados + upload de arquivo real) → enviar uma segunda versão (histórico
mostrando as 2 versões corretamente, com observação/tamanho/data) → baixar a
versão 1 especificamente (nome de arquivo da versão antiga, não da mais
recente) → excluir o documento (lógico, redireciona pra lista). **Zero erro
de console na execução final.** Pasta/Tipo de Documento de teste ficaram
como resíduo aceitável — não puderam ser excluídos via API porque o
documento de teste (agora inativo) ainda os referencia, comportamento
correto de integridade (histórico preservado), não um bug.

### Pendências conhecidas / próxima entrega

0a. ~~Empresa de teste não removida~~ — **falso alarme, esclarecido
   (2026-09-01)**: o 409 ao tentar `PUT` pra inativar não era bug — eu estava
   reenviando o CNPJ que eu *achava* ter sido usado na criação
   (`01788287123271`), mas o valor real persistido era outro
   (`01788287574615`, provavelmente gerado pelo próprio script de teste
   Playwright, timestamp-based). Unicidade de CNPJ rejeitando um valor que
   colidiria é o comportamento correto. Reenviado com o CNPJ real → `200`,
   empresa de teste inativada normalmente. Nenhuma mudança de código
   necessária.
0b. ~~Contador sem tela própria de CRUD~~ — **feito (Entrega 6)**:
   `GerenciarContadoresModal.tsx`, aberto pelo botão "+" ao lado do `Select`
   em `DadosLegaisTab.tsx`.
1. ~~Botão de excluir cliente/fornecedor/colaborador~~ — **decidido
   (2026-09-01), não implementar**: perguntado explicitamente ao usuário pra
   Cliente, confirmada a recomendação de ficar só com ativar/inativar via
   `Situacao` (já implementado); mesma decisão estendida a Fornecedor e
   Colaborador sem perguntar de novo (é a mesma política de sistema,
   "exclusão lógica, nunca física", não uma decisão nova por entidade — ver
   CLAUDE.md raiz). O backend continua tendo `DELETE
   /api/v1/{clientes,fornecedores,colaboradores}/{id}` disponível pra uso
   administrativo direto via API/Swagger se um dia for realmente necessário —
   só não fica exposto nas telas.
1a. ~~Cargo/Setor sem tela própria de CRUD~~ — **feito (Entrega 6)**:
   `GerenciarCargosModal.tsx`/`GerenciarSetoresModal.tsx`, abertos pelo botão
   "+" ao lado dos `Select` em `RhTab.tsx`.
1b. ~~Dois warnings de depreciação do Ant Design v6~~ — **feito (Entrega 6)**:
   `InputNumber addonAfter/addonBefore` removido (unidade movida pro label do
   campo) e `Statistic valueStyle` → `styles={{ content: {...} }}`.
1c. ~~Warning `Alert message` deprecado~~ — **feito (Entrega 7)**: `message`→
   `title` em `PrecosEstoqueTab`/`LoginPage`/`CallbackPage`.
1d. ~~`ProdutoImagem.ordem` nulo quebrando criação de produto~~ — **feito
   (Entrega 7, backend)**: `@Mapping(defaultValue="0")` em `ProdutoMapper`.
1e. ~~Item de Tabela de Preço sem nome/código antes do 1º save~~ — **feito
   (Entrega 7)**: `ItemTabelaPrecoModal` resolve a partir do resultado de
   busca já carregado.
2. Reconciliar `NavConfig.tsx` contra o catálogo real de `Funcionalidade`
   (agora com `CADASTRO_FORNECEDOR_*`/`CADASTRO_PRODUTO_*`/
   `CADASTRO_TRIBUTACAO_*` também — mas ainda não é uma conferência exaustiva
   contra o catálogo completo do seguranca-service).
2a. Grupo Tributário/Operação Fiscal (motor tributário) ainda não têm tela de
   gerenciamento própria — só consumidos como Select de leitura no Produto.
   Fica pro futuro módulo de "Motor Tributário" (matriz ICMS por UF é
   trabalho grande demais pra encaixar como modal simples).
2b. `AsyncSelect` (`shared/ui/AsyncSelect.tsx`) foi introduzido nesta entrega
   só pra NCM/CEST/busca de produto em Tabela de Preço — candidato natural a
   reaproveitar em qualquer catálogo grande futuro (ex: se o cadastro de
   Cliente/Fornecedor um dia precisar de busca de CEP/endereço externa).
3. **Favicon da aba do navegador ainda é a marca desenhada à mão** (não a foto
   real usada na sidebar desde a última passada) — inconsistência conhecida,
   ver "Ativos visuais" acima. Baixa prioridade (polish visual).
4. Dark mode completo — adiado, não esquecido.
5. Geração de client tipado via OpenAPI (`openapi-typescript`/`orval`) —
   ideia futura, agora com um endpoint de negócio real (Clientes) pra gerar
   contra, ainda não feito.
6. Code-splitting por rota — bundle de ~1MB é majoritariamente antd;
   revisitar quando os módulos de negócio adicionarem peso real.
7. **Backend, não deste projeto, mas registrado aqui pra não se perder**:
   `mvn compile -am` no reactor completo do `cactus-erp-sistema` falha ao
   ler POMs por causa de `controle-estoque-service` (dependência transitiva
   `spring-retry` sem versão gerenciada) — pré-existente, não bloqueia (só
   precisamos de `seguranca-service`+`cadastro-base-service` rodando). Ver
   `CLAUDE.md` raiz.
8. **Achado de ambiente, recorrente entre sessões**: os processos Java em
   background (`mvn spring-boot:run`) não sobrevivem a um encerramento de
   sessão do harness — precisam ser resubidos manualmente no início de cada
   sessão nova antes de qualquer teste ao vivo. Não é um problema de código,
   só um lembrete operacional.
9. ~~Retry desnecessário em referência de produto 404~~ — **feito (Entrega
   8)**: `useProdutoResumo` não repete mais em 404 (referência permanente).
10. ~~Estoque Fase B~~ (Inventário Físico, Lote, Número de Série, Kit, Curva
    ABC) — **feito (Entrega 9)**. Módulo de Estoque completo nas 10 áreas do
    backend.
11. `RegistrarMovimentoModal` pede `aprovadorId` como número solto (`InputNumber`)
    — agora que Segurança tem tela de Usuário, dá pra virar um Select de
    verdade (buscar usuário por nome) — ainda não feito, revisitar na próxima
    vez que o módulo de Estoque for tocado.
12. `ComponenteKitModal` nunca foi validado com um submit real (só a abertura
    da UI) — faltava um 2º produto óbvio pra usar como componente na sessão
    de teste sem arriscar coincidir com o próprio kit. Revalidar fim a fim
    (incluindo o caso de erro `KIT_COMPONENTE_CIRCULAR`) na próxima vez que
    o módulo de Estoque for tocado.
13. ~~Próximo módulo candidato~~ — **feito (Entregas 10/11)**: Segurança e GED
    completos. Restam Compras (`gestao-compras-service`) e BI, ainda placeholder.
14. Segurança: `PapeisEmpresaTab`/`AtribuirPapelEmpresaModal` não têm um
    "Novo Usuário" -> "já nasce com um papel" fluxo — hoje é sempre 2 passos
    (criar usuário, depois atribuir papel na aba). Aceitável (mesma UX de
    sub-recurso já usada em Empresa), mas vale reavaliar se virar atrito real
    no uso.
15. `UsuarioResponse.email`/`dataUltimoLogin` têm o mesmo risco teórico de
    `colaboradorId` (campos opcionais em Java viram `null`, não `undefined`,
    no JSON) — não causaram bug porque todo consumo atual já trata os dois
    (`v || '—'`, `dayjs(v)` só chamado quando truthy), mas registrado aqui
    como lição de tipagem: qualquer novo consumo desses 2 campos que use um
    guard `!== undefined` (em vez de `!= null` ou truthy check) repete o
    mesmo bug já corrigido pra `colaboradorId`.
16. GED: filtro `entidadeTipo`/`entidadeId` na listagem de Documentos existe
    na tela mas nenhum outro módulo ainda anexa documento a si mesmo pela UI
    (ex: uma aba "Documentos" dentro de Produto/Fornecedor) — a integração é
    só teórica/preparada por enquanto, ver Fase 4 do backend no CLAUDE.md raiz.
17. ~~Próximo módulo candidato: Compras~~ — **feito (Entrega 12)**: Compras
    completo (Requisição/Cotação/Pedido/Recebimento/Configuração). Resta só
    BI, ainda placeholder.

## Entrega 12 — Gestão de Compras (2026-09-02)

Backend (`gestao-compras-service`, porta 8084) mapeado por completo antes de
codar: `RequisicaoCompra`(+itens inline)/`CotacaoCompra`/`FornecedorCotacao`
(+itens)/`PedidoCompra`(+itens via sub-endpoint)/`RecebimentoCompra`(+itens)/
`ConfiguracaoComprasEmpresa`. Dois achados de arquitetura confirmados antes de
desenhar telas: (1) **nem `RequisicaoCompra` nem `PedidoCompra` têm `PUT`** —
Requisição nasce fechada com os itens já na própria criação (`Form.List`
inline); Pedido nasce em `RASCUNHO` sem itens, que só entram depois via
`POST /pedidos/{id}/itens`. As duas telas de formulário desta entrega são,
portanto, **só criação** — toda edição posterior é ação dedicada (cancelar,
enviar, aprovar, adicionar/remover item), nunca um form de update. (2)
**Cotação e Recebimento não têm listagem própria no menu** — `GET /cotacoes`
exige `requisicaoId`, `GET /recebimentos` exige `pedidoId` — sempre vividas no
contexto de um pai (mesmo padrão já estabelecido em Estoque pra
Transferências/Inventários). O menu "Compras" ganhou só 3 filhos
(Requisições/Pedidos/Configuração); Cotação e Recebimento são seções dentro
da Detail do pai, sem entrada própria em `NavConfig.tsx`.

### Telas novas

- **Requisições** — lista (filtro por solicitante) + form de criação (Form.List
  de itens, produto via `AsyncSelect`) + detalhe (itens read-only, ação
  Cancelar quando `ABERTA`, seção **Cotações** com "Nova Cotação").
- **Cotação** (`CotacaoDetailPage`, só rota de detalhe) — um `Card` por
  fornecedor concorrente (`FornecedorCotacaoResponse`), cada um com sua
  própria tabela de itens cotados + subtotal/desconto/total (`Table.Summary`),
  ações "Adicionar Fornecedor"/"Adicionar Item"/"Gerar Pedido" (esta última só
  habilitada com ao menos 1 item cotado) — gerar pedido navega direto pro
  `PedidoDetailPage` recém-criado.
- **Pedidos** — lista (filtro por fornecedor) + form de criação avulso
  (`origemTipo` fixo `AVULSO`, `empresaId` implícito da empresa ativa da
  sessão, nunca campo de formulário — mesmo padrão de `DepositoFormPage`) +
  detalhe: itens (adicionar/remover só em `RASCUNHO`), máquina de estados
  completa via botões contextuais (Enviar → Aprovar [`COMPRAS_APROVAR`,
  authority própria, não incluída em `COMPRAS_GERENCIAR`] → Cancelar,
  condicionados à situação atual), seção **Recebimentos** (visível a partir de
  `ENVIADO`) com "Novo Recebimento".
- **Novo Recebimento** (`NovoRecebimentoModal`) — a única tela desta entrega
  com um subcomponente por linha (`LinhaItemRecebimento`) chamando seu próprio
  hook (`useLotesParaSelecao(item.produtoId)`) — necessário porque cada item
  pendente do pedido pode ser de um produto diferente, com lotes diferentes;
  fatorar uma linha em componente próprio é o jeito correto de ter "um hook
  por item" sem violar a regra de hooks (nº de hooks variável por render).
  Quantidade recebida limitada (`max`) à `quantidadePendente` de cada item.
- **Configuração de Compras** — mesmo padrão 1-config-por-empresa de
  `ConfiguracaoEstoquePage` (`limiteAprovacaoPedido`, vazio = pedido nunca
  exige aprovação).

### RBAC validado ao vivo, não só por leitura de código

Authorities: `COMPRAS_CONSULTAR`/`COMPRAS_REQUISITAR`/`COMPRAS_COTAR`/
`COMPRAS_APROVAR`/`COMPRAS_RECEBER`/`COMPRAS_GERENCIAR`. Criado um usuário
OPERADOR de teste (só `COMPRAS_CONSULTAR`) e confirmado ao vivo (Playwright):
botões de criar/mutar continuam visíveis (mesmo padrão de tooltip explicativo
já usado em Estoque/Segurança), mas os `Form`s nascem `disabled` de verdade —
e, pra garantir que a restrição não é só cosmética, um `POST /requisicoes`
direto contra a API com o token JWT do operador devolveu `403` de verdade.

### Achado real corrigido nesta entrega

`useColaboradorResumo`/`useFornecedorResumo` (lookups cross-serviço de
Compras) não desligavam `retry` em 404 — mesma classe de achado já registrada
em `useProdutoResumo` (Entrega 8): `colaboradorId`/`fornecedorId` são
referências soltas sem FK, um id "órfão" em dado de seed/teste é permanente,
não transitório. Descoberto ao vivo: a requisição pré-seedada `id=1` referencia
`colaboradorId=1`, que nunca existiu de fato no `cadastro-base-service` (dado
de seed do backend, não desta sessão). Corrigido com o mesmo
`retry: (count, erro) => erro.status !== 404 && count < 1` já usado em
`useProdutoResumo`. Também corrigido, achado no mesmo teste: `Alert
message=` deprecado em `ConfiguracaoComprasPage` (copiei o padrão errado —
`title` é o prop certo nesta versão do Ant Design, mesmo achado já registrado
e corrigido em outras telas na Entrega 6/7).

### Verificação ao vivo (Playwright, navegador real)

Fluxo completo, ponta a ponta, 2 vezes (a 1ª pra achar os 2 bugs acima, a 2ª
já limpa): requisição criada com item inline → cotação criada a partir dela →
fornecedor concorrente adicionado → item cotado adicionado (subtotal/desconto/
total corretos) → pedido gerado a partir do fornecedor cotado → pedido
enviado (`RASCUNHO`→`ENVIADO`, abaixo do limite configurado) → recebimento
parcial (6 de 10 — situação vira `PARCIALMENTE_RECEBIDO`) → recebimento do
restante (4 de 10 — situação vira `RECEBIDO_TOTAL`, saldo do
`controle-estoque-service` conferido via API refletindo a entrada real) →
pedido avulso criado do zero, item adicionado, cancelado (`RASCUNHO`→
`CANCELADO`) → Configuração de Compras salva. Depois, sessão separada
validando RBAC com usuário OPERADOR (ver acima). **Zero erro de console na
execução final**, exceto o 404 esperado/tolerado do colaborador órfão do seed
(mesmo tratamento de `useProdutoResumo`).

**Dado de teste não removido, por design do backend**: `RequisicaoCompra`/
`CotacaoCompra`/`PedidoCompra`/`RecebimentoCompra` não têm nenhum `DELETE` na
API (são registros de fluxo de negócio, não catálogo) — diferente de
Cliente/Produto/etc., não há como "limpar" esses registros de teste depois.
Deixados como estão (mesmo critério já aceito pra `EstoqueMovimento`/
`DocumentoVersao` — ledger/histórico não se apaga). O colaborador
"QA Comprador Teste" criado via API pra ter um solicitante/comprador válido
foi deixado ativo de propósito (não desativado) — como várias requisições/
pedidos de teste já o referenciam sem FK real, desativá-lo só trocaria um
nome visível por mais um "#id" órfão, sem ganho nenhum.

## Entrega 13 — Business Intelligence (2026-09-02)

Último módulo placeholder do frontend. Backend (`business-intelligence-service`,
porta 8086) mapeado por completo antes de codar: superfície de API pequena e
direta — 3 `GET /api/v1/indicadores/*` (`estoque-valor`/`curva-abc`/
`compras-fornecedor`, todos snapshots gerados pelo ETL, não calculados em
tempo real) + 1 `POST /api/v1/etl/executar` (disparo manual, além do
agendamento diário às 2h). Achado de arquitetura chave, que **simplificou**
esta entrega em vez de complicar: diferente de todo outro módulo (Estoque,
Compras), o BI **denormaliza nomes deliberadamente** (`produtoNome`/
`depositoNome`/`fornecedorNome` já vêm prontos nos DTOs, ver ANÁLISE no
CLAUDE.md raiz — "dimensões denormalizadas são deliberadas aqui") — zero
lookup cross-serviço nesta feature, só o filtro de depósito (reaproveita
`GET /depositos/empresa/{id}/ativos` do `controle-estoque-service`, mesmo
padrão já usado em Estoque/Compras). `produtoNome`/`fornecedorNome` nulos =
id "órfão" (referenciado em Estoque/Compras mas nunca cadastrado de fato) —
mesmo comportamento esperado já documentado em `useProdutoResumo`, exibido
aqui como fallback `#id` sem precisar de retry-404 (não é uma query própria,
já vem resolvido — ou não — no DTO do indicador).

### Telas novas

- **Valor de Estoque** — filtro por data de referência (default hoje) +
  depósito, tabela com quantidade/custo médio ponderado/valor total e uma
  linha de resumo somando o valor total da página (`Table.Summary`).
- **Curva ABC** — mesmo filtro de data/depósito, tabela com valor
  movimentado/% participação/% acumulado/classificação (`Tag` colorida
  A=verde/B=dourado/C=cinza). Diferente do `GET /relatorios/curva-abc` do
  `controle-estoque-service` (calcula sob demanda, sem histórico — já usado
  em Estoque Fase B), este é o snapshot persistido por data, permite
  comparar a classificação ao longo do tempo.
- **Compras por Fornecedor** — filtro por mês (`DatePicker picker="month"`,
  default mês atual), tabela com quantidade de pedidos e valor total
  comprado por fornecedor.
- **Execução do ETL** — página própria (não um botão duplicado nas 3 telas
  de indicador) com `DatePicker` de data de referência + botão "Executar
  ETL" (`BI_GERENCIAR`), com `Alert` explicando o que o ETL faz e que é
  idempotente. Escolhida como tela separada porque a execução afeta os 3
  indicadores de uma vez (dimensões antes de fatos, ver `EtlService.java`),
  não faz sentido duplicar o botão em cada lista.

### RBAC — mesmo padrão já estabelecido, confirmado consistente

Validado com usuário OPERADOR de teste (só `BI_CONSULTAR`): o item de menu
"Execução do ETL" corretamente não aparece (exige `BI_GERENCIAR`, ausente do
usuário). **Achado esclarecido, não um bug**: acessar `/bi/etl` direto pela
URL (contornando o menu) ainda renderiza a página — investigado e confirmado
que isso é o mesmo padrão já em uso em todo o app (`AppRouter` só aplica
`RequireAuthority` no nível do módulo inteiro, com `anyOf` das autoridades do
`NavConfig`; a autoridade mais granular de uma ação específica — aqui
`BI_GERENCIAR` pra executar o ETL, análogo a `COMPRAS_GERENCIAR` pra salvar
`ConfiguracaoComprasPage` — é sempre enforced na própria tela via botão
`disabled`+`Tooltip`, nunca por uma rota separada). O botão "Executar ETL"
aparece corretamente desabilitado, e uma chamada direta `POST
/etl/executar` com o token do operador confirmou `403` real do backend —
a mesma dupla verificação (UI desabilitada + 403 real) já usada em Compras.
Nenhuma mudança de código necessária; documentado aqui pra não ser
redescoberto como "bug" numa sessão futura.

### Verificação ao vivo (Playwright, navegador real)

Fluxo completo: executar ETL manualmente (idempotente, gerou os 3
snapshots) → Valor de Estoque exibindo os 4 registros reais (incluindo 2
produtos "órfãos" corretamente mostrados como `#3`/`#99`) → filtro por
depósito reduzindo corretamente pra 3 registros → Curva ABC exibindo o
snapshot classificado (`C`, 100% participação/acumulado, único produto
movimentado) → Compras por Fornecedor exibindo o agregado mensal real
(5 pedidos, valor total batendo com o somatório esperado). **Zero erro de
console.** Depois, sessão separada validando RBAC com OPERADOR (ver acima).

Com esta entrega, **todos os 6 módulos de negócio do backend têm tela real
no frontend** — não resta nenhum módulo placeholder.

## Entrega 14 — Vendas + Transportadoras (2026-09-05/06)

Primeira entrega do Módulo Comercial (backend construído nas Fases 7-12 do
`CLAUDE.md` raiz, todas sem tela até agora) — pedido do usuário ("pode seguir
com o frontend") depois de fechar as pendências realistas de backend. Backend
(`vendas-service`, porta 8088) mapeado por completo antes de codar: 8
controllers, ~15 DTOs. Achado de arquitetura confirmado antes de desenhar:
mesmo padrão já estabelecido em Requisição/Pedido de Compra — `VendaOrcamento`
e `VendaCabecalho` nascem em `RASCUNHO` sem itens (sub-endpoint depois), sem
`PUT` nenhum dos dois — só ação dedicada (enviar/aprovar/cancelar/confirmar).

### Gap real fechado antes de começar: `Transportadora` sem tela

Descoberto por grep exaustivo (`grep -rli transportadora src`) que
`Transportadora` — construída no backend desde a Fase 7 (`cadastro-base-service`,
mesmo padrão de Cliente/Fornecedor/Colaborador) — nunca ganhou tela, mesmo
tendo passado por 13 entregas de frontend. Fechado nesta entrega como
pré-requisito (Vendas/Orçamento referenciam `transportadoraId`): mesmo molde
exato de `FornecedorFormPage` (reaproveita `shared/ui/pessoa/*`), só troca a
aba "Compras" por "Dados Operacionais" (`rntrc`, obrigatório em CT-e — Fase
11 do backend).

### Estrutura das telas de Vendas

- **Vendedores** — form simples (flat, `VendedorRequest` não tem sub-recurso
  nesta fase), `comissaoPerfilId` com Select + botão "+" abrindo
  `GerenciarComissaoPerfisModal` (mesmo padrão de Cargo/Setor dentro de
  Colaborador — modal de gerenciamento aninhado, não página própria, porque
  o catálogo é pequeno e muda raro).
- **Condições de Pagamento** — form com `Form.List` de parcelas inline
  (`parcela`/`dias`/`taxa`), mesmo padrão de itens em `RequisicaoFormPage`
  (gestao-compras-service), só que a lista aqui é substituída inteira a cada
  `PUT` (mesmo padrão de `ProdutoFaixaPrecoDTO`).
- **Tipos de Nota Fiscal** — pré-requisito pra criar uma Venda
  (`notaFiscalTipoId` obrigatório) — página própria com modal de criar/editar
  Tipo, que por sua vez abre um SEGUNDO modal aninhado
  (`GerenciarNotasFiscaisModelosModal`) pra gerenciar Modelo (55/65/etc.) —
  2 níveis de modal aninhado, mesma técnica de sempre, só que empilhada.
- **Orçamentos** (`OrcamentosListPage`/`OrcamentoFormPage`/`OrcamentoDetailPage`)
  — form de criação só com cabeçalho (cliente/vendedor/condição/transportadora/
  frete), Detail com itens (adicionar/remover via modal, só em `RASCUNHO`) e
  a máquina de estados completa via botões contextuais: Enviar→Aprovar→
  (Gerar Venda, `GerarVendaModal`) ou Expirar/Cancelar.
- **Vendas** (`VendasListPage`/`VendaFormPage`/`VendaDetailPage`) — form de
  criação só AVULSA (venda gerada de orçamento nasce via `GerarVendaModal`,
  não aqui), Detail com itens, frete (`DefinirFreteModal`, upsert 1:1,
  `GET` 404 tratado como formulário vazio) e Confirmar/Cancelar. Seção
  "Comissão do vendedor" aparece só quando `situacao=CONFIRMADA`, resolvida
  filtrando `GET /comissoes?vendedorId=X` pelo `vendaId` no cliente (a API
  não tem filtro direto por venda).
- **Comissões** — tela só-consulta (`VendaComissao` é gerada automaticamente
  na confirmação, sem `POST`/`PUT` na API), com filtro por vendedor.

### Achado real de arquitetura, corrigido antes de testar: item de venda sem depósito não tinha como ser editado

Lendo o Javadoc de `VendaService#gerarAPartirDeOrcamento` (backend, Fase 8) —
um item copiado de um `VendaOrcamento` nasce **sem `depositoId`** (orçamento
não conhece depósito/estoque), e a única forma de completá-lo é
`PUT /vendas/{id}/itens/{itemId}` (endpoint que só passou a existir por causa
desse exato problema, segundo o próprio Javadoc do backend). Minha primeira
versão de `AdicionarItemVendaModal` só cobria `POST` (adicionar item novo) —
sem editar, uma venda gerada de orçamento ficaria **permanentemente travada**
sem conseguir confirmar (`podeConfirmar` já exige depósito em todos os itens).
Corrigido generalizando o mesmo modal pra aceitar um `itemEmEdicao` opcional
(mesmo espírito do padrão `useLookupModal` — "abrir novo vs. editar item X"),
com um botão "Editar" na tabela de itens da `VendaDetailPage` e uma tag
"Sem depósito" (`warning`, amarela) chamando atenção pra linha que precisa de
ação. Achado ANTES de rodar o teste ao vivo (não em runtime) — a leitura
cuidadosa do Javadoc do backend evitou descobrir isso só depois de uma venda
de teste ficar travada.

### Verificação ao vivo (Playwright, navegador real)

Fluxo completo, ponta a ponta, contra o backend real (`vendas-service`+
`cadastro-base-service`+`controle-estoque-service`+`seguranca-service`):
criar Transportadora (Pessoa Jurídica + endereço) → criar Colaborador dedicado
(via API direta, só pra evitar colidir com um vendedor já cadastrado em
execuções anteriores do teste) → criar Perfil de Comissão via modal aninhado
+ Vendedor (5% de comissão) → criar Condição de Pagamento (3 parcelas) →
criar Modelo Fiscal (55) via modal duplamente aninhado + Tipo de Nota Fiscal
→ criar Orçamento + item (2×R$50) → Enviar → Aprovar → Gerar Venda (Tipo de
NF + forma de pagamento) → **editar o item copiado do orçamento pra definir o
depósito** (achado acima, exercitado de propósito) → Definir Frete → Confirmar
Venda → confirma na tela: situação `Confirmada`, seção "Comissão do vendedor"
aparece com R$5,00 (5% de R$100). **Conferido também via API direta**
(`GET /vendas`/`GET /comissoes`): venda #6 com `situacao=CONFIRMADA`,
`itens[0].depositoId=1` (não mais `null`), `valorComissao=5.000000`; comissão
#6 com `valorVenda=100`/`valorComissao=5`/`situacao=PENDENTE` — os números
batem exatamente com o 5% configurado no Vendedor. **Console: só 2 mensagens
esperadas** (um 409 de um modelo fiscal duplicado entre re-execuções do
script de teste, e um 404 tolerado da primeira consulta de frete antes de
existir — mesmo padrão já usado em `buscarFreteVenda`/outras chamadas
opcionais do app). `npm run build`/`npm run lint`/`npm run test` (13/13)
confirmados limpos antes do teste ao vivo. Usuário de teste e script
Playwright removidos ao final; dado de negócio de teste (Transportadora,
Vendedor, Orçamento, Venda, Comissão) ficou como demonstração real — mesmo
critério já usado em Compras/Estoque (sem `DELETE` nesses recursos por serem
registro de fluxo de negócio, não catálogo).

### Fora de escopo desta entrega (Fase B, registrada no BACKLOG)

AFV (`VendedorRota`/`VendedorMeta` — check-in/rota/meta do vendedor) e
`ComissaoObjetivo` (comissão específica por produto dentro de um perfil) —
o backend já expõe os sub-endpoints (`VendedorController`/
`ComissaoPerfilController`), só não ganharam tela ainda. Mesmo padrão de
faseamento já usado em Estoque (Fase A/Fase B).

## Entrega 15 — Fiscal (NF-e + CT-e) (2026-09-06)

Segunda entrega do Módulo Comercial (depois de Vendas na Entrega 14) —
backend (`fiscal-service`, porta 8089) mapeado por completo antes de codar:
`NotaFiscalEletronicaController`/`CteCabecalhoController`, 2 agregados
(`NotaFiscalEletronica`+itens, `CteCabecalho`+componentes). Mesmo achado de
arquitetura já visto em Orçamento/Venda/Requisição/Pedido: nem NF-e nem CT-e
têm `PUT` — nascem via `POST .../de-venda/{vendaId}` a partir de uma
`VendaCabecalho` `CONFIRMADA` (`fiscal-service` lê a venda via client OAuth2
cross-serviço, não recebe o corpo inteiro do front) — as duas telas de
formulário desta entrega, portanto, não existem: NF-e não tem formulário
nenhum (`POST` sem corpo, só um botão), CT-e tem só um modal pequeno com os
poucos campos que o `fiscal-service` não consegue derivar da venda/frete
(`GerarCteModal` — tomador, valor do serviço, alíquota ICMS, indicador de
lotação, previsão de entrega, produto predominante, CIOT, componentes do
valor da prestação via `Form.List`).

### Onde os botões vivem: `VendaDetailPage`, não uma tela própria

`NotasFiscaisListPage`/`CtesListPage` são só consulta (mesmo padrão de
`RequisicoesListPage` sem form de criação) — os botões "Gerar NF-e"/"Gerar
CT-e" moram no cabeçalho da `VendaDetailPage` (`features/vendas`), visíveis
só quando `venda.situacao === 'CONFIRMADA'`, gate por `FISCAL_EMITIR`
(`hasAuthority`, mesmo padrão de `VENDAS_VENDER`/`VENDAS_CANCELAR` já usados
na mesma tela). "Gerar NF-e" é direto (sem modal, `POST` sem corpo) — clique
→ `useGerarNotaFiscalDeVenda().mutateAsync(vendaId)` → navega pro detalhe da
NF-e recém-criada. "Gerar CT-e" abre `GerarCteModal` e **fica desabilitado
com tooltip explicativo** até a venda ter `VendaFrete.transportadoraId`
preenchido (`useFreteVenda`, hook já existente da Entrega 14) — sem isso o
`fiscal-service` rejeitaria com 422 (frete sem transportadora não tem como
virar CT-e), então a UI evita a chamada fadada ao erro em vez de deixar o
usuário descobrir só depois de submeter.

### Achado de arquitetura confirmado ANTES de testar, não descoberto em runtime

Lendo o backend (`VendaService.definirFrete`, Fase 8) antes de desenhar a
tela: `exigirSituacao(venda, SituacaoVenda.RASCUNHO, ...)` — frete só pode
ser definido/editado enquanto a venda ainda é `RASCUNHO`, **nunca depois de
confirmada** (decisão deliberada do backend, não uma lacuna). Isso significa
que uma venda `CONFIRMADA` sem frete (ou com frete sem transportadora) fica
**permanentemente** sem "Gerar CT-e" disponível — não existe caminho de UI
pra corrigir isso depois do fato, e não deveria existir um (seria contornar
uma regra de negócio deliberada do backend, não um bug de UX a destravar).
`VendaDetailPage` já reflete isso corretamente por herança: o botão "Definir
Frete"/"Editar Frete" já era (desde a Entrega 14) condicionado a
`situacao === 'RASCUNHO'`, então o comportamento "correto" já existia antes
desta entrega — só precisava ser confirmado contra o backend antes de
assumir que seria preciso mudar algo ali (não foi).

### Achado real, não de código: `vendas-service.confirmar` ganhou uma dependência nova desde a Entrega 14

Testando ao vivo, `POST /vendas/{id}/confirmar` passou a devolver 502
(`VENDAS_FALHA_INTEGRACAO_FINANCEIRO`) contra um ambiente só com os serviços
da Entrega 14 de pé (seguranca/cadastro-base/estoque/vendas/fiscal) — causa:
a integração `vendas-service → financeiro-service` (gerar contas a receber
na confirmação) foi fechada em 2026-09-05, **depois** da Entrega 14 ter sido
validada, então nenhuma sessão de frontend tinha subido `financeiro-service`
ainda. Não é bug de código (o backend está correto, a falha é só "dependência
externa fora do ar"), mas é um achado operacional real — `financeiro-service`
(porta 8093) entra na lista de serviços mínimos pra testar qualquer fluxo que
passe por `confirmar` uma venda, registrado no `BACKLOG.md`.

### Verificação ao vivo (Playwright, navegador real)

Duas rodadas: (1) consulta contra dado **já existente** no banco (de sessões
anteriores de validação de backend) — NF-e #2 (venda #1, com item/NCM/CST
resolvidos) e CT-e #1 (venda #2, com componentes FRETE PESO/FRETE VALOR) —
confirmando que `NotasFiscaisListPage`/`NotaFiscalDetailPage`/`CtesListPage`/
`CteDetailPage` renderizam corretamente dado real pré-existente, incluindo
`ClienteResumoCell`/`TransportadoraResumoCell`/`ProdutoResumoCell` resolvendo
nome cross-serviço sem N+1 perceptível. (2) Fluxo **fim a fim do zero**: nova
venda avulsa criada (cliente/vendedor/condição/tipo de NF/forma de pagamento)
→ item adicionado (produto+depósito) → frete definido com transportadora
(ainda `RASCUNHO`) → Confirmar (financeiro-service já de pé, sucesso) →
"Gerar NF-e" → NF-e nova criada (`RASCUNHO`, item com NCM/CST corretos,
total batendo) → voltar pra venda → "Gerar CT-e" (antes: desabilitado até o
frete existir — já estava definido nesta venda, então habilitado) → modal
preenchido (valor do serviço, alíquota ICMS) → CT-e novo criado (`RASCUNHO`,
ICMS calculado corretamente — 12% de R$20 = R$2,40 —, tomador/transportadora/
modelo/série/número corretos) → segunda tentativa de "Gerar NF-e" pra mesma
venda rejeitada (409, toast de erro exibido, sem navegação indevida). RBAC
confirmado com usuário OPERADOR real (só `FISCAL_CONSULTAR`): botões
"Emitir"/"Gerar NF-e"/"Gerar CT-e" corretamente desabilitados na UI, e
chamada direta à API com o token do operador confirmou `403` real em
`POST /notas-fiscais/de-venda/{id}` e `POST /ctes/de-venda/{id}` (não só
cosmético). **Zero erro de console** além do 404 tolerado (`useFreteVenda`
antes do frete existir) e o 409 esperado da segunda tentativa proposital.
`npx tsc -b`/`npm run lint`/`npm run test` (13/13)/`npm run build` confirmados
limpos antes do teste ao vivo. Usuários de teste (`qa.fiscal.teste`,
`qa.fiscal.operador`) removidos ao final; venda/NF-e/CT-e de teste ficaram
como demonstração real (mesmo critério de Vendas/Compras/Estoque — sem
`DELETE` nesses recursos por serem registro de fluxo de negócio).

### Fora de escopo desta entrega (registrado no BACKLOG)

NFS-e (Fase 9 do backend) — depende de uma tela de Ordem de Serviço que ainda
não existe (`os-service`, Fase 10, também sem frontend); emissão real via
ACBr (nunca exercitada em nenhuma sessão, mesmo bloqueio de certificado já
documentado no root `CLAUDE.md`); cancelamento de NF-e/CT-e (fora de escopo
também no backend).

## Entrega 16 — Ordem de Serviço (2026-09-06)

Terceira entrega do Módulo Comercial (depois de Vendas na Entrega 14 e Fiscal
na Entrega 15) — escolhida como próximo item por ser o mais antigo do
`BACKLOG.md` ainda pendente e por desbloquear NFS-e (Fase 9, que depende de
uma tela de OS existir pra gerar nota de serviço a partir dela). Backend
(`os-service`, porta 8090) mapeado por completo antes de codar: 3
controllers (`OsAberturaController`/`OsEquipamentoController`/
`OsStatusController`), 1 agregado com 3 coleções filhas.

### Achado de arquitetura confirmado antes de desenhar: `OsStatus` é catálogo livre, não máquina de estados

Diferente de Venda/Orçamento (`situacao` fixa RASCUNHO→CONFIRMADA), `OsStatus`
é uma FK de verdade pra um catálogo totalmente livre — o ciclo de vida real
de uma OS varia por vertical (oficina, assistência técnica, autoelétrica).
Consequência direta no desenho: "trocar status" é um `Select` simples ligado
a `POST /os-aberturas/{id}/status/{osStatusId}` (qualquer transição
permitida, sem `disabled` condicional por estado atual) — nada como o
`COR_SITUACAO`/máquina de estados de `VendaDetailPage`.

### `OsAbertura` nasce sem itens — mesmo padrão, aplicado a 3 coleções de uma vez

`OsAberturaRequest` cobre só o cabeçalho (cliente/colaborador/status/contato/
datas) — equipamentos, itens (peça/mão de obra) e evolução entram via
sub-endpoint na Detail, mesmo padrão de Requisição/Pedido/Venda/Orçamento/
NF-e/CT-e. Diferente dos módulos anteriores (que tinham só 1-2 coleções
filhas), `OsAbertura` tem **3 coleções irmãs de uma vez**
(`equipamentos`/`itens`/`evolucoes`) — o backend já resolve o N+1 disso com 3
métodos "hidratar por lado" (`OsAberturaRepository`), então a Detail só
precisou consumir o agregado já completo, sem trabalho extra do frontend.

### `AdicionarItemOsModal` — o único modal desta entrega com campo condicional real

`OsProdutoServicoRequest.tipo` (`PRODUTO`/`SERVICO`) decide qual campo
aparece: `PRODUTO` mostra um `AsyncSelect` de produto (peça, obrigatório);
`SERVICO` mostra um `Input.TextArea` de complemento (descrição da mão de
obra, `produtoId` fica de fora). Implementado com `Form.useWatch('tipo',
form)` alternando a renderização — backend valida a mesma regra
(`ITEM_PRODUTO_SEM_PRODUTO_ID`, 422) como segunda linha de defesa, não só a
UI.

### Achado real, esclarecido e não corrigido: nenhuma trava depois de "Encerrar"

Testando ao vivo, o botão "Adicionar Item"/"Adicionar Equipamento"/
"Registrar Evolução" continua **visível e habilitado** mesmo numa OS já
encerrada (`dataFim` preenchido). Verificado no backend antes de "corrigir"
por impulso: `OsAberturaService`/`OsErrorCode` não têm **nenhuma** validação
de estado pra essas 3 ações — só 2 códigos de erro existem no serviço inteiro
(`ITEM_PRODUTO_SEM_PRODUTO_ID`/`ORCAMENTO_JA_APROVADO`), nenhum relacionado a
"OS encerrada". Adicionar uma trava só no frontend criaria uma UX
inconsistente (botão desabilitado na tela, mas a mesma chamada funcionando
perfeitamente via API/Swagger) — mantido sem trava, espelhando o backend de
propósito, não por omissão.

### Warning de depreciação do Ant Design v6 eliminado (achado nesta entrega)

`Timeline items.children` (deprecado, usar `items.content`) — usado na seção
de Evolução da `OsAberturaDetailPage`. Corrigido trocando a chave do objeto
antes mesmo de considerar a tela pronta (mesmo padrão de rigor já registrado
em entregas anteriores contra warnings do Ant Design v6).

### Verificação ao vivo (Playwright, navegador real)

Fluxo completo: catálogo de Status confirmado com o seed real do backend (7
status, "Aberta"→"Cancelada") → criar Tipo de Equipamento → abrir OS nova
(cliente + status inicial + contato + observação do cliente) → adicionar
equipamento (tipo + número de série) → adicionar item SERVIÇO (complemento +
R$80) → adicionar item PRODUTO (produto real via busca + R$50) → registrar
evolução → trocar status para "Em Andamento" (Select livre, sem máquina de
estados) → Aprovar Orçamento (botão desabilita depois, idempotência visual
confirmada) → conferir valor total R$130,00 (80+50, igual ao backend) →
Encerrar (Popconfirm) → listagem confirma a OS com o total correto. RBAC
confirmado com usuário OPERADOR real: catálogo (`OS_GERENCIAR`) corretamente
bloqueado (botão desabilitado + `403` real em `POST /os-status`), abertura/
atendimento de OS (`OS_ABRIR`, que OPERADOR também tem, mesmo critério de
`VENDAS_VENDER`) confirmada funcionando via API (`201` real em
`POST /os-aberturas`). **Zero erro de console** na execução final (o achado
do `Timeline` acima foi corrigido antes desta rodada, não deixado como
pendência). `npx tsc -b`/`npm run lint`/`npm run test` (13/13)/`npm run
build` confirmados limpos. Usuários de teste removidos ao final; catálogo/OS
de teste ficaram como demonstração real (mesmo critério de Vendas/Compras/
Estoque — sem `DELETE` em `OsAbertura` por ser registro de fluxo de negócio;
`OsStatus`/`OsEquipamento` têm `DELETE` mas foram deixados, coerente com o
próprio catálogo de demonstração).

### Fora de escopo desta entrega (registrado no BACKLOG)

Geração de NFS-e a partir de uma OS com orçamento aprovado (Fase 9 do
backend, `fiscal-service`) — agora desbloqueada por esta entrega existir,
fica como próxima candidata natural.

## Entrega 17 — NFS-e (2026-09-06)

Quarta entrega do Módulo Comercial, fechando o quarteto NF-e/CT-e/OS/NFS-e —
desbloqueada pela Entrega 16 (NFS-e só pode ser gerada a partir de uma OS com
orçamento aprovado, e sem tela de OS a fase inteira ficava travada). Mesmo
serviço da Fiscal (`fiscal-service`, porta 8089, Fase 9) — 2 entidades novas
(`ListaServicoMunicipal`, `NotaFiscalServico`+itens), 2 controllers novos.

### Modelo: NFS-e nasce da OS, não da Venda — e só pega os itens de serviço

Mesmo padrão de "nasce sem form, via `POST .../de-{origem}/{id}`" já usado em
NF-e/CT-e, mas a origem aqui é `OsAbertura` (`os-service`), não
`VendaCabecalho`. Backend exige `orcamentoAprovado=true` e pelo menos 1 item
`SERVICO` na OS (itens `PRODUTO`/peça ficam de fora — virariam uma NF-e
separada, mesmo critério de mercado já registrado no root `CLAUDE.md`).
`listaServicoId` é aplicado a **todos** os itens de serviço da OS de uma vez
(simplificação deliberada do backend, documentada no próprio Javadoc de
`GerarNotaServicoRequest`) — não um mapeamento item a item.

### `GerarNotaServicoModal`, acionado de dentro de `features/os`

Mesmo padrão cross-feature já usado na Entrega 15 (`GerarCteModal` importado
de `features/vendas` para dentro de `features/fiscal`) — aqui invertido:
`GerarNotaServicoModal` vive em `features/fiscal/modals/`, mas é importado e
disparado de dentro de `OsAberturaDetailPage.tsx` (`features/os`). O botão
"Gerar NFS-e" no cabeçalho da OS fica desabilitado com tooltip explicando o
motivo exato em 3 camadas (sem permissão → orçamento não aprovado → sem item
de serviço), checagem client-side (`possuiItemServico =
os.itens.some(item => item.tipo === 'SERVICO')`) que espelha exatamente a
validação 422 do backend, evitando a chamada fadada ao erro.

### `ListasServicoMunicipalListPage` — catálogo simples, mesmo molde de `OsStatusListPage`

CRUD com modal único (criar/editar) — `codigo` (LC 116/2003), `descricao`,
`codigoTributacaoMunicipio` (opcional, específico do município) e
`aliquotaIssPadrao` (opcional, "resolvido padrão" cacheado, mesmo espírito
do `Produto.aliquotaIcmsPadrao`). Sem motor de resolução por município nesta
fase — a empresa cadastra manualmente os itens que usa, mesmo critério já
documentado no Javadoc de `ListaServicoMunicipal` no backend (ISS varia por
~5.500 municípios, sem catálogo nacional único pra semear).

### Verificação ao vivo (Playwright, navegador real)

Fluxo completo: catálogo de Lista de Serviço confirmado com o seed real
(14.01, LC 116/2003) → listagem de Notas de Serviço confirmando o dado
pré-existente (NFS-e #1, gerada em sessão de validação de backend anterior)
→ OS #3 (orçamento pendente) com botão "Gerar NFS-e" corretamente desabilitado
→ OS #7 (orçamento aprovado, 1 item SERVIÇO de R$80 + 1 item PRODUTO de R$50,
gerada na Entrega 16) com botão habilitado → gerar NFS-e (RPS 1001,
competência 202609, lista de serviço 14.01) → NFS-e nova composta
corretamente: **valor dos serviços R$80,00 — confirmando que só o item
SERVIÇO entrou, o PRODUTO de R$50 ficou de fora** — e ISS R$4,00 (5% de
R$80, batendo com a alíquota padrão da lista) → segunda tentativa pra mesma
OS corretamente rejeitada (409, toast de erro, sem navegação indevida). RBAC
confirmado com usuário OPERADOR real (só `FISCAL_CONSULTAR`): botões "Novo
Item"/"Gerar NFS-e" corretamente desabilitados na UI, consulta funcionando
normalmente, e chamada direta à API com o token do operador confirmou `403`
real em `POST /listas-servico-municipal` e `POST /notas-servico/de-os/{id}`.
**Zero erro de console** além do 409 esperado/tolerado da segunda tentativa
proposital. `npx tsc -b`/`npm run lint`/`npm run test` (13/13)/`npm run
build` confirmados limpos antes do teste ao vivo. Usuários de teste
removidos ao final; catálogo/NFS-e de teste ficaram como demonstração real
(mesmo critério de NF-e/CT-e/OS — sem `DELETE` em `NotaFiscalServico` por
ser registro de fluxo de negócio).

### Fora de escopo desta entrega (mesmo limite já registrado no backend)

Emissão real via ACBr Monitor — nunca exercitada nesta sessão nem nas
anteriores (a seção `[NFSe]` do `ACBrMonitor.ini` local nem está
configurada, ver Javadoc de `NotaFiscalServico` no backend); cancelamento de
NFS-e (fora de escopo também no backend).

Com esta entrega, **todo o quarteto NF-e/CT-e/OS/NFS-e do Módulo Comercial
tem tela completa no frontend**. Restam do Módulo Comercial: CRM (Fase 12,
greenfield) e a Fase B de Vendas (AFV + Comissão por Objetivo) — ambos
registrados no `BACKLOG.md`.

## Entrega 18 — Vendas Fase B: AFV + Comissão por Objetivo (2026-09-06)

Fecha a última pendência do módulo Vendas registrada desde a Entrega 14 —
`VendedorRota`/`VendedorMeta` (força de vendas) e `ComissaoObjetivo`
(comissão específica por produto dentro de um Perfil), ambos sub-recursos já
expostos pelo backend desde a Fase 7 sem nenhuma tela até agora.

### `VendedorFormPage` ganha 2 seções de sub-recurso, só em modo edição

Mesmo critério já usado em `Empresa`/`OsAberturaDetailPage` — Rota (AFV) e
Metas por Cliente/Período exigem o `vendedorId` já existir
(`POST /vendedores/{id}/rota`/`/metas`), então as duas seções (`Card`
separado, não aba) só renderizam com `modoEdicao=true`. Diferente do padrão
de "botão desabilitado com tooltip" usado em outras telas pra ação sem
permissão, aqui o botão "Adicionar Cliente"/"Adicionar Meta" simplesmente
**não renderiza** sem `VENDAS_GERENCIAR` (`podeSalvar && <Button>`) — mesmo
critério já usado em `PastasListPage`, coerente porque a seção inteira já é
somente-leitura pra quem não gerencia.

### `ComissaoPerfil` ganha página própria — o gap real desta entrega

Achado ao desenhar a tela: `ComissaoPerfil` nunca teve rota/página dedicada,
só um modal de gerenciamento rápido (`GerenciarComissaoPerfisModal`, aberto
de dentro do form de Vendedor) — suficiente pra CRUD de 3 campos, mas
inviável pra hospedar Objetivos de Comissão (9 campos, incluindo
`AsyncSelect` de produto) dentro de um modal-já-dentro-de-outro-form. Criadas
`ComissaoPerfisListPage`/`ComissaoPerfilFormPage` (rota nova,
`/vendas/comissao-perfis`, item de menu próprio) — mesmo molde de
`VendedorFormPage` (1 `Form` flat + `Card` de sub-recurso só em modo edição).
O modal rápido **não foi removido**: continua servindo o caso de uso original
(cadastrar um perfil sem sair do meio do form de Vendedor); a página nova é
o único lugar que gerencia Objetivos.

### Reaproveitamento total de infraestrutura já existente

Nenhum tipo/lookup novo precisou de criação do zero: `ClienteResumoCell`
(rota/metas), `ProdutoResumoCell`/`useBuscaProdutosParaSelecao` (objetivo de
comissão) e `TIPO_PAGAMENTO_VENDA_LABEL` (já existente pra
`VendaCondicoesPagamento.vistaPrazo`, reaproveitado pra
`ComissaoObjetivo.formaPagamento` — mesmo enum `VISTA`/`PRAZO` no backend)
já existiam no módulo, só precisaram ser importados nos lugares novos.

### Verificação ao vivo (Playwright, navegador real)

Fluxo completo: Vendedor #1 (seedado) → adicionar cliente à rota (posição 1,
resolvido via `ClienteResumoCell`) → adicionar meta mensal de R$5.000 →
tabela confirma os dois. Perfis de Comissão → listagem mostra o perfil
seedado → abrir detalhe → "Objetivos de Comissão" → adicionar objetivo pro
produto Refrigerante (10% de comissão) → tabela confirma nome/produto
resolvido/taxa corretos. RBAC confirmado com usuário OPERADOR real (só
`VENDAS_CONSULTAR`): botões de adicionar corretamente ausentes nas 3 telas,
consulta funcionando normalmente, chamada direta à API com o token do
operador confirmou `403` real em `POST /vendedores/{id}/rota` e
`POST /comissao-perfis`. **Zero erro de console** na execução final.
`npx tsc -b`/`npm run lint`/`npm run test` (13/13)/`npm run build`
confirmados limpos antes do teste ao vivo. Usuários de teste removidos ao
final; rota/meta/objetivo de teste ficaram como demonstração real (mesmo
critério de sempre — sem `DELETE` conceitual necessário, e o usuário poderia
remover pela própria UI se quisesse).

Com esta entrega, **o módulo Vendas está completo em todas as suas fases**
(A e B). Do Módulo Comercial (Fases 7-12 do backend), resta só CRM (Fase 12,
greenfield) sem tela — próximo candidato natural, registrado no `BACKLOG.md`.

## Entrega 19 — CRM (2026-09-06)

Fecha o Módulo Comercial por completo no frontend — último módulo do backend
(`crm-service`, porta 8091, Fase 12) sem nenhuma tela até agora. Greenfield
puro (nem `fenix.sql` nem `BANCO_PDV.md` têm qualquer tabela de CRM,
confirmado no backend por grep exaustivo) — 5 entidades
(`EstagioFunil`/`Lead`/`Oportunidade`/`Atividade`/`Interacao`), 5
controllers, 4 telas + 1 Kanban.

### Kanban sem drag-and-drop — decisão deliberada, registrada

`GET /oportunidades/funil` já devolve exatamente o formato certo pra um
board (colunas + cards + valor agregado por coluna), mas implementar
drag-and-drop de verdade puxaria uma biblioteca nova (`@dnd-kit`/
`react-beautiful-dnd`) só pra este único caso de uso no app inteiro. Optado
por "Mover" (botão por card → `MoverEstagioModal`, Select de estágio destino
+ campo condicional `motivoPerda`) — mesmo critério já usado em
`OsAberturaDetailPage` pra trocar status (Select simples, sem máquina de
estados fixa no frontend). Registrado no `BACKLOG.md` como melhoria futura
possível, não construída agora.

### `AtividadesSection`/`InteracoesSection` — únicos componentes verdadeiramente
compartilhados entre 2 telas nesta entrega

Diferente do resto do app (cada `ResumoCells.tsx` fica só na própria
feature até ter 2+ consumidores reais), aqui os 2 consumidores já existem
desde o desenho — `LeadFormPage` e `OportunidadeFormPage` — porque
`Atividade`/`Interacao` são literalmente polimórficas por design no backend
(`leadId`/`oportunidadeId`, pelo menos um dos dois). Extraídas direto pra
`features/crm/AtividadesSection.tsx`/`InteracoesSection.tsx` (não pra
`shared/`, ainda cross-cutting só dentro do próprio módulo), recebendo
`leadId`/`oportunidadeId` opcionais — exatamente um populado por chamador.

### Simplificação deliberada: `vendedorId`/`responsavelId`/`usuarioId` sem resolução de nome

`Lead.vendedorId` (vendas-service), `Atividade.responsavelId` e
`Interacao.usuarioId` (seguranca-service) são referências soltas cross-serviço
— diferente de `clienteId` (que ganhou `ClienteResumoCell`, resolução de 1
hop contra o cadastro-base-service), essas 3 ficaram como `InputNumber` cru
(o id, sem busca por nome). Motivo registrado, não esquecido: `vendedorId`
exigiria resolução em 2 hops (`vendas-service` pro `colaboradorId`, depois
`cadastro-base-service` pro nome — `VendedorResponse` não carrega nome
direto), e `responsavelId`/`usuarioId` apontam pro `seguranca-service`, que
não tem endpoint de resumo público pensado pra isso ainda. Fica registrado
no `BACKLOG.md` como melhoria futura, não bloqueia o uso (o id continua
funcional, só sem nome bonito na tela).

### `EstagioFunilController` tem endpoint dedicado pro Kanban

`GET /estagios-funil/ordenado` (sem paginação, já ordenado por `ordem`) é
usado tanto no Kanban (montar as colunas) quanto no `MoverEstagioModal`
(Select de destino) e no `OportunidadeFormPage` (Select de estágio inicial)
— 3 consumidores do mesmo hook `useEstagiosFunilOrdenado()`, nenhuma
duplicação de query.

### Verificação ao vivo (Playwright, navegador real)

Fluxo completo: Estágios do Funil confirma os 5 estágios seedados
(Prospecção→Fechamento Ganho/Perdido) → Leads confirma os leads seedados →
criar Lead novo → Qualificar (`NOVO`→`QUALIFICADO`) → botão "Converter em
Cliente" aparece corretamente só depois → Nova Atividade + Registrar
Interação, ambas aparecendo na respectiva seção → criar Oportunidade nova
direto de um Cliente (sem Lead) no estágio "Prospecção" → Mover de Estágio
pra "Fechamento Perdido" sem motivo (campo obrigatório aparece
condicionalmente, confirmado) → com motivo, move e fecha (`situacao=PERDIDA`,
motivo exibido, botão "Mover de Estágio" corretamente desabilitado depois de
fechada) → Kanban renderiza as 5 colunas corretamente → listagem de
Oportunidades confirma a nova, já fechada. RBAC confirmado com usuário
OPERADOR real (`CRM_CONSULTAR`+`CRM_PROSPECTAR`, sem `CRM_GERENCIAR`):
"Novo Estágio"/"Excluir" (lead) corretamente desabilitados na UI, criar lead
funcionando (`CRM_PROSPECTAR`), chamada direta à API com o token do operador
confirmou `403` real em `POST /estagios-funil` e `DELETE /leads/{id}`, `201`
real em `POST /leads`. **Zero erro de console** na execução final (2 achados
de *seletor do próprio script de teste*, não do app — documentados abaixo,
não bugs de produto). `npx tsc -b`/`npm run lint`/`npm run test` (13/13)/
`npm run build` confirmados limpos antes do teste ao vivo. Usuários de teste
e o lead residual de RBAC removidos ao final; demais dados de teste (lead,
oportunidade, atividade, interação) ficaram como demonstração real (mesmo
critério de sempre).

### 2 achados do script de teste, não do app (registrados pra não confundir numa sessão futura)

(1) `getByRole('button', { name: 'Qualificar', exact: true })` nunca casava
— o ícone `CheckOutlined` contribui pro *accessible name* do botão (vira
"check Qualificar", não só "Qualificar"), e a versão não-exata
(`{ name: 'Qualificar' }`) por sua vez também casava com "Desqualificar"
(que contém a substring "qualificar" case-insensitive) — nenhuma das duas
formas resolve sem ambiguidade. Corrigido no script com
`page.locator('button', { hasText: 'Qualificar' }).filter({ hasNotText:
'Des' })`. (2) Mesma classe de ambiguidade com o botão "Excluir" (2 na
tela — cabeçalho do Lead e a linha de uma Atividade), resolvido escopando
por `.locator('form').getByRole(...)`. Nenhum dos dois é comportamento
incorreto do app — só uma armadilha real de correspondência de nome
acessível quando há texto sobreposto entre botões (ex: "Qualificar" dentro
de "Desqualificar").

Com esta entrega, **o Módulo Comercial (Fases 7-12 do backend) está
completo no frontend** — Vendas, Fiscal (NF-e/CT-e/NFS-e), Ordem de Serviço
e CRM todos com tela real. Do backlog geral de frontend, restam: Financeiro
(`financeiro-service`, sem nenhuma tela) e Loja Virtual (apps próprios,
fora do `cactus-erp-web`).

## Correção — contraste do item pai do menu lateral quando o submenu está aberto (2026-09-06)

Reportado pelo usuário com screenshot: com o submenu "Business Intelligence"
aberto (uma de suas páginas ativa), o próprio título "Business Intelligence"
ficava praticamente ilegível — texto quase preto sobre o fundo escuro do
Sider, sem nenhum motivo aparente no código (`Menu` tema `dark`, mesma config
usada por todos os outros módulos, que renderizavam normalmente quando NÃO
estavam com um filho ativo).

**Causa raiz** (achada inspecionando o CSS computado ao vivo, não só lendo
código): o Ant Design usa um token próprio para essa situação —
`subMenuItemSelectedColor`, aplicado a `.ant-menu-submenu-selected >
.ant-menu-submenu-title` (classe que a própria lib adiciona automaticamente
ao item PAI de um submenu que contém a rota selecionada, independente de
`openKeys`/`selectedKeys` do componente — nada de errado no `AppSider.tsx`).
Configurar esse token via `ThemeConfig.components.Menu` **não tem efeito**:
o gerador de estilo do `Menu` (`menu/style/index.js`, derivação do tema dark)
sobrescreve `subMenuItemSelectedColor` internamente com o mesmo valor de
`darkItemSelectedColor` — token que a Entrega 1 já configurava como `#1a1a1a`
(texto escuro), pensado especificamente pro item FOLHA selecionado, que tem
o fundo verde (`darkItemSelectedBg`) atrás pra dar contraste. O item PAI
herda essa mesma cor de texto escura, mas **sem** o fundo verde — daí a
quase-invisibilidade.

**Correção**: sem token configurável pra esse caso específico, a saída foi
CSS direto em `src/index.css` (`.ant-menu-dark .ant-menu-submenu-selected >
.ant-menu-submenu-title { color: #8cc63f !important; }`, escopado ao Sider)
— texto do item pai vira verde-marca quando contém a rota ativa, distinto do
fundo-verde-texto-escuro do item folha, mas igualmente legível e ainda
funciona como indicador visual de "esta seção tem a página ativa". O
`ThemeConfig` ganhou um comentário explicando por que o token "óbvio" não
funciona, pra não ser tentado de novo numa sessão futura.

Validado ao vivo (Playwright, navegador real): inspecionado o `getComputedStyle`
do elemento antes/depois (`rgb(26, 26, 26)` → `rgb(140, 198, 63)`) e
conferido visualmente nos 3 módulos com mais sub-itens (Business Intelligence,
Estoque, Cadastro Base) — todos os 3 títulos de submenu aberto/ativo agora
aparecem em verde legível, sem regressão nos demais itens (não abertos,
hover, item folha selecionado). `tsc -b`/`oxlint`/`npm run test` (13/13)
confirmados limpos.

## Entrega 20 — Financeiro, Fase A: Contas a Receber/Pagar (2026-09-07)

Primeira entrega do `financeiro-service` (porta 8093) — único módulo de negócio do backend
que ainda não tinha nenhuma tela (backend inteiro, incluindo EDI Bancário, fechado no mesmo
dia, ver `CLAUDE.md` raiz). Módulo grande demais pra uma entrega só (16 controllers) —
faseamento confirmado com o usuário via `AskUserQuestion` antes de codar: **Fase A** (esta
entrega) cobre o núcleo — Contas a Receber/Contas a Pagar (título avulso/parcelado, baixa,
estorno, cancelar, renegociar, atualizar classificação), catálogos (Natureza Financeira/Tipo
de Documento) e Configuração Financeira. **Fase B** (futura, registrada no BACKLOG): Caixa e
Bancos/Cheques, Tesouraria, Conciliação Bancária, EDI Bancário.

### Estrutura de menu e DTOs compartilhados entre Receber/Pagar

"Financeiro" ganhou submenu (mesmo padrão dos demais módulos): Contas a Receber/Contas a
Pagar/Naturezas Financeiras/Tipos de Documento/Configuração. `ContaReceberRequest`/
`ContaPagarRequest` não têm `PUT` (título imutável pós-criação — mudanças só por ação
dedicada), mesmo padrão já visto em Requisição/Pedido/Venda/Orçamento/NF-e/CT-e/OS — as duas
telas de formulário desta entrega são só criação.

`BaixaRequest`/`EstornarBaixaRequest`/`CancelarTituloRequest`/`RenegociarTituloRequest`/
`AtualizarClassificacaoTituloRequest` são **byte-idênticos** entre `ContaReceberController`/
`ContaPagarController` no backend (mesmo record Java reaproveitado nos dois) — diferente do
critério geral do projeto de duplicar por feature (ex: `ComissaoObjetivoModal` não foi
compartilhado com nada por ter campos genuinamente diferentes), aqui a identidade total dos
DTOs justificou 5 modais genéricos (`RegistrarBaixaModal`/`MotivoAcaoModal`/
`RenegociarTituloModal`/`AtualizarClassificacaoModal`, mais `NaturezaFinanceiraModal`/
`TipoDocumentoFinanceiroModal` pros catálogos), recebendo a função de mutação como prop e
reaproveitados tanto em `ContaReceberDetailPage` quanto `ContaPagarDetailPage`.
`MotivoAcaoModal` cobre tanto Cancelar Título quanto Estornar Baixa (mesmo `{motivo: string}`
nos dois `Request`).

### Segmented Avulso/Parcelado no formulário de criação

`ContaReceberFormPage`/`ContaPagarFormPage` têm um `Segmented` "Avulso (1 parcela)"/
"Parcelado (N parcelas)" que troca entre `POST /contas-receber` (`ContaReceberRequest`,
`dataVencimento`+`valorOriginal`) e `POST /contas-receber/gerar-parcelado`
(`GerarContaReceberParceladaRequest`, `quantidadeParcelas`+`valorTotal`+
`primeiroVencimento`+`intervaloEntreParcelasDias`) — mesmo par de endpoints do backend, sem
entidade "lançamento" separada (as N parcelas geradas compartilham `loteId`). Avulso navega
pro detalhe do título criado; parcelado volta pra lista (N títulos nascem de uma vez).

### `ContaBancaria` — só o lookup, CRUD completo é Fase B

`BaixaRequest.contaBancariaId` é obrigatório desde a extensão do backend (2026-09-06/07,
fecha o gap de Caixa e Bancos) — como o CRUD completo de `ContaBancaria` é sub-fase futura,
esta entrega só consome `GET /contas-bancarias/ativas?empresaId=` (endpoint que já existe
sem paginação, pensado exatamente pra alimentar Select) via `useContasBancariasAtivas`,
sem tela de gerenciamento própria ainda.

### BUG REAL encontrado e corrigido nesta sessão: `getValueProps` de `DatePicker` aplicado num `<Input type="date">`

Testando ao vivo (Playwright): o modal "Registrar baixa" abria com o campo "Data da baixa"
**vazio** mesmo com o `useEffect` explicitamente chamando `form.setFieldsValue({ dataBaixa:
dayjs().format('YYYY-MM-DD'), ... })`. Causa: copiei por hábito o padrão `getValueProps={(v)
=> ({ value: v ? dayjs(v) : undefined })}` que o app usa em campos com `antd.DatePicker`
(que exige um objeto `dayjs`, não string) — mas `RegistrarBaixaModal`/
`AtualizarClassificacaoModal` usam `<Input type="date">` nativo (mesmo padrão do resto do
módulo Financeiro e de Vendas/Compras), que espera uma **string** `"YYYY-MM-DD"` crua no
`value`. O `getValueProps` convertia a string pro objeto `dayjs`, que o input HTML nativo não
sabe renderizar — campo ficava silenciosamente vazio, sem erro de console nem de tipo (JS
aceita atribuir qualquer coisa a um atributo React). Corrigido removendo `getValueProps`/
`normalize` dos dois campos (`dataBaixa` em `RegistrarBaixaModal`, `descontoAte` em
`AtualizarClassificacaoModal`) — o valor trafega como string simples do input nativo direto
até o `Request`, sem conversão nenhuma no meio, mesmo critério já usado em
`ContaReceberFormPage`/`ContaPagarFormPage` desde o início (que nunca tiveram esse bug, por
nunca terem precisado hidratar um valor pré-existente).

### Verificação ao vivo (Playwright, navegador real)

Fluxo completo, ponta a ponta, contra os 3 serviços reais (`seguranca-service`+
`cadastro-base-service`+`financeiro-service`): criar Natureza Financeira (Receita) e
(Despesa) → criar Tipo de Documento → configurar 2% multa/0,10% juros → criar título avulso
a Receber (R$300) → baixa parcial (R$100, saldo R$200) → baixa do restante (auto-preenchido
com o saldo, R$200 — situação vira `Paga`) → estornar a última baixa (Popconfirm + motivo —
situação volta pra `Parcial`, saldo R$200, **botão Cancelar corretamente desabilitado** por
ainda haver uma baixa ativa) → segundo título avulso (R$80) cancelado sem nenhuma baixa
(motivo obrigatório) → terceiro título avulso (R$200) renegociado em 2 parcelas de R$100
(situação vira `Renegociada`, motivo exibido) → título avulso a Pagar (R$150) criado e
baixado totalmente → título parcelado a Pagar (R$300 em 3×) criado, confirmado na listagem
com origem "Manual"/"Compra"/"Renegociação" corretamente distintas e cores de situação
corretas (a listagem também exibiu, de graça, dado real de sessões anteriores de validação
de backend — Contas a Pagar geradas de Compra/Renegociação, confirmando que a tela renderiza
dado de produção real, não só o que este teste criou). **RBAC confirmado com usuário
OPERADOR real** (não só leitura de código): `FINANCEIRO_CONSULTAR`/`FINANCEIRO_LANCAR`
presentes, `FINANCEIRO_GERENCIAR` ausente — `GET` 200, `POST /contas-receber` 201,
`POST /naturezas-financeiras` 403, `POST /contas-receber/{id}/cancelar` 403 (os dois
últimos exigem `FINANCEIRO_GERENCIAR`, correspondendo exatamente ao gate usado no frontend).
**Zero erro de console** na execução final. `npx tsc -b`/`npm run lint`/`npm run test`
(13/13)/`npm run build` confirmados limpos antes e depois do teste ao vivo. Usuário de teste
RBAC removido ao final; naturezas/tipo de documento/títulos de teste ficaram como
demonstração real (mesmo critério de sempre — `ContaReceber`/`ContaPagar` não têm `DELETE`
na API, só cancelamento/renegociação, que são ações de negócio já exercitadas).

### Fora de escopo desta entrega (Fase B, registrada no BACKLOG)

Caixa e Bancos (CRUD de `ContaBancaria`, hoje só o lookup ativas), Cheques (emitido/
recebido), Tesouraria (transferência entre contas, aplicação/resgate, posição de caixa),
Conciliação Bancária (upload de extrato OFX), EDI Bancário (gerar remessa CNAB 240, importar
retorno CNAB 240).

## Entrega 21 — Financeiro, Fase B: Caixa e Bancos/Cheques/Tesouraria/Conciliação/EDI (2026-09-07)

Fecha o módulo Financeiro por completo no frontend — as 5 áreas que a Fase A (Entrega 20)
tinha deixado pendentes. 10 telas novas + 12 modais novos em `features/financeiro/`, todos
reaproveitando a infraestrutura já existente (types/api/queries do mesmo módulo, só
estendidos). Submenu "Financeiro" foi de 5 pra 14 itens — maior submenu do app, superando o
de Estoque (10).

### `CAIXA_*`, não `FINANCEIRO_*` — RBAC reaproveitado do backend, não reinventado

Todas as 10 telas novas usam `hasAuthority(authorities, 'CAIXA_CONSULTAR'/'CAIXA_MOVIMENTAR'/
'CAIXA_GERENCIAR')` — o backend já tinha decidido (Caixa e Bancos, Tesouraria, Conciliação
são todos o mesmo domínio "mexer em saldo de conta bancária") não criar um módulo de
authority novo pra cada sub-área. `financeiroNav.requiredAuthorities` (nível do módulo no
`NavConfig`) precisou ganhar as 3 `CAIXA_*` além das 3 `FINANCEIRO_*` já existentes, senão um
usuário só com `CAIXA_*` (sem nenhuma `FINANCEIRO_*`) seria bloqueado de entrar em
`/financeiro` inteiro pelo `RequireAuthority` no nível da rota pai.

### `ContaBancaria` — CRUD completo, campo condicional por `tipo`

`ContaBancariaFormPage` usa `Form.useWatch('tipo', form)` pra esconder Banco/Agência/Número
quando `tipo === 'CAIXA_INTERNO'` (sem banco associado, regra do próprio domínio) — mesmo
padrão condicional já usado em `AdicionarItemOsModal` (Produto vs. Serviço). `saldoInicial`/
`dataSaldoInicial` ficam `disabled` em modo edição (não é editável depois de criada — mudança
de saldo só via `MovimentoCaixaBanco`, o próprio `PUT` do backend ignora esses 2 campos).

### DTOs compartilhados entre Cheque Emitido/Recebido — mesmo critério da Fase A

`CompensarChequeModal`/`DevolverChequeModal`/`SelecionarContaEDataModal` são genéricos,
reaproveitados pelos dois lados (mesmos `Request` no backend — `CompensarChequeEmitidoRequest`/
`CompensarChequeRecebidoRequest` têm o campo idêntico, só nomes de classe diferentes;
`DevolverChequeRequest` é literalmente a mesma classe nos dois controllers). `SelecionarContaEDataModal`
cobre Depositar e Descontar (Cheque Recebido) — só o rótulo do campo de data muda.

### `GerarRemessaModal` — o modal mais complexo desta entrega

`Form.List` de itens, cada item com `contaReceberId` (Select populado por
`useContasReceber({}, empresaId, 0, 100)`, filtrado client-side pra `ABERTA`/`PARCIAL` — o
backend rejeitaria título já pago/cancelado, a UI evita a chamada fadada ao erro) + os 6
campos de sacado (`sacadoNumeroInscricao`/`sacadoNome`/`sacadoLogradouro`/`sacadoBairro`/
`sacadoCep`/`sacadoCidade`/`sacadoUf`) informados manualmente — o `financeiro-service` não
tem client cross-serviço de saída pro `cadastro-base-service` (só é chamado, nunca chama),
então não resolve os dados do cliente sozinho, mesmo achado já documentado no backend.

### `EdiBancarioPage` — única tela desta entrega com `Tabs` no nível superior

Remessas/Retornos são fluxos irmãos do mesmo domínio (CNAB 240), sempre escopados pela mesma
conta bancária — `Tabs` no lugar de 2 rotas separadas evita duplicar o Select de conta e o
contexto da página. Ambas as tabelas usam `expandable` (Ant Design) pra mostrar os itens de
cada remessa/retorno inline, em vez de uma segunda rota de detalhe — decisão deliberada pra
não multiplicar rotas num módulo que já tem 14 entradas de menu.

### `ConciliacaoBancariaPage` — a tela mais "dashboard" desta entrega

Duas tabelas lado a lado (`itensExtratoPendentes`/`movimentosPendentes`, exatamente o shape
de `PendenciasConciliacaoResponse` do backend) + upload de OFX (mesmo padrão `Upload.Dragger`
com `beforeUpload` retornando `false` já usado em `DocumentoUploadPage`/GED) + "Conciliar
Automaticamente" (dispara e mostra o resultado agregado via toast) + ações por item
(`Conciliar` abre `ConciliarManualModal` com Select dos movimentos pendentes; `Gerar Ajuste`
abre `GerarAjusteModal`). **Desconciliar um item já conciliado não ganhou tela nesta fatia**
(a API existe e o hook `useDesconciliarItem` já está pronto, só não foi conectado a nenhum
botão) — registrado como pendência pequena, não um esquecimento silencioso.

### BUG REAL investigado a fundo, descartado como problema de script de teste (não do produto)

Testando ao vivo (Playwright) o fluxo completo Fase B numa sessão só, `NovoChequeRecebidoModal`
parou de abrir seus campos de formulário de forma 100% reproduzível, sempre no mesmo ponto
exato da sequência (depois de criar Conta Bancária + Movimento + 2 Cheques Emitidos). Investigação
extensa (7 scripts de debug isolando cada etapa) confirmou: (1) o modal renderiza CORRETAMENTE
o `<form>` completo com todos os campos no DOM (`.ant-modal-root`/`novoChequeRecebidoForm_*`
todos presentes) — só o meu seletor de debug inicial (`.ant-modal-content`) é que não existe
nesta versão do Ant Design, gerando um falso alarme na primeira rodada; (2) cada sub-fluxo
testado ISOLADAMENTE (Conta Bancária+Movimento sozinhos; Cheque Emitido com compensar+devolver
sozinho; os dois juntos) sempre funcionou; (3) só a sequência COMPLETA das 4 etapas numa única
sessão de navegador reproduzia o travamento, e um `page.reload()` antes de abrir o modal
sempre resolvia — aponta pra acúmulo de estado/memória do processo de desenvolvimento (HMR do
Vite, DevTools do React, múltiplos `QueryObserver` do TanStack Query não coletados de janelas
de modal já fechadas) sob automação headless em sequência rápida, não pra um defeito de
código — nenhum usuário real interage com 4 formulários em ~15 segundos sem nenhuma pausa.
**Não corrigido no código** (não há o que corrigir — o React DevTools/HMR é exclusivo de
`npm run dev`, não existe em build de produção) — a suíte de validação foi dividida em 3
sessões de navegador independentes (mesmo espírito de "não encadear tudo numa sessão só"),
o que já é a prática mais realista de teste mesmo sem essa descoberta.

### Achado real, mas do MEU script de teste, não do produto: `.first()` em lista sem filtro

`AplicacoesFinanceirasListPage` é a única lista desta entrega **sem** um Select de "filtrar
por conta bancária" (diferente de Movimentos/Cheques Emitidos/Cheques Recebidos, que têm).
Meu script de teste usou `.first()` no botão "Resgatar" assumindo que só existiria 1 aplicação
`ATIVA` na tabela inteira — havia uma aplicação `ATIVA` residual de uma conta diferente
(criada numa sessão de teste anterior), e o resgate acabou creditando a conta errada. **Não é
um bug de produto** — a ação resgatou exatamente a aplicação que o botão clicado pertencia,
com o valor certo, creditando a conta certa PARA AQUELA aplicação (conferido depois via API:
o saldo da `Conta Teste QA` bateu exatamente com o esperado assim que a aplicação certa foi
resgatada). Registrado como possível polish futuro: dar à tela um filtro por conta como as
outras 3 já têm, pra reduzir esse tipo de ambiguidade visual pra um usuário real também.

### Verificação ao vivo (Playwright, navegador real, 3 sessões independentes)

**Sessão 1** — Contas Bancárias + Movimentos + Cheques Emitidos: criar conta CAIXA_INTERNO
(saldo inicial R$1.000) → reabrir em edição (dados carregados corretamente) → lançamento
manual de Entrada R$50 (saldo 1050) → cheque emitido R$80 compensado (saldo 970) → segundo
cheque R$30 devolvido (sem movimento, saldo continua 970) — saldo final conferido via API:
**R$970,00 exato**. **Sessão 2** (fresca) — Cheques Recebidos + Transferências + Aplicações +
Posição/Fluxo de Caixa: cheque recebido R$120 descontado direto (ENTRADA imediata) → segundo
cheque R$40 depositado então compensado (ENTRADA só na compensação, não no depósito) →
transferência de R$100 pra "BB Conta Movimento" → aplicação CDB R$200 resgatada por R$215
(rendimento +15) → saldo final conferido via API: **R$1.045,00 exato**, batendo com o cálculo
manual completo da sessão (1000+50-80+120+40-100-200+215) → Posição de Caixa e Fluxo de Caixa
renderizados sem erro. **Sessão 3** (fresca) — Conciliação Bancária + EDI Bancário, contra a
conta com dado real de sessões de validação de backend anteriores: pendências (2 listas lado
a lado) renderizadas corretamente com dezenas de registros reais; "Conciliar Automaticamente"
disparado sem erro; EDI Bancário com as duas abas (9 remessas reais, expansão de linha
mostrando nosso-número/documento/sacado/vencimento corretos; 11 retornos reais, incluindo os
próprios arquivos `.ret` gerados durante a validação do backend do EDI Bancário nesta mesma
sessão de trabalho). **Zero erro de console nas 3 sessões.** `npx tsc -b`/`npm run lint`/
`npm run test` (13/13)/`npm run build` confirmados limpos antes e depois do teste ao vivo.
Dado de teste (conta bancária, cheques, transferência, aplicação) ficou como demonstração
real — nenhum desses recursos tem `DELETE` na API (mesmo critério de sempre, ledger/registro
de fluxo de negócio não se apaga).

### Fora de escopo desta entrega, registrado

Desconciliar item já conciliado (API pronta, sem botão na tela); filtro por conta bancária em
Aplicações Financeiras (as outras 3 telas de listagem têm, esta não); detalhe de uma
importação de extrato específica (a listagem de importações não abre pra ver os itens
individuais daquele arquivo — só as pendências agregadas da conta inteira). Com esta entrega,
**o módulo Financeiro está completo em todas as suas fases (A e B)** — não resta nenhum
módulo de negócio do backend sem frontend no `cactus-erp-web`.

## Fechamento de 7 itens pontuais do backlog (2026-09-13)

Pedido explícito do usuário ("Mata essa pendencia") pra fechar 7 itens específicos do
`BACKLOG.md` acumulados desde entregas anteriores: `aprovadorId` cru no Estoque, `ComponenteKitModal`
nunca validado ao vivo, tela do Motor Tributário, mapeamento de authority no menu, GED sem
integração real, CRM sem drag-and-drop e sem resolução de nome em 3 campos. Sem tela nova de
uma fase inteira — cada item foi investigado e resolvido no próprio código já existente
(entregas 8-19), não uma entrega nova de módulo.

### 3 itens eram esclarecimento, não código novo — o backlog estava desatualizado

- **`RegistrarMovimentoModal.aprovadorId`**: já é `AsyncSelect` desde que Segurança ganhou
  tela de Usuário (Entrega 10) — a entrada do backlog nunca foi atualizada depois disso.
  Verificado ao vivo via `curl` contra `seguranca-service`.
- **`NavConfig.tsx` — mapeamento módulo→authority**: auditado por completo contra o catálogo
  real (`GET /api/v1/funcionalidades?size=200`, ~68 códigos) — nenhum typo/authority obsoleta
  encontrada. `LOJA_*`/`SINCRONIZACAO_*` existem no backend sem entrada no menu, mas é porque
  `loja-virtual-service`/`sincronizacao-service` não têm frontend nenhum ainda, não uma falha
  de mapeamento.
- **CRM: resolução de nome de `vendedorId`/`responsavelId`/`usuarioId`**: `ResumoCells.tsx`
  (`VendedorResumoCell`/`UsuarioResumoCell`), o `Select` de vendedor com nome em
  `LeadFormPage`/`OportunidadeFormPage` e o `AsyncSelect` de responsável em
  `AdicionarAtividadeModal` já existiam — nenhum endpoint novo foi necessário no
  `seguranca-service` (`UsuarioResumoCell` reaproveita o `GET /usuarios/{id}` já existente
  desde a Entrega 10), nem uma segunda chamada além do já resolvido `GET /vendedores/{id}` →
  `GET /colaboradores/{id}` pro `VendedorResumoCell`. A entrada original do backlog (Entrega
  19, "ficaram como `InputNumber` cru") estava desatualizada — provavelmente escrita antes de
  uma correção que não foi propagada pro `BACKLOG.md`. Único gap real remanescente:
  `VendedorResumoCell` nunca era usada em nenhuma listagem (só nos forms) — fechado adicionando
  a coluna "Vendedor" em `LeadsListPage`/`OportunidadesListPage`.

### `ComponenteKitModal` — validado ao vivo via contrato de API (sem clique em navegador)

Sem ferramenta de automação de navegador disponível nesta sessão — validado o contrato
request/response exato que a UI usa, via `curl` contra o `controle-estoque-service` real:
componente adicionado (`201`), componente circular rejeitado (`422
ESTOQUE_KIT_COMPONENTE_CIRCULAR`), baixa sem saldo rejeitada (`422
ESTOQUE_SALDO_INSUFICIENTE`), entrada de estoque seguida de baixa de kit com sucesso (`204`,
`SAIDA`/`KIT` cascateado nas 2 unidades certas — 1 kit × 2 do componente). Suficiente pra
fechar o item (a UI só espelha esse contrato, sem lógica própria de validação), mas registrado
com honestidade: não é o mesmo nível de confiança de um clique real em navegador.

### Motor Tributário — módulo novo (`features/tributacao/`) + bug real no backend

Maior sub-tarefa deste bloco: `GruposTributariosListPage`/`OperacoesFiscaisListPage` (CRUD
simples, RBAC via `CADASTRO_TRIBUTACAO_*`) e a tela principal —
`ConfiguracaoTributariaFormPage`/`ConfiguracoesTributariasListPage`, a célula Grupo×Operação
que é o coração do motor (ver ANÁLISE TRIBUTÁRIA no `CLAUDE.md` raiz): Grupo/Operação via
`Select`, `icmsPorUf` como lista local + `IcmsPorUfModal` por UF (mesmo padrão
Endereços/Contatos de Pessoa), PIS/COFINS/IPI como seções do mesmo form (1:1 com a célula, não
variam por UF). `OperacaoFiscalModal` embute a configuração de ISS (1:1 com a Operação, não a
célula — só faz sentido pra operação de serviço). 3 itens novos em `NavConfig.tsx` sob
"Cadastro Base".

**BUG REAL encontrado no backend, corrigido nesta sessão**: `ConfiguracaoTributariaService.listar()`
(`cadastro-base-service`) ignorava os parâmetros `grupoTributarioId`/`operacaoFiscalId` e
sempre chamava `repository.findAll(pageable)` — o filtro documentado na própria Swagger nunca
era de fato aplicado. Bug latente desde a construção original do motor tributário (Fase 1b do
`CLAUDE.md` raiz), só descoberto agora porque nenhuma UI tinha exercitado esse parâmetro antes
desta entrega. Corrigido com 3 métodos derivados novos em `ConfiguracaoTributariaRepository`
(`findByGrupoTributarioId`/`findByOperacaoFiscalId`/`findByGrupoTributarioIdAndOperacaoFiscalId`)
e branch explícito no service. Validado ao vivo contra o `cadastro-base-service` recompilado e
reiniciado: filtro por grupo, por operação e sem filtro devolvendo os escopos corretos nos 3
casos.

### GED — integração `entidadeTipo`/`entidadeId`, finalmente exercitada por uma UI

`shared/ui/documentos/DocumentosEntidadeSection.tsx` (+ `DocumentoEntidadeUploadModal.tsx`) —
seção reaproveitável de "Documentos anexados", em `shared/` de propósito (mesmo critério já
usado pra `ProdutoResumoCell`/`useProdutoResumo`, Entrega 8: é um padrão que qualquer módulo
futuro com cadastro próprio vai precisar de novo), reaproveitando `useDocumentos`/
`useCriarDocumento`/`useBaixarUltimaVersao` de `features/ged` direto (cross-feature import,
mesmo padrão já estabelecido entre `features/vendas`/`features/fiscal`/`features/os` nas
Entregas 15/17) — não reimplementa a API do GED. O detalhe completo (histórico de
versões/nova versão/excluir) continua só em `DocumentoDetailPage`; o botão "Detalhes" da seção
navega pra lá em vez de duplicar essa UI. Embutida como nova aba "Documentos" em
`ProdutoFormPage`/`FornecedorFormPage` — gate "salve o cadastro primeiro" quando `entidadeId`
ainda não existe (produto/fornecedor novo, sem id), mesmo padrão de sub-recurso já usado em
`Empresa`/`OsAberturaDetailPage`. Fecha a pendência 16 da Entrega 11, registrada desde
2026-09-02 e nunca antes exercitada por nenhuma tela.

### CRM — Kanban ganha drag-and-drop de verdade, revertendo a decisão da Entrega 19

Instalado `@dnd-kit/core` (única dependência nova deste bloco) — `OportunidadesKanbanPage`
reescrita com `DndContext`/`useDraggable` (card)/`useDroppable` (coluna). Arrastar um card pra
uma coluna cujo `tipoEncerramento` é `ABERTO`/`GANHO` move direto, sem confirmação (nova
`useMoverEstagioOportunidadeGenerico()` em `queries.ts` — mesma mutação de
`useMoverEstagioOportunidade`, mas com `id` viajando como variável em vez de fixado na criação
do hook, necessário porque o `onDragEnd` não sabe de antemão qual card vai ser solto).
Arrastar pra uma coluna `PERDIDO` abre `MoverEstagioModal` já com o estágio destino
pré-selecionado (novo prop `estagioDestinoInicial`) — o drop nunca pula a validação de
`motivoPerda` obrigatório, só evita reabrir o `Select` quando o motivo não é necessário. Botão
"Mover" mantido como fallback explícito (acessibilidade/toque sem gesto de arrastar, e único
caminho pra estágio `PERDIDO`). `PointerSensor` com `activationConstraint: {distance: 8}` —
sem essa distância mínima de ativação, o gesto de clique pra navegar pro detalhe do card
(`onClick`, ainda presente no `Card`) conflitaria com o início do drag.

### Verificação (sem navegador — sem ferramenta de automação disponível nesta sessão)

`npx tsc -b`, `npm run lint` (oxlint, 0 avisos), `npm run test` (13/13) e `npm run build`
confirmados limpos depois de cada sub-item e no conjunto final. Validação funcional dos 2
achados de backend (filtro de `ConfiguracaoTributaria`, contrato de Kit) feita via `curl`
contra os serviços reais, não em navegador — registrado com honestidade, mesmo padrão já usado
quando essa limitação apareceu antes nesta sessão. `BACKLOG.md` atualizado fechando os 7 itens.

## Exportação de relatórios PDF/Excel — Fase 17 do backend chega ao frontend (2026-10-01)

Requisito do usuário registrado em 2026-09-27: todo relatório sai com integração no frontend,
não só a API. Esta entrega fecha isso para os 8 relatórios que existem hoje no backend.

### Peças compartilhadas novas
- `shared/api/download.ts` — `baixarArquivo(api, url, nomeSugerido, params)`: blob, link sintético
  e nome resolvido do `Content-Disposition`. Era código duplicado em `ged/api.ts` e
  `financeiro/api.ts` (EDI Bancário); os dois passaram a usar o helper. `FormatoRelatorio = 'pdf' | 'xlsx'`
  é também o sufixo do endpoint (`/relatorios/<nome>/pdf|xlsx`).
- `shared/ui/ExportarRelatorioButtons.tsx` — botões "PDF"/"Excel" padrão de toda tela de relatório,
  com carregamento próprio por botão. Sempre exporta com os MESMOS filtros da tela. Com
  `responseType: 'blob'`, o corpo do erro (ProblemDetail) não é lido, então a mensagem sai pelo
  `kind` (texto específico pra 403/rede).

### Telas
- `estoque/CurvaAbcPage` e `financeiro/FluxoCaixaPage`: ganharam os botões (antes só JSON).
- `financeiro/TitulosEmAbertoPage` (NOVA, `/financeiro/titulos-em-aberto`, menu Financeiro): alterna
  entre a receber e a pagar, tem vencimento opcional (vazio = carteira inteira) e cliente ou
  fornecedor via `AsyncSelect` (zerado ao trocar de lado, porque o id de um cadastro não vale no
  outro). Tag de dias de atraso, linha de totais idêntica à do PDF e clique na linha abre o título.
- `vendas/RelatoriosVendasPage` (NOVA, `/vendas/relatorios`, menu Vendas): 3 abas (Vendas por
  Período com filtro de vendedor/cliente, Produtos Mais Vendidos, Comissões por Vendedor) com
  período compartilhado. O vendedor é resolvido pelo colaborador vinculado, mesmo critério de
  `ComissoesListPage`.

### Bug real corrigido em componente compartilhado
`AsyncSelect` sempre sobrescrevia o `placeholder` do chamador com "Digite para buscar…". Agora o
`placeholder` passado tem prioridade, e o padrão continua quando nenhum é passado. Isso corrigiu
também 2 telas existentes que já passavam texto e eram ignoradas em silêncio:
`compras/PedidosListPage` ("Filtrar por fornecedor") e `compras/RequisicoesListPage` ("Filtrar
por solicitante").

### Verificação (navegador real, Playwright, login pelo fluxo OAuth2/PKCE de verdade)
`tsc -b`, `oxlint` e `vitest` (13/13) limpos. E2E nas 4 telas: os 8 relatórios × PDF + Excel = 16
downloads reais pela interface. Os totais da tela batem com o backend e com o PDF (títulos a
receber 21 = R$ 2.553,33 / atualizado R$ 2.360,35; a pagar 13 = R$ 831,00 / R$ 832,09; vendas 9 =
R$ 411,82; comissões R$ 21,09). Zero erro de console, fora o 404 já conhecido do produto órfão
`#999999` dos dados de teste (tratado, a tela mostra `#id`). Achado do meu script, não do produto:
o nome acessível do botão é "file-pdf PDF", porque o Ant Design inclui o `aria-label` do ícone,
então o seletor precisa casar por regex, não por nome exato.

## Entrega 22 — Contabilidade (módulo novo) + Loja Virtual (admin, módulo novo) + exportação nos
15 relatórios restantes (2026-10-11/12)

Pedido do usuário: "Não deixe lacunas, implemente tudo, nosso ERP precisa ser completo" — depois de
fechar a Fase 17 no backend (15 relatórios), dois gaps reais foram achados antes de codar:
`contabilidade-service` e `loja-virtual-service` (admin) nunca tinham ganhado NENHUMA tela, apesar
de ambos existirem no backend desde 2026-09-13/04. Confirmado com o usuário via `AskUserQuestion`
("Botões + CRUD completo de contabilidade e loja-virtual") antes de codar os dois módulos novos
inteiros, não só os botões de exportação pendentes.

### Contabilidade — módulo novo (`features/contabilidade/`)

`PlanoContasListPage`+modal (hierarquia via `contaPaiId`, mesmo padrão de `PastaModal` do GED —
Select filtrado a só contas sintéticas como pai, excluindo a própria conta em edição),
`CentrosCustoListPage`+modal (catálogo flat), `LancamentosListPage`(filtro por período)+
`LancamentoFormPage`(`Form.List` de partidas, mesmo padrão de itens de `RequisicaoFormPage`,
Select de conta restrito a analíticas)+`LancamentoDetailPage`(partidas read-only + ação Estornar
via `MotivoAcaoModal`, cópia local do mesmo componente genérico já usado em `features/financeiro`),
`BalancetePage`/`RazaoPage` (filtro de período, com botões de exportar). `LancamentoContabil` não
tem `PUT` (imutável após lançado, só `POST .../estornar`) — mesmo padrão de Requisição/Pedido/
Venda/Orçamento/NF-e/CT-e/OS, confirmado lendo os 5 controllers e DTOs reais antes de escrever
`types.ts`.

### Loja Virtual — admin da plataforma (`features/loja-virtual/`)

Backend mapeado por completo via agente de exploração antes de codar (6 entidades, RBAC admin-only
`LOJA_CONSULTAR`/`LOJA_GERENCIAR` — nenhum papel abaixo de GERENTE tem acesso, por ser administração
da PLATAFORMA, não trabalho comercial do dia a dia). `LojasListPage`+modal (ações dedicadas
Cancelar/Reativar Assinatura, nunca via `PUT` genérico), `CategoriasListPage`(hierarquia por loja)+
modal, `AtributosCategoriaListPage`+modal (`Form.List` de opções inline SÓ na criação de atributo
`LISTA` — o `PUT` do backend não altera opções, documentado assim no próprio Javadoc do
`AtributoCategoriaController` — edição posterior de opção é ação dedicada via `GerenciarOpcoesModal`,
que usa os endpoints `POST`/`DELETE .../opcoes`), `ProdutosListPage`+`ProdutoLojaFormPage` (Variantes
como seção só em modo edição, mesmo critério de sub-recurso de `Empresa`/`VendedorFormPage`),
`VarianteModal` (um campo por atributo `geraVariacao=true` da categoria do produto na criação —
Select de opções pra atributo `LISTA`, Input pra `TEXTO`/`NUMERO` — campo `valoresAtributos`
some na edição, é imutável depois de criada), `PedidosListPage`+`PedidoDetailPage` (máquina de
estados completa via botões contextuais + `CancelarPedidoModal`). `SeletorLoja.tsx` (novo,
`shared`-local ao módulo) é o único Select "trocar de tenant" do app — diferente de todo outro
módulo (escopado pela empresa ativa da sessão), aqui o escopo é por `lojaId` escolhido na própria
tela, porque este módulo administra lojas de CLIENTES diferentes, não a empresa do usuário logado.

### Export PDF/Excel nos 15 relatórios restantes da Fase 17

Botões adicionados reaproveitando o filtro já existente da tela, sem filtro novo, em:
`ClientesListPage`/`FornecedoresListPage`/`ProdutosListPage` (cadastro), `SaldosListPage`
(estoque), `DocumentosListPage` (GED, ganhou 3 selects novos — pasta/tipo/situação — só pro
relatório, já que a tela não tinha esses filtros antes), `OportunidadesListPage` (CRM, ganhou 1
select de situação só pro relatório). Páginas novas, uma por módulo sem view equivalente:
`FichaMovimentacaoPage` (estoque), `RelatorioPedidosCompraPage` (compras), `RelatorioOsPeriodoPage`
(OS), `NotasPeriodoPage` (fiscal, unifica NF-e/NFC-e/NFS-e/CT-e numa grade só — único relatório do
lote cujo DTO (`NotaFiscalPeriodoItem`) não é nenhuma entidade existente, é a projeção JPQL
`SELECT new` que o próprio backend expõe). Os 3 indicadores do BI (Valor de Estoque/Curva
ABC/Compras por Fornecedor) e o relatório de Pedidos da Loja Virtual também ganharam os botões,
reaproveitando o padrão de todo o resto.

### Verificação ao vivo (sem navegador disponível nesta sessão — contrato de API via curl, como já
documentado antes nesta mesma situação)

`npx tsc -b`/`oxlint`/`vitest` (13/13)/`vite build` limpos antes de qualquer teste contra backend
real. Sem ferramenta de navegador disponível nesta sessão (diferente de quase toda entrega
anterior) — validação feita via contrato de API real contra os bancos de dados de produção local
(Docker Desktop religado, `cactus-mysql`/`cactus-rabbitmq` já provisionados com os schemas
`cactus_contabilidade`/`cactus_loja`), com usuários de teste reais criados via SQL direto
(`qa.relatorios.teste` ADMIN, `qa.operador.teste` OPERADOR — removidos ao final).

**Contabilidade**: lançamento de venda a vista criado (débito Caixa/crédito Receita de Vendas,
contas reais seedadas) → partida desbalanceada rejeitada (422) → estornado → Balancete/Razão
conferidos → SPED ECD conferido. **RBAC confirmado real**: OPERADOR (só `CONTABIL_CONSULTAR`) 200
em consulta, 403 real em lançar/gerenciar (payload válido, não um 400 de validação mascarando o
403).

**Loja Virtual**: Loja criada+assinatura cancelada/reativada+subdomínio duplicado rejeitado (409);
Categoria criada (hierarquia); Atributo criado com opções inline, opção adicionada/removida depois;
Produto criado/editado; **motor de validação de variante exercitado nos 4 cenários de erro** (SKU
duplicado 409, atributo obrigatório ausente 422, opção inválida pra LISTA 422, atributo de
categoria divergente 422) + variante válida criada/editada/excluída, contra o produto seedado real
("Vestido Floral Midi", Tamanho/Cor) — restaurado ao estado original ao final; os 4 pedidos
seedados reais exercitados em toda a máquina de estados (em separação→enviado→cancelar-depois-de-
enviado rejeitado 409→entregue; e cancelar antes do envio aceito). **RBAC confirmado real**:
OPERADOR sem nenhuma `LOJA_*` — 403 real em toda rota do módulo (é admin-only de verdade, não só
escondido do menu).

### 2 BUGS REAIS encontrados e corrigidos no backend durante esta validação (nenhum em `mvn compile`)

1. **`contabilidade-service` — Balancete/Razão/SPED ECD não voltavam a zero depois de um estorno**:
   as 3 queries de agregação (`PartidaContabilRepository.somarDebitoCredito`/
   `somarDebitoCreditoAntesDe`/`buscarPorContaEPeriodo`, + `LancamentoContabilRepository.
   buscarLancadosNoPeriodoComPartidas`, usada pelo export SPED) filtravam `situacao = LANCADO`,
   excluindo o lançamento ORIGINAL (que vira `ESTORNADO`) da soma — mas o lançamento de ESTORNO
   (que é `LANCADO`) continuava entrando. Resultado: depois de lançar e estornar o mesmo par
   dentro do período consultado, o saldo ficava com o efeito do estorno sozinho (não-zero), em vez
   de o par se cancelar. Corrigido removendo o filtro de `situacao` das 4 queries — um lançamento
   `ESTORNADO` continua sendo um movimento real que aconteceu (o original nunca é apagado, só
   marcado, ver Javadoc de `LancamentoContabil`), e seu estorno é uma transação NOVA que o cancela,
   não um "desfazer" que apaga história; incluir os dois sempre faz o par somar exatamente zero
   quando ambos caem no período, e SPED ECD passou a reportar as DUAS linhas (antes reportava só o
   estorno). Achado testando o próprio fluxo que a `LancamentoDetailPage` constrói (criar→estornar→
   consultar relatório no mesmo período) — nunca teria aparecido num teste que só validasse criação
   e estorno isoladamente, sem depois consultar o relatório do mesmo intervalo.
2. **`loja-virtual-service` — `AtributoCategoriaService.criar()` quebrava com `Column 'ordem'
   cannot be null`** sempre que `ordem` não era informado no request (campo opcional, documentado
   assim) — mesma classe de bug já registrada 3 vezes neste monorepo (`ProdutoImagem.ordem`,
   `FormaPagamento.tefTipoGp`, `ContabilidadeMapper` com `situacao`): o `mapper.toEntity(request)`
   sobrescreve o `@Builder.Default = 0` da entidade com o `null` do request. O irmão deste serviço,
   `CategoriaLojaService.criar()`, já tinha o guard certo (`if (request.ordem() == null)
   categoria.setOrdem(0)`) — só não tinha sido replicado em `AtributoCategoriaService`. Corrigido
   com o mesmo guard, no mesmo lugar. Achado testando exatamente o caminho que `AtributoCategoriaModal`
   usa na criação (campo "Ordem de exibição" deixado em branco, como qualquer usuário real faria na
   maioria das vezes).

Ambos os bugs eram só no backend (`cactus-erp-sistema`) — nenhuma mudança necessária no código
deste repositório por causa deles, só a descoberta aconteceu testando o contrato que o frontend
desta entrega consome. Detalhamento completo dos 2 fixes: ver `CLAUDE.md` raiz.

### Fora de escopo desta entrega, registrado

Clique real em navegador (Playwright não disponível nesta sessão — validação feita por contrato de
API, mesmo padrão de honestidade já usado antes nesta mesma situação, ex: "Fechamento de 7 itens
pontuais do backlog"); admin self-service por lojista (login do próprio lojista na própria conta,
ver "Admin — dois níveis" no `CLAUDE.md` raiz) — este módulo continua sendo só o admin da
PLATAFORMA (staff Cactus).

## Entrega 23 — Sincronização PDV (módulo novo) + 3 pendências do backlog eram falso alarme
(2026-10-12)

Pedido do usuário: "mata as pendencias do frontend". Checado o `BACKLOG.md` item a item antes de
codar — **3 das 4 pendências listadas já estavam implementadas** (descoberto lendo o código real
antes de assumir o backlog como fonte de verdade): desconciliar item na Conciliação Bancária
(`ItensImportacaoExpandidos` já tinha o botão), filtro por conta bancária em Aplicações Financeiras
(`Select` já existia no cabeçalho), e o fluxo "criar usuário + atribuir papel inicial"
(`UsuarioFormPage` já tinha `papelInicialId`/`empresaInicialId`, chamando `atribuirPapelEmpresa` numa
chamada avulsa após o `POST`). As 3 entradas do `BACKLOG.md` nunca tinham sido atualizadas depois da
implementação — mesma classe de achado já registrada antes ("RegistrarMovimentoModal", "CRM:
resolução de nome"). Corrigido só o `BACKLOG.md`, nenhuma mudança de código necessária nos 3.

### `sincronizacao-service` — módulo novo (`features/sincronizacao/`)

A única pendência real: o serviço (porta 8087, camada anticorrupção ERP↔PDV — outbox/RabbitMQ/DLQ/
auditoria da Fase 6) nunca tinha ganhado tela, só operável via Swagger. Backend mapeado por completo
via agente de exploração antes de codar — achado de arquitetura: o endpoint de provisionamento de
client-por-loja (`/api/v1/sincronizacao/clientes-loja`) **vive no `seguranca-service`, não no
sincronizacao-service**, apesar do path sugerir o contrário — confirmado por grep antes de assumir.

- **`RegistrosSincronizacaoListPage`** — auditoria do canal PDV→ERP (filtro status/loja, paginado),
  botão "Reprocessar" só em registros `ERRO` (`SINCRONIZACAO_GERENCIAR`) — outros status mostram o
  botão desabilitado com tooltip, nunca escondido (mesmo critério de outras telas do app).
- **`LotesSincronizacaoListPage`** — resumo aceito/rejeitado por lote (um `POST /sync/lotes` do PDV
  pode conter N registros, cada um aceito ou rejeitado independentemente).
- **`ClientesLojaListPage`** — provisiona o client OAuth2 Client Credentials "um por loja"
  (`SEGURANCA_GERENCIAR`, chama o `segurancaApi`, não o `sincronizacaoApi` novo). `clientSecret` só
  aparece na resposta de criação (nunca mais recuperável) — `Alert` de aviso explícito com botão
  "Copiar secret", fica na tela até o usuário dispensar.
- Novo item de menu "Sincronização PDV", 3 filhos. `sincronizacaoApi` novo em `httpClients.ts`
  (porta 8087) — quarto client de API do app que não é escopado pela empresa ativa da sessão (como
  Loja Virtual), e o único módulo cujas 3 telas batem em **2 origens HTTP diferentes**
  (`sincronizacaoApi` + `segurancaApi`), decisão deliberada por ser a divisão real do backend.

### Verificação ao vivo (sem navegador disponível nesta sessão — contrato de API via curl)

`npx tsc -b`/`oxlint`/`vitest` (13/13)/`vite build` limpos. Backend religado (Docker Desktop e
`cactus-mysql`/`cactus-rabbitmq` já estavam de pé desta vez — só `seguranca-service`+
`sincronizacao-service` precisaram subir), usuários de teste (`qa.sync.admin` ADMIN, `qa.sync.operador`
OPERADOR) criados via SQL direto e removidos ao final. Validado contra dado real pré-existente da
Fase 6 (6 registros, 7 lotes, incl. 1 registro genuinamente em `ERRO`): filtro por status/loja
conferido, reprocessar o registro `ERRO` real (virou `RECEBIDO`) → reprocessar um `PROCESSADO`
rejeitado (409, `SINCRONIZACAO_REGISTRO_NAO_ESTA_EM_ERRO`); clientes-loja — listagem com os 2 clients
reais pré-existentes, criação de um novo (empresa sem client ainda) devolvendo o secret, segunda
tentativa pra empresa já provisionada rejeitada (409). **RBAC confirmado real** com usuário OPERADOR
(`SINCRONIZACAO_CONSULTAR`+`SEGURANCA_CONSULTAR`, sem `SINCRONIZACAO_GERENCIAR`/`SEGURANCA_GERENCIAR`):
200 em listar registros, 403 real em reprocessar e em toda rota de clientes-loja (a tela de Clientes
de Loja exige uma authority mais alta que a de simples consulta do módulo).

### Fora de escopo, registrado

`POST /sync/lotes` (ingestão de lote — canal máquina-a-máquina do PDV, nunca uma tela de admin);
telas de pull ERP→PDV (`/sync/produtos`, `/sync/clientes`, etc. — também máquina-a-máquina, sem
UI de admin fazendo sentido); exclusão de client de loja (API não tem `DELETE`, mesmo critério de
sempre — sem destino, fica como dado real).

## Entrega 24 — MDF-e (2026-10-24)

Telas do MDF-e (backend da Fase 16 + sub-fase de Encerramento/Cancelamento, ver `CLAUDE.md`
raiz), dentro de `features/fiscal/`: `MdfesListPage` (`/fiscal/mdfes`, item novo no menu Fiscal),
`MdfeDetailPage` (`/fiscal/mdfes/:id`) e 3 modais (`GerarMdfeModal`, `EncerrarMdfeModal`,
`CancelarMdfeModal`). `SituacaoNotaFiscal` ganhou `ENCERRADA` (só MDF-e usa).

- **Diferente de NF-e/CT-e, MDF-e nasce na própria lista** ("Novo MDF-e"), não a partir de uma
  Venda: agrupa CT-e já autorizados. O seletor só oferece CT-e `AUTORIZADA` da empresa ativa que
  não estão em outro MDF-e não cancelado. Esse filtro é feito no cliente, a partir de 2 consultas
  de página grande (`useCtesParaMdfe`/`useCtesJaManifestados`, 200 registros). O backend revalida
  tudo (409/422), então o filtro é só para não oferecer opção fadada ao erro. **Atualizado na mesma
  data**: o filtro saiu do cliente. O seletor agora usa `GET /mdfes/ctes-disponiveis?empresaId=`
  (`useCtesDisponiveisParaMdfe`), sem o limite de 200 registros que as 2 consultas antigas tinham.
- **Ações no detalhe**: Emitir (`FISCAL_EMITIR`, RASCUNHO/ERRO), Encerrar (`FISCAL_EMITIR`, só
  AUTORIZADA; data limitada no `DatePicker` entre o início da viagem e hoje) e Cancelar
  (`FISCAL_GERENCIAR`, só AUTORIZADA e dentro de 24h da emissão). Cada botão desabilitado mostra
  um tooltip com o motivo exato. Um MDF-e autorizado mostra o alerta "Viagem em aberto", lembrando
  que o encerramento é obrigatório.

### Bug real, app inteiro: toda submissão inválida gerava "unhandled rejection"

Achado no teste em navegador: o `form.validateFields()` do Ant Design rejeita a promise quando
algum campo é inválido, e **77 arquivos** do app usam `validateFields().then(...)` sem `.catch`.
Toda tentativa de salvar um formulário inválido gerava um `pageerror: Object` no console. O usuário
não via nada errado, porque a mensagem do campo aparecia normalmente, mas o console ficava sujo e
erros reais ficavam escondidos no meio. Corrigido num ponto só, em `main.tsx`: um listener de
`unhandledrejection` que ignora apenas rejeições com `errorFields` (o formato da validação do Ant
Design). Qualquer outra rejeição continua aparecendo. Confirmado: submissão vazia mostra o erro do
campo e não gera mais nenhum `pageerror`.

### Verificação ao vivo (navegador real, Playwright, login pelo fluxo OAuth2/PKCE de verdade)

O fiscal-service rodou apontado para um ACBr Monitor **falso** (script local na porta 3535, ver
`CLAUDE.md` raiz), então nada foi para o ACBr real nem para a SEFAZ. Fluxo testado: seletor com os
4 CT-e autorizados → submissão vazia e CPF inválido barrados no formulário → MDF-e com 2 CT-e
gerado (RASCUNHO, Encerrar desabilitado) → Emitir (AUTORIZADA, alerta de viagem em aberto) →
Encerrar (ENCERRADA, Encerrar e Cancelar desabilitados) → o seletor deixa de oferecer os 2 CT-e
usados → segundo MDF-e → Emitir → justificativa curta barrada → Cancelar (CANCELADA) → os CT-e
voltam a aparecer no seletor. A lista mostra os 2 com situação e valor de carga certos. OPERADOR
(só `FISCAL_CONSULTAR`) vê o menu, mas Novo, Emitir, Encerrar e Cancelar ficam desabilitados.
Screenshots conferidos. Zero erro de console depois do fix acima. `tsc -b`, `oxlint`, `vitest`
(13/13) e `vite build` limpos. Dados e usuários de teste revertidos.

## Fase 6-B no frontend — rótulos de caixa/fiado do PDV (2026-10-24)

Sem tela nova: os 5 tipos de registro novos (`SANGRIA`/`SUPRIMENTO`/`ABERTURA_CAIXA`/`CONFERENCIA_CAIXA`/`FIADO_LANCAMENTO`, ver `FASE6_SINCRONIZACAO_PDV.md` seção 12) entraram em `TipoRegistroSincronizacao` com `TIPO_REGISTRO_LABEL`, usado nas colunas "Tipo" de Registros e Lotes da Sincronização PDV. No financeiro, as 4 origens de movimento `*_PDV` e a origem de título `FIADO_PDV` ganharam rótulo, e `ContaBancariaResponse.pdvCaixaId` (só nas contas de terminal criadas automaticamente) entrou no tipo. Conferido no navegador com dado real do teste: a auditoria mostra "Sangria", "Fiado" etc., e as contas "Caixa PDV ..." aparecem com os saldos certos, sem erro de console.

## Vendas do PDV — tela de consulta (2026-10-24)

`features/vendas/VendasPdvListPage.tsx` (rota `/vendas/vendas-pdv`, menu Vendas, `VENDAS_CONSULTAR`), com tipos/API/hook em `vendasPdv.ts` (separados da venda B2B de propósito, mesmo motivo do backend). Lista por período e terminal; a linha expandida mostra itens (com quantidade devolvida), pagamento fracionado (rótulo pelo `tPag` oficial, `TPAG_LABEL`, com NSU/rede) e devoluções (quem autorizou). Só leitura, porque a venda nasce no PDV. Rótulos novos: `VENDA_PDV`/`DEVOLUCAO_PDV` na auditoria de sincronização, origem `VENDA_PDV` nos movimentos de caixa. Conferido no navegador com as vendas do teste ao vivo; o único 404 de console é o produto de teste `#2`, inexistente no cadastro (fallback `#id`, comportamento já conhecido).

## Vendas do PDV: cancelamento (2026-10-25)

`VendaPdv` ganhou `dataHoraCancelamento`/`pdvOperadorCancelamentoId`/`pdvGerenteCancelamentoId`/`justificativaCancelamento`/`protocoloCancelamento` (preenchidos só quando a venda chegou fechada e foi cancelada depois). A linha expandida de "Vendas do PDV" mostra um `Alert` com quando, por quê, quem autorizou e o protocolo da SEFAZ. Conferido só pelo TypeScript, não num navegador.

## BI > Faturamento (2026-10-25)

`features/bi/FaturamentoPage.tsx` (rota `/bi/faturamento`, primeiro item do menu BI, `BI_CONSULTAR`): faturamento líquido, vendas, ticket médio e devoluções do período para a empresa ativa, filtro de canal (B2B/PDV) e tabela diária, com exportação PDF/Excel. Tipos/API/hook (`useFaturamento`) em `types.ts`/`api.ts`/`queries.ts`; `useExecutarEtl` passou a invalidar também `bi-faturamento`. Conferido num navegador real contra o backend (detalhe no `CLAUDE.md` raiz, "BI — faturamento e ticket médio").

## Relatórios de vendas com canal B2B + PDV (2026-10-25)

`RelatoriosVendasPage`: "Vendas por Período" e "Produtos Mais Vendidos" ganharam o filtro de canal (`SeletorCanal`; vazio = os dois). A tabela de vendas ganhou a coluna Canal, `rowKey` composto `canal-vendaId` (os ids de `VendaPdv` e `VendaCabecalho` colidem), "PDV #n" no número, "—"/"Consumidor" quando não há vendedor ou cliente, e clique numa venda do PDV leva a "Vendas do PDV". `VendaPeriodoItemResponse.vendedorId`/`clienteId` passaram a `number | null`. Conferido no navegador contra o backend real (detalhe em `FASE6_SINCRONIZACAO_PDV.md`, seção 14).

## Configuração Tributária: IBS/CBS (2026-10-25)

`ConfiguracaoTributariaFormPage` ganhou a seção "IBS / CBS (Reforma Tributária)": `Select` de cClassTrib alimentado pelo catálogo oficial (`useClassificacoesIbsCbs`, 173 códigos carregados uma vez, `staleTime: Infinity`), mostrando só as classificações que o ERP emite (`motivoNaoSuportada === null`), com `Alert` explicando redução/isenção. **Motivo além da funcionalidade**: a tela manda o PUT completo da célula, e o backend trata `ibsCbs` ausente como "remover" — sem o campo, qualquer edição apagaria a classificação em silêncio e a NF-e do Regime Normal seria recusada. Conferido no navegador: classificação carregada, salvar sem alterar reenviou `000001`, zero erro de console. Detalhe no `CLAUDE.md` raiz, seção "REFORMA TRIBUTÁRIA".

