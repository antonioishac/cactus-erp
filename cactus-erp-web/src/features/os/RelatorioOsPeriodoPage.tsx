import { useState } from 'react'
import { Card, Flex, Input, Select, Table, Tag, Typography } from 'antd'
import dayjs from 'dayjs'
import ExportarRelatorioButtons from '@shared/ui/ExportarRelatorioButtons'
import { ClienteResumoCell } from './ResumoCells'
import { exportarOsPorPeriodo } from './api'
import { useOsPorPeriodo, useTodosOsStatus } from './queries'
import type { OsAberturaResponse } from './types'

const { Title, Text } = Typography

export default function RelatorioOsPeriodoPage() {
  const [dataInicio, setDataInicio] = useState('')
  const [dataFim, setDataFim] = useState('')
  const [osStatusId, setOsStatusId] = useState<number | undefined>(undefined)

  const { data: statusList } = useTodosOsStatus()
  const { data, isLoading } = useOsPorPeriodo(dataInicio || undefined, dataFim || undefined, osStatusId, undefined)
  const aberturas = data ?? []
  const filtroCompleto = !!dataInicio && !!dataFim
  const total = aberturas.reduce((soma, os) => soma + os.valorTotal, 0)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Ordens de Serviço por Período
      </Title>

      <Card>
        <Flex gap={12} align="center" wrap="wrap" style={{ marginBottom: 16 }}>
          <span>Data de início</span>
          <Input type="date" value={dataInicio} onChange={(e) => setDataInicio(e.target.value)} style={{ width: 160 }} />
          <span>até</span>
          <Input type="date" value={dataFim} onChange={(e) => setDataFim(e.target.value)} style={{ width: 160 }} />
          <Select
            allowClear
            style={{ width: 200 }}
            placeholder="Todos os status"
            options={statusList?.map((s) => ({ value: s.id, label: s.nome }))}
            onChange={(v: number | undefined) => setOsStatusId(v)}
          />
          <ExportarRelatorioButtons
            disabled={!filtroCompleto}
            motivoDesabilitado="Selecione o período"
            onExportar={(formato) => exportarOsPorPeriodo(dataInicio, dataFim, osStatusId, undefined, formato)}
          />
        </Flex>

        <Table<OsAberturaResponse>
          rowKey="id"
          loading={isLoading}
          dataSource={aberturas}
          pagination={false}
          locale={{ emptyText: 'Selecione o período pra listar as ordens de serviço' }}
          columns={[
            { title: 'Número', key: 'numero', render: (_, os) => os.numero || `#${os.id}` },
            { title: 'Status', dataIndex: 'osStatusNome', render: (v) => <Tag>{v}</Tag> },
            { title: 'Cliente', key: 'cliente', render: (_, os) => <ClienteResumoCell clienteId={os.clienteId} /> },
            { title: 'Início', dataIndex: 'dataInicio', render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY') : '—') },
            { title: 'Fim', dataIndex: 'dataFim', render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY') : '—') },
            { title: 'Valor total', dataIndex: 'valorTotal', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
          ]}
          summary={() =>
            aberturas.length > 0 && (
              <Table.Summary.Row>
                <Table.Summary.Cell index={0} colSpan={5}>
                  <Text strong>Total ({aberturas.length} OS)</Text>
                </Table.Summary.Cell>
                <Table.Summary.Cell index={5} align="right">
                  <Text strong>R$ {total.toFixed(2)}</Text>
                </Table.Summary.Cell>
              </Table.Summary.Row>
            )
          }
        />
      </Card>
    </Flex>
  )
}
