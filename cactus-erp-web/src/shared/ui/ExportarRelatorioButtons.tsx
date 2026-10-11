import { useState } from 'react'
import { App, Button, Space, Tooltip } from 'antd'
import { FileExcelOutlined, FilePdfOutlined } from '@ant-design/icons'
import type { FormatoRelatorio } from '@shared/api/download'
import type { AppError } from '@shared/api/problemDetail'

interface Props {
  /** Chama o endpoint `/pdf` ou `/xlsx` do relatório com os MESMOS filtros da tela — o
   * arquivo baixado é sempre o que o usuário está vendo. */
  onExportar: (formato: FormatoRelatorio) => Promise<void>
  disabled?: boolean
  /** Motivo exibido no tooltip quando `disabled` (ex: "Calcule o relatório primeiro"). */
  motivoDesabilitado?: string
}

/**
 * Botões "PDF"/"Excel" padrão de toda tela de relatório (ver ANÁLISE: MÓDULO DE
 * RELATÓRIOS no CLAUDE.md raiz — todo relatório sai com integração no frontend).
 * Cada botão tem o próprio estado de carregamento; erro vira `message.error`, com texto
 * específico pra 403 (o corpo de erro de um download em blob não é lido, ver
 * `shared/api/download.ts`).
 */
export default function ExportarRelatorioButtons({ onExportar, disabled, motivoDesabilitado }: Props) {
  const { message } = App.useApp()
  const [exportando, setExportando] = useState<FormatoRelatorio | null>(null)

  async function exportar(formato: FormatoRelatorio) {
    setExportando(formato)
    try {
      await onExportar(formato)
    } catch (erro) {
      const appError = erro as Partial<AppError>
      message.error(
        appError.kind === 'forbidden'
          ? 'Você não tem permissão para exportar este relatório.'
          : appError.kind === 'network'
            ? appError.message
            : 'Falha ao gerar o arquivo do relatório.',
      )
    } finally {
      setExportando(null)
    }
  }

  const botoes = (
    <Space>
      <Button
        icon={<FilePdfOutlined />}
        loading={exportando === 'pdf'}
        disabled={disabled || exportando !== null}
        onClick={() => exportar('pdf')}
      >
        PDF
      </Button>
      <Button
        icon={<FileExcelOutlined />}
        loading={exportando === 'xlsx'}
        disabled={disabled || exportando !== null}
        onClick={() => exportar('xlsx')}
      >
        Excel
      </Button>
    </Space>
  )

  return disabled && motivoDesabilitado ? <Tooltip title={motivoDesabilitado}>{botoes}</Tooltip> : botoes
}
