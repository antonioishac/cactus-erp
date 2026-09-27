import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Col, DatePicker, Flex, Form, Input, InputNumber, Row, Select, Space, Tooltip, Typography } from 'antd'
import { ArrowLeftOutlined, DeleteOutlined, PlusOutlined, SaveOutlined } from '@ant-design/icons'
import dayjs, { type Dayjs } from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useBuscaColaboradoresParaSelecao, useBuscaProdutosParaSelecao, useCriarRequisicao } from './queries'
import { TIPO_REQUISICAO_LABEL } from './types'
import type { RequisicaoCompraItemRequest, RequisicaoCompraRequest } from './types'

const { Title } = Typography

interface FormValues {
  colaboradorId: number
  tipo: RequisicaoCompraRequest['tipo']
  descricao?: string
  dataRequisicao?: Dayjs
  observacao?: string
  itens: RequisicaoCompraItemRequest[]
}

const VALORES_PADRAO: Partial<FormValues> = { tipo: 'INTERNA', dataRequisicao: dayjs() }

/**
 * `RequisicaoCompra` não tem `PUT` (ver `RequisicaoCompraController`) — nasce
 * fechada, os itens vão inline na própria criação (`Form.List`), diferente do
 * padrão de Pedido (itens só entram depois, via sub-endpoint). Sem edição
 * possível, esta tela é só criação; consulta/cancelamento ficam na Detail.
 */
export default function RequisicaoFormPage() {
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<FormValues>()
  const { authorities } = useAuth()
  const podeRequisitar = hasAuthority(authorities, 'COMPRAS_REQUISITAR')

  const [termoColaborador, setTermoColaborador] = useState<string | undefined>(undefined)
  const { data: colaboradores, isLoading: carregandoColaboradores } = useBuscaColaboradoresParaSelecao(termoColaborador)
  const [termoProduto, setTermoProduto] = useState<string | undefined>(undefined)
  const { data: produtos, isLoading: carregandoProdutos } = useBuscaProdutosParaSelecao(termoProduto)

  const criar = useCriarRequisicao()

  async function handleSalvar() {
    const valores = await form.validateFields()
    const request: RequisicaoCompraRequest = {
      ...valores,
      dataRequisicao: valores.dataRequisicao ? valores.dataRequisicao.format('YYYY-MM-DD') : undefined,
    }
    try {
      const criada = await criar.mutateAsync(request)
      message.success('Requisição criada com sucesso.')
      navigate(`/compras/requisicoes/${criada.id}`)
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao criar requisição.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[
          { title: <a onClick={() => navigate('/compras/requisicoes')}>Requisições de Compra</a> },
          { title: 'Nova requisição' },
        ]}
      />

      <Card>
        <Form form={form} name="requisicaoForm" layout="vertical" initialValues={VALORES_PADRAO} disabled={!podeRequisitar}>
          <Flex justify="space-between" align="center" style={{ marginBottom: 16 }}>
            <Title level={4} style={{ margin: 0 }}>
              Nova Requisição de Compra
            </Title>
            <Space>
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/compras/requisicoes')}>
                Cancelar
              </Button>
              <Tooltip title={podeRequisitar ? undefined : 'Você não tem permissão para criar requisições'}>
                <Button type="primary" icon={<SaveOutlined />} loading={criar.isPending} disabled={!podeRequisitar} onClick={handleSalvar}>
                  Salvar
                </Button>
              </Tooltip>
            </Space>
          </Flex>

          <Row gutter={12}>
            <Col span={10}>
              <Form.Item name="colaboradorId" label="Solicitante" rules={[{ required: true, message: 'Selecione o solicitante' }]}>
                <AsyncSelect
                  opcoes={colaboradores}
                  carregando={carregandoColaboradores}
                  termo={termoColaborador}
                  onTermoChange={setTermoColaborador}
                  toOption={(c) => ({ value: c.id, label: c.nome })}
                />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="tipo" label="Tipo" rules={[{ required: true }]}>
                <Select options={Object.entries(TIPO_REQUISICAO_LABEL).map(([value, label]) => ({ value, label }))} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="dataRequisicao" label="Data da requisição">
                <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={24}>
              <Form.Item name="descricao" label="Descrição" rules={[{ max: 200 }]}>
                <Input maxLength={200} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={12}>
            <Col span={24}>
              <Form.Item name="observacao" label="Observação" rules={[{ max: 250 }]}>
                <Input.TextArea rows={2} maxLength={250} showCount />
              </Form.Item>
            </Col>
          </Row>

          <Title level={5}>Itens</Title>
          <Form.List name="itens" rules={[{ validator: async (_, itens) => {
            if (!itens || itens.length === 0) {
              throw new Error('Adicione pelo menos um item')
            }
          } }]}>
            {(fields, { add, remove }, { errors }) => (
              <Flex vertical gap={8}>
                {fields.map((field) => (
                  <Row gutter={12} key={field.key} align="middle">
                    <Col span={18}>
                      <Form.Item
                        name={[field.name, 'produtoId']}
                        rules={[{ required: true, message: 'Selecione o produto' }]}
                        style={{ marginBottom: 8 }}
                      >
                        <AsyncSelect
                          opcoes={produtos}
                          carregando={carregandoProdutos}
                          termo={termoProduto}
                          onTermoChange={setTermoProduto}
                          toOption={(p) => ({ value: p.id, label: `${p.codigoInterno} — ${p.nome}` })}
                        />
                      </Form.Item>
                    </Col>
                    <Col span={5}>
                      <Form.Item
                        name={[field.name, 'quantidade']}
                        rules={[{ required: true, message: 'Informe a quantidade' }]}
                        style={{ marginBottom: 8 }}
                      >
                        <InputNumber style={{ width: '100%' }} min={0.001} precision={3} placeholder="Quantidade" />
                      </Form.Item>
                    </Col>
                    <Col span={1}>
                      <Button type="text" danger icon={<DeleteOutlined />} onClick={() => remove(field.name)} />
                    </Col>
                  </Row>
                ))}
                <Form.ErrorList errors={errors} />
                <Flex>
                  <Button icon={<PlusOutlined />} onClick={() => add()}>
                    Adicionar item
                  </Button>
                </Flex>
              </Flex>
            )}
          </Form.List>
        </Form>
      </Card>
    </Flex>
  )
}
