import { App, Form, Input, Modal } from 'antd'
import type { DevolverChequeRequest } from '../types'

interface DevolverChequeModalProps {
  open: boolean
  onClose: () => void
  onSubmit: (request: DevolverChequeRequest) => Promise<unknown>
  submitting: boolean
}

/** Genérico — reaproveitado por Cheque Emitido e Cheque Recebido (mesmo
 * `DevolverChequeRequest` no backend, compartilhado entre os 2 controllers). */
export default function DevolverChequeModal({ open, onClose, onSubmit, submitting }: DevolverChequeModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<DevolverChequeRequest>()

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await onSubmit({ ...valores, dataDevolucao: valores.dataDevolucao || undefined })
        message.success('Cheque marcado como devolvido.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao devolver cheque.')
      }
    })
  }

  return (
    <Modal
      title="Devolver cheque"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Devolver"
      okButtonProps={{ danger: true }}
      cancelText="Voltar"
      confirmLoading={submitting}
      destroyOnHidden
    >
      <Form form={form} name="devolverChequeForm" layout="vertical">
        <Form.Item name="dataDevolucao" label="Data da devolução" tooltip="Em branco = hoje">
          <Input type="date" />
        </Form.Item>
        <Form.Item name="motivo" label="Motivo" rules={[{ required: true, message: 'Informe o motivo' }, { max: 250 }]}>
          <Input.TextArea rows={2} maxLength={250} showCount />
        </Form.Item>
      </Form>
    </Modal>
  )
}
