import { useEffect } from 'react'
import { App, Col, Form, Input, InputNumber, Modal, Row, Select } from 'antd'
import { useAuth } from '@features/auth/useAuth'
import { useContasBancariasAtivas, useRegistrarMovimentoManual } from '../queries'
import { TIPO_CONTA_BANCARIA_LABEL } from '../types'
import type { MovimentoCaixaBancoRequest } from '../types'

interface NovoMovimentoManualModalProps {
  open: boolean
  onClose: () => void
}

/** Lançamento manual de ajuste (tarifa, IOF, correção de saldo) — só `ENTRADA`/`SAIDA`
 * (transferência entre contas próprias tem tela dedicada em Tesouraria). */
export default function NovoMovimentoManualModal({ open, onClose }: NovoMovimentoManualModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<MovimentoCaixaBancoRequest>()
  const { empresaAtivaId, usuarioId } = useAuth()
  const { data: contasBancarias, isLoading: carregandoContas } = useContasBancariasAtivas(empresaAtivaId ?? undefined)
  const registrar = useRegistrarMovimentoManual()

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (open) form.resetFields()
  }, [open, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await registrar.mutateAsync({ ...valores, usuarioId: usuarioId ?? undefined })
        message.success('Movimento registrado com sucesso.')
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao registrar movimento.')
      }
    })
  }

  return (
    <Modal
      title="Novo movimento manual"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Registrar"
      cancelText="Cancelar"
      confirmLoading={registrar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="novoMovimentoManualForm" layout="vertical">
        <Form.Item name="contaBancariaId" label="Conta bancária" rules={[{ required: true, message: 'Selecione a conta' }]}>
          <Select
            loading={carregandoContas}
            options={contasBancarias?.map((c) => ({ value: c.id, label: `${c.nome} — ${TIPO_CONTA_BANCARIA_LABEL[c.tipo]}` }))}
          />
        </Form.Item>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="tipoMovimento" label="Tipo" rules={[{ required: true, message: 'Selecione o tipo' }]}>
              <Select options={[{ value: 'ENTRADA', label: 'Entrada' }, { value: 'SAIDA', label: 'Saída' }]} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="valor" label="Valor (R$)" rules={[{ required: true, message: 'Informe o valor' }]}>
              <InputNumber<number> style={{ width: '100%' }} min={0.01} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
        </Row>
        <Form.Item name="dataMovimento" label="Data do movimento">
          <Input type="date" />
        </Form.Item>
        <Form.Item name="historico" label="Histórico" rules={[{ required: true, message: 'Informe o histórico' }, { max: 250 }]}>
          <Input.TextArea rows={2} maxLength={250} showCount placeholder="Ex: Tarifa bancária, IOF, correção de saldo" />
        </Form.Item>
      </Form>
    </Modal>
  )
}
