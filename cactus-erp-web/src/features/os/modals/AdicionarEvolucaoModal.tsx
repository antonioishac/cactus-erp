import { App, Checkbox, Form, Input, Modal } from 'antd'
import { useAdicionarEvolucaoOs } from '../queries'
import type { OsEvolucaoRequest } from '../types'

interface AdicionarEvolucaoModalProps {
  open: boolean
  osAberturaId: number
  onClose: () => void
}

const VALORES_PADRAO: Partial<OsEvolucaoRequest> = { enviarEmail: false }

/** Append-only — sem edição/remoção depois de registrado (mesmo critério de `EstoqueMovimento`/`DocumentoVersao`). */
export default function AdicionarEvolucaoModal({ open, osAberturaId, onClose }: AdicionarEvolucaoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<OsEvolucaoRequest>()
  const adicionar = useAdicionarEvolucaoOs(osAberturaId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await adicionar.mutateAsync(valores)
        message.success('Evolução registrada.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao registrar evolução.')
      }
    })
  }

  return (
    <Modal
      title="Registrar evolução"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Registrar"
      cancelText="Cancelar"
      confirmLoading={adicionar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="adicionarEvolucaoForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Form.Item name="observacao" label="Observação" rules={[{ max: 500 }]}>
          <Input.TextArea rows={3} maxLength={500} showCount />
        </Form.Item>
        <Form.Item name="enviarEmail" valuePropName="checked">
          <Checkbox>Enviar por e-mail ao cliente</Checkbox>
        </Form.Item>
      </Form>
    </Modal>
  )
}
