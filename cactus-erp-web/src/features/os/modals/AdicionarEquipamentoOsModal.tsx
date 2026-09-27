import { App, Form, Input, Modal, Select } from 'antd'
import { useAdicionarEquipamentoOs, useTodosOsEquipamentos } from '../queries'
import type { OsAberturaEquipamentoRequest } from '../types'

interface AdicionarEquipamentoOsModalProps {
  open: boolean
  osAberturaId: number
  onClose: () => void
}

export default function AdicionarEquipamentoOsModal({ open, osAberturaId, onClose }: AdicionarEquipamentoOsModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<OsAberturaEquipamentoRequest>()
  const { data: tiposEquipamento } = useTodosOsEquipamentos()
  const adicionar = useAdicionarEquipamentoOs(osAberturaId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await adicionar.mutateAsync(valores)
        message.success('Equipamento adicionado.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao adicionar equipamento.')
      }
    })
  }

  return (
    <Modal
      title="Adicionar equipamento à OS"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Adicionar"
      cancelText="Cancelar"
      confirmLoading={adicionar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="adicionarEquipamentoOsForm" layout="vertical">
        <Form.Item name="osEquipamentoId" label="Tipo de equipamento" rules={[{ required: true, message: 'Selecione o tipo de equipamento' }]}>
          <Select options={tiposEquipamento?.map((t) => ({ value: t.id, label: t.nome }))} />
        </Form.Item>
        <Form.Item name="numeroSerie" label="Número de série" rules={[{ max: 50 }]}>
          <Input maxLength={50} />
        </Form.Item>
        <Form.Item
          name="tipoCobertura"
          label="Tipo de cobertura"
          rules={[{ max: 1 }]}
          extra="Domínio de valores não confirmado contra o backend — campo livre de 1 caractere."
        >
          <Input maxLength={1} />
        </Form.Item>
      </Form>
    </Modal>
  )
}
