import { useState } from 'react'
import { App, Form, Input, InputNumber, Modal, Select } from 'antd'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useAdicionarItemOs, useBuscaProdutosParaSelecao } from '../queries'
import { TIPO_ITEM_OS_LABEL } from '../types'
import type { OsProdutoServicoRequest, TipoItemOs } from '../types'

interface AdicionarItemOsModalProps {
  open: boolean
  osAberturaId: number
  onClose: () => void
}

type FormValues = OsProdutoServicoRequest

const VALORES_PADRAO: Partial<FormValues> = { tipo: 'SERVICO' }

/** `produtoId` obrigatório quando `tipo=PRODUTO` (peça) — validado no backend, `ITEM_PRODUTO_SEM_PRODUTO_ID` se ausente. */
export default function AdicionarItemOsModal({ open, osAberturaId, onClose }: AdicionarItemOsModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<FormValues>()
  const tipo = Form.useWatch('tipo', form) as TipoItemOs | undefined
  const [termoProduto, setTermoProduto] = useState<string | undefined>(undefined)
  const { data: produtos, isLoading: carregandoProdutos } = useBuscaProdutosParaSelecao(termoProduto)
  const adicionar = useAdicionarItemOs(osAberturaId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await adicionar.mutateAsync(valores)
        message.success('Item adicionado.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao adicionar item.')
      }
    })
  }

  return (
    <Modal
      title="Adicionar item (peça ou mão de obra) à OS"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Adicionar"
      cancelText="Cancelar"
      confirmLoading={adicionar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="adicionarItemOsForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Form.Item name="tipo" label="Tipo" rules={[{ required: true }]}>
          <Select options={Object.entries(TIPO_ITEM_OS_LABEL).map(([value, label]) => ({ value, label }))} />
        </Form.Item>

        {tipo === 'PRODUTO' ? (
          <Form.Item name="produtoId" label="Produto (peça)" rules={[{ required: true, message: 'Selecione o produto' }]}>
            <AsyncSelect
              opcoes={produtos}
              carregando={carregandoProdutos}
              termo={termoProduto}
              onTermoChange={setTermoProduto}
              toOption={(p) => ({ value: p.id, label: `${p.codigoInterno} — ${p.nome}` })}
            />
          </Form.Item>
        ) : (
          <Form.Item name="complemento" label="Complemento (descrição da mão de obra)" rules={[{ max: 250 }]}>
            <Input.TextArea rows={2} maxLength={250} showCount />
          </Form.Item>
        )}

        <Form.Item name="quantidade" label="Quantidade" rules={[{ required: true, message: 'Informe a quantidade' }]}>
          <InputNumber<number> style={{ width: '100%' }} min={0.000001} precision={6} />
        </Form.Item>
        <Form.Item name="valorUnitario" label="Valor unitário (R$)" rules={[{ required: true, message: 'Informe o valor' }]}>
          <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
        </Form.Item>
        <Form.Item name="taxaDesconto" label="Desconto (%)">
          <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
        </Form.Item>
      </Form>
    </Modal>
  )
}
