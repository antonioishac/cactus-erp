import { useEffect } from 'react'
import { App, Col, Form, Input, Modal, Row } from 'antd'
import { useCriarTipoDocumentoFinanceiro } from '../queries'
import type { TipoDocumentoFinanceiroRequest } from '../types'

interface TipoDocumentoFinanceiroModalProps {
  open: boolean
  onClose: () => void
}

/** Catálogo GLOBAL (não por empresa) — sem `PUT` na API (`TipoDocumentoFinanceiroController` só
 * tem `POST`/`DELETE`), então esta modal é sempre criação, nunca edição. */
export default function TipoDocumentoFinanceiroModal({ open, onClose }: TipoDocumentoFinanceiroModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<TipoDocumentoFinanceiroRequest>()
  const criar = useCriarTipoDocumentoFinanceiro()

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (open) {
      form.resetFields()
    }
  }, [open, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await criar.mutateAsync(valores)
        message.success('Tipo de documento criado.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao criar tipo de documento.')
      }
    })
  }

  return (
    <Modal
      title="Novo tipo de documento financeiro"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      confirmLoading={criar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="tipoDocumentoFinanceiroForm" layout="vertical">
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="codigo" label="Código" rules={[{ required: true, message: 'Informe o código' }, { max: 10 }]}>
              <Input maxLength={10} placeholder="Ex: NF, BOL, REC" />
            </Form.Item>
          </Col>
          <Col span={16}>
            <Form.Item name="descricao" label="Descrição" rules={[{ required: true, message: 'Informe a descrição' }, { max: 100 }]}>
              <Input maxLength={100} placeholder="Ex: Nota Fiscal, Boleto, Recibo" />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
