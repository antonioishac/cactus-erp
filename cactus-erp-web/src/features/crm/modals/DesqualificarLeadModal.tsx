import { App, Form, Input, Modal } from 'antd'
import { useDesqualificarLead } from '../queries'
import type { DesqualificarLeadRequest } from '../types'

interface DesqualificarLeadModalProps {
  open: boolean
  leadId: number
  onClose: () => void
}

export default function DesqualificarLeadModal({ open, leadId, onClose }: DesqualificarLeadModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<DesqualificarLeadRequest>()
  const desqualificar = useDesqualificarLead(leadId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await desqualificar.mutateAsync(valores)
        message.success('Lead desqualificado.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao desqualificar lead.')
      }
    })
  }

  return (
    <Modal
      title="Desqualificar lead"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Desqualificar"
      okButtonProps={{ danger: true }}
      cancelText="Cancelar"
      confirmLoading={desqualificar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="desqualificarLeadForm" layout="vertical">
        <Form.Item name="motivo" label="Motivo" rules={[{ required: true, message: 'Informe o motivo' }, { max: 250 }]}>
          <Input.TextArea rows={3} maxLength={250} showCount />
        </Form.Item>
      </Form>
    </Modal>
  )
}
