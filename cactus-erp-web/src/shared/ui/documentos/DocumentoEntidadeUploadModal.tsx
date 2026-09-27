import { useState } from 'react'
import { App, Form, Input, Modal, Select, Upload } from 'antd'
import { InboxOutlined } from '@ant-design/icons'
import { useCriarDocumento, useTodosTiposDocumento } from '@features/ged/queries'
import type { DocumentoRequest } from '@features/ged/types'

const { Dragger } = Upload

interface DocumentoEntidadeUploadModalProps {
  open: boolean
  entidadeTipo: string
  entidadeId: number
  onClose: () => void
}

/**
 * Versão compacta de `DocumentoUploadPage` (features/ged) pra anexar um
 * documento direto de dentro de outro cadastro (Produto, Fornecedor...) —
 * `entidadeTipo`/`entidadeId` vêm fixos por prop (não são campos do form),
 * mesmo vínculo genérico já modelado no GED desde a Fase 4 (ver CLAUDE.md
 * raiz), nunca exercitado por nenhuma tela até agora.
 */
export default function DocumentoEntidadeUploadModal({
  open,
  entidadeTipo,
  entidadeId,
  onClose,
}: DocumentoEntidadeUploadModalProps) {
  const { message } = App.useApp()
  const [form] = Form.useForm<{ nome: string; tipoDocumentoId: number; descricao?: string }>()
  const { data: tipos, isLoading: carregandoTipos } = useTodosTiposDocumento()
  const criar = useCriarDocumento()
  const [arquivo, setArquivo] = useState<File | undefined>(undefined)

  function fecharEResetar() {
    form.resetFields()
    setArquivo(undefined)
    onClose()
  }

  async function handleOk() {
    const valores = await form.validateFields()
    if (!arquivo) {
      message.error('Selecione um arquivo.')
      return
    }
    const metadados: DocumentoRequest = {
      ...valores,
      entidadeTipo,
      entidadeId,
      permiteExclusao: true,
      permiteAlteracao: true,
      assinado: false,
    }
    try {
      await criar.mutateAsync({ metadados, arquivo })
      message.success('Documento anexado com sucesso.')
      fecharEResetar()
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao anexar documento.')
    }
  }

  return (
    <Modal
      title="Adicionar documento"
      open={open}
      onCancel={fecharEResetar}
      onOk={handleOk}
      okText="Enviar"
      cancelText="Cancelar"
      confirmLoading={criar.isPending}
      destroyOnHidden
    >
      <Form form={form} name="documentoEntidadeForm" layout="vertical">
        <Form.Item name="nome" label="Nome" rules={[{ required: true, message: 'Informe o nome' }, { max: 150 }]}>
          <Input />
        </Form.Item>
        <Form.Item name="tipoDocumentoId" label="Tipo de documento" rules={[{ required: true, message: 'Selecione o tipo' }]}>
          <Select
            showSearch
            loading={carregandoTipos}
            optionFilterProp="label"
            options={tipos?.map((t) => ({ value: t.id, label: `${t.nome} (até ${t.tamanhoMaximoMb} MB)` }))}
          />
        </Form.Item>
        <Form.Item name="descricao" label="Descrição" rules={[{ max: 250 }]}>
          <Input />
        </Form.Item>
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
            <p className="ant-upload-text">Clique ou arraste o arquivo aqui</p>
          </Dragger>
        </Form.Item>
      </Form>
    </Modal>
  )
}
