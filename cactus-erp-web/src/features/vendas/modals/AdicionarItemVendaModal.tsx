import { useEffect, useState } from 'react'
import { App, Col, Form, InputNumber, Modal, Row, Select } from 'antd'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useAdicionarItemVenda, useAtualizarItemVenda, useBuscaProdutosParaSelecao, useDepositosAtivos } from '../queries'
import type { VendaItemRequest, VendaItemResponse } from '../types'

interface AdicionarItemVendaModalProps {
  open: boolean
  vendaId: number
  empresaId: number
  /** Presente = modo edição (ex: item copiado de um Orçamento, sem depósito ainda — ver `VendaService#gerarAPartirDeOrcamento`). Ausente = modo adicionar. */
  itemEmEdicao?: VendaItemResponse | null
  onClose: () => void
}

/**
 * `depositoId` é obrigatório na venda (diferente de Orçamento) — a venda baixa
 * estoque de verdade na confirmação. Serve tanto pra adicionar item novo
 * quanto editar um já existente (mesmo componente, `itemEmEdicao` decide qual
 * mutação usar) — necessário porque um item gerado a partir de um Orçamento
 * nasce sem `depositoId`, e só `PUT` consegue completá-lo (ver Javadoc de
 * `VendaService`, achado real da Fase 7 do backend).
 */
export default function AdicionarItemVendaModal({ open, vendaId, empresaId, itemEmEdicao, onClose }: AdicionarItemVendaModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<VendaItemRequest>()
  const [termo, setTermo] = useState<string | undefined>(undefined)
  const { data: produtos, isLoading: carregandoProdutos } = useBuscaProdutosParaSelecao(termo)
  const { data: depositos } = useDepositosAtivos(empresaId)
  const adicionar = useAdicionarItemVenda(vendaId)
  const atualizar = useAtualizarItemVenda(vendaId)
  const salvando = adicionar.isPending || atualizar.isPending

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (open) {
      form.setFieldsValue(itemEmEdicao ?? {})
    }
  }, [open, itemEmEdicao, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        if (itemEmEdicao) {
          await atualizar.mutateAsync({ itemId: itemEmEdicao.id, request: valores })
          message.success('Item atualizado.')
        } else {
          await adicionar.mutateAsync(valores)
          message.success('Item adicionado à venda.')
        }
        form.resetFields()
        setTermo(undefined)
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao salvar item.')
      }
    })
  }

  return (
    <Modal
      title={itemEmEdicao ? 'Editar item da venda' : 'Adicionar item à venda'}
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText={itemEmEdicao ? 'Salvar' : 'Adicionar'}
      cancelText="Cancelar"
      confirmLoading={salvando}
      width={560}
      destroyOnHidden
    >
      <Form form={form} name="adicionarItemVendaForm" layout="vertical">
        <Form.Item name="produtoId" label="Produto" rules={[{ required: true, message: 'Selecione o produto' }]}>
          <AsyncSelect
            opcoes={produtos}
            carregando={carregandoProdutos}
            termo={termo}
            onTermoChange={setTermo}
            toOption={(p) => ({ value: p.id, label: `${p.codigoInterno} — ${p.nome}` })}
            labelValorAtual={itemEmEdicao ? `Produto #${itemEmEdicao.produtoId}` : undefined}
          />
        </Form.Item>
        <Form.Item name="depositoId" label="Depósito" rules={[{ required: true, message: 'Selecione o depósito' }]}>
          <Select options={depositos?.map((d) => ({ value: d.id, label: `${d.codigo} — ${d.nome}` }))} />
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
