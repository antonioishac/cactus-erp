import { useState } from 'react'
import { Button, Card, Flex, Select, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import AsyncSelect from '@shared/ui/AsyncSelect'
import ProdutoResumoCell from '@shared/ui/ProdutoResumoCell'
import RegistrarMovimentoModal from './modals/RegistrarMovimentoModal'
import { useBuscaProdutosParaSelecao, useDepositosAtivos, useMovimentos } from './queries'
import { ORIGEM_MOVIMENTO_LABEL, TIPO_MOVIMENTO_LABEL } from './types'
import type { EstoqueMovimentoResponse, MovimentosFiltro } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

const COR_TIPO: Record<string, string> = {
  ENTRADA: 'success',
  DEVOLUCAO: 'success',
  TRANSFERENCIA_ENTRADA: 'success',
  SAIDA: 'error',
  PERDA: 'error',
  TRANSFERENCIA_SAIDA: 'error',
  AJUSTE: 'gold',
}

/** Ledger append-only — sem edição/exclusão, só listagem + lançamento de novo movimento (ver `EstoqueMovimentoController`). */
export default function MovimentosListPage() {
  const { empresaAtivaId, authorities } = useAuth()
  const podeMovimentar = hasAuthority(authorities, 'ESTOQUE_MOVIMENTAR')
  const [filtro, setFiltro] = useState<MovimentosFiltro>({})
  const [pagina, setPagina] = useState(0)
  const [termoProduto, setTermoProduto] = useState<string | undefined>(undefined)
  const [modalAberto, setModalAberto] = useState(false)

  const { data: depositos } = useDepositosAtivos(empresaAtivaId ?? undefined)
  const { data: produtos, isLoading: carregandoProdutos } = useBuscaProdutosParaSelecao(termoProduto)
  const { data, isLoading, isPlaceholderData } = useMovimentos(filtro, pagina, TAMANHO_PAGINA)

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Movimentos de Estoque
        </Title>
        <Tooltip title={podeMovimentar ? undefined : 'Você não tem permissão para lançar movimentos de estoque'}>
          <Button type="primary" icon={<PlusOutlined />} disabled={!podeMovimentar} onClick={() => setModalAberto(true)}>
            Registrar Movimento
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Flex gap={12} wrap="wrap" style={{ marginBottom: 16 }}>
          <AsyncSelect
            allowClear
            style={{ width: 320 }}
            opcoes={produtos}
            carregando={carregandoProdutos}
            termo={termoProduto}
            onTermoChange={setTermoProduto}
            toOption={(p) => ({ value: p.id, label: `${p.codigoInterno} — ${p.nome}` })}
            onChange={(produtoId: number | undefined) => {
              setFiltro((f) => ({ ...f, produtoId }))
              setPagina(0)
            }}
          />
          <Select
            allowClear
            style={{ width: 240 }}
            placeholder="Filtrar por depósito"
            options={depositos?.map((d) => ({ value: d.id, label: d.nome }))}
            onChange={(depositoId: number | undefined) => {
              setFiltro((f) => ({ ...f, depositoId }))
              setPagina(0)
            }}
          />
        </Flex>

        <Table<EstoqueMovimentoResponse>
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
            {
              title: 'Data',
              dataIndex: 'dataMovimento',
              render: (v: string) => dayjs(v).format('DD/MM/YYYY HH:mm'),
            },
            { title: 'Produto', key: 'produto', render: (_, m) => <ProdutoResumoCell produtoId={m.produtoId} /> },
            {
              title: 'Tipo',
              dataIndex: 'tipoMovimento',
              render: (tipo: keyof typeof TIPO_MOVIMENTO_LABEL) => <Tag color={COR_TIPO[tipo]}>{TIPO_MOVIMENTO_LABEL[tipo]}</Tag>,
            },
            { title: 'Quantidade', dataIndex: 'quantidade', align: 'right' },
            {
              title: 'Anterior → Nova',
              key: 'saldo',
              render: (_, m) => `${m.quantidadeAnterior} → ${m.quantidadeNova}`,
            },
            {
              title: 'Origem',
              dataIndex: 'origemTipo',
              render: (origem: keyof typeof ORIGEM_MOVIMENTO_LABEL) => ORIGEM_MOVIMENTO_LABEL[origem],
            },
            { title: 'Aprovador', dataIndex: 'aprovadorId', render: (v?: number) => (v ? `#${v}` : '—') },
            { title: 'Observação', dataIndex: 'observacao', render: (v) => v || '—', ellipsis: true },
          ]}
        />
      </Card>

      <RegistrarMovimentoModal open={modalAberto} onClose={() => setModalAberto(false)} />
    </Flex>
  )
}
