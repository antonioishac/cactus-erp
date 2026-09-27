import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import {
  App,
  Breadcrumb,
  Button,
  Card,
  Descriptions,
  Flex,
  Popconfirm,
  Select,
  Skeleton,
  Space,
  Table,
  Tag,
  Timeline,
  Tooltip,
  Typography,
} from 'antd'
import { CheckOutlined, FileTextOutlined, FlagOutlined, PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import ProdutoResumoCell from '@shared/ui/ProdutoResumoCell'
import GerarNotaServicoModal from '@features/fiscal/modals/GerarNotaServicoModal'
import { ClienteResumoCell, ColaboradorResumoCell } from './ResumoCells'
import AdicionarEquipamentoOsModal from './modals/AdicionarEquipamentoOsModal'
import AdicionarEvolucaoModal from './modals/AdicionarEvolucaoModal'
import AdicionarItemOsModal from './modals/AdicionarItemOsModal'
import {
  useAprovarOrcamentoOsAbertura,
  useEncerrarOsAbertura,
  useOsAbertura,
  useRemoverEquipamentoOs,
  useRemoverItemOs,
  useTodosOsStatus,
  useTrocarStatusOsAbertura,
} from './queries'
import { TIPO_ITEM_OS_LABEL } from './types'
import type { OsAberturaEquipamentoResponse, OsProdutoServicoResponse } from './types'

const { Title } = Typography

export default function OsAberturaDetailPage() {
  const { id } = useParams<{ id: string }>()
  const osId = Number(id)
  const navigate = useNavigate()
  const { message } = App.useApp()
  const { authorities } = useAuth()
  const podeAbrir = hasAuthority(authorities, 'OS_ABRIR')
  const podeEmitirFiscal = hasAuthority(authorities, 'FISCAL_EMITIR')

  const [modalEquipamentoAberto, setModalEquipamentoAberto] = useState(false)
  const [modalItemAberto, setModalItemAberto] = useState(false)
  const [modalEvolucaoAberto, setModalEvolucaoAberto] = useState(false)
  const [modalNotaServicoAberto, setModalNotaServicoAberto] = useState(false)

  const { data: os, isLoading } = useOsAbertura(osId)
  const { data: todosStatus } = useTodosOsStatus()
  const trocarStatus = useTrocarStatusOsAbertura(osId)
  const aprovarOrcamento = useAprovarOrcamentoOsAbertura(osId)
  const encerrar = useEncerrarOsAbertura(osId)
  const removerEquipamento = useRemoverEquipamentoOs(osId)
  const removerItem = useRemoverItemOs(osId)

  async function handleTrocarStatus(osStatusId: number) {
    try {
      await trocarStatus.mutateAsync(osStatusId)
      message.success('Status atualizado.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao trocar status.')
    }
  }

  async function handleAprovarOrcamento() {
    try {
      await aprovarOrcamento.mutateAsync()
      message.success('Orçamento aprovado.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao aprovar orçamento.')
    }
  }

  async function handleEncerrar() {
    try {
      await encerrar.mutateAsync()
      message.success('Ordem de serviço encerrada.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao encerrar ordem de serviço.')
    }
  }

  async function handleRemoverEquipamento(vinculoId: number) {
    try {
      await removerEquipamento.mutateAsync(vinculoId)
      message.success('Equipamento removido.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao remover equipamento.')
    }
  }

  async function handleRemoverItem(itemId: number) {
    try {
      await removerItem.mutateAsync(itemId)
      message.success('Item removido.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao remover item.')
    }
  }

  if (isLoading || !os) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 8 }} />
      </Card>
    )
  }

  const encerrada = !!os.dataFim
  const possuiItemServico = os.itens.some((item) => item.tipo === 'SERVICO')
  const podeGerarNotaServico = os.orcamentoAprovado && possuiItemServico && podeEmitirFiscal

  return (
    <Flex vertical gap={16}>
      <Breadcrumb items={[{ title: <a onClick={() => navigate('/os/aberturas')}>Ordens de Serviço</a> }, { title: os.numero || `OS #${os.id}` }]} />

      <Card>
        <Flex justify="space-between" align="center" wrap="wrap" gap={16}>
          <Space size="large" wrap>
            <Title level={4} style={{ margin: 0 }}>
              {os.numero || `OS #${os.id}`}
            </Title>
            <Select<number>
              value={os.osStatusId}
              disabled={!podeAbrir}
              style={{ minWidth: 180 }}
              options={todosStatus?.map((s) => ({ value: s.id, label: s.nome }))}
              onChange={handleTrocarStatus}
            />
            {os.orcamentoAprovado ? (
              <Tag color="success">Orçamento aprovado</Tag>
            ) : (
              <Tag color="warning">Orçamento pendente</Tag>
            )}
            {encerrada ? (
              <Tag>Encerrada em {dayjs(os.dataFim).format('DD/MM/YYYY')}</Tag>
            ) : (
              <Tag color="processing">Em aberto</Tag>
            )}
          </Space>
          <Space>
            <Tooltip title={!podeAbrir ? 'Sem permissão' : os.orcamentoAprovado ? 'Orçamento já aprovado' : undefined}>
              <Button
                icon={<CheckOutlined />}
                disabled={!podeAbrir || os.orcamentoAprovado}
                loading={aprovarOrcamento.isPending}
                onClick={handleAprovarOrcamento}
              >
                Aprovar Orçamento
              </Button>
            </Tooltip>
            <Tooltip title={!podeAbrir ? 'Sem permissão' : encerrada ? 'Já encerrada' : undefined}>
              <Popconfirm title="Encerrar esta ordem de serviço?" onConfirm={handleEncerrar} disabled={!podeAbrir || encerrada}>
                <Button icon={<FlagOutlined />} disabled={!podeAbrir || encerrada} loading={encerrar.isPending}>
                  Encerrar
                </Button>
              </Popconfirm>
            </Tooltip>
            <Tooltip
              title={
                !podeEmitirFiscal
                  ? 'Sem permissão para emitir documento fiscal'
                  : !os.orcamentoAprovado
                    ? 'Aprove o orçamento antes de gerar a NFS-e'
                    : !possuiItemServico
                      ? 'A OS precisa de ao menos 1 item de serviço (mão de obra)'
                      : undefined
              }
            >
              <Button icon={<FileTextOutlined />} disabled={!podeGerarNotaServico} onClick={() => setModalNotaServicoAberto(true)}>
                Gerar NFS-e
              </Button>
            </Tooltip>
          </Space>
        </Flex>

        <Descriptions column={3} style={{ marginTop: 16 }}>
          <Descriptions.Item label="Cliente">
            <ClienteResumoCell clienteId={os.clienteId} />
          </Descriptions.Item>
          <Descriptions.Item label="Colaborador responsável">
            {os.colaboradorId ? <ColaboradorResumoCell colaboradorId={os.colaboradorId} /> : '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Valor total">
            <strong>R$ {os.valorTotal.toFixed(2)}</strong>
          </Descriptions.Item>
          <Descriptions.Item label="Data de início">{os.dataInicio ? dayjs(os.dataInicio).format('DD/MM/YYYY') : '—'}</Descriptions.Item>
          <Descriptions.Item label="Previsão">
            {os.dataPrevisao ? dayjs(os.dataPrevisao).format('DD/MM/YYYY') : '—'} {os.horaPrevisao || ''}
          </Descriptions.Item>
          <Descriptions.Item label="Contato">
            {os.nomeContato || '—'} {os.foneContato ? `— ${os.foneContato}` : ''}
          </Descriptions.Item>
          <Descriptions.Item label="Observação do cliente" span={3}>
            {os.observacaoCliente || '—'}
          </Descriptions.Item>
          <Descriptions.Item label="Observação de abertura" span={3}>
            {os.observacaoAbertura || '—'}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      <Card
        title="Equipamentos"
        extra={
          podeAbrir && (
            <Button icon={<PlusOutlined />} onClick={() => setModalEquipamentoAberto(true)}>
              Adicionar Equipamento
            </Button>
          )
        }
      >
        <Table<OsAberturaEquipamentoResponse>
          rowKey="id"
          dataSource={os.equipamentos}
          pagination={false}
          locale={{ emptyText: 'Nenhum equipamento vinculado' }}
          columns={[
            { title: 'Tipo', dataIndex: 'osEquipamentoNome' },
            { title: 'Número de série', dataIndex: 'numeroSerie', render: (v) => v || '—' },
            { title: 'Tipo de cobertura', dataIndex: 'tipoCobertura', render: (v) => v || '—' },
            ...(podeAbrir
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    width: 100,
                    render: (_: unknown, vinculo: OsAberturaEquipamentoResponse) => (
                      <Popconfirm title="Remover este equipamento?" onConfirm={() => handleRemoverEquipamento(vinculo.id)}>
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

      <Card
        title="Itens (peças e mão de obra)"
        extra={
          podeAbrir && (
            <Button icon={<PlusOutlined />} onClick={() => setModalItemAberto(true)}>
              Adicionar Item
            </Button>
          )
        }
      >
        <Table<OsProdutoServicoResponse>
          rowKey="id"
          dataSource={os.itens}
          pagination={false}
          locale={{ emptyText: 'Nenhum item adicionado ainda' }}
          columns={[
            { title: 'Tipo', dataIndex: 'tipo', render: (v: OsProdutoServicoResponse['tipo']) => TIPO_ITEM_OS_LABEL[v] },
            {
              title: 'Produto / Complemento',
              key: 'descricao',
              render: (_, item) => (item.tipo === 'PRODUTO' && item.produtoId ? <ProdutoResumoCell produtoId={item.produtoId} /> : item.complemento || '—'),
            },
            { title: 'Quantidade', dataIndex: 'quantidade', align: 'right' },
            { title: 'Valor unitário', dataIndex: 'valorUnitario', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Desconto', dataIndex: 'valorDesconto', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            { title: 'Valor total', dataIndex: 'valorTotal', align: 'right', render: (v: number) => `R$ ${v.toFixed(2)}` },
            ...(podeAbrir
              ? [
                  {
                    title: 'Ações',
                    key: 'acoes',
                    width: 100,
                    render: (_: unknown, item: OsProdutoServicoResponse) => (
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

      <Card
        title="Evolução (linha do tempo)"
        extra={
          podeAbrir && (
            <Button icon={<PlusOutlined />} onClick={() => setModalEvolucaoAberto(true)}>
              Registrar Evolução
            </Button>
          )
        }
      >
        {os.evolucoes.length > 0 ? (
          <Timeline
            items={[...os.evolucoes]
              .sort((a, b) => (a.dataRegistro + a.horaRegistro < b.dataRegistro + b.horaRegistro ? 1 : -1))
              .map((evolucao) => ({
                key: evolucao.id,
                content: (
                  <Flex vertical>
                    <Typography.Text type="secondary">
                      {dayjs(evolucao.dataRegistro).format('DD/MM/YYYY')} às {evolucao.horaRegistro}
                      {evolucao.enviarEmail && ' — enviado por e-mail'}
                    </Typography.Text>
                    <Typography.Text>{evolucao.observacao || '—'}</Typography.Text>
                  </Flex>
                ),
              }))}
          />
        ) : (
          <Typography.Text type="secondary">Nenhuma evolução registrada ainda.</Typography.Text>
        )}
      </Card>

      <AdicionarEquipamentoOsModal open={modalEquipamentoAberto} osAberturaId={osId} onClose={() => setModalEquipamentoAberto(false)} />
      <AdicionarItemOsModal open={modalItemAberto} osAberturaId={osId} onClose={() => setModalItemAberto(false)} />
      <AdicionarEvolucaoModal open={modalEvolucaoAberto} osAberturaId={osId} onClose={() => setModalEvolucaoAberto(false)} />
      <GerarNotaServicoModal open={modalNotaServicoAberto} osAberturaId={osId} onClose={() => setModalNotaServicoAberto(false)} />
    </Flex>
  )
}
