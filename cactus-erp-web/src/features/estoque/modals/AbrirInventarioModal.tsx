import { App, Form, Modal, Select } from 'antd'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '@features/auth/useAuth'
import { useAbrirInventario } from '../queries'
import { TIPO_INVENTARIO_LABEL } from '../types'
import type { TipoInventario } from '../types'

interface AbrirInventarioModalProps {
  open: boolean
  depositoId: number
  onClose: () => void
}

const VALORES_PADRAO: { tipo: TipoInventario } = { tipo: 'POR_SALDO' }

/** Tira um snapshot do saldo atual de todo produto com estoque no depósito — cada um vira um item a contar (ver `InventarioFisicoController.abrir`). */
export default function AbrirInventarioModal({ open, depositoId, onClose }: AbrirInventarioModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<{ tipo: TipoInventario }>()
  const { empresaAtivaId } = useAuth()
  const abrir = useAbrirInventario()
  const navigate = useNavigate()

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        const inventario = await abrir.mutateAsync({ empresaId: empresaAtivaId ?? 0, depositoId, ...valores })
        message.success('Inventário aberto.')
        form.resetFields()
        onClose()
        navigate(`/estoque/inventarios/${inventario.id}`)
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao abrir inventário.')
      }
    })
  }

  return (
    <Modal
      title="Abrir inventário"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Abrir"
      cancelText="Cancelar"
      confirmLoading={abrir.isPending}
      destroyOnHidden
    >
      <Form form={form} name="abrirInventarioForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Form.Item
          name="tipo"
          label="Tipo de contagem"
          rules={[{ required: true }]}
          tooltip="Cega força uma contagem real, sem o contador ver o saldo esperado. Por saldo é mais rápida, mas mais sujeita a viés."
        >
          <Select options={Object.entries(TIPO_INVENTARIO_LABEL).map(([value, label]) => ({ value, label }))} />
        </Form.Item>
      </Form>
    </Modal>
  )
}
