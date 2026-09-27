import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Col, DatePicker, Flex, Form, Input, InputNumber, Row, Select, Space, Tooltip, Typography } from 'antd'
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import type { Dayjs } from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import AsyncSelect from '@shared/ui/AsyncSelect'
import {
  useBuscaClientesParaSelecao,
  useBuscaTransportadorasParaSelecao,
  useCriarVenda,
  useFormasPagamentoParaSelecao,
  useNotasFiscaisTipos,
  useTodasCondicoesPagamento,
  useVendedores,
} from './queries'
import { TIPO_FRETE_LABEL } from './types'
import type { VendaRequest } from './types'

const { Title } = Typography

interface FormValues extends Omit<VendaRequest, 'dataVenda' | 'dataSaida' | 'empresaId'> {
  dataVenda?: Dayjs
  dataSaida?: Dayjs
}

/**
 * `VendaCabecalho` não tem `PUT` (ver `VendaController`) — nasce em `RASCUNHO`
 * sem itens, que entram depois via sub-endpoint na Detail. Esta tela cobre só
 * venda AVULSA (sem orçamento de origem) — vendas geradas de um Orçamento
 * aprovado nascem via `GerarVendaModal`, não aqui. `empresaId` é implícito da
 * empresa ativa da sessão, mesmo padrão de `PedidoFormPage` (gestao-compras-service).
 */
export default function VendaFormPage() {
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<FormValues>()
  const { empresaAtivaId, authorities } = useAuth()
  const podeVender = hasAuthority(authorities, 'VENDAS_VENDER')

  const [termoCliente, setTermoCliente] = useState<string | undefined>(undefined)
  const { data: clientes, isLoading: carregandoClientes } = useBuscaClientesParaSelecao(termoCliente)
  const [termoTransportadora, setTermoTransportadora] = useState<string | undefined>(undefined)
  const { data: transportadoras, isLoading: carregandoTransportadoras } = useBuscaTransportadorasParaSelecao(termoTransportadora)
  const { data: vendedores } = useVendedores(0, 100)
  const { data: condicoes } = useTodasCondicoesPagamento()
  const { data: notasFiscaisTipos } = useNotasFiscaisTipos()
  const { data: formasPagamento } = useFormasPagamentoParaSelecao()

  const criar = useCriarVenda()

  async function handleSalvar() {
    const valores = await form.validateFields()
    const request: VendaRequest = {
      ...valores,
      empresaId: empresaAtivaId ?? 0,
      dataVenda: valores.dataVenda ? valores.dataVenda.format('YYYY-MM-DD') : undefined,
      dataSaida: valores.dataSaida ? valores.dataSaida.format('YYYY-MM-DD') : undefined,
    }
    try {
      const criada = await criar.mutateAsync(request)
      message.success('Venda criada com sucesso.')
      navigate(`/vendas/vendas/${criada.id}`)
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao criar venda.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Breadcrumb items={[{ title: <a onClick={() => navigate('/vendas/vendas')}>Vendas</a> }, { title: 'Nova venda avulsa' }]} />

      <Card>
        <Form form={form} name="vendaForm" layout="vertical" disabled={!podeVender}>
          <Flex justify="space-between" align="center" style={{ marginBottom: 16 }}>
            <Title level={4} style={{ margin: 0 }}>
              Nova Venda Avulsa
            </Title>
            <Space>
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/vendas/vendas')}>
                Cancelar
              </Button>
              <Tooltip title={podeVender ? undefined : 'Você não tem permissão para criar vendas'}>
                <Button type="primary" icon={<SaveOutlined />} loading={criar.isPending} disabled={!podeVender} onClick={handleSalvar}>
                  Salvar
                </Button>
              </Tooltip>
            </Space>
          </Flex>

          <Row gutter={12}>
            <Col span={8}>
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
              <Form.Item name="vendedorId" label="Vendedor" rules={[{ required: true, message: 'Selecione o vendedor' }]}>
                <Select options={vendedores?.conteudo.map((v) => ({ value: v.id, label: v.comissaoPerfilNome ?? `Vendedor #${v.id}` }))} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="condicoesPagamentoId" label="Condição de pagamento" rules={[{ required: true, message: 'Selecione a condição' }]}>
                <Select options={condicoes?.conteudo.map((c) => ({ value: c.id, label: c.nome }))} />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name="notaFiscalTipoId" label="Tipo de nota fiscal" rules={[{ required: true, message: 'Selecione o tipo' }]}>
                <Select options={notasFiscaisTipos?.conteudo.map((t) => ({ value: t.id, label: t.nome }))} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="formaPagamentoId" label="Forma de pagamento" rules={[{ required: true, message: 'Selecione a forma' }]}>
                <Select options={formasPagamento?.map((f) => ({ value: f.id, label: f.descricao }))} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="transportadoraId" label="Transportadora">
                <AsyncSelect
                  allowClear
                  opcoes={transportadoras}
                  carregando={carregandoTransportadoras}
                  termo={termoTransportadora}
                  onTermoChange={setTermoTransportadora}
                  toOption={(t) => ({ value: t.id, label: t.nome })}
                />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={12}>
            <Col span={8}>
              <Form.Item name="dataVenda" label="Data da venda">
                <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="dataSaida" label="Data de saída">
                <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="numeroFatura" label="Nº da fatura">
                <InputNumber style={{ width: '100%' }} min={1} />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={12}>
            <Col span={12}>
              <Form.Item name="localEntrega" label="Local de entrega" rules={[{ max: 200 }]}>
                <Input maxLength={200} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="localCobranca" label="Local de cobrança" rules={[{ max: 200 }]}>
                <Input maxLength={200} />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={12}>
            <Col span={6}>
              <Form.Item name="tipoFrete" label="Tipo de frete">
                <Select allowClear options={Object.entries(TIPO_FRETE_LABEL).map(([value, label]) => ({ value, label }))} />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="valorFrete" label="Valor do frete (R$)">
                <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="valorSeguro" label="Valor do seguro (R$)">
                <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="diaFixoParcela" label="Dia fixo de parcela">
                <InputNumber style={{ width: '100%' }} min={1} max={31} />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={12}>
            <Col span={6}>
              <Form.Item name="taxaComissao" label="Taxa de comissão (%)">
                <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="taxaDesconto" label="Desconto (%)">
                <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
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
