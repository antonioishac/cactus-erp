import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Table, Tag, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { ClienteResumoCell } from './ResumoCells'
import { useVendas } from './queries'
import { SITUACAO_VENDA_LABEL } from './types'
import type { VendasFiltro, VendaResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_SITUACAO: Record<string, string> = {
  RASCUNHO: 'default',
  CONFIRMADA: 'success',
  CANCELADA: 'error',
}

export default function VendasListPage() {
  const navigate = useNavigate()
  const [filtro] = useState<VendasFiltro>({})
  const [pagina, setPagina] = useState(0)
  const { data, isLoading, isPlaceholderData } = useVendas(filtro, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Vendas
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/vendas/vendas/novo')}>
          Nova Venda Avulsa
        </Button>
      </Flex>

      <Card>
        <Table<VendaResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(venda) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/vendas/vendas/${venda.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} venda(s)`,
          }}
          columns={[
            { title: 'Id', dataIndex: 'id', width: 80 },
            { title: 'Cliente', key: 'cliente', render: (_, venda) => <ClienteResumoCell clienteId={venda.clienteId} /> },
            { title: 'Data', dataIndex: 'dataVenda', render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY') : '—') },
            { title: 'Origem', dataIndex: 'vendaOrcamentoId', render: (v?: number) => (v ? `Orçamento #${v}` : 'Avulsa') },
            { title: 'Total', dataIndex: 'valorTotal', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (situacao: keyof typeof SITUACAO_VENDA_LABEL) => (
                <Tag color={COR_SITUACAO[situacao]}>{SITUACAO_VENDA_LABEL[situacao]}</Tag>
              ),
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
