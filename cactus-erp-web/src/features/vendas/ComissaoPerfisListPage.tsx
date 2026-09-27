import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Table, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import SituacaoTag from '@shared/ui/SituacaoTag'
import { useComissaoPerfis } from './queries'
import type { ComissaoPerfilResponse } from './types'

const { Title } = Typography

/** Sem paginação própria no backend (`GET /comissao-perfis?size=100`) — catálogo pequeno, mesmo critério de outros lookups do sistema. */
export default function ComissaoPerfisListPage() {
  const navigate = useNavigate()
  const { data, isLoading } = useComissaoPerfis()

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Perfis de Comissão
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/vendas/comissao-perfis/novo')}>
          Novo Perfil
        </Button>
      </Flex>

      <Card>
        <Table<ComissaoPerfilResponse>
          rowKey="id"
          loading={isLoading}
          dataSource={data?.conteudo ?? []}
          pagination={false}
          onRow={(perfil) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/vendas/comissao-perfis/${perfil.id}`),
          })}
          columns={[
            { title: 'Código', dataIndex: 'codigo', render: (v?: string) => v || '—' },
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Situação', dataIndex: 'situacao', render: (situacao) => <SituacaoTag situacao={situacao} /> },
          ]}
        />
      </Card>
    </Flex>
  )
}
