import { Col, Form, Input, Row } from 'antd'

const { TextArea } = Input

/** Campo próprio de Transportadora (não existe em Fornecedor) — `TransportadoraRequest.rntrc`, obrigatório em CT-e (Fase 11). */
export default function DadosOperacionaisTab() {
  return (
    <>
      <Row gutter={12}>
        <Col span={12}>
          <Form.Item
            name="rntrc"
            label="RNTRC"
            tooltip="Registro Nacional de Transportadores Rodoviários de Cargas — usado na emissão de CT-e."
            rules={[{ max: 20 }]}
          >
            <Input maxLength={20} />
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
