import { App, Form, Input, Select, Modal } from 'antd'
import { useAuth } from '@features/auth/useAuth'
import { useCriarInteracao } from '../queries'
import { TIPO_INTERACAO_LABEL } from '../types'
import type { InteracaoRequest } from '../types'

interface AdicionarInteracaoModalProps {
  open: boolean
  leadId?: number
  oportunidadeId?: number
  onClose: () => void
}

const VALORES_PADRAO: Partial<InteracaoRequest> = { tipo: 'NOTA' }

/**
 * Append-only — sem edição/remoção depois de registrada, mesmo critério de
 * `EstoqueMovimento`/`DocumentoVersao`. `usuarioId` nunca é campo do formulário — vem do
 * JWT de quem está registrando (mesmo critério já usado em `RegistrarBaixaModal`/
 * `NovoMovimentoManualModal` no Financeiro), não um id arbitrário digitado.
 */
export default function AdicionarInteracaoModal({ open, leadId, oportunidadeId, onClose }: AdicionarInteracaoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<InteracaoRequest>()
  const { usuarioId } = useAuth()
  const criar = useCriarInteracao(leadId, oportunidadeId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      const request: InteracaoRequest = { ...valores, leadId, oportunidadeId, usuarioId: usuarioId ?? undefined }
      try {
        await criar.mutateAsync(request)
        message.success('Interação registrada.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao registrar interação.')
      }
    })
  }

  return (
    <Modal
      title="Registrar interação"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Registrar"
      cancelText="Cancelar"
      confirmLoading={criar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="adicionarInteracaoForm" layout="vertical" initialValues={VALORES_PADRAO}>
        <Form.Item name="tipo" label="Tipo" rules={[{ required: true }]}>
          <Select options={Object.entries(TIPO_INTERACAO_LABEL).map(([value, label]) => ({ value, label }))} />
        </Form.Item>
        <Form.Item name="descricao" label="Descrição" rules={[{ required: true, message: 'Descreva a interação' }, { max: 1000 }]}>
          <Input.TextArea rows={3} maxLength={1000} showCount />
        </Form.Item>
      </Form>
    </Modal>
  )
}
