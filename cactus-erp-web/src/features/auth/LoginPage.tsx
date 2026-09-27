import { useEffect, useState } from 'react'
import { Alert, App, Button, Flex, Typography } from 'antd'
import { LoginOutlined } from '@ant-design/icons'
import { Navigate } from 'react-router-dom'
import { useAuth } from './useAuth'
import { CACTUS_DARK, CACTUS_GREEN } from '@app/theme/antdTheme'

const { Title, Text, Paragraph } = Typography

// Pilares, NUNCA nomes de módulo/funcionalidade específica (nem "cadastro",
// "estoque" etc.) — instrução explícita do usuário: a lista de módulos só cresce,
// então travar nomes aqui ficaria desatualizado rápido, e citar módulo por módulo
// (principalmente "cadastro") pareceu limitado, não o "orgulho de usar" que ele
// pediu. Achado real: a versão anterior ainda vazava "Cadastros, suprimentos..."
// na descrição do primeiro pilar — corrigido, mantendo o conceito só em nível
// abstrato (unificação, dados, segurança), sem citar NENHUMA área funcional.
const PILARES = [
  {
    titulo: 'Operação unificada',
    descricao: 'Toda a operação da sua empresa, em um só lugar, sem retrabalho.',
  },
  {
    titulo: 'Decisões com dados',
    descricao: 'Inteligência de negócio nativa, não um complemento.',
  },
  {
    titulo: 'Segurança de verdade',
    descricao: 'Controle de acesso, auditoria e arquitetura multiempresa desde a fundação.',
  },
]

export default function LoginPage() {
  const { message } = App.useApp()
  const { isAuthenticated, initialized, signIn } = useAuth()
  const [entrando, setEntrando] = useState(false)
  const [erro, setErro] = useState<string | null>(null)

  // Bug real reportado pelo usuário (2026-09-01): clicar "Entrar" liga o
  // spinner e navega pro Authorization Server (signinRedirect, full page
  // navigation) — não existe "sucesso" client-side pra desligar o loading, o
  // comentário de handleEntrar já assumia isso. Mas se o usuário aperta
  // "voltar" no navegador, o Chrome/Firefox restaura esta página do bfcache
  // (back-forward cache) EXATAMENTE como estava congelada — inclusive
  // `entrando=true` — sem rodar nenhum código novo, então o spinner ficava
  // preso pra sempre até um refresh forçar recarregar do zero. `pageshow`
  // com `event.persisted=true` é o evento padrão do browser pra "esta página
  // voltou do bfcache, não foi carregada de novo" — reseta o estado nesse caso.
  useEffect(() => {
    function handlePageShow(event: PageTransitionEvent) {
      if (event.persisted) {
        setEntrando(false)
      }
    }
    window.addEventListener('pageshow', handlePageShow)
    return () => window.removeEventListener('pageshow', handlePageShow)
  }, [])

  // Sessão já válida (ex: usuário voltou pra /login por engano) — não mostra o
  // formulário de novo, manda direto pro shell.
  if (initialized && isAuthenticated) {
    return <Navigate to="/" replace />
  }

  // signinRedirect() faz descoberta OIDC (GET /.well-known/openid-configuration)
  // antes de redirecionar — se isso falhar (backend fora do ar, CORS mal
  // configurado, etc.) a promise rejeita e, sem este catch, nada aparece na
  // tela pro usuário (achado real desta sessão — ver CLAUDE.md raiz, entrada
  // "BUG REAL encontrado testando o login de ponta a ponta").
  async function handleEntrar() {
    setErro(null)
    setEntrando(true)
    try {
      await signIn()
    } catch (err) {
      console.error('[auth] Falha ao iniciar login', err)
      setErro(
        'Não foi possível iniciar o login. Verifique se o servidor de autenticação (seguranca-service, porta 8082) está no ar.',
      )
      message.error('Falha ao iniciar login.')
      setEntrando(false)
    }
    // Em caso de sucesso o navegador já está sendo redirecionado — não há
    // "sucesso" pra tratar aqui, só o caminho de erro precisa desligar o loading.
  }

  return (
    <div className="login-split">
      {/* Painel de marca — mesma classe CSS (index.css) que o LoginPageController
          do backend usa (.login-brand-panel lá é .brand-panel, valores idênticos:
          flex 42%/max-width 560px/breakpoint 768px) — de propósito, pra não
          divergir entre as duas telas do mesmo fluxo (achado real reportado pelo
          usuário: "o layout se move" navegando de uma pra outra em tela larga,
          porque o grid do Ant Design (Row/Col) é % pura sem teto, e o backend já
          tinha um teto de 560px). */}
      <div className="login-brand-panel" style={{ background: CACTUS_DARK }}>
        <div
          aria-hidden
          style={{
            position: 'absolute',
            top: -120,
            right: -120,
            width: 360,
            height: 360,
            borderRadius: '50%',
            background: CACTUS_GREEN,
            opacity: 0.15,
            filter: 'blur(10px)',
          }}
        />
        <div
          aria-hidden
          style={{
            position: 'absolute',
            bottom: -160,
            left: -100,
            width: 420,
            height: 420,
            borderRadius: '50%',
            background: CACTUS_GREEN,
            opacity: 0.1,
            filter: 'blur(10px)',
          }}
        />

        <div style={{ position: 'relative', maxWidth: 400 }}>
          <div style={{ width: 40, height: 4, borderRadius: 2, background: CACTUS_GREEN, marginBottom: 20 }} />
          <Title level={2} style={{ color: '#fff', margin: 0, fontWeight: 700 }}>
            Cactus <span style={{ color: CACTUS_GREEN }}>ERP</span>
          </Title>
          <Paragraph style={{ color: 'rgba(255,255,255,0.85)', marginTop: 12, fontSize: 19, fontWeight: 600, lineHeight: 1.4 }}>
            Menos burocracia, mais controle: o ERP que move sua empresa.
          </Paragraph>

          <div style={{ height: 1, background: 'rgba(255,255,255,0.12)', margin: '28px 0' }} />

          <Flex vertical gap={22}>
            {PILARES.map((pilar) => (
              <div key={pilar.titulo}>
                <Text strong style={{ color: '#fff', fontSize: 15, display: 'block' }}>
                  {pilar.titulo}
                </Text>
                <Text style={{ color: 'rgba(255,255,255,0.65)', fontSize: 14, lineHeight: 1.5 }}>
                  {pilar.descricao}
                </Text>
              </div>
            ))}
          </Flex>
        </div>
      </div>

      {/* Painel do formulário — mesma classe .login-form-panel do backend. */}
      <div className="login-form-panel" style={{ background: '#fff' }}>
        <div style={{ width: '100%', maxWidth: 340 }}>
          <Flex vertical align="center" gap={4} style={{ marginBottom: 40 }}>
            <img
              src="/cactus-logo.jpg"
              alt="Cactus Digital"
              style={{ width: 88, height: 88, objectFit: 'contain' }}
            />
            <Title level={3} style={{ margin: 0, marginTop: 16 }}>
              Bem-vindo
            </Title>
            <Text type="secondary">Acesse sua conta para continuar</Text>
          </Flex>

          <Button type="primary" size="large" icon={<LoginOutlined />} block loading={entrando} onClick={handleEntrar}>
            Entrar
          </Button>

          {erro && <Alert type="error" showIcon title={erro} style={{ marginTop: 16 }} />}

          <Text
            type="secondary"
            style={{ display: 'block', textAlign: 'center', marginTop: 24, fontSize: 12 }}
          >
            Você será redirecionado para a tela de autenticação segura do Cactus ERP.
          </Text>
        </div>
      </div>
    </div>
  )
}
