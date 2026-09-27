import { useState } from 'react'
import { Button, Card, Flex, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import CompensarChequeModal from './modals/CompensarChequeModal'
import DevolverChequeModal from './modals/DevolverChequeModal'
import NovoChequeRecebidoModal from './modals/NovoChequeRecebidoModal'
import SelecionarContaEDataModal from './modals/SelecionarContaEDataModal'
import { ClienteResumoCell } from './ResumoCells'
import {
  useChequesRecebidos,
  useCompensarChequeRecebido,
  useDepositarChequeRecebido,
  useDescontarChequeRecebido,
  useDevolverChequeRecebido,
} from './queries'
import { SITUACAO_CHEQUE_RECEBIDO_LABEL } from './types'
import type { ChequeRecebidoResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_SITUACAO: Record<string, string> = {
  EM_CARTEIRA: 'default',
  DEPOSITADO: 'processing',
  COMPENSADO: 'success',
  DESCONTADO: 'success',
  DEVOLVIDO: 'error',
}

export default function ChequesRecebidosListPage() {
  const { authorities } = useAuth()
  const podeMovimentar = hasAuthority(authorities, 'CAIXA_MOVIMENTAR')
  const [pagina, setPagina] = useState(0)
  const [modalNovoAberto, setModalNovoAberto] = useState(false)
  const [chequeEmDeposito, setChequeEmDeposito] = useState<ChequeRecebidoResponse | null>(null)
  const [chequeEmCompensacao, setChequeEmCompensacao] = useState<ChequeRecebidoResponse | null>(null)
  const [chequeEmDesconto, setChequeEmDesconto] = useState<ChequeRecebidoResponse | null>(null)
  const [chequeEmDevolucao, setChequeEmDevolucao] = useState<ChequeRecebidoResponse | null>(null)

  const { data, isLoading, isPlaceholderData } = useChequesRecebidos(undefined, pagina, TAMANHO_PAGINA)
  const depositar = useDepositarChequeRecebido()
  const compensar = useCompensarChequeRecebido()
  const descontar = useDescontarChequeRecebido()
  const devolver = useDevolverChequeRecebido()

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Cheques Recebidos
        </Title>
        <Tooltip title={podeMovimentar ? undefined : 'Você não tem permissão para movimentar cheques'}>
          <Button type="primary" icon={<PlusOutlined />} disabled={!podeMovimentar} onClick={() => setModalNovoAberto(true)}>
            Novo Cheque
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Table<ChequeRecebidoResponse>
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
            { title: 'Emitente', dataIndex: 'nomeEmitente' },
            { title: 'Cliente', key: 'cliente', render: (_, c) => (c.clienteId ? <ClienteResumoCell clienteId={c.clienteId} /> : '—') },
            { title: 'Número', dataIndex: 'numero', render: (v?: string) => v || '—' },
            { title: 'Emissão', dataIndex: 'dataEmissao', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
            { title: 'Valor', dataIndex: 'valor', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            {
              title: 'Situação',
              key: 'situacao',
              render: (_, c) => (
                <Tooltip title={c.motivoSituacao}>
                  <Tag color={COR_SITUACAO[c.situacao]}>{SITUACAO_CHEQUE_RECEBIDO_LABEL[c.situacao]}</Tag>
                </Tooltip>
              ),
            },
            ...(podeMovimentar
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    render: (_: unknown, cheque: ChequeRecebidoResponse) => (
                      <Space wrap>
                        {cheque.situacao === 'EM_CARTEIRA' && (
                          <>
                            <Button size="small" onClick={() => setChequeEmDeposito(cheque)}>
                              Depositar
                            </Button>
                            <Button size="small" onClick={() => setChequeEmDesconto(cheque)}>
                              Descontar
                            </Button>
                          </>
                        )}
                        {cheque.situacao === 'DEPOSITADO' && (
                          <Button size="small" onClick={() => setChequeEmCompensacao(cheque)}>
                            Compensar
                          </Button>
                        )}
                        {(cheque.situacao === 'EM_CARTEIRA' || cheque.situacao === 'DEPOSITADO') && (
                          <Button size="small" danger onClick={() => setChequeEmDevolucao(cheque)}>
                            Devolver
                          </Button>
                        )}
                      </Space>
                    ),
                  },
                ]
              : []),
          ]}
        />
      </Card>

      <NovoChequeRecebidoModal open={modalNovoAberto} onClose={() => setModalNovoAberto(false)} />
      <SelecionarContaEDataModal
        open={chequeEmDeposito !== null}
        title="Depositar cheque"
        okText="Depositar"
        dataLabel="Data do depósito"
        submitting={depositar.isPending}
        successMessage="Cheque depositado."
        onClose={() => setChequeEmDeposito(null)}
        onSubmit={(contaBancariaId, data) => {
          if (!chequeEmDeposito) return Promise.resolve()
          return depositar.mutateAsync({ id: chequeEmDeposito.id, request: { contaBancariaId, dataDeposito: data } })
        }}
      />
      <SelecionarContaEDataModal
        open={chequeEmDesconto !== null}
        title="Descontar cheque em banco"
        okText="Descontar"
        dataLabel="Data do desconto"
        submitting={descontar.isPending}
        successMessage="Cheque descontado."
        onClose={() => setChequeEmDesconto(null)}
        onSubmit={(contaBancariaId, data) => {
          if (!chequeEmDesconto) return Promise.resolve()
          return descontar.mutateAsync({ id: chequeEmDesconto.id, request: { contaBancariaId, dataDesconto: data } })
        }}
      />
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
    </Flex>
  )
}
