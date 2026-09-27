import { App, Col, Form, Input, InputNumber, Modal, Row, Select } from 'antd'
import { useAuth } from '@features/auth/useAuth'
import { useContasBancariasAtivas, useTransferirEntreContas } from '../queries'
import { TIPO_CONTA_BANCARIA_LABEL } from '../types'
import type { TransferenciaBancariaRequest } from '../types'

interface NovaTransferenciaModalProps {
  open: boolean
  onClose: () => void
}

/** Débito na origem e crédito no destino atomicamente — sem estado "em trânsito" (não há
 * liquidação bancária real por trás, ver Javadoc de `TransferenciaBancaria` no backend). */
export default function NovaTransferenciaModal({ open, onClose }: NovaTransferenciaModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<TransferenciaBancariaRequest>()
  const { empresaAtivaId, usuarioId } = useAuth()
  const { data: contasBancarias, isLoading: carregandoContas } = useContasBancariasAtivas(empresaAtivaId ?? undefined)
  const transferir = useTransferirEntreContas()

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await transferir.mutateAsync({ ...valores, usuarioId: usuarioId ?? undefined })
        message.success('Transferência realizada com sucesso.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao transferir entre contas.')
      }
    })
  }

  const opcoesContas = contasBancarias?.map((c) => ({ value: c.id, label: `${c.nome} — ${TIPO_CONTA_BANCARIA_LABEL[c.tipo]}` }))

  return (
    <Modal
      title="Nova transferência entre contas"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Transferir"
      cancelText="Cancelar"
      confirmLoading={transferir.isPending}
      width={560}
      destroyOnHidden
    >
      <Form form={form} name="novaTransferenciaForm" layout="vertical">
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="contaOrigemId" label="Conta de origem" rules={[{ required: true, message: 'Selecione a conta de origem' }]}>
              <Select loading={carregandoContas} options={opcoesContas} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="contaDestinoId" label="Conta de destino" rules={[{ required: true, message: 'Selecione a conta de destino' }]}>
              <Select loading={carregandoContas} options={opcoesContas} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="dataTransferencia" label="Data da transferência">
              <Input type="date" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="valor" label="Valor (R$)" rules={[{ required: true, message: 'Informe o valor' }]}>
              <InputNumber<number> style={{ width: '100%' }} min={0.01} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
        </Row>
        <Form.Item name="historico" label="Histórico" rules={[{ max: 250 }]}>
          <Input.TextArea rows={2} maxLength={250} showCount />
        </Form.Item>
      </Form>
    </Modal>
  )
}
