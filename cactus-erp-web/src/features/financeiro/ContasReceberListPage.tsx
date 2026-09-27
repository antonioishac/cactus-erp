import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Table, Tag, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { ClienteResumoCell } from './ResumoCells'
import { useContasReceber } from './queries'
import { ORIGEM_CONTA_RECEBER_LABEL, SITUACAO_TITULO_LABEL } from './types'
import type { ContaReceberResponse, ContasReceberFiltro } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_SITUACAO: Record<string, string> = {
  ABERTA: 'default',
  PARCIAL: 'gold',
  PAGA: 'success',
  CANCELADA: 'error',
  RENEGOCIADA: 'purple',
}

export default function ContasReceberListPage() {
  const navigate = useNavigate()
  const { empresaAtivaId } = useAuth()
  const [filtro] = useState<ContasReceberFiltro>({})
  const [pagina, setPagina] = useState(0)
  const { data, isLoading, isPlaceholderData } = useContasReceber(filtro, empresaAtivaId ?? undefined, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Contas a Receber
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/financeiro/contas-receber/novo')}>
          Novo Título
        </Button>
      </Flex>

      <Card>
        <Table<ContaReceberResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(conta) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/financeiro/contas-receber/${conta.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} título(s)`,
          }}
          columns={[
            { title: 'Id', dataIndex: 'id', width: 70 },
            { title: 'Cliente', key: 'cliente', render: (_, conta) => <ClienteResumoCell clienteId={conta.clienteId} /> },
            { title: 'Documento', dataIndex: 'numeroDocumento', render: (v?: string) => v || '—' },
            { title: 'Origem', dataIndex: 'origemTipo', render: (v: keyof typeof ORIGEM_CONTA_RECEBER_LABEL) => ORIGEM_CONTA_RECEBER_LABEL[v] },
            { title: 'Vencimento', dataIndex: 'dataVencimento', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
            { title: 'Valor original', dataIndex: 'valorOriginal', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Saldo', dataIndex: 'valorSaldo', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            {
              title: 'Situação',
              key: 'situacao',
              render: (_, conta) => (
                <Flex gap={4}>
                  <Tag color={COR_SITUACAO[conta.situacao]}>{SITUACAO_TITULO_LABEL[conta.situacao]}</Tag>
                  {conta.vencido && <Tag color="volcano">Vencido</Tag>}
                </Flex>
              ),
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
