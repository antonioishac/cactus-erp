import { useEffect } from 'react'
import { App, Col, Form, Input, Modal, Row, Select } from 'antd'
import { useCriarNaturezaFinanceira, useAtualizarNaturezaFinanceira } from '../queries'
import { TIPO_NATUREZA_FINANCEIRA_LABEL } from '../types'
import type { NaturezaFinanceiraRequest, NaturezaFinanceiraResponse } from '../types'

interface NaturezaFinanceiraModalProps {
  open: boolean
  empresaId: number
  valorInicial?: NaturezaFinanceiraResponse
  onClose: () => void
}

/** Sem `reativar` — inativar (`DELETE`, na real um soft-delete) some da listagem (`GET` só
 * devolve ativas) sem forma de reverter pela API. */
export default function NaturezaFinanceiraModal({ open, empresaId, valorInicial, onClose }: NaturezaFinanceiraModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<NaturezaFinanceiraRequest>()
  const criar = useCriarNaturezaFinanceira(empresaId)
  const atualizar = useAtualizarNaturezaFinanceira(empresaId)
  const salvando = criar.isPending || atualizar.isPending

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (open) {
      form.setFieldsValue(valorInicial ?? { tipo: 'RECEITA' })
    }
  }, [open, valorInicial, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      const request = { ...valores, empresaId }
      try {
        if (valorInicial) {
          await atualizar.mutateAsync({ id: valorInicial.id, request })
          message.success('Natureza financeira atualizada.')
        } else {
          await criar.mutateAsync(request)
          message.success('Natureza financeira criada.')
        }
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao salvar natureza financeira.')
      }
    })
  }

  return (
    <Modal
      title={valorInicial ? 'Editar natureza financeira' : 'Nova natureza financeira'}
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      confirmLoading={salvando}
      destroyOnHidden
    >
      <Form form={form} name="naturezaFinanceiraForm" layout="vertical" initialValues={{ tipo: 'RECEITA' }}>
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="codigo" label="Código" rules={[{ required: true, message: 'Informe o código' }, { max: 10 }]}>
              <Input maxLength={10} />
            </Form.Item>
          </Col>
          <Col span={16}>
            <Form.Item name="descricao" label="Descrição" rules={[{ required: true, message: 'Informe a descrição' }, { max: 100 }]}>
              <Input maxLength={100} />
            </Form.Item>
          </Col>
        </Row>
        <Form.Item name="tipo" label="Tipo" rules={[{ required: true }]}>
          <Select options={Object.entries(TIPO_NATUREZA_FINANCEIRA_LABEL).map(([value, label]) => ({ value, label }))} />
        </Form.Item>
      </Form>
    </Modal>
  )
}
