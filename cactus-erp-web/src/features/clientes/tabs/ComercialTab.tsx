import { Col, Form, type FormInstance, InputNumber, Radio, Row, Typography } from 'antd'
import type { ClienteRequest } from '../types'

const { Text } = Typography

interface ComercialTabProps {
  form: FormInstance<ClienteRequest>
}

export default function ComercialTab({ form }: ComercialTabProps) {
  const fidelidadeAviso = Form.useWatch('fidelidadeAviso', form)

  return (
    <>
      <Row gutter={12}>
        <Col span={12}>
          <Form.Item name="limiteCredito" label="Limite de crédito (R$)">
            <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} placeholder="0,00" />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item name="taxaDesconto" label="Taxa de desconto padrão (%)">
            <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} />
          </Form.Item>
        </Col>
      </Row>

      <Text strong>Programa de fidelidade</Text>
      <Row gutter={12} style={{ marginTop: 8 }}>
        <Col span={24}>
          <Form.Item name="fidelidadeAviso" label="Avisar por">
            <Radio.Group
              options={[
                { label: 'Quantidade de compras', value: 'QUANTIDADE' },
                { label: 'Valor acumulado', value: 'VALOR' },
              ]}
              optionType="button"
            />
          </Form.Item>
        </Col>
      </Row>
      {fidelidadeAviso === 'QUANTIDADE' && (
        <Form.Item name="fidelidadeQuantidade" label="Quantidade de compras para aviso">
          <InputNumber<number> style={{ width: '100%' }} min={1} />
        </Form.Item>
      )}
      {fidelidadeAviso === 'VALOR' && (
        <Form.Item name="fidelidadeValor" label="Valor acumulado para aviso (R$)">
          <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} />
        </Form.Item>
      )}
    </>
  )
}
