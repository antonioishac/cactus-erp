import { Form, InputNumber, Modal, Select } from 'antd'
import type { EmpresaRegimeTributarioRequest } from '../types'

interface RegimeTributarioModalProps {
  open: boolean
  salvando: boolean
  onCancel: () => void
  onSalvar: (request: EmpresaRegimeTributarioRequest) => void
}

const OPCOES_CRT = [
  { value: '1', label: '1 — Simples Nacional' },
  { value: '2', label: '2 — Simples Nacional (excesso de sublimite)' },
  { value: '3', label: '3 — Regime Normal' },
]

/**
 * Histórico append-only (ver `RegimeTributarioTab.tsx`) — este modal só
 * ADICIONA um ano novo, nunca edita um existente (o backend rejeita com 422
 * se o ano já tiver registro, é assim que a auditoria fica íntegra).
 */
export default function RegimeTributarioModal({ open, salvando, onCancel, onSalvar }: RegimeTributarioModalProps) {
  const [form] = Form.useForm<EmpresaRegimeTributarioRequest>()

  const handleOk = () => {
    form.validateFields().then((valores) => {
      onSalvar(valores)
    })
  }

  return (
    <Modal
      title="Registrar regime tributário do ano"
      open={open}
      onCancel={onCancel}
      onOk={handleOk}
      okText="Registrar"
      okButtonProps={{ loading: salvando }}
      cancelText="Cancelar"
      destroyOnHidden
      afterClose={() => form.resetFields()}
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="anoCalendario"
          label="Ano-calendário"
          rules={[{ required: true, message: 'Informe o ano' }]}
        >
          <InputNumber<number> style={{ width: '100%' }} min={2000} max={2100} />
        </Form.Item>
        <Form.Item name="crt" label="CRT" rules={[{ required: true, message: 'Selecione o CRT' }]}>
          <Select options={OPCOES_CRT} />
        </Form.Item>
      </Form>
    </Modal>
  )
}
