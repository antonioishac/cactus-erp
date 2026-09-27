import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Descriptions, Flex, Popconfirm, Skeleton, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import ProdutoResumoCell from '@shared/ui/ProdutoResumoCell'
import { ClienteResumoCell, TransportadoraResumoCell } from './ResumoCells'
import AdicionarItemOrcamentoModal from './modals/AdicionarItemOrcamentoModal'
import GerarVendaModal from './modals/GerarVendaModal'
import {
  useAprovarOrcamento,
  useCancelarOrcamento,
  useEnviarOrcamento,
  useExpirarOrcamento,
  useOrcamento,
  useRemoverItemOrcamento,
} from './queries'
import { SITUACAO_ORCAMENTO_LABEL } from './types'
import type { VendaOrcamentoItemResponse } from './types'

const { Title } = Typography

const COR_SITUACAO: Record<string, string> = {
  RASCUNHO: 'default',
  ENVIADO: 'processing',
  APROVADO: 'gold',
  CONVERTIDO: 'success',
  EXPIRADO: 'default',
  CANCELADO: 'error',
}

export default function OrcamentoDetailPage() {
  const { id } = useParams<{ id: string }>()
  const orcamentoId = Number(id)
  const navigate = useNavigate()
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeOrcar = hasAuthority(authorities, 'VENDAS_ORCAR')
  const podeVender = hasAuthority(authorities, 'VENDAS_VENDER')

  const [modalItemAberto, setModalItemAberto] = useState(false)
  const [modalGerarVendaAberto, setModalGerarVendaAberto] = useState(false)

  const { data: orcamento, isLoading } = useOrcamento(orcamentoId)
  const removerItem = useRemoverItemOrcamento(orcamentoId)
  const enviar = useEnviarOrcamento(orcamentoId)
  const aprovar = useAprovarOrcamento(orcamentoId)
  const expirar = useExpirarOrcamento(orcamentoId)
  const cancelar = useCancelarOrcamento(orcamentoId)

  async function handleRemoverItem(itemId: number) {
    try {
      await removerItem.mutateAsync(itemId)
      message.success('Item removido.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao remover item.')
    }
  }

  async function handleTransicao(acao: typeof enviar, sucesso: string, falha: string) {
    try {
      await acao.mutateAsync()
      message.success(sucesso)
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : falha)
    }
  }

  if (isLoading || !orcamento) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 8 }} />
      </Card>
    )
  }

  const podeEditarItens = orcamento.situacao === 'RASCUNHO' && podeOrcar
  const podeEnviar = orcamento.situacao === 'RASCUNHO' && podeOrcar && orcamento.itens.length > 0
  const podeAprovar = orcamento.situacao === 'ENVIADO' && podeOrcar
  const podeExpirar = (orcamento.situacao === 'ENVIADO' || orcamento.situacao === 'APROVADO') && podeOrcar
  const podeCancelar = ['RASCUNHO', 'ENVIADO', 'APROVADO'].includes(orcamento.situacao) && podeOrcar
  const podeGerarVenda = orcamento.situacao === 'APROVADO' && podeVender

  return (
    <Flex vertical gap={16}>
      <Breadcrumb items={[{ title: <a onClick={() => navigate('/vendas/orcamentos')}>Orçamentos</a> }, { title: `Orçamento #${orcamento.id}` }]} />

      <Card>
        <Flex justify="space-between" align="center" wrap="wrap" gap={16}>
          <Space size="large">
            <Title level={4} style={{ margin: 0 }}>
              Orçamento #{orcamento.id}
            </Title>
            <Tag color={COR_SITUACAO[orcamento.situacao]}>{SITUACAO_ORCAMENTO_LABEL[orcamento.situacao]}</Tag>
          </Space>
          <Space>
            {orcamento.situacao === 'RASCUNHO' && (
              <Tooltip title={podeEnviar ? undefined : 'Adicione ao menos um item antes de enviar'}>
                <Button type="primary" disabled={!podeEnviar} loading={enviar.isPending} onClick={() => handleTransicao(enviar, 'Orçamento enviado.', 'Falha ao enviar orçamento.')}>
                  Enviar
                </Button>
              </Tooltip>
            )}
            {orcamento.situacao === 'ENVIADO' && (
              <Tooltip title={podeAprovar ? undefined : 'Sem permissão para aprovar orçamentos'}>
                <Button type="primary" disabled={!podeAprovar} loading={aprovar.isPending} onClick={() => handleTransicao(aprovar, 'Orçamento aprovado.', 'Falha ao aprovar orçamento.')}>
                  Aprovar
                </Button>
              </Tooltip>
            )}
            {podeGerarVenda && (
              <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalGerarVendaAberto(true)}>
                Gerar Venda
              </Button>
            )}
            {podeExpirar && (
              <Popconfirm title="Marcar este orçamento como expirado?" onConfirm={() => handleTransicao(expirar, 'Orçamento expirado.', 'Falha ao expirar orçamento.')}>
                <Button loading={expirar.isPending}>Expirar</Button>
              </Popconfirm>
            )}
            {podeCancelar && (
              <Popconfirm title="Cancelar este orçamento?" onConfirm={() => handleTransicao(cancelar, 'Orçamento cancelado.', 'Falha ao cancelar orçamento.')}>
                <Button danger loading={cancelar.isPending}>
                  Cancelar
                </Button>
              </Popconfirm>
            )}
          </Space>
        </Flex>

        <Descriptions column={3} style={{ marginTop: 16 }}>
          <Descriptions.Item label="Cliente">
            <ClienteResumoCell clienteId={orcamento.clienteId} />
          </Descriptions.Item>
          <Descriptions.Item label="Código">{orcamento.codigo || '—'}</Descriptions.Item>
          <Descriptions.Item label="Transportadora">
            {orcamento.transportadoraId ? <TransportadoraResumoCell transportadoraId={orcamento.transportadoraId} /> : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Data de cadastro">{dayjs(orcamento.dataCadastro).format('DD/MM/YYYY')}</Descriptions.Item>
          <Descriptions.Item label="Entrega prevista">{orcamento.dataEntrega ? dayjs(orcamento.dataEntrega).format('DD/MM/YYYY') : '—'}</Descriptions.Item>
          <Descriptions.Item label="Validade">{orcamento.validade ? dayjs(orcamento.validade).format('DD/MM/YYYY') : '—'}</Descriptions.Item>
          <Descriptions.Item label="Subtotal">R$ {orcamento.valorSubtotal.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="Desconto">R$ {orcamento.valorDesconto.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="Total">
            <strong>R$ {orcamento.valorTotal.toFixed(2)}</strong>
          </Descriptions.Item>
          <Descriptions.Item label="Observação" span={3}>
            {orcamento.observacao || '—'}
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
        <Table<VendaOrcamentoItemResponse>
          rowKey="id"
          dataSource={orcamento.itens}
          pagination={false}
          locale={{ emptyText: 'Nenhum item adicionado ainda' }}
          columns={[
            { title: 'Produto', key: 'produto', render: (_, item) => <ProdutoResumoCell produtoId={item.produtoId} /> },
            { title: 'Quantidade', dataIndex: 'quantidade', align: 'right' },
            { title: 'Valor unitário', dataIndex: 'valorUnitario', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Desconto', dataIndex: 'valorDesconto', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Valor total', dataIndex: 'valorTotal', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            ...(podeEditarItens
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    render: (_: unknown, item: VendaOrcamentoItemResponse) => (
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

      <AdicionarItemOrcamentoModal open={modalItemAberto} orcamentoId={orcamentoId} onClose={() => setModalItemAberto(false)} />
      <GerarVendaModal open={modalGerarVendaAberto} orcamentoId={orcamentoId} onClose={() => setModalGerarVendaAberto(false)} />
    </Flex>
  )
}
