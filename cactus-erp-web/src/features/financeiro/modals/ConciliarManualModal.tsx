import { App, Form, Modal, Select } from 'antd'
import dayjs from 'dayjs'
import type { MovimentoCaixaBancoResponse } from '../types'

interface ConciliarManualModalProps {
  open: boolean
  onClose: () => void
  onSubmit: (movimentoCaixaBancoId: number) => Promise<unknown>
  submitting: boolean
  movimentosPendentes: MovimentoCaixaBancoResponse[]
}

/** Override do operador — liga um item de extrato a um movimento interno específico, sem
 * revalidar valor/data (usado quando o automático não resolveu por ambiguidade). */
export default function ConciliarManualModal({ open, onClose, onSubmit, submitting, movimentosPendentes }: ConciliarManualModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<{ movimentoCaixaBancoId: number }>()

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await onSubmit(valores.movimentoCaixaBancoId)
        message.success('Item conciliado.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao conciliar manualmente.')
      }
    })
  }

  return (
    <Modal
      title="Conciliar manualmente"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Conciliar"
      cancelText="Cancelar"
      confirmLoading={submitting}
      destroyOnHidden
    >
      <Form form={form} name="conciliarManualForm" layout="vertical">
        <Form.Item
          name="movimentoCaixaBancoId"
          label="Movimento interno correspondente"
          rules={[{ required: true, message: 'Selecione o movimento' }]}
        >
          <Select
            options={movimentosPendentes.map((m) => ({
              value: m.id,
              label: `${dayjs(m.dataMovimento).format('DD/MM/YYYY')} — ${m.historico} — R$ ${m.valor.toFixed(2)}`,
            }))}
            notFoundContent="Nenhum movimento interno pendente"
          />
        </Form.Item>
      </Form>
    </Modal>
  )
}
