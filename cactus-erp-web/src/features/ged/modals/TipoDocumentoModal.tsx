import { useEffect } from 'react'
import { App, Col, Form, Input, InputNumber, Modal, Row, Select } from 'antd'
import { useAtualizarTipoDocumento, useCriarTipoDocumento } from '../queries'
import type { TipoDocumentoResponse } from '../types'

interface TipoDocumentoModalProps {
  open: boolean
  valorInicial?: TipoDocumentoResponse
  onClose: () => void
}

const VALORES_PADRAO = { situacao: 'ATIVO' as const }

/** `tamanhoMaximoMb` valida todo upload deste tipo (ver `DocumentoController.criar`). */
export default function TipoDocumentoModal({ open, valorInicial, onClose }: TipoDocumentoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<{ nome: string; tamanhoMaximoMb: number; situacao: 'ATIVO' | 'INATIVO' }>()
  const criar = useCriarTipoDocumento()
  const atualizar = useAtualizarTipoDocumento()
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
          message.success('Tipo de documento atualizado.')
        } else {
          await criar.mutateAsync(valores)
          message.success('Tipo de documento criado.')
        }
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao salvar tipo de documento.')
      }
    })
  }

  return (
    <Modal
      title={valorInicial ? 'Editar tipo de documento' : 'Novo tipo de documento'}
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      confirmLoading={salvando}
      destroyOnHidden
    >
      <Form form={form} name="tipoDocumentoForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 100 }]}>
              <Input placeholder="Ex: Contrato, Nota Fiscal, RG..." />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item
              name="tamanhoMaximoMb"
              label="Tamanho máximo (MB)"
              rules={[{ required: true, message: 'Informe o limite' }]}
            >
              <InputNumber<number> style={{ width: '100%' }} min={0.01} precision={2} />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="situacao" label="Situação">
              <Select options={[{ value: 'ATIVO', label: 'Ativo' }, { value: 'INATIVO', label: 'Inativo' }]} />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
