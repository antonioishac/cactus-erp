import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Flex, Select, Table, Tag, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import ExportarRelatorioButtons from '@shared/ui/ExportarRelatorioButtons'
import { ClienteResumoCell, VendedorResumoCell } from './ResumoCells'
import { exportarFunilOportunidades } from './api'
import { useOportunidades } from './queries'
import { SITUACAO_OPORTUNIDADE_LABEL } from './types'
import type { OportunidadeResponse, SituacaoOportunidade } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_SITUACAO: Record<string, string> = {
  ABERTA: 'processing',
  GANHA: 'success',
  PERDIDA: 'error',
}

/** Lista flat, alternativa ao Kanban (`/crm/funil`) — útil pra ordenar/filtrar sem depender da visão de coluna. */
export default function OportunidadesListPage() {
  const navigate = useNavigate()
  const [pagina, setPagina] = useState(0)
  const [situacaoRelatorio, setSituacaoRelatorio] = useState<SituacaoOportunidade | undefined>(undefined)
  const { data, isLoading, isPlaceholderData } = useOportunidades(pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Oportunidades
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/crm/oportunidades/novo')}>
          Nova Oportunidade
        </Button>
      </Flex>

      <Card>
        <Flex gap={12} align="center" wrap="wrap" style={{ marginBottom: 16 }}>
          <span>Relatório do funil:</span>
          <Select
            allowClear
            style={{ width: 200 }}
            placeholder="Abertas (padrão)"
            value={situacaoRelatorio}
            onChange={setSituacaoRelatorio}
            options={Object.entries(SITUACAO_OPORTUNIDADE_LABEL).map(([value, label]) => ({ value, label }))}
          />
          <ExportarRelatorioButtons onExportar={(formato) => exportarFunilOportunidades(situacaoRelatorio, formato)} />
        </Flex>

        <Table<OportunidadeResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(oportunidade) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/crm/oportunidades/${oportunidade.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} oportunidade(s)`,
          }}
          columns={[
            { title: 'Nome', dataIndex: 'nome' },
            { title: 'Cliente', key: 'cliente', render: (_, o) => (o.clienteId ? <ClienteResumoCell clienteId={o.clienteId} /> : '—') },
            { title: 'Estágio', dataIndex: 'estagioFunilNome' },
            {
              title: 'Vendedor',
              key: 'vendedor',
              render: (_, o) => (o.vendedorId ? <VendedorResumoCell vendedorId={o.vendedorId} /> : '—'),
            },
            { title: 'Valor estimado', dataIndex: 'valorEstimado', align: 'right', render: (v?: number) => (v != null ? `R$ ${v.toFixed(2)}` : '—') },
            {
              title: 'Previsão de fechamento',
              dataIndex: 'dataPrevisaoFechamento',
              render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY') : '—'),
            },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (situacao: keyof typeof SITUACAO_OPORTUNIDADE_LABEL) => (
                <Tag color={COR_SITUACAO[situacao]}>{SITUACAO_OPORTUNIDADE_LABEL[situacao]}</Tag>
              ),
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
