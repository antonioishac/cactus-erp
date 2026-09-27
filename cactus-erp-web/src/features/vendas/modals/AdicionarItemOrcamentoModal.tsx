import { useState } from 'react'
import { App, Col, Form, InputNumber, Modal, Row } from 'antd'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useAdicionarItemOrcamento, useBuscaProdutosParaSelecao } from '../queries'
import type { VendaOrcamentoItemRequest } from '../types'

interface AdicionarItemOrcamentoModalProps {
  open: boolean
  orcamentoId: number
  onClose: () => void
}

export default function AdicionarItemOrcamentoModal({ open, orcamentoId, onClose }: AdicionarItemOrcamentoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<VendaOrcamentoItemRequest>()
  const [termo, setTermo] = useState<string | undefined>(undefined)
  const { data: produtos, isLoading: carregandoProdutos } = useBuscaProdutosParaSelecao(termo)
  const adicionar = useAdicionarItemOrcamento(orcamentoId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await adicionar.mutateAsync(valores)
        message.success('Item adicionado ao orçamento.')
        form.resetFields()
        setTermo(undefined)
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao adicionar item.')
      }
    })
  }

  return (
    <Modal
      title="Adicionar item ao orçamento"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Adicionar"
      cancelText="Cancelar"
      confirmLoading={adicionar.isPending}
      width={560}
      destroyOnHidden
    >
      <Form form={form} name="adicionarItemOrcamentoForm" layout="vertical">
        <Form.Item name="produtoId" label="Produto" rules={[{ required: true, message: 'Selecione o produto' }]}>
          <AsyncSelect
            opcoes={produtos}
            carregando={carregandoProdutos}
            termo={termo}
            onTermoChange={setTermo}
            toOption={(p) => ({ value: p.id, label: `${p.codigoInterno} — ${p.nome}` })}
          />
        </Form.Item>
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="quantidade" label="Quantidade" rules={[{ required: true, message: 'Informe a quantidade' }]}>
              <InputNumber style={{ width: '100%' }} min={0.001} precision={3} />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="valorUnitario" label="Valor unitário (R$)" rules={[{ required: true, message: 'Informe o valor' }]}>
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="taxaDesconto" label="Desconto (%)">
              <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
