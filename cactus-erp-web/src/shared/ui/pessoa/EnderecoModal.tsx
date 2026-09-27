import { useEffect } from 'react'
import { Checkbox, Col, Form, Input, Modal, Row, Select } from 'antd'
import { UFS } from '@shared/domain/pessoaUtils'
import type { EnderecoDTO } from '@shared/domain/pessoa'

interface EnderecoModalProps {
  open: boolean
  valorInicial?: EnderecoDTO
  onCancel: () => void
  onSalvar: (endereco: EnderecoDTO) => void
}

const VALORES_PADRAO: EnderecoDTO = {
  principal: false,
  entrega: false,
  cobranca: false,
  correspondencia: false,
}

/** Endereço tem campos e flags demais pra editar inline numa tabela — modal
 *  dedicado, mesmo padrão usado pra Contato/Telefone (consistência entre as 3
 *  listas filhas de qualquer especialização de Pessoa). */
export default function EnderecoModal({ open, valorInicial, onCancel, onSalvar }: EnderecoModalProps) {
  const [form] = Form.useForm<EnderecoDTO>()

  useEffect(() => {
    if (open) {
      form.setFieldsValue(valorInicial ?? VALORES_PADRAO)
    }
  }, [open, valorInicial, form])

  const handleOk = () => {
    form.validateFields().then((valores) => {
      onSalvar(valores)
      form.resetFields()
    })
  }

  return (
    <Modal
      title={valorInicial ? 'Editar endereço' : 'Adicionar endereço'}
      open={open}
      onCancel={onCancel}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      destroyOnHidden
    >
      <Form form={form} layout="vertical" initialValues={VALORES_PADRAO}>
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
          <Col span={8}>
            <Form.Item
              name="cep"
              label="CEP"
              rules={[{ pattern: /^\d{8}$/, message: 'CEP deve ter 8 dígitos' }]}
            >
              <Input placeholder="00000000" maxLength={8} />
            </Form.Item>
          </Col>
          <Col span={10}>
            <Form.Item name="municipio" label="Município">
              <Input />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="uf" label="UF">
              <Select
                showSearch
                options={UFS.map((uf) => ({ value: uf, label: uf }))}
                allowClear
              />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={[12, 0]}>
          <Col span={6}>
            <Form.Item name="principal" valuePropName="checked" style={{ marginBottom: 0 }}>
              <Checkbox>Principal</Checkbox>
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="entrega" valuePropName="checked" style={{ marginBottom: 0 }}>
              <Checkbox>Entrega</Checkbox>
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="cobranca" valuePropName="checked" style={{ marginBottom: 0 }}>
              <Checkbox>Cobrança</Checkbox>
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="correspondencia" valuePropName="checked" style={{ marginBottom: 0 }}>
              <Checkbox>Correspondência</Checkbox>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
