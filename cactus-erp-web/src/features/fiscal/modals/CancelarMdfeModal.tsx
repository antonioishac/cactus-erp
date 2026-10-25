import { App, Form, Input, Modal } from 'antd'
import { useCancelarMdfe } from '../queries'

interface CancelarMdfeModalProps {
  open: boolean
  mdfeId: number
  onClose: () => void
}

/** Justificativa de 15 a 255 caracteres — regra do evento de cancelamento da SEFAZ, revalidada pelo backend. */
export default function CancelarMdfeModal({ open, mdfeId, onClose }: CancelarMdfeModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<{ justificativa: string }>()
  const cancelar = useCancelarMdfe(mdfeId)

  const handleOk = () => {
    form.validateFields().then(async ({ justificativa }) => {
      try {
        await cancelar.mutateAsync({ justificativa: justificativa.trim() })
        message.success('MDF-e cancelado. Os CT-e foram liberados para um novo manifesto.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao cancelar o MDF-e.')
      }
    })
  }

  return (
    <Modal
      title="Cancelar MDF-e"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Cancelar MDF-e"
      okButtonProps={{ danger: true }}
      cancelText="Voltar"
      confirmLoading={cancelar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="cancelarMdfeForm" layout="vertical">
        <Form.Item
          name="justificativa"
          label="Justificativa"
          rules={[
            { required: true, whitespace: true, message: 'Informe a justificativa' },
            { min: 15, max: 255, message: 'Entre 15 e 255 caracteres' },
          ]}
        >
          <Input.TextArea rows={3} maxLength={255} showCount />
        </Form.Item>
      </Form>
    </Modal>
  )
}
