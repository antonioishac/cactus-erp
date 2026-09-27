import { useEffect } from 'react'
import { App, Form, Input, Modal, Select } from 'antd'
import { useEstagiosFunilOrdenado, useMoverEstagioOportunidade } from '../queries'
import type { MoverEstagioRequest } from '../types'

interface MoverEstagioModalProps {
  open: boolean
  oportunidadeId: number
  estagioAtualId?: number
  /** Pré-seleciona o estágio destino — usado pelo drag-and-drop do Kanban
   * quando o card é soltado numa coluna de encerramento PERDIDO (a única
   * situação em que o drop não move direto, porque `motivoPerda` é
   * obrigatório e só o modal coleta esse dado). */
  estagioDestinoInicial?: number
  onClose: () => void
}

/** Mover de estágio é um Select + confirmação (mesmo critério de "trocar status" já usado em `OsAberturaDetailPage`), disponível tanto pelo botão "Mover" quanto por arrastar o card no Kanban pra uma coluna de encerramento PERDIDO (ver `OportunidadesKanbanPage` — arrastar pra qualquer outra coluna move direto, sem abrir este modal). Se o estágio destino for de encerramento PERDIDO, `motivoPerda` passa a ser obrigatório (validado no backend, replicado aqui client-side). */
export default function MoverEstagioModal({ open, oportunidadeId, estagioAtualId, estagioDestinoInicial, onClose }: MoverEstagioModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<MoverEstagioRequest>()
  const { data: estagios } = useEstagiosFunilOrdenado()
  const mover = useMoverEstagioOportunidade(oportunidadeId)
  const estagioFunilId = Form.useWatch('estagioFunilId', form)
  const estagioDestino = estagios?.find((e) => e.id === estagioFunilId)
  const exigeMotivo = estagioDestino?.tipoEncerramento === 'PERDIDO'

  useEffect(() => {
    if (open) {
      form.resetFields()
      if (estagioDestinoInicial !== undefined) {
        form.setFieldsValue({ estagioFunilId: estagioDestinoInicial })
      }
    }
  }, [open, estagioDestinoInicial, form])

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await mover.mutateAsync(valores)
        message.success('Oportunidade movida.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao mover oportunidade.')
      }
    })
  }

  return (
    <Modal
      title="Mover oportunidade de estágio"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Mover"
      cancelText="Cancelar"
      confirmLoading={mover.isPending}
      destroyOnHidden
    >
      <Form form={form} name="moverEstagioForm" layout="vertical">
        <Form.Item name="estagioFunilId" label="Novo estágio" rules={[{ required: true, message: 'Selecione o estágio destino' }]}>
          <Select
            options={estagios
              ?.filter((e) => e.id !== estagioAtualId)
              .map((e) => ({ value: e.id, label: e.nome }))}
          />
        </Form.Item>
        {exigeMotivo && (
          <Form.Item name="motivoPerda" label="Motivo da perda" rules={[{ required: true, message: 'Informe o motivo da perda' }, { max: 250 }]}>
            <Input.TextArea rows={3} maxLength={250} showCount />
          </Form.Item>
        )}
      </Form>
    </Modal>
  )
}
