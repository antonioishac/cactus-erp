import { App, Form, Input, Modal } from 'antd'

interface CompensarChequeModalProps {
  open: boolean
  onClose: () => void
  onSubmit: (dataCompensacao: string | undefined) => Promise<unknown>
  submitting: boolean
}

/** Genérico — reaproveitado por Cheque Emitido e Cheque Recebido (mesmo formato
 * `{dataCompensacao?: string}` nos dois `Request` do backend). `dataCompensacao` vazia =
 * backend usa a data de hoje. */
export default function CompensarChequeModal({ open, onClose, onSubmit, submitting }: CompensarChequeModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<{ dataCompensacao?: string }>()

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await onSubmit(valores.dataCompensacao || undefined)
        message.success('Cheque compensado.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao compensar cheque.')
      }
    })
  }

  return (
    <Modal
      title="Compensar cheque"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Compensar"
      cancelText="Cancelar"
      confirmLoading={submitting}
      destroyOnHidden
    >
      <Form form={form} name="compensarChequeForm" layout="vertical">
        <Form.Item name="dataCompensacao" label="Data da compensação" tooltip="Em branco = hoje">
          <Input type="date" />
        </Form.Item>
      </Form>
    </Modal>
  )
}
