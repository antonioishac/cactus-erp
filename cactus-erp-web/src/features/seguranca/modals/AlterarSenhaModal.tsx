import { App, Form, Input, Modal } from 'antd'
import { useAlterarSenhaUsuario } from '../queries'

interface AlterarSenhaModalProps {
  open: boolean
  usuarioId: number
  onClose: () => void
}

/** Ação dedicada (`PATCH /usuarios/{id}/senha`) — nunca reescrita pelo PUT genérico de edição de usuário. */
export default function AlterarSenhaModal({ open, usuarioId, onClose }: AlterarSenhaModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<{ novaSenha: string; confirmacao: string }>()
  const alterar = useAlterarSenhaUsuario(usuarioId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await alterar.mutateAsync({ novaSenha: valores.novaSenha })
        message.success('Senha alterada.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao alterar senha.')
      }
    })
  }

  return (
    <Modal
      title="Alterar senha"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Alterar"
      cancelText="Cancelar"
      confirmLoading={alterar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="alterarSenhaForm" layout="vertical">
        <Form.Item
          name="novaSenha"
          label="Nova senha"
          rules={[
            { required: true, message: 'Informe a nova senha' },
            { min: 8, message: 'A senha precisa ter pelo menos 8 caracteres' },
          ]}
        >
          <Input.Password autoComplete="new-password" />
        </Form.Item>
        <Form.Item
          name="confirmacao"
          label="Confirmar nova senha"
          dependencies={['novaSenha']}
          rules={[
            { required: true, message: 'Confirme a nova senha' },
            ({ getFieldValue }) => ({
              validator: (_, value) =>
                !value || value === getFieldValue('novaSenha') ? Promise.resolve() : Promise.reject(new Error('As senhas não coincidem')),
            }),
          ]}
        >
          <Input.Password autoComplete="new-password" />
        </Form.Item>
      </Form>
    </Modal>
  )
}
