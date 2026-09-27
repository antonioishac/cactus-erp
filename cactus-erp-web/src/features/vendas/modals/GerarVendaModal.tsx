import { useNavigate } from 'react-router-dom'
import { App, Col, DatePicker, Form, InputNumber, Modal, Row, Select } from 'antd'
import type { Dayjs } from 'dayjs'
import { useAuth } from '@features/auth/useAuth'
import { useFormasPagamentoParaSelecao, useGerarVendaDeOrcamento, useNotasFiscaisTipos } from '../queries'
import type { GerarVendaDeOrcamentoRequest } from '../types'

interface GerarVendaModalProps {
  open: boolean
  orcamentoId: number
  onClose: () => void
}

interface FormValues extends Omit<GerarVendaDeOrcamentoRequest, 'dataVenda' | 'dataSaida' | 'empresaId'> {
  dataVenda?: Dayjs
  dataSaida?: Dayjs
}

/**
 * `VendaOrcamento` não carrega `notaFiscalTipoId`/`formaPagamentoId` (o
 * orçamento cobre condição de pagamento/vendedor/cliente/frete, mas não o
 * fechamento fiscal de fato) — ver Javadoc de `GerarVendaDeOrcamentoRequest`.
 */
export default function GerarVendaModal({ open, orcamentoId, onClose }: GerarVendaModalProps) {
  const { message } = App.useApp()
  const navigate = useNavigate()
  const [form] = Form.useForm<FormValues>()
  const { empresaAtivaId } = useAuth()
  const { data: notasFiscaisTipos } = useNotasFiscaisTipos()
  const { data: formasPagamento } = useFormasPagamentoParaSelecao()
  const gerar = useGerarVendaDeOrcamento(orcamentoId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      const request: GerarVendaDeOrcamentoRequest = {
        ...valores,
        empresaId: empresaAtivaId ?? 0,
        dataVenda: valores.dataVenda ? valores.dataVenda.format('YYYY-MM-DD') : undefined,
        dataSaida: valores.dataSaida ? valores.dataSaida.format('YYYY-MM-DD') : undefined,
      }
      try {
        const venda = await gerar.mutateAsync(request)
        message.success('Venda gerada a partir do orçamento.')
        form.resetFields()
        onClose()
        navigate(`/vendas/vendas/${venda.id}`)
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao gerar venda.')
      }
    })
  }

  return (
    <Modal
      title="Gerar venda a partir do orçamento"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Gerar Venda"
      cancelText="Cancelar"
      confirmLoading={gerar.isPending}
      width={560}
      destroyOnHidden
    >
      <Form form={form} name="gerarVendaForm" layout="vertical">
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="notaFiscalTipoId" label="Tipo de nota fiscal" rules={[{ required: true, message: 'Selecione o tipo' }]}>
              <Select options={notasFiscaisTipos?.conteudo.map((t) => ({ value: t.id, label: t.nome }))} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="formaPagamentoId" label="Forma de pagamento" rules={[{ required: true, message: 'Selecione a forma' }]}>
              <Select options={formasPagamento?.map((f) => ({ value: f.id, label: f.descricao }))} />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="dataVenda" label="Data da venda">
              <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="dataSaida" label="Data de saída">
              <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="numeroFatura" label="Nº da fatura">
              <InputNumber style={{ width: '100%' }} min={1} />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
