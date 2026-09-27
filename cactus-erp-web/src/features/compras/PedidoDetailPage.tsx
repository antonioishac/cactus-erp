import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Descriptions, Flex, Popconfirm, Skeleton, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import ProdutoResumoCell from '@shared/ui/ProdutoResumoCell'
import { ColaboradorResumoCell, FornecedorResumoCell } from './ResumoCells'
import AdicionarItemPedidoModal from './modals/AdicionarItemPedidoModal'
import NovoRecebimentoModal from './modals/NovoRecebimentoModal'
import {
  useAprovarPedido,
  useCancelarPedido,
  useEnviarPedido,
  usePedido,
  useRecebimentos,
  useRemoverItemPedido,
} from './queries'
import { ORIGEM_PEDIDO_LABEL, SITUACAO_PEDIDO_LABEL, TIPO_PEDIDO_LABEL } from './types'
import type { PedidoCompraItemResponse, RecebimentoCompraResponse } from './types'

const { Title } = Typography

const COR_SITUACAO: Record<string, string> = {
  RASCUNHO: 'default',
  AGUARDANDO_APROVACAO: 'warning',
  ENVIADO: 'processing',
  PARCIALMENTE_RECEBIDO: 'gold',
  RECEBIDO_TOTAL: 'success',
  CANCELADO: 'error',
}

export default function PedidoDetailPage() {
  const { id } = useParams<{ id: string }>()
  const pedidoId = Number(id)
  const navigate = useNavigate()
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'COMPRAS_GERENCIAR')
  const podeAprovar = hasAuthority(authorities, 'COMPRAS_APROVAR')
  const podeReceber = hasAuthority(authorities, 'COMPRAS_RECEBER')

  const [modalItemAberto, setModalItemAberto] = useState(false)
  const [modalRecebimentoAberto, setModalRecebimentoAberto] = useState(false)

  const { data: pedido, isLoading } = usePedido(pedidoId)
  const { data: recebimentos, isLoading: carregandoRecebimentos } = useRecebimentos(pedidoId)
  const removerItem = useRemoverItemPedido(pedidoId)
  const enviar = useEnviarPedido(pedidoId)
  const aprovar = useAprovarPedido(pedidoId)
  const cancelar = useCancelarPedido(pedidoId)

  async function handleRemoverItem(itemId: number) {
    try {
      await removerItem.mutateAsync(itemId)
      message.success('Item removido.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao remover item.')
    }
  }

  async function handleEnviar() {
    try {
      await enviar.mutateAsync()
      message.success('Pedido enviado.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao enviar pedido.')
    }
  }

  async function handleAprovar() {
    try {
      await aprovar.mutateAsync()
      message.success('Pedido aprovado.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao aprovar pedido.')
    }
  }

  async function handleCancelar() {
    try {
      await cancelar.mutateAsync()
      message.success('Pedido cancelado.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao cancelar pedido.')
    }
  }

  if (isLoading || !pedido) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 8 }} />
      </Card>
    )
  }

  const podeEditarItens = pedido.situacao === 'RASCUNHO' && podeGerenciar
  const podeEnviar = pedido.situacao === 'RASCUNHO' && podeGerenciar && pedido.itens.length > 0
  const podeAprovarAgora = pedido.situacao === 'AGUARDANDO_APROVACAO' && podeAprovar
  const podeCancelar = ['RASCUNHO', 'AGUARDANDO_APROVACAO', 'ENVIADO'].includes(pedido.situacao) && podeGerenciar
  const itensPendentes = pedido.itens.filter((item) => item.quantidadePendente > 0)
  const podeReceberAgora =
    (pedido.situacao === 'ENVIADO' || pedido.situacao === 'PARCIALMENTE_RECEBIDO') && podeReceber && itensPendentes.length > 0
  const mostrarRecebimentos = pedido.situacao !== 'RASCUNHO' && pedido.situacao !== 'AGUARDANDO_APROVACAO'

  return (
    <Flex vertical gap={16}>
      <Breadcrumb items={[{ title: <a onClick={() => navigate('/compras/pedidos')}>Pedidos de Compra</a> }, { title: `Pedido #${pedido.id}` }]} />

      <Card>
        <Flex justify="space-between" align="center" wrap="wrap" gap={16}>
          <Space size="large">
            <Title level={4} style={{ margin: 0 }}>
              Pedido #{pedido.id}
            </Title>
            <Tag color={COR_SITUACAO[pedido.situacao]}>{SITUACAO_PEDIDO_LABEL[pedido.situacao]}</Tag>
          </Space>
          <Space>
            {pedido.situacao === 'RASCUNHO' && (
              <Tooltip title={podeEnviar ? undefined : 'Adicione ao menos um item antes de enviar'}>
                <Button type="primary" disabled={!podeEnviar} loading={enviar.isPending} onClick={handleEnviar}>
                  Enviar Pedido
                </Button>
              </Tooltip>
            )}
            {pedido.situacao === 'AGUARDANDO_APROVACAO' && (
              <Tooltip title={podeAprovarAgora ? undefined : 'Sem permissão para aprovar pedidos'}>
                <Button type="primary" disabled={!podeAprovarAgora} loading={aprovar.isPending} onClick={handleAprovar}>
                  Aprovar Pedido
                </Button>
              </Tooltip>
            )}
            {podeCancelar && (
              <Popconfirm title="Cancelar este pedido?" onConfirm={handleCancelar}>
                <Button danger loading={cancelar.isPending}>
                  Cancelar
                </Button>
              </Popconfirm>
            )}
          </Space>
        </Flex>

        <Descriptions column={3} style={{ marginTop: 16 }}>
          <Descriptions.Item label="Fornecedor">
            <FornecedorResumoCell fornecedorId={pedido.fornecedorId} />
          </Descriptions.Item>
          <Descriptions.Item label="Comprador">
            <ColaboradorResumoCell colaboradorId={pedido.colaboradorId} />
          </Descriptions.Item>
          <Descriptions.Item label="Tipo">{TIPO_PEDIDO_LABEL[pedido.tipo]}</Descriptions.Item>
          <Descriptions.Item label="Origem">{ORIGEM_PEDIDO_LABEL[pedido.origemTipo]}</Descriptions.Item>
          <Descriptions.Item label="Data do pedido">{pedido.dataPedido ? dayjs(pedido.dataPedido).format('DD/MM/YYYY') : '—'}</Descriptions.Item>
          <Descriptions.Item label="Previsão de entrega">
            {pedido.dataPrevistaEntrega ? dayjs(pedido.dataPrevistaEntrega).format('DD/MM/YYYY') : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Subtotal">R$ {pedido.valorSubtotal.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="Desconto">R$ {pedido.valorDesconto.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="Total">
            <strong>R$ {pedido.valorTotal.toFixed(2)}</strong>
          </Descriptions.Item>
          {pedido.aprovadorId && (
            <>
              <Descriptions.Item label="Aprovador">#{pedido.aprovadorId}</Descriptions.Item>
              <Descriptions.Item label="Data da aprovação" span={2}>
                {pedido.dataAprovacao ? dayjs(pedido.dataAprovacao).format('DD/MM/YYYY HH:mm') : '—'}
              </Descriptions.Item>
            </>
          )}
          <Descriptions.Item label="Observação" span={3}>
            {pedido.observacao || '—'}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      <Card
        title="Itens"
        extra={
          podeEditarItens && (
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalItemAberto(true)}>
              Adicionar Item
            </Button>
          )
        }
      >
        <Table<PedidoCompraItemResponse>
          rowKey="id"
          dataSource={pedido.itens}
          pagination={false}
          locale={{ emptyText: 'Nenhum item adicionado ainda' }}
          columns={[
            { title: 'Produto', key: 'produto', render: (_, item) => <ProdutoResumoCell produtoId={item.produtoId} /> },
            { title: 'Quantidade', dataIndex: 'quantidade', align: 'right' },
            { title: 'Recebida', dataIndex: 'quantidadeRecebida', align: 'right' },
            { title: 'Pendente', dataIndex: 'quantidadePendente', align: 'right' },
            { title: 'Valor unitário', dataIndex: 'valorUnitario', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Valor total', dataIndex: 'valorTotal', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            ...(podeEditarItens
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    render: (_: unknown, item: PedidoCompraItemResponse) => (
                      <Popconfirm title="Remover este item?" onConfirm={() => handleRemoverItem(item.id)}>
                        <Button size="small" danger>
                          Remover
                        </Button>
                      </Popconfirm>
                    ),
                  },
                ]
              : []),
          ]}
        />
      </Card>

      {mostrarRecebimentos && (
        <Card
          title="Recebimentos"
          extra={
            <Tooltip
              title={
                podeReceberAgora
                  ? undefined
                  : itensPendentes.length === 0
                    ? 'Todos os itens já foram recebidos'
                    : 'Sem permissão para registrar recebimento'
              }
            >
              <Button
                type="primary"
                icon={<PlusOutlined />}
                disabled={!podeReceberAgora}
                onClick={() => setModalRecebimentoAberto(true)}
              >
                Novo Recebimento
              </Button>
            </Tooltip>
          }
        >
          <Table<RecebimentoCompraResponse>
            rowKey="id"
            loading={carregandoRecebimentos}
            dataSource={recebimentos?.conteudo ?? []}
            pagination={false}
            locale={{ emptyText: 'Nenhum recebimento registrado ainda' }}
            columns={[
              { title: 'Id', dataIndex: 'id', width: 80 },
              { title: 'Data', dataIndex: 'dataRecebimento', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
              { title: 'Nota fiscal', dataIndex: 'numeroNotaFiscal', render: (v) => v || '—' },
              { title: 'Itens', key: 'itens', render: (_, r) => r.itens.length },
              { title: 'Observação', dataIndex: 'observacao', render: (v) => v || '—', ellipsis: true },
            ]}
          />
        </Card>
      )}

      <AdicionarItemPedidoModal open={modalItemAberto} pedidoId={pedidoId} onClose={() => setModalItemAberto(false)} />
      <NovoRecebimentoModal
        open={modalRecebimentoAberto}
        pedidoId={pedidoId}
        empresaId={pedido.empresaId}
        itensPendentes={itensPendentes}
        onClose={() => setModalRecebimentoAberto(false)}
      />
    </Flex>
  )
}
