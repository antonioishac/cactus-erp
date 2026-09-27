import { useState } from 'react'
import { Button, Card, Flex, Select, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import NovoMovimentoManualModal from './modals/NovoMovimentoManualModal'
import { ContaBancariaResumoCell } from './ResumoCells'
import { useContasBancariasAtivas, useMovimentosCaixaBanco } from './queries'
import { ORIGEM_MOVIMENTO_CAIXA_LABEL, TIPO_MOVIMENTO_CAIXA_LABEL } from './types'
import type { MovimentoCaixaBancoResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_TIPO: Record<string, string> = {
  ENTRADA: 'success',
  SAIDA: 'error',
  TRANSFERENCIA_ENTRADA: 'success',
  TRANSFERENCIA_SAIDA: 'error',
}

/** Ledger append-only — sem editar/excluir, só consulta + lançamento manual novo. */
export default function MovimentosCaixaBancoListPage() {
  const { empresaAtivaId, authorities } = useAuth()
  const podeMovimentar = hasAuthority(authorities, 'CAIXA_MOVIMENTAR')
  const [contaBancariaId, setContaBancariaId] = useState<number | undefined>(undefined)
  const [pagina, setPagina] = useState(0)
  const [modalAberto, setModalAberto] = useState(false)

  const { data: contasBancarias } = useContasBancariasAtivas(empresaAtivaId ?? undefined)
  const { data, isLoading, isPlaceholderData } = useMovimentosCaixaBanco(contaBancariaId, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center" wrap="wrap" gap={16}>
        <Title level={3} style={{ margin: 0 }}>
          Movimentos de Caixa e Banco
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
          <Tooltip title={podeMovimentar ? undefined : 'Você não tem permissão para movimentar caixa'}>
            <Button type="primary" icon={<PlusOutlined />} disabled={!podeMovimentar} onClick={() => setModalAberto(true)}>
              Novo Movimento
            </Button>
          </Tooltip>
        </Flex>
      </Flex>

      <Card>
        <Table<MovimentoCaixaBancoResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} movimento(s)`,
          }}
          columns={[
            { title: 'Data', dataIndex: 'dataMovimento', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
            { title: 'Conta', key: 'conta', render: (_, m) => <ContaBancariaResumoCell contaBancariaId={m.contaBancariaId} /> },
            {
              title: 'Tipo',
              dataIndex: 'tipoMovimento',
              render: (v: keyof typeof TIPO_MOVIMENTO_CAIXA_LABEL) => <Tag color={COR_TIPO[v]}>{TIPO_MOVIMENTO_CAIXA_LABEL[v]}</Tag>,
            },
            { title: 'Origem', dataIndex: 'origemTipo', render: (v: keyof typeof ORIGEM_MOVIMENTO_CAIXA_LABEL) => ORIGEM_MOVIMENTO_CAIXA_LABEL[v] },
            { title: 'Histórico', dataIndex: 'historico' },
            { title: 'Valor', dataIndex: 'valor', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Saldo anterior', dataIndex: 'saldoAnterior', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Saldo posterior', dataIndex: 'saldoPosterior', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
          ]}
        />
      </Card>

      <NovoMovimentoManualModal open={modalAberto} onClose={() => setModalAberto(false)} />
    </Flex>
  )
}
