import { useState } from 'react'
import { App, Form, Input, Modal, Upload } from 'antd'
import { InboxOutlined } from '@ant-design/icons'
import { useAdicionarVersaoDocumento } from '../queries'

const { Dragger } = Upload

interface NovaVersaoModalProps {
  open: boolean
  documentoId: number
  onClose: () => void
}

export default function NovaVersaoModal({ open, documentoId, onClose }: NovaVersaoModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<{ observacao?: string }>()
  const [arquivo, setArquivo] = useState<File | undefined>(undefined)
  const adicionar = useAdicionarVersaoDocumento(documentoId)

  const handleOk = async () => {
    if (!arquivo) {
      message.error('Selecione um arquivo.')
      return
    }
    const valores = await form.validateFields()
    try {
      await adicionar.mutateAsync({ arquivo, observacao: valores.observacao })
      message.success('Nova versão registrada.')
      form.resetFields()
      setArquivo(undefined)
      onClose()
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao registrar nova versão.')
    }
  }

  return (
    <Modal
      title="Enviar nova versão"
      open={open}
      onCancel={onClose}
      onOk={handleOk}
      okText="Enviar"
      cancelText="Cancelar"
      confirmLoading={adicionar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="novaVersaoForm" layout="vertical">
        <Form.Item label="Arquivo" required>
          <Dragger
            multiple={false}
            maxCount={1}
            beforeUpload={(file) => {
              setArquivo(file)
              return false
            }}
            onRemove={() => setArquivo(undefined)}
            fileList={arquivo ? [{ uid: '1', name: arquivo.name, size: arquivo.size, status: 'done' as const }] : []}
          >
            <p className="ant-upload-drag-icon">
              <InboxOutlined />
            </p>
            <p className="ant-upload-text">Clique ou arraste o novo arquivo aqui</p>
          </Dragger>
        </Form.Item>
        <Form.Item name="observacao" label="Observação" rules={[{ max: 250 }]}>
          <Input.TextArea rows={2} maxLength={250} showCount />
        </Form.Item>
      </Form>
    </Modal>
  )
}
