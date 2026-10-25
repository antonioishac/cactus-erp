import { App, DatePicker, Form, InputNumber, Modal } from 'antd'
import dayjs, { type Dayjs } from 'dayjs'
import { useEncerrarMdfe } from '../queries'

interface EncerrarMdfeModalProps {
  open: boolean
  mdfeId: number
  dataInicioViagem?: string
  municipioPadraoNome?: string
  onClose: () => void
}

interface FormValues {
  dataEncerramento?: Dayjs
  municipioCodigoIbge?: number
}

/** Os dois campos são opcionais — vazio usa hoje e o município de descarga do último CT-e (regra do backend). */
export default function EncerrarMdfeModal({ open, mdfeId, dataInicioViagem, municipioPadraoNome, onClose }: EncerrarMdfeModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<FormValues>()
  const encerrar = useEncerrarMdfe(mdfeId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await encerrar.mutateAsync({
          dataEncerramento: valores.dataEncerramento?.format('YYYY-MM-DD'),
          municipioCodigoIbge: valores.municipioCodigoIbge ?? undefined,
        })
        message.success('MDF-e encerrado.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao encerrar o MDF-e.')
      }
    })
  }

  return (
    <Modal
      title="Encerrar MDF-e"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Encerrar"
      cancelText="Voltar"
      confirmLoading={encerrar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="encerrarMdfeForm" layout="vertical">
        <Form.Item name="dataEncerramento" label="Data do encerramento" tooltip="Vazio = hoje">
          <DatePicker
            style={{ width: '100%' }}
            format="DD/MM/YYYY"
            disabledDate={(d) => d.isAfter(dayjs(), 'day') || (dataInicioViagem != null && d.isBefore(dayjs(dataInicioViagem), 'day'))}
          />
        </Form.Item>
        <Form.Item
          name="municipioCodigoIbge"
          label="Município do encerramento (código IBGE)"
          tooltip={`Vazio = ${municipioPadraoNome ?? 'município de descarga do último CT-e'}`}
          rules={[{ type: 'number', min: 1000000, max: 9999999, message: '7 dígitos' }]}
        >
          <InputNumber<number> style={{ width: '100%' }} controls={false} />
        </Form.Item>
      </Form>
    </Modal>
  )
}
