import { useEffect } from 'react'
import { Checkbox, Form, Input, Modal, Select } from 'antd'
import { TIPO_CONTATO_LABEL } from '@shared/domain/pessoaUtils'
import type { ContatoDTO } from '@shared/domain/pessoa'

interface ContatoModalProps {
  open: boolean
  valorInicial?: ContatoDTO
  onCancel: () => void
  onSalvar: (contato: ContatoDTO) => void
}

const VALORES_PADRAO: Partial<ContatoDTO> = { tipoContato: 'EMAIL', principal: false }

export default function ContatoModal({ open, valorInicial, onCancel, onSalvar }: ContatoModalProps) {
  const [form] = Form.useForm<ContatoDTO>()

  useEffect(() => {
    if (open) {
      form.setFieldsValue((valorInicial ?? VALORES_PADRAO) as ContatoDTO)
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
      title={valorInicial ? 'Editar contato' : 'Adicionar contato'}
      open={open}
      onCancel={onCancel}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      destroyOnHidden
    >
      <Form form={form} layout="vertical" initialValues={VALORES_PADRAO}>
        <Form.Item name="tipoContato" label="Tipo" rules={[{ required: true }]}>
          <Select
            options={Object.entries(TIPO_CONTATO_LABEL).map(([value, label]) => ({ value, label }))}
          />
        </Form.Item>
        <Form.Item name="valor" label="Valor" rules={[{ required: true, message: 'Informe o valor do contato' }, { max: 250 }]}>
          <Input placeholder="email@exemplo.com ou nome do responsável" />
        </Form.Item>
        <Form.Item name="principal" valuePropName="checked">
          <Checkbox>Principal</Checkbox>
        </Form.Item>
      </Form>
    </Modal>
  )
}
