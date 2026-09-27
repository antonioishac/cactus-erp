import { App, Col, DatePicker, Form, Input, InputNumber, Modal, Row, Select } from 'antd'
import dayjs from 'dayjs'
import ProdutoResumoCell from '@shared/ui/ProdutoResumoCell'
import { useCriarRecebimento, useDepositosAtivos, useLotesParaSelecao } from '../queries'
import type { DepositoResumo, PedidoCompraItemResponse, RecebimentoCompraItemRequest } from '../types'

interface NovoRecebimentoModalProps {
  open: boolean
  pedidoId: number
  empresaId: number
  itensPendentes: PedidoCompraItemResponse[]
  onClose: () => void
}

interface FormValues {
  dataRecebimento: dayjs.Dayjs
  numeroNotaFiscal?: string
  chaveAcessoNfe?: string
  observacao?: string
  itens: { quantidadeRecebida?: number; depositoId?: number; loteId?: number }[]
}

/**
 * `RecebimentoCompraItemRequest` referencia `pedidoCompraItemId`, não
 * `produtoId` — a linha do form é indexada exatamente pela ordem de
 * `itensPendentes`, não editável pelo usuário (sem add/remove, ver Javadoc de
 * `RecebimentoCompraController`: o backend valida quantidade recebida contra
 * o pendente do próprio item do pedido).
 */
function LinhaItemRecebimento({
  item,
  index,
  depositos,
}: {
  item: PedidoCompraItemResponse
  index: number
  depositos: DepositoResumo[] | undefined
}) {
  const { data: lotes } = useLotesParaSelecao(item.produtoId)
  return (
    <Row gutter={12} align="middle" style={{ marginBottom: 4 }}>
      <Col span={7}>
        <ProdutoResumoCell produtoId={item.produtoId} />
        <div style={{ color: 'rgba(0,0,0,0.45)', fontSize: 12 }}>Pendente: {item.quantidadePendente}</div>
      </Col>
      <Col span={5}>
        <Form.Item
          name={['itens', index, 'quantidadeRecebida']}
          rules={[{ required: true, message: 'Informe a quantidade' }]}
          style={{ marginBottom: 0 }}
        >
          <InputNumber style={{ width: '100%' }} min={0.001} max={item.quantidadePendente} precision={3} placeholder="Qtd. recebida" />
        </Form.Item>
      </Col>
      <Col span={7}>
        <Form.Item name={['itens', index, 'depositoId']} rules={[{ required: true, message: 'Selecione o depósito' }]} style={{ marginBottom: 0 }}>
          <Select placeholder="Depósito" options={depositos?.map((d) => ({ value: d.id, label: d.nome }))} />
        </Form.Item>
      </Col>
      <Col span={5}>
        <Form.Item name={['itens', index, 'loteId']} style={{ marginBottom: 0 }}>
          <Select allowClear placeholder="Lote (opcional)" options={lotes?.map((l) => ({ value: l.id, label: l.codigo }))} />
        </Form.Item>
      </Col>
    </Row>
  )
}

export default function NovoRecebimentoModal({ open, pedidoId, empresaId, itensPendentes, onClose }: NovoRecebimentoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<FormValues>()
  const { data: depositos } = useDepositosAtivos(empresaId)
  const criar = useCriarRecebimento(pedidoId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      const itens: RecebimentoCompraItemRequest[] = valores.itens
        .map((linha, index) => ({
          pedidoCompraItemId: itensPendentes[index]?.id as number,
          quantidadeRecebida: linha.quantidadeRecebida as number,
          depositoId: linha.depositoId as number,
          loteId: linha.loteId,
        }))
        .filter((linha) => linha.quantidadeRecebida > 0)

      try {
        await criar.mutateAsync({
          pedidoId,
          dataRecebimento: valores.dataRecebimento.format('YYYY-MM-DD'),
          numeroNotaFiscal: valores.numeroNotaFiscal,
          chaveAcessoNfe: valores.chaveAcessoNfe,
          observacao: valores.observacao,
          itens,
        })
        message.success('Recebimento registrado — entrada gerada no estoque.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao registrar recebimento.')
      }
    })
  }

  return (
    <Modal
      title="Novo recebimento"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Registrar Recebimento"
      cancelText="Cancelar"
      confirmLoading={criar.isPending}
      width={780}
      destroyOnHidden
    >
      <Form form={form} name="novoRecebimentoForm" layout="vertical" initialValues={{ dataRecebimento: dayjs() }}>
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="dataRecebimento" label="Data do recebimento" rules={[{ required: true, message: 'Informe a data' }]}>
              <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="numeroNotaFiscal" label="Número da nota fiscal">
              <Input maxLength={20} />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="chaveAcessoNfe" label="Chave de acesso NF-e">
              <Input maxLength={44} />
            </Form.Item>
          </Col>
        </Row>
        <Form.Item name="observacao" label="Observação" rules={[{ max: 250 }]}>
          <Input.TextArea rows={2} maxLength={250} showCount />
        </Form.Item>

        <div style={{ marginBottom: 8, fontWeight: 500 }}>Itens pendentes de recebimento</div>
        {itensPendentes.map((item, index) => (
          <LinhaItemRecebimento key={item.id} item={item} index={index} depositos={depositos} />
        ))}
      </Form>
    </Modal>
  )
}
