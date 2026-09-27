import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Descriptions, Flex, Popconfirm, Skeleton, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import ProdutoResumoCell from '@shared/ui/ProdutoResumoCell'
import { FornecedorResumoCell } from './ResumoCells'
import AdicionarFornecedorCotacaoModal from './modals/AdicionarFornecedorCotacaoModal'
import AdicionarItemFornecedorCotacaoModal from './modals/AdicionarItemFornecedorCotacaoModal'
import GerarPedidoModal from './modals/GerarPedidoModal'
import { useCancelarCotacao, useCotacao } from './queries'
import { SITUACAO_COTACAO_LABEL } from './types'
import type { FornecedorCotacaoItemResponse, FornecedorCotacaoResponse } from './types'

const { Title } = Typography

const COR_SITUACAO: Record<string, string> = {
  ABERTA: 'processing',
  FINALIZADA: 'success',
  CANCELADA: 'error',
}

export default function CotacaoDetailPage() {
  const { id } = useParams<{ id: string }>()
  const cotacaoId = Number(id)
  const navigate = useNavigate()
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeCotar = hasAuthority(authorities, 'COMPRAS_COTAR')

  const [modalFornecedorAberto, setModalFornecedorAberto] = useState(false)
  const [fornecedorItemAlvo, setFornecedorItemAlvo] = useState<number | undefined>(undefined)
  const [fornecedorPedidoAlvo, setFornecedorPedidoAlvo] = useState<number | undefined>(undefined)

  const { data: cotacao, isLoading } = useCotacao(cotacaoId)
  const cancelar = useCancelarCotacao(cotacaoId)

  async function handleCancelar() {
    try {
      await cancelar.mutateAsync()
      message.success('Cotação cancelada.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao cancelar cotação.')
    }
  }

  if (isLoading || !cotacao) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 8 }} />
      </Card>
    )
  }

  const cotacaoAberta = cotacao.situacao === 'ABERTA'
  const podeGerenciar = cotacaoAberta && podeCotar

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[
          { title: <a onClick={() => navigate(`/compras/requisicoes/${cotacao.requisicaoId}`)}>Requisição #{cotacao.requisicaoId}</a> },
          { title: `Cotação #${cotacao.id}` },
        ]}
      />

      <Card>
        <Flex justify="space-between" align="center" wrap="wrap" gap={16}>
          <Space size="large">
            <Title level={4} style={{ margin: 0 }}>
              Cotação #{cotacao.id}
            </Title>
            <Tag color={COR_SITUACAO[cotacao.situacao]}>{SITUACAO_COTACAO_LABEL[cotacao.situacao]}</Tag>
          </Space>
          <Space>
            {cotacaoAberta && (
              <Tooltip title={podeGerenciar ? undefined : 'Sem permissão'}>
                <Button
                  type="primary"
                  icon={<PlusOutlined />}
                  disabled={!podeGerenciar}
                  onClick={() => setModalFornecedorAberto(true)}
                >
                  Adicionar Fornecedor
                </Button>
              </Tooltip>
            )}
            {cotacaoAberta && (
              <Popconfirm title="Cancelar esta cotação?" onConfirm={handleCancelar} disabled={!podeGerenciar}>
                <Button danger disabled={!podeGerenciar} loading={cancelar.isPending}>
                  Cancelar Cotação
                </Button>
              </Popconfirm>
            )}
          </Space>
        </Flex>

        <Descriptions column={2} style={{ marginTop: 16 }}>
          <Descriptions.Item label="Data da cotação">
            {cotacao.dataCotacao ? dayjs(cotacao.dataCotacao).format('DD/MM/YYYY') : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Descrição">{cotacao.descricao || '—'}</Descriptions.Item>
        </Descriptions>
      </Card>

      {cotacao.fornecedores.length === 0 && (
        <Card>
          <Flex justify="center" style={{ color: 'rgba(0,0,0,0.45)', padding: 24 }}>
            Nenhum fornecedor concorrente adicionado ainda.
          </Flex>
        </Card>
      )}

      {cotacao.fornecedores.map((fornecedor: FornecedorCotacaoResponse) => (
        <Card
          key={fornecedor.id}
          title={<FornecedorResumoCell fornecedorId={fornecedor.fornecedorId} />}
          extra={
            <Space>
              {cotacaoAberta && (
                <Tooltip title={podeGerenciar ? undefined : 'Sem permissão'}>
                  <Button size="small" disabled={!podeGerenciar} onClick={() => setFornecedorItemAlvo(fornecedor.id)}>
                    Adicionar Item
                  </Button>
                </Tooltip>
              )}
              {cotacaoAberta && (
                <Tooltip
                  title={
                    !podeGerenciar
                      ? 'Sem permissão'
                      : fornecedor.itens.length === 0
                        ? 'Adicione ao menos um item cotado antes de gerar o pedido'
                        : undefined
                  }
                >
                  <Button
                    type="primary"
                    size="small"
                    disabled={!podeGerenciar || fornecedor.itens.length === 0}
                    onClick={() => setFornecedorPedidoAlvo(fornecedor.id)}
                  >
                    Gerar Pedido
                  </Button>
                </Tooltip>
              )}
            </Space>
          }
        >
          <Descriptions column={2} size="small" style={{ marginBottom: 12 }}>
            <Descriptions.Item label="Prazo de entrega">{fornecedor.prazoEntrega || '—'}</Descriptions.Item>
            <Descriptions.Item label="Condições de pagamento">{fornecedor.condicoesPagamento || '—'}</Descriptions.Item>
          </Descriptions>

          <Table<FornecedorCotacaoItemResponse>
            rowKey="id"
            dataSource={fornecedor.itens}
            pagination={false}
            size="small"
            locale={{ emptyText: 'Nenhum item cotado ainda' }}
            columns={[
              { title: 'Produto', key: 'produto', render: (_, item) => <ProdutoResumoCell produtoId={item.produtoId} /> },
              { title: 'Quantidade', dataIndex: 'quantidade', align: 'right' },
              { title: 'Valor unitário', dataIndex: 'valorUnitario', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
              { title: 'Desconto (%)', dataIndex: 'taxaDesconto', align: 'right', render: (v?: number) => v ?? '—' },
              { title: 'Valor total', dataIndex: 'valorTotal', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            ]}
            summary={() => (
              <Table.Summary.Row>
                <Table.Summary.Cell index={0} colSpan={4} align="right">
                  <strong>Subtotal / Desconto / Total</strong>
                </Table.Summary.Cell>
                <Table.Summary.Cell index={1} align="right">
                  R$ {fornecedor.valorSubtotal.toFixed(2)} / R$ {fornecedor.valorDesconto.toFixed(2)} / R${' '}
                  {fornecedor.valorTotal.toFixed(2)}
                </Table.Summary.Cell>
              </Table.Summary.Row>
            )}
          />
        </Card>
      ))}

      <AdicionarFornecedorCotacaoModal
        open={modalFornecedorAberto}
        cotacaoId={cotacaoId}
        onClose={() => setModalFornecedorAberto(false)}
      />
      <AdicionarItemFornecedorCotacaoModal
        open={fornecedorItemAlvo !== undefined}
        cotacaoId={cotacaoId}
        fornecedorCotacaoId={fornecedorItemAlvo}
        onClose={() => setFornecedorItemAlvo(undefined)}
      />
      <GerarPedidoModal
        open={fornecedorPedidoAlvo !== undefined}
        cotacaoId={cotacaoId}
        fornecedorCotacaoId={fornecedorPedidoAlvo}
        onClose={(pedidoId) => {
          setFornecedorPedidoAlvo(undefined)
          if (pedidoId) navigate(`/compras/pedidos/${pedidoId}`)
        }}
      />
    </Flex>
  )
}
