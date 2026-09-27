import { useState } from 'react'
import { Button, Card, Flex, Popconfirm, Select, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import CompensarChequeModal from './modals/CompensarChequeModal'
import DevolverChequeModal from './modals/DevolverChequeModal'
import MotivoAcaoModal from './modals/MotivoAcaoModal'
import NovoChequeEmitidoModal from './modals/NovoChequeEmitidoModal'
import { ContaBancariaResumoCell } from './ResumoCells'
import { useCancelarChequeEmitido, useChequesEmitidos, useCompensarChequeEmitido, useContasBancariasAtivas, useDevolverChequeEmitido } from './queries'
import { SITUACAO_CHEQUE_EMITIDO_LABEL } from './types'
import type { ChequeEmitidoResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_SITUACAO: Record<string, string> = {
  EMITIDO: 'default',
  COMPENSADO: 'success',
  DEVOLVIDO: 'error',
  CANCELADO: 'default',
}

export default function ChequesEmitidosListPage() {
  const { empresaAtivaId, authorities } = useAuth()
  const podeMovimentar = hasAuthority(authorities, 'CAIXA_MOVIMENTAR')
  const podeGerenciar = hasAuthority(authorities, 'CAIXA_GERENCIAR')
  const [contaBancariaId, setContaBancariaId] = useState<number | undefined>(undefined)
  const [pagina, setPagina] = useState(0)
  const [modalNovoAberto, setModalNovoAberto] = useState(false)
  const [chequeEmCompensacao, setChequeEmCompensacao] = useState<ChequeEmitidoResponse | null>(null)
  const [chequeEmDevolucao, setChequeEmDevolucao] = useState<ChequeEmitidoResponse | null>(null)
  const [chequeEmCancelamento, setChequeEmCancelamento] = useState<ChequeEmitidoResponse | null>(null)

  const { data: contasBancarias } = useContasBancariasAtivas(empresaAtivaId ?? undefined)
  const { data, isLoading, isPlaceholderData } = useChequesEmitidos(contaBancariaId, pagina, TAMANHO_PAGINA)
  const compensar = useCompensarChequeEmitido()
  const devolver = useDevolverChequeEmitido()
  const cancelar = useCancelarChequeEmitido()

  async function handleCancelar(motivo: string) {
    if (!chequeEmCancelamento) return
    await cancelar.mutateAsync({ id: chequeEmCancelamento.id, request: { motivo } })
  }

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center" wrap="wrap" gap={16}>
        <Title level={3} style={{ margin: 0 }}>
          Cheques Emitidos
        </Title>
        <Flex gap={12} align="center">
          <Select
            allowClear
            style={{ width: 260 }}
            placeholder="Todas as contas"
            options={contasBancarias?.map((c) => ({ value: c.id, label: c.nome }))}
            value={contaBancariaId}
            onChange={(v) => {
              setContaBancariaId(v)
              setPagina(0)
            }}
          />
          <Tooltip title={podeMovimentar ? undefined : 'Você não tem permissão para movimentar cheques'}>
            <Button type="primary" icon={<PlusOutlined />} disabled={!podeMovimentar} onClick={() => setModalNovoAberto(true)}>
              Novo Cheque
            </Button>
          </Tooltip>
        </Flex>
      </Flex>

      <Card>
        <Table<ChequeEmitidoResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} cheque(s)`,
          }}
          columns={[
            { title: 'Número', dataIndex: 'numero' },
            { title: 'Conta', key: 'conta', render: (_, c) => <ContaBancariaResumoCell contaBancariaId={c.contaBancariaId} /> },
            { title: 'Emissão', dataIndex: 'dataEmissao', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
            { title: 'Bom para', dataIndex: 'bomPara', render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY') : '—') },
            { title: 'Nominal a', dataIndex: 'nominalA', render: (v?: string) => v || '—' },
            { title: 'Valor', dataIndex: 'valor', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            {
              title: 'Situação',
              key: 'situacao',
              render: (_, c) => (
                <Tooltip title={c.motivoSituacao}>
                  <Tag color={COR_SITUACAO[c.situacao]}>{SITUACAO_CHEQUE_EMITIDO_LABEL[c.situacao]}</Tag>
                </Tooltip>
              ),
            },
            ...(podeMovimentar
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    render: (_: unknown, cheque: ChequeEmitidoResponse) =>
                      cheque.situacao === 'EMITIDO' && (
                        <Space>
                          <Button size="small" onClick={() => setChequeEmCompensacao(cheque)}>
                            Compensar
                          </Button>
                          <Button size="small" danger onClick={() => setChequeEmDevolucao(cheque)}>
                            Devolver
                          </Button>
                          {podeGerenciar && (
                            <Popconfirm title="Cancelar este cheque?" onConfirm={() => setChequeEmCancelamento(cheque)}>
                              <Button size="small" danger>
                                Cancelar
                              </Button>
                            </Popconfirm>
                          )}
                        </Space>
                      ),
                  },
                ]
              : []),
          ]}
        />
      </Card>

      <NovoChequeEmitidoModal open={modalNovoAberto} onClose={() => setModalNovoAberto(false)} />
      <CompensarChequeModal
        open={chequeEmCompensacao !== null}
        onClose={() => setChequeEmCompensacao(null)}
        submitting={compensar.isPending}
        onSubmit={(dataCompensacao) => {
          if (!chequeEmCompensacao) return Promise.resolve()
          return compensar.mutateAsync({ id: chequeEmCompensacao.id, request: { dataCompensacao } })
        }}
      />
      <DevolverChequeModal
        open={chequeEmDevolucao !== null}
        onClose={() => setChequeEmDevolucao(null)}
        submitting={devolver.isPending}
        onSubmit={(request) => {
          if (!chequeEmDevolucao) return Promise.resolve()
          return devolver.mutateAsync({ id: chequeEmDevolucao.id, request })
        }}
      />
      <MotivoAcaoModal
        open={chequeEmCancelamento !== null}
        title="Cancelar cheque emitido"
        okText="Cancelar cheque"
        okDanger
        onClose={() => setChequeEmCancelamento(null)}
        onSubmit={handleCancelar}
        submitting={cancelar.isPending}
        successMessage="Cheque cancelado."
      />
    </Flex>
  )
}
