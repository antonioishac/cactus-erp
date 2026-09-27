import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Table, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import SituacaoTag from '@shared/ui/SituacaoTag'
import { ColaboradorResumoCell } from './ResumoCells'
import { useVendedores } from './queries'
import type { VendedorResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function VendedoresListPage() {
  const navigate = useNavigate()
  const [pagina, setPagina] = useState(0)
  const { data, isLoading, isPlaceholderData } = useVendedores(pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Vendedores
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/vendas/vendedores/novo')}>
          Novo Vendedor
        </Button>
      </Flex>

      <Card>
        <Table<VendedorResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(vendedor) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/vendas/vendedores/${vendedor.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} vendedor(es)`,
          }}
          columns={[
            {
              title: 'Colaborador',
              key: 'colaborador',
              render: (_, vendedor) => <ColaboradorResumoCell colaboradorId={vendedor.colaboradorId} />,
            },
            {
              title: 'Perfil de comissão',
              dataIndex: 'comissaoPerfilNome',
              render: (v?: string) => v || '—',
            },
            {
              title: 'Comissão (%)',
              dataIndex: 'comissaoPercentual',
              align: 'right',
              render: (v?: number) => (v != null ? `${v.toFixed(2)}%` : '—'),
            },
            {
              title: 'Meta de venda',
              dataIndex: 'metaVenda',
              align: 'right',
              render: (v?: number) => (v != null ? `R$ ${v.toFixed(2)}` : '—'),
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
