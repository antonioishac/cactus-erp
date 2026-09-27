import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Descriptions, Flex, Popconfirm, Skeleton, Space, Table, Tag, Tooltip, Typography } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import ProdutoResumoCell from '@shared/ui/ProdutoResumoCell'
import { ColaboradorResumoCell } from './ResumoCells'
import NovaCotacaoModal from './modals/NovaCotacaoModal'
import { useCancelarRequisicao, useCotacoes, useRequisicao } from './queries'
import { SITUACAO_COTACAO_LABEL, SITUACAO_REQUISICAO_LABEL, TIPO_REQUISICAO_LABEL } from './types'
import type { CotacaoCompraResponse, RequisicaoCompraItemResponse } from './types'

const { Title } = Typography

const COR_SITUACAO_REQUISICAO: Record<string, string> = {
  ABERTA: 'processing',
  PEDIDO_GERADO: 'success',
  CANCELADA: 'error',
}

const COR_SITUACAO_COTACAO: Record<string, string> = {
  ABERTA: 'processing',
  FINALIZADA: 'success',
  CANCELADA: 'error',
}

export default function RequisicaoDetailPage() {
  const { id } = useParams<{ id: string }>()
  const requisicaoId = Number(id)
  const navigate = useNavigate()
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeRequisitar = hasAuthority(authorities, 'COMPRAS_REQUISITAR')
  const podeCotar = hasAuthority(authorities, 'COMPRAS_COTAR')

  const [modalCotacaoAberto, setModalCotacaoAberto] = useState(false)

  const { data: requisicao, isLoading } = useRequisicao(requisicaoId)
  const { data: cotacoes, isLoading: carregandoCotacoes } = useCotacoes(requisicaoId)
  const cancelar = useCancelarRequisicao(requisicaoId)

  async function handleCancelar() {
    try {
      await cancelar.mutateAsync()
      message.success('Requisição cancelada.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao cancelar requisição.')
    }
  }

  if (isLoading || !requisicao) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 8 }} />
      </Card>
    )
  }

  const podeCancelar = requisicao.situacao === 'ABERTA' && podeRequisitar
  const podeNovaCotacao = requisicao.situacao === 'ABERTA' && podeCotar

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[
          { title: <a onClick={() => navigate('/compras/requisicoes')}>Requisições de Compra</a> },
          { title: `Requisição #${requisicao.id}` },
        ]}
      />

      <Card>
        <Flex justify="space-between" align="center" wrap="wrap" gap={16}>
          <Space size="large">
            <Title level={4} style={{ margin: 0 }}>
              Requisição #{requisicao.id}
            </Title>
            <Tag color={COR_SITUACAO_REQUISICAO[requisicao.situacao]}>{SITUACAO_REQUISICAO_LABEL[requisicao.situacao]}</Tag>
          </Space>
          {requisicao.situacao === 'ABERTA' && (
            <Tooltip title={podeCancelar ? undefined : 'Sem permissão'}>
              <Popconfirm title="Cancelar esta requisição?" onConfirm={handleCancelar} disabled={!podeCancelar}>
                <Button danger disabled={!podeCancelar} loading={cancelar.isPending}>
                  Cancelar Requisição
                </Button>
              </Popconfirm>
            </Tooltip>
          )}
        </Flex>

        <Descriptions column={3} style={{ marginTop: 16 }}>
          <Descriptions.Item label="Solicitante">
            <ColaboradorResumoCell colaboradorId={requisicao.colaboradorId} />
          </Descriptions.Item>
          <Descriptions.Item label="Tipo">{TIPO_REQUISICAO_LABEL[requisicao.tipo]}</Descriptions.Item>
          <Descriptions.Item label="Data">
            {requisicao.dataRequisicao ? dayjs(requisicao.dataRequisicao).format('DD/MM/YYYY') : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Descrição" span={3}>
            {requisicao.descricao || '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Observação" span={3}>
            {requisicao.observacao || '—'}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      <Card title="Itens">
        <Table<RequisicaoCompraItemResponse>
          rowKey="id"
          dataSource={requisicao.itens}
          pagination={false}
          columns={[
            { title: 'Produto', key: 'produto', render: (_, item) => <ProdutoResumoCell produtoId={item.produtoId} /> },
            { title: 'Quantidade', dataIndex: 'quantidade', align: 'right' },
          ]}
        />
      </Card>

      <Card
        title="Cotações"
        extra={
          <Tooltip title={podeNovaCotacao ? undefined : 'Só é possível cotar requisições em aberto, com permissão de cotação'}>
            <Button type="primary" icon={<PlusOutlined />} disabled={!podeNovaCotacao} onClick={() => setModalCotacaoAberto(true)}>
              Nova Cotação
            </Button>
          </Tooltip>
        }
      >
        <Table<CotacaoCompraResponse>
          rowKey="id"
          loading={carregandoCotacoes}
          dataSource={cotacoes?.conteudo ?? []}
          pagination={false}
          onRow={(cotacao) => ({
            style: { cursor: 'pointer' },
            onClick: () => navigate(`/compras/cotacoes/${cotacao.id}`),
          })}
          locale={{ emptyText: 'Nenhuma cotação criada ainda' }}
          columns={[
            { title: 'Id', dataIndex: 'id', width: 80 },
            { title: 'Descrição', dataIndex: 'descricao', render: (v) => v || '—' },
            {
              title: 'Data',
              dataIndex: 'dataCotacao',
              render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY') : '—'),
            },
            { title: 'Fornecedores concorrentes', key: 'fornecedores', render: (_, c) => c.fornecedores.length },
            {
              title: 'Situação',
              dataIndex: 'situacao',
              render: (situacao: keyof typeof SITUACAO_COTACAO_LABEL) => (
                <Tag color={COR_SITUACAO_COTACAO[situacao]}>{SITUACAO_COTACAO_LABEL[situacao]}</Tag>
              ),
            },
          ]}
        />
      </Card>

      <NovaCotacaoModal
        open={modalCotacaoAberto}
        requisicaoId={requisicaoId}
        onClose={(cotacaoId) => {
          setModalCotacaoAberto(false)
          if (cotacaoId) navigate(`/compras/cotacoes/${cotacaoId}`)
        }}
      />
    </Flex>
  )
}
