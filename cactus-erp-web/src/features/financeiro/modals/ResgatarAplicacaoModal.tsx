import { App, Form, Input, InputNumber, Modal } from 'antd'
import type { ResgatarAplicacaoRequest } from '../types'

interface ResgatarAplicacaoModalProps {
  open: boolean
  onClose: () => void
  onSubmit: (request: ResgatarAplicacaoRequest) => Promise<unknown>
  submitting: boolean
  valorAplicado: number
}

/** `valorResgatado` pode ser maior ou menor que `valorAplicado` (rendimento positivo ou
 * negativo — fundo/Tesouro Direto marcado a mercado pode perder valor). */
export default function ResgatarAplicacaoModal({ open, onClose, onSubmit, submitting, valorAplicado }: ResgatarAplicacaoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<ResgatarAplicacaoRequest>()

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await onSubmit(valores)
        message.success('Aplicação resgatada.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao resgatar aplicação.')
      }
    })
  }

  return (
    <Modal
      title="Resgatar aplicação"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Resgatar"
      cancelText="Cancelar"
      confirmLoading={submitting}
      destroyOnHidden
    >
      <Form form={form} name="resgatarAplicacaoForm" layout="vertical">
        <Form.Item name="dataResgate" label="Data do resgate">
          <Input type="date" />
        </Form.Item>
        <Form.Item
          name="valorResgatado"
          label="Valor resgatado (R$)"
          rules={[{ required: true, message: 'Informe o valor' }]}
          tooltip={`Valor aplicado: R$ ${valorAplicado.toFixed(2)} — pode ser maior ou menor (rendimento positivo ou negativo)`}
        >
          <InputNumber<number> style={{ width: '100%' }} min={0.01} precision={2} decimalSeparator="," />
        </Form.Item>
      </Form>
    </Modal>
  )
}
