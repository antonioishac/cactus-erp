import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { App, Breadcrumb, Button, Card, Col, DatePicker, Flex, Form, Input, InputNumber, Row, Select, Space, Tooltip, Typography } from 'antd'
import { ArrowLeftOutlined, SaveOutlined } from '@ant-design/icons'
import type { Dayjs } from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useBuscaClientesParaSelecao, useBuscaTransportadorasParaSelecao, useCriarOrcamento, useTodasCondicoesPagamento, useVendedores } from './queries'
import { TIPO_FRETE_LABEL } from './types'
import type { VendaOrcamentoRequest } from './types'

const { Title } = Typography

interface FormValues extends Omit<VendaOrcamentoRequest, 'dataEntrega' | 'validade'> {
  dataEntrega?: Dayjs
  validade?: Dayjs
}

/**
 * `VendaOrcamento` nasce em `RASCUNHO` sem itens (ver `VendaOrcamentoController`)
 * — mesmo padrão de `RequisicaoCompra`/`PedidoCompra`. Itens entram depois, na
 * Detail. `vendedorId` vem de `useVendedores` (só 1 página de até 10 — troca
 * pra `AsyncSelect` se o volume real de vendedores um dia justificar).
 */
export default function OrcamentoFormPage() {
  const navigate = useNavigate()
  const { message } = App.useApp()
  const [form] = Form.useForm<FormValues>()
  const { authorities } = useAuth()
  const podeOrcar = hasAuthority(authorities, 'VENDAS_ORCAR')

  const [termoCliente, setTermoCliente] = useState<string | undefined>(undefined)
  const { data: clientes, isLoading: carregandoClientes } = useBuscaClientesParaSelecao(termoCliente)
  const [termoTransportadora, setTermoTransportadora] = useState<string | undefined>(undefined)
  const { data: transportadoras, isLoading: carregandoTransportadoras } = useBuscaTransportadorasParaSelecao(termoTransportadora)
  const { data: vendedores } = useVendedores(0, 100)
  const { data: condicoes } = useTodasCondicoesPagamento()

  const criar = useCriarOrcamento()

  async function handleSalvar() {
    const valores = await form.validateFields()
    const request: VendaOrcamentoRequest = {
      ...valores,
      dataEntrega: valores.dataEntrega ? valores.dataEntrega.format('YYYY-MM-DD') : undefined,
      validade: valores.validade ? valores.validade.format('YYYY-MM-DD') : undefined,
    }
    try {
      const criado = await criar.mutateAsync(request)
      message.success('Orçamento criado com sucesso.')
      navigate(`/vendas/orcamentos/${criado.id}`)
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao criar orçamento.')
    }
  }

  return (
    <Flex vertical gap={16}>
      <Breadcrumb items={[{ title: <a onClick={() => navigate('/vendas/orcamentos')}>Orçamentos</a> }, { title: 'Novo orçamento' }]} />

      <Card>
        <Form form={form} name="orcamentoForm" layout="vertical" disabled={!podeOrcar}>
          <Flex justify="space-between" align="center" style={{ marginBottom: 16 }}>
            <Title level={4} style={{ margin: 0 }}>
              Novo Orçamento
            </Title>
            <Space>
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/vendas/orcamentos')}>
                Cancelar
              </Button>
              <Tooltip title={podeOrcar ? undefined : 'Você não tem permissão para criar orçamentos'}>
                <Button type="primary" icon={<SaveOutlined />} loading={criar.isPending} disabled={!podeOrcar} onClick={handleSalvar}>
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
              <Form.Item name="vendedorId" label="Vendedor" rules={[{ required: true, message: 'Selecione o vendedor' }]}>
                <Select options={vendedores?.conteudo.map((v) => ({ value: v.id, label: v.comissaoPerfilNome ?? `Vendedor #${v.id}` }))} />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="condicoesPagamentoId" label="Condição de pagamento" rules={[{ required: true, message: 'Selecione a condição' }]}>
                <Select options={condicoes?.conteudo.map((c) => ({ value: c.id, label: c.nome }))} />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={12}>
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
            <Col span={8}>
              <Form.Item name="dataEntrega" label="Data de entrega prevista">
                <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="validade" label="Validade do orçamento">
                <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
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
