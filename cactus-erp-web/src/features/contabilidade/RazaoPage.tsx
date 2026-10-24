import { useState } from 'react'
import { Card, Flex, Input, Select, Table, Typography } from 'antd'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import ExportarRelatorioButtons from '@shared/ui/ExportarRelatorioButtons'
import { exportarRazao } from './api'
import { useRazao, useTodasContas } from './queries'
import { TIPO_PARTIDA_LABEL } from './types'
import type { RazaoItemResponse } from './types'

const { Title, Text } = Typography

const moeda = (v: number) => `R$ ${v.toFixed(2)}`

export default function RazaoPage() {
  const { empresaAtivaId } = useAuth()
  const [contaId, setContaId] = useState<number | undefined>(undefined)
  const [dataInicio, setDataInicio] = useState('')
  const [dataFim, setDataFim] = useState('')

  const { data: contas, isLoading: carregandoContas } = useTodasContas(empresaAtivaId ?? -1)
  const contasAnaliticas = contas?.filter((c) => c.contaAnalitica)

  const { data, isLoading } = useRazao(empresaAtivaId ?? -1, contaId, dataInicio || undefined, dataFim || undefined)
  const itens = data ?? []
  const filtroCompleto = !!empresaAtivaId && !!contaId && !!dataInicio && !!dataFim

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Razão
      </Title>

      <Card>
        <Flex gap={12} align="center" wrap="wrap" style={{ marginBottom: 16 }}>
          <Select
            showSearch
            allowClear
            loading={carregandoContas}
            optionFilterProp="label"
            placeholder="Selecione a conta"
            style={{ width: 280 }}
            value={contaId}
            onChange={setContaId}
            options={contasAnaliticas?.map((c) => ({ value: c.id, label: `${c.codigo} — ${c.nome}` }))}
          />
          <span>Período</span>
          <Input type="date" value={dataInicio} onChange={(e) => setDataInicio(e.target.value)} style={{ width: 160 }} />
          <span>até</span>
          <Input type="date" value={dataFim} onChange={(e) => setDataFim(e.target.value)} style={{ width: 160 }} />
          <ExportarRelatorioButtons
            disabled={!filtroCompleto}
            motivoDesabilitado="Selecione a conta e o período"
            onExportar={(formato) => exportarRazao(empresaAtivaId ?? -1, contaId as number, dataInicio, dataFim, formato)}
          />
        </Flex>

        <Table<RazaoItemResponse>
          rowKey="lancamentoContabilId"
          size="small"
          loading={isLoading}
          dataSource={itens}
          pagination={false}
          locale={{ emptyText: 'Selecione a conta e o período pra consultar o razão' }}
          columns={[
            { title: 'Lançamento', dataIndex: 'lancamentoContabilId', width: 100, render: (v: number) => `#${v}` },
            { title: 'Data', dataIndex: 'dataLancamento', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
            { title: 'Histórico', dataIndex: 'historico' },
            {
              title: 'Tipo',
              dataIndex: 'tipoPartida',
              render: (v: keyof typeof TIPO_PARTIDA_LABEL) => TIPO_PARTIDA_LABEL[v],
            },
            { title: 'Valor', dataIndex: 'valor', align: 'right', render: moeda },
            {
              title: 'Saldo corrido',
              dataIndex: 'saldoCorrido',
              align: 'right',
              render: (v: number) => <strong>{moeda(v)}</strong>,
            },
          ]}
          summary={() =>
            itens.length > 0 && (
              <Table.Summary.Row>
                <Table.Summary.Cell index={0} colSpan={4}>
                  <Text strong>{itens.length} lançamento(s)</Text>
                </Table.Summary.Cell>
                <Table.Summary.Cell index={4} />
                <Table.Summary.Cell index={5} align="right">
                  <Text strong>{moeda(itens.at(-1)?.saldoCorrido ?? 0)}</Text>
                </Table.Summary.Cell>
              </Table.Summary.Row>
            )
          }
        />
      </Card>
    </Flex>
  )
}
