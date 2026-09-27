import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Table, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import SituacaoTag from '@shared/ui/SituacaoTag'
import { useCondicoesPagamento } from './queries'
import { TIPO_PAGAMENTO_VENDA_LABEL } from './types'
import type { VendaCondicoesPagamentoResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function CondicoesPagamentoListPage() {
  const navigate = useNavigate()
  const [pagina, setPagina] = useState(0)
  const { data, isLoading, isPlaceholderData } = useCondicoesPagamento(pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Condições de Pagamento
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/vendas/condicoes-pagamento/novo')}>
          Nova Condição
        </Button>
      </Flex>

      <Card>
        <Table<VendaCondicoesPagamentoResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(condicao) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/vendas/condicoes-pagamento/${condicao.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} condição(ões)`,
          }}
          columns={[
            { title: 'Nome', dataIndex: 'nome' },
            {
              title: 'À vista/A prazo',
              dataIndex: 'vistaPrazo',
              render: (v?: keyof typeof TIPO_PAGAMENTO_VENDA_LABEL) => (v ? TIPO_PAGAMENTO_VENDA_LABEL[v] : '—'),
            },
            {
              title: 'Prazo médio (dias)',
              dataIndex: 'prazoMedio',
              align: 'right',
              render: (v?: number) => v ?? '—',
            },
            {
              title: 'Parcelas',
              key: 'parcelas',
              align: 'right',
              render: (_, condicao) => condicao.parcelas?.length ?? 0,
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
