import { App, Col, DatePicker, Form, Input, Modal, Row } from 'antd'
import dayjs, { type Dayjs } from 'dayjs'
import { useCriarCotacao } from '../queries'

interface NovaCotacaoModalProps {
  open: boolean
  requisicaoId: number
  onClose: (cotacaoId?: number) => void
}

interface FormValues {
  dataCotacao?: Dayjs
  descricao?: string
}

const VALORES_PADRAO: Partial<FormValues> = { dataCotacao: dayjs() }

export default function NovaCotacaoModal({ open, requisicaoId, onClose }: NovaCotacaoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<FormValues>()
  const criar = useCriarCotacao()

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        const criada = await criar.mutateAsync({
          requisicaoId,
          dataCotacao: valores.dataCotacao ? valores.dataCotacao.format('YYYY-MM-DD') : undefined,
          descricao: valores.descricao,
        })
        message.success('Cotação criada com sucesso.')
        form.resetFields()
        onClose(criada.id)
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao criar cotação.')
      }
    })
  }

  return (
    <Modal
      title="Nova cotação"
      open={open}
      onCancel={() => onClose()}
      onOk={handleOk}
      okText="Criar"
      cancelText="Cancelar"
      confirmLoading={criar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="novaCotacaoForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="dataCotacao" label="Data da cotação">
              <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="descricao" label="Descrição" rules={[{ max: 200 }]}>
              <Input maxLength={200} />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}
