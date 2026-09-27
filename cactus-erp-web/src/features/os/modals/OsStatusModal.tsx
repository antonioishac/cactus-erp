import { useEffect } from 'react'
import { App, Col, Form, Input, Modal, Row, Select } from 'antd'
import { useAtualizarOsStatus, useCriarOsStatus } from '../queries'
import type { OsStatusResponse } from '../types'

interface OsStatusModalProps {
  open: boolean
  valorInicial?: OsStatusResponse
  onClose: () => void
}

const VALORES_PADRAO = { situacao: 'ATIVO' as const }

/** Catálogo livre — o ciclo de vida real de uma OS varia por vertical (oficina/assistência técnica/autoelétrica), sem máquina de estados fixa (ver Javadoc de `OsAberturaService` no backend). */
export default function OsStatusModal({ open, valorInicial, onClose }: OsStatusModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<{ codigo?: string; nome: string; situacao: 'ATIVO' | 'INATIVO' }>()
  const criar = useCriarOsStatus()
  const atualizar = useAtualizarOsStatus()
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
          message.success('Status atualizado.')
        } else {
          await criar.mutateAsync(valores)
          message.success('Status criado.')
        }
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao salvar status.')
      }
    })
  }

  return (
    <Modal
      title={valorInicial ? 'Editar status de OS' : 'Novo status de OS'}
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      confirmLoading={salvando}
      destroyOnHidden
    >
      <Form form={form} name="osStatusForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Row gutter={12}>
          <Col span={16}>
            <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 100 }]}>
              <Input placeholder="Ex: Aberta, Em Orçamento, Aguardando Peça..." />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="codigo" label="Código" rules={[{ max: 2 }]}>
              <Input maxLength={2} />
            </Form.Item>
          </Col>
        </Row>
        <Form.Item name="situacao" label="Situação">
          <Select options={[{ value: 'ATIVO', label: 'Ativo' }, { value: 'INATIVO', label: 'Inativo' }]} />
        </Form.Item>
      </Form>
    </Modal>
  )
}
