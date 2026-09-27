import { useState } from 'react'
import { App, Col, Form, Input, Modal, Row } from 'antd'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useAdicionarFornecedorCotacao, useBuscaFornecedoresParaSelecao } from '../queries'

interface AdicionarFornecedorCotacaoModalProps {
  open: boolean
  cotacaoId: number
  onClose: () => void
}

interface FormValues {
  fornecedorId: number
  prazoEntrega?: string
  condicoesPagamento?: string
}

export default function AdicionarFornecedorCotacaoModal({ open, cotacaoId, onClose }: AdicionarFornecedorCotacaoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<FormValues>()
  const [termo, setTermo] = useState<string | undefined>(undefined)
  const { data: fornecedores, isLoading: carregando } = useBuscaFornecedoresParaSelecao(termo)
  const adicionar = useAdicionarFornecedorCotacao(cotacaoId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await adicionar.mutateAsync(valores)
        message.success('Fornecedor adicionado à cotação.')
        form.resetFields()
        setTermo(undefined)
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao adicionar fornecedor.')
      }
    })
  }

  return (
    <Modal
      title="Adicionar fornecedor concorrente"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Adicionar"
      cancelText="Cancelar"
      confirmLoading={adicionar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="adicionarFornecedorCotacaoForm" layout="vertical">
        <Form.Item name="fornecedorId" label="Fornecedor" rules={[{ required: true, message: 'Selecione o fornecedor' }]}>
          <AsyncSelect
            opcoes={fornecedores}
            carregando={carregando}
            termo={termo}
            onTermoChange={setTermo}
            toOption={(f) => ({ value: f.id, label: f.nome })}
          />
        </Form.Item>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="prazoEntrega" label="Prazo de entrega" rules={[{ max: 50 }]}>
              <Input maxLength={50} placeholder="Ex: 5 dias úteis" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="condicoesPagamento" label="Condições de pagamento" rules={[{ max: 100 }]}>
              <Input maxLength={100} placeholder="Ex: 30/60/90 dias" />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
