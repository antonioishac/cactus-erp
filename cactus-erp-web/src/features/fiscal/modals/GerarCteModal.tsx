import { useNavigate } from 'react-router-dom'
import { App, Button, Col, DatePicker, Flex, Form, Input, InputNumber, Modal, Row, Select } from 'antd'
import { DeleteOutlined, PlusOutlined } from '@ant-design/icons'
import type { Dayjs } from 'dayjs'
import { useGerarCteDeVenda } from '../queries'
import { TIPO_TOMADOR_CTE_LABEL } from '../types'
import type { GerarCteDeVendaRequest } from '../types'

interface GerarCteModalProps {
  open: boolean
  vendaId: number
  onClose: () => void
}

interface FormValues extends Omit<GerarCteDeVendaRequest, 'dataPrevistaEntrega'> {
  dataPrevistaEntrega?: Dayjs
}

const VALORES_PADRAO: Partial<FormValues> = { tomador: 'DESTINATARIO' }

/**
 * A venda precisa ter frete definido (`VendaFrete`, com transportadora) antes
 * de gerar o CT-e — a carga/destinatário/placa já vêm de lá; aqui só o que é
 * específico do serviço de transporte em si (`fiscal-service` não conhece
 * preço de frete, ver Javadoc de `GerarCteDeVendaRequest`). Backend valida
 * `tomador` restrito a REMETENTE/DESTINATARIO — os outros 3 valores do enum
 * aparecem no Select por completude, mas são rejeitados
 * (`TOMADOR_NAO_SUPORTADO`) se selecionados, mesmo padrão de qualquer outra
 * regra de negócio validada no backend e surfacada por toast.
 */
export default function GerarCteModal({ open, vendaId, onClose }: GerarCteModalProps) {
  const { message } = App.useApp()
  const navigate = useNavigate()
  const [form] = Form.useForm<FormValues>()
  const gerar = useGerarCteDeVenda(vendaId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      const request: GerarCteDeVendaRequest = {
        ...valores,
        dataPrevistaEntrega: valores.dataPrevistaEntrega ? valores.dataPrevistaEntrega.format('YYYY-MM-DD') : undefined,
      }
      try {
        const cte = await gerar.mutateAsync(request)
        message.success('CT-e gerado a partir da venda.')
        form.resetFields()
        onClose()
        navigate(`/fiscal/ctes/${cte.id}`)
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao gerar CT-e.')
      }
    })
  }

  return (
    <Modal
      title="Gerar CT-e a partir da venda"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Gerar CT-e"
      cancelText="Cancelar"
      confirmLoading={gerar.isPending}
      width={640}
      destroyOnHidden
    >
      <Form form={form} name="gerarCteForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="tomador" label="Tomador do serviço" rules={[{ required: true, message: 'Selecione o tomador' }]}>
              <Select options={Object.entries(TIPO_TOMADOR_CTE_LABEL).map(([value, label]) => ({ value, label }))} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="valorTotalServico" label="Valor do serviço (R$)" rules={[{ required: true, message: 'Informe o valor' }]}>
              <InputNumber<number> style={{ width: '100%' }} min={0.000001} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={8}>
            <Form.Item name="aliquotaIcms" label="Alíquota ICMS (%)">
              <InputNumber<number> style={{ width: '100%' }} min={0} max={100} precision={2} decimalSeparator="," />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="indicadorLotacao" label="Indicador de lotação">
              <Select
                allowClear
                options={[
                  { value: '0', label: 'Carga fracionada' },
                  { value: '1', label: 'Carga lotação' },
                ]}
              />
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item name="dataPrevistaEntrega" label="Previsão de entrega">
              <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="produtoPredominante" label="Produto predominante" rules={[{ max: 60 }]}>
              <Input maxLength={60} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="ciot" label="CIOT">
              <Input />
            </Form.Item>
          </Col>
        </Row>
        <Form.Item name="observacoesGerais" label="Observações gerais">
          <Input.TextArea rows={2} />
        </Form.Item>

        <Form.List name="componentes">
          {(fields, { add, remove }) => (
            <Flex vertical gap={8}>
              {fields.map((field) => (
                <Row gutter={12} key={field.key} align="middle">
                  <Col span={14}>
                    <Form.Item
                      name={[field.name, 'nome']}
                      rules={[{ required: true, message: 'Nome do componente' }, { max: 15 }]}
                      style={{ marginBottom: 8 }}
                    >
                      <Input placeholder="Ex: FRETE PESO" maxLength={15} />
                    </Form.Item>
                  </Col>
                  <Col span={8}>
                    <Form.Item name={[field.name, 'valor']} rules={[{ required: true, message: 'Valor' }]} style={{ marginBottom: 8 }}>
                      <InputNumber<number> style={{ width: '100%' }} min={0} precision={2} decimalSeparator="," placeholder="Valor (R$)" />
                    </Form.Item>
                  </Col>
                  <Col span={2}>
                    <Button type="text" danger icon={<DeleteOutlined />} onClick={() => remove(field.name)} />
                  </Col>
                </Row>
              ))}
              <Flex>
                <Button icon={<PlusOutlined />} onClick={() => add()}>
                  Adicionar componente do valor
                </Button>
              </Flex>
            </Flex>
          )}
        </Form.List>
      </Form>
    </Modal>
  )
}
