import { useState } from 'react'
import { Card, Flex, Input, Select, Table, Typography } from 'antd'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import ExportarRelatorioButtons from '@shared/ui/ExportarRelatorioButtons'
import { exportarFluxoCaixa } from './api'
import { useFluxoCaixa } from './queries'
import type { AgrupamentoFluxoCaixa, FluxoCaixaPeriodoResponse } from './types'

const { Title } = Typography

/** Fluxo de caixa PROJETADO (soma `valorSaldo` de títulos ABERTA/PARCIAL com vencimento no
 * intervalo) — não é fluxo realizado, não olha baixas já pagas. Sem persistência, calculado
 * sob demanda a cada consulta. */
export default function FluxoCaixaPage() {
  const { empresaAtivaId } = useAuth()
  const hoje = dayjs()
  const [dataInicio, setDataInicio] = useState(hoje.startOf('month').format('YYYY-MM-DD'))
  const [dataFim, setDataFim] = useState(hoje.add(3, 'month').endOf('month').format('YYYY-MM-DD'))
  const [agrupamento, setAgrupamento] = useState<AgrupamentoFluxoCaixa>('MES')

  const { data, isLoading } = useFluxoCaixa(empresaAtivaId ?? undefined, dataInicio, dataFim, agrupamento)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Fluxo de Caixa Projetado
      </Title>

      <Card>
        <Flex gap={12} align="center" wrap="wrap" style={{ marginBottom: 16 }}>
          <Input type="date" value={dataInicio} onChange={(e) => setDataInicio(e.target.value)} style={{ width: 180 }} />
          <span>até</span>
          <Input type="date" value={dataFim} onChange={(e) => setDataFim(e.target.value)} style={{ width: 180 }} />
          <Select
            value={agrupamento}
            onChange={setAgrupamento}
            style={{ width: 160 }}
            options={[
              { value: 'DIA', label: 'Por dia' },
              { value: 'MES', label: 'Por mês' },
            ]}
          />
          <ExportarRelatorioButtons
            disabled={!empresaAtivaId}
            motivoDesabilitado="Selecione uma empresa ativa"
            onExportar={(formato) => exportarFluxoCaixa(empresaAtivaId as number, dataInicio, dataFim, agrupamento, formato)}
          />
        </Flex>

        <Table<FluxoCaixaPeriodoResponse>
          rowKey="periodo"
          loading={isLoading}
          dataSource={data ?? []}
          pagination={false}
          locale={{ emptyText: 'Nenhum título em aberto no intervalo selecionado' }}
          columns={[
            {
              title: 'Período',
              dataIndex: 'periodo',
              render: (v: string) => dayjs(v).format(agrupamento === 'MES' ? 'MM/YYYY' : 'DD/MM/YYYY'),
            },
            { title: 'Entradas (a receber)', dataIndex: 'totalEntradas', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Saídas (a pagar)', dataIndex: 'totalSaidas', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            {
              title: 'Saldo do período',
              dataIndex: 'saldoPeriodo',
              align: 'right',
              render: (v: number) => <strong style={{ color: v >= 0 ? '#3f8600' : '#cf1322' }}>R$ {v.toFixed(2)}</strong>,
            },
            {
              title: 'Saldo acumulado',
              dataIndex: 'saldoAcumulado',
              align: 'right',
              render: (v: number) => <strong style={{ color: v >= 0 ? '#3f8600' : '#cf1322' }}>R$ {v.toFixed(2)}</strong>,
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
