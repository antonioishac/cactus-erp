import { useEffect } from 'react'
import { App, Form, Input, Modal, Select } from 'antd'
import { useAtualizarPasta, useCriarPasta, useTodasPastas } from '../queries'
import type { PastaDocumentoRequest, PastaDocumentoResponse } from '../types'

interface PastaModalProps {
  open: boolean
  valorInicial?: PastaDocumentoResponse
  onClose: () => void
}

/** Ciclo (pasta pai = descendente da própria pasta) é validado pelo backend (422) — não replicado aqui, o `catch` mostra a mensagem real. */
export default function PastaModal({ open, valorInicial, onClose }: PastaModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<PastaDocumentoRequest>()
  const { data: pastas, isLoading: carregandoPastas } = useTodasPastas()
  const criar = useCriarPasta()
  const atualizar = useAtualizarPasta()
  const salvando = criar.isPending || atualizar.isPending

  useEffect(() => {
    if (open) {
      form.setFieldsValue(valorInicial ?? {})
    }
  }, [open, valorInicial, form])

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        if (valorInicial) {
          await atualizar.mutateAsync({ id: valorInicial.id, request: valores })
          message.success('Pasta atualizada.')
        } else {
          await criar.mutateAsync(valores)
          message.success('Pasta criada.')
        }
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao salvar pasta.')
      }
    })
  }

  return (
    <Modal
      title={valorInicial ? 'Editar pasta' : 'Nova pasta'}
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      confirmLoading={salvando}
      destroyOnHidden
    >
      <Form form={form} name="pastaForm" layout="vertical">
        <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 100 }]}>
          <Input />
        </Form.Item>
        <Form.Item name="descricao" label="Descrição" rules={[{ max: 250 }]}>
          <Input />
        </Form.Item>
        <Form.Item
          name="pastaPaiId"
          label="Pasta pai"
          tooltip="Deixe em branco pra criar uma pasta raiz."
          rules={[
            {
              validator: (_, value) =>
                value != null && valorInicial != null && value === valorInicial.id
                  ? Promise.reject(new Error('Uma pasta não pode ser pai de si mesma'))
                  : Promise.resolve(),
            },
          ]}
        >
          <Select
            allowClear
            showSearch
            loading={carregandoPastas}
            optionFilterProp="label"
            options={pastas?.filter((p) => p.id !== valorInicial?.id).map((p) => ({ value: p.id, label: p.nome }))}
          />
        </Form.Item>
      </Form>
    </Modal>
  )
}
