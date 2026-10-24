import { useState } from 'react'
import { Card, Flex, Input, Table, Typography } from 'antd'
import { useAuth } from '@features/auth/useAuth'
import ExportarRelatorioButtons from '@shared/ui/ExportarRelatorioButtons'
import { exportarBalancete } from './api'
import { useBalancete } from './queries'
import { NATUREZA_CONTA_LABEL, TIPO_CONTA_CONTABIL_LABEL } from './types'
import type { BalanceteItemResponse } from './types'

const { Title, Text } = Typography

const moeda = (v: number) => `R$ ${v.toFixed(2)}`

/**
 * Soma já ajustada pela natureza da conta (ver `RelatorioContabilController` do
 * backend) — "positivo = a favor da natureza esperada", mesma convenção de exibição
 * de qualquer balancete contábil.
 */
export default function BalancetePage() {
  const { empresaAtivaId } = useAuth()
  const [dataInicio, setDataInicio] = useState('')
  const [dataFim, setDataFim] = useState('')

  const { data, isLoading } = useBalancete(empresaAtivaId ?? -1, dataInicio || undefined, dataFim || undefined)
  const itens = data ?? []
  const filtroCompleto = !!empresaAtivaId && !!dataInicio && !!dataFim

  const total = (campo: 'totalDebito' | 'totalCredito' | 'saldo') => itens.reduce((soma, i) => soma + i[campo], 0)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Balancete
      </Title>

      <Card>
        <Flex gap={12} align="center" wrap="wrap" style={{ marginBottom: 16 }}>
          <span>Período</span>
          <Input type="date" value={dataInicio} onChange={(e) => setDataInicio(e.target.value)} style={{ width: 160 }} />
          <span>até</span>
          <Input type="date" value={dataFim} onChange={(e) => setDataFim(e.target.value)} style={{ width: 160 }} />
          <ExportarRelatorioButtons
            disabled={!filtroCompleto}
            motivoDesabilitado="Selecione o período"
            onExportar={(formato) => exportarBalancete(empresaAtivaId ?? -1, dataInicio, dataFim, formato)}
          />
        </Flex>

        <Table<BalanceteItemResponse>
          rowKey="contaId"
          size="small"
          loading={isLoading}
          dataSource={itens}
          pagination={false}
          locale={{ emptyText: 'Selecione o período pra calcular o balancete' }}
          columns={[
            { title: 'Código', dataIndex: 'contaCodigo', width: 100 },
            { title: 'Conta', dataIndex: 'contaNome' },
            { title: 'Tipo', dataIndex: 'tipo', render: (v: keyof typeof TIPO_CONTA_CONTABIL_LABEL) => TIPO_CONTA_CONTABIL_LABEL[v] },
            { title: 'Natureza', dataIndex: 'natureza', render: (v: keyof typeof NATUREZA_CONTA_LABEL) => NATUREZA_CONTA_LABEL[v] },
            { title: 'Débito', dataIndex: 'totalDebito', align: 'right', render: moeda },
            { title: 'Crédito', dataIndex: 'totalCredito', align: 'right', render: moeda },
            { title: 'Saldo', dataIndex: 'saldo', align: 'right', render: (v: number) => <strong>{moeda(v)}</strong> },
          ]}
          summary={() =>
            itens.length > 0 && (
              <Table.Summary.Row>
                <Table.Summary.Cell index={0} colSpan={4}>
                  <Text strong>Total ({itens.length} conta(s))</Text>
                </Table.Summary.Cell>
                <Table.Summary.Cell index={4} align="right">
                  <Text strong>{moeda(total('totalDebito'))}</Text>
                </Table.Summary.Cell>
                <Table.Summary.Cell index={5} align="right">
                  <Text strong>{moeda(total('totalCredito'))}</Text>
                </Table.Summary.Cell>
                <Table.Summary.Cell index={6} align="right">
                  <Text strong>{moeda(total('saldo'))}</Text>
                </Table.Summary.Cell>
              </Table.Summary.Row>
            )
          }
        />
      </Card>
    </Flex>
  )
}
