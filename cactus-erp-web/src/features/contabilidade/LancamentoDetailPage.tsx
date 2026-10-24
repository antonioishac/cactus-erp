import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { Breadcrumb, Button, Card, Descriptions, Flex, Skeleton, Table, Tag, Tooltip, Typography } from 'antd'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import MotivoAcaoModal from './modals/MotivoAcaoModal'
import { useEstornarLancamento, useLancamento } from './queries'
import { ORIGEM_LANCAMENTO_LABEL, SITUACAO_LANCAMENTO_LABEL, TIPO_PARTIDA_LABEL } from './types'
import type { PartidaContabilResponse } from './types'

const { Title } = Typography

const COR_SITUACAO: Record<string, string> = { LANCADO: 'success', ESTORNADO: 'default' }

export default function LancamentoDetailPage() {
  const navigate = useNavigate()
  const { id } = useParams()
  const { authorities } = useAuth()
  const podeLancar = hasAuthority(authorities, 'CONTABIL_LANCAR')
  const [modalEstornoAberto, setModalEstornoAberto] = useState(false)

  const lancamentoId = id ? Number(id) : undefined
  const { data: lancamento, isLoading } = useLancamento(lancamentoId)
  const estornar = useEstornarLancamento(lancamentoId ?? -1)

  if (isLoading || !lancamento) {
    return <Skeleton active />
  }

  const totalDebito = lancamento.partidas.filter((p) => p.tipoPartida === 'DEBITO').reduce((soma, p) => soma + p.valor, 0)
  const totalCredito = lancamento.partidas.filter((p) => p.tipoPartida === 'CREDITO').reduce((soma, p) => soma + p.valor, 0)
  const podeEstornar = podeLancar && lancamento.situacao === 'LANCADO'

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[
          { title: <a onClick={() => navigate('/contabilidade/lancamentos')}>Lançamentos Contábeis</a> },
          { title: `Lançamento #${lancamento.id}` },
        ]}
      />

      <Card>
        <Flex justify="space-between" align="center" style={{ marginBottom: 16 }}>
          <Title level={4} style={{ margin: 0 }}>
            Lançamento #{lancamento.id}
          </Title>
          <Tooltip
            title={
              lancamento.situacao !== 'LANCADO'
                ? 'Lançamento já estornado'
                : !podeLancar
                  ? 'Você não tem permissão para estornar'
                  : undefined
            }
          >
            <Button danger disabled={!podeEstornar} onClick={() => setModalEstornoAberto(true)}>
              Estornar
            </Button>
          </Tooltip>
        </Flex>

        <Descriptions bordered column={2} size="small">
          <Descriptions.Item label="Data">{dayjs(lancamento.dataLancamento).format('DD/MM/YYYY')}</Descriptions.Item>
          <Descriptions.Item label="Nº documento">{lancamento.numeroDocumento || '—'}</Descriptions.Item>
          <Descriptions.Item label="Histórico" span={2}>
            {lancamento.historico}
          </Descriptions.Item>
          <Descriptions.Item label="Origem">{ORIGEM_LANCAMENTO_LABEL[lancamento.origemTipo]}</Descriptions.Item>
          <Descriptions.Item label="Situação">
            <Tag color={COR_SITUACAO[lancamento.situacao]}>{SITUACAO_LANCAMENTO_LABEL[lancamento.situacao]}</Tag>
          </Descriptions.Item>
          {lancamento.estornoDeId && (
            <Descriptions.Item label="Estorno do lançamento">
              <a onClick={() => navigate(`/contabilidade/lancamentos/${lancamento.estornoDeId}`)}>
                #{lancamento.estornoDeId}
              </a>
            </Descriptions.Item>
          )}
          {lancamento.estornadoPorId && (
            <Descriptions.Item label="Estornado pelo lançamento">
              <a onClick={() => navigate(`/contabilidade/lancamentos/${lancamento.estornadoPorId}`)}>
                #{lancamento.estornadoPorId}
              </a>
            </Descriptions.Item>
          )}
        </Descriptions>
      </Card>

      <Card title="Partidas">
        <Table<PartidaContabilResponse>
          rowKey="id"
          dataSource={lancamento.partidas}
          pagination={false}
          columns={[
            { title: 'Conta', key: 'conta', render: (_, p) => `${p.contaCodigo} — ${p.contaNome}` },
            { title: 'Centro de custo', key: 'centro', render: (_, p) => p.centroCustoNome || '—' },
            {
              title: 'Tipo',
              dataIndex: 'tipoPartida',
              render: (v: keyof typeof TIPO_PARTIDA_LABEL) => (
                <Tag color={v === 'DEBITO' ? 'blue' : 'orange'}>{TIPO_PARTIDA_LABEL[v]}</Tag>
              ),
            },
            {
              title: 'Valor',
              dataIndex: 'valor',
              align: 'right',
              render: (v: number) => v.toLocaleString('pt-BR', { minimumFractionDigits: 2 }),
            },
            { title: 'Complemento', dataIndex: 'historicoComplementar', render: (v) => v || '—' },
          ]}
          summary={() => (
            <Table.Summary fixed>
              <Table.Summary.Row>
                <Table.Summary.Cell index={0} colSpan={3}>
                  <strong>Total</strong>
                </Table.Summary.Cell>
                <Table.Summary.Cell index={1} align="right">
                  <strong>
                    D {totalDebito.toLocaleString('pt-BR', { minimumFractionDigits: 2 })} / C{' '}
                    {totalCredito.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
                  </strong>
                </Table.Summary.Cell>
                <Table.Summary.Cell index={2} />
              </Table.Summary.Row>
            </Table.Summary>
          )}
        />
      </Card>

      <MotivoAcaoModal
        open={modalEstornoAberto}
        title="Estornar lançamento"
        okText="Estornar"
        onClose={() => setModalEstornoAberto(false)}
        onSubmit={(motivo) => estornar.mutateAsync({ motivo })}
        submitting={estornar.isPending}
        successMessage="Lançamento estornado com sucesso."
      />
    </Flex>
  )
}
