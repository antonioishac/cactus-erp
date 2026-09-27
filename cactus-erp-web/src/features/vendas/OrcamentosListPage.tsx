import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Table, Tag, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { ClienteResumoCell } from './ResumoCells'
import { useOrcamentos } from './queries'
import { SITUACAO_ORCAMENTO_LABEL } from './types'
import type { OrcamentosFiltro, VendaOrcamentoResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_SITUACAO: Record<string, string> = {
  RASCUNHO: 'default',
  ENVIADO: 'processing',
  APROVADO: 'gold',
  CONVERTIDO: 'success',
  EXPIRADO: 'default',
  CANCELADO: 'error',
}

export default function OrcamentosListPage() {
  const navigate = useNavigate()
  const [filtro] = useState<OrcamentosFiltro>({})
  const [pagina, setPagina] = useState(0)
  const { data, isLoading, isPlaceholderData } = useOrcamentos(filtro, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Orçamentos
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/vendas/orcamentos/novo')}>
          Novo Orçamento
        </Button>
      </Flex>

      <Card>
        <Table<VendaOrcamentoResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(orcamento) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/vendas/orcamentos/${orcamento.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} orçamento(s)`,
          }}
          columns={[
            { title: 'Id', dataIndex: 'id', width: 80 },
            { title: 'Código', dataIndex: 'codigo', render: (v?: string) => v || '—' },
            { title: 'Cliente', key: 'cliente', render: (_, orcamento) => <ClienteResumoCell clienteId={orcamento.clienteId} /> },
            { title: 'Data', dataIndex: 'dataCadastro', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
            { title: 'Validade', dataIndex: 'validade', render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY') : '—') },
            { title: 'Total', dataIndex: 'valorTotal', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (situacao: keyof typeof SITUACAO_ORCAMENTO_LABEL) => (
                <Tag color={COR_SITUACAO[situacao]}>{SITUACAO_ORCAMENTO_LABEL[situacao]}</Tag>
              ),
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
