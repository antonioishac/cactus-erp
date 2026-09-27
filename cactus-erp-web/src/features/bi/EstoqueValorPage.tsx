import { useState } from 'react'
import { Card, DatePicker, Flex, Select, Table, Typography } from 'antd'
import dayjs, { type Dayjs } from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { useDepositosAtivos, useEstoqueValor } from './queries'
import type { EstoqueValorFiltro, EstoqueValorIndicadorResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

/**
 * Snapshot da data de referência (default hoje), gerado pelo ETL a partir do
 * `saldo_estoque` real do `controle-estoque-service` — não recalculado em
 * tempo real, ver Javadoc de `IndicadoresController`. Se a data escolhida
 * nunca teve um ETL rodado, a lista simplesmente vem vazia (não é erro).
 */
export default function EstoqueValorPage() {
  const { empresaAtivaId } = useAuth()
  const [dataReferencia, setDataReferencia] = useState<Dayjs>(dayjs())
  const [depositoId, setDepositoId] = useState<number | undefined>(undefined)
  const [pagina, setPagina] = useState(0)

  const { data: depositos } = useDepositosAtivos(empresaAtivaId ?? undefined)
  const filtro: EstoqueValorFiltro = { dataReferencia: dataReferencia.format('YYYY-MM-DD'), depositoId }
  const { data, isLoading, isPlaceholderData } = useEstoqueValor(filtro, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Valor de Estoque
      </Title>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <DatePicker
            value={dataReferencia}
            format="DD/MM/YYYY"
            allowClear={false}
            onChange={(data) => {
              setDataReferencia(data ?? dayjs())
              setPagina(0)
            }}
          />
          <Select
            allowClear
            style={{ width: 240 }}
            placeholder="Filtrar por depósito"
            options={depositos?.map((d) => ({ value: d.id, label: d.nome }))}
            onChange={(valor: number | undefined) => {
              setDepositoId(valor)
              setPagina(0)
            }}
          />
        </Flex>

        <Table<EstoqueValorIndicadorResponse>
          rowKey={(item) => `${item.dataReferencia}-${item.depositoId}-${item.produtoId}`}
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} registro(s)`,
          }}
          locale={{ emptyText: 'Nenhum snapshot pra esta data — rode o ETL em Business Intelligence > Execução do ETL' }}
          columns={[
            { title: 'Depósito', dataIndex: 'depositoNome' },
            {
              title: 'Produto',
              key: 'produto',
              render: (_, item) => item.produtoNome ?? `#${item.produtoId}`,
            },
            { title: 'Quantidade', dataIndex: 'quantidade', align: 'right' },
            {
              title: 'Custo médio ponderado',
              dataIndex: 'custoMedioPonderado',
              align: 'right',
              render: (v: number) => `R$ ${v.toFixed(2)}`,
            },
            { title: 'Valor total', dataIndex: 'valorTotal', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
          ]}
          summary={(linhas) => {
            if (linhas.length === 0) return null
            const total = linhas.reduce((soma, l) => soma + l.valorTotal, 0)
            return (
              <Table.Summary.Row>
                <Table.Summary.Cell index={0} colSpan={4} align="right">
                  <strong>Total da página</strong>
                </Table.Summary.Cell>
                <Table.Summary.Cell index={1} align="right">
                  <strong>R$ {total.toFixed(2)}</strong>
                </Table.Summary.Cell>
              </Table.Summary.Row>
            )
          }}
        />
      </Card>
    </Flex>
  )
}
