import { useEffect } from 'react'
import { Checkbox, Form, Input, Modal, Select } from 'antd'
import { TIPO_TELEFONE_LABEL } from '@shared/domain/pessoaUtils'
import type { TelefoneDTO } from '@shared/domain/pessoa'

interface TelefoneModalProps {
  open: boolean
  valorInicial?: TelefoneDTO
  onCancel: () => void
  onSalvar: (telefone: TelefoneDTO) => void
}

const VALORES_PADRAO: Partial<TelefoneDTO> = { tipoTelefone: 'CELULAR', principal: false }

export default function TelefoneModal({ open, valorInicial, onCancel, onSalvar }: TelefoneModalProps) {
  const [form] = Form.useForm<TelefoneDTO>()

  useEffect(() => {
    if (open) {
      form.setFieldsValue((valorInicial ?? VALORES_PADRAO) as TelefoneDTO)
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
      title={valorInicial ? 'Editar telefone' : 'Adicionar telefone'}
      open={open}
      onCancel={onCancel}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      destroyOnHidden
    >
      <Form form={form} layout="vertical" initialValues={VALORES_PADRAO}>
        <Form.Item name="tipoTelefone" label="Tipo" rules={[{ required: true }]}>
          <Select
            options={Object.entries(TIPO_TELEFONE_LABEL).map(([value, label]) => ({ value, label }))}
          />
        </Form.Item>
        <Form.Item name="numero" label="Número" rules={[{ required: true, message: 'Informe o número' }, { max: 15 }]}>
          <Input placeholder="(00) 00000-0000" />
        </Form.Item>
        <Form.Item name="principal" valuePropName="checked">
          <Checkbox>Principal</Checkbox>
        </Form.Item>
      </Form>
    </Modal>
  )
}
