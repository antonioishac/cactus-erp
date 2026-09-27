import { useState } from 'react'
import { App, Modal, Upload } from 'antd'
import { InboxOutlined } from '@ant-design/icons'
import { useAuth } from '@features/auth/useAuth'
import { useImportarExtratoOfx } from '../queries'

const { Dragger } = Upload

interface ImportarExtratoModalProps {
  open: boolean
  contaBancariaId: number
  onClose: () => void
}

/** Upload manual de OFX 1.x/SGML (mesmo padrão de `features/ged/DocumentoUploadPage` —
 * `beforeUpload` retorna `false` pra não disparar upload automático do Ant Design, arquivo
 * só é enviado no submit real). Dedup por FITID é responsabilidade do backend. */
export default function ImportarExtratoModal({ open, contaBancariaId, onClose }: ImportarExtratoModalProps) {
  const { message } = App.useApp()
  const { usuarioId } = useAuth()
  const [arquivo, setArquivo] = useState<File | undefined>(undefined)
  const importar = useImportarExtratoOfx()

  const handleOk = async () => {
    if (!arquivo) {
      message.error('Selecione um arquivo OFX.')
      return
    }
    try {
      const resultado = await importar.mutateAsync({ contaBancariaId, arquivo, usuarioId: usuarioId ?? undefined })
      message.success(`Extrato importado: ${resultado.quantidadeItens} item(ns), ${resultado.quantidadeDuplicados} duplicado(s).`)
      setArquivo(undefined)
      onClose()
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao importar extrato.')
    }
  }

  return (
    <Modal
      title="Importar extrato bancário (OFX)"
      open={open}
      onCancel={() => {
        setArquivo(undefined)
        onClose()
      }}
      onOk={handleOk}
      okText="Importar"
      cancelText="Cancelar"
      confirmLoading={importar.isPending}
      destroyOnHidden
    >
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
        <p className="ant-upload-text">Clique ou arraste o arquivo .ofx aqui</p>
      </Dragger>
    </Modal>
  )
}
