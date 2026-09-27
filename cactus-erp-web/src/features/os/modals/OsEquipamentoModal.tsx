import { useEffect } from 'react'
import { App, Form, Input, Modal, Select } from 'antd'
import { useAtualizarOsEquipamento, useCriarOsEquipamento } from '../queries'
import type { OsEquipamentoResponse } from '../types'

interface OsEquipamentoModalProps {
  open: boolean
  valorInicial?: OsEquipamentoResponse
  onClose: () => void
}

const VALORES_PADRAO = { situacao: 'ATIVO' as const }

export default function OsEquipamentoModal({ open, valorInicial, onClose }: OsEquipamentoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<{ nome: string; descricao?: string; situacao: 'ATIVO' | 'INATIVO' }>()
  const criar = useCriarOsEquipamento()
  const atualizar = useAtualizarOsEquipamento()
  const salvando = criar.isPending || atualizar.isPending

  useEffect(() => {
    if (open) {
      form.setFieldsValue(valorInicial ?? VALORES_PADRAO)
    }
  }, [open, valorInicial, form])

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        if (valorInicial) {
          await atualizar.mutateAsync({ id: valorInicial.id, request: valores })
          message.success('Tipo de equipamento atualizado.')
        } else {
          await criar.mutateAsync(valores)
          message.success('Tipo de equipamento criado.')
        }
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao salvar tipo de equipamento.')
      }
    })
  }

  return (
    <Modal
      title={valorInicial ? 'Editar tipo de equipamento' : 'Novo tipo de equipamento'}
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      confirmLoading={salvando}
      destroyOnHidden
    >
      <Form form={form} name="osEquipamentoForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 100 }]}>
          <Input placeholder="Ex: Notebook, Motor de partida, Ar-condicionado..." />
        </Form.Item>
        <Form.Item name="descricao" label="Descrição" rules={[{ max: 250 }]}>
          <Input.TextArea rows={2} maxLength={250} showCount />
        </Form.Item>
        <Form.Item name="situacao" label="Situação">
          <Select options={[{ value: 'ATIVO', label: 'Ativo' }, { value: 'INATIVO', label: 'Inativo' }]} />
        </Form.Item>
      </Form>
    </Modal>
  )
}
