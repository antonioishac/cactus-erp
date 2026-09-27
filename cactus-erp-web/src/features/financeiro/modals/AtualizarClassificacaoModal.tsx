import { useEffect } from 'react'
import { App, Col, Form, Input, InputNumber, Modal, Row, Select } from 'antd'
import dayjs from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { useContasBancariasAtivas, useNaturezasFinanceiras, useTiposDocumentoFinanceiro } from '../queries'
import { TIPO_CONTA_BANCARIA_LABEL } from '../types'
import type { AtualizarClassificacaoTituloRequest, TipoNaturezaFinanceira } from '../types'

interface AtualizarClassificacaoModalProps {
  open: boolean
  onClose: () => void
  onSubmit: (request: AtualizarClassificacaoTituloRequest) => Promise<unknown>
  submitting: boolean
  valoresAtuais: AtualizarClassificacaoTituloRequest | undefined
  /** Filtra o Select de Natureza pelo tipo certo (RECEITA em Contas a Receber, DESPESA em Contas
   * a Pagar) — o backend não impõe essa regra, é só uma conveniência de UX. */
  tipoNatureza: TipoNaturezaFinanceira
}

/** Nunca mexe em valor/vencimento/situação — só categorização (`AtualizarClassificacaoTituloRequest`,
 * compartilhado entre `ContaReceberController`/`ContaPagarController`). Fecha a lacuna de títulos
 * nascidos de integração (VENDA/COMPRA), que não têm como informar isso na geração automática. */
export default function AtualizarClassificacaoModal({
  open,
  onClose,
  onSubmit,
  submitting,
  valoresAtuais,
  tipoNatureza,
}: AtualizarClassificacaoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<AtualizarClassificacaoTituloRequest>()
  const { empresaAtivaId } = useAuth()
  const { data: naturezas } = useNaturezasFinanceiras(empresaAtivaId ?? undefined)
  const { data: tiposDocumento } = useTiposDocumentoFinanceiro()
  const { data: contasBancarias, isLoading: carregandoContas } = useContasBancariasAtivas(empresaAtivaId ?? undefined)
  const naturezasDoTipo = naturezas?.filter((n) => n.tipo === tipoNatureza)

  /* oxlint-disable react-hooks/set-state-in-effect */
  useEffect(() => {
    if (open) {
      form.setFieldsValue(valoresAtuais ?? {})
    }
  }, [open, valoresAtuais, form])
  /* oxlint-enable react-hooks/set-state-in-effect */

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await onSubmit({ ...valores, descontoAte: valores.descontoAte ? dayjs(valores.descontoAte).format('YYYY-MM-DD') : undefined })
        message.success('Classificação atualizada com sucesso.')
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao atualizar classificação.')
      }
    })
  }

  return (
    <Modal
      title="Atualizar classificação do título"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      confirmLoading={submitting}
      width={640}
      destroyOnHidden
    >
      <Form form={form} name="atualizarClassificacaoForm" layout="vertical">
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="naturezaFinanceiraId" label="Natureza financeira" rules={[{ required: true, message: 'Selecione a natureza' }]}>
              <Select options={naturezasDoTipo?.map((n) => ({ value: n.id, label: `${n.codigo} — ${n.descricao}` }))} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="tipoDocumentoId" label="Tipo de documento" rules={[{ required: true, message: 'Selecione o tipo' }]}>
              <Select options={tiposDocumento?.map((t) => ({ value: t.id, label: `${t.codigo} — ${t.descricao}` }))} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={24}>
            <Form.Item name="contaBancariaId" label="Conta bancária prevista">
              <Select
                allowClear
                loading={carregandoContas}
                options={contasBancarias?.map((c) => ({ value: c.id, label: `${c.nome} — ${TIPO_CONTA_BANCARIA_LABEL[c.tipo]}` }))}
              />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="descontoAte" label="Desconto até">
              <Input type="date" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="percentualDesconto" label="% desconto">
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={4} decimalSeparator="," />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="percentualMultaAtraso" label="% multa por atraso (override)">
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="percentualJurosDiaAtraso" label="% juros/dia atraso (override)">
              <InputNumber<number> style={{ width: '100%' }} min={0} precision={4} decimalSeparator="," />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
