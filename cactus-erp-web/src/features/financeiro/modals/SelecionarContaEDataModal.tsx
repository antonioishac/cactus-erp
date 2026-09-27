import { App, Form, Input, Modal, Select } from 'antd'
import { useAuth } from '@features/auth/useAuth'
import { useContasBancariasAtivas } from '../queries'
import { TIPO_CONTA_BANCARIA_LABEL } from '../types'

interface SelecionarContaEDataModalProps {
  open: boolean
  title: string
  okText: string
  dataLabel: string
  onClose: () => void
  onSubmit: (contaBancariaId: number, data: string | undefined) => Promise<unknown>
  submitting: boolean
  successMessage: string
}

/** Genérico — cobre Depositar e Descontar Cheque Recebido, os dois `Request` só variam no
 * nome do campo de data (`dataDeposito`/`dataDesconto`), mas o formato de captura na UI é
 * idêntico: conta bancária de destino + data opcional (vazia = hoje). */
export default function SelecionarContaEDataModal({
  open,
  title,
  okText,
  dataLabel,
  onClose,
  onSubmit,
  submitting,
  successMessage,
}: SelecionarContaEDataModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<{ contaBancariaId: number; data?: string }>()
  const { empresaAtivaId } = useAuth()
  const { data: contasBancarias, isLoading: carregandoContas } = useContasBancariasAtivas(empresaAtivaId ?? undefined)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await onSubmit(valores.contaBancariaId, valores.data || undefined)
        message.success(successMessage)
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao executar a ação.')
      }
    })
  }

  return (
    <Modal title={title} open={open} onCancel={onClose} onOk={handleOk} okText={okText} cancelText="Cancelar" confirmLoading={submitting} destroyOnHidden>
      <Form form={form} name="selecionarContaEDataForm" layout="vertical">
        <Form.Item name="contaBancariaId" label="Conta bancária" rules={[{ required: true, message: 'Selecione a conta' }]}>
          <Select
            loading={carregandoContas}
            options={contasBancarias?.map((c) => ({ value: c.id, label: `${c.nome} — ${TIPO_CONTA_BANCARIA_LABEL[c.tipo]}` }))}
          />
        </Form.Item>
        <Form.Item name="data" label={dataLabel} tooltip="Em branco = hoje">
          <Input type="date" />
        </Form.Item>
      </Form>
    </Modal>
  )
}
