import { Col, Form, Input, InputNumber, Row, Select } from 'antd'

const { TextArea } = Input

const OPCOES_VEICULO = [
  { value: 'CARRO', label: 'Carro' },
  { value: 'MOTO', label: 'Moto' },
  { value: 'BICICLETA', label: 'Bicicleta' },
  { value: 'APLICATIVO', label: 'Aplicativo' },
]

/**
 * Comissão (à vista/a prazo) vale pra qualquer vertical com vendedor
 * comissionado (loja, garçom...) — `entregadorVeiculo` só é relevante quando
 * o colaborador também atua como entregador (delivery), por isso fica opcional
 * e sem depender de nenhum outro campo (não há "nível de autorização"
 * modelado no cadastro-base ainda — isso é RBAC, vive no seguranca-service).
 */
export default function ComissaoTab() {
  return (
    <>
      <Row gutter={12}>
        <Col span={12}>
          <Form.Item name="comissaoVista" label="Comissão à vista (%)">
            <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item name="comissaoPrazo" label="Comissão a prazo (%)">
            <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} />
          </Form.Item>
        </Col>
      </Row>
      <Row gutter={12}>
        <Col span={12}>
          <Form.Item name="entregadorVeiculo" label="Veículo de entrega (se for entregador)">
            <Select allowClear options={OPCOES_VEICULO} />
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
