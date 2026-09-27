import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Descriptions, Flex, Popconfirm, Skeleton, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { EditOutlined, FileProtectOutlined, PlusOutlined, SendOutlined, TruckOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import ProdutoResumoCell from '@shared/ui/ProdutoResumoCell'
import GerarCteModal from '@features/fiscal/modals/GerarCteModal'
import { useGerarNotaFiscalDeVenda } from '@features/fiscal/queries'
import { ClienteResumoCell } from './ResumoCells'
import AdicionarItemVendaModal from './modals/AdicionarItemVendaModal'
import DefinirFreteModal from './modals/DefinirFreteModal'
import { useCancelarVenda, useComissoes, useConfirmarVenda, useFreteVenda, useRemoverItemVenda, useVenda } from './queries'
import { SITUACAO_COMISSAO_LABEL, SITUACAO_VENDA_LABEL } from './types'
import type { VendaItemResponse } from './types'

const { Title } = Typography

const COR_SITUACAO: Record<string, string> = {
  RASCUNHO: 'default',
  CONFIRMADA: 'success',
  CANCELADA: 'error',
}

const COR_SITUACAO_COMISSAO: Record<string, string> = {
  PENDENTE: 'gold',
  PAGO: 'success',
  CANCELADO: 'error',
}

export default function VendaDetailPage() {
  const { id } = useParams<{ id: string }>()
  const vendaId = Number(id)
  const navigate = useNavigate()
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeVender = hasAuthority(authorities, 'VENDAS_VENDER')
  const podeCancelar = hasAuthority(authorities, 'VENDAS_CANCELAR')
  const podeEmitirFiscal = hasAuthority(authorities, 'FISCAL_EMITIR')

  const [modalItemAberto, setModalItemAberto] = useState(false)
  const [itemEmEdicao, setItemEmEdicao] = useState<VendaItemResponse | null>(null)
  const [modalFreteAberto, setModalFreteAberto] = useState(false)
  const [modalCteAberto, setModalCteAberto] = useState(false)

  const { data: venda, isLoading } = useVenda(vendaId)
  const { data: frete } = useFreteVenda(vendaId)
  const { data: comissoes } = useComissoes({ vendedorId: venda?.vendedorId }, 0, 50)
  const removerItem = useRemoverItemVenda(vendaId)
  const confirmar = useConfirmarVenda(vendaId)
  const cancelar = useCancelarVenda(vendaId)
  const gerarNotaFiscal = useGerarNotaFiscalDeVenda()

  const comissaoDaVenda = comissoes?.conteudo.find((c) => c.vendaId === vendaId)

  async function handleRemoverItem(itemId: number) {
    try {
      await removerItem.mutateAsync(itemId)
      message.success('Item removido.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao remover item.')
    }
  }

  async function handleConfirmar() {
    try {
      await confirmar.mutateAsync()
      message.success('Venda confirmada — estoque baixado e comissão gerada.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao confirmar venda.')
    }
  }

  async function handleCancelar() {
    try {
      await cancelar.mutateAsync()
      message.success('Venda cancelada.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao cancelar venda.')
    }
  }

  async function handleGerarNotaFiscal() {
    try {
      const nota = await gerarNotaFiscal.mutateAsync(vendaId)
      message.success('NF-e gerada a partir da venda.')
      navigate(`/fiscal/notas-fiscais/${nota.id}`)
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao gerar NF-e.')
    }
  }

  if (isLoading || !venda) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 8 }} />
      </Card>
    )
  }

  const podeEditarItens = venda.situacao === 'RASCUNHO' && podeVender
  const podeConfirmar = venda.situacao === 'RASCUNHO' && podeVender && venda.itens.length > 0 && venda.itens.every((item) => item.depositoId)
  const podeCancelarAgora = venda.situacao === 'RASCUNHO' && podeCancelar
  const podeDefinirFrete = venda.situacao === 'RASCUNHO' && podeVender
  const podeGerarNotaFiscal = venda.situacao === 'CONFIRMADA' && podeEmitirFiscal
  const podeGerarCte = venda.situacao === 'CONFIRMADA' && podeEmitirFiscal && !!frete?.transportadoraId

  return (
    <Flex vertical gap={16}>
      <Breadcrumb items={[{ title: <a onClick={() => navigate('/vendas/vendas')}>Vendas</a> }, { title: `Venda #${venda.id}` }]} />

      <Card>
        <Flex justify="space-between" align="center" wrap="wrap" gap={16}>
          <Space size="large">
            <Title level={4} style={{ margin: 0 }}>
              Venda #{venda.id}
            </Title>
            <Tag color={COR_SITUACAO[venda.situacao]}>{SITUACAO_VENDA_LABEL[venda.situacao]}</Tag>
            {venda.vendaOrcamentoId && <Tag>Gerada do orçamento #{venda.vendaOrcamentoId}</Tag>}
          </Space>
          <Space>
            {podeDefinirFrete && (
              <Button icon={<TruckOutlined />} onClick={() => setModalFreteAberto(true)}>
                {frete ? 'Editar Frete' : 'Definir Frete'}
              </Button>
            )}
            {venda.situacao === 'RASCUNHO' && (
              <Tooltip title={podeConfirmar ? undefined : 'Adicione ao menos um item, todos com depósito definido, antes de confirmar'}>
                <Button type="primary" disabled={!podeConfirmar} loading={confirmar.isPending} onClick={handleConfirmar}>
                  Confirmar Venda
                </Button>
              </Tooltip>
            )}
            {podeCancelarAgora && (
              <Popconfirm title="Cancelar esta venda?" onConfirm={handleCancelar}>
                <Button danger loading={cancelar.isPending}>
                  Cancelar
                </Button>
              </Popconfirm>
            )}
            {venda.situacao === 'CONFIRMADA' && (
              <>
                <Tooltip title={podeGerarNotaFiscal ? undefined : 'Sem permissão para emitir documento fiscal'}>
                  <Button
                    icon={<SendOutlined />}
                    disabled={!podeGerarNotaFiscal}
                    loading={gerarNotaFiscal.isPending}
                    onClick={handleGerarNotaFiscal}
                  >
                    Gerar NF-e
                  </Button>
                </Tooltip>
                <Tooltip
                  title={
                    !podeEmitirFiscal
                      ? 'Sem permissão para emitir documento fiscal'
                      : !frete?.transportadoraId
                        ? 'Defina o frete (com transportadora) antes de gerar o CT-e'
                        : undefined
                  }
                >
                  <Button icon={<FileProtectOutlined />} disabled={!podeGerarCte} onClick={() => setModalCteAberto(true)}>
                    Gerar CT-e
                  </Button>
                </Tooltip>
              </>
            )}
          </Space>
        </Flex>

        <Descriptions column={3} style={{ marginTop: 16 }}>
          <Descriptions.Item label="Cliente">
            <ClienteResumoCell clienteId={venda.clienteId} />
          </Descriptions.Item>
          <Descriptions.Item label="Data da venda">{venda.dataVenda ? dayjs(venda.dataVenda).format('DD/MM/YYYY') : '—'}</Descriptions.Item>
          <Descriptions.Item label="Data de saída">{venda.dataSaida ? dayjs(venda.dataSaida).format('DD/MM/YYYY') : '—'}</Descriptions.Item>
          <Descriptions.Item label="Local de entrega">{venda.localEntrega || '—'}</Descriptions.Item>
          <Descriptions.Item label="Local de cobrança">{venda.localCobranca || '—'}</Descriptions.Item>
          <Descriptions.Item label="Nº da fatura">{venda.numeroFatura ?? '—'}</Descriptions.Item>
          <Descriptions.Item label="Subtotal">R$ {venda.valorSubtotal.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="Desconto">R$ {venda.valorDesconto.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="Total">
            <strong>R$ {venda.valorTotal.toFixed(2)}</strong>
          </Descriptions.Item>
          <Descriptions.Item label="Observação" span={3}>
            {venda.observacao || '—'}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      <Card
        title="Itens"
        extra={
          podeEditarItens && (
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => {
                setItemEmEdicao(null)
                setModalItemAberto(true)
              }}
            >
              Adicionar Item
            </Button>
          )
        }
      >
        <Table<VendaItemResponse>
          rowKey="id"
          dataSource={venda.itens}
          pagination={false}
          locale={{ emptyText: 'Nenhum item adicionado ainda' }}
          columns={[
            { title: 'Produto', key: 'produto', render: (_, item) => <ProdutoResumoCell produtoId={item.produtoId} /> },
            {
              title: 'Depósito',
              dataIndex: 'depositoId',
              render: (v?: number) => (v ? `#${v}` : <Tag color="warning">Sem depósito</Tag>),
            },
            { title: 'Quantidade', dataIndex: 'quantidade', align: 'right' },
            { title: 'Valor unitário', dataIndex: 'valorUnitario', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Desconto', dataIndex: 'valorDesconto', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Valor total', dataIndex: 'valorTotal', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            ...(podeEditarItens
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    render: (_: unknown, item: VendaItemResponse) => (
                      <Space>
                        <Button
                          size="small"
                          icon={<EditOutlined />}
                          onClick={() => {
                            setItemEmEdicao(item)
                            setModalItemAberto(true)
                          }}
                        >
                          Editar
                        </Button>
                        <Popconfirm title="Remover este item?" onConfirm={() => handleRemoverItem(item.id)}>
                          <Button size="small" danger>
                            Remover
                          </Button>
                        </Popconfirm>
                      </Space>
                    ),
                  },
                ]
              : []),
          ]}
        />
      </Card>

      {venda.situacao === 'CONFIRMADA' && (
        <Card title="Comissão do vendedor">
          {comissaoDaVenda ? (
            <Descriptions column={3}>
              <Descriptions.Item label="Valor da venda">R$ {comissaoDaVenda.valorVenda.toFixed(2)}</Descriptions.Item>
              <Descriptions.Item label="Valor da comissão">
                <strong>R$ {comissaoDaVenda.valorComissao.toFixed(2)}</strong>
              </Descriptions.Item>
              <Descriptions.Item label="Situação">
                <Tag color={COR_SITUACAO_COMISSAO[comissaoDaVenda.situacao]}>{SITUACAO_COMISSAO_LABEL[comissaoDaVenda.situacao]}</Tag>
              </Descriptions.Item>
            </Descriptions>
          ) : (
            <Typography.Text type="secondary">Nenhuma comissão gerada para esta venda.</Typography.Text>
          )}
        </Card>
      )}

      <AdicionarItemVendaModal
        open={modalItemAberto}
        vendaId={vendaId}
        empresaId={venda.empresaId}
        itemEmEdicao={itemEmEdicao}
        onClose={() => {
          setModalItemAberto(false)
          setItemEmEdicao(null)
        }}
      />
      <DefinirFreteModal open={modalFreteAberto} vendaId={vendaId} onClose={() => setModalFreteAberto(false)} />
      <GerarCteModal open={modalCteAberto} vendaId={vendaId} onClose={() => setModalCteAberto(false)} />
    </Flex>
  )
}
