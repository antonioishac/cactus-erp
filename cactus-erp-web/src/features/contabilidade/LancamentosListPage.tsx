import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, DatePicker, Flex, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs, { type Dayjs } from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { useLancamentos } from './queries'
import { ORIGEM_LANCAMENTO_LABEL, SITUACAO_LANCAMENTO_LABEL } from './types'
import type { LancamentoContabilResponse } from './types'

const { Title } = Typography
const { RangePicker } = DatePicker
const TAMANHO_PAGINA = 10

const COR_SITUACAO: Record<string, string> = { LANCADO: 'success', ESTORNADO: 'default' }

export default function LancamentosListPage() {
  const navigate = useNavigate()
  const { empresaAtivaId, authorities } = useAuth()
  const podeLancar = hasAuthority(authorities, 'CONTABIL_LANCAR')
  const [pagina, setPagina] = useState(0)
  const [periodo, setPeriodo] = useState<[Dayjs, Dayjs] | null>(null)

  const { data, isLoading, isPlaceholderData } = useLancamentos(
    {
      empresaId: empresaAtivaId ?? -1,
      dataInicio: periodo?.[0]?.format('YYYY-MM-DD'),
      dataFim: periodo?.[1]?.format('YYYY-MM-DD'),
    },
    pagina,
    TAMANHO_PAGINA,
  )

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Lançamentos Contábeis
        </Title>
        <Tooltip title={podeLancar ? undefined : 'Você não tem permissão para lançar'}>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            disabled={!podeLancar}
            onClick={() => navigate('/contabilidade/lancamentos/novo')}
          >
            Novo Lançamento
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Flex style={{ marginBottom: 16 }}>
          <RangePicker
            format="DD/MM/YYYY"
            value={periodo}
            onChange={(valores) => {
              setPeriodo(valores && valores[0] && valores[1] ? [valores[0], valores[1]] : null)
              setPagina(0)
            }}
          />
        </Flex>

        <Table<LancamentoContabilResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(lancamento) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/contabilidade/lancamentos/${lancamento.id}`),
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} lançamento(s)`,
          }}
          columns={[
            { title: 'Data', dataIndex: 'dataLancamento', render: (v) => dayjs(v).format('DD/MM/YYYY') },
            { title: 'Histórico', dataIndex: 'historico' },
            { title: 'Nº documento', dataIndex: 'numeroDocumento', render: (v) => v || '—' },
            {
              title: 'Origem',
              dataIndex: 'origemTipo',
              render: (v: keyof typeof ORIGEM_LANCAMENTO_LABEL) => ORIGEM_LANCAMENTO_LABEL[v],
            },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (v: keyof typeof SITUACAO_LANCAMENTO_LABEL) => (
                <Tag color={COR_SITUACAO[v]}>{SITUACAO_LANCAMENTO_LABEL[v]}</Tag>
              ),
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
