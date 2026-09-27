import { useNavigate } from 'react-router-dom'
import { App, Col, Form, Input, Modal, Row, Select } from 'antd'
import { useGerarNotaServicoDeOs, useTodasListasServico } from '../queries'
import type { GerarNotaServicoRequest } from '../types'

interface GerarNotaServicoModalProps {
  open: boolean
  osAberturaId: number
  onClose: () => void
}

/** `listaServicoId` é aplicado a TODOS os itens SERVICO da OS (simplificação deliberada, ver Javadoc de `GerarNotaServicoRequest` no backend). */
export default function GerarNotaServicoModal({ open, osAberturaId, onClose }: GerarNotaServicoModalProps) {
  const { message } = App.useApp()
  const navigate = useNavigate()
  const [form] = Form.useForm<GerarNotaServicoRequest>()
  const { data: listasServico } = useTodasListasServico()
  const gerar = useGerarNotaServicoDeOs(osAberturaId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        const nota = await gerar.mutateAsync(valores)
        message.success('NFS-e gerada a partir da OS.')
        form.resetFields()
        onClose()
        navigate(`/fiscal/notas-servico/${nota.id}`)
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao gerar NFS-e.')
      }
    })
  }

  return (
    <Modal
      title="Gerar NFS-e a partir da OS"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Gerar NFS-e"
      cancelText="Cancelar"
      confirmLoading={gerar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="gerarNotaServicoForm" layout="vertical">
        <Form.Item
          name="listaServicoId"
          label="Lista de serviço (aplicada a todos os itens de serviço da OS)"
          rules={[{ required: true, message: 'Selecione o item da lista de serviço' }]}
        >
          <Select
            options={listasServico?.map((l) => ({ value: l.id, label: `${l.codigo} — ${l.descricao}` }))}
            placeholder="Ex: 14.01 — Lubrificação, limpeza..."
          />
        </Form.Item>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="numeroRps" label="Número do RPS" rules={[{ required: true, message: 'Informe o número do RPS' }, { max: 15 }]}>
              <Input maxLength={15} />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="serieRps" label="Série do RPS" rules={[{ max: 5 }]}>
              <Input maxLength={5} placeholder="Ex: 1" />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item name="competencia" label="Competência" rules={[{ required: true, message: 'Informe a competência' }, { max: 6 }]}>
              <Input maxLength={6} placeholder="AAAAMM" />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
