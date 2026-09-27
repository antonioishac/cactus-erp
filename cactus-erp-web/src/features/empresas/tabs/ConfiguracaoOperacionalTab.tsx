import { useEffect } from 'react'
import { App, Button, Checkbox, Col, Flex, Form, Input, InputNumber, Row, Skeleton, Space } from 'antd'
import { SaveOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { hasAuthority } from '@features/auth/authority'
import { useConfiguracaoOperacional, useSalvarConfiguracaoOperacional } from '../queries'
import type { ConfiguracaoOperacionalLojaRequest } from '../types'

interface ConfiguracaoOperacionalTabProps {
  empresaId: number
}

const VALORES_PADRAO: ConfiguracaoOperacionalLojaRequest = {
  permiteMesas: false,
  permiteComandas: false,
  permiteDelivery: false,
  permiteMesaAvulsa: false,
  permiteDescontoAcimaLimite: false,
}

/**
 * Sub-recurso 1:1 com endpoint próprio (`PUT /empresas/{id}/configuracao-operacional`,
 * upsert) — botão "Salvar" independente do formulário principal de Dados
 * Gerais/Endereço/Dados Legais, porque é literalmente uma chamada HTTP
 * diferente. Config empurrada pro PDV no provisionamento do terminal (Fase 6).
 */
export default function ConfiguracaoOperacionalTab({ empresaId }: ConfiguracaoOperacionalTabProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<ConfiguracaoOperacionalLojaRequest>()
  const { data: config, isLoading } = useConfiguracaoOperacional(empresaId)
  const salvar = useSalvarConfiguracaoOperacional(empresaId)

  const { authorities } = useAuth()
  const podeSalvar = hasAuthority(authorities, 'CADASTRO_EMPRESA_EDITAR')

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    form.setFieldsValue(config ?? VALORES_PADRAO)
  }, [config, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  async function handleSalvar() {
    const valores = await form.validateFields()
    try {
      await salvar.mutateAsync(valores)
      message.success('Configuração operacional salva.')
    } catch (erro) {
      const mensagem = erro instanceof Error ? erro.message : 'Falha ao salvar configuração operacional.'
      message.error(mensagem)
    }
  }

  if (isLoading) {
    return <Skeleton active paragraph={{ rows: 6 }} />
  }

  return (
    <Form form={form} layout="vertical" initialValues={VALORES_PADRAO} disabled={!podeSalvar}>
      <Flex justify="flex-end" style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<SaveOutlined />} loading={salvar.isPending} disabled={!podeSalvar} onClick={handleSalvar}>
          Salvar configuração operacional
        </Button>
      </Flex>

      <Space size="large" wrap style={{ marginBottom: 8 }}>
        <Form.Item name="permiteMesas" valuePropName="checked" noStyle>
          <Checkbox>Permite mesas</Checkbox>
        </Form.Item>
        <Form.Item name="permiteComandas" valuePropName="checked" noStyle>
          <Checkbox>Permite comandas</Checkbox>
        </Form.Item>
        <Form.Item name="permiteDelivery" valuePropName="checked" noStyle>
          <Checkbox>Permite delivery</Checkbox>
        </Form.Item>
        <Form.Item name="permiteMesaAvulsa" valuePropName="checked" noStyle>
          <Checkbox>Permite mesa avulsa</Checkbox>
        </Form.Item>
      </Space>

      <Row gutter={12} style={{ marginTop: 16 }}>
        <Col span={6}>
          <Form.Item
            name="modoComanda"
            label="Modo comanda"
            tooltip="Código de 1 caractere específico do terminal PDV — domínio não documentado, mantido como texto livre"
          >
            <Input maxLength={1} />
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={12}>
        <Col span={8}>
          <Form.Item name="limiteDescontoItemPercentual" label="Limite de desconto por item (%)">
            <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="limiteDescontoVendaPercentual" label="Limite de desconto por venda (%)">
            <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="limiteDescontoVendaValor" label="Limite de desconto por venda (R$)">
            <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} />
          </Form.Item>
        </Col>
      </Row>
      <Row gutter={12}>
        <Col span={8}>
          <Form.Item name="limiteAcrescimoItemPercentual" label="Limite de acréscimo por item (%)">
            <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="limiteAcrescimoVendaPercentual" label="Limite de acréscimo por venda (%)">
            <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} />
          </Form.Item>
        </Col>
        <Col span={8} style={{ display: 'flex', alignItems: 'center' }}>
          <Form.Item name="permiteDescontoAcimaLimite" valuePropName="checked" noStyle>
            <Checkbox>Permite desconto acima do limite (com autorização gerencial)</Checkbox>
          </Form.Item>
        </Col>
      </Row>
    </Form>
  )
}
