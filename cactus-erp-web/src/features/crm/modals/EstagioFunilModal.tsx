import { useEffect } from 'react'
import { App, Col, Form, Input, InputNumber, Modal, Row, Select } from 'antd'
import { useAtualizarEstagioFunil, useCriarEstagioFunil } from '../queries'
import { TIPO_ENCERRAMENTO_FUNIL_LABEL } from '../types'
import type { EstagioFunilRequest, EstagioFunilResponse } from '../types'

interface EstagioFunilModalProps {
  open: boolean
  valorInicial?: EstagioFunilResponse
  onClose: () => void
}

const VALORES_PADRAO: Partial<EstagioFunilRequest> = { tipoEncerramento: 'ABERTO' }

/** Catálogo livre — cada empresa/vertical desenha o próprio funil (mesmo critério de `OsStatus`). `tipoEncerramento` decide o efeito de mover uma oportunidade pra este estágio. */
export default function EstagioFunilModal({ open, valorInicial, onClose }: EstagioFunilModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<EstagioFunilRequest>()
  const criar = useCriarEstagioFunil()
  const atualizar = useAtualizarEstagioFunil()
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
          message.success('Estágio atualizado.')
        } else {
          await criar.mutateAsync(valores)
          message.success('Estágio criado.')
        }
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao salvar estágio.')
      }
    })
  }

  return (
    <Modal
      title={valorInicial ? 'Editar estágio do funil' : 'Novo estágio do funil'}
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      confirmLoading={salvando}
      destroyOnHidden
    >
      <Form form={form} name="estagioFunilForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Row gutter={12}>
          <Col span={16}>
            <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 100 }]}>
              <Input placeholder="Ex: Prospecção, Qualificação, Proposta, Fechamento..." />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="ordem" label="Ordem" rules={[{ required: true, message: 'Informe a ordem' }]}>
              <InputNumber style={{ width: '100%' }} min={1} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="codigo" label="Código" rules={[{ max: 30 }]}>
              <Input maxLength={30} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="tipoEncerramento" label="Tipo de encerramento" rules={[{ required: true }]}>
              <Select options={Object.entries(TIPO_ENCERRAMENTO_FUNIL_LABEL).map(([value, label]) => ({ value, label }))} />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
