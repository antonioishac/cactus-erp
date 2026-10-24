import { App, Form, Input, Modal } from 'antd'

interface MotivoAcaoModalProps {
  open: boolean
  title: string
  okText: string
  onClose: () => void
  onSubmit: (motivo: string) => Promise<unknown>
  submitting: boolean
  successMessage: string
}

/** Modal "ação + motivo obrigatório" — mesmo padrão de `financeiro/modals/MotivoAcaoModal`,
 * usado aqui só pra Estornar Lançamento (`EstornarLancamentoRequest.motivo`). */
export default function MotivoAcaoModal({ open, title, okText, onClose, onSubmit, submitting, successMessage }: MotivoAcaoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<{ motivo: string }>()

  const handleOk = () => {
    form.validateFields().then(async ({ motivo }) => {
      try {
        await onSubmit(motivo)
        message.success(successMessage)
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao executar a ação.')
      }
    })
  }

  return (
    <Modal
      title={title}
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText={okText}
      okButtonProps={{ danger: true }}
      cancelText="Voltar"
      confirmLoading={submitting}
      destroyOnHidden
    >
      <Form form={form} name="motivoAcaoForm" layout="vertical">
        <Form.Item name="motivo" label="Motivo" rules={[{ required: true, message: 'Informe o motivo' }, { max: 250 }]}>
          <Input.TextArea rows={3} maxLength={250} showCount autoFocus />
        </Form.Item>
      </Form>
    </Modal>
  )
}
