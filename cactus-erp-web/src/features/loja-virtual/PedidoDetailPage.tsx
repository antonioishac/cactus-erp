import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Descriptions, Flex, Skeleton, Space, Table, Tag, Tooltip, Typography } from 'antd'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import CancelarPedidoModal from './modals/CancelarPedidoModal'
import {
  useConfirmarPagamentoPedido,
  useMarcarPedidoEmSeparacao,
  useMarcarPedidoEntregue,
  useMarcarPedidoEnviado,
  usePedido,
} from './queries'
import { FORMA_PAGAMENTO_LOJA_LABEL, SITUACAO_PEDIDO_LABEL } from './types'
import type { ItemPedidoResponse } from './types'

const { Title } = Typography

const COR_SITUACAO: Record<string, string> = {
  AGUARDANDO_PAGAMENTO: 'gold',
  PAGO: 'blue',
  EM_SEPARACAO: 'processing',
  ENVIADO: 'cyan',
  ENTREGUE: 'success',
  CANCELADO: 'default',
}

export default function PedidoDetailPage() {
  const navigate = useNavigate()
  const { message } = App.useApp()
  const { id } = useParams()
  const { authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'LOJA_GERENCIAR')
  const [modalCancelarAberto, setModalCancelarAberto] = useState(false)

  const pedidoId = id ? Number(id) : undefined
  const { data: pedido, isLoading } = usePedido(pedidoId)
  const confirmarPagamento = useConfirmarPagamentoPedido(pedidoId ?? -1)
  const marcarEmSeparacao = useMarcarPedidoEmSeparacao(pedidoId ?? -1)
  const marcarEnviado = useMarcarPedidoEnviado(pedidoId ?? -1)
  const marcarEntregue = useMarcarPedidoEntregue(pedidoId ?? -1)

  if (isLoading || !pedido) {
    return <Skeleton active />
  }

  async function transicionar(acao: () => Promise<unknown>, sucesso: string) {
    try {
      await acao()
      message.success(sucesso)
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao atualizar o pedido.')
    }
  }

  const podeCancelar = podeGerenciar && pedido.situacao !== 'ENVIADO' && pedido.situacao !== 'ENTREGUE' && pedido.situacao !== 'CANCELADO'

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[
          { title: <a onClick={() => navigate('/loja-virtual/pedidos')}>Pedidos</a> },
          { title: `Pedido #${pedido.id}` },
        ]}
      />

      <Card>
        <Flex justify="space-between" align="center" style={{ marginBottom: 16 }}>
          <Title level={4} style={{ margin: 0 }}>
            Pedido #{pedido.id}
          </Title>
          <Space>
            {podeGerenciar && pedido.situacao === 'AGUARDANDO_PAGAMENTO' && (
              <Button type="primary" onClick={() => transicionar(() => confirmarPagamento.mutateAsync(), 'Pagamento confirmado.')}>
                Confirmar Pagamento
              </Button>
            )}
            {podeGerenciar && pedido.situacao === 'PAGO' && (
              <Button onClick={() => transicionar(() => marcarEmSeparacao.mutateAsync(), 'Pedido em separação.')}>
                Marcar Em Separação
              </Button>
            )}
            {podeGerenciar && pedido.situacao === 'EM_SEPARACAO' && (
              <Button onClick={() => transicionar(() => marcarEnviado.mutateAsync(), 'Pedido enviado.')}>Marcar Enviado</Button>
            )}
            {podeGerenciar && pedido.situacao === 'ENVIADO' && (
              <Button onClick={() => transicionar(() => marcarEntregue.mutateAsync(), 'Pedido entregue.')}>Marcar Entregue</Button>
            )}
            <Tooltip title={podeCancelar ? undefined : 'Não é possível cancelar a partir desta situação'}>
              <Button danger disabled={!podeCancelar} onClick={() => setModalCancelarAberto(true)}>
                Cancelar
              </Button>
            </Tooltip>
          </Space>
        </Flex>

        <Descriptions bordered column={2} size="small">
          <Descriptions.Item label="Cliente">{pedido.clienteNome}</Descriptions.Item>
          <Descriptions.Item label="E-mail">{pedido.clienteEmail}</Descriptions.Item>
          <Descriptions.Item label="Data">{dayjs(pedido.dataCriacao).format('DD/MM/YYYY HH:mm')}</Descriptions.Item>
          <Descriptions.Item label="Forma de pagamento">{FORMA_PAGAMENTO_LOJA_LABEL[pedido.formaPagamento]}</Descriptions.Item>
          <Descriptions.Item label="Situação">
            <Tag color={COR_SITUACAO[pedido.situacao]}>{SITUACAO_PEDIDO_LABEL[pedido.situacao]}</Tag>
          </Descriptions.Item>
          {pedido.motivoCancelamento && <Descriptions.Item label="Motivo do cancelamento">{pedido.motivoCancelamento}</Descriptions.Item>}
          <Descriptions.Item label="Endereço de entrega" span={2}>
            {[pedido.logradouroEntrega, pedido.numeroEntrega, pedido.bairroEntrega, pedido.municipioEntrega, pedido.ufEntrega]
              .filter(Boolean)
              .join(', ') || '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Subtotal">R$ {pedido.valorSubtotal.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="Frete">R$ {pedido.valorFrete.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="Total" span={2}>
            <strong>R$ {pedido.valorTotal.toFixed(2)}</strong>
          </Descriptions.Item>
        </Descriptions>
      </Card>

      <Card title="Itens">
        <Table<ItemPedidoResponse>
          rowKey="id"
          dataSource={pedido.itens}
          pagination={false}
          columns={[
            { title: 'SKU', dataIndex: 'skuSnapshot' },
            { title: 'Produto', dataIndex: 'nomeSnapshot' },
            { title: 'Quantidade', dataIndex: 'quantidade', align: 'right' },
            { title: 'Preço unitário', dataIndex: 'precoUnitario', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Total', dataIndex: 'valorTotal', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
          ]}
        />
      </Card>

      {pedidoId && <CancelarPedidoModal open={modalCancelarAberto} pedidoId={pedidoId} onClose={() => setModalCancelarAberto(false)} />}
    </Flex>
  )
}
