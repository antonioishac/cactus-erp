import { useState } from 'react'
import { Button, Card, Flex, Switch, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import AsyncSelect from '@shared/ui/AsyncSelect'
import ProdutoResumoCell from '@shared/ui/ProdutoResumoCell'
import LoteModal from './modals/LoteModal'
import { useBuscaProdutosParaSelecao, useLotes, useLotesProximosDoVencimento } from './queries'
import type { LoteResponse } from './types'

const { Title } = Typography
const TAMANHO_PAGINA = 10

/** Rastreabilidade por lote — obrigatório em alimentício/farma (ver `LoteController`). */
export default function LotesListPage() {
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'ESTOQUE_GERENCIAR')
  const [termoProduto, setTermoProduto] = useState<string | undefined>(undefined)
  const [produtoId, setProdutoId] = useState<number | undefined>(undefined)
  const [pagina, setPagina] = useState(0)
  const [soVencimentoProximo, setSoVencimentoProximo] = useState(false)
  const [modalAberto, setModalAberto] = useState(false)
  const [loteEmEdicao, setLoteEmEdicao] = useState<LoteResponse | undefined>(undefined)

  const { data: produtos, isLoading: carregandoProdutos } = useBuscaProdutosParaSelecao(termoProduto)
  const listaNormal = useLotes(produtoId, pagina, TAMANHO_PAGINA)
  const listaVencimento = useLotesProximosDoVencimento(produtoId, 30, pagina, TAMANHO_PAGINA)
  const { data, isLoading, isPlaceholderData } = soVencimentoProximo ? listaVencimento : listaNormal

  return (
    <Flex vertical gap={16}>
      <Flex justify="space-between" align="center">
        <Title level={3} style={{ margin: 0 }}>
          Lotes
        </Title>
        <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para gerenciar lotes'}>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            disabled={!podeGerenciar}
            onClick={() => {
              setLoteEmEdicao(undefined)
              setModalAberto(true)
            }}
          >
            Novo Lote
          </Button>
        </Tooltip>
      </Flex>

      <Card>
        <Flex gap={12} wrap="wrap" align="center" style={{ marginBottom: 16 }}>
          <AsyncSelect
            allowClear
            style={{ width: 320 }}
            opcoes={produtos}
            carregando={carregandoProdutos}
            termo={termoProduto}
            onTermoChange={setTermoProduto}
            toOption={(p) => ({ value: p.id, label: `${p.codigoInterno} — ${p.nome}` })}
            onChange={(valor: number | undefined) => {
              setProdutoId(valor)
              setPagina(0)
            }}
          />
          <Tooltip title={produtoId ? undefined : 'Selecione um produto pra ver só os lotes próximos do vencimento (30 dias)'}>
            <Flex gap={8} align="center">
              <Switch
                disabled={!produtoId}
                checked={soVencimentoProximo}
                onChange={(v) => {
                  setSoVencimentoProximo(v)
                  setPagina(0)
                }}
              />
              <span>Só vencimento próximo (30 dias)</span>
            </Flex>
          </Tooltip>
        </Flex>

        <Table<LoteResponse>
          rowKey="id"
          loading={isLoading || isPlaceholderData}
          dataSource={data?.conteudo ?? []}
          onRow={(lote) => ({
            style: { cursor: 'pointer' },
            onClick: () => {
              setLoteEmEdicao(lote)
              setModalAberto(true)
            },
          })}
          pagination={{
            current: pagina + 1,
            pageSize: TAMANHO_PAGINA,
            total: data?.totalRegistros ?? 0,
            onChange: (novaPagina) => setPagina(novaPagina - 1),
            showTotal: (total) => `${total} lote(s)`,
          }}
          columns={[
            { title: 'Código', dataIndex: 'codigo' },
            { title: 'Produto', key: 'produto', render: (_, lote) => <ProdutoResumoCell produtoId={lote.produtoId} /> },
            {
              title: 'Fabricação',
              dataIndex: 'dataFabricacao',
              render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY') : '—'),
            },
            {
              title: 'Validade',
              dataIndex: 'dataValidade',
              render: (v?: string) =>
                v ? (
                  <Tag color={dayjs(v).diff(dayjs(), 'day') <= 30 ? 'error' : undefined}>{dayjs(v).format('DD/MM/YYYY')}</Tag>
                ) : (
                  '—'
                ),
            },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (v) => <Tag color={v === 'ATIVO' ? 'success' : 'default'}>{v === 'ATIVO' ? 'Ativo' : 'Inativo'}</Tag>,
            },
          ]}
        />
      </Card>

      <LoteModal open={modalAberto} valorInicial={loteEmEdicao} onClose={() => setModalAberto(false)} />
    </Flex>
  )
}
