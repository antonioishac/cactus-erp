import { useEffect, useState } from 'react'
import { App, Col, DatePicker, Form, Input, Modal, Row, Select } from 'antd'
import dayjs, { type Dayjs } from 'dayjs'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useProdutoResumo } from '@shared/hooks/useProdutoResumo'
import { useBuscaProdutosParaSelecao, useCriarLote, useAtualizarLote } from '../queries'
import type { LoteResponse } from '../types'

interface LoteModalProps {
  open: boolean
  valorInicial?: LoteResponse
  onClose: () => void
}

/** `produtoId` fica travado em edição — trocar o produto de um lote já em uso seria reescrever a rastreabilidade, não faz sentido de negócio. */
export default function LoteModal({ open, valorInicial, onClose }: LoteModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<Record<string, unknown>>()
  const [termoProduto, setTermoProduto] = useState<string | undefined>(undefined)
  const { data: produtos, isLoading: carregandoProdutos } = useBuscaProdutosParaSelecao(termoProduto)
  const { data: produtoAtual } = useProdutoResumo(valorInicial?.produtoId)
  const criar = useCriarLote()
  const atualizar = useAtualizarLote()
  const salvando = criar.isPending || atualizar.isPending

  useEffect(() => {
    if (open) {
      form.setFieldsValue({
        ...(valorInicial ?? { situacao: 'ATIVO' }),
        dataFabricacao: valorInicial?.dataFabricacao ? dayjs(valorInicial.dataFabricacao) : undefined,
        dataValidade: valorInicial?.dataValidade ? dayjs(valorInicial.dataValidade) : undefined,
      })
    }
  }, [open, valorInicial, form])

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      const dataFabricacao = valores.dataFabricacao as Dayjs | undefined
      const dataValidade = valores.dataValidade as Dayjs | undefined
      const request = {
        ...valores,
        dataFabricacao: dataFabricacao ? dataFabricacao.format('YYYY-MM-DD') : undefined,
        dataValidade: dataValidade ? dataValidade.format('YYYY-MM-DD') : undefined,
      } as Parameters<typeof criar.mutateAsync>[0]

      try {
        if (valorInicial) {
          await atualizar.mutateAsync({ id: valorInicial.id, request })
          message.success('Lote atualizado.')
        } else {
          await criar.mutateAsync(request)
          message.success('Lote criado.')
        }
        form.resetFields()
        setTermoProduto(undefined)
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao salvar lote.')
      }
    })
  }

  return (
    <Modal
      title={valorInicial ? 'Editar lote' : 'Novo lote'}
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      confirmLoading={salvando}
      destroyOnHidden
    >
      <Form form={form} name="loteForm" layout="vertical">
        <Form.Item name="produtoId" label="Produto" rules={[{ required: true, message: 'Selecione o produto' }]}>
          <AsyncSelect
            disabled={!!valorInicial}
            opcoes={produtos}
            carregando={carregandoProdutos}
            termo={termoProduto}
            onTermoChange={setTermoProduto}
            toOption={(p) => ({ value: p.id, label: `${p.codigoInterno} — ${p.nome}` })}
            labelValorAtual={produtoAtual ? `${produtoAtual.codigoInterno} — ${produtoAtual.nome}` : undefined}
          />
        </Form.Item>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="codigo" label="Código do lote" rules={[{ required: true, message: 'Informe o código' }, { max: 60 }]}>
              <Input />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="situacao" label="Situação">
              <Select options={[{ value: 'ATIVO', label: 'Ativo' }, { value: 'INATIVO', label: 'Inativo' }]} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="dataFabricacao" label="Data de fabricação">
              <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="dataValidade" label="Data de validade">
              <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
