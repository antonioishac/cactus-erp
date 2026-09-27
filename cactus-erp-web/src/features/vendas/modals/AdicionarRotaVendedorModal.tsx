import { useState } from 'react'
import { App, Form, InputNumber, Modal } from 'antd'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useAdicionarRotaVendedor, useBuscaClientesParaSelecao } from '../queries'
import type { VendedorRotaRequest } from '../types'

interface AdicionarRotaVendedorModalProps {
  open: boolean
  vendedorId: number
  onClose: () => void
}

export default function AdicionarRotaVendedorModal({ open, vendedorId, onClose }: AdicionarRotaVendedorModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<VendedorRotaRequest>()
  const [termoCliente, setTermoCliente] = useState<string | undefined>(undefined)
  const { data: clientes, isLoading: carregandoClientes } = useBuscaClientesParaSelecao(termoCliente)
  const adicionar = useAdicionarRotaVendedor(vendedorId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await adicionar.mutateAsync(valores)
        message.success('Cliente adicionado à rota.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao adicionar cliente à rota.')
      }
    })
  }

  return (
    <Modal
      title="Adicionar cliente à rota (AFV)"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Adicionar"
      cancelText="Cancelar"
      confirmLoading={adicionar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="adicionarRotaVendedorForm" layout="vertical">
        <Form.Item name="clienteId" label="Cliente" rules={[{ required: true, message: 'Selecione o cliente' }]}>
          <AsyncSelect
            opcoes={clientes}
            carregando={carregandoClientes}
            termo={termoCliente}
            onTermoChange={setTermoCliente}
            toOption={(c) => ({ value: c.id, label: c.nome })}
          />
        </Form.Item>
        <Form.Item name="posicao" label="Posição na rota" extra="Ordem de visita — menor número é visitado primeiro.">
          <InputNumber style={{ width: '100%' }} min={1} />
        </Form.Item>
      </Form>
    </Modal>
  )
}
