import { useState } from 'react'
import { App, Col, Form, Input, InputNumber, Modal, Row, Select } from 'antd'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useAdicionarObjetivoComissaoPerfil, useBuscaProdutosParaSelecao } from '../queries'
import { TIPO_PAGAMENTO_VENDA_LABEL } from '../types'
import type { ComissaoObjetivoRequest } from '../types'

interface AdicionarObjetivoComissaoModalProps {
  open: boolean
  comissaoPerfilId: number
  onClose: () => void
}

/** Comissão específica por produto dentro de um Perfil — sobrepõe a comissão padrão do `Vendedor` quando o produto vendido bate com este objetivo. */
export default function AdicionarObjetivoComissaoModal({ open, comissaoPerfilId, onClose }: AdicionarObjetivoComissaoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<ComissaoObjetivoRequest>()
  const [termoProduto, setTermoProduto] = useState<string | undefined>(undefined)
  const { data: produtos, isLoading: carregandoProdutos } = useBuscaProdutosParaSelecao(termoProduto)
  const adicionar = useAdicionarObjetivoComissaoPerfil(comissaoPerfilId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await adicionar.mutateAsync(valores)
        message.success('Objetivo de comissão adicionado.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao adicionar objetivo de comissão.')
      }
    })
  }

  return (
    <Modal
      title="Adicionar objetivo de comissão"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Adicionar"
      cancelText="Cancelar"
      confirmLoading={adicionar.isPending}
      destroyOnHidden
      width={640}
    >
      <Form form={form} name="adicionarObjetivoComissaoForm" layout="vertical">
        <Row gutter={12}>
          <Col span={16}>
            <Form.Item name="produtoId" label="Produto" rules={[{ required: true, message: 'Selecione o produto' }]}>
              <AsyncSelect
                opcoes={produtos}
                carregando={carregandoProdutos}
                termo={termoProduto}
                onTermoChange={setTermoProduto}
                toOption={(p) => ({ value: p.id, label: `${p.codigoInterno} — ${p.nome}` })}
              />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="codigo" label="Código" rules={[{ max: 3 }]}>
              <Input maxLength={3} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={24}>
            <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 100 }]}>
              <Input maxLength={100} />
            </Form.Item>
          </Col>
        </Row>
        <Form.Item name="descricao" label="Descrição" rules={[{ max: 250 }]}>
          <Input.TextArea rows={2} maxLength={250} showCount />
        </Form.Item>
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="formaPagamento" label="Forma de pagamento">
              <Select allowClear options={Object.entries(TIPO_PAGAMENTO_VENDA_LABEL).map(([value, label]) => ({ value, label }))} />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="taxaPagamento" label="Taxa de comissão (%)">
              <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="valorPagamento" label="Valor fixo de comissão (R$)">
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="valorMeta" label="Valor da meta (R$)">
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="quantidade" label="Quantidade da meta">
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
