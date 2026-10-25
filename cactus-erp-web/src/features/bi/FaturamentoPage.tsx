import { useState } from 'react'
import { Card, Col, DatePicker, Flex, Row, Select, Statistic, Table, Tag, Typography } from 'antd'
import dayjs, { type Dayjs } from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import ExportarRelatorioButtons from '@shared/ui/ExportarRelatorioButtons'
import { exportarFaturamento } from './api'
import { useFaturamento } from './queries'
import type { CanalVenda, FaturamentoDiarioIndicadorResponse, FaturamentoFiltro } from './types'

const { Title, Text } = Typography
const { RangePicker } = DatePicker

const moeda = (v: number) => v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })

/**
 * Faturamento e ticket médio da empresa ativa, B2B + PDV — snapshot do ETL
 * (`faturamento_diario`), não calculado em tempo real. O ETL recalcula os
 * últimos dias a cada execução, então venda do PDV sincronizada com atraso
 * entra na próxima rodada.
 */
export default function FaturamentoPage() {
  const { empresaAtivaId } = useAuth()
  const [periodo, setPeriodo] = useState<[Dayjs, Dayjs]>([dayjs().startOf('month'), dayjs()])
  const [canal, setCanal] = useState<CanalVenda | undefined>(undefined)

  const filtro: FaturamentoFiltro = {
    dataInicio: periodo[0].format('YYYY-MM-DD'),
    dataFim: periodo[1].format('YYYY-MM-DD'),
    empresaId: empresaAtivaId ?? undefined,
    canal,
  }
  const { data, isLoading, isPlaceholderData } = useFaturamento(filtro)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Faturamento
      </Title>

      <Card>
        <Flex gap={12} wrap="wrap" align="center">
          <RangePicker
            value={periodo}
            format="DD/MM/YYYY"
            allowClear={false}
            onChange={(datas) => {
              if (datas?.[0] && datas[1]) setPeriodo([datas[0], datas[1]])
            }}
          />
          <Select<CanalVenda>
            allowClear
            placeholder="Todos os canais"
            style={{ width: 180 }}
            value={canal}
            onChange={setCanal}
            options={[
              { value: 'B2B', label: 'B2B (Vendas)' },
              { value: 'PDV', label: 'PDV (Balcão)' },
            ]}
          />
          <ExportarRelatorioButtons onExportar={(formato) => exportarFaturamento(filtro, formato)} />
        </Flex>
      </Card>

      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <Card loading={isLoading}>
            <Statistic title="Faturamento líquido" value={moeda(data?.valorLiquido ?? 0)} />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card loading={isLoading}>
            <Statistic title="Vendas" value={data?.quantidadeVendas ?? 0} />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card loading={isLoading}>
            <Statistic title="Ticket médio" value={moeda(data?.ticketMedio ?? 0)} />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card loading={isLoading}>
            <Statistic title="Devoluções (PDV)" value={moeda(data?.valorDevolvido ?? 0)} />
          </Card>
        </Col>
      </Row>

      <Card title="Por dia">
        <Table<FaturamentoDiarioIndicadorResponse>
          rowKey={(item) => `${item.dataReferencia}-${item.empresaId}-${item.canal}`}
          size="small"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.dias ?? []}
          pagination={{ pageSize: 31, hideOnSinglePage: true }}
          locale={{ emptyText: 'Nenhuma venda no período. Se houver, rode o ETL em Business Intelligence > Execução do ETL' }}
          columns={[
            { title: 'Data', dataIndex: 'dataReferencia', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
            {
              title: 'Canal',
              dataIndex: 'canal',
              render: (v: CanalVenda) => <Tag color={v === 'PDV' ? 'green' : 'blue'}>{v}</Tag>,
            },
            { title: 'Vendas', dataIndex: 'quantidadeVendas', align: 'right' },
            { title: 'Valor bruto', dataIndex: 'valorBruto', align: 'right', render: moeda },
            { title: 'Devolvido', dataIndex: 'valorDevolvido', align: 'right', render: moeda },
            { title: 'Valor líquido', dataIndex: 'valorLiquido', align: 'right', render: (v: number) => <Text strong>{moeda(v)}</Text> },
            { title: 'Ticket médio', dataIndex: 'ticketMedio', align: 'right', render: moeda },
          ]}
        />
      </Card>
    </Flex>
  )
}
