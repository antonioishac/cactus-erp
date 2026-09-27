import { useState } from 'react'
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
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import type { Dayjs } from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useBuscaColaboradoresParaSelecao, useBuscaFornecedoresParaSelecao, useCriarPedido } from './queries'
import { INDICADOR_PAGAMENTO_LABEL, TIPO_FRETE_LABEL, TIPO_PEDIDO_LABEL } from './types'
import type { PedidoCompraRequest } from './types'

const { Title } = Typography

interface FormValues extends Omit<PedidoCompraRequest, 'dataPedido' | 'dataPrevistaEntrega' | 'dataPrevisaoPagamento' | 'empresaId' | 'origemTipo'> {
  dataPedido?: Dayjs
  dataPrevistaEntrega?: Dayjs
  dataPrevisaoPagamento?: Dayjs
}

const VALORES_PADRAO: Partial<FormValues> = { tipo: 'NORMAL' }

/**
 * `PedidoCompra` não tem `PUT` (ver `PedidoCompraController`) — nasce em
 * `RASCUNHO` sem itens, que entram depois via sub-endpoint na Detail. Esta
 * tela cobre só a origem AVULSO (sem requisição/cotação de origem) — pedidos
 * originados de Cotação nascem via `GerarPedidoModal`, não aqui.
 */
export default function PedidoFormPage() {
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<FormValues>()
  const { empresaAtivaId, authorities } = useAuth()
  const podeGerenciar = hasAuthority(authorities, 'COMPRAS_GERENCIAR')

  const [termoFornecedor, setTermoFornecedor] = useState<string | undefined>(undefined)
  const { data: fornecedores, isLoading: carregandoFornecedores } = useBuscaFornecedoresParaSelecao(termoFornecedor)
  const [termoColaborador, setTermoColaborador] = useState<string | undefined>(undefined)
  const { data: colaboradores, isLoading: carregandoColaboradores } = useBuscaColaboradoresParaSelecao(termoColaborador)

  const criar = useCriarPedido()

  async function handleSalvar() {
    const valores = await form.validateFields()
    const request: PedidoCompraRequest = {
      ...valores,
      empresaId: empresaAtivaId ?? 0,
      origemTipo: 'AVULSO',
      dataPedido: valores.dataPedido ? valores.dataPedido.format('YYYY-MM-DD') : undefined,
      dataPrevistaEntrega: valores.dataPrevistaEntrega ? valores.dataPrevistaEntrega.format('YYYY-MM-DD') : undefined,
      dataPrevisaoPagamento: valores.dataPrevisaoPagamento ? valores.dataPrevisaoPagamento.format('YYYY-MM-DD') : undefined,
    }
    try {
      const criado = await criar.mutateAsync(request)
      message.success('Pedido de compra criado com sucesso.')
      navigate(`/compras/pedidos/${criado.id}`)
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao criar pedido.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Breadcrumb items={[{ title: <a onClick={() => navigate('/compras/pedidos')}>Pedidos de Compra</a> }, { title: 'Novo pedido avulso' }]} />

      <Card>
        <Form form={form} name="pedidoForm" layout="vertical" initialValues={VALORES_PADRAO} disabled={!podeGerenciar}>
          <Flex justify="space-between" align="center" style={{ marginBottom: 16 }}>
            <Title level={4} style={{ margin: 0 }}>
              Novo Pedido de Compra Avulso
            </Title>
            <Space>
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/compras/pedidos')}>
                Cancelar
              </Button>
              <Tooltip title={podeGerenciar ? undefined : 'Você não tem permissão para criar pedidos'}>
                <Button type="primary" icon={<SaveOutlined />} loading={criar.isPending} disabled={!podeGerenciar} onClick={handleSalvar}>
                  Salvar
                </Button>
              </Tooltip>
            </Space>
          </Flex>

          <Row gutter={12}>
            <Col span={10}>
              <Form.Item name="fornecedorId" label="Fornecedor" rules={[{ required: true, message: 'Selecione o fornecedor' }]}>
                <AsyncSelect
                  opcoes={fornecedores}
                  carregando={carregandoFornecedores}
                  termo={termoFornecedor}
                  onTermoChange={setTermoFornecedor}
                  toOption={(f) => ({ value: f.id, label: f.nome })}
                />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="colaboradorId" label="Comprador responsável" rules={[{ required: true, message: 'Selecione o comprador' }]}>
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
                <Select options={Object.entries(TIPO_PEDIDO_LABEL).map(([value, label]) => ({ value, label }))} />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name="dataPedido" label="Data do pedido">
                <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="dataPrevistaEntrega" label="Previsão de entrega">
                <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="dataPrevisaoPagamento" label="Previsão de pagamento">
                <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name="localEntrega" label="Local de entrega" rules={[{ max: 200 }]}>
                <Input maxLength={200} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="localCobranca" label="Local de cobrança" rules={[{ max: 200 }]}>
                <Input maxLength={200} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="contato" label="Contato" rules={[{ max: 100 }]}>
                <Input maxLength={100} />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name="tipoFrete" label="Tipo de frete">
                <Select allowClear options={Object.entries(TIPO_FRETE_LABEL).map(([value, label]) => ({ value, label }))} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="indicadorPagamento" label="Indicador de pagamento (NF-e)">
                <Select allowClear options={Object.entries(INDICADOR_PAGAMENTO_LABEL).map(([value, label]) => ({ value, label }))} />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={12}>
            <Col span={6}>
              <Form.Item name="quantidadeParcelas" label="Qtd. parcelas">
                <InputNumber style={{ width: '100%' }} min={1} />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="diaPrimeiroVencimento" label="1º vencimento (dias)">
                <InputNumber style={{ width: '100%' }} min={0} />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="intervaloEntreParcelas" label="Intervalo entre parcelas (dias)">
                <InputNumber style={{ width: '100%' }} min={0} />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="diaFixoParcela" label="Dia fixo de parcela">
                <InputNumber style={{ width: '100%' }} min={1} max={31} />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name="valorFrete" label="Valor do frete (R$)">
                <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="valorSeguro" label="Valor do seguro (R$)">
                <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="valorOutrasDespesas" label="Outras despesas (R$)">
                <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item name="observacao" label="Observação" rules={[{ max: 250 }]}>
            <Input.TextArea rows={2} maxLength={250} showCount />
          </Form.Item>
        </Form>
      </Card>
    </Flex>
  )
}
