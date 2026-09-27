import { useState } from 'react'
import { App, Form, InputNumber, Modal } from 'antd'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useAdicionarComponenteKit, useBuscaProdutosParaSelecao } from '../queries'

interface ComponenteKitModalProps {
  open: boolean
  produtoKitId: number
  onClose: () => void
}

/**
 * Produto não pode ser componente de si mesmo — validado pelo backend
 * (`KIT_COMPONENTE_CIRCULAR`, 422), não replicado aqui pra não duplicar a
 * regra: o `catch` já mostra a mensagem de erro real do backend.
 */
export default function ComponenteKitModal({ open, produtoKitId, onClose }: ComponenteKitModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<{ produtoComponenteId: number; quantidade: number }>()
  const [termoProduto, setTermoProduto] = useState<string | undefined>(undefined)
  const { data: produtos, isLoading: carregandoProdutos } = useBuscaProdutosParaSelecao(termoProduto)
  const adicionar = useAdicionarComponenteKit(produtoKitId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await adicionar.mutateAsync(valores)
        message.success('Componente adicionado ao kit.')
        form.resetFields()
        setTermoProduto(undefined)
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao adicionar componente.')
      }
    })
  }

  return (
    <Modal
      title="Adicionar componente ao kit"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Adicionar"
      cancelText="Cancelar"
      confirmLoading={adicionar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="componenteKitForm" layout="vertical">
        <Form.Item name="produtoComponenteId" label="Produto componente" rules={[{ required: true, message: 'Selecione o componente' }]}>
          <AsyncSelect
            opcoes={produtos}
            carregando={carregandoProdutos}
            termo={termoProduto}
            onTermoChange={setTermoProduto}
            toOption={(p) => ({ value: p.id, label: `${p.codigoInterno} — ${p.nome}` })}
          />
        </Form.Item>
        <Form.Item
          name="quantidade"
          label="Quantidade por kit"
          rules={[{ required: true, message: 'Informe a quantidade' }]}
          tooltip="Quantidade deste componente consumida a cada 1 unidade do kit vendida."
        >
          <InputNumber<number> style={{ width: '100%' }} min={0.000001} precision={3} />
        </Form.Item>
      </Form>
    </Modal>
  )
}
