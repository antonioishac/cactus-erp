import { App, Form, Input, Modal } from 'antd'
import { useCancelarPedido } from '../queries'

interface Props {
  open: boolean
  pedidoId: number
  onClose: () => void
}

export default function CancelarPedidoModal({ open, pedidoId, onClose }: Props) {
  const { message } = App.useApp()
  const [form] = Form.useForm<{ motivo: string }>()
  const cancelar = useCancelarPedido(pedidoId)

  const handleOk = () => {
    form.validateFields().then(async ({ motivo }) => {
      try {
        await cancelar.mutateAsync({ motivo })
        message.success('Pedido cancelado.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao cancelar pedido.')
      }
    })
  }

  return (
    <Modal
      title="Cancelar pedido"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Cancelar pedido"
      okButtonProps={{ danger: true }}
      cancelText="Voltar"
      confirmLoading={cancelar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="cancelarPedidoForm" layout="vertical">
        <Form.Item name="motivo" label="Motivo" rules={[{ required: true, message: 'Informe o motivo' }, { max: 250 }]}>
          <Input.TextArea rows={3} maxLength={250} showCount autoFocus />
        </Form.Item>
      </Form>
    </Modal>
  )
}
