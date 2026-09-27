import { App, Button, Col, Flex, Form, Input, InputNumber, Modal, Row, Typography } from 'antd'
import { DeleteOutlined, PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import type { RenegociarTituloRequest } from '../types'

interface RenegociarTituloModalProps {
  open: boolean
  onClose: () => void
  onSubmit: (request: RenegociarTituloRequest) => Promise<unknown>
  submitting: boolean
  valorSaldo: number
}

/** Substitui o saldo em aberto por N título(s) novo(s) — a soma de `novasParcelas[].valor`
 * precisa bater EXATAMENTE com `valorSaldo` (validado no backend, 422 se divergir; aviso
 * client-side aqui é só conveniência, não substitui a validação real). */
export default function RenegociarTituloModal({ open, onClose, onSubmit, submitting, valorSaldo }: RenegociarTituloModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<RenegociarTituloRequest>()
  const novasParcelas = Form.useWatch('novasParcelas', form) as RenegociarTituloRequest['novasParcelas'] | undefined
  const somaParcelas = (novasParcelas ?? []).reduce((soma, p) => soma + (Number(p?.valor) || 0), 0)
  const somaBate = Math.abs(somaParcelas - valorSaldo) < 0.005

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await onSubmit({
          ...valores,
          novasParcelas: valores.novasParcelas.map((p) => ({ ...p, dataVencimento: dayjs(p.dataVencimento).format('YYYY-MM-DD') })),
        })
        message.success('Título renegociado com sucesso.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao renegociar título.')
      }
    })
  }

  return (
    <Modal
      title="Renegociar título"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Renegociar"
      cancelText="Voltar"
      confirmLoading={submitting}
      width={640}
      destroyOnHidden
    >
      <Form form={form} name="renegociarTituloForm" layout="vertical" initialValues={{ novasParcelas: [{}] }}>
        <Form.Item name="motivo" label="Motivo" rules={[{ required: true, message: 'Informe o motivo' }, { max: 250 }]}>
          <Input.TextArea rows={2} maxLength={250} showCount />
        </Form.Item>

        <Typography.Text type="secondary">
          Saldo em aberto: <strong>R$ {valorSaldo.toFixed(2)}</strong> — a soma das novas parcelas precisa bater exatamente com esse valor.
        </Typography.Text>

        <Form.List name="novasParcelas">
          {(fields, { add, remove }) => (
            <Flex vertical gap={8} style={{ marginTop: 12 }}>
              {fields.map((field) => (
                <Row gutter={12} key={field.key} align="middle">
                  <Col span={10}>
                    <Form.Item
                      name={[field.name, 'dataVencimento']}
                      rules={[{ required: true, message: 'Vencimento' }]}
                      style={{ marginBottom: 8 }}
                    >
                      <Input type="date" placeholder="Vencimento" />
                    </Form.Item>
                  </Col>
                  <Col span={10}>
                    <Form.Item name={[field.name, 'valor']} rules={[{ required: true, message: 'Valor' }]} style={{ marginBottom: 8 }}>
                      <InputNumber<number> style={{ width: '100%' }} min={0.01} precision={2} decimalSeparator="," placeholder="Valor (R$)" />
                    </Form.Item>
                  </Col>
                  <Col span={4}>
                    <Button type="text" danger icon={<DeleteOutlined />} onClick={() => remove(field.name)} disabled={fields.length <= 1} />
                  </Col>
                </Row>
              ))}
              <Flex justify="space-between" align="center">
                <Button icon={<PlusOutlined />} onClick={() => add({})}>
                  Adicionar parcela
                </Button>
                <Typography.Text type={somaBate ? 'success' : 'danger'}>Soma: R$ {somaParcelas.toFixed(2)}</Typography.Text>
              </Flex>
            </Flex>
          )}
        </Form.List>
      </Form>
    </Modal>
  )
}
