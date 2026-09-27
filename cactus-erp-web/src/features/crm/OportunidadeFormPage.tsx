import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import {
  App,
  Breadcrumb,
  Button,
  Card,
  Col,
  DatePicker,
  Flex,
  Form,
  Input,
  InputNumber,
  Popconfirm,
  Row,
  Select,
  Skeleton,
  Space,
  Tag,
  Tooltip,
  Typography,
} from 'antd'
import { ArrowLeftOutlined, SaveOutlined, SwapOutlined } from '@ant-design/icons'
import type { Dayjs } from 'dayjs'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import AsyncSelect from '@shared/ui/AsyncSelect'
import AtividadesSection from './AtividadesSection'
import InteracoesSection from './InteracoesSection'
import MoverEstagioModal from './modals/MoverEstagioModal'
import {
  useAtualizarOportunidade,
  useBuscaClientesParaSelecao,
  useCriarOportunidade,
  useEstagiosFunilOrdenado,
  useExcluirOportunidade,
  useOportunidade,
  useVendedoresComNome,
} from './queries'
import { SITUACAO_OPORTUNIDADE_LABEL } from './types'
import type { OportunidadeRequest } from './types'

const { Title } = Typography

const COR_SITUACAO: Record<string, string> = {
  ABERTA: 'processing',
  GANHA: 'success',
  PERDIDA: 'error',
}

interface FormValues extends Omit<OportunidadeRequest, 'dataPrevisaoFechamento'> {
  dataPrevisaoFechamento?: Dayjs
}

/**
 * `PUT` não move de estágio (usa `POST /{id}/mover-estagio`, mesmo critério
 * de `VendaService`/`OsAberturaService` — ação dedicada, nunca reescrita
 * pelo update genérico). Atividades/Interações só em modo edição.
 */
export default function OportunidadeFormPage() {
  const { id } = useParams<{ id: string }>()
  const oportunidadeId = id ? Number(id) : undefined
  const modoEdicao = oportunidadeId !== undefined
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<FormValues>()

  const { authorities } = useAuth()
  const podeProspectar = hasAuthority(authorities, 'CRM_PROSPECTAR')
  const podeGerenciar = hasAuthority(authorities, 'CRM_GERENCIAR')

  const { data: oportunidade, isLoading: carregando } = useOportunidade(oportunidadeId)
  const { data: estagios } = useEstagiosFunilOrdenado()
  const { data: vendedores, isLoading: carregandoVendedores } = useVendedoresComNome()
  const criar = useCriarOportunidade()
  const atualizar = useAtualizarOportunidade(oportunidadeId ?? -1)
  const excluir = useExcluirOportunidade()
  const salvando = criar.isPending || atualizar.isPending

  const [termoCliente, setTermoCliente] = useState<string | undefined>(undefined)
  const { data: clientes, isLoading: carregandoClientes } = useBuscaClientesParaSelecao(termoCliente)
  const [modalMoverAberto, setModalMoverAberto] = useState(false)

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (oportunidade) {
      form.setFieldsValue({
        ...oportunidade,
        dataPrevisaoFechamento: oportunidade.dataPrevisaoFechamento ? dayjs(oportunidade.dataPrevisaoFechamento) : undefined,
      })
    }
  }, [oportunidade, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function handleSalvar() {
    const valores = await form.validateFields()
    const request: OportunidadeRequest = {
      ...valores,
      dataPrevisaoFechamento: valores.dataPrevisaoFechamento ? valores.dataPrevisaoFechamento.format('YYYY-MM-DD') : undefined,
    }
    try {
      if (modoEdicao) {
        await atualizar.mutateAsync(request)
        message.success('Oportunidade atualizada com sucesso.')
      } else {
        const criada = await criar.mutateAsync(request)
        message.success('Oportunidade criada com sucesso.')
        navigate(`/crm/oportunidades/${criada.id}`)
        return
      }
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar oportunidade.')
    }
  }

  async function handleExcluir() {
    if (!oportunidadeId) return
    try {
      await excluir.mutateAsync(oportunidadeId)
      message.success('Oportunidade excluída.')
      navigate('/crm/oportunidades')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao excluir oportunidade.')
    }
  }

  if (modoEdicao && carregando) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 4 }} />
      </Card>
    )
  }

  const fechada = oportunidade && oportunidade.situacao !== 'ABERTA'

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[
          { title: <a onClick={() => navigate('/crm/oportunidades')}>Oportunidades</a> },
          { title: modoEdicao ? `Oportunidade #${oportunidadeId}` : 'Nova oportunidade' },
        ]}
      />

      <Card>
        <Form form={form} layout="vertical" disabled={!podeProspectar}>
          <Flex justify="space-between" align="center" wrap="wrap" gap={16} style={{ marginBottom: 24 }}>
            <Space size="large">
              <Title level={4} style={{ margin: 0 }}>
                {modoEdicao ? 'Editar Oportunidade' : 'Nova Oportunidade'}
              </Title>
              {modoEdicao && oportunidade && (
                <Tag color={COR_SITUACAO[oportunidade.situacao]}>{SITUACAO_OPORTUNIDADE_LABEL[oportunidade.situacao]}</Tag>
              )}
              {modoEdicao && oportunidade && <Tag>{oportunidade.estagioFunilNome}</Tag>}
            </Space>
            <Space wrap>
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/crm/oportunidades')}>
                Cancelar
              </Button>
              {modoEdicao && (
                <Popconfirm title="Excluir esta oportunidade?" onConfirm={handleExcluir} disabled={!podeGerenciar}>
                  <Tooltip title={podeGerenciar ? undefined : 'Sem permissão para excluir'}>
                    <Button danger disabled={!podeGerenciar} loading={excluir.isPending}>
                      Excluir
                    </Button>
                  </Tooltip>
                </Popconfirm>
              )}
              {modoEdicao && (
                <Tooltip title={!podeProspectar ? 'Sem permissão' : fechada ? 'Oportunidade já fechada' : undefined}>
                  <Button icon={<SwapOutlined />} disabled={!podeProspectar || fechada} onClick={() => setModalMoverAberto(true)}>
                    Mover de Estágio
                  </Button>
                </Tooltip>
              )}
              <Tooltip title={podeProspectar ? undefined : 'Você não tem permissão para gerenciar oportunidades'}>
                <Button type="primary" icon={<SaveOutlined />} loading={salvando} disabled={!podeProspectar} onClick={handleSalvar}>
                  Salvar
                </Button>
              </Tooltip>
            </Space>
          </Flex>

          {oportunidade?.motivoPerda && <Typography.Paragraph type="danger">Motivo da perda: {oportunidade.motivoPerda}</Typography.Paragraph>}

          <Row gutter={12}>
            <Col span={12}>
              <Form.Item name="nome" label="Nome da oportunidade" rules={[{ required: true, message: 'Informe o nome' }, { max: 120 }]}>
                <Input maxLength={120} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="clienteId" label="Cliente" extra="Opcional — a oportunidade pode nascer só do Lead, ver detalhe do Lead de origem.">
                <AsyncSelect
                  allowClear
                  opcoes={clientes}
                  carregando={carregandoClientes}
                  termo={termoCliente}
                  onTermoChange={setTermoCliente}
                  toOption={(c) => ({ value: c.id, label: c.nome })}
                />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={8}>
              <Form.Item
                name="estagioFunilId"
                label="Estágio inicial"
                rules={[{ required: true, message: 'Selecione o estágio' }]}
                extra={modoEdicao ? 'Trocar o estágio depois de criada é via "Mover de Estágio"' : undefined}
              >
                <Select disabled={modoEdicao} options={estagios?.map((e) => ({ value: e.id, label: e.nome }))} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="valorEstimado" label="Valor estimado (R$)">
                <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="probabilidadePercentual" label="Probabilidade (%)">
                <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name="dataPrevisaoFechamento" label="Previsão de fechamento">
                <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="vendedorId" label="Vendedor">
                <Select
                  allowClear
                  showSearch
                  loading={carregandoVendedores}
                  optionFilterProp="label"
                  options={vendedores?.map((v) => ({ value: v.id, label: v.nome }))}
                />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item name="observacao" label="Observação" rules={[{ max: 500 }]}>
            <Input.TextArea rows={2} maxLength={500} showCount />
          </Form.Item>
        </Form>
      </Card>

      {modoEdicao && oportunidadeId && (
        <>
          <AtividadesSection oportunidadeId={oportunidadeId} podeGerenciar={podeProspectar} />
          <InteracoesSection oportunidadeId={oportunidadeId} podeGerenciar={podeProspectar} />
          <MoverEstagioModal
            open={modalMoverAberto}
            oportunidadeId={oportunidadeId}
            estagioAtualId={oportunidade?.estagioFunilId}
            onClose={() => setModalMoverAberto(false)}
          />
        </>
      )}
    </Flex>
  )
}
