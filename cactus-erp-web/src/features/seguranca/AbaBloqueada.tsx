import { Empty } from 'antd'
import { LockOutlined } from '@ant-design/icons'

/** Papéis por Empresa é sub-recurso (`/api/v1/usuarios/{id}/papeis-empresa`) — só existe com o usuário já salvo. Mesmo padrão de `features/empresas/AbaBloqueada.tsx`. */
export default function AbaBloqueada({ mensagem }: { mensagem: string }) {
  return (
    <Empty
      image={<LockOutlined style={{ fontSize: 40, color: '#bfbfbf' }} />}
      description={mensagem}
      style={{ padding: '48px 0' }}
    />
  )
}
