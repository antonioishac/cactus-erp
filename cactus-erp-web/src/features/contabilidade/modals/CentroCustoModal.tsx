import { useEffect } from 'react'
import { App, Form, Input, Modal } from 'antd'
import { useAtualizarCentroCusto, useCriarCentroCusto } from '../queries'
import type { CentroCustoRequest, CentroCustoResponse } from '../types'

interface Props {
  open: boolean
  empresaId: number
  valorInicial?: CentroCustoResponse
  onClose: () => void
}

export default function CentroCustoModal({ open, empresaId, valorInicial, onClose }: Props) {
  const { message } = App.useApp()
  const [form] = Form.useForm<CentroCustoRequest>()
  const criar = useCriarCentroCusto()
  const atualizar = useAtualizarCentroCusto()
  const salvando = criar.isPending || atualizar.isPending

  useEffect(() => {
    if (open) {
      form.setFieldsValue(valorInicial ?? { empresaId, situacao: 'ATIVO' })
    }
  }, [open, valorInicial, empresaId, form])

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        const request: CentroCustoRequest = { ...valores, empresaId }
        if (valorInicial) {
          await atualizar.mutateAsync({ id: valorInicial.id, request })
          message.success('Centro de custo atualizado.')
        } else {
          await criar.mutateAsync(request)
          message.success('Centro de custo criado.')
        }
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao salvar centro de custo.')
      }
    })
  }

  return (
    <Modal
      title={valorInicial ? 'Editar centro de custo' : 'Novo centro de custo'}
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      confirmLoading={salvando}
      destroyOnHidden
    >
      <Form form={form} name="centroCustoForm" layout="vertical">
        <Form.Item name="codigo" label="Código" rules={[{ required: true, message: 'Informe o código' }, { max: 20 }]}>
          <Input />
        </Form.Item>
        <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 150 }]}>
          <Input />
        </Form.Item>
      </Form>
    </Modal>
  )
}
