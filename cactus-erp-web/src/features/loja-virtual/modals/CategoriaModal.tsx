import { useEffect } from 'react'
import { App, Form, Input, InputNumber, Modal, Select } from 'antd'
import { useAtualizarCategoria, useCriarCategoria, useTodasCategorias } from '../queries'
import type { CategoriaLojaRequest, CategoriaLojaResponse } from '../types'

interface Props {
  open: boolean
  lojaId: number
  valorInicial?: CategoriaLojaResponse
  onClose: () => void
}

export default function CategoriaModal({ open, lojaId, valorInicial, onClose }: Props) {
  const { message } = App.useApp()
  const [form] = Form.useForm<CategoriaLojaRequest>()
  const { data: categorias, isLoading: carregandoCategorias } = useTodasCategorias(lojaId)
  const criar = useCriarCategoria()
  const atualizar = useAtualizarCategoria()
  const salvando = criar.isPending || atualizar.isPending

  useEffect(() => {
    if (open) {
      form.setFieldsValue(valorInicial ?? { lojaId })
    }
  }, [open, valorInicial, lojaId, form])

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        const request: CategoriaLojaRequest = { ...valores, lojaId }
        if (valorInicial) {
          await atualizar.mutateAsync({ id: valorInicial.id, request })
          message.success('Categoria atualizada.')
        } else {
          await criar.mutateAsync(request)
          message.success('Categoria criada.')
        }
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao salvar categoria.')
      }
    })
  }

  return (
    <Modal
      title={valorInicial ? 'Editar categoria' : 'Nova categoria'}
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Salvar"
      cancelText="Cancelar"
      confirmLoading={salvando}
      destroyOnHidden
    >
      <Form form={form} name="categoriaLojaForm" layout="vertical">
        <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 100 }]}>
          <Input />
        </Form.Item>
        <Form.Item
          name="categoriaPaiId"
          label="Categoria pai"
          tooltip="Deixe em branco pra criar uma categoria raiz."
          rules={[
            {
              validator: (_, value) =>
                value != null && valorInicial != null && value === valorInicial.id
                  ? Promise.reject(new Error('Uma categoria não pode ser pai de si mesma'))
                  : Promise.resolve(),
            },
          ]}
        >
          <Select
            allowClear
            showSearch
            loading={carregandoCategorias}
            optionFilterProp="label"
            options={categorias?.filter((c) => c.id !== valorInicial?.id).map((c) => ({ value: c.id, label: c.nome }))}
          />
        </Form.Item>
        <Form.Item name="ordem" label="Ordem de exibição">
          <InputNumber style={{ width: '100%' }} min={0} />
        </Form.Item>
      </Form>
    </Modal>
  )
}
