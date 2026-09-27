import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Table, Tag, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import SituacaoTag from '@shared/ui/SituacaoTag'
import { useDepositos } from './queries'
import { TIPO_DEPOSITO_LABEL } from './types'
import type { DepositoResponse } from './types'

const { Title } = Typography

/**
 * Sem paginador de busca por texto (backend só filtra por `empresaId`, ver
 * `DepositoController`) — a lista já é escopada pela empresa ativa
 * (`empresaAtivaId`), então a tabela naturalmente já é pequena por empresa.
 */
export default function DepositosListPage() {
  const navigate = useNavigate()
  const { empresaAtivaId } = useAuth()
  const { data, isLoading } = useDepositos({ empresaId: empresaAtivaId ?? undefined }, 0, 100)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Depósitos
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/estoque/depositos/novo')}>
          Novo Depósito
        </Button>
      </Flex>

      <Card>
        <Table<DepositoResponse>
          rowKey="id"
          loading={isLoading}
          dataSource={data?.conteudo ?? []}
          pagination={false}
          onRow={(deposito) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/estoque/depositos/${deposito.id}`),
          })}
          columns={[
            { title: 'Código', dataIndex: 'codigo', width: 120 },
            { title: 'Nome', dataIndex: 'nome' },
            {
              title: 'Tipo',
              dataIndex: 'tipo',
              render: (tipo: keyof typeof TIPO_DEPOSITO_LABEL) => <Tag>{TIPO_DEPOSITO_LABEL[tipo]}</Tag>,
            },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (situacao) => <SituacaoTag situacao={situacao} />,
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
