import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Col, DatePicker, Flex, Form, Input, Row, Select, Space, Tooltip, Typography } from 'antd'
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import type { Dayjs } from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useBuscaClientesParaSelecao, useBuscaColaboradoresParaSelecao, useCriarOsAbertura, useTodosOsStatus } from './queries'
import type { OsAberturaRequest } from './types'

const { Title } = Typography

interface FormValues extends Omit<OsAberturaRequest, 'empresaId' | 'dataInicio' | 'dataPrevisao'> {
  dataInicio?: Dayjs
  dataPrevisao?: Dayjs
}

/** Nasce sem equipamentos/itens/evolução — adicione via sub-recursos na Detail, mesmo padrão de Requisição/Pedido/Venda/Orçamento. */
export default function OsAberturaFormPage() {
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<FormValues>()
  const { empresaAtivaId, authorities } = useAuth()
  const podeAbrir = hasAuthority(authorities, 'OS_ABRIR')

  const { data: status } = useTodosOsStatus()
  const [termoCliente, setTermoCliente] = useState<string | undefined>(undefined)
  const { data: clientes, isLoading: carregandoClientes } = useBuscaClientesParaSelecao(termoCliente)
  const [termoColaborador, setTermoColaborador] = useState<string | undefined>(undefined)
  const { data: colaboradores, isLoading: carregandoColaboradores } = useBuscaColaboradoresParaSelecao(termoColaborador)

  const criar = useCriarOsAbertura()

  async function handleSalvar() {
    const valores = await form.validateFields()
    const request: OsAberturaRequest = {
      ...valores,
      empresaId: empresaAtivaId ?? 0,
      dataInicio: valores.dataInicio ? valores.dataInicio.format('YYYY-MM-DD') : undefined,
      dataPrevisao: valores.dataPrevisao ? valores.dataPrevisao.format('YYYY-MM-DD') : undefined,
    }
    try {
      const criada = await criar.mutateAsync(request)
      message.success('Ordem de serviço aberta com sucesso.')
      navigate(`/os/aberturas/${criada.id}`)
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao abrir ordem de serviço.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Breadcrumb items={[{ title: <a onClick={() => navigate('/os/aberturas')}>Ordens de Serviço</a> }, { title: 'Abrir OS' }]} />

      <Card>
        <Form form={form} name="osAberturaForm" layout="vertical" disabled={!podeAbrir}>
          <Flex justify="space-between" align="center" style={{ marginBottom: 16 }}>
            <Title level={4} style={{ margin: 0 }}>
              Abrir Ordem de Serviço
            </Title>
            <Space>
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/os/aberturas')}>
                Cancelar
              </Button>
              <Tooltip title={podeAbrir ? undefined : 'Você não tem permissão para abrir ordens de serviço'}>
                <Button type="primary" icon={<SaveOutlined />} loading={criar.isPending} disabled={!podeAbrir} onClick={handleSalvar}>
                  Salvar
                </Button>
              </Tooltip>
            </Space>
          </Flex>

          <Row gutter={12}>
            <Col span={10}>
              <Form.Item name="clienteId" label="Cliente" rules={[{ required: true, message: 'Selecione o cliente' }]}>
                <AsyncSelect
                  opcoes={clientes}
                  carregando={carregandoClientes}
                  termo={termoCliente}
                  onTermoChange={setTermoCliente}
                  toOption={(c) => ({ value: c.id, label: c.nome })}
                />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="colaboradorId" label="Colaborador responsável">
                <AsyncSelect
                  allowClear
                  opcoes={colaboradores}
                  carregando={carregandoColaboradores}
                  termo={termoColaborador}
                  onTermoChange={setTermoColaborador}
                  toOption={(c) => ({ value: c.id, label: c.nome })}
                />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="osStatusId" label="Status inicial" rules={[{ required: true, message: 'Selecione o status' }]}>
                <Select options={status?.map((s) => ({ value: s.id, label: s.nome }))} />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={12}>
            <Col span={6}>
              <Form.Item name="numero" label="Número" rules={[{ max: 20 }]}>
                <Input maxLength={20} placeholder="Livre — sem numeração fiscal obrigatória" />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="dataInicio" label="Data de início">
                <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" placeholder="Hoje (padrão)" />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="dataPrevisao" label="Previsão de entrega">
                <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="horaPrevisao" label="Hora prevista" rules={[{ max: 8 }]}>
                <Input maxLength={8} placeholder="HH:mm:ss" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={12}>
            <Col span={12}>
              <Form.Item name="nomeContato" label="Nome do contato" rules={[{ max: 50 }]}>
                <Input maxLength={50} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="foneContato" label="Telefone do contato" rules={[{ max: 15 }]}>
                <Input maxLength={15} />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item name="observacaoCliente" label="Observação do cliente (relato do problema)" rules={[{ max: 500 }]}>
            <Input.TextArea rows={2} maxLength={500} showCount />
          </Form.Item>
          <Form.Item name="observacaoAbertura" label="Observação de abertura (interna)" rules={[{ max: 500 }]}>
            <Input.TextArea rows={2} maxLength={500} showCount />
          </Form.Item>
        </Form>
      </Card>
    </Flex>
  )
}
