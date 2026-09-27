import { Empty } from 'antd'
import { LockOutlined } from '@ant-design/icons'

/**
 * Regime Tributário/Configuração Operacional/Configuração Fiscal são
 * sub-recursos com endpoint próprio (`/api/v1/empresas/{id}/...`) — só
 * existem depois que a empresa tem um `id` de verdade. Ao criar uma empresa
 * nova, essas abas ficam desabilitadas no `Tabs` (ver `EmpresaFormPage`); este
 * componente é o conteúdo de fallback caso o usuário force acesso por rota
 * direta antes de salvar, deixando claro o porquê em vez de um formulário
 * vazio confuso ou um 404 de API.
 */
export default function AbaBloqueada() {
  return (
    <Empty
      image={<LockOutlined style={{ fontSize: 40, color: '#bfbfbf' }} />}
      description="Salve os dados gerais da empresa primeiro para liberar esta configuração."
      style={{ padding: '48px 0' }}
    />
  )
}
