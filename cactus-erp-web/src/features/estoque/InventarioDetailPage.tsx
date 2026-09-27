import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Flex, InputNumber, Popconfirm, Skeleton, Space, Table, Tag, Tooltip, Typography } from 'antd'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import ProdutoResumoCell from '@shared/ui/ProdutoResumoCell'
import {
  useCancelarInventario,
  useFinalizarInventario,
  useIniciarContagemInventario,
  useInventario,
  useLancarContagemInventario,
} from './queries'
import { SITUACAO_INVENTARIO_LABEL, TIPO_INVENTARIO_LABEL } from './types'
import type { InventarioFisicoItemResponse } from './types'

const { Title } = Typography

const COR_SITUACAO: Record<string, string> = {
  ABERTO: 'default',
  EM_CONTAGEM: 'processing',
  FINALIZADO: 'success',
  CANCELADO: 'error',
}

/**
 * `quantidadeSistema` só é exibida quando o inventário é `POR_SALDO` ou já
 * foi fechado — em `CEGA` durante a contagem, a API devolve o campo mesmo
 * assim (documentado como decisão do app cliente, ver `InventarioFisicoItemResponse.java`),
 * então esconder é responsabilidade desta tela, não do backend.
 */
export default function InventarioDetailPage() {
  const { id } = useParams<{ id: string }>()
  const inventarioId = Number(id)
  const navigate = useNavigate()
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'ESTOQUE_INVENTARIO_GERENCIAR')

  const { data: inventario, isLoading } = useInventario(inventarioId)
  const iniciarContagem = useIniciarContagemInventario(inventarioId)
  const lancarContagem = useLancarContagemInventario(inventarioId)
  const finalizar = useFinalizarInventario(inventarioId)
  const cancelar = useCancelarInventario(inventarioId)

  const [contagens, setContagens] = useState<Record<number, number>>({})

  async function handleIniciarContagem() {
    try {
      await iniciarContagem.mutateAsync()
      message.success('Contagem iniciada.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao iniciar contagem.')
    }
  }

  async function handleLancarContagem(itemId: number) {
    const quantidadeContada = contagens[itemId]
    if (quantidadeContada === undefined) return
    try {
      await lancarContagem.mutateAsync({ itemId, request: { quantidadeContada } })
      message.success('Contagem lançada.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao lançar contagem.')
    }
  }

  async function handleFinalizar() {
    try {
      await finalizar.mutateAsync()
      message.success('Inventário finalizado — ajuste automático gerado pra cada divergência.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao finalizar inventário.')
    }
  }

  async function handleCancelar() {
    try {
      await cancelar.mutateAsync()
      message.success('Inventário cancelado.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao cancelar inventário.')
    }
  }

  if (isLoading || !inventario) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 8 }} />
      </Card>
    )
  }

  const podeContar = inventario.situacao === 'EM_CONTAGEM'
  const mostrarQuantidadeSistema = inventario.tipo === 'POR_SALDO' || inventario.situacao === 'FINALIZADO' || inventario.situacao === 'CANCELADO'

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[
          { title: <a onClick={() => navigate('/estoque/inventarios')}>Inventários Físicos</a> },
          { title: `Inventário #${inventario.id}` },
        ]}
      />

      <Card>
        <Flex justify="space-between" align="center" wrap="wrap" gap={16}>
          <Space size="large">
            <Title level={4} style={{ margin: 0 }}>
              Inventário #{inventario.id} — {inventario.depositoNome}
            </Title>
            <Tag color={COR_SITUACAO[inventario.situacao]}>{SITUACAO_INVENTARIO_LABEL[inventario.situacao]}</Tag>
            <span>{TIPO_INVENTARIO_LABEL[inventario.tipo]}</span>
          </Space>
          <Space>
            {inventario.situacao === 'ABERTO' && (
              <Tooltip title={podeGerenciar ? undefined : 'Sem permissão'}>
                <Button type="primary" disabled={!podeGerenciar} loading={iniciarContagem.isPending} onClick={handleIniciarContagem}>
                  Iniciar Contagem
                </Button>
              </Tooltip>
            )}
            {inventario.situacao === 'EM_CONTAGEM' && (
              <Tooltip title={podeGerenciar ? undefined : 'Sem permissão'}>
                <Button type="primary" disabled={!podeGerenciar} loading={finalizar.isPending} onClick={handleFinalizar}>
                  Finalizar Inventário
                </Button>
              </Tooltip>
            )}
            {(inventario.situacao === 'ABERTO' || inventario.situacao === 'EM_CONTAGEM') && podeGerenciar && (
              <Popconfirm title="Cancelar este inventário?" onConfirm={handleCancelar}>
                <Button danger loading={cancelar.isPending}>
                  Cancelar
                </Button>
              </Popconfirm>
            )}
          </Space>
        </Flex>

        <Flex gap={24} style={{ marginTop: 12, color: 'rgba(0,0,0,0.45)' }}>
          <span>Abertura: {dayjs(inventario.dataAbertura).format('DD/MM/YYYY HH:mm')}</span>
          {inventario.dataFechamento && <span>Fechamento: {dayjs(inventario.dataFechamento).format('DD/MM/YYYY HH:mm')}</span>}
        </Flex>
      </Card>

      <Card>
        <Table<InventarioFisicoItemResponse>
          rowKey="id"
          dataSource={inventario.itens}
          pagination={false}
          columns={[
            { title: 'Produto', key: 'produto', render: (_, item) => <ProdutoResumoCell produtoId={item.produtoId} /> },
            { title: 'Lote', dataIndex: 'loteId', render: (v?: number) => v ?? '—' },
            ...(mostrarQuantidadeSistema
              ? [{ title: 'Qtd. sistema', dataIndex: 'quantidadeSistema', align: 'right' as const }]
              : []),
            {
              title: 'Qtd. contada',
              key: 'quantidadeContada',
              align: 'right' as const,
              render: (_, item) =>
                podeContar && podeGerenciar ? (
                  <InputNumber
                    size="small"
                    min={0}
                    value={contagens[item.id] ?? item.quantidadeContada}
                    onChange={(v) => setContagens((c) => ({ ...c, [item.id]: v ?? 0 }))}
                  />
                ) : (
                  (item.quantidadeContada ?? '—')
                ),
            },
            ...(podeContar && podeGerenciar
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    render: (_: unknown, item: InventarioFisicoItemResponse) => (
                      <Button size="small" disabled={contagens[item.id] === undefined} onClick={() => handleLancarContagem(item.id)}>
                        Salvar contagem
                      </Button>
                    ),
                  },
                ]
              : []),
            {
              title: 'Divergência',
              dataIndex: 'quantidadeDivergencia',
              align: 'right',
              render: (v?: number) =>
                v === undefined || v === null ? '—' : <Tag color={v === 0 ? 'success' : 'error'}>{v > 0 ? `+${v}` : v}</Tag>,
            },
          ]}
        />
      </Card>
    </Flex>
  )
}
