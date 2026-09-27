import { App, Col, Form, Input, InputNumber, Modal, Row, Select } from 'antd'
import { useAuth } from '@features/auth/useAuth'
import { useContasBancariasAtivas, useCriarChequeEmitido } from '../queries'
import { TIPO_CONTA_BANCARIA_LABEL } from '../types'
import type { ChequeEmitidoRequest } from '../types'

interface NovoChequeEmitidoModalProps {
  open: boolean
  onClose: () => void
}

export default function NovoChequeEmitidoModal({ open, onClose }: NovoChequeEmitidoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<ChequeEmitidoRequest>()
  const { empresaAtivaId } = useAuth()
  const { data: contasBancarias, isLoading: carregandoContas } = useContasBancariasAtivas(empresaAtivaId ?? undefined)
  const criar = useCriarChequeEmitido()

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await criar.mutateAsync(valores)
        message.success('Cheque emitido registrado com sucesso.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao registrar cheque emitido.')
      }
    })
  }

  return (
    <Modal
      title="Registrar cheque emitido"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Registrar"
      cancelText="Cancelar"
      confirmLoading={criar.isPending}
      width={560}
      destroyOnHidden
    >
      <Form form={form} name="novoChequeEmitidoForm" layout="vertical">
        <Row gutter={12}>
          <Col span={16}>
            <Form.Item name="contaBancariaId" label="Conta bancária" rules={[{ required: true, message: 'Selecione a conta' }]}>
              <Select
                loading={carregandoContas}
                options={contasBancarias?.map((c) => ({ value: c.id, label: `${c.nome} — ${TIPO_CONTA_BANCARIA_LABEL[c.tipo]}` }))}
              />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="numero" label="Número" rules={[{ required: true, message: 'Informe o número' }, { max: 20 }]}>
              <Input maxLength={20} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="dataEmissao" label="Data de emissão" rules={[{ required: true, message: 'Informe a emissão' }]}>
              <Input type="date" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="bomPara" label="Bom para (pré-datado)">
              <Input type="date" />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="valor" label="Valor (R$)" rules={[{ required: true, message: 'Informe o valor' }]}>
              <InputNumber<number> style={{ width: '100%' }} min={0.01} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="nominalA" label="Nominal a" rules={[{ max: 150 }]}>
              <Input maxLength={150} />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
