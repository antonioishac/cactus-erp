import { App, Col, Form, Input, InputNumber, Modal, Row, Select } from 'antd'
import { useAuth } from '@features/auth/useAuth'
import { useAplicarFinanceiro, useContasBancariasAtivas } from '../queries'
import { TIPO_APLICACAO_FINANCEIRA_LABEL, TIPO_CONTA_BANCARIA_LABEL } from '../types'
import type { AplicacaoFinanceiraRequest } from '../types'

interface NovaAplicacaoModalProps {
  open: boolean
  onClose: () => void
}

/** Debita a conta bancária de origem na hora (`SAIDA` real) — resgate é ação separada. */
export default function NovaAplicacaoModal({ open, onClose }: NovaAplicacaoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<AplicacaoFinanceiraRequest>()
  const { empresaAtivaId } = useAuth()
  const { data: contasBancarias, isLoading: carregandoContas } = useContasBancariasAtivas(empresaAtivaId ?? undefined)
  const aplicar = useAplicarFinanceiro()

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await aplicar.mutateAsync(valores)
        message.success('Aplicação registrada com sucesso.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao registrar aplicação.')
      }
    })
  }

  return (
    <Modal
      title="Nova aplicação financeira"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Aplicar"
      cancelText="Cancelar"
      confirmLoading={aplicar.isPending}
      width={560}
      destroyOnHidden
    >
      <Form form={form} name="novaAplicacaoForm" layout="vertical">
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="contaBancariaId" label="Conta bancária de origem" rules={[{ required: true, message: 'Selecione a conta' }]}>
              <Select
                loading={carregandoContas}
                options={contasBancarias?.map((c) => ({ value: c.id, label: `${c.nome} — ${TIPO_CONTA_BANCARIA_LABEL[c.tipo]}` }))}
              />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="tipoAplicacao" label="Tipo" rules={[{ required: true, message: 'Selecione o tipo' }]}>
              <Select options={Object.entries(TIPO_APLICACAO_FINANCEIRA_LABEL).map(([value, label]) => ({ value, label }))} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="dataAplicacao" label="Data da aplicação">
              <Input type="date" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="valorAplicado" label="Valor aplicado (R$)" rules={[{ required: true, message: 'Informe o valor' }]}>
              <InputNumber<number> style={{ width: '100%' }} min={0.01} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
        </Row>
        <Form.Item name="descricao" label="Descrição" rules={[{ max: 150 }]}>
          <Input maxLength={150} />
        </Form.Item>
      </Form>
    </Modal>
  )
}
