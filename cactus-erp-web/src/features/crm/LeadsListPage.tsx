import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Table, Tag, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import { VendedorResumoCell } from './ResumoCells'
import { useLeads } from './queries'
import { ORIGEM_LEAD_LABEL, SITUACAO_LEAD_LABEL } from './types'
import type { LeadResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_SITUACAO: Record<string, string> = {
  NOVO: 'default',
  EM_QUALIFICACAO: 'processing',
  QUALIFICADO: 'success',
  DESQUALIFICADO: 'error',
  CONVERTIDO: 'success',
}

export default function LeadsListPage() {
  const navigate = useNavigate()
  const [pagina, setPagina] = useState(0)
  const { data, isLoading, isPlaceholderData } = useLeads(pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Leads
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/crm/leads/novo')}>
          Novo Lead
        </Button>
      </Flex>

      <Card>
        <Table<LeadResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(lead) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/crm/leads/${lead.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} lead(s)`,
          }}
          columns={[
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Empresa', dataIndex: 'nomeEmpresa', render: (v) => v || '—' },
            { title: 'E-mail', dataIndex: 'email', render: (v) => v || '—' },
            { title: 'Origem', dataIndex: 'origem', render: (v?: keyof typeof ORIGEM_LEAD_LABEL) => (v ? ORIGEM_LEAD_LABEL[v] : '—') },
            {
              title: 'Vendedor',
              dataIndex: 'vendedorId',
              render: (v?: number) => (v ? <VendedorResumoCell vendedorId={v} /> : '—'),
            },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (situacao: keyof typeof SITUACAO_LEAD_LABEL) => <Tag color={COR_SITUACAO[situacao]}>{SITUACAO_LEAD_LABEL[situacao]}</Tag>,
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
