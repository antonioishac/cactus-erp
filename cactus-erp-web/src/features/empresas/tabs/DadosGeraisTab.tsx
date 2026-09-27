import { Col, Form, Input, Row, Select } from 'antd'

const OPCOES_TIPO_ESTABELECIMENTO = [
  { value: 'VAREJO', label: 'Varejo' },
  { value: 'RESTAURANTE', label: 'Restaurante' },
  { value: 'BAR', label: 'Bar' },
  { value: 'SUPERMERCADO', label: 'Supermercado' },
  { value: 'AUTOCENTER', label: 'Autocenter' },
  { value: 'OUTRO', label: 'Outro' },
]

const OPCOES_TIPO_EMPRESA = [
  { value: 'MATRIZ', label: 'Matriz' },
  { value: 'FILIAL', label: 'Filial' },
]

const OPCOES_CRT = [
  { value: '1', label: '1 — Simples Nacional' },
  { value: '2', label: '2 — Simples Nacional (excesso de sublimite)' },
  { value: '3', label: '3 — Regime Normal' },
]

export default function DadosGeraisTab() {
  return (
    <>
      <Row gutter={12}>
        <Col span={16}>
          <Form.Item name="razaoSocial" label="Razão social" rules={[{ required: true, message: 'Informe a razão social' }, { max: 150 }]}>
            <Input />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="cnpj" label="CNPJ" rules={[{ pattern: /^\d{14}$/, message: 'CNPJ deve ter 14 dígitos' }]}>
            <Input placeholder="Somente números" maxLength={14} />
          </Form.Item>
        </Col>
      </Row>
      <Row gutter={12}>
        <Col span={16}>
          <Form.Item name="nomeFantasia" label="Nome fantasia" rules={[{ max: 150 }]}>
            <Input />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="inscricaoEstadual" label="Inscrição estadual">
            <Input maxLength={20} />
          </Form.Item>
        </Col>
      </Row>
      <Row gutter={12}>
        <Col span={8}>
          <Form.Item name="tipo" label="Matriz/Filial">
            <Select allowClear options={OPCOES_TIPO_EMPRESA} />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="tipoEstabelecimento" label="Tipo de estabelecimento" rules={[{ required: true, message: 'Informe o tipo de estabelecimento' }]}>
            <Select options={OPCOES_TIPO_ESTABELECIMENTO} />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item
            name="crt"
            label="CRT atual"
            tooltip="Regime tributário vigente — o histórico completo por ano-calendário fica na aba Regime Tributário"
          >
            <Select allowClear options={OPCOES_CRT} />
          </Form.Item>
        </Col>
      </Row>
    </>
  )
}
