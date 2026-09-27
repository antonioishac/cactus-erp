import { Col, DatePicker, Form, Input, InputNumber, Row } from 'antd'

const { TextArea } = Input

/** Campos próprios de Fornecedor (não existem em Cliente) — `FornecedorRequest.fornecedorDesde`/`prazoEntregaDias`/`observacao`. */
export default function ComprasTab() {
  return (
    <>
      <Row gutter={12}>
        <Col span={12}>
          <Form.Item name="fornecedorDesde" label="Fornecedor desde">
            <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item name="prazoEntregaDias" label="Prazo de entrega (dias)">
            <InputNumber<number> style={{ width: '100%' }} min={0} />
          </Form.Item>
        </Col>
      </Row>
      <Row gutter={12}>
        <Col span={24}>
          <Form.Item name="observacao" label="Observação" rules={[{ max: 250 }]}>
            <TextArea rows={4} maxLength={250} showCount />
          </Form.Item>
        </Col>
      </Row>
    </>
  )
}
