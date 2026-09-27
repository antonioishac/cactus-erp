import { App, Form, Modal, Select } from 'antd'
import { useAuth } from '@features/auth/useAuth'
import { useGerarPedidoDeCotacao } from '../queries'
import { TIPO_PEDIDO_LABEL } from '../types'
import type { TipoPedidoCompra } from '../types'

interface GerarPedidoModalProps {
  open: boolean
  cotacaoId: number
  fornecedorCotacaoId: number | undefined
  onClose: (pedidoId?: number) => void
}

interface FormValues {
  tipo: TipoPedidoCompra
}

const VALORES_PADRAO: Partial<FormValues> = { tipo: 'NORMAL' }

/** `colaboradorId` do pedido gerado é herdado do solicitante da requisição de origem — não pedido aqui (ver `GerarPedidoDeCotacaoRequest`). */
export default function GerarPedidoModal({ open, cotacaoId, fornecedorCotacaoId, onClose }: GerarPedidoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<FormValues>()
  const { empresaAtivaId } = useAuth()
  const gerar = useGerarPedidoDeCotacao(cotacaoId)

  const handleOk = () => {
    if (fornecedorCotacaoId === undefined || empresaAtivaId === null) return
    form.validateFields().then(async (valores) => {
      try {
        const pedido = await gerar.mutateAsync({
          fornecedorCotacaoId,
          request: { empresaId: empresaAtivaId, tipo: valores.tipo },
        })
        message.success('Pedido de compra gerado com sucesso.')
        onClose(pedido.id)
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao gerar pedido.')
      }
    })
  }

  return (
    <Modal
      title="Gerar pedido de compra"
      open={open}
      onCancel={() => onClose()}
      onOk={handleOk}
      okText="Gerar Pedido"
      cancelText="Cancelar"
      confirmLoading={gerar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="gerarPedidoForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Form.Item name="tipo" label="Tipo do pedido" rules={[{ required: true }]}>
          <Select options={Object.entries(TIPO_PEDIDO_LABEL).map(([value, label]) => ({ value, label }))} />
        </Form.Item>
      </Form>
    </Modal>
  )
}
