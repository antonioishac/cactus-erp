import { useEffect } from 'react'
import { App, Form, Input, InputNumber, Modal, Select } from 'antd'
import { useAtualizarLoja, useCriarLoja } from '../queries'
import { PLANO_ASSINATURA_LABEL } from '../types'
import type { LojaRequest, LojaResponse } from '../types'

interface Props {
  open: boolean
  valorInicial?: LojaResponse
  onClose: () => void
}

export default function LojaModal({ open, valorInicial, onClose }: Props) {
  const { message } = App.useApp()
  const [form] = Form.useForm<LojaRequest>()
  const criar = useCriarLoja()
  const atualizar = useAtualizarLoja()
  const salvando = criar.isPending || atualizar.isPending

  useEffect(() => {
    if (open) {
      form.setFieldsValue(valorInicial ?? { planoAssinatura: 'BASICO' })
    }
  }, [open, valorInicial, form])

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        if (valorInicial) {
          await atualizar.mutateAsync({ id: valorInicial.id, request: valores })
          message.success('Loja atualizada.')
        } else {
          await criar.mutateAsync(valores)
          message.success('Loja criada.')
        }
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao salvar loja.')
      }
    })
  }

  return (
    <Modal
      title={valorInicial ? 'Editar loja' : 'Nova loja'}
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      confirmLoading={salvando}
      destroyOnHidden
      width={560}
    >
      <Form form={form} name="lojaForm" layout="vertical">
        <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 120 }]}>
          <Input />
        </Form.Item>
        <Form.Item name="razaoSocial" label="Razão social" rules={[{ max: 150 }]}>
          <Input />
        </Form.Item>
        <Form.Item name="cnpj" label="CNPJ" rules={[{ max: 14 }]}>
          <Input maxLength={14} />
        </Form.Item>
        <Form.Item
          name="subdominio"
          label="Subdomínio"
          tooltip="Chave de roteamento white-label — só letras minúsculas, números e hífen."
          rules={[
            { required: true, message: 'Informe o subdomínio' },
            { max: 60 },
            { pattern: /^[a-z0-9-]+$/, message: 'Só letras minúsculas, números e hífen' },
          ]}
        >
          <Input />
        </Form.Item>
        <Form.Item name="emailContato" label="E-mail de contato" rules={[{ type: 'email' }, { max: 120 }]}>
          <Input />
        </Form.Item>
        <Form.Item name="telefone" label="Telefone" rules={[{ max: 20 }]}>
          <Input />
        </Form.Item>
        <Form.Item name="empresaId" label="Empresa fiscal (ERP)" tooltip="Vincula a filial do Cactus ERP responsável pela emissão fiscal desta loja.">
          <InputNumber style={{ width: '100%' }} min={1} />
        </Form.Item>
        <Form.Item name="planoAssinatura" label="Plano de assinatura">
          <Select options={Object.entries(PLANO_ASSINATURA_LABEL).map(([value, label]) => ({ value, label }))} />
        </Form.Item>
      </Form>
    </Modal>
  )
}
