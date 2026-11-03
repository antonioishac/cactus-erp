import { App, Form, Input, InputNumber, Modal } from 'antd'
import { useInutilizarNumeracaoNfe } from '../queries'

interface InutilizarNumeracaoModalProps {
  open: boolean
  empresaId: number
  onClose: () => void
}

interface Valores {
  serie?: string
  numeroInicial: number
  numeroFinal: number
  justificativa: string
}

/**
 * Pedido de inutilização à SEFAZ. As regras que dependem de dados do servidor (número ainda não reservado, número que
 * já é NF-e, faixa já inutilizada) são checadas pelo backend, que responde com o motivo.
 */
export default function InutilizarNumeracaoModal({ open, empresaId, onClose }: InutilizarNumeracaoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<Valores>()
  const inutilizar = useInutilizarNumeracaoNfe()

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        const resultado = await inutilizar.mutateAsync({
          empresaId,
          serie: valores.serie?.trim() || undefined,
          numeroInicial: valores.numeroInicial,
          numeroFinal: valores.numeroFinal,
          justificativa: valores.justificativa.trim(),
        })
        if (resultado.situacao === 'HOMOLOGADA') {
          message.success(`Faixa inutilizada. Protocolo ${resultado.protocolo}.`)
        } else if (resultado.situacao === 'DISPENSADA') {
          message.success('Nesta UF a inutilização é dispensada (Ajuste SINIEF 40/2026): o salto foi registrado e os números regularizados.')
        } else {
          message.warning(`SEFAZ rejeitou a inutilização: ${resultado.mensagemSefaz}`)
        }
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao inutilizar a numeração.')
      }
    })
  }

  return (
    <Modal
      title="Inutilizar numeração de NF-e"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Enviar à SEFAZ"
      cancelText="Voltar"
      confirmLoading={inutilizar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="inutilizarNumeracaoForm" layout="vertical">
        <Form.Item
          name="serie"
          label="Série"
          extra="Em branco = série de NF-e configurada na empresa"
          rules={[{ pattern: /^\d{1,3}$/, message: 'Série numérica de 0 a 999' }]}
        >
          <Input maxLength={3} style={{ width: 120 }} />
        </Form.Item>
        <Form.Item
          name="numeroInicial"
          label="Número inicial"
          rules={[{ required: true, message: 'Informe o número inicial' }]}
        >
          <InputNumber min={1} max={999999999} precision={0} style={{ width: 200 }} />
        </Form.Item>
        <Form.Item
          name="numeroFinal"
          label="Número final"
          dependencies={['numeroInicial']}
          rules={[
            { required: true, message: 'Informe o número final' },
            ({ getFieldValue }) => ({
              validator: (_, valor?: number) =>
                valor == null || valor >= getFieldValue('numeroInicial')
                  ? Promise.resolve()
                  : Promise.reject(new Error('Não pode ser menor que o inicial')),
            }),
          ]}
        >
          <InputNumber min={1} max={999999999} precision={0} style={{ width: 200 }} />
        </Form.Item>
        <Form.Item
          name="justificativa"
          label="Justificativa"
          rules={[
            { required: true, whitespace: true, message: 'Informe a justificativa' },
            { min: 15, max: 255, message: 'Entre 15 e 255 caracteres' },
          ]}
        >
          <Input.TextArea rows={3} maxLength={255} showCount />
        </Form.Item>
      </Form>
    </Modal>
  )
}
