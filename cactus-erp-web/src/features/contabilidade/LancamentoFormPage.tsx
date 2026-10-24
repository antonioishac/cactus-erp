import { useNavigate } from 'react-router-dom'
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
  Row,
  Select,
  Space,
  Tooltip,
  Typography,
} from 'antd'
import { ArrowLeftOutlined, DeleteOutlined, PlusOutlined, SaveOutlined } from '@ant-design/icons'
import dayjs, { type Dayjs } from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { useCriarLancamento, useTodasContas, useTodosCentrosCusto } from './queries'
import { TIPO_PARTIDA_LABEL } from './types'
import type { LancamentoContabilRequest, PartidaContabilRequest } from './types'

const { Title, Text } = Typography

interface FormValues {
  dataLancamento: Dayjs
  historico: string
  numeroDocumento?: string
  partidas: PartidaContabilRequest[]
}

const VALORES_PADRAO: Partial<FormValues> = { dataLancamento: dayjs() }

/**
 * `LancamentoContabil` não tem `PUT` (é imutável depois de lançado, ver
 * `LancamentoContabilController`) — nasce fechado, partidas vão inline na própria
 * criação (`Form.List`), mesmo padrão de `RequisicaoFormPage`. A validação de
 * débito=crédito exato é feita pelo servidor (422) — não replicada aqui por ser
 * aritmética simples demais pra justificar duplicação, e o backend é quem decide
 * a régua ("não podemos admitir erros").
 */
export default function LancamentoFormPage() {
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<FormValues>()
  const { empresaAtivaId, authorities } = useAuth()
  const podeLancar = hasAuthority(authorities, 'CONTABIL_LANCAR')

  const { data: contas, isLoading: carregandoContas } = useTodasContas(empresaAtivaId ?? -1)
  const { data: centros } = useTodosCentrosCusto(empresaAtivaId ?? -1)
  const contasAnaliticas = contas?.filter((c) => c.contaAnalitica)

  const criar = useCriarLancamento()

  async function handleSalvar() {
    const valores = await form.validateFields()
    const request: LancamentoContabilRequest = {
      empresaId: empresaAtivaId ?? -1,
      dataLancamento: valores.dataLancamento.format('YYYY-MM-DD'),
      historico: valores.historico,
      numeroDocumento: valores.numeroDocumento,
      partidas: valores.partidas,
    }
    try {
      const criado = await criar.mutateAsync(request)
      message.success('Lançamento registrado com sucesso.')
      navigate(`/contabilidade/lancamentos/${criado.id}`)
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao registrar lançamento.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Breadcrumb
        items={[
          { title: <a onClick={() => navigate('/contabilidade/lancamentos')}>Lançamentos Contábeis</a> },
          { title: 'Novo lançamento' },
        ]}
      />

      <Card>
        <Form form={form} name="lancamentoForm" layout="vertical" initialValues={VALORES_PADRAO} disabled={!podeLancar}>
          <Flex justify="space-between" align="center" style={{ marginBottom: 16 }}>
            <Title level={4} style={{ margin: 0 }}>
              Novo Lançamento Contábil
            </Title>
            <Space>
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/contabilidade/lancamentos')}>
                Cancelar
              </Button>
              <Tooltip title={podeLancar ? undefined : 'Você não tem permissão para lançar'}>
                <Button
                  type="primary"
                  icon={<SaveOutlined />}
                  loading={criar.isPending}
                  disabled={!podeLancar}
                  onClick={handleSalvar}
                >
                  Salvar
                </Button>
              </Tooltip>
            </Space>
          </Flex>

          <Row gutter={12}>
            <Col span={6}>
              <Form.Item name="dataLancamento" label="Data do lançamento" rules={[{ required: true, message: 'Informe a data' }]}>
                <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="numeroDocumento" label="Nº do documento" rules={[{ max: 30 }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={10}>
              <Form.Item name="historico" label="Histórico" rules={[{ required: true, message: 'Informe o histórico' }, { max: 250 }]}>
                <Input />
              </Form.Item>
            </Col>
          </Row>

          <Title level={5}>Partidas</Title>
          <Text type="secondary">A soma dos débitos precisa ser exatamente igual à soma dos créditos.</Text>
          <Form.List
            name="partidas"
            rules={[
              {
                validator: async (_, partidas) => {
                  if (!partidas || partidas.length < 2) {
                    throw new Error('Informe ao menos 2 partidas (1 débito e 1 crédito)')
                  }
                },
              },
            ]}
          >
            {(fields, { add, remove }, { errors }) => (
              <Flex vertical gap={8} style={{ marginTop: 8 }}>
                {fields.map((field) => (
                  <Row gutter={12} key={field.key} align="middle">
                    <Col span={7}>
                      <Form.Item
                        name={[field.name, 'contaId']}
                        rules={[{ required: true, message: 'Selecione a conta' }]}
                        style={{ marginBottom: 8 }}
                      >
                        <Select
                          showSearch
                          loading={carregandoContas}
                          optionFilterProp="label"
                          placeholder="Conta"
                          options={contasAnaliticas?.map((c) => ({ value: c.id, label: `${c.codigo} — ${c.nome}` }))}
                        />
                      </Form.Item>
                    </Col>
                    <Col span={5}>
                      <Form.Item
                        name={[field.name, 'centroCustoId']}
                        style={{ marginBottom: 8 }}
                      >
                        <Select
                          allowClear
                          showSearch
                          optionFilterProp="label"
                          placeholder="Centro de custo"
                          options={centros?.map((c) => ({ value: c.id, label: `${c.codigo} — ${c.nome}` }))}
                        />
                      </Form.Item>
                    </Col>
                    <Col span={4}>
                      <Form.Item
                        name={[field.name, 'tipoPartida']}
                        rules={[{ required: true, message: 'Débito ou crédito' }]}
                        style={{ marginBottom: 8 }}
                      >
                        <Select
                          placeholder="D/C"
                          options={Object.entries(TIPO_PARTIDA_LABEL).map(([value, label]) => ({ value, label }))}
                        />
                      </Form.Item>
                    </Col>
                    <Col span={4}>
                      <Form.Item
                        name={[field.name, 'valor']}
                        rules={[{ required: true, message: 'Informe o valor' }]}
                        style={{ marginBottom: 8 }}
                      >
                        <InputNumber style={{ width: '100%' }} min={0.01} precision={2} placeholder="Valor" />
                      </Form.Item>
                    </Col>
                    <Col span={3}>
                      <Form.Item name={[field.name, 'historicoComplementar']} style={{ marginBottom: 8 }}>
                        <Input placeholder="Complemento" />
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
                    Adicionar partida
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
