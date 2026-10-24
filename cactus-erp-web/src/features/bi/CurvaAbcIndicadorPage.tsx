import { useState } from 'react'
import { Card, DatePicker, Flex, Select, Table, Tag, Typography } from 'antd'
import dayjs, { type Dayjs } from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import ExportarRelatorioButtons from '@shared/ui/ExportarRelatorioButtons'
import { exportarCurvaAbc } from './api'
import { useCurvaAbc, useDepositosAtivos } from './queries'
import type { ClassificacaoAbc, CurvaAbcFiltro, CurvaAbcIndicadorResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_CLASSIFICACAO: Record<ClassificacaoAbc, string> = { A: 'success', B: 'gold', C: 'default' }

/**
 * Diferente de `GET /relatorios/curva-abc` do controle-estoque-service (que
 * calcula sob demanda, sem histórico), este é o snapshot persistido pelo ETL
 * do BI por data de referência — permite comparar a classificação ao longo
 * do tempo, ver Javadoc de `IndicadoresController`.
 */
export default function CurvaAbcIndicadorPage() {
  const { empresaAtivaId } = useAuth()
  const [dataReferencia, setDataReferencia] = useState<Dayjs>(dayjs())
  const [depositoId, setDepositoId] = useState<number | undefined>(undefined)
  const [pagina, setPagina] = useState(0)

  const { data: depositos } = useDepositosAtivos(empresaAtivaId ?? undefined)
  const filtro: CurvaAbcFiltro = { dataReferencia: dataReferencia.format('YYYY-MM-DD'), depositoId }
  const { data, isLoading, isPlaceholderData } = useCurvaAbc(filtro, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Title level={3} style={{ margin: 0 }}>
        Curva ABC
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
          <ExportarRelatorioButtons onExportar={(formato) => exportarCurvaAbc(filtro, formato)} />
        </Flex>

        <Table<CurvaAbcIndicadorResponse>
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
            { title: 'Produto', key: 'produto', render: (_, item) => item.produtoNome ?? `#${item.produtoId}` },
            {
              title: 'Valor movimentado',
              dataIndex: 'valorMovimentado',
              align: 'right',
              render: (v: number) => `R$ ${v.toFixed(2)}`,
            },
            { title: '% Participação', dataIndex: 'percentualParticipacao', align: 'right', render: (v: number) => `${v.toFixed(2)}%` },
            { title: '% Acumulado', dataIndex: 'percentualAcumulado', align: 'right', render: (v: number) => `${v.toFixed(2)}%` },
            {
              title: 'Classificação',
              dataIndex: 'classificacao',
              render: (c: ClassificacaoAbc) => <Tag color={COR_CLASSIFICACAO[c]}>{c}</Tag>,
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
