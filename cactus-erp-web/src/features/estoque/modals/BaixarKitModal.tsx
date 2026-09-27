import { App, Form, InputNumber, Modal, Select } from 'antd'
import { useAuth } from '@features/auth/useAuth'
import { useBaixarKit, useDepositosAtivos } from '../queries'

interface BaixarKitModalProps {
  open: boolean
  produtoKitId: number
  onClose: () => void
}

/** Gera SAIDA em cascata pra cada componente, quantidade vendida × quantidade do componente (ver `ProdutoKitController.baixar`). */
export default function BaixarKitModal({ open, produtoKitId, onClose }: BaixarKitModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<{ depositoId: number; quantidade: number }>()
  const { empresaAtivaId } = useAuth()
  const { data: depositos, isLoading: carregandoDepositos } = useDepositosAtivos(empresaAtivaId ?? undefined)
  const baixar = useBaixarKit(produtoKitId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await baixar.mutateAsync(valores)
        message.success('Kit baixado — saída gerada em cascata pra cada componente.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao baixar kit.')
      }
    })
  }

  return (
    <Modal
      title="Baixar kit"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Baixar"
      cancelText="Cancelar"
      confirmLoading={baixar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="baixarKitForm" layout="vertical">
        <Form.Item name="depositoId" label="Depósito" rules={[{ required: true, message: 'Selecione o depósito' }]}>
          <Select showSearch loading={carregandoDepositos} optionFilterProp="label" options={depositos?.map((d) => ({ value: d.id, label: d.nome }))} />
        </Form.Item>
        <Form.Item name="quantidade" label="Quantidade de kits vendidos" rules={[{ required: true, message: 'Informe a quantidade' }]}>
          <InputNumber<number> style={{ width: '100%' }} min={0.000001} precision={3} />
        </Form.Item>
      </Form>
    </Modal>
  )
}
