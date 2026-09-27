import { useState } from 'react'
import { App, Modal, Upload } from 'antd'
import { InboxOutlined } from '@ant-design/icons'
import { useImportarRetornoBancario } from '../queries'

const { Dragger } = Upload

interface ImportarRetornoModalProps {
  open: boolean
  contaBancariaId: number
  onClose: () => void
}

/** Resolve título por nosso número (contra remessas já geradas) e dispara baixa automática
 * nos títulos liquidados — o resultado item a item (processado/observação) fica visível na
 * listagem de retornos depois de importar. */
export default function ImportarRetornoModal({ open, contaBancariaId, onClose }: ImportarRetornoModalProps) {
  const { message } = App.useApp()
  const [arquivo, setArquivo] = useState<File | undefined>(undefined)
  const importar = useImportarRetornoBancario()

  const handleOk = async () => {
    if (!arquivo) {
      message.error('Selecione um arquivo de retorno CNAB 240.')
      return
    }
    try {
      const retorno = await importar.mutateAsync({ contaBancariaId, arquivo })
      const processados = retorno.itens.filter((i) => i.processado).length
      message.success(`Retorno importado: ${retorno.quantidadeRegistros} registro(s), ${processados} baixa(s) automática(s).`)
      setArquivo(undefined)
      onClose()
    } catch (erro) {
      message.error(erro instanceof Error ? erro.message : 'Falha ao importar retorno.')
    }
  }

  return (
    <Modal
      title="Importar retorno CNAB 240"
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
        <p className="ant-upload-text">Clique ou arraste o arquivo de retorno (.ret) aqui</p>
      </Dragger>
    </Modal>
  )
}
