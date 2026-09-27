import { useMemo, useState } from 'react'
import { Layout, Menu } from 'antd'
import { HomeOutlined } from '@ant-design/icons'
import { useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '@features/auth/useAuth'
import { hasAnyAuthority } from '@features/auth/authority'
import { MODULE_NAV } from '@shared/ui/NavConfig'
import CactusMark from '@shared/ui/CactusMark'
import { CACTUS_DARK } from '@app/theme/antdTheme'

const { Sider } = Layout

export default function AppSider() {
  const [collapsed, setCollapsed] = useState(false)
  const { authorities } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()

  // Só mostra o link do módulo (ou sub-item) se o usuário tiver ALGUMA das
  // autoridades relevantes — mesmo predicado usado pela guarda de rota
  // (RequireAuthority), ver features/auth/authority.ts. Sub-item sem nenhuma
  // autoridade visível também esconde o pai se ele não tiver mais nada pra
  // mostrar (evita "Cadastro Base" vazio, sem Clientes nem Fornecedores).
  const itensVisiveis = useMemo(
    () =>
      MODULE_NAV.filter((entry) => hasAnyAuthority(authorities, entry.requiredAuthorities)).map((entry) => ({
        ...entry,
        children: entry.children?.filter((child) => hasAnyAuthority(authorities, child.requiredAuthorities)),
      })),
    [authorities],
  )

  const items = [
    { key: '/', icon: <HomeOutlined />, label: 'Início' },
    ...itensVisiveis.map((entry) =>
      entry.children && entry.children.length > 0
        ? {
            key: entry.path,
            icon: entry.icon,
            label: entry.label,
            children: entry.children.map((child) => ({ key: child.path, label: child.label })),
          }
        : { key: entry.path, icon: entry.icon, label: entry.label },
    ),
  ]

  // Chave selecionada: o path mais ESPECÍFICO que bate com a URL atual (evita
  // destacar só "Cadastro Base" quando na verdade está em "Clientes").
  const todasAsChaves = itensVisiveis.flatMap((entry) => [entry.path, ...(entry.children?.map((c) => c.path) ?? [])])
  const selectedKey =
    todasAsChaves
      .filter((key) => location.pathname.startsWith(key))
      .sort((a, b) => b.length - a.length)[0] ?? '/'
  const paiAberto = itensVisiveis.find((entry) => entry.children?.some((c) => c.path === selectedKey))?.path

  return (
    <Sider collapsible collapsed={collapsed} onCollapse={setCollapsed} width={232} theme="dark">
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: collapsed ? 'center' : 'flex-start',
          gap: 12,
          padding: '18px 16px',
          background: CACTUS_DARK,
          borderBottom: '1px solid rgba(255,255,255,0.08)',
        }}
      >
        {/* Marca vetorial (SVG), não mais um recorte raster da foto original — ver
            CactusMark.tsx pro porquê (recorte de JPEG pequeno sempre ficava
            "cortado no Paint", não importa o ajuste; SVG renderiza nítido em
            qualquer tamanho). */}
        <div style={{ flexShrink: 0 }}>
          <CactusMark size={34} />
        </div>
        {!collapsed && (
          <span style={{ color: '#fff', fontWeight: 600, fontSize: 16, letterSpacing: 0.2 }}>
            Cactus ERP
          </span>
        )}
      </div>
      <Menu
        theme="dark"
        mode="inline"
        selectedKeys={[selectedKey]}
        defaultOpenKeys={paiAberto ? [paiAberto] : []}
        items={items}
        onClick={({ key }) => navigate(key)}
      />
    </Sider>
  )
}
