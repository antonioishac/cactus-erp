import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Col, Flex, Form, Input, Popconfirm, Row, Select, Skeleton, Space, Tag, Tooltip, Typography } from 'antd'
import { ArrowLeftOutlined, CheckOutlined, CloseOutlined, SaveOutlined, SwapOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import AtividadesSection from './AtividadesSection'
import InteracoesSection from './InteracoesSection'
import ConverterLeadModal from './modals/ConverterLeadModal'
import DesqualificarLeadModal from './modals/DesqualificarLeadModal'
import { useAtualizarLead, useCriarLead, useExcluirLead, useLead, useQualificarLead, useVendedoresComNome } from './queries'
import { ORIGEM_LEAD_LABEL, SITUACAO_LEAD_LABEL } from './types'
import type { LeadRequest } from './types'

const { Title } = Typography

const COR_SITUACAO: Record<string, string> = {
  NOVO: 'default',
  EM_QUALIFICACAO: 'processing',
  QUALIFICADO: 'success',
  DESQUALIFICADO: 'error',
  CONVERTIDO: 'success',
}

/** Ações de máquina de estados (qualificar/desqualificar/converter) só aparecem em modo edição — não fazem sentido num lead ainda não salvo. Atividades/Interações, mesmo critério. */
export default function LeadFormPage() {
  const { id } = useParams<{ id: string }>()
  const leadId = id ? Number(id) : undefined
  const modoEdicao = leadId !== undefined
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<LeadRequest>()

  const { authorities } = useAuth()
  const podeProspectar = hasAuthority(authorities, 'CRM_PROSPECTAR')
  const podeGerenciar = hasAuthority(authorities, 'CRM_GERENCIAR')

  const { data: lead, isLoading: carregando } = useLead(leadId)
  const { data: vendedores, isLoading: carregandoVendedores } = useVendedoresComNome()
  const criar = useCriarLead()
  const atualizar = useAtualizarLead(leadId ?? -1)
  const excluir = useExcluirLead()
  const qualificar = useQualificarLead(leadId ?? -1)
  const salvando = criar.isPending || atualizar.isPending

  const [modalDesqualificarAberto, setModalDesqualificarAberto] = useState(false)
  const [modalConverterAberto, setModalConverterAberto] = useState(false)

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (lead) {
      form.setFieldsValue(lead)
    }
  }, [lead, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function handleSalvar() {
    const valores = await form.validateFields()
    try {
      if (modoEdicao) {
        await atualizar.mutateAsync(valores)
        message.success('Lead atualizado com sucesso.')
      } else {
        const criado = await criar.mutateAsync(valores)
        message.success('Lead criado com sucesso.')
        navigate(`/crm/leads/${criado.id}`)
        return
      }
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao salvar lead.')
    }
  }

  async function handleExcluir() {
    if (!leadId) return
    try {
      await excluir.mutateAsync(leadId)
      message.success('Lead excluído.')
      navigate('/crm/leads')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao excluir lead.')
    }
  }

  async function handleQualificar() {
    try {
      await qualificar.mutateAsync()
      message.success('Lead qualificado.')
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao qualificar lead.')
    }
  }

  if (modoEdicao && carregando) {
    return (
      <Card>
        <Skeleton active paragraph={{ rows: 4 }} />
      </Card>
    )
  }

  const podeQualificar = modoEdicao && lead && (lead.situacao === 'NOVO' || lead.situacao === 'EM_QUALIFICACAO') && podeProspectar
  const podeDesqualificar = modoEdicao && lead && lead.situacao !== 'DESQUALIFICADO' && lead.situacao !== 'CONVERTIDO' && podeProspectar
  const podeConverter = modoEdicao && lead && lead.situacao === 'QUALIFICADO' && podeProspectar

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[{ title: <a onClick={() => navigate('/crm/leads')}>Leads</a> }, { title: modoEdicao ? `Lead #${leadId}` : 'Novo lead' }]}
      />

      <Card>
        <Form form={form} layout="vertical" disabled={!podeProspectar}>
          <Flex justify="space-between" align="center" wrap="wrap" gap={16} style={{ marginBottom: 24 }}>
            <Space size="large">
              <Title level={4} style={{ margin: 0 }}>
                {modoEdicao ? 'Editar Lead' : 'Novo Lead'}
              </Title>
              {modoEdicao && lead && <Tag color={COR_SITUACAO[lead.situacao]}>{SITUACAO_LEAD_LABEL[lead.situacao]}</Tag>}
              {lead?.clienteId && <Tag>Convertido no cliente #{lead.clienteId}</Tag>}
            </Space>
            <Space wrap>
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/crm/leads')}>
                Cancelar
              </Button>
              {modoEdicao && (
                <Popconfirm title="Excluir este lead?" onConfirm={handleExcluir} disabled={!podeGerenciar}>
                  <Tooltip title={podeGerenciar ? undefined : 'Sem permissão para excluir'}>
                    <Button danger disabled={!podeGerenciar} loading={excluir.isPending}>
                      Excluir
                    </Button>
                  </Tooltip>
                </Popconfirm>
              )}
              {podeQualificar && (
                <Button icon={<CheckOutlined />} loading={qualificar.isPending} onClick={handleQualificar}>
                  Qualificar
                </Button>
              )}
              {podeDesqualificar && (
                <Button icon={<CloseOutlined />} onClick={() => setModalDesqualificarAberto(true)}>
                  Desqualificar
                </Button>
              )}
              {podeConverter && (
                <Button icon={<SwapOutlined />} onClick={() => setModalConverterAberto(true)}>
                  Converter em Cliente
                </Button>
              )}
              <Tooltip title={podeProspectar ? undefined : 'Você não tem permissão para gerenciar leads'}>
                <Button type="primary" icon={<SaveOutlined />} loading={salvando} disabled={!podeProspectar} onClick={handleSalvar}>
                  Salvar
                </Button>
              </Tooltip>
            </Space>
          </Flex>

          {lead?.motivoDesqualificacao && (
            <Typography.Paragraph type="danger">Motivo da desqualificação: {lead.motivoDesqualificacao}</Typography.Paragraph>
          )}

          <Row gutter={12}>
            <Col span={12}>
              <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 120 }]}>
                <Input maxLength={120} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="nomeEmpresa" label="Empresa" rules={[{ max: 120 }]}>
                <Input maxLength={120} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name="email" label="E-mail" rules={[{ type: 'email', message: 'E-mail inválido' }, { max: 120 }]}>
                <Input maxLength={120} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="telefone" label="Telefone" rules={[{ max: 20 }]}>
                <Input maxLength={20} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="origem" label="Origem">
                <Select allowClear options={Object.entries(ORIGEM_LEAD_LABEL).map(([value, label]) => ({ value, label }))} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
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

      {modoEdicao && leadId && (
        <>
          <AtividadesSection leadId={leadId} podeGerenciar={podeProspectar} />
          <InteracoesSection leadId={leadId} podeGerenciar={podeProspectar} />
          <DesqualificarLeadModal open={modalDesqualificarAberto} leadId={leadId} onClose={() => setModalDesqualificarAberto(false)} />
          <ConverterLeadModal open={modalConverterAberto} leadId={leadId} onClose={() => setModalConverterAberto(false)} />
        </>
      )}
    </Flex>
  )
}
