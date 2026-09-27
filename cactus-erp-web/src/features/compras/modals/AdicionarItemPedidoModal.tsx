import { useState } from 'react'
import { App, Col, Form, InputNumber, Modal, Row, Select } from 'antd'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useAdicionarItemPedido, useBuscaProdutosParaSelecao, useProdutoUnidadesParaSelecao } from '../queries'
import type { PedidoCompraItemRequest } from '../types'

interface AdicionarItemPedidoModalProps {
  open: boolean
  pedidoId: number
  onClose: () => void
}

/**
 * `unidadeCompraId`/`fatorConversaoEstoque` são opcionais — deixar em branco
 * assume unidade de compra = unidade de estoque (fator 1), ver Javadoc de
 * `PedidoCompraItemRequest.java`. Só preencher quando a compra é numa unidade
 * diferente (ex: compra em caixa de 12, estoca em unidade).
 */
export default function AdicionarItemPedidoModal({ open, pedidoId, onClose }: AdicionarItemPedidoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<PedidoCompraItemRequest>()
  const [termo, setTermo] = useState<string | undefined>(undefined)
  const { data: produtos, isLoading: carregandoProdutos } = useBuscaProdutosParaSelecao(termo)
  const { data: unidades } = useProdutoUnidadesParaSelecao()
  const adicionar = useAdicionarItemPedido(pedidoId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await adicionar.mutateAsync(valores)
        message.success('Item adicionado ao pedido.')
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
      title="Adicionar item ao pedido"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Adicionar"
      cancelText="Cancelar"
      confirmLoading={adicionar.isPending}
      width={640}
      destroyOnHidden
    >
      <Form form={form} name="adicionarItemPedidoForm" layout="vertical">
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
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item
              name="unidadeCompraId"
              label="Unidade de compra"
              tooltip="Deixe em branco se a compra usa a mesma unidade do estoque."
            >
              <Select allowClear options={unidades?.map((u) => ({ value: u.id, label: `${u.sigla} — ${u.descricao}` }))} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="fatorConversaoEstoque"
              label="Fator de conversão p/ estoque"
              tooltip="Ex: comprou em caixa de 12, estoca em unidade — fator 12. Deixe em branco pra fator 1."
            >
              <InputNumber style={{ width: '100%' }} min={0.0001} precision={4} />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
