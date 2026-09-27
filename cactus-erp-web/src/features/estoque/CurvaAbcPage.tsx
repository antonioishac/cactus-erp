import { useState } from 'react'
import { Button, Card, Flex, InputNumber, Select, Table, Tag, Typography } from 'antd'
import { BarChartOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import ProdutoResumoCell from '@shared/ui/ProdutoResumoCell'
import { useDepositosAtivos, useCurvaAbc } from './queries'
import type { CurvaAbcItemResponse } from './types'

const { Title } = Typography

const COR_CLASSIFICACAO: Record<string, string> = { A: 'success', B: 'gold', C: 'default' }

/**
 * Calculada sob demanda a partir do ledger de movimentos (ver
 * `CurvaAbcController`) — não é uma entidade persistida, então não tem
 * paginação/lista contínua: o botão "Calcular" dispara a query.
 */
export default function CurvaAbcPage() {
  const { empresaAtivaId } = useAuth()
  const { data: depositos } = useDepositosAtivos(empresaAtivaId ?? undefined)
  const [depositoId, setDepositoId] = useState<number | undefined>(undefined)
  const [dias, setDias] = useState(90)
  const [calcularHabilitado, setCalcularHabilitado] = useState(false)

  const { data, isLoading, isFetching } = useCurvaAbc(depositoId, dias, calcularHabilitado)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Curva ABC
      </Title>

      <Card>
        <Flex gap={12} wrap="wrap" align="center" style={{ marginBottom: 16 }}>
          <Select
            allowClear
            style={{ width: 240 }}
            placeholder="Todos os depósitos"
            options={depositos?.map((d) => ({ value: d.id, label: d.nome }))}
            onChange={setDepositoId}
          />
          <Flex gap={8} align="center">
            <span>Últimos</span>
            <InputNumber min={1} value={dias} onChange={(v) => setDias(v ?? 90)} style={{ width: 90 }} />
            <span>dias</span>
          </Flex>
          <Button type="primary" icon={<BarChartOutlined />} loading={isFetching} onClick={() => setCalcularHabilitado(true)}>
            Calcular
          </Button>
        </Flex>

        <Table<CurvaAbcItemResponse>
          rowKey="produtoId"
          loading={isLoading && calcularHabilitado}
          dataSource={data ?? []}
          pagination={false}
          locale={{ emptyText: calcularHabilitado ? 'Nenhuma movimentação no período' : 'Clique em "Calcular" pra gerar a curva ABC' }}
          columns={[
            { title: 'Produto', key: 'produto', render: (_, item) => <ProdutoResumoCell produtoId={item.produtoId} /> },
            {
              title: 'Valor movimentado',
              dataIndex: 'valorMovimentado',
              align: 'right',
              render: (v: number) => `R$ ${v.toFixed(2)}`,
            },
            {
              title: 'Participação',
              dataIndex: 'percentualParticipacao',
              align: 'right',
              render: (v: number) => `${v.toFixed(2)}%`,
            },
            {
              title: 'Acumulado',
              dataIndex: 'percentualAcumulado',
              align: 'right',
              render: (v: number) => `${v.toFixed(2)}%`,
            },
            {
              title: 'Classe',
              dataIndex: 'classificacao',
              align: 'center',
              render: (v: string) => <Tag color={COR_CLASSIFICACAO[v]}>{v}</Tag>,
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
