import { useState } from 'react'
import { Button, Card, Flex, Table, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import NovaTransferenciaModal from './modals/NovaTransferenciaModal'
import { ContaBancariaResumoCell } from './ResumoCells'
import { useTransferenciasBancarias } from './queries'
import type { TransferenciaBancariaResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

export default function TransferenciasBancariasListPage() {
  const { authorities } = useAuth()
  const podeMovimentar = hasAuthority(authorities, 'CAIXA_MOVIMENTAR')
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)
  const { data, isLoading, isPlaceholderData } = useTransferenciasBancarias(undefined, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Transferências entre Contas
        </Title>
        <Tooltip title={podeMovimentar ? undefined : 'Você não tem permissão para movimentar caixa'}>
          <Button type="primary" icon={<PlusOutlined />} disabled={!podeMovimentar} onClick={() => setModalAberto(true)}>
            Nova Transferência
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Table<TransferenciaBancariaResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} transferência(s)`,
          }}
          columns={[
            { title: 'Data', dataIndex: 'dataTransferencia', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
            { title: 'Origem', key: 'origem', render: (_, t) => <ContaBancariaResumoCell contaBancariaId={t.contaOrigemId} /> },
            { title: 'Destino', key: 'destino', render: (_, t) => <ContaBancariaResumoCell contaBancariaId={t.contaDestinoId} /> },
            { title: 'Valor', dataIndex: 'valor', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Histórico', dataIndex: 'historico', render: (v?: string) => v || '—' },
          ]}
        />
      </Card>

      <NovaTransferenciaModal open={modalAberto} onClose={() => setModalAberto(false)} />
    </Flex>
  )
}
