import { Alert, Col, Form, InputNumber, Row } from 'antd'

/**
 * `estoqueMinimo`/`estoqueMaximo` aqui são só política sugerida — o saldo real
 * por depósito mora inteiramente no `controle-estoque-service` (sem tela ainda),
 * nunca um campo flat neste cadastro (ver ANÁLISE: ESTOQUE NÍVEL ENTERPRISE, CLAUDE.md raiz).
 */
export default function PrecosEstoqueTab() {
  return (
    <>
      <Row gutter={12}>
        <Col span={8}>
          <Form.Item name="valorCusto" label="Valor de custo (R$)" tooltip="Custo médio ponderado real vem do estoque — este é só um valor de referência do cadastro.">
            <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="valorCompra" label="Valor de compra (R$)">
            <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="valorVenda" label="Valor de venda (R$)">
            <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
          </Form.Item>
        </Col>
      </Row>

      <Alert
        type="info"
        showIcon
        style={{ marginBottom: 16 }}
        title="Estoque mínimo/máximo aqui é só uma política sugerida de cadastro."
        description="O saldo real por depósito é controlado pelo módulo de Estoque — este cadastro não guarda quantidade em estoque."
      />

      <Row gutter={12}>
        <Col span={8}>
          <Form.Item name="estoqueMinimo" label="Estoque mínimo (sugerido)">
            <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="estoqueMaximo" label="Estoque máximo (sugerido)">
            <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
          </Form.Item>
        </Col>
      </Row>
    </>
  )
}
