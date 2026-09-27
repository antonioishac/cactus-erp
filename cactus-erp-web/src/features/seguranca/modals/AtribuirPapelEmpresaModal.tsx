import { useState } from 'react'
import { App, Form, Modal, Select } from 'antd'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useAtribuirPapelEmpresa, useBuscaEmpresasParaSelecao, useTodosPapeis } from '../queries'
import type { UsuarioPapelEmpresaRequest } from '../types'

interface AtribuirPapelEmpresaModalProps {
  open: boolean
  usuarioId: number
  onClose: () => void
}

/**
 * É esta associação (Papel × Empresa) que define as authorities do token
 * quando o usuário loga nessa empresa (ver Javadoc de `UsuarioController`) —
 * a ação mais crítica de todo o módulo de Segurança.
 */
export default function AtribuirPapelEmpresaModal({ open, usuarioId, onClose }: AtribuirPapelEmpresaModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<UsuarioPapelEmpresaRequest>()
  const { data: papeis, isLoading: carregandoPapeis } = useTodosPapeis()
  const [termoEmpresa, setTermoEmpresa] = useState<string | undefined>(undefined)
  const { data: empresas, isLoading: carregandoEmpresas } = useBuscaEmpresasParaSelecao(termoEmpresa)
  const atribuir = useAtribuirPapelEmpresa(usuarioId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await atribuir.mutateAsync(valores)
        message.success('Papel atribuído.')
        form.resetFields()
        setTermoEmpresa(undefined)
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao atribuir papel.')
      }
    })
  }

  return (
    <Modal
      title="Atribuir papel numa empresa"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Atribuir"
      cancelText="Cancelar"
      confirmLoading={atribuir.isPending}
      destroyOnHidden
    >
      <Form form={form} name="atribuirPapelEmpresaForm" layout="vertical">
        <Form.Item name="empresaId" label="Empresa" rules={[{ required: true, message: 'Selecione a empresa' }]}>
          <AsyncSelect
            opcoes={empresas}
            carregando={carregandoEmpresas}
            termo={termoEmpresa}
            onTermoChange={setTermoEmpresa}
            toOption={(e) => ({ value: e.id, label: e.nomeFantasia || e.razaoSocial })}
          />
        </Form.Item>
        <Form.Item name="papelId" label="Papel" rules={[{ required: true, message: 'Selecione o papel' }]}>
          <Select
            showSearch
            loading={carregandoPapeis}
            optionFilterProp="label"
            options={papeis?.filter((p) => p.situacao === 'ATIVO').map((p) => ({ value: p.id, label: p.nome }))}
          />
        </Form.Item>
      </Form>
    </Modal>
  )
}
