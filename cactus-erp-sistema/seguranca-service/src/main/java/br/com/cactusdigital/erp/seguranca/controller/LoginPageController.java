package br.com.cactusdigital.erp.seguranca.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Base64;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Página HTML de login com a marca do Cactus ERP — servida diretamente aqui (HTML
 * estático com CSS inline, sem template engine) porque é a ÚNICA tela humana de todo
 * o backend (o resto é API REST pura); não justifica trazer Thymeleaf pra isso só.
 * <p>
 * Substitui o {@code DefaultLoginPageGeneratingFilter} genérico do Spring Security —
 * a página padrão dele ("Please sign in", cinza, sem identidade nenhuma) causou
 * confusão real testando com o usuário: parecia tela de erro/sistema quebrado, não a
 * etapa esperada do login (2026-08-31). Também elimina de vez os dois workarounds que
 * existiam em {@code ResourceServerConfig} pra fazer aquela página funcionar
 * ({@code DefaultLoginPageGeneratingFilter}/{@code DefaultResourcesFilter} registrados
 * na mão — ver `CLAUDE.md` raiz pro histórico) — com esta página própria, nenhum dos
 * dois é mais necessário.
 * <p>
 * Layout split-screen (painel de marca escuro à esquerda + formulário claro à
 * direita) espelhando exatamente o mesmo desenho de {@code LoginPage.tsx} do
 * `cactus-erp-web` — o usuário vê as duas telas em sequência no mesmo fluxo de login,
 * então a identidade visual precisa ser consistente entre as duas (2026-08-31,
 * segunda passada — a primeira versão, um card centralizado genérico com um ícone
 * placeholder em vez da logo real, não agradou).
 * <p>
 * O form posta pra {@code POST /login} com os campos {@code username}/{@code password}
 * (nomes padrão que {@code .formLogin(Customizer.withDefaults())} já espera, em
 * {@link br.com.cactusdigital.erp.seguranca.config.ResourceServerConfig} — não muda
 * nada da autenticação em si, só a página que o usuário vê). Falha de login redireciona
 * de volta aqui com {@code ?error} (também comportamento padrão do formLogin).
 */
@RestController
public class LoginPageController {

    /** Logo completa (`cactus-logo.jpg`, com o texto "CACTUS Digital" por extenso) —
     *  só usada onde há espaço de sobra pra ela ficar legível (o painel do formulário). */
    private static final String LOGO_BASE64 = loadImageBase64("/branding/cactus-logo.jpg");

    /** Ícone só-marca — SVG desenhado do zero (não recortado da foto original,
     *  ver `CactusMark.tsx` no frontend pro porquê: recorte raster de JPEG pequeno
     *  sempre ficava com borda áspera, "cortado no Paint"). Usado como favicon da
     *  aba do navegador. */
    private static final String FAVICON_BASE64 = loadImageBase64("/branding/favicon.svg");

    @GetMapping(value = "/login", produces = MediaType.TEXT_HTML_VALUE)
    public String loginPage(@RequestParam(required = false) String error) {
        String errorBlock = error != null
                ? "<div class=\"error\">Usuário ou senha inválidos.</div>"
                : "";
        return HTML_TEMPLATE
                .replace("{{ERROR_BLOCK}}", errorBlock)
                .replace("{{LOGO_BASE64}}", LOGO_BASE64)
                .replace("{{FAVICON_BASE64}}", FAVICON_BASE64);
    }

    private static String loadImageBase64(String classpathLocation) {
        try (InputStream in = LoginPageController.class.getResourceAsStream(classpathLocation)) {
            if (in == null) {
                return "";
            }
            return Base64.getEncoder().encodeToString(in.readAllBytes());
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Falha ao carregar imagem da marca Cactus ERP pra tela de login: " + classpathLocation, e);
        }
    }

    private static final String HTML_TEMPLATE = """
            <!doctype html>
            <html lang="pt-BR">
            <head>
              <meta charset="utf-8">
              <meta name="viewport" content="width=device-width, initial-scale=1">
              <title>Cactus ERP — Entrar</title>
              <link rel="icon" type="image/svg+xml" href="data:image/svg+xml;base64,{{FAVICON_BASE64}}">
              <meta name="theme-color" content="#262825">
              <style>
                * { box-sizing: border-box; }
                html, body { margin: 0; height: 100%; }
                body {
                  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                  display: flex; min-height: 100vh;
                }

                /* Painel de marca (esquerda) — escondido em telas estreitas, mesmo
                   breakpoint (768px) usado pelo LoginPage.tsx do frontend. */
                .brand-panel {
                  flex: 0 0 42%;
                  max-width: 560px;
                  background: #262825;
                  position: relative;
                  overflow: hidden;
                  display: flex;
                  align-items: center;
                  padding: 0 64px;
                }
                .brand-panel::before, .brand-panel::after {
                  content: ""; position: absolute; border-radius: 50%; background: #8cc63f;
                  filter: blur(10px);
                }
                .brand-panel::before { width: 360px; height: 360px; top: -120px; right: -120px; opacity: .15; }
                .brand-panel::after { width: 420px; height: 420px; bottom: -160px; left: -100px; opacity: .10; }
                .brand-content { position: relative; max-width: 400px; }
                .brand-accent { width: 40px; height: 4px; border-radius: 2px; background: #8cc63f; margin-bottom: 20px; }
                .brand-content h1 { color: #fff; font-size: 28px; margin: 0; font-weight: 700; }
                .brand-content h1 span { color: #8cc63f; }
                .brand-content p.tagline { color: rgba(255,255,255,.85); font-size: 19px; font-weight: 600; margin-top: 12px; line-height: 1.4; }
                .divider { height: 1px; background: rgba(255,255,255,.12); margin: 28px 0; }
                .pillars { display: flex; flex-direction: column; gap: 22px; }
                .pillars strong { color: #fff; font-size: 15px; display: block; }
                .pillars p { color: rgba(255,255,255,.65); font-size: 14px; line-height: 1.5; margin: 2px 0 0; }

                /* Painel do formulário (direita) */
                .form-panel {
                  flex: 1; background: #fff; display: flex; align-items: center; justify-content: center; padding: 24px;
                }
                .form-inner { width: 100%; max-width: 340px; }
                .form-header { display: flex; flex-direction: column; align-items: center; margin-bottom: 40px; }
                .form-header img { width: 88px; height: 88px; object-fit: contain; }
                .form-header h2 { font-size: 20px; margin: 16px 0 0; color: #1a1a1a; }
                .form-header p { color: #8c8c8c; margin: 4px 0 0; font-size: 14px; }

                label { display: block; font-size: 13px; color: #1a1a1a; margin-bottom: 6px; font-weight: 500; }
                input {
                  width: 100%; padding: 10px 12px; margin-bottom: 16px; border: 1px solid #d9d9d9;
                  border-radius: 6px; font-size: 14px; font-family: inherit;
                }
                input:focus { outline: none; border-color: #8cc63f; box-shadow: 0 0 0 2px rgba(140,198,63,.25); }
                button {
                  width: 100%; padding: 11px; background: #8cc63f; color: #1a1a1a; font-weight: 600;
                  border: none; border-radius: 6px; font-size: 15px; cursor: pointer; font-family: inherit;
                }
                button:hover { background: #7ab332; }
                .error {
                  background: #fff1f0; border: 1px solid #ffa39e; color: #cf1322; padding: 10px 12px;
                  border-radius: 6px; font-size: 13px; margin-bottom: 16px;
                }
                .footer { text-align: center; margin-top: 24px; font-size: 12px; color: #8c8c8c; }

                @media (max-width: 768px) {
                  .brand-panel { display: none; }
                }
              </style>
            </head>
            <body>
              <div class="brand-panel">
                <div class="brand-content">
                  <div class="brand-accent"></div>
                  <h1>Cactus <span>ERP</span></h1>
                  <p class="tagline">Menos burocracia, mais controle: o ERP que move sua empresa.</p>
                  <div class="divider"></div>
                  <div class="pillars">
                    <div>
                      <strong>Operação unificada</strong>
                      <p>Toda a operação da sua empresa, em um só lugar, sem retrabalho.</p>
                    </div>
                    <div>
                      <strong>Decisões com dados</strong>
                      <p>Inteligência de negócio nativa, não um complemento.</p>
                    </div>
                    <div>
                      <strong>Segurança de verdade</strong>
                      <p>Controle de acesso, auditoria e arquitetura multiempresa desde a fundação.</p>
                    </div>
                  </div>
                </div>
              </div>
              <div class="form-panel">
                <div class="form-inner">
                  <div class="form-header">
                    <img src="data:image/jpeg;base64,{{LOGO_BASE64}}" alt="Cactus Digital">
                    <h2>Bem-vindo</h2>
                    <p>Entre com suas credenciais para continuar</p>
                  </div>
                  {{ERROR_BLOCK}}
                  <form method="post" action="/login">
                    <label for="username">Usuário</label>
                    <input id="username" name="username" type="text" autofocus required autocomplete="username">
                    <label for="password">Senha</label>
                    <input id="password" name="password" type="password" required autocomplete="current-password">
                    <button type="submit">Entrar</button>
                  </form>
                  <p class="footer">Cactus Digital &middot; Autenticação segura</p>
                </div>
              </div>
            </body>
            </html>
            """;
}
