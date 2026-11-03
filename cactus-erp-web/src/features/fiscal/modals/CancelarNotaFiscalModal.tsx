import { Alert, App, Form, Input, Modal } from 'antd'
import { useCancelarNotaFiscal } from '../queries'

interface CancelarNotaFiscalModalProps {
  open: boolean
  notaId: number
  onClose: () => void
}

/**
 * Evento 110111 na SEFAZ. Justificativa de 15 a 255 caracteres (revalidada pelo backend). O prazo é decidido pela
 * SEFAZ (24h na regra geral): uma rejeição volta com o cStat e a nota continua autorizada.
 */
export default function CancelarNotaFiscalModal({ open, notaId, onClose }: CancelarNotaFiscalModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<{ justificativa: string }>()
  const cancelar = useCancelarNotaFiscal(notaId)

  const handleOk = () => {
    form.validateFields().then(async ({ justificativa }) => {
      try {
        await cancelar.mutateAsync({ justificativa: justificativa.trim() })
        message.success('NF-e cancelada na SEFAZ.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao cancelar a NF-e.')
      }
    })
  }

  return (
    <Modal
      title="Cancelar NF-e"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Cancelar NF-e"
      okButtonProps={{ danger: true }}
      cancelText="Voltar"
      confirmLoading={cancelar.isPending}
      destroyOnHidden
    >
      <Alert
        type="warning"
        showIcon
        style={{ marginBottom: 16 }}
        title="O cancelamento é definitivo"
        description="A SEFAZ aceita cancelamento, em regra, até 24 horas depois da autorização. Depois disso, o caminho é uma nota de devolução ou de ajuste."
      />
      <Form form={form} name="cancelarNotaFiscalForm" layout="vertical">
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
