import { App, Form, Input, Modal } from 'antd'
import type { GerarAjusteConciliacaoRequest } from '../types'

interface GerarAjusteModalProps {
  open: boolean
  onClose: () => void
  onSubmit: (request: GerarAjusteConciliacaoRequest) => Promise<unknown>
  submitting: boolean
}

/** Fecha o cenário mais comum de divergência — o banco cobrou uma tarifa (ou creditou
 * rendimento) que nunca foi lançado no ERP. Cria um `MovimentoCaixaBanco` real e já
 * concilia os dois na mesma chamada. `historico` vazio = usa o histórico da própria
 * linha do extrato. */
export default function GerarAjusteModal({ open, onClose, onSubmit, submitting }: GerarAjusteModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<GerarAjusteConciliacaoRequest>()

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await onSubmit({ historico: valores.historico || undefined })
        message.success('Ajuste gerado e item conciliado.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao gerar ajuste.')
      }
    })
  }

  return (
    <Modal
      title="Gerar ajuste a partir do item do extrato"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Gerar ajuste"
      cancelText="Cancelar"
      confirmLoading={submitting}
      destroyOnHidden
    >
      <Form form={form} name="gerarAjusteForm" layout="vertical">
        <Form.Item name="historico" label="Histórico" tooltip="Em branco = usa o histórico da própria linha do extrato" rules={[{ max: 250 }]}>
          <Input.TextArea rows={2} maxLength={250} showCount />
        </Form.Item>
      </Form>
    </Modal>
  )
}
