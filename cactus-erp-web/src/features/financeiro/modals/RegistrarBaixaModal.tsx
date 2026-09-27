import { useEffect } from 'react'
import { App, Col, Form, Input, InputNumber, Modal, Row, Select } from 'antd'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { useContasBancariasAtivas, useFormasPagamentoParaSelecao } from '../queries'
import { TIPO_CONTA_BANCARIA_LABEL } from '../types'
import type { BaixaRequest } from '../types'

interface RegistrarBaixaModalProps {
  open: boolean
  onClose: () => void
  /** Injetado pelo chamador — `useRegistrarBaixaContaReceber(id)`/`useRegistrarBaixaContaPagar(id)`,
   * mesmas ações, DTOs idênticos no backend (`BaixaRequest` compartilhado entre os dois controllers). */
  onSubmit: (request: BaixaRequest) => Promise<unknown>
  submitting: boolean
  valorSaldo: number
  descontoAte?: string
}

/** Registra recebimento (Contas a Receber) ou pagamento (Contas a Pagar) parcial/total —
 * mesmo modal reaproveitado nos dois lados, ver Javadoc de `BaixaRequest` no backend.
 * `chequeEmitidoId`/`chequeRecebidoId` não expostos nesta fase (CRUD de Cheque é sub-fase
 * futura do Caixa e Bancos). */
export default function RegistrarBaixaModal({ open, onClose, onSubmit, submitting, valorSaldo, descontoAte }: RegistrarBaixaModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<BaixaRequest>()
  const { empresaAtivaId, usuarioId } = useAuth()
  const { data: contasBancarias, isLoading: carregandoContas } = useContasBancariasAtivas(empresaAtivaId ?? undefined)
  const { data: formasPagamento } = useFormasPagamentoParaSelecao()

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (open) {
      form.setFieldsValue({ dataBaixa: dayjs().format('YYYY-MM-DD'), valorBaixa: valorSaldo } as unknown as BaixaRequest)
    }
  }, [open, valorSaldo, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await onSubmit({ ...valores, usuarioId: usuarioId ?? undefined })
        message.success('Baixa registrada com sucesso.')
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao registrar baixa.')
      }
    })
  }

  return (
    <Modal
      title="Registrar baixa"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Registrar"
      cancelText="Cancelar"
      confirmLoading={submitting}
      width={560}
      destroyOnHidden
    >
      <Form form={form} name="registrarBaixaForm" layout="vertical">
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="dataBaixa" label="Data da baixa" rules={[{ required: true, message: 'Informe a data' }]}>
              <Input type="date" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="valorBaixa"
              label="Valor da baixa (R$)"
              rules={[{ required: true, message: 'Informe o valor' }]}
              tooltip={`Saldo em aberto: R$ ${valorSaldo.toFixed(2)}`}
            >
              <InputNumber<number> style={{ width: '100%' }} min={0.01} max={valorSaldo} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item
              name="valorDesconto"
              label="Desconto por antecipação (R$)"
              tooltip={descontoAte ? `Só válido se a data da baixa for até ${dayjs(descontoAte).format('DD/MM/YYYY')}` : 'Título sem desconto configurado'}
            >
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="formaPagamentoId" label="Forma de pagamento">
              <Select
                allowClear
                loading={!formasPagamento}
                options={formasPagamento?.map((f) => ({ value: f.id, label: f.descricao }))}
              />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={24}>
            <Form.Item
              name="contaBancariaId"
              label="Conta bancária"
              rules={[{ required: true, message: 'Selecione a conta bancária' }]}
              tooltip="Toda baixa gera um movimento real na conta selecionada"
            >
              <Select
                loading={carregandoContas}
                options={contasBancarias?.map((c) => ({
                  value: c.id,
                  label: `${c.nome} — ${TIPO_CONTA_BANCARIA_LABEL[c.tipo]}${c.numero ? ` (${c.numero})` : ''}`,
                }))}
              />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={24}>
            <Form.Item name="observacao" label="Observação" rules={[{ max: 250 }]}>
              <Input.TextArea rows={2} maxLength={250} showCount />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
