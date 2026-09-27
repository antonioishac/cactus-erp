import { useState } from 'react'
import { App, Form, Modal } from 'antd'
import AsyncSelect from '@shared/ui/AsyncSelect'
import { useBuscaClientesParaSelecao, useConverterLead } from '../queries'
import type { ConverterLeadRequest } from '../types'

interface ConverterLeadModalProps {
  open: boolean
  leadId: number
  onClose: () => void
}

/** `clienteId` referencia um `Cliente` JÁ cadastrado no cadastro-base-service — este fluxo não cria o Cliente automaticamente (ver Javadoc do backend). */
export default function ConverterLeadModal({ open, leadId, onClose }: ConverterLeadModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<ConverterLeadRequest>()
  const [termoCliente, setTermoCliente] = useState<string | undefined>(undefined)
  const { data: clientes, isLoading: carregandoClientes } = useBuscaClientesParaSelecao(termoCliente)
  const converter = useConverterLead(leadId)

  const handleOk = () => {
    form.validateFields().then(async (valores) => {
      try {
        await converter.mutateAsync(valores)
        message.success('Lead convertido em cliente.')
        form.resetFields()
        onClose()
      } catch (erro) {
        message.error(erro instanceof Error ? erro.message : 'Falha ao converter lead.')
      }
    })
  }

  return (
    <Modal
      title="Converter lead em cliente"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Converter"
      cancelText="Cancelar"
      confirmLoading={converter.isPending}
      destroyOnHidden
    >
      <Form form={form} name="converterLeadForm" layout="vertical">
        <Form.Item
          name="clienteId"
          label="Cliente já cadastrado"
          rules={[{ required: true, message: 'Selecione o cliente' }]}
          extra="O Cliente precisa já existir no Cadastro Base — este fluxo não cria um cliente novo."
        >
          <AsyncSelect
            opcoes={clientes}
            carregando={carregandoClientes}
            termo={termoCliente}
            onTermoChange={setTermoCliente}
            toOption={(c) => ({ value: c.id, label: c.nome })}
          />
        </Form.Item>
      </Form>
    </Modal>
  )
}
