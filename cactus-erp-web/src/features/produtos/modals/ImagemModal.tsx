import { useEffect } from 'react'
import { Checkbox, Col, Form, Input, InputNumber, Modal, Row, Select } from 'antd'
import type { ProdutoImagemDTO } from '../types'

interface ImagemModalProps {
  open: boolean
  valorInicial?: ProdutoImagemDTO
  onCancel: () => void
  onSalvar: (imagem: ProdutoImagemDTO) => void
}

const VALORES_PADRAO: ProdutoImagemDTO = { url: '', principal: false, situacao: 'ATIVO' }

/** Upload direto ainda não existe (depende do futuro GED) — só imagem já hospedada, referenciada por URL. */
export default function ImagemModal({ open, valorInicial, onCancel, onSalvar }: ImagemModalProps) {
  const [form] = Form.useForm<ProdutoImagemDTO>()

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
      title={valorInicial ? 'Editar imagem' : 'Adicionar imagem'}
      open={open}
      onCancel={onCancel}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      destroyOnHidden
    >
      <Form form={form} layout="vertical" initialValues={VALORES_PADRAO}>
        <Form.Item name="url" label="URL da imagem" rules={[{ required: true, message: 'Informe a URL' }, { max: 500 }]}>
          <Input placeholder="https://..." />
        </Form.Item>
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="ordem" label="Ordem de exibição">
              <InputNumber style={{ width: '100%' }} min={0} />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="principal" valuePropName="checked" label=" " colon={false}>
              <Checkbox>Imagem principal</Checkbox>
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="situacao" label="Situação">
              <Select options={[{ value: 'ATIVO', label: 'Ativo' }, { value: 'INATIVO', label: 'Inativo' }]} />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
