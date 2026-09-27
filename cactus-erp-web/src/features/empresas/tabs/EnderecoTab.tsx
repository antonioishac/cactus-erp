import { Col, Form, Input, Row, Select } from 'antd'
import { UFS } from '@shared/domain/pessoaUtils'

/**
 * Diferente de Cliente/Fornecedor/Colaborador: Empresa tem um endereço único
 * inline no próprio `EmpresaRequest` (`logradouro`/`numero`/...), não uma
 * lista — não há conceito de "vários endereços" pra uma filial, então não
 * reaproveita `@shared/ui/pessoa/EnderecosTab` (aquele é pra listas).
 */
export default function EnderecoTab() {
  return (
    <>
      <Row gutter={12}>
        <Col span={16}>
          <Form.Item name="logradouro" label="Logradouro" rules={[{ max: 150 }]}>
            <Input placeholder="Rua, Avenida..." />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item name="numero" label="Número">
            <Input />
          </Form.Item>
        </Col>
      </Row>
      <Row gutter={12}>
        <Col span={12}>
          <Form.Item name="complemento" label="Complemento">
            <Input />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item name="bairro" label="Bairro">
            <Input />
          </Form.Item>
        </Col>
      </Row>
      <Row gutter={12}>
        <Col span={6}>
          <Form.Item name="cep" label="CEP" rules={[{ pattern: /^\d{8}$/, message: 'CEP deve ter 8 dígitos' }]}>
            <Input placeholder="00000000" maxLength={8} />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item name="municipio" label="Município">
            <Input />
          </Form.Item>
        </Col>
        <Col span={6}>
          <Form.Item name="uf" label="UF">
            <Select showSearch allowClear options={UFS.map((uf) => ({ value: uf, label: uf }))} />
          </Form.Item>
        </Col>
      </Row>
      <Row gutter={12}>
        <Col span={8}>
          <Form.Item name="telefone" label="Telefone" rules={[{ max: 15 }]}>
            <Input placeholder="(00) 00000-0000" />
          </Form.Item>
        </Col>
      </Row>
    </>
  )
}
