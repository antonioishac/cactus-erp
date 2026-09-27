import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import {
  App,
  Breadcrumb,
  Button,
  Card,
  Col,
  Flex,
  Form,
  InputNumber,
  Popconfirm,
  Row,
  Select,
  Skeleton,
  Space,
  Switch,
  Table,
  Tooltip,
  Typography,
} from 'antd'
import { ArrowLeftOutlined, PlusOutlined, SaveOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { ClienteResumoCell } from './ResumoCells'
import GerenciarComissaoPerfisModal from './modals/GerenciarComissaoPerfisModal'
import AdicionarMetaVendedorModal from './modals/AdicionarMetaVendedorModal'
import AdicionarRotaVendedorModal from './modals/AdicionarRotaVendedorModal'
import {
  useAtualizarVendedor,
  useBuscaColaboradoresParaSelecao,
  useComissaoPerfis,
  useCriarVendedor,
  useMetasVendedor,
  useRemoverMetaVendedor,
  useRemoverRotaVendedor,
  useRotaVendedor,
  useVendedor,
} from './queries'
import { PERIODO_META_LABEL } from './types'
import type { VendedorMetaResponse, VendedorRequest, VendedorRotaResponse } from './types'

const { Title } = Typography

const VALORES_PADRAO_NOVO: Partial<VendedorRequest> = { situacao: 'ATIVO' }

/**
 * `VendedorRequest` em si é flat (1 `Form`, mesmo molde de `DepositoFormPage`)
 * — `colaboradorId` referencia um `Colaborador` já cadastrado (RH), este
 * formulário não cria colaborador, só marca um já existente como vendedor.
 * Rota (AFV) e Metas por cliente/período são sub-recursos (Fase B, fechada
 * nesta passada) — só aparecem em modo edição, mesmo critério já usado nos
 * sub-recursos de `Empresa`/`OsAbertura` (precisam do id já existente).
 */
export default function VendedorFormPage() {
  const { id } = useParams<{ id: string }>()
  const vendedorId = id ? Number(id) : undefined
  const modoEdicao = vendedorId !== undefined
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<VendedorRequest>()

  const { authorities } = useAuth()
  const podeSalvar = hasAuthority(authorities, 'VENDAS_GERENCIAR')

  const { data: vendedor, isLoading: carregando } = useVendedor(vendedorId)
  const { data: perfis } = useComissaoPerfis()
  const criar = useCriarVendedor()
  const atualizar = useAtualizarVendedor(vendedorId ?? -1)
  const salvando = criar.isPending || atualizar.isPending

  const [termoColaborador, setTermoColaborador] = useState<string | undefined>(undefined)
  const { data: colaboradores, isLoading: carregandoColaboradores } = useBuscaColaboradoresParaSelecao(termoColaborador)
  const [modalPerfisAberto, setModalPerfisAberto] = useState(false)
  const [modalRotaAberto, setModalRotaAberto] = useState(false)
  const [modalMetaAberto, setModalMetaAberto] = useState(false)

  const { data: rota } = useRotaVendedor(vendedorId ?? -1)
  const { data: metas } = useMetasVendedor(vendedorId ?? -1)
  const removerRota = useRemoverRotaVendedor(vendedorId ?? -1)
  const removerMeta = useRemoverMetaVendedor(vendedorId ?? -1)

  async function handleRemoverRota(rotaId: number) {
    try {
      await removerRota.mutateAsync(rotaId)
      message.success('Cliente removido da rota.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao remover cliente da rota.')
    }
  }

  async function handleRemoverMeta(metaId: number) {
    try {
      await removerMeta.mutateAsync(metaId)
      message.success('Meta removida.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao remover meta.')
    }
  }

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (vendedor) {
      form.setFieldsValue(vendedor)
    }
  }, [vendedor, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function handleSalvar() {
    const valores = await form.validateFields()
    try {
      if (modoEdicao) {
        await atualizar.mutateAsync(valores)
        message.success('Vendedor atualizado com sucesso.')
      } else {
        await criar.mutateAsync(valores)
        message.success('Vendedor criado com sucesso.')
      }
      navigate('/vendas/vendedores')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar vendedor.')
    }
  }

  if (modoEdicao && carregando) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 4 }} />
      </Card>
    )
  }

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[
          { title: <a onClick={() => navigate('/vendas/vendedores')}>Vendedores</a> },
          { title: modoEdicao ? `Vendedor #${vendedorId}` : 'Novo vendedor' },
        ]}
      />

      <Card>
        <Form form={form} layout="vertical" initialValues={VALORES_PADRAO_NOVO} disabled={!podeSalvar}>
          <Flex justify="space-between" align="center" wrap="wrap" gap={16} style={{ marginBottom: 24 }}>
            <Title level={4} style={{ margin: 0 }}>
              {modoEdicao ? 'Editar Vendedor' : 'Novo Vendedor'}
            </Title>
            <Space size="large">
              <Form.Item
                name="situacao"
                valuePropName="checked"
                noStyle
                normalize={(v: boolean) => (v ? 'ATIVO' : 'INATIVO')}
                getValueProps={(v: string) => ({ checked: v === 'ATIVO' })}
              >
                <Switch checkedChildren="Ativo" unCheckedChildren="Inativo" />
              </Form.Item>
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/vendas/vendedores')}>
                Cancelar
              </Button>
              <Tooltip title={podeSalvar ? undefined : 'Você não tem permissão para gerenciar vendedores'}>
                <Button type="primary" icon={<SaveOutlined />} loading={salvando} disabled={!podeSalvar} onClick={handleSalvar}>
                  Salvar
                </Button>
              </Tooltip>
            </Space>
          </Flex>

          <Row gutter={12}>
            <Col span={12}>
              <Form.Item name="colaboradorId" label="Colaborador" rules={[{ required: true, message: 'Selecione o colaborador' }]}>
                <AsyncSelect
                  opcoes={colaboradores}
                  carregando={carregandoColaboradores}
                  termo={termoColaborador}
                  onTermoChange={setTermoColaborador}
                  toOption={(c) => ({ value: c.id, label: c.nome })}
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="Perfil de comissão">
                <Space.Compact style={{ width: '100%' }}>
                  <Form.Item name="comissaoPerfilId" noStyle>
                    <Select
                      allowClear
                      style={{ width: '100%' }}
                      options={perfis?.conteudo.map((p) => ({ value: p.id, label: p.nome }))}
                    />
                  </Form.Item>
                  <Button icon={<PlusOutlined />} onClick={() => setModalPerfisAberto(true)} aria-label="Gerenciar perfis de comissão" />
                </Space.Compact>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name="comissaoPercentual" label="Comissão padrão (%)">
                <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="metaVenda" label="Meta de venda (R$)">
                <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Card>

      {modoEdicao && (
        <>
          <Card
            title="Rota de Visita (AFV)"
            extra={
              podeSalvar && (
                <Button icon={<PlusOutlined />} onClick={() => setModalRotaAberto(true)}>
                  Adicionar Cliente
                </Button>
              )
            }
          >
            <Table<VendedorRotaResponse>
              rowKey="id"
              dataSource={[...(rota ?? [])].sort((a, b) => (a.posicao ?? 0) - (b.posicao ?? 0))}
              pagination={false}
              locale={{ emptyText: 'Nenhum cliente na rota ainda' }}
              columns={[
                { title: 'Posição', dataIndex: 'posicao', width: 100, render: (v?: number) => v ?? '—' },
                { title: 'Cliente', key: 'cliente', render: (_, item) => <ClienteResumoCell clienteId={item.clienteId} /> },
                ...(podeSalvar
                  ? [
                      {
                        title: 'Ações',
                        key: 'acoes',
                        width: 100,
                        render: (_: unknown, item: VendedorRotaResponse) => (
                          <Popconfirm title="Remover este cliente da rota?" onConfirm={() => handleRemoverRota(item.id)}>
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
            title="Metas por Cliente/Período"
            extra={
              podeSalvar && (
                <Button icon={<PlusOutlined />} onClick={() => setModalMetaAberto(true)}>
                  Adicionar Meta
                </Button>
              )
            }
          >
            <Table<VendedorMetaResponse>
              rowKey="id"
              dataSource={metas}
              pagination={false}
              locale={{ emptyText: 'Nenhuma meta cadastrada ainda' }}
              columns={[
                { title: 'Cliente', key: 'cliente', render: (_, item) => <ClienteResumoCell clienteId={item.clienteId} /> },
                { title: 'Período', dataIndex: 'periodoMeta', render: (v?: keyof typeof PERIODO_META_LABEL) => (v ? PERIODO_META_LABEL[v] : '—') },
                { title: 'Meta orçada', dataIndex: 'metaOrcada', align: 'right', render: (v?: number) => (v != null ? `R$ ${v.toFixed(2)}` : '—') },
                { title: 'Meta realizada', dataIndex: 'metaRealizada', align: 'right', render: (v?: number) => (v != null ? `R$ ${v.toFixed(2)}` : '—') },
                { title: 'Início', dataIndex: 'dataInicio', render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY') : '—') },
                { title: 'Fim', dataIndex: 'dataFim', render: (v?: string) => (v ? dayjs(v).format('DD/MM/YYYY') : '—') },
                ...(podeSalvar
                  ? [
                      {
                        title: 'Ações',
                        key: 'acoes',
                        width: 100,
                        render: (_: unknown, item: VendedorMetaResponse) => (
                          <Popconfirm title="Remover esta meta?" onConfirm={() => handleRemoverMeta(item.id)}>
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
        </>
      )}

      <GerenciarComissaoPerfisModal open={modalPerfisAberto} onClose={() => setModalPerfisAberto(false)} />
      {modoEdicao && (
        <>
          <AdicionarRotaVendedorModal open={modalRotaAberto} vendedorId={vendedorId} onClose={() => setModalRotaAberto(false)} />
          <AdicionarMetaVendedorModal open={modalMetaAberto} vendedorId={vendedorId} onClose={() => setModalMetaAberto(false)} />
        </>
      )}
    </Flex>
  )
}
